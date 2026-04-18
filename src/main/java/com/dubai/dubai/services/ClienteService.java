package com.dubai.dubai.services;

import com.dubai.dubai.models.Cliente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final List<Cliente> clientes = new ArrayList<>(List.of(
            new Cliente(1L, "Ana", "Lopez", "12345678", "999111222", "ana@correo.com"),
            new Cliente(2L, "Luis", "Perez", "87654321", "988777666", "luis@correo.com")
    ));

    public List<Cliente> listar() {
        return clientes;
    }

    public Cliente buscarPorId(Long id) {
        return clientes.stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
