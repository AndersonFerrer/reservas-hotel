package com.dubai.dubai.controllers;

import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.services.TipoHabitacionService;
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
class TipoHabitacionControllerTest {

    @Mock
    private TipoHabitacionService tipoHabitacionService;

    @InjectMocks
    private TipoHabitacionController sut;

    @Test
    void listar_debeRetornarLista() {
        List<TipoHabitacion> esperado = List.of(new TipoHabitacion(1L, "Simple", "Base", 120.0, 1));
        doReturn(esperado).when(tipoHabitacionService).listar();

        List<TipoHabitacion> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(tipoHabitacionService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        TipoHabitacion esperado = new TipoHabitacion(1L, "Simple", "Base", 120.0, 1);
        doReturn(esperado).when(tipoHabitacionService).buscarPorId(1L);

        ResponseEntity<TipoHabitacion> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(tipoHabitacionService).buscarPorId(1L);
    }
}
