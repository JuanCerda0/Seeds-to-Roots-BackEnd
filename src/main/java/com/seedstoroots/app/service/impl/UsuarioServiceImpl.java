package com.seedstoroots.app.service.impl;

import com.seedstoroots.app.dto.UsuarioCreateRequest;
import com.seedstoroots.app.dto.UsuarioResponse;
import com.seedstoroots.app.dto.UsuarioUpdateRequest;
import com.seedstoroots.app.entity.Rol;
import com.seedstoroots.app.entity.Usuario;
import com.seedstoroots.app.repository.UsuarioRepository;
import com.seedstoroots.app.service.UsuarioService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public UsuarioResponse crear(UsuarioCreateRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        if (usuarioRepository.existsByRun(request.getRun())) {
            throw new RuntimeException("El RUN ya está registrado");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }

        Usuario usuario = new Usuario();
        usuario.setRun(request.getRun());
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());
        usuario.setRegion(request.getRegion());
        usuario.setComuna(request.getComuna());
        usuario.setCiudad(request.getCiudad());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(obtenerRol(request.getRol()));
        usuario.setActivo(request.getActivo() != null ? request.getActivo() : true);

        Usuario guardado = usuarioRepository.save(usuario);
        return convertirAResponse(guardado);
    }

    @Override
    public List<UsuarioResponse> obtenerTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirAResponse(usuario);
    }

    @Override
    public UsuarioResponse obtenerPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirAResponse(usuario);
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNombre() != null) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getApellidos() != null) {
            usuario.setApellidos(request.getApellidos());
        }
        if (request.getTelefono() != null) {
            usuario.setTelefono(request.getTelefono());
        }
        if (request.getDireccion() != null) {
            usuario.setDireccion(request.getDireccion());
        }
        if (request.getRegion() != null) {
            usuario.setRegion(request.getRegion());
        }
        if (request.getComuna() != null) {
            usuario.setComuna(request.getComuna());
        }
        if (request.getCiudad() != null) {
            usuario.setCiudad(request.getCiudad());
        }
        if (request.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(request.getFechaNacimiento());
        }
        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }

        Usuario actualizado = usuarioRepository.save(usuario);
        return convertirAResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Eliminación lógica
        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        // O eliminación física (descomentar si prefieres):
        // usuarioRepository.delete(usuario);
    }

    private Rol obtenerRol(String rol) {
        if (rol == null || rol.isBlank()) {
            return Rol.CLIENTE;
        }
        try {
            return Rol.valueOf(rol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido");
        }
    }

    private UsuarioResponse convertirAResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setRun(usuario.getRun());
        response.setNombre(usuario.getNombre());
        response.setApellidos(usuario.getApellidos());
        response.setEmail(usuario.getEmail());
        response.setTelefono(usuario.getTelefono());
        response.setDireccion(usuario.getDireccion());
        response.setRegion(usuario.getRegion());
        response.setComuna(usuario.getComuna());
        response.setCiudad(usuario.getCiudad());
        response.setFechaNacimiento(usuario.getFechaNacimiento());
        response.setRol(usuario.getRol().name());
        response.setActivo(usuario.getActivo());
        response.setFechaRegistro(usuario.getFechaRegistro());
        response.setUltimoLogin(usuario.getUltimoLogin());
        return response;
    }
}
