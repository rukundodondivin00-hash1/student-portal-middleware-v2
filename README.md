# Student Portal Middleware

Finance–Student Portal Integration Service | Spring Boot 3.2 | Port 8081

---

## What This Does

This middleware bridges the Student Portal and AUCA Finance API.

**Flow:**
1. Student authenticates with AUCA ? receives JWT access token
2. Student Portal forwards JWT to middleware
3. Middleware extracts studentId and calls Finance API with API key
4. Finance API returns the correct student's data

---

## Environment Variables

`bash
AUCA_API_KEY=your-api-key-from-auca-backend
`

---

## API Endpoints

- GET /api/v1/student/payments
- GET /api/v1/student/fees  
- GET /api/v1/student/balance
- POST /api/v1/finance/student-payments/notifications

---

## Deploy

1. Push to GitHub
2. Create Render Web Service
3. Build: ./mvnw clean package -DskipTests
4. Start: java -jar target/student-portal-middleware-1.0.0.jar
5. Env var: AUCA_API_KEY

