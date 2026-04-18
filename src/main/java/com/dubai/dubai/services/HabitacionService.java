package com.dubai.dubai.services;

import com.dubai.dubai.models.EstadoHabitacion;
import com.dubai.dubai.models.Habitacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HabitacionService {

    private final List<Habitacion> habitaciones = new ArrayList<>(List.of(
            new Habitacion(1L, "101", 1L, EstadoHabitacion.DISPONIBLE, 1),
            new Habitacion(2L, "102", 2L, EstadoHabitacion.OCUPADA, 1),
            new Habitacion(3L, "201", 3L, EstadoHabitacion.MANTENIMIENTO, 2)
    ));

    public List<Habitacion> listar() {
        return habitaciones;
    }

    public Habitacion buscarPorId(Long id) {
        return habitaciones.stream()
                .filter(habitacion -> habitacion.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
