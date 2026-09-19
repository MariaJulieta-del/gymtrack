package com.example.gymtrack_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class DisciplinaRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    private String instructor;
    private String horario;
    private Integer cupoMaximo;
    private Boolean activa = true;
    private List<String> diasSemana;
    private String horaInicio;
    private String horaFin;
}
