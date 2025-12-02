package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.ProductoRequest;
import com.seedstoroots.app.dto.ProductoResponse;
import com.seedstoroots.app.entity.Producto;

import java.util.List;

public interface ProductoService {

    List<ProductoResponse> obtenerTodos();
    ProductoResponse obtenerPorId(Long id);

    List<ProductoResponse> obtenerRecientes(int limit);

    ProductoResponse crear(ProductoRequest request);
    ProductoResponse actualizar(Long id, ProductoRequest request);

    void eliminar(Long id);
}
