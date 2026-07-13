package com.dubai.dubai.services;

import com.dubai.dubai.dto.HabitacionDisponibleResponse;
import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.models.EstadoHabitacion;
import com.dubai.dubai.models.Habitacion;
import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.repositories.HabitacionRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class HabitacionServiceListarDisponiblesTest {

    @Mock
    private HabitacionRepository habitacionRepository;

    @Mock
    private TipoHabitacionRepository tipoHabitacionRepository;

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private HabitacionService sut;

    private static final LocalDate INGRESO = LocalDate.of(2026, 8, 1);
    private static final LocalDate SALIDA = LocalDate.of(2026, 8, 5);

    @Test
    void listarDisponibles_dosTiposConHabitaciones_devuelveRespuestaAgrupada() {
        TipoHabitacion suite = tipo(1L, "Suite Presidencial", 220.5, 4,
                "Suite presidencial completamente amplia con todas las comodidades a la mano",
                "jacuzzi", "tv 50");
        TipoHabitacion estandar = tipo(2L, "Estandar", 90.0, 2,
                "Habitacion estandar confortable",
                "wifi");

        Habitacion h1 = habitacion(1L, "A01", suite, EstadoHabitacion.DISPONIBLE);
        Habitacion h2 = habitacion(2L, "A02", suite, EstadoHabitacion.DISPONIBLE);
        Habitacion h3 = habitacion(3L, "A03", suite, EstadoHabitacion.DISPONIBLE);
        Habitacion h4 = habitacion(4L, "B01", estandar, EstadoHabitacion.DISPONIBLE);
        Habitacion h5 = habitacion(5L, "B02", estandar, EstadoHabitacion.DISPONIBLE);

        // Simula el resultado del query: 3 suites + 2 estandar, en ese orden
        doReturn(List.of(h1, h2, h3, h4, h5))
                .when(habitacionRepository)
                .findDisponibles(eq(INGRESO), eq(SALIDA), eq(2));

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(2, response.size());

        HabitacionDisponibleResponse r1 = response.get(0);
        assertEquals(1L, r1.tipoHabitacionId());
        assertEquals("Suite Presidencial", r1.nombre());
        assertEquals(220.5, r1.precioBase());
        assertEquals(4, r1.huespedes());
        assertEquals(3, r1.habitaciones().size());
        assertEquals(List.of(1L, 2L, 3L), r1.habitaciones().stream().map(HabitacionDisponibleResponse.HabitacionResumen::habitacionId).toList());
        assertEquals(List.of("A01", "A02", "A03"), r1.habitaciones().stream().map(HabitacionDisponibleResponse.HabitacionResumen::numero).toList());
        // caracteristicas ordenadas alfabeticamente
        assertEquals(List.of("jacuzzi", "tv 50"), r1.caracteristicas());

        HabitacionDisponibleResponse r2 = response.get(1);
        assertEquals(2L, r2.tipoHabitacionId());
        assertEquals("Estandar", r2.nombre());
        assertEquals(2, r2.habitaciones().size());

        verify(habitacionRepository).findDisponibles(INGRESO, SALIDA, 2);
    }

    @Test
    void listarDisponibles_huespedesMayoresQueCapacidadMaxima_retornaListaVacia() {
        // El repo simula el caso: ninguna habitacion cumple con la capacidad
        doReturn(List.<Habitacion>of())
                .when(habitacionRepository)
                .findDisponibles(eq(INGRESO), eq(SALIDA), eq(20));

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 20);

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }

    @Test
    void listarDisponibles_rangoConReservasPendienteYConfirmada_soloDevuelveHabitacionesSinReserva() {
        // Escenario: el repo ya filtro segun la SQL NOT EXISTS; devolvemos solo las disponibles
        TipoHabitacion estandar = tipo(2L, "Estandar", 90.0, 2, "Estandar confortable", "wifi");
        Habitacion libre = habitacion(10L, "B05", estandar, EstadoHabitacion.DISPONIBLE);

        doReturn(List.of(libre))
                .when(habitacionRepository)
                .findDisponibles(eq(INGRESO), eq(SALIDA), eq(2));

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(1, response.size());
        assertEquals(1, response.get(0).habitaciones().size());
        assertEquals(10L, response.get(0).habitaciones().get(0).habitacionId());
    }

    @Test
    void listarDisponibles_reservaCanceladaEnRango_noBloqueaDisponibilidad() {
        // Documenta el contrato: CANCELADA no entra en el IN (...) del NOT EXISTS,
        // por lo tanto el repo devuelve la habitacion y el service la retorna.
        TipoHabitacion estandar = tipo(2L, "Estandar", 90.0, 2, "Estandar confortable", "wifi");
        Habitacion h = habitacion(11L, "B11", estandar, EstadoHabitacion.DISPONIBLE);

        doReturn(List.of(h))
                .when(habitacionRepository)
                .findDisponibles(eq(INGRESO), eq(SALIDA), eq(2));

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(1, response.size());
        assertEquals(11L, response.get(0).habitaciones().get(0).habitacionId());
    }

    @Test
    void listarDisponibles_reservaFinalizadaEnRango_noBloqueaDisponibilidad() {
        // FINALIZADA tampoco entra en el IN del NOT EXISTS.
        TipoHabitacion estandar = tipo(2L, "Estandar", 90.0, 2, "Estandar confortable", "wifi");
        Habitacion h = habitacion(12L, "B12", estandar, EstadoHabitacion.DISPONIBLE);

        doReturn(List.of(h))
                .when(habitacionRepository)
                .findDisponibles(eq(INGRESO), eq(SALIDA), eq(2));

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(1, response.size());
        assertEquals(12L, response.get(0).habitaciones().get(0).habitacionId());
    }

    @Test
    void listarDisponibles_fechaSalidaIgualAFechaIngreso_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.listarDisponibles(INGRESO, INGRESO, 2));
        assertTrue(ex.getMessage().contains("fechaSalida"));
        verifyNoInteractions(habitacionRepository);
    }

    @Test
    void listarDisponibles_fechaSalidaAnteriorAFechaIngreso_lanzaExcepcion() {
        LocalDate salidaAntes = INGRESO.minusDays(1);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.listarDisponibles(INGRESO, salidaAntes, 2));
        assertTrue(ex.getMessage().contains("fechaSalida"));
        verifyNoInteractions(habitacionRepository);
    }

    @Test
    void listarDisponibles_huespedesMenorAUno_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.listarDisponibles(INGRESO, SALIDA, 0));
        assertTrue(ex.getMessage().contains("huespedes"));
        verifyNoInteractions(habitacionRepository);
    }

    @Test
    void listarDisponibles_huespedesNull_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.listarDisponibles(INGRESO, SALIDA, null));
        assertTrue(ex.getMessage().contains("huespedes"));
        verifyNoInteractions(habitacionRepository);
    }

    @Test
    void listarDisponibles_fechaIngresoNull_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.listarDisponibles(null, SALIDA, 2));
        assertTrue(ex.getMessage().contains("fechaIngreso"));
        verifyNoInteractions(habitacionRepository);
    }

    @Test
    void listarDisponibles_fechaSalidaNull_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> sut.listarDisponibles(INGRESO, null, 2));
        assertTrue(ex.getMessage().contains("fechaSalida"));
        verifyNoInteractions(habitacionRepository);
    }

    @Test
    void listarDisponibles_habitacionEnMantenimiento_excluidaPorElRepo() {
        // La exclusion de MANTENIMIENTO ocurre en la SQL con h.estado <> MANTENIMIENTO;
        // documentamos el contrato: el repo no la devuelve y el service solo mapea lo que llega.
        TipoHabitacion estandar = tipo(2L, "Estandar", 90.0, 2, "Estandar", "wifi");
        Habitacion disponible = habitacion(20L, "B20", estandar, EstadoHabitacion.DISPONIBLE);

        // el repo recibe el filtro ya aplicado y devuelve solo DISPONIBLES
        doReturn(List.of(disponible))
                .when(habitacionRepository)
                .findDisponibles(any(), any(), any());

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(1, response.size());
        assertEquals(20L, response.get(0).habitaciones().get(0).habitacionId());
    }

    @Test
    void listarDisponibles_habitacionOcupadaPeroSinReservasEnRango_seIncluye() {
        // Decisión confirmada: una Habitacion con estado OCUPADA se INCLUYE en la respuesta
        // siempre que NO tenga reservas PENDIENTE/CONFIRMADA que solapen con el rango solicitado.
        // La OCUPADA sin reservas activas es irrelevante para el calculo de disponibilidad
        // (la regla operacional es por reservas, no por estado).
        TipoHabitacion estandar = tipo(2L, "Estandar", 90.0, 2, "Estandar", "wifi");
        Habitacion h = habitacion(30L, "B30", estandar, EstadoHabitacion.OCUPADA);

        doReturn(List.of(h))
                .when(habitacionRepository)
                .findDisponibles(any(), any(), any());

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(1, response.size());
        assertEquals(30L, response.get(0).habitaciones().get(0).habitacionId());
    }

    @Test
    void listarDisponibles_agrupacionPorTipoHabitacion_mismoTipoAgrupaHabitaciones() {
        // Mismo tipoHabitacionId agrupa todas las habitaciones en un solo objeto del response,
        // mientras que tipos diferentes van en objetos separados.
        TipoHabitacion tipoA = tipo(1L, "Suite", 200.0, 2, "Desc A", "wifi");
        TipoHabitacion tipoB = tipo(2L, "Estandar", 90.0, 2, "Desc B", "tv");

        Habitacion h1 = habitacion(50L, "101", tipoA, EstadoHabitacion.DISPONIBLE);
        Habitacion h2 = habitacion(51L, "102", tipoA, EstadoHabitacion.DISPONIBLE);
        Habitacion h3 = habitacion(52L, "201", tipoB, EstadoHabitacion.DISPONIBLE);
        Habitacion h4 = habitacion(53L, "202", tipoB, EstadoHabitacion.DISPONIBLE);

        // El query devuelve 2 habitaciones del tipo A seguidas de 2 del tipo B,
        // lo que ejercita la rama computeIfAbsent de LinkedHashMap.
        doReturn(List.of(h1, h2, h3, h4))
                .when(habitacionRepository)
                .findDisponibles(any(), any(), any());

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(2, response.size());
        assertEquals(1L, response.get(0).tipoHabitacionId());
        assertEquals(2, response.get(0).habitaciones().size());
        assertEquals(2L, response.get(1).tipoHabitacionId());
        assertEquals(2, response.get(1).habitaciones().size());
    }

    @Test
    void listarDisponibles_ordenRespuesta_respetaOrdenPorTipoHabitacionId() {
        // El JPQL hace ORDER BY t.id ASC, h.numero ASC y el service preserva ese orden
        // porque itera secuencialmente e inserta en LinkedHashMap.
        TipoHabitacion tipoA = tipo(1L, "Tipo A", 100.0, 2, "Desc A", "wifi");
        TipoHabitacion tipoB = tipo(2L, "Tipo B", 200.0, 2, "Desc B", "tv");

        Habitacion h1 = habitacion(60L, "001", tipoA, EstadoHabitacion.DISPONIBLE);
        Habitacion h2 = habitacion(61L, "101", tipoB, EstadoHabitacion.DISPONIBLE);

        doReturn(List.of(h1, h2))
                .when(habitacionRepository)
                .findDisponibles(any(), any(), any());

        List<HabitacionDisponibleResponse> response = sut.listarDisponibles(INGRESO, SALIDA, 2);

        assertEquals(1L, response.get(0).tipoHabitacionId());
        assertEquals(2L, response.get(1).tipoHabitacionId());
    }

    // ───────────── helpers ─────────────

    private TipoHabitacion tipo(Long id, String nombre, Double precio, Integer capacidad,
                                String descripcion, String... caracteristicas) {
        TipoHabitacion t = new TipoHabitacion(id, nombre, descripcion, precio, capacidad);
        Set<Caracteristica> set = new LinkedHashSet<>();
        for (String c : caracteristicas) {
            Caracteristica car = new Caracteristica();
            car.setId((long) (set.size() + 1));
            car.setNombre(c);
            car.setDescripcion(c);
            set.add(car);
        }
        t.setCaracteristicas(set);
        return t;
    }

    private Habitacion habitacion(Long id, String numero, TipoHabitacion tipo, EstadoHabitacion estado) {
        Habitacion h = new Habitacion();
        h.setId(id);
        h.setNumero(numero);
        h.setTipoHabitacion(tipo);
        h.setEstado(estado);
        h.setPiso(1);
        return h;
    }
}
