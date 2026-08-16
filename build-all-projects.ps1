[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Cyan
    Write-Host " >> $Message" -ForegroundColor Yellow
    Write-Host "============================================================" -ForegroundColor Cyan
}

$arm3Root = "D:\arm3"
$clientJarRoot = "D:\client_arm3_jar"
$clientUnityRoot = "D:\arm3_client"
$flutterRoot = "D:\MobiArmy3Flutter"
$buildsDir = Join-Path $clientUnityRoot "Builds"

# 1. Build Server JAR
Write-Step "1. Building Server JAR (D:\arm3)..."
Push-Location $arm3Root
try {
    & powershell -ExecutionPolicy Bypass -File "scripts\build-jar.ps1"
} finally {
    Pop-Location
}

# 2. Build J2ME Clients (.jar / .jad)
Write-Step "2. Building J2ME Clients (D:\client_arm3_jar)..."
Push-Location $clientJarRoot
try {
    & powershell -ExecutionPolicy Bypass -File "build-all-current.ps1"
    $jarBuildDir = Join-Path $buildsDir "JAR"
    New-Item -ItemType Directory -Force -Path $jarBuildDir | Out-Null
    Copy-Item -LiteralPath "build\release-clients\vxldeptrai-mobile.jar" -Destination (Join-Path $jarBuildDir "vxldeptrai-mobile.jar") -Force
    Copy-Item -LiteralPath "build\release-clients\vxldeptrai-mobile.jad" -Destination (Join-Path $jarBuildDir "vxldeptrai-mobile.jad") -Force
    Copy-Item -LiteralPath "build\release-clients\vxldeptrai-base.jar" -Destination (Join-Path $jarBuildDir "vxldeptrai-base.jar") -Force
    Copy-Item -LiteralPath "build\release-clients\vxldeptrai-base.jad" -Destination (Join-Path $jarBuildDir "vxldeptrai-base.jad") -Force
    Write-Host "Copied J2ME clients to $jarBuildDir" -ForegroundColor Green
} finally {
    Pop-Location
}

# 3. Build Unity PC (.exe)
Write-Step "3. Building Unity PC (.exe) (D:\arm3_client)..."
Push-Location $clientUnityRoot
try {
    & cmd /c "build-pc.bat"
} finally {
    Pop-Location
}

# 4. Build Unity Native Android APK (D:\arm3_client)
Write-Step "4. Building Unity Native Android APK (D:\arm3_client)..."
Push-Location $clientUnityRoot
try {
    & cmd /c "build-apk.bat"
    $androidBuildDir = Join-Path $buildsDir "Android"
    New-Item -ItemType Directory -Force -Path $androidBuildDir | Out-Null
    $builtUnityApk = Join-Path $androidBuildDir "LoCheo3.apk"
    if (Test-Path $builtUnityApk) {
        Write-Host "Unity Native Android APK created: $builtUnityApk" -ForegroundColor Green
    } else {
        Write-Warning "Unity Native APK not found at $builtUnityApk"
    }
} finally {
    Pop-Location
}

# 5. Build Flutter Android APK (D:\MobiArmy3Flutter)
Write-Step "5. Building Flutter Android APK (D:\MobiArmy3Flutter)..."
Push-Location $flutterRoot
try {
    & flutter build apk --release --target-platform android-arm,android-arm64
    $flutterBuildDir = Join-Path $buildsDir "APKFLUTTER"
    New-Item -ItemType Directory -Force -Path $flutterBuildDir | Out-Null
    $builtApk = "build\app\outputs\flutter-apk\app-release.apk"
    if (Test-Path $builtApk) {
        Copy-Item -LiteralPath $builtApk -Destination (Join-Path $flutterBuildDir "MobiArmy3-Flutter.apk") -Force
        Copy-Item -LiteralPath $builtApk -Destination (Join-Path $flutterRoot "dist\Mobiarmy3J2me.apk") -Force
        Write-Host "Copied Flutter APK to $flutterBuildDir\MobiArmy3-Flutter.apk" -ForegroundColor Green
    } else {
        Write-Warning "Flutter APK build file not found at $builtApk"
    }
} finally {
    Pop-Location
}

Write-Step "ALL BUILDS COMPLETED SUCCESSFULLY!"
Write-Host "Build artifacts located in: $buildsDir" -ForegroundColor Green
Get-ChildItem -Path $buildsDir -Recurse -File | Select-Object FullName, Length, LastWriteTime
