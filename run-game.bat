@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "PORT=19150"
set "PIDS= "

echo [1/2] Checking port %PORT%...
for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:":%PORT% .*LISTENING"') do (
    echo !PIDS! | findstr /C:" %%P " >nul || set "PIDS=!PIDS!%%P "
)

if "%PIDS%"==" " (
    echo Port %PORT% is free.
) else (
    for %%P in (%PIDS%) do (
        echo Stopping PID %%P on port %PORT%...
        taskkill /PID %%P /F >nul 2>&1
        if errorlevel 1 (
            echo ERROR: Could not stop PID %%P.
            echo Try running this file as Administrator.
            pause
            exit /b 1
        )
    )

    timeout /t 1 /nobreak >nul
    set "PORT_BUSY="
    for /f "tokens=5" %%P in ('netstat -ano ^| findstr /R /C:":%PORT% .*LISTENING"') do set "PORT_BUSY=1"
    if defined PORT_BUSY (
        echo ERROR: Port %PORT% is still in use.
        pause
        exit /b 1
    )
)

echo [2/2] Starting project...
echo.
call "%~dp0gradlew.bat" run
set "EXIT_CODE=%ERRORLEVEL%"

if not "%EXIT_CODE%"=="0" (
    echo.
    echo Project stopped with exit code %EXIT_CODE%.
    pause
)

exit /b %EXIT_CODE%