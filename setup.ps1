#####################################################################
# Setup Script for Student Portal Middleware
# Run this script in a fresh PowerShell terminal to set up and run
# the project in development mode from scratch.
#####################################################################

# Exit on any error
$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Student Portal Middleware - Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# --- Step 1: Set Environment Variables ---
Write-Host "[1/5] Setting environment variables..." -ForegroundColor Yellow
$env:AUCA_SERVICE_USERNAME = "25306"
$env:AUCA_SERVICE_PASSWORD = "25306"
Write-Host "  AUCA_SERVICE_USERNAME=$($env:AUCA_SERVICE_USERNAME)" -ForegroundColor Green
Write-Host "  AUCA_SERVICE_PASSWORD=******" -ForegroundColor Green
Write-Host ""

# --- Step 2: Set Java PATH ---
Write-Host "[2/5] Setting up Java..." -ForegroundColor Yellow
$javaPath = "C:\Program Files\Java\jdk-19\bin"
if (Test-Path $javaPath) {
    $env:PATH = "$javaPath;$($env:PATH)"
    Write-Host "  Java added to PATH: $javaPath" -ForegroundColor Green
} else {
    Write-Host "  WARNING: Java not found at $javaPath" -ForegroundColor Red
    Write-Host "  Please install JDK 17+ and ensure 'java' is in PATH" -ForegroundColor Red
}
& java -version 2>&1 | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
Write-Host ""

# --- Step 3: Set Maven PATH ---
Write-Host "[3/5] Checking Maven..." -ForegroundColor Yellow
$mvn = Get-Command mvn -ErrorAction SilentlyContinue
if ($mvn) {
    Write-Host "  Maven found: $($mvn.Source)" -ForegroundColor Green
} else {
    Write-Host "  ERROR: Maven not found in PATH" -ForegroundColor Red
    Write-Host "  Please install Maven 3.8+ from:" -ForegroundColor Yellow
    Write-Host "  https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host "  Then add Maven 'bin' directory to PATH" -ForegroundColor Yellow
    exit 1
}
& mvn --version | Select-Object -First 1 | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
Write-Host ""

# --- Step 4: Navigate to project ---
Write-Host "[4/5] Navigating to project..." -ForegroundColor Yellow
$projectDir = "C:\Users\rukundo divin\Documents\student-portal-middleware-v2\student-portal-middleware"
Set-Location $projectDir
Write-Host "  Current directory: $(pwd)" -ForegroundColor Green
Write-Host ""

# --- Step 5: Build ---
Write-Host "[5/5] Building project..." -ForegroundColor Yellow
Write-Host "  Running: mvn clean package -DskipTests" -ForegroundColor Cyan
& mvn clean package -DskipTests -Dmaven.test.skip=true
if ($LASTEXITCODE -ne 0) {
    Write-Host "  ERROR: Build failed!" -ForegroundColor Red
    exit 1
}
Write-Host "  Build successful!" -ForegroundColor Green
Write-Host ""

# --- Run ---
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Build Complete! Starting application..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "The application will start at: http://localhost:8081" -ForegroundColor Green
Write-Host "Swagger UI: http://localhost:8081/swagger-ui.html" -ForegroundColor Green
Write-Host ""
Write-Host "To test endpoints in Swagger:" -ForegroundColor Yellow
Write-Host "  1. Sign into AUCA student portal in your browser" -ForegroundColor Gray
Write-Host "  2. Open DevTools → Application → Cookies" -ForegroundColor Gray
Write-Host "  3. Copy the 'access_token' cookie value" -ForegroundColor Gray
Write-Host "  4. In Swagger, enter: access_token=<paste-value>" -ForegroundColor Gray
Write-Host ""

# Run in development mode
& mvn spring-boot:run -Dspring-boot.run.profiles=dev