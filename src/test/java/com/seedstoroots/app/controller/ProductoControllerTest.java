package com.seedstoroots.app.controller;

import com.seedstoroots.app.dto.ProductoRequest;
import com.seedstoroots.app.dto.ProductoResponse;
import com.seedstoroots.app.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ProductoResponse productoResponse;
    private ProductoRequest productoRequest;

    @BeforeEach
    void setUp() {
        productoResponse = new ProductoResponse();
        productoResponse.setId(1L);
        productoResponse.setNombre("Semillas de Tomate");
        productoResponse.setPrecio(BigDecimal.valueOf(2990));
        productoResponse.setStock(50);

        productoRequest = new ProductoRequest();
        productoRequest.setNombre("Semillas de Tomate");
        productoRequest.setPrecio(BigDecimal.valueOf(2990));
        productoRequest.setStock(50);
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaDeProductos() {
        // Arrange
        List<ProductoResponse> productos = Arrays.asList(productoResponse);
        when(productoService.obtenerTodos()).thenReturn(productos);

        // Act
        ResponseEntity<List<ProductoResponse>> response = productoController.obtenerTodos();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Semillas de Tomate", response.getBody().get(0).getNombre());
        verify(productoService, times(1)).obtenerTodos();
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornarProducto() {
        // Arrange
        when(productoService.obtenerPorId(1L)).thenReturn(productoResponse);

        // Act
        ResponseEntity<ProductoResponse> response = productoController.obtenerPorId(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Semillas de Tomate", response.getBody().getNombre());
        verify(productoService, times(1)).obtenerPorId(1L);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaRetornar404() {
        // Arrange
        when(productoService.obtenerPorId(999L))
                .thenThrow(new RuntimeException("Producto no encontrado"));

        // Act
        ResponseEntity<ProductoResponse> response = productoController.obtenerPorId(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(productoService, times(1)).obtenerPorId(999L);
    }

    @Test
    void crear_DeberiaRetornar201YProductoCreado() {
        // Arrange
        when(productoService.crear(any(ProductoRequest.class))).thenReturn(productoResponse);

        // Act
        ResponseEntity<ProductoResponse> response = productoController.crear(productoRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Semillas de Tomate", response.getBody().getNombre());
        verify(productoService, times(1)).crear(any(ProductoRequest.class));
    }

    @Test
    void actualizar_CuandoExiste_DeberiaRetornarProductoActualizado() {
        // Arrange
        when(productoService.actualizar(eq(1L), any(ProductoRequest.class)))
                .thenReturn(productoResponse);

        // Act
        ResponseEntity<ProductoResponse> response = productoController.actualizar(1L, productoRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productoService, times(1)).actualizar(eq(1L), any(ProductoRequest.class));
    }

    @Test
    void eliminar_CuandoExiste_DeberiaRetornar204() {
        // Arrange
        doNothing().when(productoService).eliminar(1L);

        // Act
        ResponseEntity<Void> response = productoController.eliminar(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productoService, times(1)).eliminar(1L);
    }
}