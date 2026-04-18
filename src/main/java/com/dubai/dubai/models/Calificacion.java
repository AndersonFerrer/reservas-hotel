package com.dubai.dubai.models;

import java.time.LocalDate;

public class Calificacion {
    private Long id;
    private Long clienteId;
    private Long tipoHabitacionId;
    private Integer puntaje;
    private String comentario;
    private LocalDate fecha;

    public Calificacion() {
    }

    public Calificacion(Long id, Long clienteId, Long tipoHabitacionId, Integer puntaje, String comentario, LocalDate fecha) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipoHabitacionId = tipoHabitacionId;
        this.puntaje = puntaje;
        this.comentario = comentario;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getTipoHabitacionId() {
        return tipoHabitacionId;
    }

    public void setTipoHabitacionId(Long tipoHabitacionId) {
        this.tipoHabitacionId = tipoHabitacionId;
    }

    public Integer getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
