@echo off
REM run-dev.bat
REM Script para Windows CMD: cargar env, levantar Docker y ejecutar Spring Boot

setlocal enabledelayedexpansion

set "ENV_FILE=.env"

REM ----------------------------------------
REM 1️⃣ Cargar variables de entorno desde .env
REM ----------------------------------------
if exist "%ENV_FILE%" (
    echo Cargando variables de entorno desde %ENV_FILE%...
    echo.
    
    for /f "usebackq delims=" %%a in ("%ENV_FILE%") do (
        set "line=%%a"
        if not "!line!"=="" (
            echo !line! | findstr /r "^#" >nul
            if errorlevel 1 (
                for /f "tokens=1,* delims==" %%b in ("!line!") do (
                    set "key=%%b"
                    set "value=%%c"
                    for /f "tokens=* delims= " %%d in ("!key!") do set "key=%%d"
                    if defined value (
                        set "value=!value:"=!"
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

REM ----------------------------------------
REM 2️⃣ Levantar contenedor de Postgres con Docker Compose
REM ----------------------------------------
if exist "docker-compose.yml" (
    echo Levantando contenedor de Postgres con Docker Compose...
    docker compose up -d postgres
) else (
    echo No se encontro docker-compose.yml, saltando levantamiento de Postgres...
)

REM ----------------------------------------
REM 3️⃣ Esperar activamente a que Postgres acepte conexiones
REM ----------------------------------------
echo Esperando a que Postgres acepte conexiones...
set MAX_RETRIES=30
set COUNT=0

:WAIT_PG
docker exec -i postgres-dev pg_isready -U devuser -d devdb >nul 2>&1
if errorlevel 1 (
    set /a COUNT+=1
    if %COUNT% GEQ %MAX_RETRIES% (
        echo Error: Postgres no respondio despues de %MAX_RETRIES% intentos.
        exit /b 1
    )
    timeout /t 2 >nul
    goto WAIT_PG
)
echo Postgres esta listo ✅
echo.

REM ----------------------------------------
REM 4️⃣ Ejecutar Spring Boot con perfil dev
REM ----------------------------------------
echo Iniciando Spring Boot con perfil 'dev'...
call mvnw spring-boot:run -Dspring-boot.run.profiles=dev

endlocal
pause
