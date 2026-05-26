package com.dubai.dubai.repositories;

import com.dubai.dubai.models.TipoHabitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TipoHabitacionRepository extends JpaRepository<TipoHabitacion, Long> {
    @Query("SELECT DISTINCT t FROM TipoHabitacion t LEFT JOIN FETCH t.caracteristicas ORDER BY t.id ASC")
    List<TipoHabitacion> listarConJpql();

    @Query("SELECT t FROM TipoHabitacion t LEFT JOIN FETCH t.caracteristicas WHERE t.id = :id")
    Optional<TipoHabitacion> buscarPorIdConJpql(@Param("id") Long id);

    boolean existsByNombre(String nombre);
    Optional<TipoHabitacion> findByNombre(String nombre);
}
