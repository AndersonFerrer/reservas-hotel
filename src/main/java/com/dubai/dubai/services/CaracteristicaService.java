package com.dubai.dubai.services;

import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.repositories.CaracteristicaRepository;
import com.dubai.dubai.repositories.HabitacionCaracteristicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaracteristicaService {

    private final CaracteristicaRepository caracteristicaRepository;
    private final HabitacionCaracteristicaRepository habitacionCaracteristicaRepository;

    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository,
                                 HabitacionCaracteristicaRepository habitacionCaracteristicaRepository) {
        this.caracteristicaRepository = caracteristicaRepository;
        this.habitacionCaracteristicaRepository = habitacionCaracteristicaRepository;
    }

    public List<Caracteristica> listar() {
        return caracteristicaRepository.listarConJpql();
    }

    public Caracteristica buscarPorId(Long id) {
        return caracteristicaRepository.buscarPorIdConJpql(id).orElse(null);
    }

    public Caracteristica crear(Caracteristica caracteristica) {
        validarCaracteristica(caracteristica);
        validarNombreDisponible(caracteristica.getNombre(), null);
        caracteristica.setId(null);
        return caracteristicaRepository.save(caracteristica);
    }

    public Caracteristica actualizar(Long id, Caracteristica datos) {
        Caracteristica existente = caracteristicaRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarCaracteristica(datos);
        validarNombreDisponible(datos.getNombre(), id);

        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        return caracteristicaRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!caracteristicaRepository.existsById(id)) {
            return false;
        }
        if (habitacionCaracteristicaRepository.existsByCaracteristica_Id(id)) {
            throw new IllegalStateException("No se puede eliminar la caracteristica porque esta asociada a tipos de habitacion");
        }
        caracteristicaRepository.deleteById(id);
        return true;
    }

    private void validarCaracteristica(Caracteristica caracteristica) {
        if (caracteristica == null) {
            throw new IllegalArgumentException("La caracteristica es obligatoria");
        }
        if (caracteristica.getNombre() == null || caracteristica.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la caracteristica es obligatorio");
        }
        if (caracteristica.getDescripcion() == null || caracteristica.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("La descripcion de la caracteristica es obligatoria");
        }
    }

    private void validarNombreDisponible(String nombre, Long idActual) {
        caracteristicaRepository.findByNombre(nombre)
                .filter(caracteristica -> !caracteristica.getId().equals(idActual))
                .ifPresent(caracteristica -> {
                    throw new IllegalArgumentException("La caracteristica ya se encuentra registrada");
                });
    }
}
