@echo off
REM Script para cargar variables de entorno desde .env en Windows CMD
REM Uso: load-env.bat

setlocal enabledelayedexpansion

set "ENV_FILE=.env"

REM Verificar si el archivo .env existe
if not exist "%ENV_FILE%" (
    echo Error: No se encontro el archivo %ENV_FILE%
    exit /b 1
)

echo Cargando variables de entorno desde %ENV_FILE%...
echo.

REM Leer el archivo línea por línea
for /f "usebackq tokens=*" %%a in ("%ENV_FILE%") do (
    set "line=%%a"
    
    REM Ignorar líneas vacías
    if not "!line!"=="" (
        REM Ignorar comentarios
        echo !line! | findstr /r "^#" >nul
        if errorlevel 1 (
            REM Procesar la línea
            for /f "tokens=1,* delims==" %%b in ("!line!") do (
                set "key=%%b"
                set "value=%%c"
                
                REM Remover espacios en blanco del key
                for /f "tokens=* delims= " %%d in ("!key!") do set "key=%%d"
                
                REM Remover comillas del value si existen
                set "value=!value:"=!"
                
                REM Establecer la variable
                set "!key!=!value!"
                echo   [OK] !key!
            )
        )
    )
)

echo.
echo Variables de entorno cargadas exitosamente
echo Nota: Estas variables solo estan disponibles en la sesion actual de CMD
echo.
echo Para usar las variables en tu sesion, ejecuta este script con:
echo   call load-env.bat

endlocal
