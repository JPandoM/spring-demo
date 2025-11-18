# Technical Documentation: User Authentication and Authorization

## 1. Overview

This document provides the technical details for implementing the user authentication and authorization features as described in the `FunctionalDoc.md`.

## 2. Project Dependencies

The following dependency will be added to the `pom.xml` to include Spring Security:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## 3. Component Implementation

### 3.1. `LoginController.java`

A new REST controller will be created at `src/main/java/com/example/demo/controller/LoginController.java`.

-   **Endpoint:** `/login`
-   **Method:** `POST`
-   **Request DTO:** A new `LoginRequest.java` record will be created in `src/main/java/com/example/demo/dto/` to model the request body.

```java
package com.example.demo.dto;

public record LoginRequest(String username, String password) {}
```

The controller will have a single method that will be secured by Spring Security, and the actual authentication will be handled by the framework.

### 3.2. `SecurityConfig.java`

A new configuration class will be created at `src/main/java/com/example/demo/security/SecurityConfig.java`. This class will be annotated with `@Configuration` and `@EnableWebSecurity`.

-   **User Details Service:** An `InMemoryUserDetailsManager` will be used to define the two users (`playground-user` and `book-user`) and their corresponding roles (`PLAYGROUND` and `BOOK`). Passwords will be encoded using `BCryptPasswordEncoder`.

-   **Security Filter Chain:** A `SecurityFilterChain` bean will be configured to:
    -   Disable CSRF for the `/login` and `/h2-console` endpoints.
    -   Authorize requests to `/playground/**` for users with the `PLAYGROUND` role.
    -   Authorize requests to `/api/books/**` for users with the `BOOK` role.
    -   Permit all requests to `/actuator/**` to allow public access to monitoring endpoints.
    -   Permit all other requests (including `/login`).
    -   Configure form-based login with the `/login` endpoint and custom success and failure handlers.
    -   Configure logout handling for the `/logout` endpoint.

### 3.3. `CustomAuthenticationSuccessHandler.java`

This class, located at `src/main/java/com/example/demo/security/CustomAuthenticationSuccessHandler.java`, will implement the `AuthenticationSuccessHandler` interface.

-   **`onAuthenticationSuccess` method:**
    -   Log the successful authentication event.
    -   Set the HTTP response status to `200 OK`.
    -   Write a success message to the response body.

### 3.4. `CustomAuthenticationFailureHandler.java`

This class, located at `src/main/java/com/example/demo/security/CustomAuthenticationFailureHandler.java`, will implement the `AuthenticationFailureHandler` interface.

-   **Failure Tracking:** A `Map<String, Integer>` will be used to store the number of failed login attempts for each username.

-   **`onAuthenticationFailure` method:**
    -   Log the failed authentication attempt.
    -   Increment the failure count for the user.
    -   If the failure count reaches 3:
        -   Log a message indicating that the application will shut down.
        -   Exit the application using `System.exit(1)`.
    -   If the failure count is less than 3:
        -   Set the HTTP response status to `401 Unauthorized`.
        -   Write an error message to the response body.

### 3.5. Actuator Configuration

To expose management endpoints, the `spring-boot-starter-actuator` dependency is included. The following configuration in `application.yml` is used to expose all actuator endpoints over HTTP:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

This makes endpoints like `/actuator/health` and `/actuator/info` publicly available.

## 4. Testing Strategy

### 4.1. Unit Tests

-   **`CustomAuthenticationFailureHandlerTest.java`:**
    -   Test that the failure count is incremented on each failed attempt.
    -   Test that the application exits after 3 failed attempts.

### 4.2. Integration Tests

-   **`LoginControllerIT.java`:**
    -   Test successful login for both `playground-user` and `book-user`.
    -   Test that authenticated users can access their respective endpoints.
    -   Test that unauthorized users are denied access to secured endpoints.
    -   Test the authentication failure flow, including the application shutdown after 3 failed attempts.
-   **Actuator Endpoints IT:**
    -   Verify that actuator endpoints like `/actuator/health` and `/actuator/info` are publicly accessible and return a `200 OK` status.
