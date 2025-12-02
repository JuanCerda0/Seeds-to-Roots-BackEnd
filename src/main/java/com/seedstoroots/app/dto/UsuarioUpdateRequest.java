package com.seedstoroots.app.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioUpdateRequest {
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String region;
    private String comuna;
    private String ciudad;
    private LocalDate fechaNacimiento;
    private Boolean activo;
}