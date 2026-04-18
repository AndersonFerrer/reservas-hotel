package com.dubai.dubai.services;

import com.dubai.dubai.models.TipoHabitacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TipoHabitacionService {

    private final List<TipoHabitacion> tipos = new ArrayList<>(List.of(
            new TipoHabitacion(1L, "Simple", "Habitacion simple", 120.0, 1),
            new TipoHabitacion(2L, "Doble", "Habitacion doble", 180.0, 2),
            new TipoHabitacion(3L, "Suite", "Suite ejecutiva", 300.0, 4)
    ));

    public List<TipoHabitacion> listar() {
        return tipos;
    }

    public TipoHabitacion buscarPorId(Long id) {
        return tipos.stream()
                .filter(tipo -> tipo.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
