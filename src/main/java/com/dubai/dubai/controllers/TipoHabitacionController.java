package com.dubai.dubai.controllers;

import com.dubai.dubai.dto.ReemplazarCaracteristicasRequest;
import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.services.TipoHabitacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TipoHabitacion tipoHabitacion) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(tipoHabitacionService.crear(tipoHabitacion));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody TipoHabitacion tipoHabitacion) {
        try {
            TipoHabitacion actualizada = tipoHabitacionService.actualizar(id, tipoHabitacion);
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            return tipoHabitacionService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}/caracteristicas")
    public ResponseEntity<?> reemplazarCaracteristicas(@PathVariable Long id,
                                                       @RequestBody(required = false) ReemplazarCaracteristicasRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(error("El cuerpo de la solicitud es obligatorio"));
        }
        if (request.getCaracteristicaIds() == null) {
            return ResponseEntity.badRequest().body(error("El campo caracteristicaIds es obligatorio"));
        }
        try {
            TipoHabitacion actualizada = tipoHabitacionService.reemplazarCaracteristicas(id, request.getCaracteristicaIds());
            return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(error(ex.getMessage()));
        }
    }

    private Map<String, String> error(String mensaje) {
        Map<String, String> error = new LinkedHashMap<>();
        error.put("error", mensaje);
        return error;
    }
}
