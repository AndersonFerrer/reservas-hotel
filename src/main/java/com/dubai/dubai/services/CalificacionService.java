package com.dubai.dubai.services;

import com.dubai.dubai.models.Calificacion;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalificacionService {

    private final List<Calificacion> calificaciones = new ArrayList<>(List.of(
            new Calificacion(1L, 1L, 2L, 5, "Excelente servicio", LocalDate.now().minusDays(1)),
            new Calificacion(2L, 2L, 1L, 4, "Buena experiencia", LocalDate.now().minusDays(3))
    ));

    public List<Calificacion> listar() {
        return calificaciones;
    }

    public Calificacion buscarPorId(Long id) {
        return calificaciones.stream()
                .filter(calificacion -> calificacion.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
