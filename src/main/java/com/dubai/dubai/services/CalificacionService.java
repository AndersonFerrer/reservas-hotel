package com.dubai.dubai.services;

import com.dubai.dubai.models.Calificacion;
import com.dubai.dubai.repositories.CalificacionRepository;
import com.dubai.dubai.repositories.ClienteRepository;
import com.dubai.dubai.repositories.TipoHabitacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ClienteRepository clienteRepository;
    private final TipoHabitacionRepository tipoHabitacionRepository;

    public CalificacionService(CalificacionRepository calificacionRepository,
                               ClienteRepository clienteRepository,
                               TipoHabitacionRepository tipoHabitacionRepository) {
        this.calificacionRepository = calificacionRepository;
        this.clienteRepository = clienteRepository;
        this.tipoHabitacionRepository = tipoHabitacionRepository;
    }

    public List<Calificacion> listar() {
        return calificacionRepository.findAll();
    }

    public Calificacion buscarPorId(Long id) {
        return calificacionRepository.findById(id).orElse(null);
    }

    public Calificacion crear(Calificacion calificacion) {
        validarCalificacion(calificacion);
        calificacion.setCliente(clienteRepository.findById(calificacion.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente indicado no existe")));
        calificacion.setTipoHabitacion(tipoHabitacionRepository.findById(calificacion.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        return calificacionRepository.save(calificacion);
    }

    public Calificacion actualizar(Long id, Calificacion datos) {
        Calificacion existente = calificacionRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarCalificacion(datos);
        existente.setCliente(clienteRepository.findById(datos.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente indicado no existe")));
        existente.setTipoHabitacion(tipoHabitacionRepository.findById(datos.getTipoHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("El tipo de habitacion indicado no existe")));
        existente.setPuntaje(datos.getPuntaje());
        existente.setComentario(datos.getComentario());
        existente.setFecha(datos.getFecha());
        return calificacionRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!calificacionRepository.existsById(id)) {
            return false;
        }
        calificacionRepository.deleteById(id);
        return true;
    }

    private void validarCalificacion(Calificacion calificacion) {
        if (calificacion == null) {
            throw new IllegalArgumentException("La calificacion es obligatoria");
        }
        if (calificacion.getClienteId() == null || calificacion.getTipoHabitacionId() == null) {
            throw new IllegalArgumentException("clienteId y tipoHabitacionId son obligatorios");
        }
        if (calificacion.getPuntaje() == null || calificacion.getPuntaje() < 1 || calificacion.getPuntaje() > 5) {
            throw new IllegalArgumentException("El puntaje debe estar entre 1 y 5");
        }
        if (calificacion.getComentario() == null || calificacion.getComentario().isBlank()) {
            throw new IllegalArgumentException("El comentario es obligatorio");
        }
        if (calificacion.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la calificacion es obligatoria");
        }
    }
}
