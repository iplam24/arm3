@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "NO_PAUSE="
set "CLEAN_OPTION="
for %%A in (%*) do (
    if /I "%%~A"=="--no-pause" set "NO_PAUSE=1"
    if /I "%%~A"=="--clean" set "CLEAN_OPTION=-Clean"
)

powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\build-jar.ps1" %CLEAN_OPTION%
set "EXIT_CODE=%ERRORLEVEL%"

if not defined NO_PAUSE pause
exit /b %EXIT_CODE%
