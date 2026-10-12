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
- Local Docker Compose stack (PostgreSQL, backend and frontend)
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
- React 19, TypeScript and Vite (frontend)

## Frontend

The responsive Polish-language dashboard uses every application REST endpoint:

| Endpoint | Interface |
| --- | --- |
| `GET /api/currencies` | Searchable, sortable currency catalogue and currency selectors |
| `GET /api/rates/{code}/latest` | Quote cards, catalogue rates and currency converter |
| `GET /api/rates/{code}` | Interactive historical chart, dated observations and CSV export |

The interface includes an overview, historical rates, a local watchlist and an
API explorer that displays actual server responses. The converter supports PLN
and cross-currency conversions using available mean rates. It shows publication
dates and flags conversions using rates from different dates.

The dashboard checks the API every 60 seconds while the browser tab is visible;
the refresh button requests another read, not an upstream NBP import. API reads
and NBP publication dates are displayed separately. History windows end at the
latest saved observation. A single observation is shown as a point, without
invented trends. Favourites are stored only in browser local storage.

All production values come from the backend. Synthetic fixtures exist only in
browser tests. Empty databases, unavailable rates, network failures and invalid
converter input have explicit UI states.

Frontend checks (from `currency-monitor-frontend`):

```bash
npm ci
npm run build
npm run lint
npx playwright install chromium
npm test
```

Tests exercise desktop and mobile layouts, API integration with controlled
fixtures, searching, sorting, watchlist persistence, conversion, history ranges,
CSV precision and failure recovery. On a machine with Google Chrome installed,
`PLAYWRIGHT_CHANNEL=chrome npm test` can use that browser instead (on PowerShell,
set `$env:PLAYWRIGHT_CHANNEL="chrome"` before running `npm test`).

To show frontend changes in Docker, run `docker compose up --build` from the
repository root. The default frontend address is http://localhost:3000.

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

### Full local stack

Run these commands from the repository root with Docker running:

```bash
cp .env.example .env
docker compose up --build
```

Default addresses:

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/currencies
- Swagger UI: http://localhost:8080/swagger-ui/index.html

Compose reads the root `.env` automatically. Set `BACKEND_PORT` and
`FRONTEND_PORT` there to change the host ports (for example, when Helpdesk is
already running). PostgreSQL is internal to the Docker network; its data is
stored in the `postgres_data` volume. The backend starts after the database
health check passes. The frontend forwards `/api/` requests to the backend
through Nginx, so changing host ports does not require rebuilding API URLs.
The API may take a moment to start after the containers are created.

View logs and stop the stack:

```bash
docker compose logs -f backend
docker compose down
```

`docker compose down -v` also deletes this stack's database volume. Changing
PostgreSQL credentials in `.env` does not change an already initialized database.

### Local Java and Vite with PostgreSQL in Docker

Requirements: Java 17, Node.js 22.12+ (or a supported newer LTS) and Docker Compose.
Copy `.env.example` to `.env` if you have not done so yet. Start only PostgreSQL
from the repository root:

```bash
docker compose -f docker-compose.db.dev.yml up -d --wait
```

This exposes PostgreSQL at `localhost:5433` by default (`POSTGRES_PORT` in `.env`)
and uses a separate `postgres_dev_data` volume. Use one Compose variant at a time:
stop the full stack before starting the database-only variant, or vice versa.

In a terminal at the repository root, export the local settings, build the
multi-module backend and run its executable JAR:

```bash
set -a
. ./.env
set +a
cd currency-monitor-backend
./mvnw clean package
java -jar currency-monitor-app/target/currency-monitor-app-0.0.1-SNAPSHOT.jar
```

Spring Boot receives the exported settings; unlike Compose, a local Java process
does not load `.env` automatically. Standard `SPRING_DATASOURCE_*` environment
variables can override the database connection if needed.

In another terminal, start the frontend:

```bash
cd currency-monitor-frontend
npm ci
npm run dev
```

Vite serves the frontend at http://localhost:5173 and reads `BACKEND_PORT` from
the root `.env` for its `/api` proxy. `FRONTEND_PORT` controls only the Docker
frontend. Restart Vite after changing `.env`.

Stop the development database from the repository root:

```bash
docker compose -f docker-compose.db.dev.yml down
```

The former `currency-monitor-backend/docker-compose.yml` has been replaced by
the root Compose files. Existing volumes from the old configuration are left
untouched; the new configuration starts with separate databases.

### Configuration files

- `.env.example`: local database credentials, ports and backend settings; copy to
  `.env`, which is excluded from Git.
- `docker-compose.yml`: full local stack, following the Helpdesk layout.
- `docker-compose.db.dev.yml`: database for running Java and Vite locally.
- `currency-monitor-backend/Dockerfile`: Maven build and Java 17 runtime.
- `currency-monitor-frontend/Dockerfile`: Vite build and Nginx runtime.
- `currency-monitor-frontend/nginx.conf`: SPA routing and API proxy.

These files cover local development only. No production deployment configuration
is included.
