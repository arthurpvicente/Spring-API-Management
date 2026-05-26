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
| React | 19 | Frontend UI framework |
| Vite | 6.x | Frontend build tool |
| Tailwind CSS | 4 | Utility-first CSS |

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
- Node.js 18+ and npm (for the frontend)

## Getting Started

1. Clone the repository:

```sh
git clone https://github.com/arthurpvicente/Spring-API-Management.git
cd Spring-API-Management
```

2. Set up environment variables:

```sh
cp .env.example .env
```

The `teste` profile (active by default) includes a built-in JWT secret, so the app works without editing `.env` for local development. For production, replace `JWT_SECRET` with your own base64-encoded secret. The Twilio variables are optional (see [WhatsApp Bot](#whatsapp-bot-optional) below).

3. Build the project:

```sh
./mvnw clean install
```

4. Run the backend:

```sh
./mvnw spring-boot:run
```

The API starts at `http://localhost:8081` with preloaded test data.

**Test credentials** (preloaded by the `teste` profile):

| Email | Password |
|---|---|
| arthur@example.com | 123456 |
| matheus@example.com | 123456 |

5. Run the frontend (in a separate terminal):

```sh
cd frontend
npm install
npm run dev
```

The dashboard opens at `http://localhost:5173`.

> The frontend connects to `http://localhost:8081` by default. To override, create `frontend/.env` with `VITE_API_URL=http://your-backend-host:port`. CORS is pre-configured to allow requests from `http://localhost:5173`.

6. Access the interactive API docs:

```
http://localhost:8081/swagger-ui/index.html
```

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. All endpoints except `/auth/**` and `/swagger-ui/**` require a valid token.

### Register

```sh
curl -X POST http://localhost:8081/auth/register \
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
curl -X POST http://localhost:8081/auth/login \
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
curl http://localhost:8081/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIs..."
```

**Test credentials** are preloaded by `TestConfig` when using the `teste` profile. Use `arthur@example.com` / `123456` to log in.

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

### Incomes (full CRUD)

| Method | Endpoint | Description |
|---|---|---|
| GET | /incomes | Retrieve all incomes for the authenticated user |
| GET | /incomes/{id} | Retrieve an income by ID |
| POST | /incomes | Create a new income |
| PUT | /incomes/{id} | Update an income by ID |
| DELETE | /incomes/{id} | Delete an income by ID |

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

**Create an income** `POST /incomes`

```json
{
  "title": "Freelance work",
  "value": 1500.0,
  "status": 1,
  "userId": 1,
  "categoryId": 3
}
```

**Status values:** `RECEIVED` (1), `PENDING` (2), `SCHEDULED` (3), `LATE` (4). The `date` is set automatically by the server.

---

### Outgoings (full CRUD)

| Method | Endpoint | Description |
|---|---|---|
| GET | /outgoings | Retrieve all outgoings for the authenticated user |
| GET | /outgoings/{id} | Retrieve an outgoing by ID |
| POST | /outgoings | Create a new outgoing |
| PUT | /outgoings/{id} | Update an outgoing by ID |
| DELETE | /outgoings/{id} | Delete an outgoing by ID |

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

**Create an outgoing** `POST /outgoings`

```json
{
  "title": "Electric bill",
  "value": 85.0,
  "status": 2,
  "userId": 1,
  "categoryId": 4
}
```

**Status values:** `PAID` (1), `PENDING` (2), `SCHEDULED` (3), `LATE` (4). The `date` is set automatically by the server.

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

## H2 Database Console

When running with the `teste` profile (the default), the H2 in-memory database console is available at:

```
http://localhost:8081/h2-console
```

| Setting | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testedb` |
| Username | `sa` |
| Password | *(leave blank)* |

## WhatsApp Bot (Optional)

The app includes a Twilio-powered WhatsApp bot that lets users record incomes and expenses via chat. The webhook endpoint is at `POST /webhook/whatsapp` (no authentication required).

**Bot commands:**

| Command | Description |
|---|---|
| `groceries 50` | Record a R$50 expense |
| `+salary 3000` | Record a R$3000 income |
| `/register email` | Link a WhatsApp number to an account |
| `/verify code` | Confirm the verification code |
| `/help` | Show available commands |

To enable the WhatsApp bot, configure the Twilio environment variables in your `.env` file:

```
TWILIO_ACCOUNT_SID=your-twilio-account-sid
TWILIO_AUTH_TOKEN=your-twilio-auth-token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
TWILIO_SIGNATURE_VALIDATION=false
```

These variables are optional for local development — the app starts and works normally without them.

## Docker

```sh
docker build -t api-management .
docker run -p 8081:8081 api-management
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
