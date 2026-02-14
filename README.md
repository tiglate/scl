# SCL (Settlement Control)

Simple application for settlement control.

## Prerequisites

- Java 17
- Maven
- Docker and Docker Compose

## Running the Project

### 1. Infrastructure

Start the database (MS SQL Server) and the mail server (Mailpit) using Docker Compose:

```bash
docker-compose up -d
```

*Note: The `docker-compose.yml` expects an environment variable `MSSQL_PASSWORD` for the SQL Server SA user.*

### 2. Database Setup

The database `scl_dev` is automatically created by the `mssql-setup` container defined in `docker-compose.yml`.

### 3. Application

Set the required environment variables and run the application using Maven:

```bash
# Example environment variables
export MSSQL_PASSWORD=YourStrongPassword
export JDBC_DATABASE_HOST=localhost
export JDBC_DATABASE_DB=scl_dev
export JDBC_DATABASE_USERNAME=sa
export JDBC_DATABASE_PASSWORD=$MSSQL_PASSWORD
export SMTP_HOST=localhost
export SMTP_USERNAME=
export SMTP_PASSWORD=

./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

## Mail Server

You can access the Mailpit web interface to check outgoing emails at `http://localhost:8025`.
