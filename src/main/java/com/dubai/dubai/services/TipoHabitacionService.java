package com.dubai.dubai.services;

import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;

    public TipoHabitacionService(TipoHabitacionRepository tipoHabitacionRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    public List<TipoHabitacion> listar() {
        return tipoHabitacionRepository.findAll();
    }

    public TipoHabitacion buscarPorId(Long id) {
        return tipoHabitacionRepository.findById(id).orElse(null);
    }
}
