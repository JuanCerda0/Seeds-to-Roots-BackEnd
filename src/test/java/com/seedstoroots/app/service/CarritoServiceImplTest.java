package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.CarritoRequest;
import com.seedstoroots.app.dto.CarritoResponse;
import com.seedstoroots.app.entity.*;
import com.seedstoroots.app.repository.*;
import com.seedstoroots.app.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CarritoItemRepository carritoItemRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Usuario usuario;
    private Producto producto;
    private Carrito carrito;
    private CarritoRequest carritoRequest;

    @BeforeEach
    void setUp() {
        // Usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@ejemplo.com");

        // Producto
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Tomate");
        producto.setPrecio(BigDecimal.valueOf(2990));
        producto.setStock(50);

        // Carrito
        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setEstado(EstadoCarrito.ACTIVO);
        carrito.setItems(new ArrayList<>());

        // Request
        carritoRequest = new CarritoRequest();
        carritoRequest.setProductoId(1L);
        carritoRequest.setCantidad(2);
    }

    @Test
    void obtenerCarrito_CuandoExiste_DeberiaRetornarCarrito() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));

        // Act
        CarritoResponse response = carritoService.obtenerCarrito(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUsuarioId());
        verify(carritoRepository, times(1))
                .findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO);
    }

    @Test
    void obtenerCarrito_CuandoNoExiste_DeberiaCrearNuevo() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        // Act
        CarritoResponse response = carritoService.obtenerCarrito(1L);

        // Assert
        assertNotNull(response);
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void agregarProducto_ProductoNuevo_DeberiaAgregarAlCarrito() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        // Act
        CarritoResponse response = carritoService.agregarProducto(1L, carritoRequest);

        // Assert
        assertNotNull(response);
        verify(productoRepository, times(1)).findById(1L);
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void agregarProducto_ProductoExistente_DeberiaIncrementarCantidad() {
        // Arrange
        CarritoItem itemExistente = new CarritoItem();
        itemExistente.setProducto(producto);
        itemExistente.setCantidad(3);
        itemExistente.setPrecioUnitario(BigDecimal.valueOf(2990));
        carrito.getItems().add(itemExistente);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        // Act
        CarritoResponse response = carritoService.agregarProducto(1L, carritoRequest);

        // Assert
        assertNotNull(response);
        assertEquals(5, itemExistente.getCantidad()); // 3 + 2
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void agregarProducto_ProductoNoExiste_DeberiaLanzarExcepcion() {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());

        carritoRequest.setProductoId(999L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            carritoService.agregarProducto(1L, carritoRequest);
        });
    }

    @Test
    void eliminarProducto_DeberiaEliminarDelCarrito() {
        // Arrange
        CarritoItem item = new CarritoItem();
        item.setProducto(producto);
        carrito.getItems().add(item);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        // Act
        CarritoResponse response = carritoService.eliminarProducto(1L, 1L);

        // Assert
        assertNotNull(response);
        assertTrue(carrito.getItems().isEmpty());
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void limpiarCarrito_DeberiaVaciarTodosLosItems() {
        // Arrange
        CarritoItem item1 = new CarritoItem();
        CarritoItem item2 = new CarritoItem();
        carrito.getItems().add(item1);
        carrito.getItems().add(item2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndEstado(1L, EstadoCarrito.ACTIVO))
                .thenReturn(Optional.of(carrito));
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carrito);

        // Act
        carritoService.limpiarCarrito(1L);

        // Assert
        assertTrue(carrito.getItems().isEmpty());
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }
}