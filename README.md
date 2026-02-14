# DondeSiempre Backend

---

En este documento se detallan los pasos para poder iniciar el backend de donde siempre y empezar el desarrollo.

# Clone del repositorio

Primero clone el repositorio con `git clone`.

## Carga de las variables de entorno desde .env

Estos scripts te permiten cargar variables de entorno desde un archivo `.env` en diferentes sistemas operativos.

### 📁 Archivos incluidos en la carpeta scripts

- `load-env.sh` - Para Linux y macOS (Bash/Zsh)
- `run-dev.sh`
- `run-dev-docker.sh`
- `load-env.ps1` - Para Windows PowerShell
- `run-dev.ps1`
- `run-dev-docker.ps1`
- `run-dev.bat` - Para Windows Command Prompt (CMD)
- `run-dev-docker.bat` - Para Windows Command Prompt (CMD)
- `.env.example` - Archivo de ejemplo

Los scripts `run` cargan el entorno y lanzan la aplicación.
Los scripts `docker` carga el entorno, levanta una base de datos postgresql con docker y lanzan la aplicación.

### 🚀 Uso

Para usar la base de datos de neon copie el .env.example vacío y rellenelo:

```bash
cp .env.example .env
```

En caso de usar la base de datos levantada en un contenedor use:

```bash
cp .env.dev .env
```

### Linux / macOS permisos

```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x ./scripts/load-env.sh
chmod +x ./scripts/run-dev.sh
chmod +x ./mvnw
```

### Windows PowerShell permisos

```powershell
# Si tienes problemas de permisos, ejecuta primero:
Unblock-File -Path .\scripts\load-env.ps1
Unblock-File -Path .\scripts\run-dev.ps1
```

## ⚠️ Notas importantes

1. **Las variables solo existen en la sesión actual** - No se guardan permanentemente
2. **Linux/Mac**: Debes usar `source` o `.` antes del script
3. **Windows CMD**: Debes usar `call` antes del script

## Iniciar el proyecto

Una vez configurado el entorno, iniciar el proyecto es tan simple como ejecutar:

### Windows

#### Windows Powershell

```powershell
.\scripts\run-dev.ps1
```

Si se va a usar la DB de docker usar:

```powershell
.\scripts\run-dev-docker.ps1
```

#### CMD

```cmd
call .\scripts\run-dev.bat
```

Si se va a usar la DB de docker usar:

```powershell
.\scripts\run-dev-docker.bat
```

### Linux/macOS

```bash
source ./scripts/run-dev.sh
```

Si se va a usar la DB de docker usar:

```powershell
.\scripts\run-dev-docker.sh
```

Tras detenter la aplicación por completo puede usar `docker compose down` para detener la base de datos. Esto supondrá su eliminación y la eliminación de datos fuera del init.sql.
