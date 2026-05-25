# 002 - Repositorios y Servicios

## Que cambio

- Se creo la capa `repositories/` con interfaces `JpaRepository`.
- Los servicios dejaron de usar listas en memoria y ahora consultan PostgreSQL.
- Las validaciones siguen viviendo en servicios.
- `ReservaService` resuelve `Cliente`, `Habitacion`, `Pago` y `Personal` antes de guardar una reserva.

## Archivos principales

- `src/main/java/com/dubai/dubai/repositories/*`
- `src/main/java/com/dubai/dubai/services/*`

## Uso

Los controladores siguen delegando en servicios. Ejemplo:

```http
GET /api/reservas
Authorization: Bearer <token>
```

Para crear una reserva se siguen enviando IDs:

```json
{
  "clienteId": 1,
  "habitacionId": 1,
  "pagoId": 1,
  "personalId": 1,
  "fechaIngreso": "2026-05-10",
  "fechaSalida": "2026-05-12"
}
```
