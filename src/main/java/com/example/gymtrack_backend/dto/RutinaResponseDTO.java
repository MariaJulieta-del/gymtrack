package com.example.gymtrack_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO que el backend devuelve al frontend con los datos de una rutina.
 * Evita exponer la entidad completa (incluyendo la relación con Socio).
 */
@Data
public class RutinaResponseDTO {

    private Long id;

    private String nombre;

    private String descripcion;

    // Solo se devuelve el ID del socio, no el objeto completo
    private Long socioId;

    private LocalDateTime fechaCreacion;
}
