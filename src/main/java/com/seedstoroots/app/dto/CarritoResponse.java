package com.seedstoroots.app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CarritoResponse {
    private Long id;
    private Long usuarioId;
    private List<CarritoItemResponse> items;
    private BigDecimal total;
}