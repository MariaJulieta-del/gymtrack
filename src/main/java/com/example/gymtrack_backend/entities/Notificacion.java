package com.example.gymtrack_backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    /** AVISO / RECORDATORIO / VENCIMIENTO */
    @Column(nullable = false)
    private String tipo = "AVISO";

    /** TODOS / DISCIPLINA / SOCIO */
    @Column(nullable = false)
    private String destinatario = "TODOS";

    /** Si destinatario = SOCIO, apunta al socio específico */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "socio_id")
    private Socio socio;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean activa = true;
}
