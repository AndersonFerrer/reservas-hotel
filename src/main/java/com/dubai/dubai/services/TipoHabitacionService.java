package com.dubai.dubai.services;

import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.models.TipoHabitacion;
import com.dubai.dubai.repositories.CalificacionRepository;
import com.dubai.dubai.repositories.CaracteristicaRepository;
import com.dubai.dubai.repositories.CuponRepository;
import com.dubai.dubai.repositories.HabitacionCaracteristicaRepository;
import com.dubai.dubai.repositories.HabitacionRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class TipoHabitacionService {

    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final HabitacionRepository habitacionRepository;
    private final HabitacionCaracteristicaRepository habitacionCaracteristicaRepository;
    private final CuponRepository cuponRepository;
    private final CalificacionRepository calificacionRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public TipoHabitacionService(TipoHabitacionRepository tipoHabitacionRepository,
                                 HabitacionRepository habitacionRepository,
                                 HabitacionCaracteristicaRepository habitacionCaracteristicaRepository,
                                 CuponRepository cuponRepository,
                                 CalificacionRepository calificacionRepository,
                                 CaracteristicaRepository caracteristicaRepository) {
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.habitacionRepository = habitacionRepository;
        this.habitacionCaracteristicaRepository = habitacionCaracteristicaRepository;
        this.cuponRepository = cuponRepository;
        this.calificacionRepository = calificacionRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<TipoHabitacion> listar() {
        return tipoHabitacionRepository.listarConJpql();
    }

    public TipoHabitacion buscarPorId(Long id) {
        return tipoHabitacionRepository.buscarPorIdConJpql(id).orElse(null);
    }

    public TipoHabitacion crear(TipoHabitacion tipoHabitacion) {
        validarTipoHabitacion(tipoHabitacion);
        validarNombreDisponible(tipoHabitacion.getNombre(), null);
        tipoHabitacion.setId(null);
        return tipoHabitacionRepository.save(tipoHabitacion);
    }

    public TipoHabitacion actualizar(Long id, TipoHabitacion datos) {
        TipoHabitacion existente = tipoHabitacionRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarTipoHabitacion(datos);
        validarNombreDisponible(datos.getNombre(), id);

        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setPrecioBase(datos.getPrecioBase());
        existente.setCapacidad(datos.getCapacidad());
        return tipoHabitacionRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!tipoHabitacionRepository.existsById(id)) {
            return false;
        }
        if (habitacionRepository.existsByTipoHabitacion_Id(id)
                || habitacionCaracteristicaRepository.existsByTipoHabitacion_Id(id)
                || cuponRepository.existsByTipoHabitacion_Id(id)
                || calificacionRepository.existsByTipoHabitacion_Id(id)) {
            throw new IllegalStateException("No se puede eliminar el tipo de habitacion porque tiene datos asociados");
        }
        tipoHabitacionRepository.deleteById(id);
        return true;
    }

    @Transactional
    public TipoHabitacion reemplazarCaracteristicas(Long id, List<Long> caracteristicaIds) {
        TipoHabitacion tipoHabitacion = tipoHabitacionRepository.findById(id).orElse(null);
        if (tipoHabitacion == null) {
            return null;
        }

        Set<Caracteristica> nuevasCaracteristicas;
        if (caracteristicaIds == null || caracteristicaIds.isEmpty()) {
            nuevasCaracteristicas = new HashSet<>();
        } else {
            Set<Long> idsUnicos = new LinkedHashSet<>(caracteristicaIds);
            List<Caracteristica> encontradas = caracteristicaRepository.findAllById(idsUnicos);
            if (encontradas.size() != idsUnicos.size()) {
                Set<Long> encontradosIds = new HashSet<>();
                for (Caracteristica c : encontradas) {
                    encontradosIds.add(c.getId());
                }
                for (Long idIndicado : idsUnicos) {
                    if (!encontradosIds.contains(idIndicado)) {
                        throw new IllegalArgumentException(
                                "La caracteristica con id " + idIndicado + " no existe");
                    }
                }
            }
            nuevasCaracteristicas = new LinkedHashSet<>(encontradas);
        }

        tipoHabitacion.getCaracteristicas().clear();
        tipoHabitacion.getCaracteristicas().addAll(nuevasCaracteristicas);
        return tipoHabitacionRepository.save(tipoHabitacion);
    }

    private void validarTipoHabitacion(TipoHabitacion tipoHabitacion) {
        if (tipoHabitacion == null) {
            throw new IllegalArgumentException("El tipo de habitacion es obligatorio");
        }
        if (tipoHabitacion.getNombre() == null || tipoHabitacion.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de habitacion es obligatorio");
        }
        if (tipoHabitacion.getDescripcion() == null || tipoHabitacion.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripcion del tipo de habitacion es obligatoria");
        }
        if (tipoHabitacion.getPrecioBase() == null || tipoHabitacion.getPrecioBase() <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor a cero");
        }
        if (tipoHabitacion.getCapacidad() == null || tipoHabitacion.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero");
        }
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        tipoHabitacionRepository.findByNombre(nombre)
                .filter(tipo -> !tipo.getId().equals(idActual))
                .ifPresent(tipo -> {
                    throw new IllegalArgumentException("El tipo de habitacion ya se encuentra registrado");
                });
    }
}
