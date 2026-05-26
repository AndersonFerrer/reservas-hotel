# EXPOSICION - Segundo avance del backend Dubai

Duracion maxima: 10 minutos  
Participantes: 4 personas  
Objetivo: explicar de forma ordenada las nuevas integraciones del backend: PostgreSQL, JPA, Spring Security, JWT, roles, relaciones y reservas con pagos.

## Distribucion de tiempo

| Persona | Tema | Tiempo sugerido |
| --- | --- | --- |
| Persona 1 | Introduccion, arquitectura y dependencias | 2 min |
| Persona 2 | PostgreSQL, JPA, entidades y relaciones | 2 min 30 s |
| Persona 3 | Autenticacion, JWT y Spring Security | 2 min 30 s |
| Persona 4 | Reservas con pagos, demo y cierre | 3 min |

## Persona 1 - Introduccion y arquitectura

Buenos dias. En este segundo avance presentamos la evolucion del backend del sistema Dubai, orientado a la gestion hotelera.

En el primer avance teniamos una API mas basica. Ahora el backend ya incorpora elementos mas cercanos a una aplicacion real: persistencia con base de datos, seguridad, autenticacion por token, roles de usuario y relaciones entre entidades.

Las principales integraciones son:

- PostgreSQL como base de datos.
- Spring Data JPA para trabajar con entidades y repositorios.
- Spring Security para proteger los endpoints.
- JWT para autenticar usuarios.
- Roles como `CLIENTE`, `CAJERO` y `ADMINISTRADOR`.
- Nuevas relaciones entre clientes, habitaciones, reservas, personal y pagos.

La arquitectura quedo organizada por capas:

```text
Cliente o Postman -> Controller -> Service -> Repository -> PostgreSQL
```

Los `controllers` reciben las peticiones HTTP.  
Los `services` contienen la logica y validaciones.  
Los `repositories` se encargan de consultar la base de datos.  
Los `models` representan las tablas.  
Y la capa `security` se encarga de validar tokens y permisos.

Tambien agregamos dependencias importantes en `pom.xml`, como:

```xml
spring-boot-starter-data-jpa
spring-boot-starter-security
postgresql
jjwt
```

Con esto, el proyecto deja de depender de datos temporales y empieza a comportarse como una API backend mas completa, segura y mantenible.

## Persona 2 - PostgreSQL, JPA y relaciones

Continuando con la parte de persistencia, ahora el sistema usa PostgreSQL como base de datos.

La conexion se configura desde `application.properties`, usando variables de entorno:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
```

Esto permite que las credenciales no esten escritas directamente en el codigo. Ademas, `ddl-auto=update` permite que Hibernate actualice la estructura de tablas segun las entidades creadas en Java.

Para trabajar con la base de datos usamos JPA. Por ejemplo, una clase se convierte en tabla usando anotaciones como:

```java
@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

`@Entity` indica que la clase sera una tabla.  
`@Table` define el nombre de esa tabla.  
`@Id` indica la clave primaria.  
Y `@GeneratedValue` permite que PostgreSQL genere el identificador automaticamente.

Tambien usamos enums para representar estados. Por ejemplo:

```java
@Enumerated(EnumType.STRING)
private EstadoReserva estado;
```

Esto permite guardar valores como `PENDIENTE`, `CONFIRMADA` o `CANCELADA` de forma legible.

Otra parte importante son las relaciones entre entidades. Por ejemplo, una reserva pertenece a un cliente:

```java
@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "cliente_id", nullable = false)
private Cliente cliente;
```

Esto significa que un cliente puede tener muchas reservas, pero cada reserva pertenece a un cliente.

Tambien se agrego una relacion entre reservas y pagos. Ahora una reserva puede tener varios pagos:

```java
@OneToMany(mappedBy = "reserva")
private List<Pago> pagos = new ArrayList<>();
```

Y cada pago pertenece a una reserva:

```java
@ManyToOne
@JoinColumn(name = "reserva_id")
private Reserva reserva;
```

Finalmente, para consultar la base de datos usamos repositorios:

```java
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByReserva_Id(Long reservaId);
}
```

`JpaRepository` ya nos da metodos como `findAll`, `findById`, `save` y `deleteById`. Ademas, Spring puede crear consultas automaticamente segun el nombre del metodo.

## Persona 3 - Autenticacion, JWT y seguridad

Ahora explicamos la parte de autenticacion y seguridad.

Se creo una entidad `Usuario` para centralizar las credenciales. Esto permite que tanto clientes como personal del hotel puedan iniciar sesion desde una misma ruta.

