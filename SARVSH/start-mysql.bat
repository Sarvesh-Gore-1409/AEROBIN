@echo off
echo Starting AeroBins Application with MySQL Database...
echo.
echo Make sure MySQL is running and the database 'aerobindb' exists!
echo The application will be available at: http://localhost:8080
echo.
echo Press Ctrl+C to stop the server.
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause

