package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tipos_habitacion")
public class TipoHabitacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double precioBase;

    @Column(nullable = false)
    private Integer capacidad;

    @ManyToMany
    @JoinTable(
            name = "tipo_habitacion_caracteristicas",
            joinColumns = @JoinColumn(name = "tipo_habitacion_id"),
            inverseJoinColumns = @JoinColumn(name = "caracteristica_id")
    )
    @JsonIgnoreProperties("tiposHabitacion")
    private Set<Caracteristica> caracteristicas = new HashSet<>();

    public TipoHabitacion() {
    }

    public TipoHabitacion(Long id, String nombre, String descripcion, Double precioBase, Integer capacidad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.capacidad = capacidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(Double precioBase) {
        this.precioBase = precioBase;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public Set<Caracteristica> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(Set<Caracteristica> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
}
