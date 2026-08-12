param([switch]$Clean)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$outputRoot = Join-Path $projectRoot 'output\build-jar'
$logDirectory = Join-Path $outputRoot 'logs'
$releaseDirectory = Join-Path $outputRoot (Join-Path 'releases' $timestamp)
$bundleDirectory = Join-Path $releaseDirectory 'server'
$logPath = Join-Path $logDirectory "build-jar-$timestamp.log"
$latestLogPath = Join-Path $outputRoot 'latest.log'
$latestInfoPath = Join-Path $outputRoot 'latest.txt'
$gradleLibDirectory = Join-Path $projectRoot 'build\libs'
$releaseJarPath = Join-Path $releaseDirectory 'vxldeptrai.jar'
$bundleJarPath = Join-Path $bundleDirectory 'vxldeptrai.jar'
$bundleZipPath = Join-Path $releaseDirectory "vxldeptrai-server-$timestamp.zip"
$detailsPath = Join-Path $releaseDirectory 'build-details.txt'
$hashPath = Join-Path $releaseDirectory 'sha256.txt'
$gradleProblemsReportPath = Join-Path $projectRoot 'build\reports\problems\problems-report.html'

New-Item -ItemType Directory -Path $logDirectory -Force | Out-Null
$script:buildLogLines = [System.Collections.Generic.List[string]]::new()

function Write-BuildLog {
    param([string]$Message, [string]$Level = 'INFO')
    $line = '[{0}] [{1}] {2}' -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss.fff'), $Level, $Message
    $script:buildLogLines.Add($line)
    Add-Content -LiteralPath $logPath -Value $line -Encoding UTF8
    Write-Host $line
}

function Invoke-LoggedCommand {
    param([string]$FilePath, [string[]]$Arguments)
    Write-BuildLog "Command: $FilePath $($Arguments -join ' ')"
    $previousErrorAction = $ErrorActionPreference
    try {
        $ErrorActionPreference = 'Continue'
        & $FilePath @Arguments 2>&1 | ForEach-Object {
            $text = $_.ToString()
            $script:buildLogLines.Add($text)
            Add-Content -LiteralPath $logPath -Value $text -Encoding UTF8
            Write-Host $text
        }
        $commandExitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previousErrorAction
    }
    return $commandExitCode
}

function Get-BuildLockProcesses {
    $buildPath = (Join-Path $projectRoot 'build').ToLowerInvariant()
    return @(Get-CimInstance Win32_Process -ErrorAction SilentlyContinue | Where-Object {
        $_.CommandLine -and
        $_.CommandLine.ToLowerInvariant().Contains($buildPath) -and
        $_.Name -match '^(mysqld|mariadbd|java|javaw)\.exe$'
    })
}

function Read-JarMetadata {
    param([string]$JarPath)
    Add-Type -AssemblyName System.IO.Compression
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $archive = [System.IO.Compression.ZipFile]::OpenRead($JarPath)
    try {
        $entries = @($archive.Entries | ForEach-Object { $_.FullName })
        $manifestEntry = $archive.GetEntry('META-INF/MANIFEST.MF')
        if ($null -eq $manifestEntry) {
            throw 'JAR does not contain META-INF/MANIFEST.MF.'
        }
        $reader = [System.IO.StreamReader]::new($manifestEntry.Open())
        try { $manifest = $reader.ReadToEnd() } finally { $reader.Dispose() }
        return [pscustomobject]@{
            EntryCount = $entries.Count
            Manifest = $manifest
            HasMainClass = $entries -contains 'com/vxl/VXLMayChu.class'
            HasNetty = @($entries | Where-Object { $_ -like 'io/netty/*' }).Count -gt 0
            HasMysql = @($entries | Where-Object { $_ -like 'com/mysql/*' }).Count -gt 0
            HasHikari = @($entries | Where-Object { $_ -like 'com/zaxxer/hikari/*' }).Count -gt 0
            HasFastjson = @($entries | Where-Object { $_ -like 'com/alibaba/fastjson2/*' }).Count -gt 0
            HasSpringCrypto = $entries -contains 'org/springframework/security/crypto/bcrypt/BCrypt.class'
            Signatures = @($entries | Where-Object { $_ -match '^META-INF/.*\.(SF|RSA|DSA)$' })
            Duplicates = @($entries | Group-Object | Where-Object { $_.Count -gt 1 } | ForEach-Object { '{0} ({1})' -f $_.Name, $_.Count })
        }
    }
    finally {
        $archive.Dispose()
    }
}

