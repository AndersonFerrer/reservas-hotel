package com.dubai.dubai.services;

import com.dubai.dubai.models.Cliente;
import com.dubai.dubai.models.Usuario;
import com.dubai.dubai.repositories.CalificacionRepository;
import com.dubai.dubai.repositories.ClienteRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import com.dubai.dubai.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final CalificacionRepository calificacionRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          ReservaRepository reservaRepository,
                          CalificacionRepository calificacionRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
        this.calificacionRepository = calificacionRepository;
    }

    public List<Cliente> listar() {
        return clienteRepository.listarConJpql();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.buscarPorIdConJpql(id).orElse(null);
    }

    public Cliente crear(Cliente cliente) {
        validarCliente(cliente);
        validarDocumentoDisponible(cliente.getDocumento(), null);
        validarEmailDisponible(cliente.getEmail(), null);
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente existente = clienteRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarCliente(datos);
        validarDocumentoDisponible(datos.getDocumento(), id);
        validarEmailDisponible(datos.getEmail(), id);

        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setDocumento(datos.getDocumento());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());

        usuarioRepository.findByCliente_Id(id).ifPresent(usuario -> {
            usuario.setEmail(datos.getEmail());
            usuarioRepository.save(usuario);
        });

        return clienteRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            return false;
        }
        if (usuarioRepository.existsByCliente_Id(id) || reservaRepository.existsByCliente_Id(id) || calificacionRepository.existsByCliente_Id(id)) {
            throw new IllegalStateException("No se puede eliminar el cliente porque tiene usuario, reservas o calificaciones asociadas");
        }
        clienteRepository.deleteById(id);
        return true;
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }
        if (esBlanco(cliente.getNombres())) {
            throw new IllegalArgumentException("Los nombres del cliente son obligatorios");
        }
        if (esBlanco(cliente.getApellidos())) {
            throw new IllegalArgumentException("Los apellidos del cliente son obligatorios");
        }
        if (esBlanco(cliente.getDocumento())) {
            throw new IllegalArgumentException("El documento del cliente es obligatorio");
        }
        if (esBlanco(cliente.getEmail())) {
            throw new IllegalArgumentException("El email del cliente es obligatorio");
        }
    }

    private void validarDocumentoDisponible(String documento, Long idActual) {
        clienteRepository.findByDocumento(documento)
                .filter(cliente -> !cliente.getId().equals(idActual))
                .ifPresent(cliente -> {
                    throw new IllegalArgumentException("El documento ya se encuentra registrado");
                });
    }

    private void validarEmailDisponible(String email, Long idActual) {
        clienteRepository.findByEmail(email)
                .filter(cliente -> !cliente.getId().equals(idActual))
                .ifPresent(cliente -> {
                    throw new IllegalArgumentException("El email ya se encuentra registrado como cliente");
                });
        usuarioRepository.findByEmail(email)
                .filter(usuario -> usuario.getCliente() == null || !usuario.getCliente().getId().equals(idActual))
                .ifPresent(usuario -> {
                    throw new IllegalArgumentException("El email ya se encuentra registrado como usuario");
                });
    }

    private boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
