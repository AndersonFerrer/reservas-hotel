# 001 - PostgreSQL y JPA

## Que cambio

- Se agrego Spring Data JPA y el driver de PostgreSQL.
- Se configuro `application.properties` para usar PostgreSQL local con `ddl-auto=update`.
- Los modelos principales ahora son entidades JPA con `@Entity`, `@Table`, `@Id` y `@GeneratedValue`.
- Los enums se persisten como texto con `@Enumerated(EnumType.STRING)`.

## Archivos principales

- `pom.xml`
- `src/main/resources/application.properties`
- `src/main/java/com/dubai/dubai/models/*`

## Uso

Crear la base local:

```sql
CREATE DATABASE dubai;
```

Credenciales por defecto:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dubai
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Hibernate creara y actualizara tablas automaticamente al levantar la aplicacion.
