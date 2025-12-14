package com.seedstoroots.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "Test", description = "Endpoints de prueba")
public class TestController {

    @Operation(
            summary = "Verificar estado del servidor",
            description = "Endpoint simple para verificar que el backend está funcionando correctamente"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Servidor funcionando correctamente"
    )
    @GetMapping
    public String ping() {
        return "Backend funcionando correctamente";
    }
}
