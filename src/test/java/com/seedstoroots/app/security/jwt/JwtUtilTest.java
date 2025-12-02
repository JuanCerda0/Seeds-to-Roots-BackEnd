package com.seedstoroots.app.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inyectar valores de configuración manualmente
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "TuClaveSecretaSuperSeguraParaJWT2024SeedsToRootsQueDebeSerMuyLargaParaQueSeaSegura");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void generateToken_DeberiaGenerarTokenValido() {
        // Act
        String token = jwtUtil.generateToken("test@ejemplo.com", "CLIENTE", 1L);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tiene 3 partes
    }

    @Test
    void validateToken_ConTokenValido_DeberiaRetornarTrue() {
        // Arrange
        String token = jwtUtil.generateToken("test@ejemplo.com", "CLIENTE", 1L);

        // Act
        boolean isValid = jwtUtil.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_ConTokenInvalido_DeberiaRetornarFalse() {
        // Arrange
        String tokenInvalido = "token.invalido.fake";

        // Act
        boolean isValid = jwtUtil.validateToken(tokenInvalido);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getEmailFromToken_DeberiaExtraerEmail() {
        // Arrange
        String token = jwtUtil.generateToken("test@ejemplo.com", "CLIENTE", 1L);

        // Act
        String email = jwtUtil.getEmailFromToken(token);

        // Assert
        assertEquals("test@ejemplo.com", email);
    }

    @Test
    void getRolFromToken_DeberiaExtraerRol() {
        // Arrange
        String token = jwtUtil.generateToken("test@ejemplo.com", "ADMIN", 1L);

        // Act
        String rol = jwtUtil.getRolFromToken(token);

        // Assert
        assertEquals("ADMIN", rol);
    }

    @Test
    void getUserIdFromToken_DeberiaExtraerUserId() {
        // Arrange
        String token = jwtUtil.generateToken("test@ejemplo.com", "CLIENTE", 5L);

        // Act
        Long userId = jwtUtil.getUserIdFromToken(token);

        // Assert
        assertEquals(5L, userId);
    }
}