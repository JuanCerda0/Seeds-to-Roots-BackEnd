package com.seedstoroots.app.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CarritoItemResponse {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String productoImagen;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;
}