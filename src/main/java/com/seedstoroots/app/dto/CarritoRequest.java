package com.seedstoroots.app.dto;

import lombok.Data;

@Data
public class CarritoRequest {
    private Long productoId;
    private Integer cantidad;
}