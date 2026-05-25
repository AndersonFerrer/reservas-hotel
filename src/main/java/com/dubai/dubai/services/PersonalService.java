package com.dubai.dubai.services;

import com.dubai.dubai.models.Personal;
import com.dubai.dubai.repositories.PersonalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonalService {

    private final PersonalRepository personalRepository;

    public PersonalService(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    public List<Personal> listar() {
        return personalRepository.findAll();
    }

    public Personal buscarPorId(Long id) {
        return personalRepository.findById(id).orElse(null);
    }
}
