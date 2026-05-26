# INSTRUCCION - Guion de exposicion del segundo avance

Este documento esta ordenado para exponer el avance del backend Dubai de forma clara: primero el problema y la arquitectura, luego base de datos, seguridad, JWT, relaciones y finalmente el flujo de reservas con pagos.

## 1. Introduccion del avance

En este segundo avance el backend evoluciono a una API mas completa para gestion hotelera.

Ahora el sistema:

- Guarda datos reales en PostgreSQL.
- Usa JPA para mapear clases Java a tablas.
- Tiene repositorios para consultar la base de datos.
- Protege rutas con Spring Security.
- Autentica usuarios con JWT.
- Maneja roles: `CLIENTE`, `CAJERO` y `ADMINISTRADOR`.
- Relaciona entidades como clientes, reservas, habitaciones, personal y pagos.
- Permite crear reservas con pago pendiente o con pago incluido.

Idea para decir:

> El objetivo fue pasar de una API basica a un backend mas realista, con persistencia, seguridad, roles y reglas de negocio.

## 2. Arquitectura por capas

El proyecto esta organizado por responsabilidades:

| Capa | Funcion |
| --- | --- |
| `controllers` | Reciben las peticiones HTTP. |
| `services` | Validan reglas de negocio. |
| `repositories` | Consultan y guardan datos en PostgreSQL. |
| `models` | Representan tablas y relaciones. |
| `security` | Valida tokens y permisos. |
| `dto` | Define cuerpos especiales para requests y responses. |

Ejemplo de flujo:

```text
Cliente/Postman -> Controller -> Service -> Repository -> PostgreSQL
```

## 3. Dependencias principales

Las integraciones se agregaron en `pom.xml`.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```

Resumen:

- `data-jpa`: persistencia y repositorios.
- `security`: autenticacion y autorizacion.
- `postgresql`: conexion con la base de datos.
- `jjwt`: creacion y validacion de JWT.

## 4. PostgreSQL y configuracion

La conexion se configura en `application.properties`.

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Puntos clave:

- Las credenciales vienen desde `.env`.
- `ddl-auto=update` permite que Hibernate actualice las tablas segun las entidades.
- `show-sql=true` muestra las consultas SQL en consola.

Idea para decir:

> Con esto la aplicacion ya no depende de datos en memoria; ahora trabaja con una base PostgreSQL.

## 5. JPA: entidades como tablas

JPA convierte clases Java en tablas.

```java
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

Explicacion rapida:

- `@Entity`: la clase sera una tabla.
- `@Table`: define el nombre de la tabla.
- `@Id`: clave primaria.
- `@GeneratedValue`: el ID se genera automaticamente.

Tambien se usan enums como texto:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private EstadoReserva estado;
```

Esto guarda valores como `PENDIENTE`, `CONFIRMADA` o `CANCELADA`, lo cual es mas legible en la base de datos.

## 6. Relaciones entre entidades

Relaciones principales del sistema:

| Relacion | Tipo | Explicacion |
| --- | --- | --- |
| `Usuario` - `Cliente` | `OneToOne` | Un usuario puede tener perfil de cliente. |
| `Usuario` - `Personal` | `OneToOne` | Un usuario tambien puede ser personal del hotel. |
| `Reserva` - `Cliente` | `ManyToOne` | Un cliente puede tener muchas reservas. |
| `Reserva` - `Habitacion` | `ManyToOne` | Una habitacion puede aparecer en varias reservas. |
| `Reserva` - `Personal` | `ManyToOne` | Un personal registra muchas reservas. |
| `Reserva` - `Pago` | `OneToMany` | Una reserva puede tener varios pagos. |
| `Pago` - `Reserva` | `ManyToOne` | Cada pago pertenece a una reserva. |

Ejemplo:

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "cliente_id", nullable = false)
private Cliente cliente;
```

Explicacion:

