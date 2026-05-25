package com.dubai.dubai.services;

import com.dubai.dubai.models.HabitacionCaracteristica;
import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.repositories.CaracteristicaRepository;
import com.dubai.dubai.repositories.HabitacionCaracteristicaRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public HabitacionCaracteristica crear(HabitacionCaracteristica relacion) {
        validarRelacion(relacion);
        validarRelacionDisponible(relacion.getTipoHabitacionId(), relacion.getCaracteristicaId(), null);
        asignarRelaciones(relacion);
        relacion.getTipoHabitacion().getCaracteristicas().add(relacion.getCaracteristica());
        tipoHabitacionRepository.save(relacion.getTipoHabitacion());
        return habitacionCaracteristicaRepository.save(relacion);
    }

    @Transactional
    public HabitacionCaracteristica actualizar(Long id, HabitacionCaracteristica datos) {
        HabitacionCaracteristica existente = habitacionCaracteristicaRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarRelacion(datos);
        validarRelacionDisponible(datos.getTipoHabitacionId(), datos.getCaracteristicaId(), id);

        TipoHabitacion tipoAnterior = existente.getTipoHabitacion();
        Caracteristica caracteristicaAnterior = existente.getCaracteristica();
        if (tipoAnterior != null && caracteristicaAnterior != null) {
            tipoAnterior.getCaracteristicas().removeIf(caracteristica -> caracteristica.getId().equals(caracteristicaAnterior.getId()));
            tipoHabitacionRepository.save(tipoAnterior);
        }

        existente.setTipoHabitacionId(datos.getTipoHabitacionId());
        existente.setCaracteristicaId(datos.getCaracteristicaId());
        asignarRelaciones(existente);
        existente.getTipoHabitacion().getCaracteristicas().add(existente.getCaracteristica());
        tipoHabitacionRepository.save(existente.getTipoHabitacion());
        return habitacionCaracteristicaRepository.save(existente);
    }

    @Transactional
    public boolean eliminar(Long id) {
        HabitacionCaracteristica relacion = habitacionCaracteristicaRepository.findById(id).orElse(null);
        if (relacion == null) {
            return false;
        }
        TipoHabitacion tipo = relacion.getTipoHabitacion();
        Caracteristica caracteristica = relacion.getCaracteristica();
        if (tipo != null && caracteristica != null) {
            tipo.getCaracteristicas().removeIf(item -> item.getId().equals(caracteristica.getId()));
            tipoHabitacionRepository.save(tipo);
        }
        habitacionCaracteristicaRepository.delete(relacion);
        return true;
    }

    private void asignarRelaciones(HabitacionCaracteristica relacion) {
        relacion.setTipoHabitacion(tipoHabitacionRepository.findById(relacion.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        relacion.setCaracteristica(caracteristicaRepository.findById(relacion.getCaracteristicaId())
                .orElseThrow(() -> new IllegalArgumentException("La caracteristica indicada no existe")));
    }

    private void validarRelacionDisponible(Long tipoHabitacionId, Long caracteristicaId, Long idActual) {
        habitacionCaracteristicaRepository.findByTipoHabitacion_IdAndCaracteristica_Id(tipoHabitacionId, caracteristicaId)
                .filter(relacion -> !relacion.getId().equals(idActual))
                .ifPresent(relacion -> {
                    throw new IllegalArgumentException("La caracteristica ya esta asociada al tipo de habitacion");
                });
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
