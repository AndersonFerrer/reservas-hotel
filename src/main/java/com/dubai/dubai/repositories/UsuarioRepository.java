package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCliente_Id(Long clienteId);
    Optional<Usuario> findByPersonal_Id(Long personalId);
    boolean existsByEmail(String email);
    boolean existsByCliente_Id(Long clienteId);
    boolean existsByPersonal_Id(Long personalId);
}
