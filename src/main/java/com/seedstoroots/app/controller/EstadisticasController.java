package com.seedstoroots.app.controller;

import com.seedstoroots.app.dto.EstadisticasResponse;
import com.seedstoroots.app.service.EstadisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
@Tag(name = "Estadísticas", description = "Dashboard de estadísticas del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @Operation(
            summary = "Obtener estadísticas generales",
            description = "Obtiene un resumen de estadísticas del sistema incluyendo totales de productos, usuarios, y alertas de stock. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EstadisticasResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "No autorizado - Requiere rol ADMIN"
            )
    })
    @GetMapping
    public ResponseEntity<EstadisticasResponse> obtenerEstadisticas() {
        return ResponseEntity.ok(estadisticasService.obtenerEstadisticas());
    }
}