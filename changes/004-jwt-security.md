# 004 - JWT y Spring Security

## Que cambio

- Se agrego Spring Security.
- Se agrego generacion y validacion de JWT.
- Se creo `JWTAuthorizationFilter` para autorizar peticiones con `Authorization: Bearer <token>`.
- Se configuro seguridad stateless.
- Solo `/api/auth/**` queda publico; el resto requiere token.

## Archivos principales

- `src/main/java/com/dubai/dubai/security/SecurityConfig.java`
- `src/main/java/com/dubai/dubai/security/JwtService.java`
- `src/main/java/com/dubai/dubai/security/JWTAuthorizationFilter.java`

## Uso

Enviar el token en cada ruta protegida:

```http
Authorization: Bearer eyJ...
```

El token incluye:

- `sub`: email
- `rol`
- `tipoUsuario`
- `usuarioId`
