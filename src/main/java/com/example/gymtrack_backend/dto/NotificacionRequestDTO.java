package com.example.gymtrack_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificacionRequestDTO {
    @NotBlank
    private String titulo;
    @NotBlank
    private String mensaje;
    private String tipo        = "AVISO";
    private String destinatario = "TODOS";
    private Long   socioId;
}
