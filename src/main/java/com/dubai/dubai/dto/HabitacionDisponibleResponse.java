package com.dubai.dubai.dto;

import java.util.List;

public record HabitacionDisponibleResponse(
        Long tipoHabitacionId,
        List<HabitacionResumen> habitaciones,
        Double precioBase,
        Integer huespedes,
        List<String> caracteristicas,
        String nombre,
        String descripcion) {

    public record HabitacionResumen(Long habitacionId, String numero) {
    }
}
