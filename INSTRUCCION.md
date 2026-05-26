# INSTRUCCION - Segundo avance del backend Dubai

Este documento resume lo nuevo integrado desde ayer, 24 de mayo de 2026, para explicar el avance en la exposicion: Spring Security, autenticacion con JWT, JPA con PostgreSQL, relaciones entre entidades y el nuevo flujo de reservas con pagos.

## 1. Resumen de lo nuevo

En este avance el proyecto dejo de funcionar como una API simple y paso a tener una arquitectura mas completa:

- Se integro PostgreSQL como base de datos.
- Se agrego Spring Data JPA para mapear clases Java a tablas.
- Se crearon repositorios `JpaRepository` para consultar y guardar datos.
- Se agrego Spring Security para proteger rutas.
- Se implemento autenticacion con JWT.
- Se agregaron roles: `CLIENTE`, `ADMINISTRADOR` y `CAJERO`.
- Se centralizo el login con la entidad `Usuario`.
- Se agregaron relaciones reales entre entidades como `Reserva`, `Cliente`, `Habitacion`, `Personal` y `Pago`.
- En los ultimos cambios locales, las reservas ya pueden crearse sin pago o con pago incluido.

## 2. Dependencias agregadas

Las dependencias principales estan en `pom.xml`.

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
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```

Que hace cada una:

- `spring-boot-starter-data-jpa`: permite usar entidades, relaciones y repositorios.
- `spring-boot-starter-security`: permite proteger endpoints y manejar permisos.
- `postgresql`: conecta Spring Boot con la base de datos PostgreSQL.
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: permiten crear, firmar y validar tokens JWT.

## 3. Conexion a PostgreSQL

La configuracion esta en `application.properties`.

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Explicacion corta:

- `spring.datasource.url`: ruta de conexion a PostgreSQL.
- `spring.datasource.username`: usuario de la base de datos.
- `spring.datasource.password`: clave de la base de datos.
- `ddl-auto=update`: Hibernate actualiza las tablas segun las entidades.
- `show-sql=true`: muestra las consultas SQL en consola.
- `format_sql=true`: imprime las consultas SQL mas ordenadas.

Tambien se usa `.env` para no quemar credenciales directamente en el codigo.

## 4. JPA: entidades y tablas

JPA permite convertir una clase Java en una tabla de la base de datos.

Ejemplo en `Reserva`:

```java
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

Que significa:

- `@Entity`: esta clase sera una tabla.
- `@Table(name = "reservas")`: la tabla se llamara `reservas`.
- `@Id`: campo que sera clave primaria.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: PostgreSQL genera el ID automaticamente.

Ejemplo de enum guardado como texto:

```java
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private EstadoReserva estado;
```

Esto guarda valores como `PENDIENTE`, `CONFIRMADA` o `CANCELADA` como texto, no como numeros. Es mejor para leer la base de datos.

## 5. Relaciones entre entidades

Las relaciones principales son:

| Entidad | Relacion | Explicacion |
| --- | --- | --- |
| `Usuario` - `Cliente` | `@OneToOne` | Un usuario puede estar asociado a un cliente. |
| `Usuario` - `Personal` | `@OneToOne` | Un usuario tambien puede representar personal del hotel. |
| `Reserva` - `Cliente` | `@ManyToOne` | Un cliente puede tener muchas reservas. |
| `Reserva` - `Habitacion` | `@ManyToOne` | Una habitacion puede aparecer en muchas reservas. |
| `Reserva` - `Personal` | `@ManyToOne` | Un personal puede registrar muchas reservas. |
| `Reserva` - `Pago` | `@OneToMany` | Una reserva puede tener uno o varios pagos. |
| `Pago` - `Reserva` | `@ManyToOne` | Cada pago pertenece a una reserva. |
| `TipoHabitacion` - `Caracteristica` | `@ManyToMany` | Un tipo de habitacion puede tener varias caracteristicas. |

Ejemplo de muchos a uno en `Reserva`:

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "cliente_id", nullable = false)
private Cliente cliente;
```

Explicacion:

- Muchas reservas pueden pertenecer a un mismo cliente.
- `cliente_id` sera la columna foreign key en la tabla `reservas`.
- `nullable = false` obliga a que una reserva tenga cliente.

Ejemplo de uno a muchos entre reserva y pagos:

```java
@OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER)
private List<Pago> pagos = new ArrayList<>();
```

Explicacion:

- Una reserva puede tener varios pagos.
- `mappedBy = "reserva"` indica que la relacion se controla desde la entidad `Pago`.

Y en `Pago`:

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "reserva_id")
private Reserva reserva;
```

Esto crea la columna `reserva_id` en la tabla `pagos`.

## 6. Repositorios con Spring Data JPA

Los repositorios permiten hacer consultas sin escribir SQL manual.

Ejemplo:

