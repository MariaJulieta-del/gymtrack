package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ejercicios_catalogo")
public class Ejercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Grupos musculares que trabaja (puede ser más de uno) */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ejercicio_grupos", joinColumns = @JoinColumn(name = "ejercicio_id"))
    @Column(name = "grupo")
    private List<String> grupos = new ArrayList<>();

    @Column(name = "es_maquina", nullable = false)
    private Boolean esMaquina = false;

    @Column(nullable = false)
    private Boolean activo = true;

    /** Imagen representativa — almacenada como data URL base64 o URL externa */
    @Column(name = "imagen_url", columnDefinition = "TEXT")
    private String imagenUrl;

    /** IDs de máquinas del catálogo necesarias para realizar este ejercicio */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "ejercicio_maquinas_requeridas",
        joinColumns = @JoinColumn(name = "ejercicio_id")
    )
    @Column(name = "maquina_id")
    private List<Long> maquinaIds = new ArrayList<>();
}
