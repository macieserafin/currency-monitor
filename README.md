# Currency Monitor

Currency Monitor is a backend-oriented Java application designed to automatically fetch,
process, and store foreign exchange rates from the official National Bank of Poland (NBP) API.
The project focuses on clean architecture, modular design, and real-world backend concepts
such as scheduled data updates, persistence, caching, and REST API exposure.

---

## Project Overview

The application periodically retrieves exchange rate data from the NBP API (Table A),
validates it, stores historical values in a PostgreSQL database, and exposes the data
through a REST API.  
The system is built as a **multi-module Maven project**, with a clear separation of
business logic, infrastructure, and application runtime.

This project was created to practice:
- backend system design,
- modular Java applications,
- integration with external APIs,
- database-driven applications,
- clean separation of responsibilities.

---

## Architecture

The project follows a **ports and adapters (clean architecture) inspired approach**.

### Modules

- **currency-monitor-core**
  - Contains pure business logic and domain models.
  - Defines use cases and ports (interfaces).
  - Has no dependency on Spring or persistence frameworks.

- **currency-monitor-nbp-client**
  - Adapter responsible for communication with the NBP API.
  - Fetches and maps exchange rate data into domain objects.

- **currency-monitor-persistence**
  - Persistence layer using JPA/Hibernate.
  - Implements ports defined in the core module.
  - Contains database entities and Spring Data repositories.

- **currency-monitor-app**
  - Spring Boot application entry point.
  - Exposes REST API endpoints.
  - Handles scheduling, caching, logging, and exception handling.

---

## Main Features

- Automatic exchange rate fetching from the NBP API
- Scheduled updates (periodic data refresh)
- Duplicate detection per currency and date
- Historical exchange rate storage
- REST API for accessing currencies and exchange rates
- In-memory caching using Caffeine
- PostgreSQL database integration
- Docker-based database environment
- Request logging and API error handling
- Swagger/OpenAPI documentation

---

## Technologies Used

- Java
- Spring Boot
- Maven (multi-module project)
- PostgreSQL
- JPA / Hibernate
- Docker & Docker Compose
- REST API
- Caffeine Cache
- Swagger (SpringDoc OpenAPI)

---

## Database

The application uses PostgreSQL as the primary database.
A Docker Compose configuration is provided for local development.

The database stores:
- currencies,
- historical exchange rates,
- API fetch logs.

---

## Running the Project Locally

### 1. Start the database

Make sure Docker is running, then execute:

```bash
docker-compose up -d
