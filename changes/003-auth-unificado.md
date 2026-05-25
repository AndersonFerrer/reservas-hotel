# 003 - Auth Unificado

## Que cambio

- Se agrego la entidad `Usuario` para centralizar credenciales.
- `Cliente` y `Personal` pueden iniciar sesion mediante la misma ruta de login.
- El registro de cliente crea un perfil `Cliente` y un `Usuario` con rol `CLIENTE`.
- El registro de personal crea un perfil `Personal` y un `Usuario` con rol `ADMINISTRADOR` o `CAJERO`.

## Archivos principales

- `src/main/java/com/dubai/dubai/models/Usuario.java`
- `src/main/java/com/dubai/dubai/models/RolUsuario.java`
- `src/main/java/com/dubai/dubai/models/TipoUsuario.java`
- `src/main/java/com/dubai/dubai/controllers/AuthController.java`
- `src/main/java/com/dubai/dubai/services/AuthService.java`
- `src/main/java/com/dubai/dubai/dto/*`

## Endpoints

```http
POST /api/auth/register/cliente
POST /api/auth/register/personal
POST /api/auth/login
```

Ejemplo login:

```json
{
  "email": "ana@correo.com",
  "password": "secret123"
}
```
