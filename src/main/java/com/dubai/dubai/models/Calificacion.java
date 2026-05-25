package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "calificaciones")
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties("usuario")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_habitacion_id", nullable = false)
    @JsonIgnoreProperties({"caracteristicas"})
    private TipoHabitacion tipoHabitacion;

    @Column(nullable = false)
    private Integer puntaje;

    @Column(nullable = false)
    private String comentario;

    @Column(nullable = false)
    private LocalDate fecha;

    public Calificacion() {
    }

    public Calificacion(Long id, Long clienteId, Long tipoHabitacionId, Integer puntaje, String comentario, LocalDate fecha) {
        this.id = id;
        setClienteId(clienteId);
        setTipoHabitacionId(tipoHabitacionId);
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getClienteId() {
        return cliente != null ? cliente.getId() : null;
    }

    public void setClienteId(Long clienteId) {
        if (clienteId == null) {
            this.cliente = null;
            return;
        }
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setId(clienteId);
        this.cliente = nuevoCliente;
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
