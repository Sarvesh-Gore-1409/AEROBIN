@echo off
title AeroBins Application Server
color 0A
echo ========================================
echo   AeroBins Application Startup
echo   Development Mode (H2 Database)
echo ========================================
echo.
echo Starting application on http://localhost:8080
echo Using H2 In-Memory Database
echo.
echo Press Ctrl+C to stop the server
echo ========================================
echo.

cd /d "%~dp0"

REM Check if Java is already running and stop it
tasklist /FI "IMAGENAME eq java.exe" 2>NUL | find /I /N "java.exe">NUL
if "%ERRORLEVEL%"=="0" (
    echo Stopping existing Java processes...
    taskkill /F /IM java.exe >NUL 2>&1
    timeout /t 2 /nobreak >NUL
    echo.
)

REM Start the application in background
echo Starting Spring Boot application...
start "AeroBins Server" /MIN cmd /c "mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev"

REM Wait for the application to start
echo Waiting for application to start...
set /a counter=0
:wait_loop
timeout /t 3 /nobreak >NUL
set /a counter+=1

REM Check if port 8080 is listening
netstat -an | find "8080" | find "LISTENING" >NUL 2>&1
if "%ERRORLEVEL%"=="0" (
    echo.
    echo ========================================
    echo   Application is ready!
    echo ========================================
    echo.
    echo Opening browser at http://localhost:8080
    echo.
    start http://localhost:8080
    echo.
    echo Application is running in the background.
    echo To stop it, close the "AeroBins Server" window or run: taskkill /F /IM java.exe
    echo.
    pause
    exit /b 0
)

REM Timeout after 2 minutes (40 attempts * 3 seconds)
if %counter% GTR 40 (
    echo.
    echo ERROR: Application failed to start within 2 minutes.
    echo Please check the console window for errors.
    echo.
    pause
    exit /b 1
)

echo Still waiting... (%counter% attempts)
goto wait_loop
