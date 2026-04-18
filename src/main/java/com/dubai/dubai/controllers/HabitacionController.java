package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Habitacion;
import com.dubai.dubai.services.HabitacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<Habitacion> buscarPorId(@PathVariable Long id) {
        Habitacion habitacion = habitacionService.buscarPorId(id);
        return habitacion != null ? ResponseEntity.ok(habitacion) : ResponseEntity.notFound().build();
    }
}
