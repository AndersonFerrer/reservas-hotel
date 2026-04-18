package com.dubai.dubai.models;

import java.time.LocalDate;

public class Cupon {
    private Long id;
    private String codigo;
    private Double descuentoPorcentaje;
    private Long tipoHabitacionId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    public Cupon() {
    }

    public Cupon(Long id, String codigo, Double descuentoPorcentaje, Long tipoHabitacionId, LocalDate fechaInicio, LocalDate fechaFin) {
        this.id = id;
        this.codigo = codigo;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.tipoHabitacionId = tipoHabitacionId;
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

    public Long getTipoHabitacionId() {
        return tipoHabitacionId;
    }

    public void setTipoHabitacionId(Long tipoHabitacionId) {
        this.tipoHabitacionId = tipoHabitacionId;
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
