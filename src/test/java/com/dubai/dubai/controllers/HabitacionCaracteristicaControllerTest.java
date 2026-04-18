package com.dubai.dubai.controllers;

import com.dubai.dubai.models.HabitacionCaracteristica;
import com.dubai.dubai.services.HabitacionCaracteristicaService;
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
class HabitacionCaracteristicaControllerTest {

    @Mock
    private HabitacionCaracteristicaService habitacionCaracteristicaService;

    @InjectMocks
    private HabitacionCaracteristicaController sut;

    @Test
    void listar_debeRetornarLista() {
        List<HabitacionCaracteristica> esperado = List.of(new HabitacionCaracteristica(1L, 1L, 1L));
        doReturn(esperado).when(habitacionCaracteristicaService).listar();

        List<HabitacionCaracteristica> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(habitacionCaracteristicaService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        HabitacionCaracteristica esperado = new HabitacionCaracteristica(1L, 1L, 1L);
        doReturn(esperado).when(habitacionCaracteristicaService).buscarPorId(1L);

        ResponseEntity<HabitacionCaracteristica> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(habitacionCaracteristicaService).buscarPorId(1L);
    }
}
