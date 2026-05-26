package com.dubai.dubai.services;

import com.dubai.dubai.models.EstadoPago;
import com.dubai.dubai.models.Pago;
import com.dubai.dubai.models.Reserva;
import com.dubai.dubai.repositories.PagoRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        pago.setReserva(obtenerReserva(pago.getReservaId()));
        completarDatosPorDefecto(pago);
        pago.setId(null);
        return pagoRepository.save(pago);
    }

    public Pago crearParaReserva(Pago pago, Reserva reserva) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio");
        }
        pago.setReserva(reserva);
        validarPago(pago);
        validarReferenciaDisponible(pago.getReferencia(), null);
        completarDatosPorDefecto(pago);
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

        existente.setReserva(obtenerReserva(datos.getReservaId()));
        existente.setMetodo(datos.getMetodo());
        existente.setMonto(datos.getMonto());
        existente.setFechaPago(datos.getFechaPago());
        existente.setReferencia(datos.getReferencia());
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : EstadoPago.PAGADO);
        existente.setObservacion(datos.getObservacion());
        existente.setMoneda(normalizarMoneda(datos.getMoneda()));
        if (existente.getFechaRegistro() == null) {
            existente.setFechaRegistro(LocalDateTime.now());
        }
        return pagoRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        Pago pago = pagoRepository.findById(id).orElse(null);
        if (pago == null) {
            return false;
        }
        if (pago.getReservaId() != null) {
            pago.setEstado(EstadoPago.ANULADO);
            pagoRepository.save(pago);
            return true;
        }
        pagoRepository.deleteById(id);
        return true;
    }

    public List<Pago> listarPorReserva(Long reservaId) {
        if (!reservaRepository.existsById(reservaId)) {
            throw new IllegalArgumentException("La reserva indicada no existe");
        }
        return pagoRepository.findByReserva_Id(reservaId);
    }

    private void validarPago(Pago pago) {
        if (pago == null) {
            throw new IllegalArgumentException("El pago es obligatorio");
        }
        if (pago.getReservaId() == null) {
            throw new IllegalArgumentException("reservaId es obligatorio");
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

    private Reserva obtenerReserva(Long reservaId) {
        return reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("La reserva indicada no existe"));
    }

    private void completarDatosPorDefecto(Pago pago) {
        if (pago.getEstado() == null) {
            pago.setEstado(EstadoPago.PAGADO);
        }
        pago.setMoneda(normalizarMoneda(pago.getMoneda()));
        if (pago.getFechaRegistro() == null) {
            pago.setFechaRegistro(LocalDateTime.now());
        }
    }

    private String normalizarMoneda(String moneda) {
        if (moneda == null || moneda.isBlank()) {
            return "PEN";
        }
        return moneda.trim().toUpperCase();
    }

    private void validarReferenciaDisponible(String referencia, Long idActual) {
        pagoRepository.findByReferencia(referencia)
                .filter(pago -> !pago.getId().equals(idActual))
                .ifPresent(pago -> {
                    throw new IllegalArgumentException("La referencia del pago ya se encuentra registrada");
                });
    }
}
