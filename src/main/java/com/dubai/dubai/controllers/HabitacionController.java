package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.HabitacionDisponibleResponse;
import com.dubai.dubai.models.Habitacion;
import com.dubai.dubai.services.HabitacionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/habitaciones")
public class HabitacionController {

    private final HabitacionService habitacionService;

    public HabitacionController(HabitacionService habitacionService) {
        this.habitacionService = habitacionService;
    }

    @GetMapping
    public List<Habitacion> listar() {
        return habitacionService.listar();
    }

    @GetMapping("/disponibles")
    public List<HabitacionDisponibleResponse> listarDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngreso,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSalida,
            @RequestParam Integer huespedes) {
        return habitacionService.listarDisponibles(fechaIngreso, fechaSalida, huespedes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> buscarPorId(@PathVariable Long id) {
        Habitacion habitacion = habitacionService.buscarPorId(id);
        return habitacion != null ? ResponseEntity.ok(habitacion) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Habitacion habitacion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(habitacionService.crear(habitacion));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Habitacion habitacion) {
        try {
            Habitacion actualizada = habitacionService.actualizar(id, habitacion);
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        return habitacionService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
