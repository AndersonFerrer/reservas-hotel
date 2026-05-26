package com.dubai.dubai.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(JWTAuthorizationFilter jwtAuthorizationFilter) {
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"No autorizado\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.getWriter().write("{\"error\":\"Acceso denegado\"}");
                        })
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reservas/mis-reservas").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/mis-reservas").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/mis-reservas/con-pago").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/clientes", "/api/clientes/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/clientes").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/personal", "/api/personal/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/pagos", "/api/pagos/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/pagos").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/pagos/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/pagos/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/reservas", "/api/reservas/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/con-pago").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/reservas/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservas/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/habitacion-caracteristicas", "/api/habitacion-caracteristicas/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/habitacion-caracteristicas").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/habitacion-caracteristicas/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/tipos-habitacion", "/api/tipos-habitacion/**").hasAnyRole("ADMINISTRADOR", "CAJERO", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/habitaciones", "/api/habitaciones/**").hasAnyRole("ADMINISTRADOR", "CAJERO", "CLIENTE")
                        .requestMatchers(HttpMethod.POST, "/api/habitaciones").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.PUT, "/api/habitaciones/**").hasAnyRole("ADMINISTRADOR", "CAJERO")
                        .requestMatchers(HttpMethod.GET, "/api/cupones", "/api/cupones/**").hasAnyRole("ADMINISTRADOR", "CAJERO", "CLIENTE")
                        .requestMatchers(HttpMethod.GET, "/api/calificaciones", "/api/calificaciones/**").hasAnyRole("ADMINISTRADOR", "CAJERO", "CLIENTE")
                        .anyRequest().hasRole("ADMINISTRADOR")
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
