package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "habitaciones")
public class Habitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    @JsonIgnoreProperties({"caracteristicas"})
    private TipoHabitacion tipoHabitacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHabitacion estado;

    @Column(nullable = false)
    private Integer piso;

    public Habitacion() {
    }

    public Habitacion(Long id, String numero, Long tipoHabitacionId, EstadoHabitacion estado, Integer piso) {
        this.id = id;
        this.numero = numero;
        setTipoHabitacionId(tipoHabitacionId);
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
