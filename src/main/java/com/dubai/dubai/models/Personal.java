package com.dubai.dubai.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "personal")
public class Personal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolPersonal rol;

    private String telefono;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @OneToOne(mappedBy = "personal")
    private Usuario usuario;

    public Personal() {
    }

    public Personal(Long id, String nombres, String apellidos, RolPersonal rol, String telefono, String email) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
        this.telefono = telefono;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public RolPersonal getRol() {
        return rol;
    }

    public void setRol(RolPersonal rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
