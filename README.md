# Healthcare Microservices System

A production-ready **Healthcare Microservices System** built with **Spring Boot 3**, **Apache Kafka**, **MongoDB**, **React**, and **Docker**. This project is designed for self-learning and interview preparation, demonstrating real-world microservices patterns used at top tech companies.

---

## Table of Contents

- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Microservices Breakdown](#microservices-breakdown)
- [Key Design Patterns](#key-design-patterns)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [API Reference](#api-reference)
- [Event-Driven Architecture](#event-driven-architecture)
- [Security Implementation](#security-implementation)
- [Circuit Breaker Pattern](#circuit-breaker-pattern)
- [Project Structure](#project-structure)
- [Environment Configuration](#environment-configuration)
- [Testing the Application](#testing-the-application)
- [Troubleshooting](#troubleshooting)
- [Interview Questions Covered](#interview-questions-covered)

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CLIENTS                                         │
│                    (React UI / Mobile App / Postman)                        │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         API GATEWAY (Port 8080)                              │
│  ┌─────────────┐  ┌─────────────────┐  ┌─────────────────────────────────┐  │
│  │ JWT Filter  │  │ Circuit Breaker │  │ Request Routing & Load Balance │  │
│  └─────────────┘  └─────────────────┘  └─────────────────────────────────┘  │
└────────┬──────────────────┬──────────────────┬──────────────────┬───────────┘
         │                  │                  │                  │
         ▼                  ▼                  ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────────┐
│    Auth     │    │   Doctor    │    │   Patient   │    │   Appointment   │
│   Service   │    │   Service   │    │   Service   │    │     Service     │
│  (Port 8080)│    │  (Port 8080)│    │  (Port 8080)│    │   (Port 8080)   │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └────────┬────────┘
       │                  │                  │                    │
       │                  │                  │                    │ Kafka Producer
       │                  ▼                  ▼                    ▼
       │           ┌─────────────────────────────────────────────────────┐
       │           │                    MongoDB                          │
       │           │              (Port 27017)                           │
       │           └─────────────────────────────────────────────────────┘
       │
       │                                    ┌─────────────────────────────┐
       │                                    │      Apache Kafka           │
       │                                    │      (Port 9092)            │
       │                                    │   Topic: appointments       │
       │                                    └──────────────┬──────────────┘
       │                                                   │ Kafka Consumer
       │                                                   ▼
       │                                    ┌─────────────────────────────┐
       │                                    │   Notification Service      │
       │                                    │      (Email via SMTP)       │
       │                                    └─────────────────────────────┘
       │
       └──────────────────────────────────────────────────────────────────────┐
                                                                              │
┌─────────────────────────────────────────────────────────────────────────────┘
│                         Spring Boot Admin (Port 9093)
│                    (Centralized Monitoring Dashboard)
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Technology Stack

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Backend Framework** | Spring Boot | 3.x | Microservices foundation |
| **API Gateway** | Spring Cloud Gateway | 4.x | Request routing, filtering |
| **Database** | MongoDB | Latest | NoSQL document storage |
| **Message Broker** | Apache Kafka | 3.9.0 | Event-driven communication |
| **Security** | Spring Security + JWT | - | Authentication & Authorization |
| **Resilience** | Resilience4j | - | Circuit breaker pattern |
| **Monitoring** | Spring Boot Admin | 3.x | Service health monitoring |
| **Frontend** | React + Vite | 18.x | User interface |
| **Containerization** | Docker + Docker Compose | - | Container orchestration |

---

## Microservices Breakdown

### 1. Gateway Service
The single entry point for all client requests.

**Responsibilities:**
- Route requests to appropriate microservices
- JWT token validation for secured endpoints
- Circuit breaker implementation for fault tolerance
- CORS handling for cross-origin requests
- Request/Response logging

**Key Classes:**
- `GatewayConfig.java` - Route definitions
- `AuthenticationFilter.java` - JWT validation filter
- `CircuitBreakerConfiguration.java` - Resilience4j config
- `JwtUtil.java` - Token parsing utilities

---

### 2. Auth Service
Handles user authentication and authorization.

**Responsibilities:**
- User registration with role assignment
- User authentication with JWT generation
- Password encryption using BCrypt
- Role-based access control (ADMIN, DOCTOR, PATIENT)

**Key Classes:**
- `AuthController.java` - REST endpoints
- `AuthService.java` - Business logic
- `JwtUtils.java` - Token generation
- `WebSecurityConfig.java` - Security configuration

**Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/signup` | Register new user |
| POST | `/api/auth/signin` | Authenticate user |

---

### 3. Doctor Service
Manages doctor-related operations.

**Responsibilities:**
- CRUD operations for doctor profiles
- Doctor search by ID or email
- Status management (ACTIVE, INACTIVE, ON_LEAVE)

**Key Classes:**
- `DoctorController.java` - REST endpoints
- `DoctorService.java` - Business logic
- `DoctorRepository.java` - MongoDB repository

**Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/doctor/all` | Get all doctors |
| GET | `/api/v1/doctor/{id}` | Get doctor by ID |
| GET | `/api/v1/doctor/email/{email}` | Get doctor by email |
| POST | `/api/v1/doctor` | Create new doctor |
| PUT | `/api/v1/doctor/{id}` | Update doctor |
| DELETE | `/api/v1/doctor/{id}` | Delete doctor |

---

### 4. Patient Service
Manages patient-related operations.

**Responsibilities:**
- CRUD operations for patient profiles
- Patient search by ID or email

**Key Classes:**
- `PatientController.java` - REST endpoints
- `PatientService.java` - Business logic
- `PatientRepository.java` - MongoDB repository

**Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/patient/all` | Get all patients |
| GET | `/api/v1/patient/{id}` | Get patient by ID |
| GET | `/api/v1/patient/email/{email}` | Get patient by email |
| POST | `/api/v1/patient/` | Create new patient |
| PATCH | `/api/v1/patient/{id}` | Update patient |

---

### 5. Appointment Service
Core service for managing appointments with Kafka integration.

**Responsibilities:**
- Book new appointments
- Update appointment status
- Fetch appointments by doctor/patient
- Publish appointment events to Kafka

**Key Classes:**
- `AppointmentController.java` - REST endpoints
- `AppointmentService.java` - Business logic with Kafka producer
- `KafkaProducerConfig.java` - Kafka producer configuration

**Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/appointments/all` | Get all appointments |
| GET | `/api/v1/appointments/doctor/{doctorId}` | Get by doctor |
| GET | `/api/v1/appointments/patient/{patientId}` | Get by patient |
| POST | `/api/v1/appointments/create` | Book appointment |
| PUT | `/api/v1/appointments` | Update appointment |

**Appointment Status Flow:**
```
PENDING → CONFIRMED → COMPLETED
    ↓         ↓
 CANCELLED  CANCELLED
```

---

### 6. Notification Service
Event-driven service that consumes Kafka messages and sends email notifications.

**Responsibilities:**
- Consume appointment events from Kafka
- Send email notifications to doctors and patients
- Handle different appointment status notifications

**Key Classes:**
- `KafkaConsumerListener.java` - Kafka consumer with `@KafkaListener`
- `KafkaConsumerConfig.java` - Consumer configuration
- `EmailService.java` - SMTP email sender

---

### 7. Admin Service
Centralized monitoring dashboard using Spring Boot Admin.

**Responsibilities:**
- Monitor health of all microservices
- View application metrics
- Check service uptime and status

**Access:** http://localhost:9093

---

### 8. UI Service
React-based frontend application.

**Features:**
- User login/registration
- Doctor and patient management
- Appointment booking interface
- Protected routes based on authentication

---

## Key Design Patterns

### 1. API Gateway Pattern
Single entry point for all microservices, handling cross-cutting concerns.

```java
@Bean
public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("doctor-service", r -> r.path("/api/v1/doctor/**")
            .filters(f -> f
                .circuitBreaker(c -> c.setName("doctorCircuitBreaker")
                    .setFallbackUri("forward:/fallback/doctor")))
            .uri("http://doctor-service:8080"))
        .build();
}
```

### 2. Circuit Breaker Pattern
Prevents cascading failures when a service is unavailable.

```java
CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)           // Trip after 50% failures
    .waitDurationInOpenState(Duration.ofMillis(1000))
    .slidingWindowSize(2)
    .build();
```

### 3. Event-Driven Architecture
Asynchronous communication between services using Kafka.

```java
// Producer (Appointment Service)
CompletableFuture<SendResult<String, String>> future = 
    kafkaTemplate.send(topicName, appointmentJson);

// Consumer (Notification Service)
@KafkaListener(topics = "#{'${spring.kafka.topic.name}'}")
public void listen(String message) {
    Appointment appointment = objectMapper.readValue(message, Appointment.class);
    emailService.triggerEmailNotification(appointment);
}
```

### 4. Database per Service
Each microservice has its own logical database/collection in MongoDB.

### 5. JWT-based Authentication
Stateless authentication using JSON Web Tokens.

```java
public boolean isInvalid(String token) {
    return getAllClaimsFromToken(token).getExpiration().before(new Date());
}
```

---

## Prerequisites

### Required Software

| Software | Version | Download Link |
|----------|---------|---------------|
| Java | 17+ | [OpenJDK](https://openjdk.org/) |
| Maven | 3.8+ | [Maven](https://maven.apache.org/install.html) |
| Docker Desktop | Latest | [Docker](https://www.docker.com/products/docker-desktop/) |
| Node.js | 18+ | [Node.js](https://nodejs.org/) (for UI development) |

### Optional Tools
- [MongoDB Compass](https://www.mongodb.com/products/compass) - GUI for MongoDB
- [Offset Explorer](https://www.kafkatool.com/) - GUI for Kafka
- [Postman](https://www.postman.com/) - API testing

---

## Quick Start

### Step 1: Clone and Configure

```bash
# Navigate to project directory
cd smart-healthcare-system-main

# Configure email (optional - for notifications)
# Edit docker-compose.yml and update:
# - MAIL_SERVER_USERNAME=your-email@gmail.com
# - MAIL_SERVER_PASSWORD=your-app-password
```

### Step 2: Start All Services

```bash
# Build and start all containers
docker-compose up --build

# Run in detached mode (background)
docker-compose up --build -d
```

### Step 3: Verify Services

Wait 2-3 minutes for all services to start, then verify:

| Service | URL | Expected |
|---------|-----|----------|
| Gateway API | http://localhost:8080 | Running |
| React UI | http://localhost:9090 | Login page |
| Admin Panel | http://localhost:9093 | Dashboard |
| MongoDB | localhost:27017 | Connection OK |
| Kafka | localhost:9092 | Broker ready |

### Step 4: Test the API

```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": ["ROLE_ADMIN"]
  }'

# Login and get JWT token
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

### Step 5: Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

---

## API Reference

### Authentication

#### Register User
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123",
  "role": ["ROLE_DOCTOR"]
}
```

**Response:**
```json
{
  "message": "User registered successfully!"
}
```

#### Login
```http
POST /api/auth/signin
Content-Type: application/json

{
  "username": "johndoe",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "id": "64f1a2b3c4d5e6f7g8h9i0j1",
  "username": "johndoe",
  "email": "john@example.com",
  "roles": ["ROLE_DOCTOR"],
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### Doctor Management

#### Create Doctor
```http
POST /api/v1/doctor
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "email": "dr.smith@hospital.com",
  "phone": "1234567890",
  "speciality": "Cardiology",
  "yearsOfExperience": 10,
  "status": "ACTIVE"
}
```

#### Get All Doctors
```http
GET /api/v1/doctor/all
Authorization: Bearer <JWT_TOKEN>
```

**Response:**
```json
{
  "message": "Fetched doctors successfully",
  "data": [
    {
      "id": "64f1a2b3c4d5e6f7g8h9i0j1",
      "firstName": "John",
      "lastName": "Smith",
      "email": "dr.smith@hospital.com",
      "phone": "1234567890",
      "speciality": "Cardiology",
      "yearsOfExperience": 10,
      "status": "ACTIVE"
    }
  ]
}
```

---

### Patient Management

#### Create Patient
```http
POST /api/v1/patient/
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@email.com",
  "phone": "0987654321",
  "age": 30
}
```

---

### Appointment Management

#### Book Appointment
```http
POST /api/v1/appointments/create
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "doctorId": "64f1a2b3c4d5e6f7g8h9i0j1",
  "patientId": "64f1a2b3c4d5e6f7g8h9i0j2",
  "appointmentTime": "2024-12-25T10:00:00",
  "notes": "Regular checkup",
  "doctorComments": ""
}
```

#### Update Appointment Status
```http
PUT /api/v1/appointments
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "id": "appointment-uuid",
  "doctorId": "64f1a2b3c4d5e6f7g8h9i0j1",
  "patientId": "64f1a2b3c4d5e6f7g8h9i0j2",
  "appointmentTime": "2024-12-25T10:00:00",
  "status": "CONFIRMED",
  "notes": "Regular checkup",
  "doctorComments": "Patient should fast for 12 hours"
}
```

---

## Event-Driven Architecture

### Kafka Configuration

The system uses Apache Kafka 3.9.0 with KRaft mode (no Zookeeper required).

**Topic:** `appointments`

**Event Flow:**
```
1. Patient books appointment via UI
2. Appointment Service saves to MongoDB
3. Appointment Service publishes event to Kafka
4. Notification Service consumes event
5. Email notification sent to doctor and patient
```

### Kafka Producer (Appointment Service)
```java
@Value("${spring.kafka.topic.name}")
private String topicName;

private void sendEventToKafka(String appointmentJson) {
    CompletableFuture<SendResult<String, String>> future = 
        kafkaTemplate.send(topicName, appointmentJson);
    
    future.whenComplete((result, exception) -> {
        if (exception == null) {
            logger.info("Message sent to topic: {}", topicName);
        } else {
            logger.error("Failed to send message: {}", exception.getMessage());
        }
    });
}
```

### Kafka Consumer (Notification Service)
```java
@KafkaListener(topics = "#{'${spring.kafka.topic.name}'}")
public void listen(String message) {
    Appointment appointment = objectMapper.readValue(message, Appointment.class);
    emailService.triggerEmailNotification(appointment);
}
```

---

## Security Implementation

### JWT Token Structure
```
Header: { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "username", "email": "user@example.com", "iat": 1234567890, "exp": 1234567890 }
Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

### Protected vs Open Endpoints

**Open Endpoints (No Auth Required):**
- `POST /api/auth/signup`
- `POST /api/auth/signin`

**Protected Endpoints (JWT Required):**
- All `/api/v1/doctor/**` endpoints
- All `/api/v1/patient/**` endpoints
- All `/api/v1/appointments/**` endpoints

### Gateway Authentication Filter
```java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    
    if (routerValidator.isSecured.test(request)) {
        if (isAuthMissing(request)) {
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Authorization header is missing.");
        }
        
        final String token = getAuthHeader(request);
        if (jwtUtil.isInvalid(token)) {
            return onError(exchange, HttpStatus.FORBIDDEN, "Invalid or expired token.");
        }
    }
    
    return chain.filter(exchange);
}
```

---

## Circuit Breaker Pattern

### Configuration
```java
CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
    .failureRateThreshold(50)                    // Open circuit after 50% failures
    .waitDurationInOpenState(Duration.ofMillis(1000))  // Wait 1 second before half-open
    .slidingWindowSize(2)                        // Evaluate last 2 calls
    .build();

TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
    .timeoutDuration(Duration.ofSeconds(4))      // Timeout after 4 seconds
    .build();
```

### Circuit Breaker States
```
CLOSED → (failure threshold exceeded) → OPEN
OPEN → (wait duration elapsed) → HALF_OPEN
HALF_OPEN → (test call succeeds) → CLOSED
HALF_OPEN → (test call fails) → OPEN
```

### Fallback Endpoints
When a circuit is open, requests are redirected to fallback endpoints:
- `/fallback/doctor` - Doctor service unavailable
- `/fallback/patient` - Patient service unavailable
- `/fallback/appointment` - Appointment service unavailable
- `/fallback/auth` - Auth service unavailable

---

## Project Structure

```
smart-healthcare-system-main/
├── docker-compose.yml              # Container orchestration
├── README.md                       # This file
│
├── gateway-service/                # API Gateway
│   ├── src/main/java/.../
│   │   ├── config/
│   │   │   ├── GatewayConfig.java
│   │   │   ├── CircuitBreakerConfiguration.java
│   │   │   ├── JwtUtil.java
│   │   │   └── RouterValidator.java
│   │   ├── filter/
│   │   │   └── AuthenticationFilter.java
│   │   └── controller/
│   │       └── FallbackController.java
│   └── Dockerfile
│
├── auth-service/                   # Authentication Service
│   ├── src/main/java/.../
│   │   ├── controllers/
│   │   ├── models/
│   │   ├── repository/
│   │   ├── security/
│   │   │   ├── jwt/
│   │   │   └── services/
│   │   └── services/
│   └── Dockerfile
│
├── doctor-service/                 # Doctor Management
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── services/
│   │   └── exception/
│   └── Dockerfile
│
├── patient-service/                # Patient Management
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── services/
│   │   └── exception/
│   └── Dockerfile
│
├── appointment-service/            # Appointment Management + Kafka Producer
│   ├── src/main/java/.../
│   │   ├── controller/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   └── config/
│   │       ├── KafkaProducerConfig.java
│   │       └── RestTemplateConfig.java
│   └── Dockerfile
│
├── notification-service/           # Kafka Consumer + Email
│   ├── src/main/java/.../
│   │   ├── config/
│   │   │   └── KafkaConsumerConfig.java
│   │   ├── listener/
│   │   │   └── KafkaConsumerListener.java
│   │   ├── model/
│   │   └── service/
│   │       └── EmailService.java
│   └── Dockerfile
│
├── admin-service/                  # Spring Boot Admin
│   └── Dockerfile
│
└── ui-component/                   # React Frontend
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   ├── services/
    │   └── auth/
    └── Dockerfile
```

---

## Environment Configuration

### Docker Compose Environment Variables

```yaml
# MongoDB
MONGO_INITDB_ROOT_USERNAME: root
MONGO_INITDB_ROOT_PASSWORD: rootpassword
MONGO_URI: mongodb://root:rootpassword@mongodb:27017/healthcare?authSource=admin

# JWT
JWT_SECRET: aHR0cHM6Ly93d3cueW91YmlsdW1hbWVyaWNhLmNvbS9sb2dpbi8=

# Kafka
KAFKA_BOOTSTRAP_SERVER: kafka:9092
KAFKA_TOPIC: appointments
KAFKA_GROUP_ID: appointment-notifications-group

# Email (Update these for notifications to work)
MAIL_SERVER_HOST: smtp.gmail.com
MAIL_SERVER_PORT: 587
MAIL_SERVER_USERNAME: your-email@gmail.com
MAIL_SERVER_PASSWORD: your-app-password
```

### Gmail App Password Setup

1. Enable 2-Factor Authentication on your Google account
2. Go to Google Account → Security → App passwords
3. Generate a new app password for "Mail"
4. Use this password in `MAIL_SERVER_PASSWORD`

---

## Testing the Application

### Using Postman

1. Import the collection (create requests based on API Reference)
2. Set up environment variable for `{{baseUrl}}` = `http://localhost:8080`
3. After login, store the token in `{{authToken}}`
4. Add `Authorization: Bearer {{authToken}}` header to protected requests

### Using cURL

```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@test.com","password":"admin123","role":["ROLE_ADMIN"]}'

# 2. Login (save the accessToken)
curl -X POST http://localhost:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 3. Create Doctor (use token from step 2)
curl -X POST http://localhost:8080/api/v1/doctor \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@hospital.com","phone":"1234567890","speciality":"Cardiology","yearsOfExperience":10,"status":"ACTIVE"}'

# 4. Get All Doctors
curl -X GET http://localhost:8080/api/v1/doctor/all \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Verify Kafka Messages

```bash
# Connect to Kafka container
docker exec -it kafka /bin/bash

# List topics
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

# Consume messages from appointments topic
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic appointments --from-beginning
```

---

## Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| **Kafka not starting** | Ensure Docker has enough memory (4GB+). Check logs: `docker logs kafka` |
| **MongoDB connection failed** | Verify credentials in docker-compose.yml. Check if port 27017 is free |
| **JWT token invalid** | Ensure same secret in gateway-service and auth-service |
| **Email not sending** | Verify Gmail app password. Check notification-service logs |
| **Service timeout** | Increase circuit breaker timeout. Check service health in Admin panel |
| **Port already in use** | Stop conflicting services or change ports in docker-compose.yml |
| **UI blank screen / JSON parse error** | Clear browser localStorage (see below) |

### UI Not Loading / Blank Screen Fix

If you see a blank screen at `http://localhost:9090` with console errors like:
```
SyntaxError: "undefined" is not valid JSON
```

This happens due to corrupted data in browser localStorage. **To fix:**

**Option 1: Clear localStorage (Quick Fix)**
1. Open browser DevTools (Press `F12` or `Ctrl+Shift+I`)
2. Go to **Application** tab (Chrome) or **Storage** tab (Firefox)
3. Expand **Local Storage** in the left sidebar
4. Click on `http://localhost:9090`
5. Click the **Clear All** button (🚫 icon) or right-click and select "Clear"
6. Refresh the page (`Ctrl+R` or `F5`)

**Option 2: Using Console (Alternative)**
1. Open browser DevTools (`F12`)
2. Go to **Console** tab
3. Paste and run:
```javascript
localStorage.clear();
location.reload();
```

**Option 3: Incognito/Private Window**
- Open `http://localhost:9090` in an Incognito/Private browser window

> **Note:** This issue typically occurs when the app was previously opened before all services were ready, or after a failed login attempt.

### Useful Commands

```bash
# View all container logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f appointment-service

# Restart a specific service
docker-compose restart notification-service

# Check container status
docker-compose ps

# Remove all containers and volumes
docker-compose down -v

# Rebuild specific service
docker-compose up --build appointment-service
```

### Health Check URLs

| Service | Health Check |
|---------|--------------|
| Gateway | http://localhost:8080/actuator/health |
| Auth | Internal only (via gateway) |
| Doctor | Internal only (via gateway) |
| Patient | Internal only (via gateway) |
| Appointment | Internal only (via gateway) |
| Admin | http://localhost:9093 |

---

## Interview Questions Covered

This project covers the following commonly asked interview topics:

### Microservices Architecture
- What is a microservice? How is it different from monolithic?
- How do microservices communicate with each other?
- What is an API Gateway and why is it needed?
- How do you handle distributed transactions?

### Spring Boot & Spring Cloud
- What is Spring Cloud Gateway?
- How does request routing work in Spring Cloud Gateway?
- What are Gateway Filters and how do you create custom filters?
- Explain the difference between `@Component` and `@Bean`

### Apache Kafka
- What is Kafka and why use it?
- Explain Producer-Consumer pattern
- What is a Kafka Topic, Partition, and Consumer Group?
- How does Kafka ensure message ordering?
- What is KRaft mode in Kafka?

### Security
- How does JWT authentication work?
- What's the difference between Authentication and Authorization?
- How do you secure microservices?
- What is CORS and how do you handle it?

### Resilience Patterns
- What is a Circuit Breaker? Explain its states.
- What is the difference between Circuit Breaker and Retry pattern?
- How does Resilience4j work?
- What is a Fallback method?

### Database
- Why use MongoDB in microservices?
- What is the Database-per-Service pattern?
- How do you handle data consistency across services?

### Docker & DevOps
- What is Docker Compose?
- How do you handle service dependencies in Docker?
- What are Docker healthchecks?
- How do you debug containerized applications?

---

## Next Steps for Learning

1. **Add Service Discovery** - Integrate Eureka or Consul
2. **Implement Config Server** - Centralized configuration
3. **Add Distributed Tracing** - Integrate Zipkin or Jaeger
4. **Implement CQRS** - Separate read and write operations
5. **Add API Rate Limiting** - Prevent abuse
6. **Implement Saga Pattern** - Distributed transactions
7. **Add Kubernetes Deployment** - Container orchestration at scale


**Happy Learning! 🚀**
