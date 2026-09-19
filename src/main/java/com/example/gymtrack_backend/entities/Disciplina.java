package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

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

    /** Legado — descripción libre del horario */
    private String horario;

    private Integer cupoMaximo;

    @Column(nullable = false)
    private Boolean activa = true;

    /** Días de la semana en los que se dicta la clase */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "disciplina_dias", joinColumns = @JoinColumn(name = "disciplina_id"))
    @Column(name = "dia")
    @OrderColumn(name = "orden")
    private List<String> diasSemana = new ArrayList<>();

    /** Hora de inicio, ej: "18:00" */
    @Column(name = "hora_inicio", length = 10)
    private String horaInicio;

    /** Hora de fin, ej: "19:00" */
    @Column(name = "hora_fin", length = 10)
    private String horaFin;
}
