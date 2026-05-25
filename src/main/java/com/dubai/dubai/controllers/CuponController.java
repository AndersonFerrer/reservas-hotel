package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Cupon;
import com.dubai.dubai.services.CuponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    private final CuponService cuponService;

    public CuponController(CuponService cuponService) {
        this.cuponService = cuponService;
    }

    @GetMapping
    public List<Cupon> listar() {
        return cuponService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cupon> buscarPorId(@PathVariable Long id) {
        Cupon cupon = cuponService.buscarPorId(id);
        return cupon != null ? ResponseEntity.ok(cupon) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cupon cupon) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(cuponService.crear(cupon));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cupon cupon) {
        try {
            Cupon actualizado = cuponService.actualizar(id, cupon);
            return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return cuponService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
