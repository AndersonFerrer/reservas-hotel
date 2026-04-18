package com.dubai.dubai.models;

public class Habitacion {
    private Long id;
    private String numero;
    private Long tipoHabitacionId;
    private EstadoHabitacion estado;
    private Integer piso;

    public Habitacion() {
    }

    public Habitacion(Long id, String numero, Long tipoHabitacionId, EstadoHabitacion estado, Integer piso) {
        this.id = id;
        this.numero = numero;
        this.tipoHabitacionId = tipoHabitacionId;
        this.estado = estado;
        this.piso = piso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getTipoHabitacionId() {
        return tipoHabitacionId;
    }

    public void setTipoHabitacionId(Long tipoHabitacionId) {
        this.tipoHabitacionId = tipoHabitacionId;
    }

    public EstadoHabitacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHabitacion estado) {
        this.estado = estado;
    }

    public Integer getPiso() {
        return piso;
    }

    public void setPiso(Integer piso) {
        this.piso = piso;
    }
}
