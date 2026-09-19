package com.example.gymtrack_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear o modificar un ejercicio dentro de una rutina.
 */
@Data
public class EjercicioRequestDTO {

    @NotBlank(message = "El nombre del ejercicio es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombreEjercicio;

    private Integer series;

    private Integer repeticiones;

    private Double pesoKg;

    // Si no se provee, el backend lo asigna automáticamente (siguiente disponible)
    private Integer orden;

    // Descanso recomendado entre series (en segundos)
    private Integer descansoSeg;

    @Size(max = 500)
    private String notas;
}
