package com.dubai.dubai.services;

import com.dubai.dubai.dto.AuthResponse;
import com.dubai.dubai.dto.LoginRequest;
import com.dubai.dubai.dto.RegistroClienteRequest;
import com.dubai.dubai.dto.RegistroPersonalRequest;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService sut;

    // ───────────── login ─────────────

    @Test
    void login_credencialesValidas_comoCliente_retornaAuthResponseConUsuarioAnidado() {
        Cliente cliente = new Cliente(10L, "Ana", "Lopez", "12345678", "999111222", "ana@correo.com");
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("ana@correo.com");
        usuario.setPassword("hash");
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);
        usuario.setCliente(cliente);

        doReturn(Optional.of(usuario)).when(usuarioRepository).findByEmail("ana@correo.com");
        doReturn(true).when(passwordEncoder).matches("secret123", "hash");
        doReturn("jwt.token").when(jwtService).generarToken(usuario);
        doReturn(86400L).when(jwtService).getExpirationSeconds();

        AuthResponse response = sut.login(loginRequest("ana@correo.com", "secret123"));

        assertEquals("jwt.token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        assertEquals(86400L, response.getExpiresIn());
        assertNotNull(response.getUsuario());

        AuthResponse.UsuarioResumen resumen = response.getUsuario();
        assertEquals(1L, resumen.getId());
        assertEquals("ana@correo.com", resumen.getEmail());
        assertEquals("Ana", resumen.getNombres());
        assertEquals("Lopez", resumen.getApellidos());
        assertEquals("Ana Lopez", resumen.getNombreCompleto());
        assertEquals(RolUsuario.CLIENTE, resumen.getRol());
        // No exponemos tipoUsuario redundante con rol
        verify(jwtService).generarToken(usuario);
    }

    @Test
    void login_credencialesValidas_comoPersonal_retornaUsuarioConDatosDePersonal() {
        Personal personal = new Personal(20L, "Carlos", "Ramos", RolPersonal.ADMINISTRADOR, "999", "carlos@hotel.com");
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setEmail("carlos@hotel.com");
        usuario.setPassword("hash");
        usuario.setRol(RolUsuario.ADMINISTRADOR);
        usuario.setTipoUsuario(TipoUsuario.PERSONAL);
        usuario.setPersonal(personal);

        doReturn(Optional.of(usuario)).when(usuarioRepository).findByEmail("carlos@hotel.com");
        doReturn(true).when(passwordEncoder).matches("secret123", "hash");
        doReturn("jwt.token").when(jwtService).generarToken(usuario);
        doReturn(86400L).when(jwtService).getExpirationSeconds();

        AuthResponse response = sut.login(loginRequest("carlos@hotel.com", "secret123"));

        AuthResponse.UsuarioResumen resumen = response.getUsuario();
        assertEquals(2L, resumen.getId());
        assertEquals("Carlos", resumen.getNombres());
        assertEquals("Ramos", resumen.getApellidos());
        assertEquals("Carlos Ramos", resumen.getNombreCompleto());
        assertEquals(RolUsuario.ADMINISTRADOR, resumen.getRol());
    }

    @Test
    void login_sinPerfilAsociado_retornaNombreCompletoNull() {
        // Cubre el caso degenerado donde el Usuario existe pero no tiene Cliente/Personal
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        usuario.setEmail("fantasma@correo.com");
        usuario.setPassword("hash");
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        doReturn(Optional.of(usuario)).when(usuarioRepository).findByEmail("fantasma@correo.com");
        doReturn(true).when(passwordEncoder).matches("pwd", "hash");
        doReturn("jwt").when(jwtService).generarToken(usuario);
        doReturn(86400L).when(jwtService).getExpirationSeconds();

        AuthResponse response = sut.login(loginRequest("fantasma@correo.com", "pwd"));

        AuthResponse.UsuarioResumen resumen = response.getUsuario();
        assertNull(resumen.getNombres());
        assertNull(resumen.getApellidos());
        assertNull(resumen.getNombreCompleto());
    }

    @Test
    void login_emailNoExiste_lanzaExcepcion() {
        doReturn(Optional.empty()).when(usuarioRepository).findByEmail("nadie@correo.com");

        assertThrows(IllegalArgumentException.class,
                () -> sut.login(loginRequest("nadie@correo.com", "cualquiera")));
    }

    @Test
    void login_passwordIncorrecta_lanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("ana@correo.com");
        usuario.setPassword("hash");
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setTipoUsuario(TipoUsuario.CLIENTE);

        doReturn(Optional.of(usuario)).when(usuarioRepository).findByEmail("ana@correo.com");
        doReturn(false).when(passwordEncoder).matches("mal", "hash");

        assertThrows(IllegalArgumentException.class,
                () -> sut.login(loginRequest("ana@correo.com", "mal")));
    }

    // ───────────── registrarCliente ─────────────

    @Test
    void registrarCliente_emailDuplicado_lanzaExcepcion() {
        RegistroClienteRequest req = new RegistroClienteRequest();
        req.setEmail("ana@correo.com");
        req.setDocumento("123");

        doReturn(true).when(usuarioRepository).existsByEmail("ana@correo.com");

        assertThrows(IllegalArgumentException.class, () -> sut.registrarCliente(req));
    }

    @Test
    void registrarCliente_documentoDuplicado_lanzaExcepcion() {
        RegistroClienteRequest req = new RegistroClienteRequest();
        req.setEmail("nuevo@correo.com");
        req.setDocumento("123");

        doReturn(false).when(usuarioRepository).existsByEmail("nuevo@correo.com");
        doReturn(true).when(clienteRepository).existsByDocumento("123");

        assertThrows(IllegalArgumentException.class, () -> sut.registrarCliente(req));
    }

    @Test
    void registrarCliente_ok_retornaAuthResponseConUsuario() {
        RegistroClienteRequest req = new RegistroClienteRequest();
        req.setEmail("nuevo@correo.com");
        req.setPassword("secret");
        req.setNombres("Ana");
        req.setApellidos("Lopez");
        req.setDocumento("99999999");
        req.setTelefono("999");

        doReturn(false).when(usuarioRepository).existsByEmail(any());
        doReturn(false).when(clienteRepository).existsByDocumento(any());
        doReturn("hash").when(passwordEncoder).encode("secret");
        doReturn("jwt.token").when(jwtService).generarToken(any());
        doReturn(86400L).when(jwtService).getExpirationSeconds();

        Usuario persistido = new Usuario();
        persistido.setId(1L);
        persistido.setEmail("nuevo@correo.com");
        persistido.setPassword("hash");
        persistido.setRol(RolUsuario.CLIENTE);
        persistido.setTipoUsuario(TipoUsuario.CLIENTE);
        Cliente cliente = new Cliente(null, "Ana", "Lopez", "99999999", "999", "nuevo@correo.com");
        cliente.setId(10L);
        persistido.setCliente(cliente);
        doReturn(persistido).when(usuarioRepository).save(any());

        AuthResponse response = sut.registrarCliente(req);

        assertEquals("jwt.token", response.getToken());
        assertEquals(86400L, response.getExpiresIn());
        assertEquals("Ana Lopez", response.getUsuario().getNombreCompleto());
        assertEquals(RolUsuario.CLIENTE, response.getUsuario().getRol());
        verify(usuarioRepository).save(any());
    }

    // ───────────── helpers ─────────────

    private LoginRequest loginRequest(String email, String password) {
        LoginRequest req = new LoginRequest();
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }
}