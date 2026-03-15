# Variáveis
DB_NAME ?= $(shell grep JDBC_DATABASE_DB .env 2>/dev/null | cut -d= -f2)
JDBC_DATABASE_PASSWORD ?= $(shell grep JDBC_DATABASE_PASSWORD .env 2>/dev/null | cut -d= -f2)

.PHONY: up down restart logs status shell-db

up:
	@echo "Levantando a firma..."
	@JDBC_DATABASE_PASSWORD=$(JDBC_DATABASE_PASSWORD) docker-compose up -d

down:
	@echo "Derrubando tudo (mantendo os dados)..."
	@docker-compose down

reset:
	@echo "🚨 PERIGO: Resetando tudo e APAGANDO os volumes..."
	@docker-compose down -v

logs:
	@docker-compose logs -f

status:
	@docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

shell-db:
	@docker exec -it mssql_dev /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $(JDBC_DATABASE_PASSWORD) -C -d ${DB_NAME}