package com.seedstoroots.app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UsuarioCreateRequest {
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
    private String password;
    private String rol;
    private Boolean activo;
}
