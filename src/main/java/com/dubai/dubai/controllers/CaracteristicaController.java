package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.services.CaracteristicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Caracteristica caracteristica) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(caracteristicaService.crear(caracteristica));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Caracteristica caracteristica) {
        try {
            Caracteristica actualizada = caracteristicaService.actualizar(id, caracteristica);
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            return caracteristicaService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(ex.getMessage()));
        }
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
