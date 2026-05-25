# 006 - CRUD administrativo del hotel

## Que cambio

- Se agregaron metodos `POST`, `PUT` y `DELETE` a los controladores existentes de administracion interna.
- Se mantuvieron los `GET` actuales y sus permisos.
- Se agregaron validaciones de negocio en servicios antes de crear, actualizar o eliminar.
- Se actualizo Spring Security para que los nuevos metodos respeten los roles actuales del sistema.

## Recursos con CRUD administrativo

- `Cliente`
- `Personal`
- `Pago`
- `Reserva`
- `Calificacion`
- `Cupon`
- `Caracteristica`
- `TipoHabitacion`
- `Habitacion`
- `HabitacionCaracteristica`

## Permisos aplicados

- `ADMINISTRADOR`: acceso total a todos los endpoints protegidos.
- `CAJERO`: operacion interna sobre clientes, pagos y reservas; puede crear y actualizar habitaciones y relaciones habitacion-caracteristicas.
- `CLIENTE`: no recibe permisos administrativos nuevos; conserva lectura de catalogo y gestion de sus propias reservas.
- Los catalogos de configuracion (`tipos-habitacion`, `caracteristicas`, `cupones`) quedan con mutaciones solo para `ADMINISTRADOR`.

## Endpoints nuevos

| Metodo | Ruta | ADMINISTRADOR | CAJERO | CLIENTE |
| --- | --- | --- | --- | --- |
| POST | `/api/clientes` | Si | Si | No |
| PUT | `/api/clientes/{id}` | Si | Si | No |
| DELETE | `/api/clientes/{id}` | Si | Si | No |
| POST | `/api/pagos` | Si | Si | No |
| PUT | `/api/pagos/{id}` | Si | Si | No |
| DELETE | `/api/pagos/{id}` | Si | Si | No |
| POST | `/api/reservas` | Si | Si | No |
| PUT | `/api/reservas/{id}` | Si | Si | No |
| DELETE | `/api/reservas/{id}` | Si | Si | No |
| POST | `/api/habitaciones` | Si | Si | No |
| PUT | `/api/habitaciones/{id}` | Si | Si | No |
| DELETE | `/api/habitaciones/{id}` | Si | No | No |
| POST | `/api/habitacion-caracteristicas` | Si | Si | No |
| PUT | `/api/habitacion-caracteristicas/{id}` | Si | Si | No |
| DELETE | `/api/habitacion-caracteristicas/{id}` | Si | No | No |
| POST | `/api/personal` | Si | No | No |
| PUT | `/api/personal/{id}` | Si | No | No |
| DELETE | `/api/personal/{id}` | Si | No | No |
| POST | `/api/tipos-habitacion` | Si | No | No |
| PUT | `/api/tipos-habitacion/{id}` | Si | No | No |
| DELETE | `/api/tipos-habitacion/{id}` | Si | No | No |
| POST | `/api/caracteristicas` | Si | No | No |
| PUT | `/api/caracteristicas/{id}` | Si | No | No |
| DELETE | `/api/caracteristicas/{id}` | Si | No | No |
| POST | `/api/cupones` | Si | No | No |
| PUT | `/api/cupones/{id}` | Si | No | No |
| DELETE | `/api/cupones/{id}` | Si | No | No |
| POST | `/api/calificaciones` | Si | No | No |
| PUT | `/api/calificaciones/{id}` | Si | No | No |
| DELETE | `/api/calificaciones/{id}` | Si | No | No |

## Reglas de negocio

- `Habitacion`: valida numero, tipo, estado y piso. El numero debe ser unico. `DELETE` cambia estado a `MANTENIMIENTO`.
- `Reserva`: valida relaciones y fechas. `DELETE` cambia estado a `CANCELADA`.
- `HabitacionCaracteristica`: valida tipo y caracteristica, evita duplicados y sincroniza `TipoHabitacion.caracteristicas`.
- `Cliente` y `Personal`: el CRUD administra datos basicos. La creacion de usuarios y contrasenas sigue en `/api/auth/register/**`.
- Los recursos sin estado se eliminan fisicamente solo si no tienen dependencias; si estan referenciados devuelven `409`.

## Respuestas

- `201`: recurso creado.
- `200`: recurso actualizado.
- `204`: recurso eliminado o desactivado.
- `400`: datos invalidos.
- `404`: recurso no encontrado.
- `409`: recurso con dependencias que impiden eliminacion fisica.
