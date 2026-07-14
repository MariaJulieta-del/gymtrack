package com.example.gymtrack_backend.entities.enums;

/**
 * Tipos de membresía disponibles.
 * El servicio usa este enum para calcular la fechaVencimiento automáticamente.
 */
public enum TipoMembresia {
    MENSUAL,      // 1 mes
    TRIMESTRAL,   // 3 meses
    SEMESTRAL,    // 6 meses
    ANUAL         // 12 meses
}