Las rutas principales son:

```http
POST /api/auth/register/cliente
POST /api/auth/register/personal
POST /api/auth/login
```

Cuando se registra un cliente, se crea su perfil y tambien su usuario. La contrasena no se guarda en texto plano, sino cifrada con BCrypt:

```java
usuario.setPassword(passwordEncoder.encode(request.getPassword()));
usuario.setRol(RolUsuario.CLIENTE);
usuario.setTipoUsuario(TipoUsuario.CLIENTE);
```

En el login, se compara la contrasena enviada con la contrasena cifrada:

```java
passwordEncoder.matches(request.getPassword(), usuario.getPassword())
```

Si las credenciales son correctas, el backend genera un JWT.

El JWT es un token que el cliente debe enviar en cada peticion protegida:

```http
Authorization: Bearer <token>
```

El token contiene informacion como:

- Email del usuario.
- Rol.
- Tipo de usuario.
- ID del usuario.
- Fecha de expiracion.

Una parte del metodo que genera el token es:

```java
claims.put("rol", usuario.getRol().name());
claims.put("tipoUsuario", usuario.getTipoUsuario().name());
claims.put("usuarioId", usuario.getId());
```

Luego el token se firma con una clave secreta:

```java
.signWith(getSigningKey())
.compact();
```

Para proteger las rutas usamos Spring Security. La configuracion principal indica que las rutas de autenticacion son publicas, pero las demas necesitan token:

```java
.requestMatchers("/api/auth/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/reservas/mis-reservas").hasRole("CLIENTE")
.requestMatchers(HttpMethod.POST, "/api/reservas/con-pago").hasAnyRole("ADMINISTRADOR", "CAJERO")
```

Los roles quedaron asi:

- `CLIENTE`: puede ver catalogo y gestionar sus propias reservas.
- `CAJERO`: puede gestionar operacion interna, reservas y pagos.
- `ADMINISTRADOR`: tiene acceso completo.

Tambien existe un filtro JWT. Este filtro lee el header `Authorization`, valida el token y carga el usuario autenticado dentro de Spring Security. Asi, antes de llegar al controlador, el sistema ya sabe quien hizo la peticion y que permisos tiene.

## Persona 4 - Reservas con pagos, demo y cierre

Para finalizar, explicamos el cambio mas reciente en el flujo de reservas y pagos.

Antes, una reserva necesitaba obligatoriamente un `pagoId`. Esto no era tan flexible, porque en un hotel puede existir una reserva pendiente de pago.

Ahora el modelo cambio:

- Una reserva puede crearse sin pago y quedar en estado `PENDIENTE`.
- Un pago se asocia despues usando `reserva_id`.
- Tambien se puede crear una reserva con pago en una sola peticion.

Para ese flujo se creo el DTO `ReservaConPagoRequest`:

```java
public class ReservaConPagoRequest {
    private Reserva reserva;
    private Pago pago;
}
```

Este DTO recibe en un mismo JSON la informacion de la reserva y la informacion del pago.

El metodo principal es:

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

`@Transactional` es importante porque si falla la reserva o falla el pago, se revierte toda la operacion. Asi evitamos datos incompletos.

Tambien se agregaron reglas por defecto para pagos:

- Si no se envia estado, queda como `PAGADO`.
- Si no se envia moneda, se usa `PEN`.
- Si se elimina un pago asociado a una reserva, no se borra fisicamente; se cambia a `ANULADO`.

Para la demo, el orden recomendado es:

1. Registrar un cliente.
2. Iniciar sesion.
3. Copiar el token JWT.
4. Usar el token en `Authorization: Bearer <token>`.
5. Crear una reserva como cliente.
6. Crear una reserva con pago.
7. Consultar los pagos de una reserva.
8. Mostrar que sin token responde `401`.
9. Mostrar que con rol incorrecto responde `403`.

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

Como cierre, podemos decir:

En resumen, este avance convierte el backend en una API mas realista. Ahora tenemos persistencia con PostgreSQL, entidades relacionadas con JPA, autenticacion con JWT, permisos por roles usando Spring Security y un flujo de reservas con pagos mucho mas flexible para el funcionamiento de un hotel.

## Cierre grupal corto

Con este segundo avance, el sistema Dubai ya cuenta con una base mas solida para seguir creciendo. La separacion por capas permite mantener mejor el codigo, la seguridad protege los endpoints y las relaciones entre entidades representan mejor el negocio hotelero.
