package com.dubai.dubai.services;

import com.dubai.dubai.models.Personal;
import com.dubai.dubai.models.RolPersonal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalService {

    private final List<Personal> personal = new ArrayList<>(List.of(
            new Personal(1L, "Carlos", "Ramos", RolPersonal.ADMINISTRADOR, "987654321", "carlos@hotel.com"),
            new Personal(2L, "Maria", "Diaz", RolPersonal.CAJERO, "912345678", "maria@hotel.com")
    ));

    public List<Personal> listar() {
        return personal;
    }

    public Personal buscarPorId(Long id) {
        return personal.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
