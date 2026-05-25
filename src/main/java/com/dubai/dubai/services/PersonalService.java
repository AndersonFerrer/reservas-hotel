package com.dubai.dubai.services;

import com.dubai.dubai.models.Personal;
import com.dubai.dubai.repositories.PersonalRepository;
import com.dubai.dubai.repositories.ReservaRepository;
import com.dubai.dubai.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonalService {

    private final PersonalRepository personalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;

    public PersonalService(PersonalRepository personalRepository,
                           UsuarioRepository usuarioRepository,
                           ReservaRepository reservaRepository) {
        this.personalRepository = personalRepository;
        this.usuarioRepository = usuarioRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Personal> listar() {
        return personalRepository.findAll();
    }

    public Personal buscarPorId(Long id) {
        return personalRepository.findById(id).orElse(null);
    }

    public Personal crear(Personal personal) {
        validarPersonal(personal);
        validarEmailDisponible(personal.getEmail(), null);
        personal.setId(null);
        return personalRepository.save(personal);
    }

    @Transactional
    public Personal actualizar(Long id, Personal datos) {
        Personal existente = personalRepository.findById(id).orElse(null);
        if (existente == null) {
            return null;
        }

        validarPersonal(datos);
        validarEmailDisponible(datos.getEmail(), id);

        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setRol(datos.getRol());
        existente.setTelefono(datos.getTelefono());
        existente.setEmail(datos.getEmail());

        usuarioRepository.findByPersonal_Id(id).ifPresent(usuario -> {
            usuario.setEmail(datos.getEmail());
            usuarioRepository.save(usuario);
        });

        return personalRepository.save(existente);
    }

    public boolean eliminar(Long id) {
        if (!personalRepository.existsById(id)) {
            return false;
        }
        if (usuarioRepository.existsByPersonal_Id(id) || reservaRepository.existsByPersonal_Id(id)) {
            throw new IllegalStateException("No se puede eliminar el personal porque tiene usuario o reservas asociadas");
        }
        personalRepository.deleteById(id);
        return true;
    }

    private void validarPersonal(Personal personal) {
        if (personal == null) {
            throw new IllegalArgumentException("El personal es obligatorio");
        }
        if (esBlanco(personal.getNombres())) {
            throw new IllegalArgumentException("Los nombres del personal son obligatorios");
        }
        if (esBlanco(personal.getApellidos())) {
            throw new IllegalArgumentException("Los apellidos del personal son obligatorios");
        }
        if (personal.getRol() == null) {
            throw new IllegalArgumentException("El rol del personal es obligatorio");
        }
        if (esBlanco(personal.getEmail())) {
            throw new IllegalArgumentException("El email del personal es obligatorio");
        }
    }

    private void validarEmailDisponible(String email, Long idActual) {
        personalRepository.findByEmail(email)
                .filter(personal -> !personal.getId().equals(idActual))
                .ifPresent(personal -> {
                    throw new IllegalArgumentException("El email ya se encuentra registrado como personal");
                });
        usuarioRepository.findByEmail(email)
                .filter(usuario -> usuario.getPersonal() == null || !usuario.getPersonal().getId().equals(idActual))
                .ifPresent(usuario -> {
                    throw new IllegalArgumentException("El email ya se encuentra registrado como usuario");
                });
    }

    private boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
