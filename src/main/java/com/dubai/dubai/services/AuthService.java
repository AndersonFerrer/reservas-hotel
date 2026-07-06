package com.dubai.dubai.services;

import com.dubai.dubai.dto.AuthResponse;
import com.dubai.dubai.dto.LoginRequest;
import com.dubai.dubai.dto.RegistroClienteRequest;
import com.dubai.dubai.dto.RegistroPersonalRequest;
import com.dubai.dubai.dto.RegistroPersonalResponse;
import com.dubai.dubai.models.Cliente;
import com.dubai.dubai.models.Personal;
import com.dubai.dubai.models.RolPersonal;
import com.dubai.dubai.models.RolUsuario;
import com.dubai.dubai.models.TipoUsuario;
import com.dubai.dubai.models.Usuario;
import com.dubai.dubai.repositories.ClienteRepository;
import com.dubai.dubai.repositories.PersonalRepository;
import com.dubai.dubai.repositories.UsuarioRepository;
import com.dubai.dubai.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PersonalRepository personalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       ClienteRepository clienteRepository,
                       PersonalRepository personalRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.personalRepository = personalRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrarCliente(RegistroClienteRequest request) {
        validarEmailDisponible(request.getEmail());
        if (clienteRepository.existsByDocumento(request.getDocumento())) {
            throw new IllegalArgumentException("El documento ya se encuentra registrado");
        }

        Cliente cliente = new Cliente(null, request.getNombres(), request.getApellidos(), request.getDocumento(), request.getTelefono(), request.getEmail());
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setCliente(cliente);

        return construirRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional
    public RegistroPersonalResponse registrarPersonal(RegistroPersonalRequest request) {
        validarEmailDisponible(request.getEmail());
        if (personalRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya se encuentra registrado como personal");
        }

        Personal personal = new Personal(null, request.getNombres(), request.getApellidos(), request.getRol(), request.getTelefono(), request.getEmail());
        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(mapearRolPersonal(request.getRol()));
        usuario.setTipoUsuario(TipoUsuario.PERSONAL);
        usuario.setPersonal(personal);

        Usuario guardado = usuarioRepository.save(usuario);
        return new RegistroPersonalResponse(
                "Personal registrado correctamente",
                guardado.getId(),
                guardado.getPersonal() != null ? guardado.getPersonal().getId() : null,
                guardado.getEmail(),
                guardado.getRol(),
                guardado.getTipoUsuario()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales invalidas");
        }

        return construirRespuesta(usuario);
    }

    private void validarEmailDisponible(String email) {
        if (usuarioRepository.existsByEmail(email) || clienteRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya se encuentra registrado");
        }
    }

    private AuthResponse construirRespuesta(Usuario usuario) {
        String token = jwtService.generarToken(usuario);
        long expiresIn = jwtService.getExpirationSeconds();

        String nombres = null;
        String apellidos = null;
        if (usuario.getCliente() != null) {
            nombres = usuario.getCliente().getNombres();
            apellidos = usuario.getCliente().getApellidos();
        } else if (usuario.getPersonal() != null) {
            nombres = usuario.getPersonal().getNombres();
            apellidos = usuario.getPersonal().getApellidos();
        }

        AuthResponse.UsuarioResumen resumen = new AuthResponse.UsuarioResumen(
                usuario.getId(),
                usuario.getEmail(),
                nombres,
                apellidos,
                construirNombreCompleto(nombres, apellidos),
                usuario.getRol()
        );

        return new AuthResponse(token, expiresIn, resumen);
    }

    private String construirNombreCompleto(String nombres, String apellidos) {
        String n = nombres == null ? "" : nombres.trim();
        String a = apellidos == null ? "" : apellidos.trim();
        String combinado = (n + " " + a).trim();
        return combinado.isEmpty() ? null : combinado;
    }

    private RolUsuario mapearRolPersonal(RolPersonal rolPersonal) {
        if (rolPersonal == RolPersonal.ADMINISTRADOR) {
            return RolUsuario.ADMINISTRADOR;
        }
        return RolUsuario.CAJERO;
    }
}
