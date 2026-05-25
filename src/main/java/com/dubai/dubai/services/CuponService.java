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
        validarCodigoDisponible(cupon.getCodigo(), null);
        cupon.setTipoHabitacion(tipoHabitacionRepository.findById(cupon.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        return cuponRepository.save(cupon);
    }

    public Cupon actualizar(Long id, Cupon datos) {
        Cupon existente = cuponRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarCupon(datos);
        validarCodigoDisponible(datos.getCodigo(), id);

        existente.setCodigo(datos.getCodigo());
        existente.setDescuentoPorcentaje(datos.getDescuentoPorcentaje());
        existente.setTipoHabitacion(tipoHabitacionRepository.findById(datos.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        existente.setFechaInicio(datos.getFechaInicio());
        existente.setFechaFin(datos.getFechaFin());
        return cuponRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!cuponRepository.existsById(id)) {
            return false;
        }
        cuponRepository.deleteById(id);
        return true;
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
        if (cupon.getDescuentoPorcentaje() == null || cupon.getDescuentoPorcentaje() <= 0 || cupon.getDescuentoPorcentaje() > 100) {
            throw new IllegalArgumentException("El descuento debe estar entre 1 y 100");
        }
        if (cupon.getFechaInicio() == null || cupon.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas del cupon son obligatorias");
        }
        if (cupon.getFechaFin().isBefore(cupon.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha fin del cupon no puede ser anterior a la fecha inicio");
        }
    }

    private void validarCodigoDisponible(String codigo, Long idActual) {
        cuponRepository.findByCodigo(codigo)
                .filter(cupon -> !cupon.getId().equals(idActual))
                .ifPresent(cupon -> {
                    throw new IllegalArgumentException("El codigo del cupon ya se encuentra registrado");
                });
    }
}
