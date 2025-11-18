# Spring Boot Security Demo

This project is a demonstration of implementing user authentication and authorization in a Spring Boot application using Spring Security. It includes features like role-based access control, custom login/logout handlers, and actuator endpoints for monitoring.

## Technology Stack

This project is built using the following technologies and libraries:

- **[Spring Boot](https://spring.io/projects/spring-boot):** Core framework for building the application.
- **[Spring Security](https://spring.io/projects/spring-security):** For handling authentication and authorization.
- **[Spring Data JPA](https://spring.io/projects/spring-data-jpa):** For data persistence and repository support.
- **[H2 Database](https://www.h2database.com):** An in-memory database used for development and testing.
- **[Maven](https://maven.apache.org/):** Dependency management and build tool.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java 21 or later
- Apache Maven 3.6.3 or later

## How to Build and Run

1.  **Clone the repository:**
    ```bash
    git clone &lt;repository-url&gt;
    cd &lt;repository-directory&gt;
    ```

2.  **Build the project:**
    Use Maven to build the project and create an executable JAR file.
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    Once the build is complete, you can run the application from the `target` directory.
    ```bash
    java -jar target/demo-0.0.1-SNAPSHOT.jar
    ```
    The application will start on `http://localhost:8080`.

## Configuration Profiles

This application uses Spring Profiles to manage different configuration settings for various environments. The available profiles are defined in `src/main/resources/application.yml`.

- **`dev` (default):** The default profile for local development.
- **`uat`:** A profile for user acceptance testing, which might use different database credentials or server settings.

### How to Change the Active Profile

By default, the `dev` profile is active. You can change the active profile by setting the `spring.profiles.active` property when running the application.

For example, to run the application with the `uat` profile, use the following command:
```bash
java -jar -Dspring.profiles.active=uat target/demo-0.0.1-SNAPSHOT.jar
```

Alternatively, you can use the `--spring.profiles.active` command-line argument:
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=uat
```

## API Usage Guide

You can use a tool like Postman to interact with the API. A Postman collection (`DemoApplicationAPI.postman_collection.json`) is included in the root of the project for your convenience.

### User Credentials

The application is configured with two in-memory users:

| Role              | Username          | Password   | Access Rights                      |
| ----------------- | ----------------- | ---------- | ---------------------------------- |
| Book User         | `book-user`       | `password` | Access to `/api/books/**`          |
| Playground User   | `playground-user` | `password` | Access to `/playground/**`         |

### 1. Login

To authenticate, send a `POST` request to the `/login` endpoint. This will create a session cookie (`JSESSIONID`) that must be included in subsequent requests to secured endpoints.

-   **Endpoint:** `POST /login`
-   **Request Body:**
    ```json
    {
      "username": "book-user",
      "password": "password"
    }
    ```
-   **Success Response:**
    -   **Status:** `200 OK`
    -   **Body:** `Login successful`
    -   **Headers:** Includes a `Set-Cookie` header for the session.

### 2. Accessing Protected Endpoints

Once logged in, you can access endpoints based on your role. Remember to include the session cookie with your requests.

#### Book API (for `book-user`)

-   **Endpoint:** `GET /api/books`
-   **Description:** Retrieves a list of all books.
-   **Authentication:** Requires `book-user` role.

#### Playground API (for `playground-user`)

-   **Endpoint:** `GET /playground/hello`
-   **Description:** Returns a simple greeting.
-   **Authentication:** Requires `playground-user` role.

### 3. Logout

To terminate your session, send a `POST` request to the `/logout` endpoint.

-   **Endpoint:** `POST /logout`
-   **Description:** Invalidates the current session.
-   **Success Response:**
    -   **Status:** `200 OK`

## Actuator Endpoints

The application exposes several actuator endpoints for monitoring. These are publicly accessible under the `/actuator` path.

-   `GET /actuator/health`: Shows application health.
-   `GET /actuator/info`: Displays application information.
-   `GET /actuator/metrics`: Provides detailed application metrics.
-   `GET /actuator/mappings`: Shows all `@RequestMapping` paths.

## Running Tests

To run the unit and integration tests, use the following Maven command:

```bash
mvn test
```
