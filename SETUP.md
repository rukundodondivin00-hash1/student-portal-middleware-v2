#####################################################################
# QUICK START - Run Student Portal Middleware Locally
# Development Mode Guide
#####################################################################

## Prerequisites
- Java JDK 17+ (OpenJDK or Oracle JDK)
- Apache Maven 3.8+
- Git (optional, for cloning)

## Project Location
```
C:\Users\rukundo divin\Documents\student-portal-middleware-v2\student-portal-middleware
```

## Quick Start (PowerShell)

### Option A: Automated Setup (Recommended)
Run the provided setup script:
```powershell
cd "C:\Users\rukundo divin\Documents\student-portal-middleware-v2"
.\setup.ps1
```

### Option B: Manual Setup

#### 1. Set Environment Variables
```powershell
$env:AUCA_SERVICE_USERNAME = "25306"
$env:AUCA_SERVICE_PASSWORD = "25306"
```

#### 2. Add Java to PATH (if not already)
```powershell
$env:PATH = "C:\Program Files\Java\jdk-19\bin;$env:PATH"
```

#### 3. Verify Java
```powershell
java -version
# Should show: openjdk version "17" or higher
```

#### 4. Navigate to Project
```powershell
cd "C:\Users\rukundo divin\Documents\student-portal-middleware-v2\student-portal-middleware"
```

#### 5. Build the Project
```powershell
mvn clean package -DskipTests
```

#### 6. Run in Development Mode
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Or run the JAR directly:
```powershell
java -jar target/student-portal-middleware-1.0.0.jar --spring.profiles.active=dev
```

## Verify It's Running

The application starts at: **http://localhost:8081**

### Health Check
```powershell
curl http://localhost:8081/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### Swagger UI
Open in browser: **http://localhost:8081/swagger-ui.html**

## Testing Student Endpoints

### Using Swagger UI
1. Open `http://localhost:8081/swagger-ui.html`
2. Sign into the AUCA student portal in your browser
3. Open DevTools (F12) → Application → Cookies
4. Copy the `access_token` cookie value
5. In Swagger, find the Cookie field on any student endpoint
6. Enter: `access_token=<paste-value-here>`
7. Click Execute

### Using curl
```powershell
# With AUCA session cookie
curl -X GET "http://localhost:8081/api/v1/student/payments" `
  -H "Cookie: access_token=<your-session-cookie>"

# Check balance
curl -X GET "http://localhost:8081/api/v1/student/balance" `
  -H "Cookie: access_token=<your-session-cookie>"

# Check fees
curl -X GET "http://localhost:8081/api/v1/student/fees" `
  -H "Cookie: access_token=<your-session-cookie>"
```

## API Endpoints

### Student Endpoints (require AUCA session cookie)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/student/payments` | Student's payment history |
| GET | `/api/v1/student/fees` | Student's registration fees |
| GET | `/api/v1/student/balance` | Student's current balance |
| POST | `/api/v1/student/payments/initiate` | Initiate a new payment |

### Webhook (open)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/finance/student-payments/notifications` | Receive payment notification from Urubuto |

### Middleware Management
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/middleware/auth/refresh` | Force re-login of service account |
| GET | `/actuator/health` | Health check |

## Configuration

### application.yml
The main configuration file:
```yaml
server:
  port: 8081

auca:
  api:
    base-url: https://auca-ims.onrender.com
    service-username: ${AUCA_SERVICE_USERNAME}  # From env var
    service-password: ${AUCA_SERVICE_PASSWORD}  # From env var
  auth:
    refresh-interval-ms: 840000  # 14 minutes
```

### Environment Variables
| Variable | Required | Description |
|----------|----------|-------------|
| `AUCA_SERVICE_USERNAME` | Yes | Service account username for AUCA API authentication |
| `AUCA_SERVICE_PASSWORD` | Yes | Service account password for AUCA API authentication |

### Dev vs Prod Profiles
- **Dev** (`-Dspring-boot.run.profiles=dev`): DEBUG logging, 30s connect timeout, 60s read timeout, 5min auth refresh
- **Prod** (default): INFO logging, 10s connect timeout, 30s read timeout, 14min auth refresh

## Troubleshooting

### Port 8081 Already in Use
```powershell
# Stop the process using port 8081
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Or change the port in application.yml
server:
  port: 8082
```

### Build Failures
```powershell
# Clean and rebuild
mvn clean
mvn package -DskipTests

# Force update dependencies
mvn clean package -U -DskipTests
```

### Authentication Failures
- Verify environment variables are set correctly:
  ```powershell
  echo $env:AUCA_SERVICE_USERNAME
  echo $env:AUCA_SERVICE_PASSWORD
  ```
- Check logs for login errors
- Ensure the AUCA service account credentials are valid

### Connection Timeouts
- The AUCA API (`auca-ims.onrender.com`) may be slow
- Increase timeouts in `application.yml`:
  ```yaml
  auca:
    api:
      connect-timeout-seconds: 30
      read-timeout-seconds: 120
  ```

## Development Workflow

### Make Code Changes
1. Edit Java files in `src/main/java/com/auca/studentportal/`
2. The app auto-reloads in dev mode (Spring DevTools)

### Run Tests
```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=StudentPaymentControllerTest
```

### Hot Reload
In development mode, changes to:
- Java files → auto-compile and reload
- Templates → auto-reload
- Static resources → auto-reload

### View Logs
```powershell
# In dev mode, logs show DEBUG level for application code
# Check for:
# - Service account login status
# - API calls to AUCA Finance
# - Cookie management
```

## Stop the Application
Press `Ctrl+C` in the terminal running the application.

## Project Structure
```
src/main/java/com/auca/studentportal/
├── controller/          # REST endpoints
│   ├── StudentPaymentController.java
│   ├── FinanceWebhookController.java
│   └── AuthController.java
├── service/             # Business logic
│   ├── StudentPaymentService.java
│   ├── WebhookService.java
│   └── AuthService.java
├── client/              # AUCA API client
│   ├── FinanceApiClient.java
│   └── FinanceApiClientImpl.java
├── cookie/              # Cookie management
│   └── CookieManager.java
├── config/              # Configuration
│   ├── AucaApiProperties.java
│   ├── AppConfig.java
│   ├── SwaggerConfig.java
│   └── SecurityConfig.java
├── dto/                 # Data transfer objects
└── exception/           # Exception handling
```

## Next Steps

1. **Explore the code**: Review controllers in `src/main/java/com/auca/studentportal/controller/`
2. **Test the API**: Use Swagger UI at `http://localhost:8081/swagger-ui.html`
3. **Add features**: Implement new endpoints or enhance existing ones
4. **Write tests**: Add unit tests in `src/test/java/com/auca/studentportal/`

## Support

For issues or questions:
- Check the README: `student-portal-middleware/README.md`
- Review logs for detailed error messages
- Verify all prerequisites are installed correctly
