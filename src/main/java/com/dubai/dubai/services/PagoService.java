package com.dubai.dubai.services;

import com.dubai.dubai.models.MetodoPago;
import com.dubai.dubai.models.Pago;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagoService {

    private final List<Pago> pagos = new ArrayList<>(List.of(
            new Pago(1L, MetodoPago.EFECTIVO, 240.0, LocalDateTime.now().minusDays(2), "REF-001"),
            new Pago(2L, MetodoPago.TARJETA, 300.0, LocalDateTime.now().minusDays(1), "REF-002")
    ));

    public List<Pago> listar() {
        return pagos;
    }

    public Pago buscarPorId(Long id) {
        return pagos.stream()
                .filter(pago -> pago.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
