package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.EjercicioRequestDTO;
import com.example.gymtrack_backend.dto.EjercicioResponseDTO;
import com.example.gymtrack_backend.dto.RutinaRequestDTO;
import com.example.gymtrack_backend.dto.RutinaResponseDTO;
import com.example.gymtrack_backend.entities.Rutina;
import com.example.gymtrack_backend.entities.RutinaEjercicio;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.RutinaEjercicioRepository;
import com.example.gymtrack_backend.repository.RutinaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con la lógica de negocio de las rutinas y sus ejercicios.
 * Valida propietario en cada operación de escritura.
 */
@Service
public class RutinaService {

    @Autowired
    private RutinaRepository rutinaRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private RutinaEjercicioRepository rutinaEjercicioRepository;

    // ──────────────────────────────────────────────
    // RUTINAS - CRUD
    // ──────────────────────────────────────────────

    @Transactional
    public RutinaResponseDTO crearRutina(Long socioId, RutinaRequestDTO dto) {
        Socio socio = socioRepository.findById(socioId)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + socioId));

        Rutina rutina = new Rutina();
        rutina.setNombre(dto.getNombre());
        rutina.setDescripcion(dto.getDescripcion());
        rutina.setSocio(socio);

        return mapearAResponseDTO(rutinaRepository.save(rutina));
    }

    /** Devuelve las rutinas de un socio (para rol SOCIO o EMPLEADO con filtro). */
    public List<RutinaResponseDTO> obtenerRutinas(Long socioId) {
        return rutinaRepository.findBySocioId(socioId)
            .stream()
            .map(this::mapearAResponseDTO)
            .collect(Collectors.toList());
    }

    /** Devuelve TODAS las rutinas del sistema (para rol ADMIN). */
    public List<RutinaResponseDTO> listarTodas() {
        return rutinaRepository.findAll()
            .stream()
            .map(this::mapearAResponseDTO)
            .collect(Collectors.toList());
    }

    public RutinaResponseDTO obtenerRutina(Long rutinaId, Long socioId) {
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no pertenece al socio indicado"));
        return mapearAResponseDTO(rutina);
    }

    @Transactional
    public RutinaResponseDTO actualizarRutina(Long rutinaId, Long socioId, RutinaRequestDTO dto) {
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no tienes permiso para modificarla"));

        rutina.setNombre(dto.getNombre());
        rutina.setDescripcion(dto.getDescripcion());

        return mapearAResponseDTO(rutinaRepository.save(rutina));
    }

    @Transactional
    public void eliminarRutina(Long rutinaId, Long socioId) {
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no tienes permiso para eliminarla"));
        rutinaRepository.delete(rutina);
    }

    // ──────────────────────────────────────────────
    // EJERCICIOS
    // ──────────────────────────────────────────────

    /**
     * Devuelve los ejercicios de una rutina, validando que el socio sea el propietario.
     */
    public List<EjercicioResponseDTO> obtenerEjercicios(Long rutinaId, Long socioId) {
        rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no pertenece al socio"));
        return rutinaEjercicioRepository.findByRutinaIdOrderByOrden(rutinaId)
            .stream()
            .map(this::mapearEjercicioADTO)
            .collect(Collectors.toList());
    }

    /**
     * Agrega un ejercicio a la rutina. El orden se auto-asigna si no se provee.
     */
    @Transactional
    public EjercicioResponseDTO agregarEjercicio(Long rutinaId, Long socioId, EjercicioRequestDTO dto) {
        Rutina rutina = rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no pertenece al socio"));

        RutinaEjercicio ej = new RutinaEjercicio();
        ej.setRutina(rutina);
        ej.setNombreEjercicio(dto.getNombreEjercicio());
        ej.setSeries(dto.getSeries());
        ej.setRepeticiones(dto.getRepeticiones());
        ej.setPesoKg(dto.getPesoKg());
        ej.setDescansoSeg(dto.getDescansoSeg());
        ej.setNotas(dto.getNotas());

        // Auto-asignar orden si no se provee
        int orden = (dto.getOrden() != null)
            ? dto.getOrden()
            : rutinaEjercicioRepository.countByRutinaId(rutinaId) + 1;
        ej.setOrden(orden);

        return mapearEjercicioADTO(rutinaEjercicioRepository.save(ej));
    }

    /**
     * Elimina un ejercicio de la rutina, validando que el socio sea el propietario.
     */
    @Transactional
    public void eliminarEjercicio(Long ejercicioId, Long rutinaId, Long socioId) {
        rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
            .orElseThrow(() -> new RuntimeException(
                "Rutina no encontrada o no pertenece al socio"));
        RutinaEjercicio ej = rutinaEjercicioRepository.findById(ejercicioId)
            .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
        rutinaEjercicioRepository.delete(ej);
    }

    // ──────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ──────────────────────────────────────────────

    private RutinaResponseDTO mapearAResponseDTO(Rutina rutina) {
        RutinaResponseDTO dto = new RutinaResponseDTO();
        dto.setId(rutina.getId());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setSocioId(rutina.getSocio().getId());
        dto.setFechaCreacion(rutina.getFechaCreacion());
        return dto;
    }

    private EjercicioResponseDTO mapearEjercicioADTO(RutinaEjercicio ej) {
        EjercicioResponseDTO dto = new EjercicioResponseDTO();
        dto.setId(ej.getId());
        dto.setNombreEjercicio(ej.getNombreEjercicio());
        dto.setSeries(ej.getSeries());
        dto.setRepeticiones(ej.getRepeticiones());
        dto.setPesoKg(ej.getPesoKg());
        dto.setOrden(ej.getOrden());
        dto.setDescansoSeg(ej.getDescansoSeg());
        dto.setNotas(ej.getNotas());
        return dto;
    }
}
