package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuponRepository extends JpaRepository<Cupon, Long> {
    boolean existsByCodigo(String codigo);
    Optional<Cupon> findByCodigo(String codigo);
    boolean existsByTipoHabitacion_Id(Long tipoHabitacionId);
}
