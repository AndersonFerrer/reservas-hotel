package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.AuthResponse;
import com.dubai.dubai.dto.LoginRequest;
import com.dubai.dubai.models.RolUsuario;
import com.dubai.dubai.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController sut;

    @Test
    void login_exitoso_debeRetornar200ConCuerpo() {
        AuthResponse.UsuarioResumen usuario = new AuthResponse.UsuarioResumen(
                1L, "ana@correo.com", "Ana", "Lopez", "Ana Lopez", RolUsuario.CLIENTE);
        AuthResponse esperado = new AuthResponse("token.jwt.aqui", 86400L, usuario);

        doReturn(esperado).when(authService).login(org.mockito.ArgumentMatchers.any(LoginRequest.class));

        ResponseEntity<?> resultado = sut.login(new LoginRequest());

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        assertEquals(esperado, resultado.getBody());
    }

    @Test
    void login_credencialesInvalidas_debeRetornar401ConError() {
        doThrow(new IllegalArgumentException("Credenciales invalidas"))
                .when(authService).login(org.mockito.ArgumentMatchers.any(LoginRequest.class));

        ResponseEntity<?> resultado = sut.login(new LoginRequest());

        assertEquals(HttpStatus.UNAUTHORIZED, resultado.getStatusCode());
        assertNotNull(resultado.getBody());
        @SuppressWarnings("unchecked")
        java.util.Map<String, String> body = (java.util.Map<String, String>) resultado.getBody();
        assertEquals("Credenciales invalidas", body.get("error"));
    }
}