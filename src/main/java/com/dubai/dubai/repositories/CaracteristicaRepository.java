package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Long> {
    boolean existsByNombre(String nombre);
    Optional<Caracteristica> findByNombre(String nombre);
}
