# RideShare Backend API

A RESTful backend API for a ride-sharing application built with Spring Boot, MongoDB, and JWT Authentication.

## Tech Stack

- **Spring Boot 3.2.0** - Java framework
- **MongoDB Atlas** - NoSQL database
- **JWT (JSON Web Tokens)** - Authentication
- **BCrypt** - Password encryption
- **Jakarta Validation** - Input validation
- **Lombok** - Boilerplate code reduction

## Features

- User Registration & Login with JWT
- Role-based Access Control (USER / DRIVER)
- Ride Request Management
- Driver Ride Acceptance
- Ride Completion
- Global Exception Handling
- Input Validation

## Project Structure

```
src/main/java/org/example/rideshare/
├── model/
│   ├── User.java
│   └── Ride.java
├── repository/
│   ├── UserRepository.java
│   └── RideRepository.java
├── dto/
│   ├── RegisterRequest.java
│   ├── LoginRequest.java
│   ├── AuthResponse.java
│   ├── CreateRideRequest.java
│   └── RideResponse.java
├── service/
│   ├── AuthService.java
│   └── RideService.java
├── controller/
│   ├── AuthController.java
│   └── RideController.java
├── config/
│   ├── SecurityConfig.java
│   └── JwtFilter.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── NotFoundException.java
│   ├── BadRequestException.java
│   └── ErrorResponse.java
├── util/
│   └── JwtUtil.java
└── RideshareApplication.java
```

## Entity Models

### User
| Field | Type | Description |
|-------|------|-------------|
| id | String | Unique identifier (MongoDB ObjectId) |
| username | String | User's username |
| password | String | BCrypt encrypted password |
| role | String | ROLE_USER or ROLE_DRIVER |

### Ride
| Field | Type | Description |
|-------|------|-------------|
| id | String | Unique identifier |
| userId | String | Passenger's user ID |
| driverId | String | Driver's user ID (null until accepted) |
| pickupLocation | String | Pickup address |
| dropLocation | String | Drop-off address |
| status | String | REQUESTED / ACCEPTED / COMPLETED |
| createdAt | Date | Timestamp of ride creation |

## API Endpoints

### Authentication (Public)

#### Register User
```
POST /api/auth/register
```
**Request Body:**
```json
{
  "username": "john",
  "password": "1234",
  "role": "ROLE_USER"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john",
  "role": "ROLE_USER"
}
```

#### Login
```
POST /api/auth/login
```
**Request Body:**
```json
{
  "username": "john",
  "password": "1234"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john",
  "role": "ROLE_USER"
}
```

---

### User Endpoints (Requires ROLE_USER)

#### Create Ride Request
```
POST /api/v1/rides
Authorization: Bearer <token>
```
**Request Body:**
```json
{
  "pickupLocation": "Koramangala",
  "dropLocation": "Indiranagar"
}
```
**Response:**
```json
{
  "id": "6754abc123",
  "userId": "6754xyz789",
  "driverId": null,
  "pickupLocation": "Koramangala",
  "dropLocation": "Indiranagar",
  "status": "REQUESTED",
  "createdAt": "2025-12-07T10:30:00.000Z"
}
```

#### Get My Rides
```
GET /api/v1/user/rides
Authorization: Bearer <token>
```
**Response:**
```json
[
  {
    "id": "6754abc123",
    "userId": "6754xyz789",
    "driverId": "6754driver456",
    "pickupLocation": "Koramangala",
    "dropLocation": "Indiranagar",
    "status": "COMPLETED",
    "createdAt": "2025-12-07T10:30:00.000Z"
  }
]
```

---

### Driver Endpoints (Requires ROLE_DRIVER)

#### View Pending Ride Requests
```
GET /api/v1/driver/rides/requests
Authorization: Bearer <token>
```
**Response:**
```json
[
  {
    "id": "6754abc123",
    "userId": "6754xyz789",
    "driverId": null,
    "pickupLocation": "Koramangala",
    "dropLocation": "Indiranagar",
    "status": "REQUESTED",
    "createdAt": "2025-12-07T10:30:00.000Z"
  }
]
```

