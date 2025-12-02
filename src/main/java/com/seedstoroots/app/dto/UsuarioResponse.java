package com.seedstoroots.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UsuarioResponse {
    private Long id;
    private String run;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String direccion;
    private String region;
    private String comuna;
    private String ciudad;
    private LocalDate fechaNacimiento;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoLogin;
}