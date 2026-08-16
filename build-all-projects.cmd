@echo off
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File "D:\arm3\build-all-projects.ps1"
pause
