package com.dubai.dubai.controllers;

import com.dubai.dubai.models.Cupon;
import com.dubai.dubai.services.CuponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    private final CuponService cuponService;

    public CuponController(CuponService cuponService) {
        this.cuponService = cuponService;
    }

    @GetMapping
    public List<Cupon> listar() {
        return cuponService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cupon> buscarPorId(@PathVariable Long id) {
        Cupon cupon = cuponService.buscarPorId(id);
        return cupon != null ? ResponseEntity.ok(cupon) : ResponseEntity.notFound().build();
    }
}
