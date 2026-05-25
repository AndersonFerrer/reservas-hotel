package com.dubai.dubai.dto;

import com.dubai.dubai.models.RolPersonal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistroPersonalRequest {
    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @NotNull
    private RolPersonal rol;

    private String telefono;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
