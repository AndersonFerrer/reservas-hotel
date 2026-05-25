package com.dubai.dubai.models;

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

    public Pago() {
    }

    public Pago(Long id, MetodoPago metodo, Double monto, LocalDateTime fechaPago, String referencia) {
        this.id = id;
        this.metodo = metodo;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.referencia = referencia;
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
}
