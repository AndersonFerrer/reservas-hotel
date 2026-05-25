package com.dubai.dubai.services;

import com.dubai.dubai.models.Cupon;
import com.dubai.dubai.repositories.CuponRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuponService {

    private final CuponRepository cuponRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;

    public CuponService(CuponRepository cuponRepository, TipoHabitacionRepository tipoHabitacionRepository) {
        this.cuponRepository = cuponRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    public List<Cupon> listar() {
        return cuponRepository.findAll();
    }

    public Cupon buscarPorId(Long id) {
        return cuponRepository.findById(id).orElse(null);
    }

    public Cupon crear(Cupon cupon) {
        validarCupon(cupon);
        cupon.setTipoHabitacion(tipoHabitacionRepository.findById(cupon.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        return cuponRepository.save(cupon);
    }

    private void validarCupon(Cupon cupon) {
        if (cupon == null) {
            throw new IllegalArgumentException("El cupon es obligatorio");
        }
        if (cupon.getCodigo() == null || cupon.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El codigo del cupon es obligatorio");
        }
        if (cupon.getTipoHabitacionId() == null) {
            throw new IllegalArgumentException("tipoHabitacionId es obligatorio");
        }
    }
}