$pushed = $false
$exitCode = 1
try {
    Write-BuildLog "Project root: $projectRoot"
    Write-BuildLog "Full log: $logPath"
    Write-BuildLog "Release directory: $releaseDirectory"
    Write-BuildLog "Latest summary: $latestInfoPath"
    Write-BuildLog "Latest log: $latestLogPath"
    Write-BuildLog "Mode: $(if ($Clean) { 'clean jar' } else { 'jar --rerun-tasks (skip full clean)' })"

    $javaCommand = Get-Command java -ErrorAction Stop
    Write-BuildLog "Java executable: $($javaCommand.Source)"
    if ((Invoke-LoggedCommand $javaCommand.Source @('-version')) -ne 0) {
        throw 'java -version failed.'
    }

    $locks = @(Get-BuildLockProcesses)
    if ($locks.Count -gt 0) {
        Write-BuildLog "Detected $($locks.Count) process(es) using build/." 'WARN'
        foreach ($process in $locks) {
            Write-BuildLog "Lock PID=$($process.ProcessId), name=$($process.Name), command=$($process.CommandLine)" 'WARN'
        }
        if ($Clean) {
            throw 'Cannot clean build/ while the listed process(es) are running. Stop them or build without --clean.'
        }
        Write-BuildLog 'Skipping full clean; only the previous JAR will be removed.' 'WARN'
    }
    else {
        Write-BuildLog 'No process using build/ was detected.'
    }

    $oldJars = @(Get-ChildItem -LiteralPath $gradleLibDirectory -Filter '*.jar' -File -ErrorAction SilentlyContinue)
    foreach ($oldJar in $oldJars) {
        Remove-Item -LiteralPath $oldJar.FullName -Force
        Write-BuildLog "Removed previous Gradle JAR: $($oldJar.FullName)"
    }

    Push-Location $projectRoot
    $pushed = $true
    $buildStartedUtc = [DateTime]::UtcNow
    $gradleArguments = if ($Clean) {
        @('clean', 'jar', '--console=plain', '--stacktrace')
    }
    else {
        @('jar', '--rerun-tasks', '--console=plain', '--stacktrace')
    }
    $gradleExit = Invoke-LoggedCommand (Join-Path $projectRoot 'gradlew.bat') $gradleArguments
    if ($gradleExit -ne 0) {
        throw "Gradle build failed with exit code $gradleExit."
    }
    $allBuiltJars = @(Get-ChildItem -LiteralPath $gradleLibDirectory -Filter '*.jar' -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTimeUtc -Descending)
    foreach ($jarCandidate in $allBuiltJars) {
        Write-BuildLog "Gradle JAR candidate: $($jarCandidate.FullName) ($($jarCandidate.Length) bytes, modified=$($jarCandidate.LastWriteTime.ToString('yyyy-MM-dd HH:mm:ss.fff')))"
    }
    $builtJar = @($allBuiltJars | Where-Object { $_.LastWriteTimeUtc -ge $buildStartedUtc.AddSeconds(-2) } | Select-Object -First 1)
    if ($builtJar.Count -eq 0) {
        $listedJars = if ($allBuiltJars.Count -eq 0) { 'none' } else { ($allBuiltJars.FullName -join '; ') }
        throw "Gradle completed but no newly built JAR was found in $gradleLibDirectory. JAR files present: $listedJars"
    }
    $gradleJarPath = $builtJar[0].FullName
    Write-BuildLog "Selected Gradle artifact: $gradleJarPath"

    $metadata = Read-JarMetadata $gradleJarPath
    if ($metadata.Manifest -notmatch '(?m)^Main-Class:\s*com\.vxl\.VXLMayChu\s*$') {
        throw 'Manifest Main-Class is missing or incorrect.'
    }
    if (-not $metadata.HasMainClass) {
        throw 'Main class com/vxl/VXLMayChu.class is missing.'
    }
    if (-not ($metadata.HasNetty -and $metadata.HasMysql -and $metadata.HasHikari -and $metadata.HasFastjson -and $metadata.HasSpringCrypto)) {
        throw 'One or more runtime dependencies are missing from the fat JAR.'
    }
    if ($metadata.Signatures.Count -gt 0) {
        throw "Dependency signatures remain in JAR: $($metadata.Signatures -join ', ')"
    }

    New-Item -ItemType Directory -Path $bundleDirectory -Force | Out-Null
    Copy-Item -LiteralPath $gradleJarPath -Destination $releaseJarPath -Force
    Copy-Item -LiteralPath $gradleJarPath -Destination $bundleJarPath -Force
    Copy-Item -LiteralPath (Join-Path $projectRoot 'config.conf') -Destination $bundleDirectory -Force
    Copy-Item -LiteralPath (Join-Path $projectRoot 'res') -Destination $bundleDirectory -Recurse -Force
    Copy-Item -LiteralPath (Join-Path $projectRoot 'cache') -Destination $bundleDirectory -Recurse -Force

    $runScript = @'
@echo off
setlocal
cd /d "%~dp0"
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -Xms512M -Xmx1024M -jar "vxldeptrai.jar"
set "EXIT_CODE=%ERRORLEVEL%"
echo.
echo Server exited with code %EXIT_CODE%.
pause
exit /b %EXIT_CODE%
'@
    Set-Content -LiteralPath (Join-Path $bundleDirectory 'run-server.bat') -Value $runScript -Encoding ASCII
    $runtimeLogDirectory = Join-Path $bundleDirectory 'logs'
    New-Item -ItemType Directory -Path $runtimeLogDirectory -Force | Out-Null
    Set-Content -LiteralPath (Join-Path $runtimeLogDirectory 'README.txt') -Value @(
        'Runtime server logs are stored in this directory.'
        'Build and packaging logs are stored under output\build-jar\logs in the project directory.'
    ) -Encoding ASCII
    Compress-Archive -Path (Join-Path $bundleDirectory '*') -DestinationPath $bundleZipPath -CompressionLevel Optimal -Force

    $jarFile = Get-Item -LiteralPath $releaseJarPath
    $zipFile = Get-Item -LiteralPath $bundleZipPath
    $jarHash = Get-FileHash -Algorithm SHA256 -LiteralPath $releaseJarPath
    $zipHash = Get-FileHash -Algorithm SHA256 -LiteralPath $bundleZipPath
    Set-Content -LiteralPath $hashPath -Value @(
        "$($jarHash.Hash)  vxldeptrai.jar"
        "$($zipHash.Hash)  $($zipFile.Name)"
    ) -Encoding ASCII

    $sources = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Filter '*.java' -File -Recurse)
    $resFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'res') -File -Recurse -ErrorAction SilentlyContinue)
    $cacheFiles = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'cache') -File -Recurse -ErrorAction SilentlyContinue)
    $warnings = @($script:buildLogLines | Where-Object { $_ -match '(?i)warning' }).Count
    $duplicates = if ($metadata.Duplicates.Count -eq 0) { 'none' } else { $metadata.Duplicates -join '; ' }
    $details = @(
        'status=SUCCESS'
        "built_at=$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')"
        "project_root=$projectRoot"
        "build_log=$logPath"
        "gradle_artifact=$gradleJarPath"
        "release_artifact=$releaseJarPath"
        "runtime_bundle=$bundleDirectory"
        "runtime_zip=$bundleZipPath"
        "hash_file=$hashPath"
        "build_details=$detailsPath"
        "latest_summary=$latestInfoPath"
        "latest_log=$latestLogPath"
        "gradle_problems_report=$gradleProblemsReportPath (exists=$(Test-Path -LiteralPath $gradleProblemsReportPath))"
        "jar_bytes=$($jarFile.Length)"
        "jar_sha256=$($jarHash.Hash)"
        "zip_bytes=$($zipFile.Length)"
        "zip_sha256=$($zipHash.Hash)"
        "jar_entries=$($metadata.EntryCount)"
        "java_source_files=$($sources.Count)"
        "build_warnings=$warnings"
        "duplicate_jar_entries=$duplicates"
        'main_class=com.vxl.VXLMayChu'
        "runtime_config=$(Join-Path $bundleDirectory 'config.conf')"
        "runtime_resources=$(Join-Path $bundleDirectory 'res') (files=$($resFiles.Count), bytes=$(($resFiles | Measure-Object Length -Sum).Sum))"
        "runtime_cache=$(Join-Path $bundleDirectory 'cache') (files=$($cacheFiles.Count), bytes=$(($cacheFiles | Measure-Object Length -Sum).Sum))"
        "runtime_logs=$runtimeLogDirectory"
        "run_script=$(Join-Path $bundleDirectory 'run-server.bat')"
        'security_note=The runtime bundle contains the local config.conf. Protect the ZIP because it may contain a database password.'
    )
    Set-Content -LiteralPath $detailsPath -Value $details -Encoding UTF8
    Set-Content -LiteralPath $latestInfoPath -Value $details -Encoding UTF8

    Write-BuildLog "JAR entries: $($metadata.EntryCount)"
    Write-BuildLog "JAR: $releaseJarPath ($($jarFile.Length) bytes)"
    Write-BuildLog "Bundle directory: $bundleDirectory"
    Write-BuildLog "Bundle ZIP: $bundleZipPath ($($zipFile.Length) bytes)"
    Write-BuildLog "SHA-256 file: $hashPath"
    Write-BuildLog "Build details: $detailsPath"
    Write-BuildLog 'The bundle includes config.conf, res/, cache/, and run-server.bat.'
    Write-BuildLog 'The bundle contains local config.conf; protect it because it may contain a database password.' 'WARN'
    Write-BuildLog 'JAR build, verification, and packaging completed.' 'SUCCESS'
    $exitCode = 0
}
catch {
    Write-BuildLog $_.Exception.Message 'ERROR'
    if ($_.ScriptStackTrace) {
        Write-BuildLog "PowerShell stack: $($_.ScriptStackTrace)" 'ERROR'
    }
    Write-BuildLog "Build failed. Full log: $logPath" 'ERROR'
    Set-Content -LiteralPath $latestInfoPath -Value @(
        'status=FAILED'
        "failed_at=$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')"
        "project_root=$projectRoot"
        "build_log=$logPath"
        "latest_log=$latestLogPath"
        "release_directory=$releaseDirectory"
        "gradle_lib_directory=$gradleLibDirectory"
        "error=$($_.Exception.Message)"
    ) -Encoding UTF8
    $exitCode = 1
}
finally {
    if ($pushed) { Pop-Location }
    Set-Content -LiteralPath $logPath -Value $script:buildLogLines -Encoding UTF8
    Copy-Item -LiteralPath $logPath -Destination $latestLogPath -Force
    Write-Host ''
    Write-Host "Latest summary: $latestInfoPath"
    Write-Host "Full build log: $logPath"
}

exit $exitCode
