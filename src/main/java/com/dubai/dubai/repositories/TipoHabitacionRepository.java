package com.dubai.dubai.repositories;

import com.dubai.dubai.models.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {
    boolean existsByNombre(String nombre);
    Optional<TipoHabitacion> findByNombre(String nombre);
}
