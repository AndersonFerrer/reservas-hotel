package com.dubai.dubai.dto;

import com.dubai.dubai.models.Pago;
import com.dubai.dubai.models.Reserva;

public class ReservaConPagoRequest {
    private Reserva reserva;
    private Pago pago;

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }
}
