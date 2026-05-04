# Student Portal Middleware

Finance–Student Portal Integration Service | Spring Boot 3.2 | Port 8081

---

## What This Does

This middleware is a secure bridge between the Student Portal and the AUCA Finance API.

**Flow:**
1. Student authenticates with AUCA → receives session cookies
2. Student Portal forwards those cookies to this middleware
3. Middleware forwards cookies to Finance API (`https://auca-ims.onrender.com`)
4. Finance identifies the student from the cookie and returns their payments
5. Middleware returns the data to the portal

---

## Before You Run

### Set Environment Variables

```bash
export AUCA_SERVICE_USERNAME=your-service-account-username
export AUCA_SERVICE_PASSWORD=your-service-account-password
```

These are the credentials of the **dedicated middleware service account** created by the AUCA backend team. The middleware uses this account to authenticate itself for webhook forwarding.

---

## Running Locally

```bash
# Development mode (debug logging)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production mode
./mvnw spring-boot:run
```

Service starts at: `http://localhost:8081`

---

## Swagger UI (Testing)

Open in browser: `http://localhost:8081/swagger-ui.html`

**How to test student endpoints in Swagger:**
1. Sign into the AUCA student portal in your browser
2. Open browser DevTools → Application → Cookies
3. Copy the `access_token` cookie value
4. In Swagger, find the Cookie field on any student endpoint
5. Enter: `access_token=<paste-value-here>`
6. Click Execute

---

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

---

## Deploying to Render

1. Push this project to GitHub
2. Create a new **Web Service** on Render
3. Connect your GitHub repo
4. Set build command: `./mvnw clean package -DskipTests`
5. Set start command: `java -jar target/student-portal-middleware-1.0.0.jar`
6. Add environment variables in Render dashboard:
   - `AUCA_SERVICE_USERNAME` = your service account username
   - `AUCA_SERVICE_PASSWORD` = your service account password
7. Deploy — Swagger will be live at `https://your-app.onrender.com/swagger-ui.html`

---

## Project Structure

```
src/main/java/com/auca/studentportal/
├── controller/
│   ├── StudentPaymentController.java    ← Student-facing endpoints
│   ├── FinanceWebhookController.java    ← Webhook receiver
│   └── AuthController.java             ← Service account management
├── service/
│   ├── StudentPaymentService.java       ← Business logic
│   ├── WebhookService.java             ← Notification forwarding
│   └── AuthService.java                ← Login + auto-refresh
├── client/
│   ├── FinanceApiClient.java           ← Interface
│   └── FinanceApiClientImpl.java       ← HTTP calls to Finance API
├── cookie/
│   └── CookieManager.java             ← Stores service account cookies
├── config/
│   ├── AucaApiProperties.java         ← Typed config properties
│   ├── AppConfig.java                 ← RestTemplate bean
│   ├── SwaggerConfig.java             ← Swagger/OpenAPI setup
│   └── SecurityConfig.java            ← Spring Security config
├── dto/                               ← Request/response shapes
└── exception/                         ← Error handling
```
