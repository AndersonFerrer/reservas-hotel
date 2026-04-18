package com.dubai.dubai.services;

import com.dubai.dubai.models.EstadoReserva;
import com.dubai.dubai.models.Reserva;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaService {

    private final List<Reserva> reservas = new ArrayList<>(List.of(
            new Reserva(1L, 1L, 1L, 1L, 1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), EstadoReserva.CONFIRMADA),
            new Reserva(2L, 2L, 2L, 2L, 2L, LocalDate.now().plusDays(4), LocalDate.now().plusDays(5), EstadoReserva.PENDIENTE)
    ));

    public List<Reserva> listar() {
        return reservas;
    }

    public Reserva buscarPorId(Long id) {
        return reservas.stream()
                .filter(reserva -> reserva.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Reserva crear(Reserva reserva) {
        validarReserva(reserva);

        if (reserva.getEstado() == null) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }

        long nuevoId = reservas.stream().mapToLong(Reserva::getId).max().orElse(0L) + 1;
        reserva.setId(nuevoId);
        reservas.add(reserva);
        return reserva;
    }

    public long calcularNoches(Reserva reserva) {
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(reserva.getFechaIngreso(), reserva.getFechaSalida());
    }

    private void validarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva es obligatoria");
        }
        if (reserva.getClienteId() == null || reserva.getHabitacionId() == null || reserva.getPagoId() == null || reserva.getPersonalId() == null) {
            throw new IllegalArgumentException("clienteId, habitacionId, pagoId y personalId son obligatorios");
        }
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            throw new IllegalArgumentException("Las fechas de ingreso y salida son obligatorias");
        }
        if (!reserva.getFechaSalida().isAfter(reserva.getFechaIngreso())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de ingreso");
        }
    }
}
