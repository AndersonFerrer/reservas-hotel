package com.dubai.dubai.services;

import com.dubai.dubai.models.EstadoReserva;
import com.dubai.dubai.models.Reserva;
import com.dubai.dubai.models.Usuario;
import com.dubai.dubai.repositories.ClienteRepository;
import com.dubai.dubai.repositories.HabitacionRepository;
import com.dubai.dubai.repositories.PagoRepository;
import com.dubai.dubai.repositories.PersonalRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import com.dubai.dubai.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final HabitacionRepository habitacionRepository;
    private final PagoRepository pagoRepository;
    private final PersonalRepository personalRepository;
    private final UsuarioRepository usuarioRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          ClienteRepository clienteRepository,
                          HabitacionRepository habitacionRepository,
                          PagoRepository pagoRepository,
                          PersonalRepository personalRepository,
                          UsuarioRepository usuarioRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.habitacionRepository = habitacionRepository;
        this.pagoRepository = pagoRepository;
        this.personalRepository = personalRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Reserva> listar() {
        return reservaRepository.findAll();
    }

    public Reserva buscarPorId(Long id) {
        return reservaRepository.findById(id).orElse(null);
    }

    public Reserva crear(Reserva reserva) {
        validarReserva(reserva);
        reserva.setCliente(clienteRepository.findById(reserva.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente indicado no existe")));
        return guardarReservaValidada(reserva);
    }

    public Reserva actualizar(Long id, Reserva datos) {
        Reserva existente = reservaRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarReserva(datos);
        existente.setCliente(clienteRepository.findById(datos.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("El cliente indicado no existe")));
        existente.setHabitacion(habitacionRepository.findById(datos.getHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("La habitacion indicada no existe")));
        existente.setPago(pagoRepository.findById(datos.getPagoId())
                .orElseThrow(() -> new IllegalArgumentException("El pago indicado no existe")));
        existente.setPersonal(personalRepository.findById(datos.getPersonalId())
                .orElseThrow(() -> new IllegalArgumentException("El personal indicado no existe")));
        existente.setFechaIngreso(datos.getFechaIngreso());
        existente.setFechaSalida(datos.getFechaSalida());
        existente.setEstado(datos.getEstado() != null ? datos.getEstado() : EstadoReserva.PENDIENTE);
        return reservaRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva == null) {
            return false;
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
        return true;
    }

    public List<Reserva> listarPorClienteAutenticado(String email) {
        Usuario usuario = obtenerUsuarioCliente(email);
        return reservaRepository.findByCliente_Id(usuario.getCliente().getId());
    }

    public Reserva crearParaClienteAutenticado(Reserva reserva, String email) {
        Usuario usuario = obtenerUsuarioCliente(email);
        if (reserva != null) {
            reserva.setCliente(usuario.getCliente());
        }
        validarReserva(reserva);
        return guardarReservaValidada(reserva);
    }

    private Reserva guardarReservaValidada(Reserva reserva) {
        reserva.setHabitacion(habitacionRepository.findById(reserva.getHabitacionId())
                .orElseThrow(() -> new IllegalArgumentException("La habitacion indicada no existe")));
        reserva.setPago(pagoRepository.findById(reserva.getPagoId())
                .orElseThrow(() -> new IllegalArgumentException("El pago indicado no existe")));
        reserva.setPersonal(personalRepository.findById(reserva.getPersonalId())
                .orElseThrow(() -> new IllegalArgumentException("El personal indicado no existe")));

        if (reserva.getEstado() == null) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }

        return reservaRepository.save(reserva);
    }

    private Usuario obtenerUsuarioCliente(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado no existe"));
        if (usuario.getCliente() == null) {
            throw new IllegalArgumentException("El usuario autenticado no tiene perfil de cliente");
        }
        return usuario;
    }

    public long calcularNoches(Reserva reserva) {
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(reserva.getFechaIngreso(), reserva.getFechaSalida());
    }

    private void validarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva es obligatoria");
        }
        if (reserva.getClienteId() == null || reserva.getHabitacionId() == null || reserva.getPagoId() == null || reserva.getPersonalId() == null) {
            throw new IllegalArgumentException("clienteId, habitacionId, pagoId y personalId son obligatorios");
        }
        if (reserva.getFechaIngreso() == null || reserva.getFechaSalida() == null) {
            throw new IllegalArgumentException("Las fechas de ingreso y salida son obligatorias");
        }
        if (!reserva.getFechaSalida().isAfter(reserva.getFechaIngreso())) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de ingreso");
        }
    }
}
