# API Management System

This project is an API Management System developed using Spring Boot. It manages users, categories, incomes, and outgoings, providing RESTful endpoints for CRUD operations on these entities.

## Features

- User Management: Create, update, delete, and retrieve users.
- Income Management: Record and track income entries.
- Outgoing Management: Record and track outgoing entries.
- Category Management: Manage categories associated with income and outgoing records.
- Data Initialization: Preloads the database with test data in the development profile.

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database (for testing and development)
- Maven
- Postman (for API testing)

## Installation

1. Clone the reppsitory:

```sh
git clone https://github.com/your-username/api-management-system.git
```
2. Build the project:

```sh
mvn clean isntall
```
3. Run the application:

```sh
mvn spring-boot:run
```
4. Access the API endpoints using Postman.

```sh
http://localhost:8080
```

# API Endpoints

### **You can test the API endpoints using Postman below:**

| Method | Endpoint         | Description                |
|--------|------------------|----------------------------|
| GET    | /users           | Retrieve all users         |
| GET    | /users/{id}      | Retrieve a user by ID      |
| POST   | /users           | Create a new user          |
| PUT    | /users/{id}      | Update a user by ID        |
| DELETE | /users/{id}      | Delete a user by ID        |
| GET    | /incomes         | Retrieve all incomes       |
| GET    | /incomes/{id}    | Retrieve an income by ID   |
| GET    | /outgoings       | Retrieve all outgoings     |
| GET    | /outgoings/{id}  | Retrieve an outgoing by ID |
| GET    | /categories      | Retrieve all categories    |
| GET    | /categories/{id} | Retrieve a category by ID  |

