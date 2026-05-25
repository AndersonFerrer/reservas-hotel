package com.dubai.dubai.security;

import com.dubai.dubai.models.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final String jwtSecret;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String jwtSecret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.jwtSecret = jwtSecret;
        this.expirationMs = expirationMs;
    }

    public String generarToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol().name());
        claims.put("tipoUsuario", usuario.getTipoUsuario().name());
        claims.put("usuarioId", usuario.getId());

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean tokenValido(String token, String email) {
        String subject = obtenerEmail(token);
        return subject.equals(email) && !estaExpirado(token);
    }

    public String obtenerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    private boolean estaExpirado(String token) {
        return obtenerClaims(token).getExpiration().before(new Date());
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
