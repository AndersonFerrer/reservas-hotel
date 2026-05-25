package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "caracteristicas")
public class Caracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @JsonIgnore
    @ManyToMany(mappedBy = "caracteristicas")
    private Set<TipoHabitacion> tiposHabitacion = new HashSet<>();

    public Caracteristica() {
    }

    public Caracteristica(Long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
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

    public Set<TipoHabitacion> getTiposHabitacion() {
        return tiposHabitacion;
    }

    public void setTiposHabitacion(Set<TipoHabitacion> tiposHabitacion) {
        this.tiposHabitacion = tiposHabitacion;
    }
}
