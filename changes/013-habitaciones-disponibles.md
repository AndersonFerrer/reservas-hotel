# 013 - Endpoint público de habitaciones disponibles

## Que cambio

- Nuevo endpoint `GET /api/habitaciones/disponibles?fechaIngreso=YYYY-MM-DD&fechaSalida=YYYY-MM-DD&huespedes=N` para que clientes (anónimos o autenticados) consulten habitaciones disponibles sin necesidad de un token.
- La consulta aplica tres filtros combinados en una sola SQL con `NOT EXISTS`:
  - `Habitacion.estado <> MANTENIMIENTO` (excluye mantenimiento; `OCUPADA` se resuelve por reservas).
  - `TipoHabitacion.capacidad >= :huespedes`.
  - No existe una `Reserva` superpuesta en estado `PENDIENTE` o `CONFIRMADA` donde `reserva.fechaIngreso < fechaSalida AND reserva.fechaSalida > fechaIngreso`. Las reservas `CANCELADA` y `FINALIZADA` no bloquean.
- Respuesta agrupada por `tipoHabitacionId`: cada elemento del array contiene todas las habitaciones disponibles de un mismo tipo, con `nombre`, `descripcion`, `precioBase`, `huespedes` y `caracteristicas` (lista de nombres, ordenados alfabéticamente) del `TipoHabitacion`.
- Orden estable: por `tipoHabitacionId` ascendente y dentro de cada tipo por `numero` ascendente (forzado en SQL y preservado por el `LinkedHashMap` del servicio).
- Validaciones de entrada en `HabitacionService.listarDisponibles`, que lanza `IllegalArgumentException` con mensajes claros por cada error:
  - `fechaIngreso` y `fechaSalida` obligatorias.
  - `fechaSalida` estrictamente posterior a `fechaIngreso`.
  - `huespedes >= 1`.
- Nuevo `@RestControllerAdvice` en `com.dubai.dubai.handlers.ApiExceptionHandler` que traduce `IllegalArgumentException` a `400 {"error":"..."}` y `EntityNotFoundException` a `404 {"error":"..."}`. Así el endpoint público y todos los `services` existentes quedan cubiertos sin tener que envolverse en `try/catch` por controller.
- Regla explícita en `SecurityConfig`: `GET /api/habitaciones/disponibles` es público (`permitAll()`), documentando la intención aunque el `GET /api/habitaciones/**` ya lo cubriera.

## Archivos principales

- src/main/java/com/dubai/dubai/dto/HabitacionDisponibleResponse.java (nuevo record, Java 21)
- src/main/java/com/dubai/dubai/handlers/ApiExceptionHandler.java (nuevo `@RestControllerAdvice` global)
- src/main/java/com/dubai/dubai/repositories/HabitacionRepository.java (nuevo `findDisponibles` con JPQL + `NOT EXISTS`)
- src/main/java/com/dubai/dubai/services/HabitacionService.java (nuevo `listarDisponibles` con validaciones, agrupación y mapeo)
- src/main/java/com/dubai/dubai/controllers/HabitacionController.java (nuevo `@GetMapping("/disponibles")`)
- src/main/java/com/dubai/dubai/security/SecurityConfig.java (regla explícita `permitAll` para `/api/habitaciones/disponibles`)
- src/test/java/com/dubai/dubai/services/HabitacionServiceListarDisponiblesTest.java
- src/test/java/com/dubai/dubai/controllers/HabitacionControllerDisponiblesTest.java
- dubai-api.postman_collection.json

## Permisos aplicados

| Metodo | Ruta | Acceso |
|--------|------|--------|
| GET | `/api/habitaciones/disponibles` | Publico (sin auth) |

## Uso

Request sin token (anonimo) o con cualquier token:

```http
GET /api/habitaciones/disponibles?fechaIngreso=2026-08-01&fechaSalida=2026-08-05&huespedes=2
```

Response `200 OK` (ejemplo con un tipo y dos habitaciones):

```json
[
  {
    "tipoHabitacionId": 1,
    "habitaciones": [
      { "habitacionId": 1, "numero": "A01" },
      { "habitacionId": 2, "numero": "A02" }
    ],
    "precioBase": 220.5,
    "huespedes": 4,
    "caracteristicas": ["jacuzzi", "tv 50"],
    "nombre": "Suite Presidencial",
    "descripcion": "Suite presidencial completamente amplia con todas las comodidades a la mano"
  }
]
```

Si no hay disponibilidad para los criterios dados, el endpoint devuelve `200 OK` con `[]`.

Validaciones rotas devuelven `400 {"error":"..."}`:

```http
GET /api/habitaciones/disponibles?fechaIngreso=2026-08-05&fechaSalida=2026-08-01&huespedes=2
```
```json
{ "error": "fechaSalida debe ser posterior a fechaIngreso" }
```

```http
GET /api/habitaciones/disponibles?fechaIngreso=2026-08-01&fechaSalida=2026-08-05&huespedes=0
```
```json
{ "error": "huespedes debe ser mayor o igual a 1" }
```

## Compatibilidad

- El nuevo `@RestControllerAdvice` aplica a TODA la API. Los controllers existentes que ya atrapaban `IllegalArgumentException` localmente siguen funcionando igual: esa captura local gana porque ocurre antes de que la excepción salga del controller. No se rompe ningun endpoint existente.
- `SecurityConfig` agrega una regla `permitAll()` antes de la regla general `GET /api/habitaciones/**`, sin pisar nada: las reglas se evaluan en orden y la primera coincidencia gana, por lo que `disponibles` queda explicitamente publico y el resto del path sigue cubierto.
- Sin nuevas dependencias en `pom.xml`. Solo `org.springframework.format.annotation.DateTimeFormat` (ya provisto por `spring-boot-starter-web`).
