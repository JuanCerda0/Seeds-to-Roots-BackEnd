package com.seedstoroots.app.service.impl;

import com.seedstoroots.app.dto.CarritoItemResponse;
import com.seedstoroots.app.dto.CarritoRequest;
import com.seedstoroots.app.dto.CarritoResponse;
import com.seedstoroots.app.entity.*;
import com.seedstoroots.app.repository.*;
import com.seedstoroots.app.service.CarritoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository,
                              CarritoItemRepository carritoItemRepository,
                              UsuarioRepository usuarioRepository,
                              ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public CarritoResponse obtenerCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return convertirAResponse(carrito);
    }

    @Override
    public CarritoResponse agregarProducto(Long usuarioId, CarritoRequest request) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        int cantidadSolicitada = validarCantidad(request.getCantidad());

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        asegurarProductoDisponible(producto);

        // Verificar si el producto ya esta en el carrito
        CarritoItem itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(producto.getId()))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            // Si existe, aumentar cantidad respetando el stock
            int nuevaCantidad = itemExistente.getCantidad() + cantidadSolicitada;
            validarStockDisponible(producto, nuevaCantidad);
            itemExistente.setCantidad(nuevaCantidad);
            itemExistente.setSubtotal(itemExistente.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(itemExistente.getCantidad())));
        } else {
            // Si no existe, crear nuevo item
            validarStockDisponible(producto, cantidadSolicitada);
            CarritoItem nuevoItem = new CarritoItem();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setPrecioUnitario(producto.getPrecio());
            nuevoItem.setCantidad(cantidadSolicitada);
            nuevoItem.setSubtotal(producto.getPrecio()
                    .multiply(BigDecimal.valueOf(cantidadSolicitada)));

            carrito.getItems().add(nuevoItem);
        }

        carritoRepository.save(carrito);
        return convertirAResponse(carrito);
    }

    @Override
    public CarritoResponse actualizarCantidad(Long usuarioId, CarritoRequest request) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        int cantidadSolicitada = validarCantidad(request.getCantidad());

        CarritoItem item = carrito.getItems().stream()
                .filter(i -> i.getProducto().getId().equals(request.getProductoId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        Producto producto = item.getProducto();
        asegurarProductoDisponible(producto);
        validarStockDisponible(producto, cantidadSolicitada);

        item.setCantidad(cantidadSolicitada);
        item.setSubtotal(item.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(cantidadSolicitada)));

        carritoRepository.save(carrito);
        return convertirAResponse(carrito);
    }

    @Override
    public CarritoResponse eliminarProducto(Long usuarioId, Long productoId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);

        carrito.getItems().removeIf(item -> item.getProducto().getId().equals(productoId));

        carritoRepository.save(carrito);
        return convertirAResponse(carrito);
    }

    @Override
    public void limpiarCarrito(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return carritoRepository.findByUsuarioIdAndEstado(usuarioId, EstadoCarrito.ACTIVO)
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    nuevoCarrito.setEstado(EstadoCarrito.ACTIVO);
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    private CarritoResponse convertirAResponse(Carrito carrito) {
        CarritoResponse response = new CarritoResponse();
        response.setId(carrito.getId());
        response.setUsuarioId(carrito.getUsuario().getId());

        List<CarritoItemResponse> items = carrito.getItems().stream()
                .map(this::convertirItemAResponse)
                .collect(Collectors.toList());
        response.setItems(items);

        BigDecimal total = items.stream()
                .map(CarritoItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotal(total);

        return response;
    }

    private CarritoItemResponse convertirItemAResponse(CarritoItem item) {
        CarritoItemResponse response = new CarritoItemResponse();
        response.setId(item.getId());
        response.setProductoId(item.getProducto().getId());
        response.setProductoNombre(item.getProducto().getNombre());
        response.setProductoImagen(item.getProducto().getImagen());
        response.setPrecioUnitario(item.getPrecioUnitario());
        response.setCantidad(item.getCantidad());
        response.setSubtotal(item.getSubtotal());
        return response;
    }

    private int validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a cero");
        }
        return cantidad;
    }

    private void asegurarProductoDisponible(Producto producto) {
        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new RuntimeException("El producto no esta disponible");
        }
    }

    private void validarStockDisponible(Producto producto, int cantidadRequerida) {
        Integer stock = producto.getStock();
        if (stock == null || cantidadRequerida > stock) {
            throw new RuntimeException("Stock insuficiente para el producto");
        }
    }
}
