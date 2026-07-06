# Dubai — Convenciones del proyecto

Referencia rápida para cualquier agente que trabaje en este repo. La fuente de verdad siempre es `AGENTS.md` en la raíz.

## Stack

- Java 21, Spring Boot 3.5.13, Maven (usar `./mvnw`)
- PostgreSQL + Spring Data JPA, enums como `STRING`
- Spring Security + JWT (jjwt 0.12.6), BCrypt
- JUnit 5 + Mockito para tests
- Sin Lombok

## Paquete base

`com.dubai.dubai` — sin sub-paquetes por dominio.

```
com.dubai.dubai.{controllers,services,repositories,models,dto,security,config}
```

## Comandos del día a día

| Acción | Comando |
|---|---|
| Levantar API | `./mvnw spring-boot:run` |
| Compilar (sin tests) | `./mvnw -DskipTests compile` |
| Tests | `./mvnw test` |
| Empaquetar | `./mvnw package` |
| Validar Postman JSON | `jq . dubai-api.postman_collection.json > /dev/null` |

## Variables de entorno

Configurar en `.env` (basado en `.env.example`):

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET` (>= 32 bytes en prod), `JWT_EXPIRATION_MS` (default `86400000`)

## Roles del JWT

- `ADMINISTRADOR` — total
- `CAJERO` — operación interna (clientes, pagos, reservas, habitaciones, habitacion-caracteristicas)
- `CLIENTE` — catálogo + sus propias reservas (`/api/reservas/mis-reservas/**`)

Matrices detalladas en `changes/005-permisos-por-roles.md` y `changes/006-crud-administrativo-hotel.md`.

## Reglas de borrado

| Recurso | DELETE hace… |
|---|---|
| `Habitacion` | cambia estado a `MANTENIMIENTO` |
| `Reserva` | cambia estado a `CANCELADA` |
| `Pago` asociado a reserva | cambia estado a `ANULADO` |
| Catálogo sin estado (`Caracteristica`, `TipoHabitacion`, `Cupon`, etc.) | borrado físico solo si no tiene dependencias; si las hay, `409` |

## Reglas de creación de reserva

- `clienteId`, `habitacionId`, `personalId` obligatorios.
- `fechaIngreso` y `fechaSalida` obligatorias; `fechaSalida > fechaIngreso`.
- Se calcula cantidad de noches al crear.
- Sin pago → estado `PENDIENTE`.
- Con pago `PAGADO` → estado `CONFIRMADA`.
- Los pagos se asocian a la reserva por `reservaId` (NO al revés desde `reservas`).

## Convenciones de nombres

- Entidades y servicios en español, sin tildes: `Cliente`, `Reserva`, `Pago`, `Habitacion`, `Calificacion`, `Cupon`, `Caracteristica`.
- IDs en JSON con sufijo `Id`: `clienteId`, `habitacionId`, `reservaId`.
- Enums persistidos con `@Enumerated(EnumType.STRING)`.

## Convenciones HTTP

| Código | Uso |
|---|---|
| `201` | Recurso creado |
| `200` | Recurso actualizado o leído |
| `204` | Recurso eliminado o desactivado |
| `400` | Validación fallida (`{"error": "..."}`) |
| `401` | Sin token o token inválido |
| `403` | Token válido pero rol sin permiso |
| `404` | Recurso no existe |
| `409` | Conflicto (ej. dependencias que impiden borrado físico) |

## Convenciones de commit

Conventional commits **en español**, sin punto final:

- `feat: Agregar endpoint de cancelacion anticipada`
- `fix: Validar fechaSalida posterior a fechaIngreso en ReservaService`
- `docs: Sincronizar Postman con nuevos endpoints de pagos`
- `refactor: Extraer logica de noches a NightCounterHelper`
- `test: Cubrir validacion de fechas en ReservaService`

## Convenciones de PR

- Branch desde `main`, no se pushea directo a `main`.
- Antes de abrir: `./mvnw test` verde + `git status` limpio.
- Cuerpo del PR referencia el `changes/00X-...md` si aplica.
- El `code-reviewer` valida: compilación, tests, conventional commit, `changes/`, Postman, seguridad, reglas de borrado.

## Artefactos que se actualizan juntos

Para un cambio mediano+ que toca endpoints:

1. Código Java (`src/main/java/...`)
2. Tests JUnit (`src/test/java/...`)
3. `changes/00X-slug.md` (estructura en `AGENTS.md`)
4. `dubai-api.postman_collection.json` (si hay endpoints nuevos/cambiados)
5. `README.md` (solo si cambia la matriz de alto nivel o aparece un recurso nuevo)

Si falta cualquiera de los primeros 3, el PR está incompleto. El 4 depende de si hay endpoints. El 5 es raro.