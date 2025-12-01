package com.seedstoroots.app.service;

import com.seedstoroots.app.dto.CarritoRequest;
import com.seedstoroots.app.dto.CarritoResponse;

public interface CarritoService {
    CarritoResponse obtenerCarrito(Long usuarioId);
    CarritoResponse agregarProducto(Long usuarioId, CarritoRequest request);
    CarritoResponse actualizarCantidad(Long usuarioId, CarritoRequest request);
    CarritoResponse eliminarProducto(Long usuarioId, Long productoId);
    void limpiarCarrito(Long usuarioId);
}