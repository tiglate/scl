# SCL (Settlement Control)

Simple application for settlement control.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tiglate_scl&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tiglate_scl)

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## Quick Start with Dev Container

The easiest way to start developing is using a **Dev Container**. 
If you use IntelliJ or VS Code, you can open the project in a container that already has the JDK and all dependencies configured.

1. Open the project in your IDE.
2. When prompted, click **Reopen in Container** (or equivalent).
3. The IDE will build and start the application environment automatically.

## Running the Project Manually

If you prefer not to use Dev Containers, follow these steps:

### 1. Infrastructure

Start the database (MS SQL Server) and the mail server (Mailpit) using Docker Compose. 

First, create a `.env` file from the example:

```bash
cp .env.example .env
```

Then start the services:

```bash
docker-compose up -d
```

*Note: The `docker-compose.yml` uses the `JDBC_DATABASE_PASSWORD` variable defined in your `.env` file.*

### 2. Database Setup

The database (defined by `JDBC_DATABASE_DB` in `.env`) is automatically created by the `mssql-setup` container defined in `docker-compose.yml`.

### 3. Application

Set the required environment variables and run the application using Maven:

```bash
# Example environment variables (defined in your .env)
export JDBC_DATABASE_HOST=localhost
export JDBC_DATABASE_DB=scl_dev
export JDBC_DATABASE_USERNAME=sa
export JDBC_DATABASE_PASSWORD=StrongPassword123! # or whatever you set in .env
export SMTP_HOST=localhost
export SMTP_USERNAME=
export SMTP_PASSWORD=

./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Mail Server

You can access the Mailpit web interface to check outgoing emails at `http://localhost:8025`.
