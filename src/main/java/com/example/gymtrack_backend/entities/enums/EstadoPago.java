package com.example.gymtrack_backend.entities.enums;

/**
 * Estados posibles de un pago.
 */
public enum EstadoPago {
    PENDIENTE,    // Registrado pero no confirmado
    COMPLETADO,   // Pago confirmado → activa la membresía si estaba PENDIENTE_PAGO
    CANCELADO     // Pago anulado
}
