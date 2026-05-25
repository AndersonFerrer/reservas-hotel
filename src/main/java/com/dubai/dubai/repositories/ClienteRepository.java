package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);
}
