package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT c FROM Cliente c ORDER BY c.id ASC")
    List<Cliente> listarConJpql();

    @Query("SELECT c FROM Cliente c WHERE c.id = :id")
    Optional<Cliente> buscarPorIdConJpql(@Param("id") Long id);

    boolean existsByEmail(String email);
    boolean existsByDocumento(String documento);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByDocumento(String documento);
}
