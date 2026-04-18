package com.dubai.dubai.controllers;

import com.dubai.dubai.models.MetodoPago;
import com.dubai.dubai.models.Pago;
import com.dubai.dubai.services.PagoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Pago> esperado = List.of(new Pago(1L, MetodoPago.EFECTIVO, 200.0, LocalDateTime.now(), "REF-1"));
        doReturn(esperado).when(pagoService).listar();

        List<Pago> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(pagoService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Pago esperado = new Pago(1L, MetodoPago.EFECTIVO, 200.0, LocalDateTime.now(), "REF-1");
        doReturn(esperado).when(pagoService).buscarPorId(1L);

        ResponseEntity<Pago> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(pagoService).buscarPorId(1L);
    }
}
