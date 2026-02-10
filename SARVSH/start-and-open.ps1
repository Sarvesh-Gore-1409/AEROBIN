# AeroBins Application Startup Script
Write-Host "========================================" -ForegroundColor Green
Write-Host "  AeroBins Application Startup" -ForegroundColor Green
Write-Host "  Production Mode (MySQL Database)" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Starting application on http://localhost:8080" -ForegroundColor Cyan
Write-Host "Using MySQL Database (Persistent)" -ForegroundColor Cyan
Write-Host ""

# Change to script directory
Set-Location $PSScriptRoot

# Check if Java is already running and stop it
$javaProcesses = Get-Process -Name java -ErrorAction SilentlyContinue
if ($javaProcesses) {
    Write-Host "Stopping existing Java processes..." -ForegroundColor Yellow
    Stop-Process -Name java -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
    Write-Host ""
}

# Start the application in a new window
Write-Host "Starting Spring Boot application..." -ForegroundColor Cyan
Start-Process -FilePath ".\mvnw.cmd" -ArgumentList "spring-boot:run" -WindowStyle Minimized

# Wait for the application to start (check if port 8080 is listening)
Write-Host "Waiting for application to start..." -ForegroundColor Cyan
$maxAttempts = 60
$attempt = 0
$started = $false

while ($attempt -lt $maxAttempts -and -not $started) {
    Start-Sleep -Seconds 3
    $attempt++
    
    # Check if port 8080 is listening
    $connection = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue
    if ($connection) {
        $started = $true
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "  Application is ready!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Opening browser at http://localhost:8080" -ForegroundColor Cyan
        Write-Host ""
        
        # Open browser
        Start-Process "http://localhost:8080"
        
        Write-Host "Application is running." -ForegroundColor Green
        Write-Host "To stop it, close the minimized window or run: Stop-Process -Name java -Force" -ForegroundColor Yellow
        Write-Host ""
    }
    else {
        Write-Host "Still waiting... ($attempt/$maxAttempts attempts)" -ForegroundColor Gray
    }
}

if (-not $started) {
    Write-Host ""
    Write-Host "ERROR: Application failed to start within 3 minutes." -ForegroundColor Red
    Write-Host "Please check the console window for errors." -ForegroundColor Red
    Write-Host ""
    exit 1
}
