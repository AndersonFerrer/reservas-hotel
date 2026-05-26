package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MetodoPago metodo;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDateTime fechaPago;

    @Column(nullable = false, unique = true)
    private String referencia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id")
    @JsonIgnoreProperties({"pagos", "cliente", "habitacion", "personal"})
    private Reserva reserva;

    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    private String observacion;

    private String moneda;

    private LocalDateTime fechaRegistro;

    public Pago() {
    }

    public Pago(Long id, MetodoPago metodo, Double monto, LocalDateTime fechaPago, String referencia) {
        this.id = id;
        this.metodo = metodo;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.referencia = referencia;
    }

    public Pago(Long id, Long reservaId, MetodoPago metodo, Double monto, LocalDateTime fechaPago,
                String referencia, EstadoPago estado, String observacion, String moneda) {
        this.id = id;
        setReservaId(reservaId);
        this.metodo = metodo;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.referencia = referencia;
        this.estado = estado;
        this.observacion = observacion;
        this.moneda = moneda;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MetodoPago getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoPago metodo) {
        this.metodo = metodo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Long getReservaId() {
        return reserva != null ? reserva.getId() : null;
    }

    public void setReservaId(Long reservaId) {
        if (reservaId == null) {
            this.reserva = null;
            return;
        }
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setId(reservaId);
        this.reserva = nuevaReserva;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
