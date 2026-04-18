package com.dubai.dubai.models;

public class HabitacionCaracteristica {
    private Long id;
    private Long tipoHabitacionId;
    private Long caracteristicaId;

    public HabitacionCaracteristica() {
    }

    public HabitacionCaracteristica(Long id, Long tipoHabitacionId, Long caracteristicaId) {
        this.id = id;
        this.tipoHabitacionId = tipoHabitacionId;
        this.caracteristicaId = caracteristicaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTipoHabitacionId() {
        return tipoHabitacionId;
    }

    public void setTipoHabitacionId(Long tipoHabitacionId) {
        this.tipoHabitacionId = tipoHabitacionId;
    }

    public Long getCaracteristicaId() {
        return caracteristicaId;
    }

    public void setCaracteristicaId(Long caracteristicaId) {
        this.caracteristicaId = caracteristicaId;
    }
}
