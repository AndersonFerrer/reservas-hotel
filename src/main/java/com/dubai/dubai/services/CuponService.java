package com.dubai.dubai.services;

import com.dubai.dubai.models.Cupon;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CuponService {

    private final List<Cupon> cupones = new ArrayList<>(List.of(
            new Cupon(1L, "VERANO10", 10.0, 2L, LocalDate.now().minusDays(15), LocalDate.now().plusDays(20)),
            new Cupon(2L, "SUITE15", 15.0, 3L, LocalDate.now().minusDays(5), LocalDate.now().plusDays(10))
    ));

    public List<Cupon> listar() {
        return cupones;
    }

    public Cupon buscarPorId(Long id) {
        return cupones.stream()
                .filter(cupon -> cupon.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
