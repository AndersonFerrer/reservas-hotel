package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.ReservaConPagoRequest;
import com.dubai.dubai.models.Pago;
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

    @PostMapping("/mis-reservas/con-pago")
    public ResponseEntity<?> crearMiReservaConPago(@RequestBody ReservaConPagoRequest request, Authentication authentication) {
        try {
            Reserva creada = reservaService.crearParaClienteAutenticadoConPago(request, authentication.getName());
            return respuestaReservaCreadaConPago(creada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PostMapping("/con-pago")
    public ResponseEntity<?> crearConPago(@RequestBody ReservaConPagoRequest request) {
        try {
            Reserva creada = reservaService.crearConPago(request);
            return respuestaReservaCreadaConPago(creada);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @GetMapping("/{id}/pagos")
    public ResponseEntity<?> listarPagos(@PathVariable Long id) {
        try {
            List<Pago> pagos = reservaService.listarPagos(id);
            return ResponseEntity.ok(pagos);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> buscarPorId(@PathVariable Long id, Authentication authentication) {
        boolean esCliente = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_CLIENTE".equals(a.getAuthority()));
        Reserva reserva = esCliente
                ? reservaService.buscarPorIdParaCliente(id, authentication.getName())
                : reservaService.buscarPorId(id);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            Reserva actualizada = reservaService.actualizar(id, reserva);
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return reservaService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private ResponseEntity<Map<String, Object>> respuestaReservaCreada(Reserva creada) {
        long noches = reservaService.calcularNoches(creada);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensaje", "Reserva creada correctamente");
        response.put("noches", noches);
        response.put("reserva", creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ResponseEntity<Map<String, Object>> respuestaReservaCreadaConPago(Reserva creada) {
        long noches = reservaService.calcularNoches(creada);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mensaje", "Reserva creada correctamente con pago");
        response.put("noches", noches);
        response.put("reserva", creada);
        response.put("pagos", reservaService.listarPagos(creada.getId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
