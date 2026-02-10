@echo off
REM Simple launcher - Double-click this file to start the application
cd /d "%~dp0"
powershell -ExecutionPolicy Bypass -File ".\start-and-open.ps1"
pause
