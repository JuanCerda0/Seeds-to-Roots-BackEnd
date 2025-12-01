package com.seedstoroots.app.service.impl;

import com.seedstoroots.app.dto.ProductoRequest;
import com.seedstoroots.app.dto.ProductoResponse;
import com.seedstoroots.app.entity.Producto;
import com.seedstoroots.app.repository.ProductoRepository;
import com.seedstoroots.app.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<ProductoResponse> obtenerTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return convertirAResponse(producto);
    }

    @Override
    public List<ProductoResponse> obtenerRecientes(int limit) {
        List<Producto> productos = productoRepository.findProductosRecientes();
        return productos.stream()
                .limit(limit)
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setSku(request.getSku());
        producto.setImagen(request.getImagen());
        producto.setActivo(request.getActivo() != null ? request.getActivo() : true);

        Producto guardado = productoRepository.save(producto);
        return convertirAResponse(guardado);
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setSku(request.getSku());
        producto.setImagen(request.getImagen());
        if (request.getActivo() != null) {
            producto.setActivo(request.getActivo());
        }

        Producto actualizado = productoRepository.save(producto);
        return convertirAResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Eliminación lógica (recomendado)
        producto.setActivo(false);
        productoRepository.save(producto);

        // O eliminación física (descomentar si prefieres):
        // productoRepository.delete(producto);
    }

    private ProductoResponse convertirAResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setNombre(producto.getNombre());
        response.setDescripcion(producto.getDescripcion());
        response.setCategoria(producto.getCategoria());
        response.setPrecio(producto.getPrecio());
        response.setStock(producto.getStock());
        response.setSku(producto.getSku());
        response.setImagen(producto.getImagen());
        response.setActivo(producto.getActivo());
        response.setFechaCreacion(producto.getFechaCreacion());
        response.setFechaActualizacion(producto.getFechaActualizacion());
        return response;
    }

}
