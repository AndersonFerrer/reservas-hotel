package com.dubai.dubai.dto;

import com.dubai.dubai.models.RolUsuario;
import com.dubai.dubai.models.TipoUsuario;

public class RegistroPersonalResponse {
    private String mensaje;
    private Long usuarioId;
    private Long personalId;
    private String email;
    private RolUsuario rol;
    private TipoUsuario tipoUsuario;

    public RegistroPersonalResponse() {
    }

    public RegistroPersonalResponse(String mensaje, Long usuarioId, Long personalId, String email, RolUsuario rol, TipoUsuario tipoUsuario) {
        this.mensaje = mensaje;
        this.usuarioId = usuarioId;
        this.personalId = personalId;
        this.email = email;
        this.rol = rol;
        this.tipoUsuario = tipoUsuario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(Long personalId) {
        this.personalId = personalId;
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
