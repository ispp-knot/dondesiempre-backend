#!/usr/bin/env bash
# Script para Linux/macOS para cargar env y ejecutar Spring Boot

set -e  # Salir si ocurre algún error

# Ejecutar script de entorno si existe
if [ -f "./scripts/load-env.sh" ]; then
    echo "Cargando variables de entorno desde load-env.sh..."
    source ./scripts/load-env.sh
else
    echo "No se encontró load-env.sh, continuando..."
fi

# Ejecutar Spring Boot con perfil dev
echo "Iniciando Spring Boot con perfil 'dev'..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
