package com.dubai.dubai.repositories;

import com.dubai.dubai.models.HabitacionCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HabitacionCaracteristicaRepository extends JpaRepository<HabitacionCaracteristica, Long> {
    Optional<HabitacionCaracteristica> findByTipoHabitacion_IdAndCaracteristica_Id(Long tipoHabitacionId, Long caracteristicaId);
    boolean existsByTipoHabitacion_IdAndCaracteristica_Id(Long tipoHabitacionId, Long caracteristicaId);
    boolean existsByCaracteristica_Id(Long caracteristicaId);
    boolean existsByTipoHabitacion_Id(Long tipoHabitacionId);
}
