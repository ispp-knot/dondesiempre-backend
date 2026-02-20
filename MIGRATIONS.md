## Migraciones

**Todos somos responsables de las migraciones que creamos**. Una migración mal hecha puede **borrar datos permanentemente**. Las migraciones **serán revisadas en los PRs** y **pueden generar conflictos** que **han de ser resueltos manualmente**. Esto es parte de la responsabilidad de desarrollar en el backend.

Las migraciones pueden ser autogeneradas, pero **con límites**. Por ejemplo, si se renombra una columna, la migración autogenerada **destruirá la vieja y creará una nueva** por defecto, y esto ha de ser ajustado manualmente. [Este artículo](https://bell-sw.com/blog/how-to-use-liquibase-with-spring-boot/) es un buen recurso. Para autogenerar, se usa:

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

Ejecutar el servidor también aplica las migraciones automáticamente.