#### Accept a Ride
```
POST /api/v1/driver/rides/{rideId}/accept
Authorization: Bearer <token>
```
**Response:**
```json
{
  "id": "6754abc123",
  "userId": "6754xyz789",
  "driverId": "6754driver456",
  "pickupLocation": "Koramangala",
  "dropLocation": "Indiranagar",
  "status": "ACCEPTED",
  "createdAt": "2025-12-07T10:30:00.000Z"
}
```

---

### Shared Endpoints (USER or DRIVER)

#### Complete a Ride
```
POST /api/v1/rides/{rideId}/complete
Authorization: Bearer <token>
```
**Response:**
```json
{
  "id": "6754abc123",
  "userId": "6754xyz789",
  "driverId": "6754driver456",
  "pickupLocation": "Koramangala",
  "dropLocation": "Indiranagar",
  "status": "COMPLETED",
  "createdAt": "2025-12-07T10:30:00.000Z"
}
```

---

## API Summary Table

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login and get JWT |
| POST | `/api/v1/rides` | USER | Create ride request |
| GET | `/api/v1/user/rides` | USER | Get user's rides |
| GET | `/api/v1/driver/rides/requests` | DRIVER | Get pending rides |
| POST | `/api/v1/driver/rides/{id}/accept` | DRIVER | Accept a ride |
| POST | `/api/v1/rides/{id}/complete` | USER/DRIVER | Complete a ride |

---

## Error Responses

All errors return a consistent format:
```json
{
  "error": "ERROR_TYPE",
  "message": "Description of the error",
  "timestamp": "2025-12-07T10:30:00.000Z"
}
```

| Error Type | HTTP Status | Description |
|------------|-------------|-------------|
| VALIDATION_ERROR | 400 | Invalid input data |
| BAD_REQUEST | 400 | Business logic error |
| NOT_FOUND | 404 | Resource not found |
| INTERNAL_ERROR | 500 | Server error |

---

## Setup & Installation

### Prerequisites
- Java 17+
- MongoDB Atlas account (or local MongoDB)

### 1. Clone the repository
```bash
git clone https://github.com/Ridaa10394/rideshare-ridaa.git
cd rideshare-ridaa
```

### 2. Configure MongoDB
Update `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb+srv://USERNAME:PASSWORD@cluster.mongodb.net/rideshare
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```
The server starts at `http://localhost:8081`

---

## Testing with cURL

### 1. Register a User
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"1234","role":"ROLE_USER"}'
```

### 2. Register a Driver
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"driver1","password":"1234","role":"ROLE_DRIVER"}'
```

### 3. Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"1234"}'
```

### 4. Create Ride (use USER token)
```bash
curl -X POST http://localhost:8081/api/v1/rides \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"pickupLocation":"Koramangala","dropLocation":"Indiranagar"}'
```

### 5. View Pending Rides (use DRIVER token)
```bash
curl -X GET http://localhost:8081/api/v1/driver/rides/requests \
  -H "Authorization: Bearer DRIVER_TOKEN"
```

### 6. Accept Ride (use DRIVER token)
```bash
curl -X POST http://localhost:8081/api/v1/driver/rides/RIDE_ID/accept \
  -H "Authorization: Bearer DRIVER_TOKEN"
```

### 7. Complete Ride
```bash
curl -X POST http://localhost:8081/api/v1/rides/RIDE_ID/complete \
  -H "Authorization: Bearer TOKEN"
```

---

## JWT Token Structure

The JWT token contains:
- `sub` - Username
- `role` - User role (ROLE_USER / ROLE_DRIVER)
- `iat` - Issued at timestamp
- `exp` - Expiration timestamp (24 hours)

**Usage:** Include in Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## Ride Status Flow

```
REQUESTED  →  ACCEPTED  →  COMPLETED
    ↑             ↑            ↑
  User        Driver       User/Driver
 creates      accepts      completes
```

---

## Author

Ridaa

## License

This project is for educational purposes.
