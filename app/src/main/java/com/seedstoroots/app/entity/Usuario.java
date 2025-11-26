package com.seedstoroots.app.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data   // Genera getters, setters, equals, hashCode y toString
@NoArgsConstructor  // Genera constructores
@AllArgsConstructor // Genera constructores
public class Usuario {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String run;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;
    private String direccion;
    private String region;
    private String comuna;
    private String ciudad;

    private LocalDate fechaNacimiento;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    private Boolean activo = true;

    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime ultimoLogin;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
