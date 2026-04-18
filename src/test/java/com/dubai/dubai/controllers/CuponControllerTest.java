package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Cupon;
import com.dubai.dubai.services.CuponService;
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
class CuponControllerTest {

    @Mock
    private CuponService cuponService;

    @InjectMocks
    private CuponController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Cupon> esperado = List.of(new Cupon(1L, "VERANO10", 10.0, 2L, LocalDate.now(), LocalDate.now().plusDays(10)));
        doReturn(esperado).when(cuponService).listar();

        List<Cupon> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(cuponService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Cupon esperado = new Cupon(1L, "VERANO10", 10.0, 2L, LocalDate.now(), LocalDate.now().plusDays(10));
        doReturn(esperado).when(cuponService).buscarPorId(1L);

        ResponseEntity<Cupon> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(cuponService).buscarPorId(1L);
    }
}
