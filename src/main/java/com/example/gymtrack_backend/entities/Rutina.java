package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad que representa una rutina de entrenamiento.
 * Cada rutina pertenece a un Socio y puede contener múltiples ejercicios.
 */
@Data
@Entity
@Table(name = "rutinas")
public class Rutina {

    // Clave primaria con auto-incremento
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la rutina (obligatorio, máx 50 chars)
    @Column(nullable = false, length = 50)
    private String nombre;

    // Descripción opcional de la rutina
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // Relación ManyToOne: muchas rutinas pertenecen a un Socio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    // Fecha de creación: se setea automáticamente al persistir
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Se ejecuta antes de insertar en BD.
     * Asigna automáticamente la fecha y hora actual.
     */
    @PrePersist
    protected void onCrear() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Relación OneToMany: una rutina puede tener muchos ejercicios
    // (lista de ejercicios se añadirá cuando exista la entidad Ejercicio)
    // @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Ejercicio> ejercicios;
}
