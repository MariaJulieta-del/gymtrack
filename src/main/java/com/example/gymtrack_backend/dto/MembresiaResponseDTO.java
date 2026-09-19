package com.example.gymtrack_backend.dto;

import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO que devuelve el backend con los datos de una membresía de socio.
 */
@Data
public class MembresiaResponseDTO {

    private Long id;
    private Long socioId;
    private String socioNombreCompleto;
    private String tipoMembresia; // String (no enum) para soportar tipos custom
    private EstadoMembresia estadoMembresia;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private BigDecimal precio;
    private LocalDateTime fechaCreacion;
}
