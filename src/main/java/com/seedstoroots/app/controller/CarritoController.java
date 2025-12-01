package com.seedstoroots.app.controller;

import com.seedstoroots.app.dto.CarritoRequest;
import com.seedstoroots.app.dto.CarritoResponse;
import com.seedstoroots.app.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponse> obtenerCarrito(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{usuarioId}/add")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @PathVariable Long usuarioId,
            @RequestBody CarritoRequest request) {
        try {
            return ResponseEntity.ok(carritoService.agregarProducto(usuarioId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{usuarioId}/update")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @PathVariable Long usuarioId,
            @RequestBody CarritoRequest request) {
        try {
            return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{usuarioId}/remove/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @PathVariable Long usuarioId,
            @PathVariable Long productoId) {
        try {
            return ResponseEntity.ok(carritoService.eliminarProducto(usuarioId, productoId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{usuarioId}/clear")
    public ResponseEntity<Void> limpiarCarrito(@PathVariable Long usuarioId) {
        try {
            carritoService.limpiarCarrito(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}