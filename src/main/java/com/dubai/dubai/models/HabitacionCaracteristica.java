package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "habitacion_caracteristicas")
public class HabitacionCaracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    @JsonIgnoreProperties({"caracteristicas"})
    private TipoHabitacion tipoHabitacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "caracteristica_id", nullable = false)
    @JsonIgnoreProperties({"tiposHabitacion"})
    private Caracteristica caracteristica;

    public HabitacionCaracteristica() {
    }

    public HabitacionCaracteristica(Long id, Long tipoHabitacionId, Long caracteristicaId) {
        this.id = id;
        setTipoHabitacionId(tipoHabitacionId);
        setCaracteristicaId(caracteristicaId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Caracteristica getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(Caracteristica caracteristica) {
        this.caracteristica = caracteristica;
    }

    public Long getCaracteristicaId() {
        return caracteristica != null ? caracteristica.getId() : null;
    }

    public void setCaracteristicaId(Long caracteristicaId) {
        if (caracteristicaId == null) {
            this.caracteristica = null;
            return;
        }
        Caracteristica nuevaCaracteristica = new Caracteristica();
        nuevaCaracteristica.setId(caracteristicaId);
        this.caracteristica = nuevaCaracteristica;
    }
}
