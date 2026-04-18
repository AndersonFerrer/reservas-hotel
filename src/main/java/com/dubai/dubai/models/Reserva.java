package com.dubai.dubai.models;

import java.time.LocalDate;

public class Reserva {
    private Long id;
    private Long clienteId;
    private Long habitacionId;
    private Long pagoId;
    private Long personalId;
    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;
    private EstadoReserva estado;

    public Reserva() {
    }

    public Reserva(Long id, Long clienteId, Long habitacionId, Long pagoId, Long personalId,
                   LocalDate fechaIngreso, LocalDate fechaSalida, EstadoReserva estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.habitacionId = habitacionId;
        this.pagoId = pagoId;
        this.personalId = personalId;
        this.fechaIngreso = fechaIngreso;
        this.fechaSalida = fechaSalida;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getHabitacionId() {
        return habitacionId;
    }

    public void setHabitacionId(Long habitacionId) {
        this.habitacionId = habitacionId;
    }

    public Long getPagoId() {
        return pagoId;
    }

    public void setPagoId(Long pagoId) {
        this.pagoId = pagoId;
    }

    public Long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }
}
