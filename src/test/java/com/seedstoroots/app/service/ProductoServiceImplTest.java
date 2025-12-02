package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.ProductoRequest;
import com.seedstoroots.app.dto.ProductoResponse;
import com.seedstoroots.app.entity.Producto;
import com.seedstoroots.app.repository.ProductoRepository;
import com.seedstoroots.app.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceImplTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Producto productoMock;
    private ProductoRequest productoRequest;

    @BeforeEach
    void setUp() {
        // Crear producto mock
        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Semillas de Tomate");
        productoMock.setDescripcion("Semillas orgánicas");
        productoMock.setCategoria("Semillas");
        productoMock.setPrecio(BigDecimal.valueOf(2990));
        productoMock.setStock(50);
        productoMock.setSku("SEM-TOM-001");
        productoMock.setImagen("https://ejemplo.com/tomate.jpg");
        productoMock.setActivo(true);

        // Crear request mock
        productoRequest = new ProductoRequest();
        productoRequest.setNombre("Semillas de Tomate");
        productoRequest.setDescripcion("Semillas orgánicas");
        productoRequest.setCategoria("Semillas");
        productoRequest.setPrecio(BigDecimal.valueOf(2990));
        productoRequest.setStock(50);
        productoRequest.setSku("SEM-TOM-001");
        productoRequest.setImagen("https://ejemplo.com/tomate.jpg");
        productoRequest.setActivo(true);
    }

    @Test
    void obtenerTodos_DeberiaRetornarListaDeProductos() {
        // Arrange
        List<Producto> productos = Arrays.asList(productoMock);
        when(productoRepository.findAll()).thenReturn(productos);

        // Act
        List<ProductoResponse> resultado = productoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Semillas de Tomate", resultado.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        // Act
        ProductoResponse resultado = productoService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Semillas de Tomate", resultado.getNombre());
        verify(productoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productoService.obtenerPorId(999L);
        });
        verify(productoRepository, times(1)).findById(999L);
    }

    @Test
    void crear_DeberiaGuardarYRetornarProducto() {
        // Arrange
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        // Act
        ProductoResponse resultado = productoService.crear(productoRequest);

        // Assert
        assertNotNull(resultado);
        assertEquals("Semillas de Tomate", resultado.getNombre());
        assertEquals(BigDecimal.valueOf(2990), resultado.getPrecio());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizar_CuandoExiste_DeberiaActualizarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        productoRequest.setNombre("Semillas de Tomate Actualizado");
        productoRequest.setPrecio(BigDecimal.valueOf(3990));

        // Act
        ProductoResponse resultado = productoService.actualizar(1L, productoRequest);

        // Assert
        assertNotNull(resultado);
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void eliminar_CuandoExiste_DeberiaDesactivarProducto() {
        // Arrange
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);

        // Act
        productoService.eliminar(1L);

        // Assert
        verify(productoRepository, times(1)).findById(1L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }
}