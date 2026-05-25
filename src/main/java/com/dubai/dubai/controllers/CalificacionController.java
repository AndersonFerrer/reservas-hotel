package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Calificacion;
import com.dubai.dubai.services.CalificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @GetMapping
    public List<Calificacion> listar() {
        return calificacionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Calificacion> buscarPorId(@PathVariable Long id) {
        Calificacion calificacion = calificacionService.buscarPorId(id);
        return calificacion != null ? ResponseEntity.ok(calificacion) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Calificacion calificacion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(calificacionService.crear(calificacion));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Calificacion calificacion) {
        try {
            Calificacion actualizada = calificacionService.actualizar(id, calificacion);
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return calificacionService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
