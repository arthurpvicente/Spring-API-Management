# Spring API Management

[![CI](https://github.com/arthurpvicente/Spring-API-Management/actions/workflows/ci.yml/badge.svg)](https://github.com/arthurpvicente/Spring-API-Management/actions/workflows/ci.yml)

A RESTful API for personal finance management built with Spring Boot. Track users, categorize transactions, and manage income and outgoing records with JWT authentication and status tracking.

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 22 | Language runtime |
| Spring Boot | 3.3.2 | Application framework |
| Spring Security | 6.x | JWT authentication |
| Spring Data JPA | - | Database ORM |
| H2 Database | - | In-memory database (dev/test) |
| SpringDoc OpenAPI | 2.6.0 | Auto-generated API docs |
| Maven | 3.9.7 | Build & dependency management |

## Architecture

```
Controller  ->  Service  ->  Repository  ->  Database
    |               |             |
 REST API     Business Logic   JPA/Hibernate
    |
 JWT Filter (authentication)
```

**Entities:**

- **User** — name, email, password (BCrypt hashed)
- **Category** — title (groups incomes and outgoings)
- **Income** — title, value, date, status (RECEIVED, PENDING, SCHEDULED, LATE)
- **Outgoing** — title, value, date, status (PAID, PENDING, SCHEDULED, LATE)

**Relationships:**

```
User 1───* Income
User 1───* Outgoing
Category 1───* Income
Category 1───* Outgoing
```

## Prerequisites

- Java 22+
- Maven 3.9+ (or use the included `./mvnw` wrapper)

## Getting Started

1. Clone the repository:

```sh
git clone https://github.com/arthurpvicente/Spring-API-Management.git
cd Spring-API-Management
```

2. Build the project:

```sh
./mvnw clean install
```

3. Run the backend:

```sh
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080` with preloaded test data.

4. Run the frontend (in a separate terminal):

```sh
cd frontend
npm install
npm run dev
```

The dashboard opens at `http://localhost:5173`.

5. Access the interactive API docs:

```
http://localhost:8080/swagger-ui/index.html
```

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. All endpoints except `/auth/**` and `/swagger-ui/**` require a valid token.

### Register

```sh
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name": "John", "email": "john@example.com", "password": "securepass"}'
```

**Response** `201 Created`

```json
{
  "id": 3,
  "name": "John",
  "email": "john@example.com"
}
```

### Login

```sh
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "john@example.com", "password": "securepass"}'
```

**Response** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 86400000
}
```

### Using the token

Include the token in the `Authorization` header for all protected endpoints:

```sh
curl http://localhost:8080/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

**Test credentials** are preloaded by `TestConfig` when using the `teste` profile. See `CLAUDE.md` for details.

## API Endpoints

### Users (full CRUD)

| Method | Endpoint | Description |
|---|---|---|
| GET | /users | Retrieve all users |
| GET | /users/{id} | Retrieve a user by ID |
| POST | /users | Create a new user |
| PUT | /users/{id} | Update a user by ID |
| DELETE | /users/{id} | Delete a user by ID |

**Create a user** `POST /users`

```json
{
  "name": "Arthur",
  "email": "arthur@example.com",
  "password": "securepass"
}
```

**Response** `201 Created`

```json
{
  "id": 1,
  "name": "Arthur",
  "email": "arthur@example.com"
}
```

---

### Incomes

| Method | Endpoint | Description |
|---|---|---|
| GET | /incomes | Retrieve all incomes |
| GET | /incomes/{id} | Retrieve an income by ID |

**Example response** `GET /incomes/1`

```json
{
  "id": 1,
  "title": "Salary",
  "value": 2900.0,
  "date": "05-25-2026 14:30:00",
  "status": "RECEIVED",
  "categoryIncome": {
    "id": 3,
    "title": "Job"
  }
}
```

**Status values:** `RECEIVED` (1), `PENDING` (2), `SCHEDULED` (3), `LATE` (4)

---

### Outgoings

| Method | Endpoint | Description |
|---|---|---|
| GET | /outgoings | Retrieve all outgoings |
| GET | /outgoings/{id} | Retrieve an outgoing by ID |

**Example response** `GET /outgoings/1`

```json
{
  "id": 1,
  "title": "Clothes",
  "value": 100.0,
  "date": "05-25-2026 10:00:00",
  "status": "PAID",
  "categoryOutgoing": {
    "id": 1,
    "title": "Shopping"
  }
}
```

**Status values:** `PAID` (1), `PENDING` (2), `SCHEDULED` (3), `LATE` (4)

---

### Categories

| Method | Endpoint | Description |
|---|---|---|
| GET | /categories | Retrieve all categories |
| GET | /categories/{id} | Retrieve a category by ID |

**Example response** `GET /categories/1`

```json
{
  "id": 1,
  "title": "Shopping"
}
```

## Error Handling

The API returns structured error responses:

**404 Not Found**

```json
{
  "timestamp": "2026-05-25T17:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/users/999"
}
```

**400 Validation Error**

```json
{
  "timestamp": "2026-05-25T17:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "fields": {
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters"
  },
  "path": "/auth/register"
}
```

## Running Tests

```sh
./mvnw verify
```

27 integration tests covering authentication, CRUD operations, validation, and error responses.

## Docker

```sh
docker build -t api-management .
docker run -p 8080:8080 api-management
```

## Database Schema

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│   tb_user    │       │   tb_receita     │       │ tb_category  │
├──────────────┤       │   (Income)       │       ├──────────────┤
│ id       PK  │───┐   ├──────────────────┤   ┌───│ id       PK  │
│ name         │   │   │ id           PK  │   │   │ title        │
│ email (uniq) │   └──>│ user_id      FK  │   │   └──────────────┘
│ password     │       │ category_id  FK  │<──┤
└──────────────┘       │ title            │   │   ┌──────────────────┐
                       │ amount           │   │   │  tb_outgoing     │
                       │ date             │   │   ├──────────────────┤
                       │ status           │   │   │ id           PK  │
                       └──────────────────┘   └──>│ category_id  FK  │
                                              ┌──>│ user_id      FK  │
                       ┌──────────────┐       │   │ title            │
                       │   tb_user    │───────┘   │ amount           │
                       └──────────────┘           │ date             │
                                                  │ status           │
                                                  └──────────────────┘
```

## Author

[Arthur Vicente](https://www.linkedin.com/in/arthurpvicente/)
