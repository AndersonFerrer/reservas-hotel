package com.dubai.dubai.dto;

import com.dubai.dubai.models.RolUsuario;

/**
 * Respuesta del endpoint de autenticación (login y registro de cliente).
 *
 * <p>Estructura intencionalmente separada en dos bloques:
 * <ul>
 *   <li><b>Cabecera</b>: datos de la credencial ({@code token}, {@code tipo}, {@code expiresIn}).
 *   <li><b>usuario</b>: perfil del usuario autenticado para uso inmediato en UI.
 * </ul>
 *
 * <p>Se elimina {@code tipoUsuario} (duplicaba el valor de {@code rol} en la mayoría de casos)
 * y {@code usuarioId} (ahora vive como {@code id} dentro de {@code usuario}).
 */
public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private long expiresIn;
    private UsuarioResumen usuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, long expiresIn, UsuarioResumen usuario) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.usuario = usuario;
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

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UsuarioResumen getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResumen usuario) {
        this.usuario = usuario;
    }

    /**
     * Perfil resumido del usuario autenticado, pensado para hidratación inmediata
     * del estado de sesión en el cliente (sin decodificar el JWT).
     *
     * <p>{@code nombreCompleto} se calcula server-side para evitar que cada cliente
     * tenga que recomponerlo y para soportar futuros casos donde el formato no sea
     * trivialmente "{nombres} {apellidos}".
     */
    public static class UsuarioResumen {
        private Long id;
        private String email;
        private String nombres;
        private String apellidos;
        private String nombreCompleto;
        private RolUsuario rol;

        public UsuarioResumen() {
        }

        public UsuarioResumen(Long id, String email, String nombres, String apellidos,
                              String nombreCompleto, RolUsuario rol) {
            this.id = id;
            this.email = email;
            this.nombres = nombres;
            this.apellidos = apellidos;
            this.nombreCompleto = nombreCompleto;
            this.rol = rol;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public void setNombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
        }

        public RolUsuario getRol() {
            return rol;
        }

        public void setRol(RolUsuario rol) {
            this.rol = rol;
        }
    }
}