# Gym Finder & Membership Management Backend

This is the backend for the Gym Finder and Membership Management system, built using **Spring Boot** and **MySQL**. It provides RESTful APIs for user authentication, gym operations, and membership management.

## Features
- User Registration & Login (JWT Authentication)
- Gym Management (CRUD Operations)
- Membership Handling
- Secure API with Role-based Access Control

## Tech Stack
- **Spring Boot** (Java)
- **Spring Security** (JWT-based Authentication)
- **MySQL** (Database)
- **Spring Data JPA**

## Prerequisites
Ensure you have the following installed:
- Java 17+
- Maven
- MySQL Server

## Setup Instructions
### 1. Clone the repository
```sh
git clone https://github.com/your-username/gymfinder-backend.git
cd gymfinder-backend
```

### 2. Set up the database
Create a MySQL database manually:
```sql
CREATE DATABASE gymfinder;
```
Update `application.properties` with your MySQL credentials:
```
spring.datasource.url=jdbc:mysql://localhost:3306/gymfinder
spring.datasource.username=root
spring.datasource.password=root
jwt.secret=your-secret-key
```

### 3. Build & Run the Application
```sh
mvn clean install
mvn spring-boot:run
```

## API Endpoints
### Authentication
| Method | Endpoint            | Description          |
|--------|--------------------|----------------------|
| POST   | `/auth/register`   | Register a new user |
| POST   | `/auth/login`      | User login (JWT)    |

### Gym Operations
| Method | Endpoint          | Description                      |
|--------|------------------|----------------------------------|
| GET    | `/gyms`          | Get all gyms                    |
| POST   | `/gyms`          | Add a new gym (Admin only)      |
| PUT    | `/gyms/{id}`     | Update gym details (Admin only) |
| DELETE | `/gyms/{id}`     | Delete a gym (Admin only)       |

### Membership Management
| Method | Endpoint                | Description                         |
|--------|------------------------|-------------------------------------|
| POST   | `/memberships`          | Add a new membership               |
| GET    | `/memberships/{userId}` | Get user memberships               |
| DELETE | `/memberships/{id}`     | Cancel a membership (User/Admin)   |

## License
This project is licensed under the MIT License.

## Contributors
- [Your Name](https://github.com/your-username)

