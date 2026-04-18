package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.services.CaracteristicaService;
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
class CaracteristicaControllerTest {

    @Mock
    private CaracteristicaService caracteristicaService;

    @InjectMocks
    private CaracteristicaController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Caracteristica> esperado = List.of(new Caracteristica(1L, "Wifi", "Internet"));
        doReturn(esperado).when(caracteristicaService).listar();

        List<Caracteristica> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(caracteristicaService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Caracteristica esperado = new Caracteristica(1L, "Wifi", "Internet");
        doReturn(esperado).when(caracteristicaService).buscarPorId(1L);

        ResponseEntity<Caracteristica> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(caracteristicaService).buscarPorId(1L);
    }
}
