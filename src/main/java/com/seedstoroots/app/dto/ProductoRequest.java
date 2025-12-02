package com.seedstoroots.app.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoRequest  {
    private String nombre;
    private String descripcion;
    private String categoria;
    private BigDecimal precio;
    private Integer stock;
    private String sku;
    private String imagen;
    private Boolean activo;
}
