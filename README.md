# DondeSiempre Backend

---

En este documento se detallan los pasos para poder iniciar el backend de donde siempre y empezar el desarrollo.

# Clone del repositorio

Primero clone el repositorio con `git clone`.

## Carga de las variables de entorno desde .env

Estos scripts te permiten cargar variables de entorno desde un archivo `.env` en diferentes sistemas operativos.

### 📁 Archivos incluidos

- `load-env.sh` - Para Linux y macOS (Bash/Zsh)
- `load-env.ps1` - Para Windows PowerShell
- `load-env.bat` - Para Windows Command Prompt (CMD)
- `.env.example` - Archivo de ejemplo

### 🚀 Uso

```bash
#Rellene el .env con los valores de las variables de entorno
cp .env.example .env
```

### Linux / macOS

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x load-env.sh

# Cargar las variables
source load-env.sh
# o
. load-env.sh

# Para usar un archivo diferente
source load-env.sh mi-archivo.env

# Verificar que se cargaron
echo $DB_URL
```

### Windows PowerShell

```powershell
# Ejecutar el script
.\scripts\load-env.ps1

# Para usar un archivo diferente
.\scripts\load-env.ps1 -EnvFile "mi-archivo.env"

# Verificar que se cargaron
echo $env:DB_URL

# Si tienes problemas de permisos, ejecuta primero:
Unblock-File -Path .\scripts\load-env.ps1
```

### Windows CMD

```cmd
# IMPORTANTE: Usar 'call' para mantener las variables en la sesión
call .\scripts\load-env.bat

# Verificar que se cargaron
echo %DB_URL%
```

## 📝 Formato del archivo .env

```bash
# Comentarios comienzan con #
VARIABLE_SIMPLE=valor

# Valores con espacios usan comillas
VARIABLE_CON_ESPACIOS="valor con espacios"
OTRA_VARIABLE='también funciona con comillas simples'

# Sin espacios alrededor del signo =
DB_HOST=localhost
DB_PORT=5432
```

## ⚠️ Notas importantes

1. **Las variables solo existen en la sesión actual** - No se guardan permanentemente
2. **Linux/Mac**: Debes usar `source` o `.` antes del script
3. **Windows CMD**: Debes usar `call` antes del script

## Iniciar el proyecto

Una vez configurado el entorno, iniciar el proyecto es tan simple como ejecutar:

### Windows

```powershell
.\mvnw spring-boot:run
```

### Linux/macOS

```bash
mvnw spring-boot:run
```
