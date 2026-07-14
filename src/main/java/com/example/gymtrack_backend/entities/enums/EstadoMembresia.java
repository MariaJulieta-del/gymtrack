package com.example.gymtrack_backend.entities.enums;

/**
 * Estados posibles de una membresía.
 */
public enum EstadoMembresia {
    PENDIENTE_PAGO,  // Creada pero aún sin pago registrado
    ACTIVA,          // Pago confirmado y dentro del período de vigencia
    VENCIDA,         // La fechaVencimiento ya pasó
    CANCELADA        // Cancelada manualmente (no se puede eliminar si fue ACTIVA)
}
