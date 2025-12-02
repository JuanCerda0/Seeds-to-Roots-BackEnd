package com.seedstoroots.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EstadisticasResponse {
    private Long totalProductos;
    private Long totalUsuarios;
    private Long productosActivos;
    private Long usuariosActivos;
    private Long productosBajoStock;
}