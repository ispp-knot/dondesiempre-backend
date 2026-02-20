# DondeSiempre Backend

---

En este documento se detallan los pasos para poder iniciar el backend de donde siempre y empezar el desarrollo.

# Requisitos

- Java 25 (Configurar JAVA_HOME y el Path)
- Docker compose(Opcional)

# Clone del repositorio

Primero clone el repositorio con `git clone`.

### 🚀 Uso

Para usar la base de datos de neon copie el .env.example vacío y rellenelo:

```bash
cp .env.example .env
```

En caso de usar la base de datos levantada en un contenedor use:

```bash
cp .env.dev .env
```

## Iniciar el proyecto

Una vez configurado el entorno, iniciar el proyecto es tan simple como ejecutar:

### Windows

```powershell
.\mvnw spring-boot:run
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

> Si se va a usar la DB de docker ejecutar los siguiente antes de arrancar spring:
>
> ```powershell
> docker compose up -d postgres
> ```
>
> Para detenerlo:
>
> ```powershell
> docker compose down
> ```

## Para Testear

Es necesario tener instalado Maven (y tenerlo añadido al entorno del sistema)

Para instalar Maven, dirigirse a las siguiente URL: [Descarga de Maven](https://maven.apache.org/download.cgi)
Seleccionar la opcion de _Binary Zip archive_ y descomprimir el archivo en una carpeta dentro de C:/Archivos de Programa/Apache

Luego de tener instalado maven, ejecutar la base de datos de pruebas en un contenedor de docker

```bash
docker compose -f docker-compose.test.yml up
```

### Windows

```powershell
.\mvnw test
```

### Linux/macOS

```bash
mvnw test
```
