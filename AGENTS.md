# AGENTS.md

API REST de reservas de hotel — Spring Boot 3.5 + Java 21 + PostgreSQL + JPA + JWT.
Empaquetado en `com.dubai.dubai`, artefacto Maven `dubai`.

## Setup commands

- Crear BD local: `CREATE DATABASE dubai;` (PostgreSQL en `localhost:5432`, user/pass `postgres`)
- Configurar credenciales: copiar `.env.example` a `.env` y editar (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`)
- Instalar deps: `./mvnw dependency:resolve` (Maven wrapper incluido; usar `mvnw`/`mvnw.cmd`, no `mvn` global)
- Levantar API: `./mvnw spring-boot:run` → `http://localhost:8080`
- Compilar sin tests: `./mvnw -DskipTests compile`
- Tests: `./mvnw test`
- Empaquetar: `./mvnw package` (genera `target/dubai-0.0.1-SNAPSHOT.jar`)

## Project layout

- `src/main/java/com/dubai/dubai/`
  - `controllers/` — endpoints REST, 1 archivo por recurso (`AuthController`, `ReservaController`, …)
  - `services/` — reglas de negocio, validaciones, mapeo a entidades
  - `repositories/` — interfaces `JpaRepository`, queries JPQL derivadas
  - `models/` — entidades JPA y enums (`@Enumerated(EnumType.STRING)`)
  - `dto/` — requests/responses específicos (`AuthResponse`, `LoginRequest`, `RegistroClienteRequest`, `ReservaConPagoRequest`, …)
  - `security/` — `SecurityConfig`, `JwtService`, `JWTAuthorizationFilter`
  - `config/` — bootstrap no-estándar (ej. `DatabaseCompatibilityConfig` para columnas legacy)
- `src/main/resources/application.properties` — datasource vía `${ENV_VAR}`, JWT secret/expiración vía env
- `src/test/java/com/dubai/dubai/controllers/` — tests JUnit 5 + Mockito, 1 archivo por controller
- `changes/` — bitácora numerada de cambios medianos+ (ver convención abajo)
- `dubai-api.postman_collection.json` — colección Postman versionada en la raíz
- `.env.example` — plantilla de variables de entorno (`.env` está en `.gitignore`)

## Code style

- Java 21, sin Lombok (escribir getters/setters/constructores explícitos)
- Spring stereotypes: `@RestController`, `@Service`, `@Repository`, `@Component`; inyección por constructor (sin `@Autowired` en campos)
- Paquete único `com.dubai.dubai.*`, sin sub-paquetes por dominio
- Entidades JPA con `@Entity` + `@Table` + `@Id` + `@GeneratedValue`; enums como `STRING`
- Nombres en español: `Cliente`, `Reserva`, `Pago`, `Habitacion`, `Calificacion`, `Cupon`, `Caracteristica` (sin tildes en identificadores)
- Respuestas JSON consistentes: `{"error": "..."}` para errores de validación, `ResponseEntity<?>` con códigos HTTP semánticos (`201` crear, `200` actualizar, `204` eliminar, `400` validación, `404` no existe, `409` con dependencias, `401`/`403` seguridad)
- Validación de negocio en `*Service`, no en controllers
- Reglas de borrado: físico solo si no hay dependencias (sino `409`); en `Habitacion` → `MANTENIMIENTO`, `Reserva` → `CANCELADA`, `Pago` → `ANULADO`

## Testing instructions

- Unit tests con JUnit 5 + Mockito (ya incluidos en `spring-boot-starter-test`)
- `@ExtendWith(MockitoExtension.class)`, `@Mock` para servicios, `@InjectMocks` para controllers
- Ejecutar `./mvnw test` antes de cualquier commit
- Cobertura mínima esperada: paths felices + al menos un caso de validación por endpoint nuevo
- No hay tests de integración ni E2E aún; si seagregan, ubicación: `src/test/java/com/dubai/dubai/integration/`

## PR & commit conventions

- Branch desde `main` (default branch, único remoto `origin`)
- Mensajes en **conventional commits en español**: `feat: ...`, `fix: ...`, `docs: ...`, `refactor: ...`, `test: ...`
- Descripción del commit explica el **por qué** en una línea, sin punto final
- Commits chicos y atómicos (un cambio lógico por commit)
- Antes de abrir PR: `./mvnw test` verde + `git status` limpio
- Si el cambio toca seguridad, agregar nota en el body del commit sobre qué rol/método se afecta

## Documentación de cambios — convención `changes/`

Cada cambio **mediano o mayor** se documenta en `changes/XXX-slug.md` con numeración secuencial (`001-`, `002-`, …).
Cambios chicos (typos, fix de una línea, refactor sin impacto funcional) **no** requieren archivo en `changes/`.

Estructura obligatoria del archivo:

```md
# 00X - Titulo corto y descriptivo

## Que cambio
- bullets concretos de qué se agregó/modificó/eliminó

## Archivos principales
- rutas relativas a los archivos clave tocados

## Uso
- (opcional) ejemplo HTTP/JSON si agrega o modifica endpoints
- (opcional) tablas de permisos si toca `SecurityConfig`

## Compatibilidad
- (opcional) notas de migración o ajustes a esquemas existentes
```

Reglas:
- Numeración correlativa, sin huecos. Antes de crear `00X`, verificar que existe `00(X-1)`.
- `slug` en kebab-case, descriptivo (ej. `crud-administrativo-hotel`, `reservas-con-pagos`)
- Documentar **después** de implementar, en el mismo PR
- Si el cambio introduce endpoints nuevos, el archivo debe listar método+ruta+rol mínimo en una tabla de "Permisos aplicados"

## Postman — convención de sincronización

`dubai-api.postman_collection.json` se mantiene en la raíz del repo y **se actualiza con cada cambio** que toque endpoints (verificado: 4 de 9 commits históricos lo modifican).

Reglas al actualizar:
- Agrupar requests por recurso (`Auth`, `Clientes`, `Tipos de Habitacion`, …) usando folders en la colección
- Nombre del request = acción + rol mínimo entre paréntesis cuando aplique:
  `Crear Reserva Operativa (ADMINISTRADOR, CAJERO)`
  `Crear Mi Reserva con Pago (CLIENTE)`
- Auth: `bearer` con variable `{{token}}` (excepto `/api/auth/**` que usa `noauth`)
- Body `raw` JSON, header `Content-Type: application/json`
- Reusar variables de colección ya definidas: `baseUrl`, `token`, `clienteId`, `tipoHabitacionId`, `habitacionId`, `personalId`, `pagoId`, `caracteristicaId`, `calificacionId`, `cuponId`, `habitacionCaracteristicaId`, `reservaId`. Si un cambio introduce un nuevo ID reusable, agregarlo al bloque `variable` con valor `"1"`.
- Validar JSON antes de commit (`jq . dubai-api.postman_collection.json > /dev/null`)
- Si se renombra/elimina un endpoint, eliminar también su request de la colección

## Permisos por rol

Tres roles en JWT (`ADMINISTRADOR`, `CAJERO`, `CLIENTE`). La matriz completa vive en `changes/005-permisos-por-roles.md` y `changes/006-crud-administrativo-hotel.md`. Resumen:

- `ADMINISTRADOR`: acceso total a todos los endpoints protegidos
- `CAJERO`: operación interna — clientes, pagos, reservas, habitaciones, `habitacion-caracteristicas`
- `CLIENTE`: lectura de catálogo (`tipos-habitacion`, `habitaciones`, `cupones`, `calificaciones`) + sus propias reservas vía `/api/reservas/mis-reservas/**`

Toda mutación a `SecurityConfig.java` debe documentarse con la matriz nueva en el `changes/` correspondiente.

## Security

- **Nunca commitear secretos** — `.env` está en `.gitignore`, solo se versiona `.env.example`
- JWT secret en `app.jwt.secret` con default **solo-dev**; producción debe setear `JWT_SECRET` con valor fuerte (>= 32 bytes)
- Contraseñas siempre con `BCryptPasswordEncoder`
- Solo `/api/auth/**` es público; el resto requiere `Authorization: Bearer <token>`
- Stateless: `SessionCreationPolicy.STATELESS`, CSRF deshabilitado (API REST)
- Errores de seguridad devuelven JSON: `{"error":"No autorizado"}` (401) o `{"error":"Acceso denegado"}` (403)

## Convenciones operativas

- **No pisar `README.md` con cambios chiquitos** — el README es de alto nivel; los detalles viven en `changes/`
- **Conventional commits + `changes/` + Postman** son los tres artefactos que se actualizan juntos. Si falta uno, el PR está incompleto.
- Si una tarea toca más de un recurso, crear **un solo `changes/XXX-...md`** que cubra todo el cambio (no uno por archivo).
- Si una tarea es **solo documentación** (README, Postman, cambios/), no requiere código, pero sí commit y changelog si es mediano+.