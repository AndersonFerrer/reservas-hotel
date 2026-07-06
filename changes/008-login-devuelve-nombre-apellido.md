# 008 - Login devuelve nombre y apellido

## Que cambio

- La respuesta de `POST /api/auth/login` ahora incluye `nombres` y `apellidos` del `Cliente` o `Personal` asociado al `Usuario` autenticado.
- Si el usuario no tiene perfil asociado, ambos campos se devuelven como `null` (no rompe el contrato).

## Archivos principales

- `src/main/java/com/dubai/dubai/dto/AuthResponse.java`
- `src/main/java/com/dubai/dubai/services/AuthService.java`

## Uso

Request sin cambios (sigue pidiendo solo email y password):

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "ana@correo.com",
  "password": "secret123"
}
```

Response nueva con los dos campos extra:

```json
{
  "token": "eyJ...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "email": "ana@correo.com",
  "nombres": "Ana",
  "apellidos": "Lopez",
  "rol": "CLIENTE",
  "tipoUsuario": "CLIENTE"
}
```

Para un `PERSONAL` los mismos campos `nombres`/`apellidos` se llenan desde `Personal`:

```json
{
  "token": "eyJ...",
  "tipo": "Bearer",
  "usuarioId": 2,
  "email": "carlos@hotel.com",
  "nombres": "Carlos",
  "apellidos": "Ramos",
  "rol": "ADMINISTRADOR",
  "tipoUsuario": "PERSONAL"
}
```

## Compatibilidad

- Es un cambio **aditivo**: clientes que ignoren los campos nuevos siguen funcionando.
- Si el `Usuario` no tiene `Cliente` ni `Personal` asociado (estado inconsistente que la app no debería producir), `nombres` y `apellidos` llegan como `null` en vez de explotar.