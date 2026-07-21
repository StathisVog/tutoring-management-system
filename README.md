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
* **Apache Maven 3.6** (or higher)
* **MySQL Server** actively running and listening on port `3307`

## Database Setup
The application is configured for automated database initialization and schema validation upon startup.

* A local MySQL server instance must be active and listening on port `3307`.
* By default, the application establishes a connection using the `root` user with no password. Database credentials can be securely modified 
within the `webapplication/src/main/resources/application.yaml` configuration file.
* The database schema named `tutoring_management` is automatically generated during the initial execution (`createDatabaseIfNotExist=true`).
* Upon the first deployment, the system executes the `data.sql` script to populate the required foundational data.

## Default Administrator Account
The system is designed to be fully operational out-of-the-box. Upon the first application startup, the database schema is initialized and a
default System Administrator account is automatically provisioned via the **data.sql** script.
- Username: admin
- Password: (Defined and hashed by the developer within **data.sql** prior to deployment)

## Installation & Running
1. **Build the Project:**  
   ```bash
   mvn clean install

2. **Run the Application:**
   ```bash
   cd webapplication
   mvn spring-boot:run
   
3. **Access the Application:**
   - Web UI: http://localhost:8080
   - Swagger API Documentation: http://localhost:8080/docs