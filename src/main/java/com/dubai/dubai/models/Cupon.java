package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "cupones")
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private Double descuentoPorcentaje;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    @JsonIgnoreProperties({"caracteristicas"})
    private TipoHabitacion tipoHabitacion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    public Cupon() {
    }

    public Cupon(Long id, String codigo, Double descuentoPorcentaje, Long tipoHabitacionId, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.codigo = codigo;
        this.descuentoPorcentaje = descuentoPorcentaje;
        setTipoHabitacionId(tipoHabitacionId);
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Double getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(Double descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public Long getTipoHabitacionId() {
        return tipoHabitacion != null ? tipoHabitacion.getId() : null;
    }

    public void setTipoHabitacionId(Long tipoHabitacionId) {
        if (tipoHabitacionId == null) {
            this.tipoHabitacion = null;
            return;
        }
        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setId(tipoHabitacionId);
        this.tipoHabitacion = tipo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }
}
