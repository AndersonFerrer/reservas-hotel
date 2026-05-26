package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties("usuario")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "habitacion_id", nullable = false)
    @JsonIgnoreProperties("tipoHabitacion")
    private Habitacion habitacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "personal_id", nullable = false)
    @JsonIgnoreProperties("usuario")
    private Personal personal;

    @Column(nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("reserva")
    private List<Pago> pagos = new ArrayList<>();

    public Reserva() {
    }

    public Reserva(Long id, Long clienteId, Long habitacionId, Long pagoId, Long personalId,
                   LocalDate fechaIngreso, LocalDate fechaSalida, EstadoReserva estado) {
        this.id = id;
        setClienteId(clienteId);
        setHabitacionId(habitacionId);
        setPersonalId(personalId);
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getClienteId() {
        return cliente != null ? cliente.getId() : null;
    }

    public void setClienteId(Long clienteId) {
        if (clienteId == null) {
            this.cliente = null;
            return;
        }
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setId(clienteId);
        this.cliente = nuevoCliente;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public Long getHabitacionId() {
        return habitacion != null ? habitacion.getId() : null;
    }

    public void setHabitacionId(Long habitacionId) {
        if (habitacionId == null) {
            this.habitacion = null;
            return;
        }
        Habitacion nuevaHabitacion = new Habitacion();
        nuevaHabitacion.setId(habitacionId);
        this.habitacion = nuevaHabitacion;
    }

    @JsonIgnore
    public Long getPagoId() {
        return null;
    }

    @JsonIgnore
    public void setPagoId(Long pagoId) {
    }

    public Personal getPersonal() {
        return personal;
    }

    public void setPersonal(Personal personal) {
        this.personal = personal;
    }

    public Long getPersonalId() {
        return personal != null ? personal.getId() : null;
    }

    public void setPersonalId(Long personalId) {
        if (personalId == null) {
            this.personal = null;
            return;
        }
        Personal nuevaPersona = new Personal();
        nuevaPersona.setId(personalId);
        this.personal = nuevaPersona;
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

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }
}
