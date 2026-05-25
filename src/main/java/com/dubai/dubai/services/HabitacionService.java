package com.dubai.dubai.services;

import com.dubai.dubai.models.Habitacion;
import com.dubai.dubai.repositories.HabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;

    public HabitacionService(HabitacionRepository habitacionRepository) {
        this.habitacionRepository = habitacionRepository;
    }

    public List<Habitacion> listar() {
        return habitacionRepository.findAll();
    }

    public Habitacion buscarPorId(Long id) {
        return habitacionRepository.findById(id).orElse(null);
    }
}
