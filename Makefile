# Variáveis
DB_NAME=scl_dev

.PHONY: up down restart logs status shell-db

up:
	@echo "Levantando a firma..."
	@MSSQL_PASSWORD=$$(pass databases/mssql/dev/scl) docker-compose up -d

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
	@docker exec -it mssql_dev /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P $$(pass databases/mssql/dev/scl) -C -d ${DB_NAME}