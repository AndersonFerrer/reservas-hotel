package com.dubai.dubai.controllers;

import com.dubai.dubai.models.EstadoReserva;
import com.dubai.dubai.models.Reserva;
import com.dubai.dubai.services.ReservaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController sut;

    @Test
    void listar_debeRetornarLista() {
        List<Reserva> esperado = List.of(new Reserva(1L, 1L, 1L, 1L, 1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), EstadoReserva.PENDIENTE));
        doReturn(esperado).when(reservaService).listar();

        List<Reserva> resultado = sut.listar();

        assertEquals(esperado, resultado);
        verify(reservaService).listar();
    }

    @Test
    void buscarPorId_debeRetornarOk() {
        Reserva esperado = new Reserva(1L, 1L, 1L, 1L, 1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), EstadoReserva.PENDIENTE);
        doReturn(esperado).when(reservaService).buscarPorId(1L);
        Authentication auth = authAdmin();

        ResponseEntity<Reserva> resultado = sut.buscarPorId(1L, auth);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(reservaService).buscarPorId(1L);
        verify(reservaService, never()).buscarPorIdParaCliente(1L, "admin@dubai");
    }

    @Test
    void buscarPorId_clienteDueno_debeRetornarOk() {
        Reserva esperado = new Reserva(1L, 1L, 1L, 1L, 1L,
                LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), EstadoReserva.PENDIENTE);
        doReturn(esperado).when(reservaService).buscarPorIdParaCliente(1L, "cliente@dubai");
        Authentication auth = authCliente();

        ResponseEntity<Reserva> resultado = sut.buscarPorId(1L, auth);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(esperado, resultado.getBody());
        verify(reservaService).buscarPorIdParaCliente(1L, "cliente@dubai");
        verify(reservaService, never()).buscarPorId(1L);
    }

    @Test
    void buscarPorId_clienteNoDueno_debeRetornarNotFound() {
        doReturn(null).when(reservaService).buscarPorIdParaCliente(7L, "otro@dubai");
        Authentication auth = authCliente("otro@dubai");

        ResponseEntity<Reserva> resultado = sut.buscarPorId(7L, auth);

        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());
        verify(reservaService).buscarPorIdParaCliente(7L, "otro@dubai");
    }

    private Authentication authAdmin() {
        return new UsernamePasswordAuthenticationToken(
                "admin@dubai", null, List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR")));
    }

    private Authentication authCliente() {
        return authCliente("cliente@dubai");
    }

    private Authentication authCliente(String email) {
        return new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE")));
    }

    @Test
    void crear_debeRetornarCreatedConRespuestaSimple() {
        Reserva request = new Reserva(null, 1L, 1L, 1L, 1L,
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12), null);
        Reserva creada = new Reserva(10L, 1L, 1L, 1L, 1L,
                LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12), EstadoReserva.PENDIENTE);

        doReturn(creada).when(reservaService).crear(request);
        doReturn(2L).when(reservaService).calcularNoches(creada);

        ResponseEntity<?> resultado = sut.crear(request);

        assertEquals(HttpStatus.CREATED, resultado.getStatusCode());
        Map<String, Object> body = (Map<String, Object>) resultado.getBody();
        assertEquals("Reserva creada correctamente", body.get("mensaje"));
        assertEquals(2L, body.get("noches"));
        assertEquals(creada, body.get("reserva"));
        verify(reservaService).crear(request);
        verify(reservaService).calcularNoches(creada);
    }
}
