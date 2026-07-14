package com.example.gymtrack_backend.dto;

import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.entities.enums.TipoMembresia;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO que devuelve el backend con los datos de una membresía.
 */
@Data
public class MembresiaResponseDTO {

    private Long id;
    private Long socioId;
    private String socioNombreCompleto; // "nombre apellido" para mostrar en frontend
    private TipoMembresia tipoMembresia;
    private EstadoMembresia estadoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private BigDecimal precio;
    private LocalDateTime fechaCreacion;
}
