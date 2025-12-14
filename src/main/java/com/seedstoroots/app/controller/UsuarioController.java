package com.seedstoroots.app.controller;

import com.seedstoroots.app.dto.UsuarioCreateRequest;
import com.seedstoroots.app.dto.UsuarioResponse;
import com.seedstoroots.app.dto.UsuarioUpdateRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestion de usuarios del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario desde el panel de administracion. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos invalidos o duplicados"
            )
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioCreateRequest request) {
        try {
            UsuarioResponse response = usuarioService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Listar todos los usuarios",
            description = "Obtiene la lista completa de usuarios registrados. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol ADMIN"
            )
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @Operation(
            summary = "Obtener usuario por ID",
            description = "Obtiene los detalles de un usuario especifico. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol ADMIN"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Obtener usuario por email",
            description = "Busca un usuario por su direccion de email. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol ADMIN"
            )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioResponse> obtenerPorEmail(
            @Parameter(description = "Email del usuario", required = true, example = "usuario@ejemplo.com")
            @PathVariable String email) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente. Requiere rol ADMIN o ser el propietario."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @Parameter(description = "ID del usuario a actualizar", required = true)
            @PathVariable Long id,
            @RequestBody UsuarioUpdateRequest request) {
        try {
            return ResponseEntity.ok(usuarioService.actualizar(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina (desactiva) un usuario del sistema. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol ADMIN"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable Long id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
