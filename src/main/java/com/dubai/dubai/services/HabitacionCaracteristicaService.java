package com.dubai.dubai.services;

import com.dubai.dubai.models.HabitacionCaracteristica;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HabitacionCaracteristicaService {

    private final List<HabitacionCaracteristica> relaciones = new ArrayList<>(List.of(
            new HabitacionCaracteristica(1L, 1L, 1L),
            new HabitacionCaracteristica(2L, 2L, 1L),
            new HabitacionCaracteristica(3L, 3L, 2L)
    ));

    public List<HabitacionCaracteristica> listar() {
        return relaciones;
    }

    public HabitacionCaracteristica buscarPorId(Long id) {
        return relaciones.stream()
                .filter(relacion -> relacion.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
