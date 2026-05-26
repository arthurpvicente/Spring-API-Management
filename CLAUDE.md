# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

### Backend (Spring Boot)
- **Run:** `./mvnw spring-boot:run` (starts on port 8081)
- **Build:** `./mvnw clean package -DskipTests`
- **Run all tests:** `./mvnw test`
- **Run single test class:** `./mvnw test -Dtest=IncomeControllerTest`
- **Run single test method:** `./mvnw test -Dtest=IncomeControllerTest#findAll_withToken_returnsOk`

### Frontend (React + Vite)
- **Dev server:** `cd frontend && npm run dev` (starts on port 5173)
- **Build:** `cd frontend && npm run build`
- **Type check:** `cd frontend && npx tsc --noEmit`
- **Lint:** `cd frontend && npm run lint`

### Docker
- **Build:** `docker build -t apimanagement .` (backend only; no frontend Docker setup)

## Architecture

### Backend
Spring Boot 3.3 app (Java 22) with JWT authentication and H2 in-memory database.

**Active profile is `teste`** (`spring.profiles.active=teste` in application.properties), which uses H2 with `ddl-auto=create` — the database is recreated on every restart. `TestConfig` seeds sample data (users, categories, incomes, outgoings) on startup.

**Authentication flow:** `AuthController` handles `/auth/login` and `/auth/register`. `JwtAuthenticationFilter` extracts the Bearer token from the Authorization header and sets the Spring Security context. All endpoints except `/auth/**`, `/swagger-ui/**`, `/h2-console/**`, and `/webhook/**` require authentication.

**Test seed credentials:** `arthur@example.com` / `123456`

**Entity relationships:**
- `User` → has many `Income` and `Outgoing` (via `user_id` FK)
- `Category` → shared by both `Income` (via `category_income_id`) and `Outgoing` (via `category_outgoing_id`)
- `Income` and `Outgoing` have `@JsonIgnore` on the `user` field but expose `userId` via `@JsonProperty`

**Status enums are stored as integers:** `IncomeStatus` (RECEIVED=1, PENDING=2, SCHEDULED=3, LATE=4) and `OutgoingStatus` (PAID=1, PENDING=2, SCHEDULED=3, LATE=4). The entity getter returns the enum; the DTO expects the integer code.

**Request DTOs are Java records** in the `dto` package (e.g., `IncomeRequest`, `OutgoingRequest`) with Bean Validation annotations.

### Frontend
React 19 + TypeScript + Vite + Tailwind CSS v4 SPA.

**Dark mode** is enabled by default via `class="dark"` on `<html>` in `index.html`, using a `@custom-variant` in `index.css`.

**API client** (`src/api/client.ts`): thin wrapper around `fetch` with Bearer token from `localStorage`. Base URL defaults to `http://localhost:8081` (override with `VITE_API_URL` env var). Auto-redirects to `/login` on 401.

**Auth state** is managed via React Context (`src/context/AuthContext.tsx`), storing the JWT in `localStorage`.

**Routing:** React Router v7 with `ProtectedRoute` wrapper. Public routes: `/login`, `/register`. Protected routes: `/` (Dashboard), `/users`, `/incomes`, `/outgoings`.

## API Endpoints

| Resource    | GET (all) | GET (by id) | POST   | PUT (by id) | DELETE (by id) |
|-------------|-----------|-------------|--------|-------------|----------------|
| /auth       | —         | —           | login, register | —    | —              |
| /users      | yes       | yes         | yes    | yes         | yes            |
| /categories | yes       | yes         | —      | —           | —              |
| /incomes    | yes       | yes         | yes    | yes         | yes            |
| /outgoings  | yes       | yes         | yes    | yes         | yes            |

Swagger UI available at `/swagger-ui.html` when the backend is running.
