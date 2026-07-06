# 009 - Mejora de la respuesta de login

## Que cambio

- La respuesta de `POST /api/auth/login` (y `POST /api/auth/register/cliente`) ahora separa
  limpiamente la **credencial** del **perfil del usuario**:
  - Cabecera (`token`, `tipo`, `expiresIn`): datos para autorizar requests.
  - `usuario` (objeto anidado): datos de perfil para hidratar la UI.
- Se elimina el campo `tipoUsuario`: duplicaba el valor de `rol` en la mayoría de los casos.
- `usuarioId` deja de existir a nivel raíz y pasa a ser `usuario.id`.
- Se agrega `expiresIn` en segundos (convención OAuth2) para que el cliente sepa cuánto
  vive el token sin tener que decodificarlo.
- Se agrega `nombreCompleto` calculado server-side (antes el cliente lo componía o usaba
  el helper `nombreCompleto(cliente)`).
- El frontend (`AuthService`) ya no necesita fallback al payload JWT para construir el
  estado de sesión: lee todo directo de `usuario`.

## Archivos principales

- `src/main/java/com/dubai/dubai/dto/AuthResponse.java` — refactor con clase interna `UsuarioResumen`
- `src/main/java/com/dubai/dubai/services/AuthService.java` — `construirRespuesta` actualizado
- `src/main/java/com/dubai/dubai/security/JwtService.java` — agrega `getExpirationSeconds()`
- `src/test/java/com/dubai/dubai/controllers/AuthControllerTest.java` — nuevo, 2 tests
- `src/test/java/com/dubai/dubai/services/AuthServiceTest.java` — nuevo, 8 tests
- `frontend/frontend-hotel/src/app/features/auth/services/auth-api.service.ts` — tipo `LoginResponse` actualizado
- `frontend/frontend-hotel/src/app/core/auth/auth.service.ts` — consume la nueva forma
- `frontend/frontend-hotel/src/app/core/models/api.model.ts` — `UsuarioAutenticado` con `nombreCompleto`
- `README.md` — ejemplo de response actualizado

## Uso

Request sin cambios:

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

Response nueva:

```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "tipo": "Bearer",
  "expiresIn": 86400,
  "usuario": {
    "id": 1,
    "email": "ana@correo.com",
    "nombres": "Ana",
    "apellidos": "Lopez",
    "nombreCompleto": "Ana Lopez",
    "rol": "CLIENTE"
  }
}
```

Para un `PERSONAL`:

```json
{
  "token": "eyJ...",
  "tipo": "Bearer",
  "expiresIn": 86400,
  "usuario": {
    "id": 2,
    "email": "carlos@hotel.com",
    "nombres": "Carlos",
    "apellidos": "Ramos",
    "nombreCompleto": "Carlos Ramos",
    "rol": "ADMINISTRADOR"
  }
}
```

## Compatibilidad

- **Breaking change para clientes que lean `usuarioId`, `email`, `nombres`, `apellidos`,
  `rol` o `tipoUsuario` a nivel raíz** — deben moverse a `usuario.*`.
- El JWT sigue incluyendo `rol`, `tipoUsuario` y `usuarioId` como claims, así que los
  consumidores que lean directo del token (no del body) no se rompen.
- El claim `email` del JWT corresponde a `sub`; el `usuario.email` del body es la misma
  dirección.
- Si el `Usuario` no tiene `Cliente` ni `Personal` asociado (estado inconsistente que la
  app no debería producir), `nombres`, `apellidos` y `nombreCompleto` llegan como `null`
  en vez de explotar.

## Permisos aplicados

| Método | Ruta               | Rol mínimo |
|--------|--------------------|------------|
| POST   | `/api/auth/login`  | público    |
| POST   | `/api/auth/register/cliente` | público |

Sin cambios en `SecurityConfig` (la matriz de permisos no se ve afectada).