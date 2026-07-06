# 010 - Reemplazar caracteristicas de un tipo de habitacion

## Que cambio

- Nuevo endpoint `PUT /api/tipos-habitacion/{id}/caracteristicas` que **reemplaza por completo**
  el set de `Caracteristica` asociadas a un `TipoHabitacion` en una sola operacion.
- Si el array viene vacio (`{"caracteristicaIds": []}`), el `TipoHabitacion` queda sin caracteristicas.
- Si el campo `caracteristicaIds` falta o es `null`, devuelve `400`.
- Valida que el `TipoHabitacion` exista (`404`) y que **todas** las `Caracteristica` indicadas
  existan (`400` indicando el id que fallo). Si alguna falla, aborta toda la operacion y no
  deja la base en estado parcial (`@Transactional`).
- Devuelve el `TipoHabitacion` actualizado con sus caracteristicas ya resueltas (mismo formato
  que `GET /api/tipos-habitacion/{id}`).
- Los endpoints existentes `POST /api/habitacion-caracteristicas`, `PUT /api/habitacion-caracteristicas/{id}`
  y `DELETE /api/habitacion-caracteristicas/{id}` **no se modifican** y siguen funcionando como hasta ahora.
- Se inyecto `CaracteristicaRepository` en `TipoHabitacionService` (no estaba antes).

## Archivos principales

- `src/main/java/com/dubai/dubai/dto/ReemplazarCaracteristicasRequest.java` — nuevo DTO, campo unico `List<Long> caracteristicaIds`.
- `src/main/java/com/dubai/dubai/services/TipoHabitacionService.java` — inyeccion de `CaracteristicaRepository` y nuevo metodo `reemplazarCaracteristicas(Long, List<Long>)` con `@Transactional`.
- `src/main/java/com/dubai/dubai/controllers/TipoHabitacionController.java` — nuevo `@PutMapping("/{id}/caracteristicas")`.
- `src/main/java/com/dubai/dubai/security/SecurityConfig.java` — nueva regla especifica para el endpoint.
- `src/test/java/com/dubai/dubai/controllers/TipoHabitacionControllerTest.java` — 5 tests nuevos: happy path, 404 tipo inexistente, 400 caracteristica inexistente, 400 body nulo, 400 campo `caracteristicaIds` nulo.

## Uso

Request:

```http
PUT /api/tipos-habitacion/3/caracteristicas
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "caracteristicaIds": [1, 4, 7]
}
```

Response exitosa (`200`):

```json
{
  "id": 3,
  "nombre": "Suite",
  "descripcion": "Habitacion premium",
  "precioBase": 250.0,
  "capacidad": 2,
  "caracteristicas": [
    { "id": 1, "nombre": "Wifi", "descripcion": "Internet inalambrico" },
    { "id": 4, "nombre": "Jacuzzi", "descripcion": "Bano privado con tina" },
    { "id": 7, "nombre": "Minibar", "descripcion": "Mini refrigerador" }
  ]
}
```

Para dejar al `TipoHabitacion` sin caracteristicas:

```json
{
  "caracteristicaIds": []
}
```

Errores:

- `400` body ausente o campo `caracteristicaIds` ausente/`null`:

  ```json
  { "error": "El campo caracteristicaIds es obligatorio" }
  ```

- `400` alguna `Caracteristica` no existe (operacion atomica, no se aplica nada):

  ```json
  { "error": "La caracteristica con id 999 no existe" }
  ```

- `404` el `TipoHabitacion` no existe: sin cuerpo.

## Permisos aplicados

| Metodo | Ruta                                          | ADMINISTRADOR | CAJERO | CLIENTE |
| --- |------------------------------------------------| --- | --- | --- |
| PUT  | `/api/tipos-habitacion/*/caracteristicas`      | Si  | Si  | No  |

La regla es especifica (`PUT /api/tipos-habitacion/*/caracteristicas`), no cae en un matcher
generico de `PUT /api/tipos-habitacion/**` que pudiera comerse otras rutas. Los endpoints
antiguos `POST/PUT/DELETE /api/habitacion-caracteristicas/**` mantienen sus permisos
anteriores (`ADMINISTRADOR` y `CAJERO`).

## Compatibilidad

- **No breaking change**: los endpoints `POST /api/habitacion-caracteristicas`, `PUT /api/habitacion-caracteristicas/{id}`
  y `DELETE /api/habitacion-caracteristicas/{id}` se mantienen intactos.
- El nuevo endpoint usa la misma entidad `TipoHabitacion` y el mismo set `@ManyToMany caracteristicas`
  existente — no se agregan tablas ni columnas.
- Comportamiento transaccional: si la validacion de caracteristicas falla, no se persiste ningun cambio
  en la tabla join `tipo_habitacion_caracteristicas`.