@echo off
title AeroBins Application Server
color 0A
echo ========================================
echo   AeroBins Application Startup
echo ========================================
echo.
echo Starting application on http://localhost:8080
echo Using H2 Database (Development Mode)
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

cd /d "%~dp0"

REM Check if Java is running
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo WARNING: Java process detected. Stopping existing processes...
    taskkill /F /IM java.exe >NUL 2>&1
    timeout /t 2 /nobreak >NUL
)

REM Start the application
call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

pause
