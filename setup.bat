@echo off
REM =====================================================================
REM Windows Batch Script - Quick Start for Student Portal Middleware
REM Run this script to set up environment and start the application
REM =====================================================================

setlocal enabledelayedexpansion

echo ========================================================
echo Student Portal Middleware - Windows Setup
echo ========================================================
echo.

REM --- Step 1: Set Environment Variables ---
echo [1/5] Setting environment variables...
set AUCA_SERVICE_USERNAME=25306
set AUCA_SERVICE_PASSWORD=25306
echo   AUCA_SERVICE_USERNAME=!AUCA_SERVICE_USERNAME!
echo   AUCA_SERVICE_PASSWORD=******
echo.

REM --- Step 2: Check Java ---
echo [2/5] Checking Java installation...
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo   ERROR: Java not found in PATH.
    echo   Please add JDK 17+ to your PATH.
    echo   Expected: C:\Program Files\Java\jdk-19\bin
    goto :error
)
for /f "tokens=3" %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    set JAVA_VERSION=%%a
)
echo   Java version: !JAVA_VERSION!" 
echo.

REM --- Step 3: Check Maven ---
echo [3/5] Checking Maven installation...
where mvn >nul 2>&1
if %errorlevel% neq 0 (
    echo   ERROR: Maven not found in PATH.
    echo   Please install Maven 3.8+ and add it to PATH.
    goto :error
)
for /f "tokens=1-3" %%a in ('mvn --version ^| findstr /i "maven"') do (
    echo   Maven: %%a %%b %%c
)
echo.

REM --- Step 4: Navigate to project ---
echo [4/5] Navigating to project...
cd /d "%~dp0"
if not exist pom.xml (
    cd "..\student-portal-middleware"
)
echo   Current directory: %cd%
echo.

REM --- Step 5: Build ---
echo [5/5] Building project...
echo   Running: mvn clean package -DskipTests
mvn clean package -DskipTests -Dmaven.test.skip=true
if %errorlevel% neq 0 (
    echo   ERROR: Build failed!
    goto :error
)
echo   Build successful!
echo.

REM --- Run ---
echo ========================================================
echo Build Complete! Starting application...
echo ========================================================
echo.
echo Application will start at: http://localhost:8081
echo Swagger UI: http://localhost:8081/swagger-ui.html
echo.
echo To test endpoints in Swagger:
echo   1. Sign into AUCA student portal in your browser
echo   2. Open DevTools ^(F12^) ^> Application ^> Cookies
echo   3. Copy the 'access_token' cookie value
echo   4. In Swagger, enter: access_token=^<paste-value^>
echo.
echo Press Ctrl+C to stop the application.
echo.

REM Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

goto :end

:error
echo.
echo Setup failed. Please check the errors above.
pause
exit /b 1

:end
endlocal