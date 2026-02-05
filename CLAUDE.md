# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.2.0 Todo CRUD application with a BFF (Backend for Frontend) architecture, designed as a learning resource for frontend developers to understand backend development. The project includes a REST API for managing todos, Swagger/OpenAPI documentation, MySQL database integration, and an embedded Vue 3 frontend.

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8 with JPA/Hibernate
- **Build Tool**: Maven
- **Frontend**: Vue 3 (embedded in static resources)
- **API Documentation**: OpenAPI 3 (SpringDoc)
- **Additional**: Lombok, Spring WebFlux, Spring Validation

## Common Commands

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Package the application
mvn clean package

# Run packaged JAR
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Application Access

- **Frontend**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui
- **H2 Console**: http://localhost:8080/h2-console (username: sa, password: empty)

## Architecture

The project follows a layered architecture with clear separation of concerns:

1. **Model Layer** (`model/`): JPA entities with validation annotations
2. **Repository Layer** (`repository/`): JPA repositories extending `JpaRepository` with custom query methods
3. **Service Layer** (`service/`): Business logic with `@Transactional` for data modification
4. **Controller Layer** (`controller/`): REST API endpoints
5. **Configuration Layer** (`config/`): Swagger/OpenAPI and WebClient configuration
6. **Client Layer** (`client/`): External API integration using reactive WebClient
7. **Exception Layer** (`exception/`): Global exception handling and custom exceptions

## Key Patterns

### Dependency Injection
- Constructor-based injection with Lombok's `@RequiredArgsConstructor` is the standard pattern

### Validation
- Use `@Valid` on controller method parameters
- Validation annotations on model fields (`@NotBlank`, `@Size`, etc.)
- Handled by `GlobalExceptionHandler` which returns appropriate HTTP status codes

### Custom JPA Queries
The `TodoRepo` includes both JPQL and native SQL queries:
- Use `@Query` for JPQL queries
- Use `@Query(..., nativeQuery = true)` for native SQL
- Use `@Modifying` for update/delete operations (must be in `@Transactional` context)
- See `README_CUSTOM_QUERIES.md` for detailed query method documentation

### External API Integration
- Uses `WebClient` for reactive, non-blocking HTTP calls
- Configuration in `WebClientConfig`
- External API base URL configurable via properties

### Exception Handling
- Global exception handler in `GlobalExceptionHandler` handles:
  - `MethodArgumentNotValidException` - validation errors
  - `TodoNotFoundException` - custom 404 responses

## Database Configuration

Located in `src/main/resources/application.properties`:
- Database: `todo_db` (auto-created if doesn't exist)
- JPA DDL auto: `update` (schema generation enabled)
- Connection: MySQL on localhost:3306 with user `root`

## API Endpoints

### Todo Management
- `GET /api/todo` - Get all todos
- `POST /api/todo` - Create new todo (requires validation)
- `PUT /api/todo/{id}` - Update todo
- `PATCH /api/todo/{id}/toggle` - Toggle completion status
- `DELETE /api/todo/{id}` - Delete todo

### External API Integration
- `GET /api/external/user/{id}` - Get user by ID
- `GET /api/external/users` - Get all users
- `GET /api/external/user/{id}/posts` - Get user's posts
- `POST /api/external/user` - Create user

## Project Structure

```
src/main/java/com/example/demo/
├── DemoApplication.java          # Main application entry
├── model/Todo.java               # JPA entity with validation
├── repository/TodoRepo.java      # JPA repository with custom queries
├── service/TodoService.java      # Business logic layer
├── controller/
│   ├── TodoController.java       # Todo CRUD endpoints
│   └── ExternalApiController.java # External API integration
├── config/
│   ├── SwaggerConfig.java        # OpenAPI configuration
│   └── WebClientConfig.java       # Reactive web client
├── client/ExternalApiService.java # External API service
└── exception/
    ├── GlobalExceptionHandler.java
    └── TodoNotFoundException.java

src/main/resources/
├── application.properties         # App configuration
└── static/index.html            # Vue 3 frontend
```

## Frontend Integration

The frontend is a single Vue 3 application embedded at `src/main/resources/static/index.html`. It demonstrates:
- Todo CRUD operations via Axios
- Reactive UI with real-time updates
- Inline editing and status toggling

When adding or modifying API endpoints, ensure the corresponding frontend calls are updated if needed.
