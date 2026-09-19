package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tarifas_membresia")
public class TarifaMembresia {

    @Id
    @Column(name = "tipo", length = 20)
    private String tipo; // MENSUAL, TRIMESTRAL, SEMESTRAL, ANUAL

    @Column(length = 100)
    private String nombre;

    @Column(nullable = false)
    private BigDecimal precio;

    @Column(name = "duracion_dias")
    private Integer duracionDias;

    private String descripcion;

    private String incluye;
}