- Muchas reservas pueden pertenecer a un cliente.
- `cliente_id` sera la llave foranea en la tabla `reservas`.

Relacion de pagos:

```java
@OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER)
private List<Pago> pagos = new ArrayList<>();
```

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "reserva_id")
private Reserva reserva;
```

Idea para decir:

> Antes una reserva dependia directamente de un pago. Ahora una reserva puede existir sin pago y los pagos se asocian despues mediante `reserva_id`.

## 7. Repositorios JPA

Los repositorios evitan escribir SQL manual para operaciones comunes.

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

Metodos heredados de `JpaRepository`:

- `findAll()`: listar.
- `findById(id)`: buscar por ID.
- `save(entity)`: crear o actualizar.
- `deleteById(id)`: eliminar.

Ejemplo de pagos:

```java
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReferencia(String referencia);
    List<Pago> findByReserva_Id(Long reservaId);
}
```

Spring interpreta el nombre del metodo y genera la consulta automaticamente.

## 8. Autenticacion unificada

Se agrego la entidad `Usuario` para centralizar credenciales.

El sistema permite:

- Registrar cliente.
- Registrar personal.
- Iniciar sesion desde una sola ruta.
- Devolver un JWT para usar rutas protegidas.

Rutas:

```http
POST /api/auth/register/cliente
POST /api/auth/register/personal
POST /api/auth/login
```

En el registro de cliente:

```java
usuario.setEmail(request.getEmail());
usuario.setPassword(passwordEncoder.encode(request.getPassword()));
usuario.setRol(RolUsuario.CLIENTE);
usuario.setTipoUsuario(TipoUsuario.CLIENTE);
usuario.setCliente(cliente);
```

Explicacion:

- Se guarda la clave cifrada con BCrypt.
- Se asigna el rol `CLIENTE`.
- Se relaciona el usuario con su perfil de cliente.

En login:

```java
if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
    throw new IllegalArgumentException("Credenciales invalidas");
}
```

Esto compara la clave ingresada con la clave cifrada.

## 9. JWT

JWT permite autenticar sin guardar sesion en el servidor.

Flujo:

1. El usuario inicia sesion.
2. El backend genera un token.
3. El cliente guarda el token.
4. En cada ruta protegida envia:

```http
Authorization: Bearer <token>
```

Generacion del token:

```java
claims.put("rol", usuario.getRol().name());
claims.put("tipoUsuario", usuario.getTipoUsuario().name());
claims.put("usuarioId", usuario.getId());

return Jwts.builder()
        .claims(claims)
        .subject(usuario.getEmail())
        .issuedAt(ahora)
        .expiration(expiracion)
        .signWith(getSigningKey())
        .compact();
```

El token contiene:

- Email del usuario.
- Rol.
- Tipo de usuario.
- ID del usuario.
- Fecha de vencimiento.

Validacion:

```java
public boolean tokenValido(String token, String email) {
    String subject = obtenerEmail(token);
    return subject.equals(email) && !estaExpirado(token);
}
```

## 10. Spring Security y permisos

La seguridad se configura en `SecurityConfig`.

```java
return http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reservas/mis-reservas").hasRole("CLIENTE")
                .requestMatchers(HttpMethod.POST, "/api/reservas/con-pago").hasAnyRole("ADMINISTRADOR", "CAJERO")
                .anyRequest().hasRole("ADMINISTRADOR")
        )
        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
