#!/bin/bash
# =====================================================================
# Bash Script - Quick Start for Student Portal Middleware
# Run this script to set up environment and start the application
# =====================================================================

set -e  # Exit on any error

echo "============================================================"
echo "Student Portal Middleware - Linux/Mac Setup"
echo "============================================================"
echo ""

# --- Colors for output ---
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# --- Step 1: Set Environment Variables ---
echo -e "${YELLOW}[1/5]${NC} Setting environment variables..."
export AUCA_SERVICE_USERNAME=25306
export AUCA_SERVICE_PASSWORD=25306
echo -e "  AUCA_SERVICE_USERNAME=$AUCA_SERVICE_USERNAME"
echo -e "  AUCA_SERVICE_PASSWORD=******"
echo ""

# --- Step 2: Check Java ---
echo -e "${YELLOW}[2/5]${NC} Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo -e "  ${RED}ERROR: Java not found in PATH.${NC}"
    echo "  Please install JDK 17+"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
echo -e "  Java version: $JAVA_VERSION"
echo ""

# --- Step 3: Check Maven ---
echo -e "${YELLOW}[3/5]${NC} Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo -e "  ${RED}ERROR: Maven not found in PATH.${NC}"
    echo "  Please install Maven 3.8+"
    exit 1
fi
MVN_VERSION=$(mvn --version | head -n 1)
echo -e "  $MVN_VERSION"
echo ""

# --- Step 4: Navigate to project ---
echo -e "${YELLOW}[4/5]${NC} Navigating to project..."
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"
echo -e "  Current directory: $(pwd)"
echo ""

# --- Step 5: Build ---
echo -e "${YELLOW}[5/5]${NC} Building project..."
echo "  Running: mvn clean package -DskipTests"
mvn clean package -DskipTests -Dmaven.test.skip=true
echo -e "  ${GREEN}Build successful!${NC}"
echo ""

# --- Run ---
echo "============================================================"
echo -e "${CYAN}Build Complete! Starting application...${NC}"
echo "============================================================"
echo ""
echo -e "Application will start at: ${GREEN}http://localhost:8081${NC}"
echo -e "Swagger UI: ${GREEN}http://localhost:8081/swagger-ui.html${NC}"
echo ""
echo "To test endpoints:"
echo "  curl http://localhost:8081/actuator/health"
echo ""
echo "To test student endpoints (requires AUCA session cookie):"
echo "  curl -X GET http://localhost:8081/api/v1/student/payments \\"
echo "    -H 'Cookie: access_token=<your-session-cookie>'"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop the application${NC}"
echo ""

# Run in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev