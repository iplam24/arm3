@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "BUILD_SCRIPT=%~dp0scripts\build-jar.ps1"
set "LATEST_SUMMARY=%~dp0output\build-jar\latest.txt"
set "LATEST_LOG=%~dp0output\build-jar\latest.log"
set "NO_PAUSE="
set "CLEAN_OPTION="
for %%A in (%*) do (
    if /I "%%~A"=="--no-pause" set "NO_PAUSE=1"
    if /I "%%~A"=="--clean" set "CLEAN_OPTION=-Clean"
)

echo [build-jar] Project: %CD%
echo [build-jar] Script:  %BUILD_SCRIPT%

if not exist "%BUILD_SCRIPT%" (
    echo [build-jar] ERROR: Build script not found: %BUILD_SCRIPT%
    set "EXIT_CODE=2"
    goto :finish
)

where powershell.exe >nul 2>&1
if errorlevel 1 (
    echo [build-jar] ERROR: powershell.exe was not found in PATH.
    set "EXIT_CODE=3"
    goto :finish
)

powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%BUILD_SCRIPT%" %CLEAN_OPTION%
set "EXIT_CODE=%ERRORLEVEL%"

:finish
echo.
if "%EXIT_CODE%"=="0" (
    echo [build-jar] SUCCESS
) else (
    echo [build-jar] FAILED with exit code %EXIT_CODE%
)
echo [build-jar] Latest summary: %LATEST_SUMMARY%
echo [build-jar] Latest log:     %LATEST_LOG%

if not defined NO_PAUSE pause
exit /b %EXIT_CODE%
