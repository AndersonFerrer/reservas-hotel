package com.dubai.dubai.repositories;

import com.dubai.dubai.models.Personal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalRepository extends JpaRepository<Personal, Long> {
    boolean existsByEmail(String email);
}
