package com.dubai.dubai.controllers;

import com.dubai.dubai.models.HabitacionCaracteristica;
import com.dubai.dubai.services.HabitacionCaracteristicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habitacion-caracteristicas")
public class HabitacionCaracteristicaController {

    private final HabitacionCaracteristicaService habitacionCaracteristicaService;

    public HabitacionCaracteristicaController(HabitacionCaracteristicaService habitacionCaracteristicaService) {
        this.habitacionCaracteristicaService = habitacionCaracteristicaService;
    }

    @GetMapping
    public List<HabitacionCaracteristica> listar() {
        return habitacionCaracteristicaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<HabitacionCaracteristica> buscarPorId(@PathVariable Long id) {
        HabitacionCaracteristica relacion = habitacionCaracteristicaService.buscarPorId(id);
        return relacion != null ? ResponseEntity.ok(relacion) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody HabitacionCaracteristica relacion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(habitacionCaracteristicaService.crear(relacion));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody HabitacionCaracteristica relacion) {
        try {
            HabitacionCaracteristica actualizada = habitacionCaracteristicaService.actualizar(id, relacion);
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return habitacionCaracteristicaService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
