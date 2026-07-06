package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.ReemplazarCaracteristicasRequest;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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

    @Test
    void reemplazarCaracteristicas_debeRetornarOkCuandoServicioResponde() {
        TipoHabitacion esperado = new TipoHabitacion(1L, "Suite", "Premium", 250.0, 2);
        ReemplazarCaracteristicasRequest request = new ReemplazarCaracteristicasRequest(List.of(1L, 4L, 7L));
        doReturn(esperado).when(tipoHabitacionService).reemplazarCaracteristicas(1L, request.getCaracteristicaIds());

        ResponseEntity<?> resultado = sut.reemplazarCaracteristicas(1L, request);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(tipoHabitacionService).reemplazarCaracteristicas(1L, request.getCaracteristicaIds());
    }

    @Test
    void reemplazarCaracteristicas_debeRetornarNotFoundCuandoTipoNoExiste() {
        ReemplazarCaracteristicasRequest request = new ReemplazarCaracteristicasRequest(List.of(1L));
        doReturn(null).when(tipoHabitacionService).reemplazarCaracteristicas(99L, request.getCaracteristicaIds());

        ResponseEntity<?> resultado = sut.reemplazarCaracteristicas(99L, request);

        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());
    }

    @Test
    void reemplazarCaracteristicas_debeRetornarBadRequestCuandoCaracteristicaNoExiste() {
        ReemplazarCaracteristicasRequest request = new ReemplazarCaracteristicasRequest(List.of(1L, 999L));
        doThrow(new IllegalArgumentException("La caracteristica con id 999 no existe"))
                .when(tipoHabitacionService).reemplazarCaracteristicas(1L, request.getCaracteristicaIds());

        ResponseEntity<?> resultado = sut.reemplazarCaracteristicas(1L, request);

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
        assertTrue(resultado.getBody() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) resultado.getBody();
        assertNotNull(body);
        assertEquals("La caracteristica con id 999 no existe", body.get("error"));
    }

    @Test
    void reemplazarCaracteristicas_debeRetornarBadRequestCuandoBodyEsNull() {
        ResponseEntity<?> resultado = sut.reemplazarCaracteristicas(1L, null);

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) resultado.getBody();
        assertNotNull(body);
        assertEquals("El cuerpo de la solicitud es obligatorio", body.get("error"));
    }

    @Test
    void reemplazarCaracteristicas_debeRetornarBadRequestCuandoCaracteristicaIdsEsNull() {
        ReemplazarCaracteristicasRequest request = new ReemplazarCaracteristicasRequest(null);

        ResponseEntity<?> resultado = sut.reemplazarCaracteristicas(1L, request);

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) resultado.getBody();
        assertNotNull(body);
        assertEquals("El campo caracteristicaIds es obligatorio", body.get("error"));
    }
}