```

Explicacion:

- `/api/auth/**` es publico.
- Las demas rutas requieren token.
- `CLIENTE` puede gestionar sus propias reservas.
- `CAJERO` gestiona operacion interna.
- `ADMINISTRADOR` tiene acceso completo.

Tabla resumida:

| Rol | Permisos principales |
| --- | --- |
| `CLIENTE` | Ver catalogo y crear/ver sus reservas. |
| `CAJERO` | Gestionar clientes, reservas y pagos. |
| `ADMINISTRADOR` | Control completo de la API. |

## 11. Filtro JWT

El filtro lee el token antes de que la peticion llegue al controlador.

```java
String authorizationHeader = request.getHeader("Authorization");

if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}

String token = authorizationHeader.substring("Bearer ".length());
```

Luego carga el rol en Spring Security:

```java
List<SimpleGrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

SecurityContextHolder.getContext().setAuthentication(authentication);
```

Idea para decir:

> El filtro convierte el token en una autenticacion real dentro de Spring Security.

## 12. Reservas con pagos

Este fue uno de los cambios mas recientes.

Antes:

- La reserva necesitaba un `pagoId`.
- No era practico crear una reserva pendiente.

Ahora:

- La reserva puede crearse sin pago y quedar `PENDIENTE`.
- El pago se relaciona con `reserva_id`.
- Tambien se puede crear reserva y pago en una sola peticion.

DTO nuevo:

```java
public class ReservaConPagoRequest {
    private Reserva reserva;
    private Pago pago;
}
```

Endpoint interno:

```http
POST /api/reservas/con-pago
```

Endpoint del cliente:

```http
POST /api/reservas/mis-reservas/con-pago
```

Metodo principal:

```java
@Transactional
public Reserva crearConPago(ReservaConPagoRequest request) {
    validarReservaConPago(request);
    Reserva reserva = crear(request.getReserva());
    Pago pago = pagoService.crearParaReserva(request.getPago(), reserva);
    actualizarEstadoPorPago(reserva, pago);
    return reservaRepository.save(reserva);
}
```

Explicacion:

- `@Transactional`: si falla la reserva o el pago, se revierte todo.
- Primero se crea la reserva.
- Luego se crea el pago asociado.
- Si el pago esta `PAGADO`, la reserva queda `CONFIRMADA`.
- Si no, queda `PENDIENTE`.

## 13. Reglas de pago

```java
private void completarDatosPorDefecto(Pago pago) {
    if (pago.getEstado() == null) {
        pago.setEstado(EstadoPago.PAGADO);
    }
    pago.setMoneda(normalizarMoneda(pago.getMoneda()));
    if (pago.getFechaRegistro() == null) {
        pago.setFechaRegistro(LocalDateTime.now());
    }
}
```

Reglas:

- Si no se envia estado, queda `PAGADO`.
- Si no se envia moneda, queda `PEN`.
- Si no se envia fecha de registro, se usa la fecha actual.

Al eliminar un pago asociado:

```java
if (pago.getReservaId() != null) {
    pago.setEstado(EstadoPago.ANULADO);
    pagoRepository.save(pago);
    return true;
}
```

No se borra fisicamente; se anula para conservar historial.

## 14. Demo recomendada para la exposicion

Orden sugerido para mostrar en Postman:

1. Registrar un cliente.
2. Iniciar sesion.
3. Copiar el token JWT.
4. Enviar el token en `Authorization: Bearer <token>`.
5. Crear una reserva como cliente.
6. Crear una reserva con pago.
7. Consultar pagos de una reserva.
8. Intentar acceder a una ruta sin token para mostrar el `401`.
9. Intentar acceder con rol incorrecto para mostrar el `403`.

Ejemplo de login:

```json
{
  "email": "ana@correo.com",
  "password": "123456"
}
```

Ejemplo de reserva con pago:

```json
{
  "reserva": {
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
    "moneda": "PEN"
  }
}
```

## 15. Cierre

Para cerrar la exposicion:

> En resumen, este avance integra persistencia con PostgreSQL, relaciones con JPA, autenticacion con JWT y autorizacion por roles con Spring Security. Ademas, el flujo de reservas se hizo mas realista porque ahora permite reservas pendientes y pagos asociados a una reserva.

Puntos fuertes del avance:

- Backend con base de datos real.
- Seguridad por token.
- Roles diferenciados.
- Relaciones entre entidades.
- Flujo de reservas y pagos mas flexible.
- Codigo separado por capas.
