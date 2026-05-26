package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaracteristicaRepository extends JpaRepository<Caracteristica, Long> {
    @Query("SELECT c FROM Caracteristica c ORDER BY c.id ASC")
    List<Caracteristica> listarConJpql();

    @Query("SELECT c FROM Caracteristica c WHERE c.id = :id")
    Optional<Caracteristica> buscarPorIdConJpql(@Param("id") Long id);

    boolean existsByNombre(String nombre);
    Optional<Caracteristica> findByNombre(String nombre);
}
