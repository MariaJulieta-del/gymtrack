package com.example.gymtrack_backend.entities;

import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.entities.enums.TipoMembresia;
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

    // Tipo de membresía (determina la duración)
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_membresia", nullable = false, length = 20)
    private TipoMembresia tipoMembresia;

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

    // Registro de cuándo se creó
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
