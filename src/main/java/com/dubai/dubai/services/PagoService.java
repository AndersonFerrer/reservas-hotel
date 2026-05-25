package com.dubai.dubai.services;

import com.dubai.dubai.models.Pago;
import com.dubai.dubai.repositories.PagoRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final ReservaRepository reservaRepository;

    public PagoService(PagoRepository pagoRepository, ReservaRepository reservaRepository) {
        this.pagoRepository = pagoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Pago> listar() {
        return pagoRepository.findAll();
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id).orElse(null);
    }

    public Pago crear(Pago pago) {
        validarPago(pago);
        validarReferenciaDisponible(pago.getReferencia(), null);
        pago.setId(null);
        return pagoRepository.save(pago);
    }

    public Pago actualizar(Long id, Pago datos) {
        Pago existente = pagoRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarPago(datos);
        validarReferenciaDisponible(datos.getReferencia(), id);

        existente.setMetodo(datos.getMetodo());
        existente.setMonto(datos.getMonto());
        existente.setFechaPago(datos.getFechaPago());
        existente.setReferencia(datos.getReferencia());
        return pagoRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!pagoRepository.existsById(id)) {
            return false;
        }
        if (reservaRepository.existsByPago_Id(id)) {
            throw new IllegalStateException("No se puede eliminar el pago porque tiene reservas asociadas");
        }
        pagoRepository.deleteById(id);
        return true;
    }

    private void validarPago(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio");
        }
        if (pago.getMetodo() == null) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio");
        }
        if (pago.getMonto() == null || pago.getMonto() <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }
        if (pago.getFechaPago() == null) {
            throw new IllegalArgumentException("La fecha de pago es obligatoria");
        }
        if (pago.getReferencia() == null || pago.getReferencia().isBlank()) {
            throw new IllegalArgumentException("La referencia del pago es obligatoria");
        }
    }

    private void validarReferenciaDisponible(String referencia, Long idActual) {
        pagoRepository.findByReferencia(referencia)
                .filter(pago -> !pago.getId().equals(idActual))
                .ifPresent(pago -> {
                    throw new IllegalArgumentException("La referencia del pago ya se encuentra registrada");
                });
    }
}
