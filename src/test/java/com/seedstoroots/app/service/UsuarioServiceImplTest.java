package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.UsuarioResponse;
import com.seedstoroots.app.dto.UsuarioUpdateRequest;
import com.seedstoroots.app.entity.Rol;
import com.seedstoroots.app.entity.Usuario;
import com.seedstoroots.app.repository.UsuarioRepository;
import com.seedstoroots.app.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private Usuario usuario;
    private UsuarioUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRun("12345678-9");
        usuario.setNombre("Juan");
        usuario.setApellidos("Pérez");
        usuario.setEmail("juan@ejemplo.com");
        usuario.setTelefono("912345678");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);

        updateRequest = new UsuarioUpdateRequest();
        updateRequest.setNombre("Juan Actualizado");
        updateRequest.setTelefono("987654321");
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaDeUsuarios() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuario));

        // Act
        List<UsuarioResponse> resultado = usuarioService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornarUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponse resultado = usuarioService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("juan@ejemplo.com", resultado.getEmail());
    }

    @Test
    void obtenerPorEmail_CuandoExiste_DeberiaRetornarUsuario() {
        // Arrange
        when(usuarioRepository.findByEmail("juan@ejemplo.com"))
                .thenReturn(Optional.of(usuario));

        // Act
        UsuarioResponse resultado = usuarioService.obtenerPorEmail("juan@ejemplo.com");

        // Assert
        assertNotNull(resultado);
        assertEquals("juan@ejemplo.com", resultado.getEmail());
    }

    @Test
    void actualizar_DeberiaActualizarCampos() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        UsuarioResponse resultado = usuarioService.actualizar(1L, updateRequest);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void eliminar_DeberiaDesactivarUsuario() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        usuarioService.eliminar(1L);

        // Assert
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}