```java
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

Explicacion:

- `JpaRepository<Usuario, Long>` ya trae metodos como `findAll`, `findById`, `save` y `deleteById`.
- `findByEmail` busca un usuario por correo.
- `existsByEmail` verifica si ya existe un correo.

Ejemplo usado en pagos:

```java
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReferencia(String referencia);
    List<Pago> findByReserva_Id(Long reservaId);
    boolean existsByReserva_Id(Long reservaId);
}
```

Explicacion:

- `findByReferencia`: busca un pago por su codigo o referencia.
- `findByReserva_Id`: lista pagos asociados a una reserva.
- `existsByReserva_Id`: verifica si una reserva ya tiene pagos asociados.

Spring Data entiende el nombre del metodo y arma la consulta automaticamente.

## 7. Spring Security

La seguridad se configura en `SecurityConfig`.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
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
}
```

Explicacion:

- `csrf().disable()`: se desactiva porque la API usa tokens, no sesiones web tradicionales.
- `SessionCreationPolicy.STATELESS`: el servidor no guarda sesion; cada request debe traer token.
- `/api/auth/**`: rutas publicas para registro y login.
- `hasRole("CLIENTE")`: solo clientes pueden entrar.
- `hasAnyRole("ADMINISTRADOR", "CAJERO")`: permite mas de un rol.
- `addFilterBefore(...)`: ejecuta el filtro JWT antes del filtro normal de login de Spring.

Tambien se agrego un encoder de contrasenas:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

Esto permite guardar contrasenas cifradas con BCrypt.

## 8. Autenticacion con JWT

El flujo es:

1. El usuario se registra o inicia sesion.
2. El backend valida email y password.
3. Si son correctos, se genera un token JWT.
4. El frontend o Postman envia ese token en cada request protegida.
5. El filtro JWT valida el token y carga el rol del usuario.

### Registro y login

Rutas principales:

```http
POST /api/auth/register/cliente
POST /api/auth/register/personal
POST /api/auth/login
```

Ejemplo de login:

```json
{
  "email": "cliente@correo.com",
  "password": "123456"
}
```

Respuesta esperada:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "email": "cliente@correo.com",
  "rol": "CLIENTE",
  "tipoUsuario": "CLIENTE"
}
```

### Como se genera el token

Fragmento de `JwtService`:

```java
public String generarToken(Usuario usuario) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("rol", usuario.getRol().name());
    claims.put("tipoUsuario", usuario.getTipoUsuario().name());
    claims.put("usuarioId", usuario.getId());

    return Jwts.builder()
            .claims(claims)
            .subject(usuario.getEmail())
            .issuedAt(new Date())
            .expiration(expiracion)
            .signWith(getSigningKey())
            .compact();
}
```

Explicacion:

- `claims`: datos extra dentro del token.
- `subject`: se usa el email como identificador principal.
- `issuedAt`: fecha de emision.
- `expiration`: fecha de vencimiento.
- `signWith`: firma el token con una clave secreta.
- `compact`: convierte todo a un String JWT.

### Como se valida el token

Fragmento:

```java
public boolean tokenValido(String token, String email) {
    String subject = obtenerEmail(token);
    return subject.equals(email) && !estaExpirado(token);
}
```

Explicacion:

- Obtiene el email guardado en el token.
- Compara que sea el mismo email del usuario.
- Verifica que el token no este vencido.

## 9. Filtro JWT

El filtro `JWTAuthorizationFilter` revisa cada request protegida.

```java
String authorizationHeader = request.getHeader("Authorization");

if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}

String token = authorizationHeader.substring("Bearer ".length());
```

Explicacion:

- Lee el header `Authorization`.
- Verifica que empiece con `Bearer`.
- Extrae el token quitando la palabra `Bearer`.

Luego autentica al usuario:

```java
List<SimpleGrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(usuario.getEmail(), null, authorities);

SecurityContextHolder.getContext().setAuthentication(authentication);
```

Explicacion:

- Convierte el rol del usuario en autoridad de Spring Security.
- Guarda la autenticacion en el contexto de seguridad.
- Desde ese momento Spring sabe quien hizo la request y que rol tiene.

## 10. Servicio de autenticacion

En `AuthService`, el registro de cliente crea dos cosas: el perfil `Cliente` y el `Usuario`.

```java
Cliente cliente = new Cliente(null, request.getNombres(), request.getApellidos(),
        request.getDocumento(), request.getTelefono(), request.getEmail());

Usuario usuario = new Usuario();
usuario.setEmail(request.getEmail());
usuario.setPassword(passwordEncoder.encode(request.getPassword()));
usuario.setRol(RolUsuario.CLIENTE);
usuario.setTipoUsuario(TipoUsuario.CLIENTE);
usuario.setCliente(cliente);

