package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.AuthResponse;
import com.seedstoroots.app.dto.LoginRequest;
import com.seedstoroots.app.dto.RegisterRequest;
import com.seedstoroots.app.entity.Rol;
import com.seedstoroots.app.entity.Usuario;
import com.seedstoroots.app.repository.UsuarioRepository;
import com.seedstoroots.app.security.jwt.JwtUtil;
import com.seedstoroots.app.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioMock;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        // Usuario mock
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("test@ejemplo.com");
        usuarioMock.setPasswordHash(passwordEncoder.encode("password123"));
        usuarioMock.setRol(Rol.CLIENTE);
        usuarioMock.setActivo(true);
        usuarioMock.setNombre("Test");
        usuarioMock.setApellidos("Usuario");
        usuarioMock.setRun("12345678-9");

        // Login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@ejemplo.com");
        loginRequest.setPassword("password123");

        // Register request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("nuevo@ejemplo.com");
        registerRequest.setPassword("password123");
        registerRequest.setRun("98765432-1");
        registerRequest.setNombre("Nuevo");
        registerRequest.setApellidos("Usuario");
        registerRequest.setTelefono("912345678");
        registerRequest.setDireccion("Calle Test 123");
        registerRequest.setRegion("Metropolitana");
        registerRequest.setComuna("Santiago");
        registerRequest.setCiudad("Santiago");
        registerRequest.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    }

    @Test
    void login_ConCredencialesValidas_DeberiaRetornarToken() {
        // Arrange
        when(usuarioRepository.findByEmail("test@ejemplo.com"))
                .thenReturn(Optional.of(usuarioMock));
        when(jwtUtil.generateToken(anyString(), anyString(), any()))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@ejemplo.com", response.getEmail());
        assertEquals("CLIENTE", response.getRol());
        verify(usuarioRepository, times(1)).findByEmail("test@ejemplo.com");
        verify(usuarioRepository, times(1)).save(any(Usuario.class)); // Actualiza ultimo login
    }

    @Test
    void login_ConUsuarioNoExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@ejemplo.com"))
                .thenReturn(Optional.empty());

        loginRequest.setEmail("noexiste@ejemplo.com");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });
        verify(usuarioRepository, times(1)).findByEmail("noexiste@ejemplo.com");
    }

    @Test
    void login_ConPasswordIncorrecta_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findByEmail("test@ejemplo.com"))
                .thenReturn(Optional.of(usuarioMock));

        loginRequest.setPassword("passwordIncorrecta");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void login_ConUsuarioInactivo_DeberiaLanzarExcepcion() {
        // Arrange
        usuarioMock.setActivo(false);
        when(usuarioRepository.findByEmail("test@ejemplo.com"))
                .thenReturn(Optional.of(usuarioMock));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void register_ConDatosValidos_DeberiaCrearUsuario() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRun(anyString())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioMock);
        when(jwtUtil.generateToken(anyString(), anyString(), any()))
                .thenReturn("fake-jwt-token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        verify(usuarioRepository, times(1)).existsByEmail("nuevo@ejemplo.com");
        verify(usuarioRepository, times(1)).existsByRun("98765432-1");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void register_ConEmailExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.existsByEmail("nuevo@ejemplo.com")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
        verify(usuarioRepository, times(1)).existsByEmail("nuevo@ejemplo.com");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_ConRunExistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRun("98765432-1")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
        verify(usuarioRepository, times(1)).existsByRun("98765432-1");
        verify(usuarioRepository, never()).save(any());
    }
}