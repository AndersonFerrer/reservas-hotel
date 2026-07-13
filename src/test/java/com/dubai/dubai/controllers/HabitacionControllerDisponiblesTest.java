package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.HabitacionDisponibleResponse;
import com.dubai.dubai.services.HabitacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HabitacionControllerDisponiblesTest {

    @Mock
    private HabitacionService habitacionService;

    @InjectMocks
    private HabitacionController sut;

    @Test
    void listarDisponibles_conDatos_retornaListaDelService() {
        HabitacionDisponibleResponse.HabitacionResumen resumen =
                new HabitacionDisponibleResponse.HabitacionResumen(1L, "A01");
        HabitacionDisponibleResponse respuesta = new HabitacionDisponibleResponse(
                1L, List.of(resumen), 220.5, 4, List.of("jacuzzi"),
                "Suite Presidencial", "Descripcion suite");
        doReturn(List.of(respuesta))
                .when(habitacionService)
                .listarDisponibles(any(LocalDate.class), any(LocalDate.class), any(Integer.class));

        List<HabitacionDisponibleResponse> resultado = sut.listarDisponibles(
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 5), 2);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Suite Presidencial", resultado.get(0).nombre());
        assertEquals(1, resultado.get(0).habitaciones().size());
        assertEquals(1L, resultado.get(0).habitaciones().get(0).habitacionId());
    }

    @Test
    void listarDisponibles_sinDisponibilidad_retornaListaVacia() {
        doReturn(List.<HabitacionDisponibleResponse>of())
                .when(habitacionService)
                .listarDisponibles(any(LocalDate.class), any(LocalDate.class), any(Integer.class));

        List<HabitacionDisponibleResponse> resultado = sut.listarDisponibles(
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 10), 50);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarDisponibles_pasaQueryParamsAlService() {
        LocalDate ingreso = LocalDate.of(2026, 8, 1);
        LocalDate salida = LocalDate.of(2026, 8, 5);
        Integer huespedes = 3;

        doReturn(List.<HabitacionDisponibleResponse>of())
                .when(habitacionService)
                .listarDisponibles(eq(ingreso), eq(salida), eq(huespedes));

        sut.listarDisponibles(ingreso, salida, huespedes);

        ArgumentCaptor<LocalDate> captorIngreso = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> captorSalida = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<Integer> captorHuespedes = ArgumentCaptor.forClass(Integer.class);
        verify(habitacionService).listarDisponibles(
                captorIngreso.capture(), captorSalida.capture(), captorHuespedes.capture());
        assertEquals(ingreso, captorIngreso.getValue());
        assertEquals(salida, captorSalida.getValue());
        assertEquals(huespedes, captorHuespedes.getValue());
    }

    @Test
    void listarDisponibles_validacionRotaEnService_propagaExcepcion() {
        // El controller NO captura IllegalArgumentException: lo deja fluir para que
        // ApiExceptionHandler lo traduzca a 400 con {"error":"..."}.
        doThrow(new IllegalArgumentException("fechaSalida debe ser posterior a fechaIngreso"))
                .when(habitacionService)
                .listarDisponibles(any(LocalDate.class), any(LocalDate.class), any(Integer.class));

        IllegalArgumentException ex = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> sut.listarDisponibles(LocalDate.of(2026, 8, 5), LocalDate.of(2026, 8, 1), 2));
        assertEquals("fechaSalida debe ser posterior a fechaIngreso", ex.getMessage());
    }
}
