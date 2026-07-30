# Tutoring Management System

A comprehensive, enterprise-grade platform designed to seamlessly connect teachers, students, and administrators.
Built with a robust **Hexagonal Architecture** (Ports and Adapters), this system streamlines educational scheduling, grading, enrollments,
and role-based access control.

## Key Features
* **Role-Based Access Control (RBAC):** Distinct dashboards and permissions for Admins, Teachers, and Students, securely handled via JWT authentication.
* **Smart Scheduling:** Global timetable management featuring built-in conflict resolution to prevent double-booking and overlaps.
* **Student Management:** Comprehensive tracking of enrollments, test results, and overall student performance via a centralized dashboard.
* **Teacher Tools:** Dedicated functionalities for lesson activity logging, absence declarations, and direct student grading.
* **Enterprise Architecture:** Strict multi-module Maven setup enforcing Hexagonal Architecture boundaries.

## Tech Stack
* **Backend:** Java 17, Spring Boot 3.2.5, Spring Security, JWT (JSON Web Tokens)
* **Frontend:** HTML5, CSS3, Vanilla JavaScript, Thymeleaf
* **Database:** MySQL, Spring Data JPA, Hibernate
* **Architecture & Testing:** jMolecules, ArchUnit, JUnit 5
* **API Documentation:** Swagger / OpenAPI 3

## Project Structure (Multi-Module)
The application strictly follows Hexagonal Architecture principles, divided into independent Maven modules to enforce a clear 
separation of concerns:

* **`application`:** The core orchestration layer containing inbound and outbound port interfaces, Use Case implementations, and 
Data Transfer Objects (DTOs).
* **`domain`:** The foundational shared core encompassing domain enumerations, custom global exceptions, and core configuration 
properties (e.g., JWT properties).
* **`repository`:** The outbound adapter layer dedicated to data persistence, integrating JPA entities, database repositories, 
entity mappers, and persistence implementations.
* **`rest`:** The inbound adapter layer managing external HTTP communications, comprising REST controllers, security configurations 
(JWT filters), API documentation, and resource payload mappers.
* **`webapplication`:** The central bootstrapping module serving as the application entry point, assembling frontend assets (Thymeleaf, 
CSS, JS) and executing architectural compliance tests (ArchUnit).

## System Prerequisites
The following dependencies are required to successfully compile and execute the application in a local development environment:

* **Java Development Kit (JDK) 17** (or higher)
* **MySQL Server** actively running locally.
* *(Note: A global Maven installation is not strictly required, as the project includes a Maven Wrapper).*

## Database Setup
The application is configured for automated database initialization and schema validation upon startup.

* A local MySQL server instance must be active. By default, the application connects to `localhost` on port `3306`.
* It uses the default MySQL credentials (`username: root`, `password: [empty]`).
* The database schema named `tutoring_management` is automatically generated during the initial execution (`createDatabaseIfNotExist=true`).
* Upon the first deployment, the system executes the `data.sql` script to populate the required foundational data.

### Environment Variables
You can override the default configurations without modifying the code by providing the following Environment Variables:

| Variable      | Default Value  | Description                                                           |
|---------------|----------------|-----------------------------------------------------------------------|
| `DB_HOST`     | `localhost`    | The hostname of the MySQL server.                                     |
| `DB_PORT`     | `3306`         | The port on which MySQL is listening (e.g., set to `3307` if needed). |
| `DB_USER`     | `root`         | The database username.                                                |
| `DB_PASSWORD` | *(empty)*      | The database password.                                                |
| `JWT_SECRET`  | *(dev secret)* | The secret key used for JWT signing.                                  |
| `SERVER_PORT` | `8080`         | The port on which the Spring Boot application runs.                   |


## Default Administrator Account
The system is designed to be fully operational out-of-the-box. Upon the first application startup, the database schema is initialized and a
default System Administrator account is automatically provisioned via the **data.sql** script.

You can log in and explore the Admin functionalities using the following credentials:
- **Username:** `admin`
- **Password:** `admin123`

> **Security Note:** While these credentials are provided in plain text for local development and demonstration purposes, the system strictly enforces security best 
practices. The default password is automatically hashed using BCrypt prior to database insertion via the `data.sql` script, demonstrating production-ready authentication 
flows.

## Installation & Running

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/StathisVog/tutoring-management-system.git](https://github.com/StathisVog/tutoring-management-system.git)
   cd tutoring-management-system
   ```

2. **Build the Project (Using Maven Wrapper):**
   ```bash
   # For Linux/macOS:
   ./mvnw clean install
   
   # For Windows:
   mvnw.cmd clean install
   ```

3. **Run the Application:**  
   Because this is a multi-module project, you can run the main web application directly from the root directory using the `-pl` (project list) flag:
   ```bash
   # For Linux/macOS:
   ./mvnw spring-boot:run -pl webapplication
   
   # For Windows:
   mvnw.cmd spring-boot:run -pl webapplication
   ```

4. **Access the Application:**
   - **Web UI:** http://localhost:8080
   - **Swagger API Documentation:** http://localhost:8080/docs