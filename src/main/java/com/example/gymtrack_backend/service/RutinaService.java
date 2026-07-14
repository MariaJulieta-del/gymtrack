package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.RutinaRequestDTO;
import com.example.gymtrack_backend.dto.RutinaResponseDTO;
import com.example.gymtrack_backend.entities.Rutina;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.RutinaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con la lógica de negocio de las rutinas.
 * Valida propietario en cada operación de escritura.
 */
@Service
public class RutinaService {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private SocioRepository socioRepository;

    // ──────────────────────────────────────────────
    // CREAR
    // ──────────────────────────────────────────────

    /**
     * Crea una nueva rutina para el socio indicado.
     * Lanza excepción si el socio no existe.
     */
    @Transactional
    public RutinaResponseDTO crearRutina(Long socioId, RutinaRequestDTO dto) {
        // Verificar que el socio existe en BD
        Socio socio = socioRepository.findById(socioId)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + socioId));

        // Mapear DTO → entidad
        Rutina rutina = new Rutina();
        rutina.setNombre(dto.getNombre());
        rutina.setDescripcion(dto.getDescripcion());
        rutina.setSocio(socio);

        // Guardar y retornar como DTO
        Rutina guardada = rutinaRepository.save(rutina);
        return mapearAResponseDTO(guardada);
    }

    // ──────────────────────────────────────────────
    // LEER
    // ──────────────────────────────────────────────

    /**
     * Devuelve todas las rutinas que pertenecen al socio.
     */
    public List<RutinaResponseDTO> obtenerRutinas(Long socioId) {
        return rutinaRepository.findBySocioId(socioId)
            .stream()
            .map(this::mapearAResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Devuelve una rutina específica, validando que pertenezca al socio.
     * Lanza excepción si no existe o si el socio no es el propietario.
     */
    public RutinaResponseDTO obtenerRutina(Long rutinaId, Long socioId) {
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no pertenece al socio indicado"));
        return mapearAResponseDTO(rutina);
    }

    // ──────────────────────────────────────────────
    // ACTUALIZAR
    // ──────────────────────────────────────────────

    /**
     * Actualiza nombre y descripción de una rutina.
     * Solo el propietario (socioId) puede modificarla.
     */
    @Transactional
    public RutinaResponseDTO actualizarRutina(Long rutinaId, Long socioId, RutinaRequestDTO dto) {
        // Buscar y validar propietario al mismo tiempo
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no tienes permiso para modificarla"));

        // Actualizar campos
        rutina.setNombre(dto.getNombre());
        rutina.setDescripcion(dto.getDescripcion());

        Rutina actualizada = rutinaRepository.save(rutina);
        return mapearAResponseDTO(actualizada);
    }

    // ──────────────────────────────────────────────
    // ELIMINAR
    // ──────────────────────────────────────────────

    /**
     * Elimina una rutina de la BD.
     * Solo el propietario (socioId) puede eliminarla.
     */
    @Transactional
    public void eliminarRutina(Long rutinaId, Long socioId) {
        // Verificar que existe y que pertenece al socio antes de borrar
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no tienes permiso para eliminarla"));

        rutinaRepository.delete(rutina);
    }

    // ──────────────────────────────────────────────
    // HELPER PRIVADO
    // ──────────────────────────────────────────────

    /**
     * Convierte una entidad Rutina en un RutinaResponseDTO.
     * Centraliza el mapeo para no repetir código.
     */
    private RutinaResponseDTO mapearAResponseDTO(Rutina rutina) {
        RutinaResponseDTO dto = new RutinaResponseDTO();
        dto.setId(rutina.getId());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setSocioId(rutina.getSocio().getId());
        dto.setFechaCreacion(rutina.getFechaCreacion());
        return dto;
    }
}
