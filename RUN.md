# Student Portal Middleware - Quick Run Guide
# ================================================
#
# This file provides all commands to run the project
# locally from scratch in development mode.
#
# Project Location:
# C:\Users\rukundo divin\Documents\student-portal-middleware-v2\student-portal-middleware
#
# ================================================================

# ----------------------------------------------------------------
# METHOD 1: Using the PowerShell Setup Script (RECOMMENDED)
# ----------------------------------------------------------------
# Run in PowerShell:
#   cd C:\Users\rukundo divin\Documents\student-portal-middleware-v2
#   .\setup.ps1
#
# This script will:
#   1. Set AUCA_SERVICE_USERNAME=25306
#   2. Set AUCA_SERVICE_PASSWORD=25306
#   3. Add Java to PATH
#   4. Verify Maven installation
#   5. Build the project
#   6. Start in development mode

# ----------------------------------------------------------------
# METHOD 2: Manual Commands (PowerShell)
# ----------------------------------------------------------------

# Step 1: Set environment variables
$env:AUCA_SERVICE_USERNAME = "25306"
$env:AUCA_SERVICE_PASSWORD = "25306"

# Step 2: Add Java to PATH
$env:PATH = "C:\Program Files\Java\jdk-19\bin;$env:PATH"

# Step 3: Navigate to project
cd "C:\Users\rukundo divin\Documents\student-portal-middleware-v2\student-portal-middleware"

# Step 4: Verify Java
java -version
# Expected: openjdk version "17" or higher

# Step 5: Verify Maven
mvn --version
# Expected: Apache Maven 3.8+

# Step 6: Build the project
mvn clean package -DskipTests

# Step 7: Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# ----------------------------------------------------------------
# METHOD 3: Run Compiled JAR
# ----------------------------------------------------------------

# After building with "mvn clean package -DskipTests":
java -jar target/student-portal-middleware-1.0.0.jar --spring.profiles.active=dev

# ----------------------------------------------------------------
# Verify Application is Running
# ----------------------------------------------------------------

# Health check:
curl http://localhost:8081/actuator/health
# Expected: {"status":"UP"}

# Swagger UI:
# Open browser: http://localhost:8081/swagger-ui.html

# ----------------------------------------------------------------
# Test Student Endpoints
# ----------------------------------------------------------------

# With AUCA session cookie:
curl -X GET "http://localhost:8081/api/v1/student/payments" \
  -H "Cookie: access_token=<your-session-cookie>"

curl -X GET "http://localhost:8081/api/v1/student/balance" \
  -H "Cookie: access_token=<your-session-cookie>"

curl -X GET "http://localhost:8081/api/v1/student/fees" \
  -H "Cookie: access_token=<your-session-cookie>"

# ----------------------------------------------------------------
# Environment Variables
# ----------------------------------------------------------------
# AUCA_SERVICE_USERNAME=25306 (dedicated service account)
# AUCA_SERVICE_PASSWORD=25306 (dedicated service account)

# ----------------------------------------------------------------
# Stop Application
# ----------------------------------------------------------------
# Press Ctrl+C in the terminal

# ----------------------------------------------------------------
# Troubleshooting
# ----------------------------------------------------------------

# Port 8081 already in use:
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Rebuild with clean:
mvn clean compile -DskipTests

# Force update dependencies:
mvn clean package -U -DskipTests

# ================================================================
# For detailed documentation, see: SETUP.md
# ================================================================
