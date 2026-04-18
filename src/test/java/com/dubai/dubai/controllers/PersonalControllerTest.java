package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Personal;
import com.dubai.dubai.models.RolPersonal;
import com.dubai.dubai.services.PersonalService;
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
class PersonalControllerTest {

    @Mock
    private PersonalService personalService;

    @InjectMocks
    private PersonalController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Personal> esperado = List.of(new Personal(1L, "Carlos", "Ramos", RolPersonal.ADMINISTRADOR, "999", "c@x.com"));
        doReturn(esperado).when(personalService).listar();

        List<Personal> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(personalService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Personal esperado = new Personal(1L, "Carlos", "Ramos", RolPersonal.ADMINISTRADOR, "999", "c@x.com");
        doReturn(esperado).when(personalService).buscarPorId(1L);

        ResponseEntity<Personal> resultado = sut.buscarPorId(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(personalService).buscarPorId(1L);
    }
}
