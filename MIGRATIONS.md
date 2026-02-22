## Migraciones

**Todos somos responsables de las migraciones que creamos**. Una migración mal hecha puede **borrar datos permanentemente**. Las migraciones **serán revisadas en los PRs** y **pueden generar conflictos** que **han de ser resueltos manualmente**. Esto es parte de la responsabilidad de desarrollar en el backend.

El perfil de `dev` (`./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`) no require de migraciones, lo cuál es
útil para probar cambios rápidos en el desarrollo.

El perfil de `test` sí que usa las migraciones, por lo que han de ser **válidas** para que pasen. Si se quieren probar
las migraciones por si mismas, se puede usar el perfil de `prod`.

### Entornos locales

- **prod** (`localhost:5434/proddb`): Usa Liquibase. Las migraciones se aplican automáticamente.
- **dev** (`localhost:5432/devdb`): Usa Hibernate auto-update. La base de datos se actualiza automáticamente sin Liquibase.
- **test** (`localhost:5433/testdb`): Usa Liquibase. Las migraciones se aplican automáticamente al ejecutar tests.

### Estructura de carpetas

Las migraciones se organizan **una carpeta por tarea**, para evitar que todo quede en un único directorio plano, conservando la atomicidad de cada archivo de migración. La estructura es la siguiente:

    src/main/resources/db/changelog/
    ├── db.changelog-master.yaml
    ├── 001-base-models/
    │   ├── base-models.changelog.yaml
    │   └── 001-create-clients.yaml
    │   └── ...
    ├── 002-maps/
    │   ├── maps.changelog.yaml
    │   ├── 001-create-map-entity.yaml
    │   └── ...

Cada **subcarpeta** tiene **su propio archivo de changelog** (_XXX\-taskName_.changelog.yaml) que lista sus changesets, y el **master** incluye los **changelogs de todas las subcarpetas**, en orden.

A continuación se muestran ejemplos del contenido de estos changelogs:

#### Master changelog

```
# db.changelog-master.yaml
databaseChangeLog:
- include:
   file: db/changelog/001-base-models/base-models.changelog.yaml
- include:
   file: db/changelog/002-maps/maps.changelog.yaml
```

#### Task changelog

```
# 001-base-models/base-models.changelog.yaml
databaseChangeLog:
- include:
   file: db/changelog/001-base-models/001-create-clients.yaml
- include:
   file: db/changelog/001-base-models/002-create-products.yaml
```

#### Archivo de migración individual

```
# 001-base-models/001-create-clients.yaml
databaseChangeLog:
- changeSet:
   id: 001-create-clients
   author: dev1
   changes:
       - createTable:
           tableName: clients
           columns:
           - column:
               name: id
               type: INTEGER
               constraints:
                   primaryKey: true
                   nullable: false
           - column:
               name: name
               type: VARCHAR(255)
               constraints:
                   nullable: false
...
```

### Generar y aplicar migraciones

Las migraciones pueden ser autogeneradas, pero **con límites**. Por ejemplo, si se renombra una columna, la migración autogenerada **destruirá la vieja y creará una nueva** por defecto, y esto ha de ser ajustado manualmente. [Este artículo](https://bell-sw.com/blog/how-to-use-liquibase-with-spring-boot/) es un buen recurso.

**Todos los comandos de migraciones se ejecutan contra la base de datos prod** (`localhost:5434/proddb`). El comando de
autogeneración lo que hace es **comparar el estado actual de esa base de datos** con **lo que dice los modelos**, por lo
que si el estado de la base de datos de prod local no es correcto, la migración tampoco lo será.

```bash
./mvnw liquibase:diff
```

Esta genera un archivo como `src/main/resources/db/changelog/2026-02-20-115618_changelog.yaml`. Ese archivo ha de ser **revisado manualmente** y renombrado a un nombre que tenga sentido (ej. `1-create-client.yml`), y luego ha de ser añadido a `src/main/resources/db/changelog/db.changelog-master.yaml`. Luego, para que se procese, hay que hacer:

```bash
./mvnw process-resources
```

Para probar la migración, se pueden usar los siguientes comandos que imprimen el SQL del upgrade y la migración. Nótese que **hay que ejecutar ambos** aún si sólo se quiere ver uno, porque imprimir el SQL actualiza el "estado" que liquibase piensa que tiene la base de datos.

```bash
./mvnw liquibase:updateSQL && cat target/liquibase/migrate.sql
./mvnw liquibase:rollbackSQL -Dliquibase.rollbackCount=1 && cat target/liquibase/migrate.sql
```

Para aplicar los cambios de verdad y revertir:

```bash
./mvnw liquibase:update
./mvnw liquibase:rollback -Dliquibase.rollbackCount=<count>
./mvnw liquibase:rollback -Dliquibase.rollbackToDate=<date>
```

Ejecutar el servidor con el perfil prod también aplica las migraciones automáticamente:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
