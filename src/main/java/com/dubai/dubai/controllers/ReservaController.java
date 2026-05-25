package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Reserva;
import com.dubai.dubai.services.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Reserva> listar() {
        return reservaService.listar();
    }

    @GetMapping("/mis-reservas")
    public List<Reserva> listarMisReservas(Authentication authentication) {
        return reservaService.listarPorClienteAutenticado(authentication.getName());
    }

    @PostMapping("/mis-reservas")
    public ResponseEntity<?> crearMiReserva(@RequestBody Reserva reserva, Authentication authentication) {
        try {
            Reserva creada = reservaService.crearParaClienteAutenticado(reserva, authentication.getName());
            return respuestaReservaCreada(creada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id) {
        Reserva reserva = reservaService.buscarPorId(id);
        return reserva != null ? ResponseEntity.ok(reserva) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Reserva reserva) {
        try {
            Reserva creada = reservaService.crear(reserva);
            return respuestaReservaCreada(creada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    private ResponseEntity<Map<String, Object>> respuestaReservaCreada(Reserva creada) {
        long noches = reservaService.calcularNoches(creada);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensaje", "Reserva creada correctamente");
        response.put("noches", noches);
        response.put("reserva", creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
