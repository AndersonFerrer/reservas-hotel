package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {
    Optional<Habitacion> findByNumero(String numero);
    boolean existsByTipoHabitacion_Id(Long tipoHabitacionId);

    @Query("SELECT DISTINCT h FROM Habitacion h " +
           "JOIN FETCH h.tipoHabitacion t " +
           "LEFT JOIN FETCH t.caracteristicas c " +
           "WHERE h.estado <> com.dubai.dubai.models.EstadoHabitacion.MANTENIMIENTO " +
           "AND t.capacidad >= :huespedes " +
           "AND NOT EXISTS (" +
           "  SELECT 1 FROM Reserva r " +
           "  WHERE r.habitacion = h " +
           "    AND r.estado IN (com.dubai.dubai.models.EstadoReserva.PENDIENTE, com.dubai.dubai.models.EstadoReserva.CONFIRMADA) " +
           "    AND r.fechaIngreso < :fechaSalida " +
           "    AND r.fechaSalida > :fechaIngreso" +
           ") " +
           "ORDER BY t.id ASC, h.numero ASC")
    List<Habitacion> findDisponibles(@Param("fechaIngreso") LocalDate fechaIngreso,
                                     @Param("fechaSalida") LocalDate fechaSalida,
                                     @Param("huespedes") Integer huespedes);
}
