package com.example.gymtrack_backend.entities;

import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Membresía de un socio en el gimnasio.
 * El servicio calcula automáticamente la fechaVencimiento según el TipoMembresia.
 * Una membresía ACTIVA no puede eliminarse; solo cancelarse.
 */
@Data
@Entity
@Table(name = "membresias")
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Socio al que pertenece la membresía
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id", nullable = false)
    private Socio socio;

    // Tipo de membresía — referencia a tarifas_membresia.tipo
    @Column(name = "tipo_membresia", nullable = false, length = 50)
    private String tipoMembresia;

    // Estado actual de la membresía
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_membresia", nullable = false, length = 20)
    private EstadoMembresia estadoMembresia = EstadoMembresia.PENDIENTE_PAGO;

    // Fecha desde la cual la membresía es válida
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    // Fecha calculada: fechaInicio + duración según TipoMembresia
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    // Precio cobrado (puede variar con promociones)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /**
     * Máximo de ingresos permitidos para esta membresía.
     * NULL = membresía por tiempo (sin límite de entradas).
     * > 0  = pase de X clases / entradas (punch-card).
     */
    @Column(name = "entradas_disponibles")
    private Integer entradasDisponibles;

    // Registro de cuándo se creó
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
