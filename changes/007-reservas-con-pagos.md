# 007 - Reservas con pago pendiente o pago incluido

## Que cambio

- Las reservas ya no requieren `pagoId` para crearse.
- Los pagos ahora se asocian a una reserva mediante `reservaId`.
- Se agrego el estado de pago con `PENDIENTE`, `PAGADO`, `ANULADO` y `REEMBOLSADO`.
- Se agregaron endpoints para crear reservas con pago incluido en una sola operacion.
- Se agrego consulta de pagos por reserva.
- Se agrego compatibilidad de esquema para quitar el `NOT NULL` legacy de `reservas.pago_id` cuando exista en PostgreSQL.

## Flujo de reserva sin pago

### Operacion interna o POS

```http
POST /api/reservas
```

```json
{
  "clienteId": 1,
  "habitacionId": 1,
  "personalId": 1,
  "fechaIngreso": "2026-05-10",
  "fechaSalida": "2026-05-12"
}
```

La reserva queda en estado `PENDIENTE`.

### Cliente autenticado

```http
POST /api/reservas/mis-reservas
```

```json
{
  "habitacionId": 1,
  "personalId": 1,
  "fechaIngreso": "2026-05-10",
  "fechaSalida": "2026-05-12"
}
```

El cliente se obtiene desde el token.

## Flujo de reserva con pago

### Operacion interna o POS

```http
POST /api/reservas/con-pago
```

```json
{
  "reserva": {
    "clienteId": 1,
    "habitacionId": 1,
    "personalId": 1,
    "fechaIngreso": "2026-05-10",
    "fechaSalida": "2026-05-12"
  },
  "pago": {
    "metodo": "TARJETA",
    "monto": 560.0,
    "fechaPago": "2026-05-10T10:00:00",
    "referencia": "POS-001",
    "estado": "PAGADO",
    "moneda": "PEN",
    "observacion": "Pago POS"
  }
}
```

### Cliente autenticado

```http
POST /api/reservas/mis-reservas/con-pago
```

El cuerpo usa la misma estructura, pero la reserva no envia `clienteId`.

## Pagos

- `POST /api/pagos` ahora requiere `reservaId`.
- `GET /api/reservas/{id}/pagos` lista los pagos de la reserva.
- `DELETE /api/pagos/{id}` cambia estado a `ANULADO` cuando el pago esta asociado a una reserva.
- Si no se envia `estado`, el pago queda como `PAGADO`.
- Si no se envia `moneda`, se usa `PEN`.

## Permisos

- `ADMINISTRADOR` y `CAJERO` pueden crear reservas operativas con o sin pago.
- `CLIENTE` puede crear sus propias reservas con o sin pago desde rutas `mis-reservas`.
- Los permisos existentes de consulta y CRUD se mantienen.

## Compatibilidad con base existente

Antes este proyecto guardaba `pago_id` directamente en `reservas` como obligatorio. Con el nuevo modelo, la relacion vive en `pagos.reserva_id`. Si una base local conserva la columna antigua `reservas.pago_id` con `NOT NULL`, la aplicacion la ajusta al iniciar para permitir reservas pendientes de pago.
