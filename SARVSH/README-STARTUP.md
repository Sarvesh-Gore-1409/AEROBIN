# AeroBins Application - Startup Guide

## Quick Start

### Option 1: Double-click the launcher (Easiest - RECOMMENDED)
1. Double-click `RUN-APPLICATION.bat` in the project folder
2. The script will automatically:
   - Start the application with H2 database (no MySQL required)
   - Wait for the application to be ready
   - Open your browser automatically at http://localhost:8080

### Option 2: Use the PowerShell script
1. Right-click `start-and-open.ps1` and select "Run with PowerShell"
2. Or run: `powershell -ExecutionPolicy Bypass -File .\start-and-open.ps1`

### Option 3: Use the batch file (Manual)
1. Double-click `start-dev.bat` in the project folder
2. Wait 30-60 seconds for the application to start
3. Manually open your browser to: http://localhost:8080

### Option 4: Command Line
```bash
cd d:\Aerobin\SARVSH\SARVSH
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

## Application URLs

Once running, access:
- **Main Page**: http://localhost:8080
- **Login Page**: http://localhost:8080/login.html
- **Dashboard**: http://localhost:8080/dashboard.html
- **View Login Data**: http://localhost:8080/api/login-activity
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:aerobindb`
  - Username: `Sarvesh`
  - Password: `Sarvesh@1409`

## Features

✅ **Login System**: All login attempts are saved to the database
✅ **H2 Database**: In-memory database (no MySQL required for development)
✅ **Auto-restart**: Script automatically stops existing instances before starting

## Troubleshooting

### Application won't start?
1. Check if port 8080 is already in use
2. Make sure Java 21 is installed
3. Check the console for error messages

### Connection Refused Error?
- **FIXED**: This has been permanently resolved! The application now uses H2 database by default in development mode, so MySQL is not required.
- If you still see this error:
  1. Make sure you're using `RUN-APPLICATION.bat` or `start-and-open.ps1` (they wait for the app to be ready)
  2. Check if port 8080 is already in use: `netstat -an | find "8080"`
  3. Make sure Java 21 is installed and in your PATH

### Login data not saving?
- Check console logs for "DEBUG: Saved login activity" messages
- Verify the application is using the `dev` profile
- Check http://localhost:8080/api/login-activity to see saved data

## Stopping the Application

- Press `Ctrl+C` in the terminal where it's running
- Or close the terminal window
- Or run: `taskkill /F /IM java.exe`

## Notes

- First startup may take 1-2 minutes (Maven downloads dependencies)
- H2 database is in-memory - data is lost when application stops
- For production, use MySQL with `start-mysql.bat`
