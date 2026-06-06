package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "socios")
public class Socio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String dni;

    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private String estado; // ACTIVO, INACTIVO

    @Column(nullable = false)
    private Boolean activo = true;
}