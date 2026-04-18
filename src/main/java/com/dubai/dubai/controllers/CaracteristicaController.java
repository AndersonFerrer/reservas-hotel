package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.services.CaracteristicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caracteristicas")
public class CaracteristicaController {

    private final CaracteristicaService caracteristicaService;

    public CaracteristicaController(CaracteristicaService caracteristicaService) {
        this.caracteristicaService = caracteristicaService;
    }

    @GetMapping
    public List<Caracteristica> listar() {
        return caracteristicaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caracteristica> buscarPorId(@PathVariable Long id) {
        Caracteristica caracteristica = caracteristicaService.buscarPorId(id);
        return caracteristica != null ? ResponseEntity.ok(caracteristica) : ResponseEntity.notFound().build();
    }
}
