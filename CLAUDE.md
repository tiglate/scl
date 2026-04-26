# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SCL is a Spring Boot application for FX trade settlement management, featuring LDAP authentication, Hibernate Envers auditing, and a server-rendered UI with Thymeleaf + HTMX.

## Commands

### Build & Run

```bash
./mvnw clean package          # Build the application JAR
./mvnw spring-boot:run        # Run locally (requires Docker services)
./mvnw test                   # Run all unit tests
./mvnw test -Dtest=ClassName  # Run a single test class
./mvnw verify -Pcoverage      # Run tests with JaCoCo coverage report
```

### Infrastructure (Docker)

```bash
make up      # Start SQL Server, Mailpit, and LDAP containers
make down    # Stop services (preserves data volumes)
make reset   # Destroy all services and volumes
make logs    # Follow Docker Compose logs
make shell-db  # Open an interactive SQL Server shell
```

### JavaScript Linting

```bash
npm install  # Install ESLint (one-time setup)
npm run lint # Run ESLint on static JS files
```

### CI / Code Quality

The GitHub Actions workflow runs on push to `main` and on PRs:
```bash
./mvnw verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
  -Dsonar.projectKey=tiglate_scl -Pcoverage
```
Requires `SONAR_TOKEN` secret. SonarCloud project: `tiglate_scl`.

## Environment Setup

Copy `.env.example` to `.env` and fill in values before starting the app. Key variable groups:
- **Database**: `JDBC_DATABASE_HOST/USERNAME/PASSWORD/DB`
- **LDAP**: `SPRING_LDAP_URLS/BASE/USERNAME/PASSWORD`
- **SMTP**: `SMTP_HOST/PORT/USERNAME/PASSWORD/MAIL_FROM`
- **App**: `BASE_HOST`, `REMEMBER_ME_KEY`

The devcontainer (`.devcontainer/devcontainer.json`) targets JDK 17 and pre-installs Node LTS. It mounts and starts all `docker-compose.yml` services automatically.

## Architecture

### Layer Structure

```
Controller (MVC + 1 REST)  →  Service (interface + impl)  →  Repository (Spring Data JPA)  →  MS SQL Server
                                                                                                 ↓
                                                                                      Flyway migrations (V001–V007)
```

- **12 MVC controllers** in `controller/` delegate all logic to services.
- **~20 services** in `service/` follow the `XxxService` / `XxxServiceImpl` naming pattern; all business logic lives here.
- **8+ repositories** in `repos/` are plain Spring Data JPA interfaces.
- **16 domain entities** in `domain/` are annotated with `@Audited` (Hibernate Envers), which automatically tracks every change to a revision table.
- **DTOs/models** in `model/` are used for form binding and API input; entities are never exposed directly to controllers.

### Frontend

- **Thymeleaf** for server-side templating. Templates live in `src/main/resources/templates/` organized by feature.
- **HTMX** for partial-page updates — controllers return HTML fragments for HTMX requests rather than JSON.
- **Bootstrap 5.3.3** (via WebJars), DataTables, Chart.js, and Flatpickr are the main client-side libraries.
- Page-specific JS lives in `static/js/pages/`; shared modules in `static/js/modules/`.

### Authentication

`CustomAuthenticationProvider` implements a hybrid flow: it first tries LDAP via Spring Security LDAP; on failure it falls back to local database authentication. Both paths produce a `UserDetails` loaded from the local `User` entity. Role-based access is managed through the `UserRoles` utility constants.

### Database & Auditing

- **Flyway** applies versioned migrations automatically on startup.
- **Hibernate Envers** (`@Audited`) tracks every entity change. `CustomRevisionEntity` enriches revisions with the acting username.
- Several read-heavy queries go through DB views mapped to `*View` classes (e.g., `FxTradeView`, `FxSettlementView`) to avoid heavy joins at the ORM level.
- **HikariCP** connection pool is capped at 10 connections.

### Key Conventions

- Custom JSR-303 validators (in `validation/`) handle uniqueness checks (e.g., `@UserEmailUnique`) so controllers stay thin.
- `LogSafe` utility in `util/` must be used when logging user-supplied strings to prevent log injection.
- Dev-only beans and data loaders (in `dev/`) are gated with `@Profile("local")`.
- The `rest/` package contains `FxSettlementRestController`, the only REST endpoint; all other endpoints return Thymeleaf views.
