package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReferencia(String referencia);
    List<Pago> findByReserva_Id(Long reservaId);
    boolean existsByReserva_Id(Long reservaId);
}
