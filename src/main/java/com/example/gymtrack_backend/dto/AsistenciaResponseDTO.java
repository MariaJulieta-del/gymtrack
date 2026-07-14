package com.example.gymtrack_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class AsistenciaResponseDTO {
    private Long      id;
    private Long      socioId;
    private String    socioNombre;
    private String    socioDni;
    private LocalDate fecha;
    private LocalTime hora;
    private String    tipo;
    private Boolean   permitido;
    private String    observacion;
}
