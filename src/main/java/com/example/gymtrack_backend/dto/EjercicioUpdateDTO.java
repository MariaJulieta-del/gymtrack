package com.example.gymtrack_backend.dto;

import lombok.Data;

/**
 * DTO para actualizar series/reps/peso/descanso/notas de un ejercicio en una rutina.
 * Todos los campos son opcionales — solo se actualizan los que llegan no nulos.
 */
@Data
public class EjercicioUpdateDTO {
    private Integer series;
    private Integer repeticiones;
    private Double  pesoKg;
    private Integer descansoSeg;
    private String  notas;
}
