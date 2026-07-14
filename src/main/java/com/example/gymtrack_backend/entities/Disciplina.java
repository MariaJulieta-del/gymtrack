package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "disciplinas")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private String instructor;

    /** Ej: "Lunes y Miércoles 18:00 - 19:00" */
    private String horario;

    private Integer cupoMaximo;

    @Column(nullable = false)
    private Boolean activa = true;
}
