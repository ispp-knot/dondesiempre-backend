$envScript = ".\scripts\load-env.ps1"
if (Test-Path $envScript) {
    Write-Host "Cargando variables de entorno desde load-env.ps1..."
    . $envScript
} else {
    Write-Host "No se encontró load-env.ps1, continuando..."
}

# Ejecutar Spring Boot con perfil dev
Write-Host "Iniciando Spring Boot con perfil 'dev'..."
./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
