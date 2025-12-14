package com.seedstoroots.app.controller;

import com.seedstoroots.app.dto.CarritoRequest;
import com.seedstoroots.app.dto.CarritoResponse;
import com.seedstoroots.app.dto.UsuarioResponse;
import com.seedstoroots.app.service.CarritoService;
import com.seedstoroots.app.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras")
@SecurityRequirement(name = "bearer-jwt")
public class CarritoController {

    private final CarritoService carritoService;
    private final UsuarioService usuarioService;

    public CarritoController(CarritoService carritoService, UsuarioService usuarioService) {
        this.carritoService = carritoService;
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Obtener carrito del usuario",
            description = "Obtiene el carrito activo del usuario con todos sus items. Si no existe, se crea uno nuevo."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Carrito obtenido exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponse> obtenerCarrito(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        try {
            if (!usuarioId.equals(obtenerUsuarioAutenticadoId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Agregar producto al carrito",
            description = "Agrega un producto al carrito del usuario. Si el producto ya existe, incrementa la cantidad."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto agregado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Producto no encontrado o datos inválidos"
            )
    })
    @PostMapping("/{usuarioId}/add")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId,
            @RequestBody CarritoRequest request) {
        try {
            if (!usuarioId.equals(obtenerUsuarioAutenticadoId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(carritoService.agregarProducto(usuarioId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Actualizar cantidad de producto",
            description = "Actualiza la cantidad de un producto específico en el carrito (reemplaza la cantidad, no suma)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Cantidad actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Producto no encontrado en el carrito"
            )
    })
    @PutMapping("/{usuarioId}/update")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId,
            @RequestBody CarritoRequest request) {
        try {
            if (!usuarioId.equals(obtenerUsuarioAutenticadoId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Eliminar producto del carrito",
            description = "Elimina un producto específico del carrito del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Producto eliminado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Producto no encontrado en el carrito"
            )
    })
    @DeleteMapping("/{usuarioId}/remove/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId,
            @Parameter(description = "ID del producto a eliminar", required = true)
            @PathVariable Long productoId) {
        try {
            if (!usuarioId.equals(obtenerUsuarioAutenticadoId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(carritoService.eliminarProducto(usuarioId, productoId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Vaciar carrito",
            description = "Elimina todos los productos del carrito del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Carrito vaciado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            )
    })
    @DeleteMapping("/{usuarioId}/clear")
    public ResponseEntity<Void> limpiarCarrito(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long usuarioId) {
        try {
            if (!usuarioId.equals(obtenerUsuarioAutenticadoId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            carritoService.limpiarCarrito(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long obtenerUsuarioAutenticadoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Usuario no autenticado");
        }
        UsuarioResponse usuario = usuarioService.obtenerPorEmail(authentication.getName());
        return usuario.getId();
    }
}
