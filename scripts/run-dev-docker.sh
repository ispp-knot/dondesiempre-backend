#!/usr/bin/env bash
# Script para Linux/macOS para cargar env, levantar Docker y ejecutar Spring Boot
# Espera activa a que Postgres esté listo

set -e  # Salir si ocurre algún error

# 1️⃣ Cargar variables de entorno si existe el script
if [ -f "./scripts/load-env.sh" ]; then
    echo "Cargando variables de entorno desde load-env.sh..."
    source ./scripts/load-env.sh
else
    echo "No se encontró load-env.sh, continuando..."
fi

# 2️⃣ Levantar contenedor de Postgres
if [ -f "docker-compose.yml" ]; then
    echo "Levantando contenedor de Postgres con Docker Compose..."
    docker compose up -d postgres
else
    echo "No se encontró docker-compose.yml, saltando el levantamiento de Postgres..."
fi

# 3️⃣ Esperar activamente a que Postgres acepte conexiones
echo "Esperando a que Postgres acepte conexiones..."
MAX_RETRIES=30
COUNT=0
until docker exec -i postgres-dev pg_isready -U devuser -d devdb >/dev/null 2>&1; do
    COUNT=$((COUNT+1))
    if [ $COUNT -ge $MAX_RETRIES ]; then
        echo "Error: Postgres no respondió después de $MAX_RETRIES intentos."
        exit 1
    fi
    sleep 2
done
echo "Postgres está listo ✅"

# 4️⃣ Ejecutar Spring Boot con perfil dev
echo "Iniciando Spring Boot con perfil 'dev'..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
