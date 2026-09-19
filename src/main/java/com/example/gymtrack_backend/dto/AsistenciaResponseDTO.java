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
    private Boolean   conDeuda;

    // ── Info de membresía (opcional, solo se llena en checkin) ──
    /** Fecha de vencimiento de la membresía activa */
    private LocalDate fechaVencimiento;
    /** Tipo de plan (ej: MENSUAL, PASE_10_CLASES) */
    private String    tipoMembresia;
    /** Entradas totales del plan (null = plan por tiempo) */
    private Integer   entradasDisponibles;
    /** Entradas ya usadas dentro del período de la membresía */
    private Integer   entradasUsadas;
    /** Entradas restantes = entradasDisponibles - entradasUsadas (null si plan por tiempo) */
    private Integer   entradasRestantes;
}
