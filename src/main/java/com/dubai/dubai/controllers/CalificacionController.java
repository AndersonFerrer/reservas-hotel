package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Calificacion;
import com.dubai.dubai.services.CalificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
