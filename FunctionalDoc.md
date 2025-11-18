# Functional Documentation: User Authentication and Authorization

## 1. Overview

This document outlines the functional requirements for implementing user authentication and authorization for the Demo application. The goal is to secure the existing `BookController` and `PlaygroundController` by introducing two distinct user roles and a new login mechanism.

## 2. User Roles and Permissions

Two user roles will be created, each with specific access rights:

| **Role**          | **Username**       | **Access**                                  |
| ----------------- | ------------------ | ------------------------------------------- |
| Playground User   | `playground-user`  | Access to all endpoints under `/playground` |
| Book User         | `book-user`        | Access to all endpoints under `/api/books`  |

## 3. New Components

### 3.1. `LoginController.java`

A new controller will be created to handle user authentication.

-   **Endpoint:** `/login`
-   **Method:** `POST`
-   **Request Body:**
    ```json
    {
      "username": "user",
      "password": "password"
    }
    ```

### 3.2. Spring Security Configuration

A new configuration class will be created to manage security settings. This class will:

-   Define the two in-memory users (`playground-user` and `book-user`) and their roles.
-   Configure URL-based authorization to restrict access to the controllers.
-   Implement custom success and failure handlers for the login process.

### 3.3. `CustomAuthenticationSuccessHandler`

This handler will be invoked upon successful authentication. It will:

-   Log the successful login attempt.
-   Redirect the user to a confirmation page or return a success message.

### 3.4. `CustomAuthenticationFailureHandler`

This handler will be invoked upon failed authentication. It will:

-   Track the number of failed login attempts for each user.
-   Return an "Unauthorized" error (HTTP 401) on each failed attempt.
-   After 3 consecutive failed attempts, it will trigger a graceful shutdown of the application.

## 4. Authentication Flow

1.  The user sends a `POST` request to the `/login` endpoint with their credentials.
2.  Spring Security attempts to authenticate the user.
3.  **On Success:**
    -   The `CustomAuthenticationSuccessHandler` is called.
    -   The user is granted a session and can now access the resources associated with their role.
4.  **On Failure:**
    -   The `CustomAuthenticationFailureHandler` is called.
    -   The failed attempt count is incremented.
    -   If the count is less than 3, a 401 error is returned.
    -   If the count reaches 3, the application will exit.

## 5. Logout Flow

1.  An authenticated user sends a `POST` request to the `/logout` endpoint.
2.  Spring Security intercepts the request and invalidates the user's session.
3.  The user is now logged out. Any subsequent requests to secured endpoints will require a new login.

## 6. Actuator Endpoints

The application exposes Spring Boot Actuator endpoints to help monitor and manage the application. These endpoints are publicly accessible under the `/actuator` path.

Key available endpoints include:
-   `/actuator/health`: Provides basic application health information.
-   `/actuator/info`: Displays application information.
-   `/actuator/metrics`: Shows various application metrics.

## 7. Non-Functional Requirements

-   **Security:** Passwords should be securely stored and handled (e.g., using bcrypt).
-   **Logging:** All authentication attempts (successful and failed) should be logged for auditing purposes.
