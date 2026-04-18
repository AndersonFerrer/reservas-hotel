package com.dubai.dubai.controllers;

import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.services.TipoHabitacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-habitacion")
public class TipoHabitacionController {

    private final TipoHabitacionService tipoHabitacionService;

    public TipoHabitacionController(TipoHabitacionService tipoHabitacionService) {
        this.tipoHabitacionService = tipoHabitacionService;
    }

    @GetMapping
    public List<TipoHabitacion> listar() {
        return tipoHabitacionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoHabitacion> buscarPorId(@PathVariable Long id) {
        TipoHabitacion tipo = tipoHabitacionService.buscarPorId(id);
        return tipo != null ? ResponseEntity.ok(tipo) : ResponseEntity.notFound().build();
    }
}
