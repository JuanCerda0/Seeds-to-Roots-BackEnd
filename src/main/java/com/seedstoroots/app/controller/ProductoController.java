package com.seedstoroots.app.controller;

import com.seedstoroots.app.dto.ProductoRequest;
import com.seedstoroots.app.dto.ProductoResponse;
import com.seedstoroots.app.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> obtenerTodos() {
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productoService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<ProductoResponse>> obtenerRecientes(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(productoService.obtenerRecientes(limit));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request) {
        try {
            ProductoResponse response = productoService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Long id,
            @RequestBody ProductoRequest request) {
        try {
            return ResponseEntity.ok(productoService.actualizar(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            productoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}