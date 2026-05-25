package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByNumero(String numero);
    boolean existsByTipoHabitacion_Id(Long tipoHabitacionId);
}
