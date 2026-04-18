package com.dubai.dubai.controllers;

import com.dubai.dubai.models.HabitacionCaracteristica;
import com.dubai.dubai.services.HabitacionCaracteristicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
