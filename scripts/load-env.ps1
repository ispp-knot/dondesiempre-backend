# Script para cargar variables de entorno desde .env en Windows PowerShell
# Uso: .\load-env.ps1

param(
    [string]$EnvFile = ".env"
)

# Verificar si el archivo .env existe
if (-Not (Test-Path $EnvFile)) {
    Write-Host "Error: No se encontró el archivo $EnvFile" -ForegroundColor Red
    exit 1
}

Write-Host "Cargando variables de entorno desde $EnvFile..." -ForegroundColor Green

# Leer el archivo línea por línea
Get-Content $EnvFile | ForEach-Object {
    $line = $_.Trim()
    
    # Ignorar líneas vacías y comentarios
    if ($line -eq "" -or $line.StartsWith("#")) {
        return
    }
    
    # Separar clave y valor
    if ($line -match '^([^=]+)=(.*)$') {
        $key = $matches[1].Trim()
        $value = $matches[2].Trim()
        
        # Remover comillas si existen
        $value = $value -replace '^["'']|["'']$', ''
        
        # Establecer la variable de entorno
        [Environment]::SetEnvironmentVariable($key, $value, "Process")
        Write-Host "  ✓ $key" -ForegroundColor Cyan
    }
}

Write-Host "`n✓ Variables de entorno cargadas exitosamente" -ForegroundColor Green
Write-Host "Nota: Estas variables solo están disponibles en la sesión actual de PowerShell" -ForegroundColor Yellow
