package com.dubai.dubai.services;

import com.dubai.dubai.models.EstadoHabitacion;
import com.dubai.dubai.models.Habitacion;
import com.dubai.dubai.repositories.HabitacionRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;
    private final ReservaRepository reservaRepository;

    public HabitacionService(HabitacionRepository habitacionRepository,
                             TipoHabitacionRepository tipoHabitacionRepository,
                             ReservaRepository reservaRepository) {
        this.habitacionRepository = habitacionRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Habitacion> listar() {
        return habitacionRepository.findAll();
    }

    public Habitacion buscarPorId(Long id) {
        return habitacionRepository.findById(id).orElse(null);
    }

    public Habitacion crear(Habitacion habitacion) {
        validarHabitacion(habitacion);
        validarNumeroDisponible(habitacion.getNumero(), null);
        habitacion.setId(null);
        habitacion.setTipoHabitacion(tipoHabitacionRepository.findById(habitacion.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        return habitacionRepository.save(habitacion);
    }

    public Habitacion actualizar(Long id, Habitacion datos) {
        Habitacion existente = habitacionRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarHabitacion(datos);
        validarNumeroDisponible(datos.getNumero(), id);

        existente.setNumero(datos.getNumero());
        existente.setTipoHabitacion(tipoHabitacionRepository.findById(datos.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        existente.setEstado(datos.getEstado());
        existente.setPiso(datos.getPiso());
        return habitacionRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id).orElse(null);
        if (habitacion == null) {
            return false;
        }
        habitacion.setEstado(EstadoHabitacion.MANTENIMIENTO);
        habitacionRepository.save(habitacion);
        return true;
    }

    public boolean eliminarFisicamente(Long id) {
        if (!habitacionRepository.existsById(id)) {
            return false;
        }
        if (reservaRepository.existsByHabitacion_Id(id)) {
            throw new IllegalStateException("No se puede eliminar fisicamente la habitacion porque tiene reservas asociadas");
        }
        habitacionRepository.deleteById(id);
        return true;
    }

    private void validarHabitacion(Habitacion habitacion) {
        if (habitacion == null) {
            throw new IllegalArgumentException("La habitacion es obligatoria");
        }
        if (habitacion.getNumero() == null || habitacion.getNumero().isBlank()) {
            throw new IllegalArgumentException("El numero de habitacion es obligatorio");
        }
        if (habitacion.getTipoHabitacionId() == null) {
            throw new IllegalArgumentException("tipoHabitacionId es obligatorio");
        }
        if (habitacion.getEstado() == null) {
            throw new IllegalArgumentException("El estado de habitacion es obligatorio");
        }
        if (habitacion.getPiso() == null || habitacion.getPiso() < 0) {
            throw new IllegalArgumentException("El piso debe ser cero o mayor");
        }
    }

    private void validarNumeroDisponible(String numero, Long idActual) {
        habitacionRepository.findByNumero(numero)
                .filter(habitacion -> !habitacion.getId().equals(idActual))
                .ifPresent(habitacion -> {
                    throw new IllegalArgumentException("El numero de habitacion ya se encuentra registrado");
                });
    }
}
