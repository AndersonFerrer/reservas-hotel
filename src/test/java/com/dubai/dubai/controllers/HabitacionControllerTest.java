package com.dubai.dubai.controllers;

import com.dubai.dubai.models.EstadoHabitacion;
import com.dubai.dubai.models.Habitacion;
import com.dubai.dubai.services.HabitacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HabitacionControllerTest {

    @Mock
    private HabitacionService habitacionService;

    @InjectMocks
    private HabitacionController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Habitacion> esperado = List.of(new Habitacion(1L, "101", 1L, EstadoHabitacion.DISPONIBLE, 1));
        doReturn(esperado).when(habitacionService).listar();

        List<Habitacion> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(habitacionService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Habitacion esperado = new Habitacion(1L, "101", 1L, EstadoHabitacion.DISPONIBLE, 1);
        doReturn(esperado).when(habitacionService).buscarPorId(1L);

        ResponseEntity<Habitacion> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(habitacionService).buscarPorId(1L);
    }
}
