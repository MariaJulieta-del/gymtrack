package com.example.gymtrack_backend.dto;

import com.example.gymtrack_backend.entities.enums.TipoMembresia;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para crear una membresía.
 * fechaVencimiento se calcula en el Service según tipoMembresia.
 */
@Data
public class MembresiaRequestDTO {

    @NotNull(message = "El ID del socio es obligatorio")
    private Long socioId;

    @NotNull(message = "El tipo de membresía es obligatorio")
    private TipoMembresia tipoMembresia;

    // Fecha de inicio; si es null el servicio usa la fecha actual
    private LocalDate fechaInicio;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    private BigDecimal precio;
}
