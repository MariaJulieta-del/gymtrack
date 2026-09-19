package com.example.gymtrack_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DisciplinaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String instructor;
    private String horario;
    private Integer cupoMaximo;
    private Boolean activa;
    private List<String> diasSemana;
    private String horaInicio;
    private String horaFin;

    /** Cuántos socios están inscriptos actualmente */
    private int totalInscritos;

    /** Cupos libres (cupoMaximo - totalInscritos), null si no hay cupo máximo */
    private Integer cuposDisponibles;

    /** Nombres completos de los socios inscriptos */
    private List<String> sociosInscritos;
}
