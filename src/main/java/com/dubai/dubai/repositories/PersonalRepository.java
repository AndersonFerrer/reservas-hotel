package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonalRepository extends JpaRepository<Personal, Long> {
    boolean existsByEmail(String email);
    Optional<Personal> findByEmail(String email);
}
