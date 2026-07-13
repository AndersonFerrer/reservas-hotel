# 011 - Cliente puede consultar su propia reserva por ID

## Que cambio

- `GET /api/reservas/{id}` ahora admite el rol `CLIENTE`, ademas de `ADMINISTRADOR` y `CAJERO`.
- Para clientes, el servicio valida que la reserva pertenezca al cliente autenticado; si no, devuelve `404` (no se distingue entre "no existe" y "no es tuya" para evitar enumeracion).
- `ReservaService.buscarPorId` mantiene el comportamiento previo para roles administrativos.
- `SecurityConfig` agrega una regla especifica `GET /api/reservas/*` antes de la regla general `GET /api/reservas/**`, que sigue restringida a `ADMINISTRADOR`/`CAJERO`. Asi, las rutas `/api/reservas` (listado) y `/api/reservas/{id}/pagos` no quedan expuestas a `CLIENTE`.

## Archivos principales

- src/main/java/com/dubai/dubai/security/SecurityConfig.java
- src/main/java/com/dubai/dubai/controllers/ReservaController.java
- src/main/java/com/dubai/dubai/services/ReservaService.java
- src/test/java/com/dubai/dubai/controllers/ReservaControllerTest.java
- dubai-api.postman_collection.json

## Permisos aplicados

| Metodo | Ruta | Rol minimo |
|--------|------|------------|
| GET | `/api/reservas/{id}` | ADMINISTRADOR, CAJERO, CLIENTE |
| GET | `/api/reservas` | ADMINISTRADOR, CAJERO (sin cambios) |
| GET | `/api/reservas/{id}/pagos` | ADMINISTRADOR, CAJERO (sin cambios) |
| GET | `/api/reservas/mis-reservas` | CLIENTE (sin cambios) |

## Uso

### Cliente autenticado pidiendo una reserva propia

```http
GET /api/reservas/42
Authorization: Bearer {{token}}
```

Respuesta `200 OK` con el detalle de la reserva.

### Cliente autenticado pidiendo una reserva ajena

```http
GET /api/reservas/99
Authorization: Bearer {{token}}
```

Respuesta `404 Not Found` (el servicio retorna `null` cuando la reserva no pertenece al cliente autenticado).
