# 012 - Catalogo de habitaciones publico sin autenticacion

## Que cambio

- `GET /api/tipos-habitacion` y `GET /api/tipos-habitacion/{id}` son publicos: ya no requieren `Authorization: Bearer`.
- `GET /api/habitaciones` y `GET /api/habitaciones/{id}` son publicos: ya no requieren `Authorization: Bearer`.
- Las mutaciones (`POST`, `PUT`, `DELETE`) y la ruta `PUT /api/tipos-habitacion/{id}/caracteristicas` siguen restringidas a `ADMINISTRADOR` y/o `CAJERO`.
- `SecurityConfig` reordena las reglas para que las nuevas `permitAll()` se evaluen antes que las restricciones de rol, y se eliminan dos reglas GET redundantes que limitaban el catalogo a roles especificos.

## Archivos principales

- src/main/java/com/dubai/dubai/security/SecurityConfig.java
- dubai-api.postman_collection.json

## Permisos aplicados

| Metodo | Ruta | Acceso |
|--------|------|--------|
| GET | `/api/tipos-habitacion` | Publico (sin auth) |
| GET | `/api/tipos-habitacion/{id}` | Publico (sin auth) |
| PUT | `/api/tipos-habitacion/{id}/caracteristicas` | ADMINISTRADOR, CAJERO |
| GET | `/api/habitaciones` | Publico (sin auth) |
| GET | `/api/habitaciones/{id}` | Publico (sin auth) |
| POST | `/api/habitaciones` | ADMINISTRADOR, CAJERO |
| PUT | `/api/habitaciones/**` | ADMINISTRADOR, CAJERO |

## Uso

Llamadas sin token, antes bloqueadas con `401 No autorizado`, ahora responden `200 OK`:

```http
GET /api/tipos-habitacion
GET /api/tipos-habitacion/1
GET /api/habitaciones
GET /api/habitaciones/1
```

Los clientes autenticados (cualquier rol) tambien pueden consumirlas sin que el token sea obligatorio. Si llega un token invalido, el filtro `JWTAuthorizationFilter` lo descarta y la peticion sigue como anonima.

## Postman

Las cuatro requests del catalogo quedaron con `auth: { "type": "noauth" }` y sufijo `(Publico)` en el nombre, para distinguir a simple vista que no requieren login:

- `Listar Tipos de Habitacion (Publico)`
- `Buscar Tipo de Habitacion por ID (Publico)`
- `Listar Habitaciones (Publico)`
- `Buscar Habitacion por ID (Publico)`