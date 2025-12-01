package com.seedstoroots.app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precio;
    private Integer stock;
    private String sku;
    private String imagen;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
