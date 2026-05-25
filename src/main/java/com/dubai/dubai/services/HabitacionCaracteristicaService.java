package com.dubai.dubai.services;

import com.dubai.dubai.models.HabitacionCaracteristica;
import com.dubai.dubai.repositories.CaracteristicaRepository;
import com.dubai.dubai.repositories.HabitacionCaracteristicaRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionCaracteristicaService {

    private final HabitacionCaracteristicaRepository habitacionCaracteristicaRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public HabitacionCaracteristicaService(HabitacionCaracteristicaRepository habitacionCaracteristicaRepository,
                                           TipoHabitacionRepository tipoHabitacionRepository,
                                           CaracteristicaRepository caracteristicaRepository) {
        this.habitacionCaracteristicaRepository = habitacionCaracteristicaRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<HabitacionCaracteristica> listar() {
        return habitacionCaracteristicaRepository.findAll();
    }

    public HabitacionCaracteristica buscarPorId(Long id) {
        return habitacionCaracteristicaRepository.findById(id).orElse(null);
    }

    public HabitacionCaracteristica crear(HabitacionCaracteristica relacion) {
        validarRelacion(relacion);
        relacion.setTipoHabitacion(tipoHabitacionRepository.findById(relacion.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        relacion.setCaracteristica(caracteristicaRepository.findById(relacion.getCaracteristicaId())
                .orElseThrow(() -> new IllegalArgumentException("La caracteristica indicada no existe")));
        relacion.getTipoHabitacion().getCaracteristicas().add(relacion.getCaracteristica());
        tipoHabitacionRepository.save(relacion.getTipoHabitacion());
        return habitacionCaracteristicaRepository.save(relacion);
    }

    private void validarRelacion(HabitacionCaracteristica relacion) {
        if (relacion == null) {
            throw new IllegalArgumentException("La relacion es obligatoria");
        }
        if (relacion.getTipoHabitacionId() == null || relacion.getCaracteristicaId() == null) {
            throw new IllegalArgumentException("tipoHabitacionId y caracteristicaId son obligatorios");
        }
    }
}
