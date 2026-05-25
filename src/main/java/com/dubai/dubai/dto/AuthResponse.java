package com.dubai.dubai.dto;

import com.dubai.dubai.models.RolUsuario;
import com.dubai.dubai.models.TipoUsuario;

public class AuthResponse {
    private String token;
    private String tipo = "Bearer";
    private Long usuarioId;
    private String email;
    private RolUsuario rol;
    private TipoUsuario tipoUsuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long usuarioId, String email, RolUsuario rol, TipoUsuario tipoUsuario) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.email = email;
        this.rol = rol;
        this.tipoUsuario = tipoUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
