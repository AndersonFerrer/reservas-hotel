package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByCliente_Id(Long clienteId);
    boolean existsByCliente_Id(Long clienteId);
    boolean existsByHabitacion_Id(Long habitacionId);
    boolean existsByPersonal_Id(Long personalId);
}
