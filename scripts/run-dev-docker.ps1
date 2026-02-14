# Script PowerShell para Windows: cargar env, levantar Docker y ejecutar Spring Boot

# 1️⃣ Cargar variables de entorno si existe
$envScript = ".\scripts\load-env.ps1"
if (Test-Path $envScript) {
    Write-Host "Cargando variables de entorno desde load-env.ps1..."
    . $envScript
} else {
    Write-Host "No se encontró load-env.ps1, continuando..."
}

# 2️⃣ Levantar contenedor de Postgres con Docker Compose si existe
$composeFile = ".\docker-compose.yml"
if (Test-Path $composeFile) {
    Write-Host "Levantando contenedor de Postgres con Docker Compose..."
    docker compose up -d postgres
} else {
    Write-Host "No se encontró docker-compose.yml, saltando el levantamiento de Postgres..."
}

# 3️⃣ Esperar activamente a que Postgres acepte conexiones
Write-Host "Esperando a que Postgres acepte conexiones..."
$maxRetries = 30
$count = 0
do {
    try {
        docker exec -i postgres-dev pg_isready -U devuser -d devdb > $null 2>&1
        $ready = $true
    } catch {
        $ready = $false
        Start-Sleep -Seconds 2
        $count++
        if ($count -ge $maxRetries) {
            Write-Error "Error: Postgres no respondió después de $maxRetries intentos."
            exit 1
        }
    }
} until ($ready)

Write-Host "Postgres está listo ✅"

# 4️⃣ Ejecutar Spring Boot con perfil dev
Write-Host "Iniciando Spring Boot con perfil 'dev'..."
./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
