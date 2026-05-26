package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.AuthResponse;
import com.dubai.dubai.dto.LoginRequest;
import com.dubai.dubai.dto.RegistroClienteRequest;
import com.dubai.dubai.dto.RegistroPersonalRequest;
import com.dubai.dubai.dto.RegistroPersonalResponse;
import com.dubai.dubai.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/cliente")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody RegistroClienteRequest request) {
        try {
            AuthResponse response = authService.registrarCliente(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PostMapping("/register/personal")
    public ResponseEntity<?> registrarPersonal(@Valid @RequestBody RegistroPersonalRequest request) {
        try {
            RegistroPersonalResponse response = authService.registrarPersonal(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error(ex.getMessage()));
        }
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
