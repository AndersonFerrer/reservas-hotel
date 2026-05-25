package com.dubai.dubai.services;

import com.dubai.dubai.models.Caracteristica;
import com.dubai.dubai.repositories.CaracteristicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaracteristicaService {

    private final CaracteristicaRepository caracteristicaRepository;

    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository) {
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<Caracteristica> listar() {
        return caracteristicaRepository.findAll();
    }

    public Caracteristica buscarPorId(Long id) {
        return caracteristicaRepository.findById(id).orElse(null);
    }
}