return construirRespuesta(usuarioRepository.save(usuario));
```

Explicacion:

- Se crea el cliente con sus datos personales.
- Se crea el usuario con email y password cifrado.
- Se asigna el rol `CLIENTE`.
- Al guardar el usuario tambien se guarda el cliente por la relacion `cascade`.
- Al final se devuelve el token.

En el login:

```java
Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
    throw new IllegalArgumentException("Credenciales invalidas");
}
```

Explicacion:

- Busca el usuario por email.
- Compara la contrasena ingresada con la contrasena cifrada.
- Si coincide, genera el JWT.

## 11. Ultimo cambio importante: reservas con pago

Antes una reserva necesitaba `pagoId`. Ahora la reserva puede crearse sin pago y quedar `PENDIENTE`. El pago se relaciona despues con `reserva_id`, o se puede crear todo junto.

Nuevo DTO:

```java
public class ReservaConPagoRequest {
    private Reserva reserva;
    private Pago pago;
}
```

Sirve para recibir en un solo JSON los datos de reserva y pago.

Endpoint interno:

```http
POST /api/reservas/con-pago
Authorization: Bearer <token-admin-o-cajero>
```

Endpoint para cliente:

```http
POST /api/reservas/mis-reservas/con-pago
Authorization: Bearer <token-cliente>
```

Ejemplo de cuerpo:

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

Metodo principal en `ReservaService`:

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

- `@Transactional`: si falla la reserva o el pago, se revierte toda la operacion.
- `validarReservaConPago`: valida que venga reserva y pago.
- `crear`: guarda primero la reserva.
- `crearParaReserva`: guarda el pago asociado a esa reserva.
- `actualizarEstadoPorPago`: si el pago esta `PAGADO`, la reserva pasa a `CONFIRMADA`; si no, queda `PENDIENTE`.

## 12. Metodos importantes de pagos

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

Explicacion:

- Si no se envia estado, el pago queda como `PAGADO`.
- Si no se envia moneda, se usa `PEN`.
- Si no se envia fecha de registro, se usa la fecha actual.

```java
public boolean eliminar(Long id) {
    Pago pago = pagoRepository.findById(id).orElse(null);
    if (pago == null) {
        return false;
    }
    if (pago.getReservaId() != null) {
        pago.setEstado(EstadoPago.ANULADO);
        pagoRepository.save(pago);
        return true;
    }
    pagoRepository.deleteById(id);
    return true;
}
```

Explicacion:

- Si el pago esta asociado a una reserva, no se borra fisicamente.
- Se cambia a estado `ANULADO`.
- Esto conserva el historial de la reserva.

## 13. Permisos por rol

Resumen para la exposicion:

| Rol | Que puede hacer |
| --- | --- |
| `CLIENTE` | Ver catalogo, ver sus reservas y crear sus propias reservas. |
| `CAJERO` | Gestionar clientes, reservas y pagos. |
| `ADMINISTRADOR` | Acceso administrativo completo. |

Ejemplo en seguridad:

```java
.requestMatchers(HttpMethod.POST, "/api/reservas/mis-reservas/con-pago").hasRole("CLIENTE")
.requestMatchers(HttpMethod.POST, "/api/reservas/con-pago").hasAnyRole("ADMINISTRADOR", "CAJERO")
```

Esto separa el flujo del cliente del flujo interno del hotel.

## 14. Ejemplo de flujo completo para mostrar en exposicion

### Paso 1: registrar cliente

```http
POST /api/auth/register/cliente
```

```json
{
  "nombres": "Ana",
  "apellidos": "Torres",
  "documento": "12345678",
  "telefono": "999888777",
  "email": "ana@correo.com",
  "password": "123456"
}
```

### Paso 2: iniciar sesion

```http
POST /api/auth/login
```

```json
{
  "email": "ana@correo.com",
  "password": "123456"
}
```

### Paso 3: usar el token

```http
Authorization: Bearer <token>
```

### Paso 4: crear una reserva propia

```http
POST /api/reservas/mis-reservas
Authorization: Bearer <token-cliente>
```

```json
{
  "habitacionId": 1,
  "personalId": 1,
  "fechaIngreso": "2026-05-10",
  "fechaSalida": "2026-05-12"
}
```

### Paso 5: consultar pagos de una reserva

```http
GET /api/reservas/1/pagos
Authorization: Bearer <token-admin-o-cajero>
```

## 15. Idea corta para explicar en clase

La idea principal del avance es que el backend ya trabaja como una API real de hotel. Ahora los datos se guardan en PostgreSQL usando JPA, las entidades tienen relaciones entre si, los usuarios se autentican con JWT, las rutas estan protegidas por roles y las reservas pueden manejar pagos de forma mas flexible.

Antes la aplicacion era mas directa. Ahora tiene capas claras:

- Controlador: recibe la peticion HTTP.
- Servicio: valida reglas de negocio.
- Repositorio: consulta o guarda en PostgreSQL.
- Modelo: representa las tablas y relaciones.
- Seguridad: valida el token y permisos.

Esta separacion permite que el sistema sea mas ordenado, seguro y facil de ampliar.
