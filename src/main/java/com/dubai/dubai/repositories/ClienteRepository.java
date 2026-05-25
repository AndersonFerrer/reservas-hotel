package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByDocumento(String documento);
}
