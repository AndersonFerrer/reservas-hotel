# API Sistema de Reservas - Dubai

Proyecto Spring Boot para gestionar reservas de habitaciones con datos estaticos en memoria.

## 1. Tecnologias

- Java 21+
- Spring Boot 3.5.x
- Maven
- JUnit 5 + Mockito

## 2. Estructura del Proyecto

```text
src/main/java/com/dubai/dubai/
├── controllers/   # Endpoints REST
├── services/      # Reglas simples, validaciones y datos estaticos
└── models/        # Modelos de dominio (POJOs + enums)

src/test/java/com/dubai/dubai/
└── controllers/   # Tests unitarios simples de controladores
```

## 3. Modelos y Enums

### Modelos principales

- `Cliente`
- `TipoHabitacion`
- `Habitacion`
- `Personal`
- `Pago`
- `Reserva`
- `Caracteristica`
- `Calificacion`
- `Cupon`
- `HabitacionCaracteristica`

### Enums

- `EstadoHabitacion`: `DISPONIBLE`, `OCUPADA`, `MANTENIMIENTO`
- `RolPersonal`: `ADMINISTRADOR`, `CAJERO`
- `MetodoPago`: `EFECTIVO`, `YAPE`, `PLIN`, `TRANSFERENCIA`, `TARJETA`
- `EstadoReserva`: `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `FINALIZADA`

## 4. Levantar la API

### Opcion 1

```bash
mvn spring-boot:run
```

### Opcion 2

```bash
./mvnw spring-boot:run
```

Base URL local:

```text
http://localhost:8080
```

## 5. Documentacion de Endpoints

Todos los endpoints devuelven JSON.

### 5.1 Clientes

- `GET /api/clientes` -> lista de clientes
- `GET /api/clientes/{id}` -> cliente por id (`404` si no existe)

### 5.2 Tipos de habitacion

- `GET /api/tipos-habitacion` -> lista de tipos
- `GET /api/tipos-habitacion/{id}` -> tipo por id (`404` si no existe)

### 5.3 Habitaciones

- `GET /api/habitaciones` -> lista de habitaciones
- `GET /api/habitaciones/{id}` -> habitacion por id (`404` si no existe)

### 5.4 Personal

- `GET /api/personal` -> lista de personal
- `GET /api/personal/{id}` -> personal por id (`404` si no existe)

### 5.5 Pagos

- `GET /api/pagos` -> lista de pagos
- `GET /api/pagos/{id}` -> pago por id (`404` si no existe)

### 5.6 Caracteristicas

- `GET /api/caracteristicas` -> lista de caracteristicas
- `GET /api/caracteristicas/{id}` -> caracteristica por id (`404` si no existe)

### 5.7 Calificaciones

- `GET /api/calificaciones` -> lista de calificaciones
- `GET /api/calificaciones/{id}` -> calificacion por id (`404` si no existe)

### 5.8 Cupones

- `GET /api/cupones` -> lista de cupones
- `GET /api/cupones/{id}` -> cupon por id (`404` si no existe)

### 5.9 Relacion Habitacion-Caracteristicas

- `GET /api/habitacion-caracteristicas` -> lista de relaciones
- `GET /api/habitacion-caracteristicas/{id}` -> relacion por id (`404` si no existe)

### 5.10 Reservas

- `GET /api/reservas` -> lista de reservas
- `GET /api/reservas/{id}` -> reserva por id (`404` si no existe)
- `POST /api/reservas` -> crea reserva, valida datos y calcula noches

#### Request ejemplo `POST /api/reservas`

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

#### Response exitosa (`201`)

```json
{
  "mensaje": "Reserva creada correctamente",
  "noches": 2,
  "reserva": {
    "id": 3,
    "clienteId": 1,
    "habitacionId": 1,
    "pagoId": 1,
    "personalId": 1,
    "fechaIngreso": "2026-05-10",
    "fechaSalida": "2026-05-12",
    "estado": "PENDIENTE"
  }
}
```

#### Response error validacion (`400`)

```json
{
  "error": "La fecha de salida debe ser posterior a la fecha de ingreso"
}
```

## 6. Reglas Actuales (Servicios)

- Los servicios trabajan con listas en memoria (sin base de datos).
- Los controladores solo delegan en servicios y responden HTTP.
- `ReservaService` valida:
- `clienteId`, `habitacionId`, `pagoId`, `personalId` obligatorios.
- `fechaIngreso` y `fechaSalida` obligatorias.
- `fechaSalida` debe ser posterior a `fechaIngreso`.
- Se calcula cantidad de noches al crear reserva.

## 7. Tests

### 7.1 Enfoque usado

Tests unitarios simples de controladores:

- `@ExtendWith(MockitoExtension.class)`
- `@Mock` para servicios
- `@InjectMocks` para el controlador (`sut`)
- `doReturn(...).when(...)` para stubbing
- `assertEquals(...)` para validaciones
- `verify(...)` para comprobar llamadas al servicio

### 7.2 Tests disponibles

- `ClienteControllerTest`
- `TipoHabitacionControllerTest`
- `HabitacionControllerTest`
- `PersonalControllerTest`
- `PagoControllerTest`
- `ReservaControllerTest`
- `CaracteristicaControllerTest`
- `CalificacionControllerTest`
- `CuponControllerTest`
- `HabitacionCaracteristicaControllerTest`

### 7.3 Ejecutar tests

Ejecutar toda la suite:

```bash
mvn test
```

Ejecutar solo tests de controladores:

```bash
mvn -Dtest='*ControllerTest' test
```
