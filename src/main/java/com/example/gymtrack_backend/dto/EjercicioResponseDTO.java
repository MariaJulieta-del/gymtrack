package com.example.gymtrack_backend.dto;

import lombok.Data;

/**
 * DTO que el backend devuelve con los datos de un ejercicio de rutina.
 */
@Data
public class EjercicioResponseDTO {

    private Long id;
    private String nombreEjercicio;
    private Integer series;
    private Integer repeticiones;
    private Double pesoKg;
    private Integer orden;
    private Integer descansoSeg;
    private String notas;
}
