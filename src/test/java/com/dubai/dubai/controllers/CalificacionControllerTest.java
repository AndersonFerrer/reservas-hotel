package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Calificacion;
import com.dubai.dubai.services.CalificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalificacionControllerTest {

    @Mock
    private CalificacionService calificacionService;

    @InjectMocks
    private CalificacionController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Calificacion> esperado = List.of(new Calificacion(1L, 1L, 1L, 5, "Excelente", LocalDate.now()));
        doReturn(esperado).when(calificacionService).listar();

        List<Calificacion> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(calificacionService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Calificacion esperado = new Calificacion(1L, 1L, 1L, 5, "Excelente", LocalDate.now());
        doReturn(esperado).when(calificacionService).buscarPorId(1L);

        ResponseEntity<Calificacion> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(calificacionService).buscarPorId(1L);
    }
}
