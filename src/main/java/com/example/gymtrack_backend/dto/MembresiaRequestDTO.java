package com.example.gymtrack_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * DTO para crear una membresía.
 * El precio y la duración se obtienen automáticamente del tarifario.
 */
@Data
public class MembresiaRequestDTO {

    @NotNull(message = "El ID del socio es obligatorio")
    private Long socioId;

    @NotBlank(message = "El tipo de membresía es obligatorio")
    private String tipoMembresia; // Referencia a tarifas_membresia.tipo

    // Fecha de inicio; si es null el servicio usa la fecha actual
    private LocalDate fechaInicio;
}
