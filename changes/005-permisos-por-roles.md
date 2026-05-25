# 005 - Permisos por roles

## Que cambio

- Se agregaron reglas reales por rol en Spring Security.
- `ADMINISTRADOR` conserva acceso total.
- `CAJERO` accede a rutas operativas internas.
- `CLIENTE` accede al catalogo y a sus propias reservas.
- Se agregaron endpoints para que el cliente autenticado vea y cree reservas para si mismo.

## Roles

- `ADMINISTRADOR`: administra toda la API.
- `CAJERO`: gestiona operacion hotelera, reservas, clientes y pagos.
- `CLIENTE`: consulta catalogo y opera solo sus reservas.

## Matriz de permisos

| Metodo | Ruta | Publico | ADMINISTRADOR | CAJERO | CLIENTE |
| --- | --- | --- | --- | --- | --- |
| POST | `/api/auth/register/cliente` | Si | Si | Si | Si |
| POST | `/api/auth/register/personal` | Si | Si | Si | Si |
| POST | `/api/auth/login` | Si | Si | Si | Si |
| GET | `/api/clientes` | No | Si | Si | No |
| GET | `/api/clientes/{id}` | No | Si | Si | No |
| GET | `/api/personal` | No | Si | No | No |
| GET | `/api/personal/{id}` | No | Si | No | No |
| GET | `/api/pagos` | No | Si | Si | No |
| GET | `/api/pagos/{id}` | No | Si | Si | No |
| GET | `/api/reservas` | No | Si | Si | No |
| GET | `/api/reservas/{id}` | No | Si | Si | No |
| POST | `/api/reservas` | No | Si | Si | No |
| GET | `/api/reservas/mis-reservas` | No | No | No | Si |
| POST | `/api/reservas/mis-reservas` | No | No | No | Si |
| GET | `/api/habitacion-caracteristicas` | No | Si | Si | No |
| GET | `/api/habitacion-caracteristicas/{id}` | No | Si | Si | No |
| GET | `/api/tipos-habitacion` | No | Si | Si | Si |
| GET | `/api/tipos-habitacion/{id}` | No | Si | Si | Si |
| GET | `/api/habitaciones` | No | Si | Si | Si |
| GET | `/api/habitaciones/{id}` | No | Si | Si | Si |
| GET | `/api/cupones` | No | Si | Si | Si |
| GET | `/api/cupones/{id}` | No | Si | Si | Si |
| GET | `/api/calificaciones` | No | Si | Si | Si |
| GET | `/api/calificaciones/{id}` | No | Si | Si | Si |

## Reservas propias del cliente

### Consultar mis reservas

```http
GET /api/reservas/mis-reservas
Authorization: Bearer <token-cliente>
```

El servicio toma el email del token, busca el usuario autenticado y devuelve solo las reservas del `Cliente` asociado.

### Crear mi reserva

```http
POST /api/reservas/mis-reservas
Authorization: Bearer <token-cliente>
Content-Type: application/json
```

```json
{
  "habitacionId": 1,
  "pagoId": 1,
  "personalId": 1,
  "fechaIngreso": "2026-05-10",
  "fechaSalida": "2026-05-12"
}
```

El cliente no envia `clienteId`; se asigna automaticamente desde el usuario autenticado.

## Codigos de seguridad

- `401`: no hay token o el token no es valido.
- `403`: el token es valido, pero el rol no tiene permiso para la ruta.
