package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Cliente;
import com.dubai.dubai.services.ClienteService;
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
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Cliente> esperado = List.of(new Cliente(1L, "Ana", "Lopez", "123", "999", "ana@correo.com"));
        doReturn(esperado).when(clienteService).listar();

        List<Cliente> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(clienteService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Cliente esperado = new Cliente(1L, "Ana", "Lopez", "123", "999", "ana@correo.com");
        doReturn(esperado).when(clienteService).buscarPorId(1L);

        ResponseEntity<Cliente> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(clienteService).buscarPorId(1L);
    }
}
