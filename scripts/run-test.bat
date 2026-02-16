@echo off
REM run-dev.bat
REM Script para Windows CMD para cargar variables de entorno y ejecutar Spring Boot

setlocal enabledelayedexpansion

set "ENV_FILE=.env"

REM Verificar si el archivo .env existe
if exist "%ENV_FILE%" (
    echo Cargando variables de entorno desde %ENV_FILE%...
    echo.
    
    REM Leer el archivo línea por línea
    for /f "usebackq delims=" %%a in ("%ENV_FILE%") do (
        set "line=%%a"
        
        REM Ignorar líneas vacías
        if not "!line!"=="" (
            REM Ignorar comentarios
            echo !line! | findstr /r "^#" >nul
            if errorlevel 1 (
                REM Procesar la línea - extraer key y value manteniendo el value completo
                for /f "tokens=1,* delims==" %%b in ("!line!") do (
                    set "key=%%b"
                    set "value=%%c"
                    
                    REM Remover espacios en blanco del key
                    for /f "tokens=* delims= " %%d in ("!key!") do set "key=%%d"
                    
                    REM Remover comillas del value si existen (sin tocar el resto)
                    if defined value (
                        set "value=!value:"=!"
                        
                        REM Establecer la variable (sin evaluar el value)
                        set "!key!=!value!"
                        echo   [OK] !key!
                    )
                )
            )
        )
    )
    
    echo.
    echo Variables de entorno cargadas exitosamente
    echo.
) else (
    echo Advertencia: No se encontro el archivo %ENV_FILE%
    echo Continuando sin cargar variables de entorno...
    echo.
)

REM Ejecutar Spring Boot con perfil dev
echo Iniciando Spring Boot con perfil 'dev'...
echo.
call mvnw test -Dspring-boot.run.profiles=dev

endlocal
pause