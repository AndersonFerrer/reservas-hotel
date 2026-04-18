package com.dubai.dubai.services;

import com.dubai.dubai.models.Caracteristica;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaracteristicaService {

    private final List<Caracteristica> caracteristicas = new ArrayList<>(List.of(
            new Caracteristica(1L, "Wifi", "Internet de alta velocidad"),
            new Caracteristica(2L, "Aire acondicionado", "Control de temperatura"),
            new Caracteristica(3L, "TV", "Television por cable")
    ));

    public List<Caracteristica> listar() {
        return caracteristicas;
    }

    public Caracteristica buscarPorId(Long id) {
        return caracteristicas.stream()
                .filter(caracteristica -> caracteristica.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
