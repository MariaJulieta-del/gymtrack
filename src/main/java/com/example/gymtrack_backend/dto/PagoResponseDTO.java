package com.example.gymtrack_backend.dto;

import com.example.gymtrack_backend.entities.enums.EstadoPago;
import com.example.gymtrack_backend.entities.enums.MetodoPago;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO que devuelve el backend con los datos de un pago.
 */
@Data
public class PagoResponseDTO {

    private Long id;
    private Long membresiaId;
    private Long socioId;           // Para facilitar filtros en el frontend
    private String socioNombre;     // Nombre del socio (sin FK completa)
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private EstadoPago estadoPago;
    private LocalDateTime fechaPago;
    private String observaciones;
}
