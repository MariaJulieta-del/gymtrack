package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.EjercicioRequestDTO;
import com.example.gymtrack_backend.dto.EjercicioResponseDTO;
import com.example.gymtrack_backend.dto.EjercicioUpdateDTO;
import com.example.gymtrack_backend.dto.RutinaRequestDTO;
import com.example.gymtrack_backend.dto.RutinaResponseDTO;
import com.example.gymtrack_backend.entities.Ejercicio;
import com.example.gymtrack_backend.entities.Rutina;
import com.example.gymtrack_backend.entities.RutinaEjercicio;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.EjercicioRepository;
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

    @Autowired
    private EjercicioRepository ejercicioRepository;

    // ──────────────────────────────────────────────
    // RUTINAS - CRUD
    // ──────────────────────────────────────────────

    @Transactional
    public RutinaResponseDTO crearRutina(Long socioId, RutinaRequestDTO dto) {
        Rutina rutina = new Rutina();
        rutina.setNombre(dto.getNombre());
        rutina.setDescripcion(dto.getDescripcion());
        rutina.setDificultad(dto.getDificultad());
        if (dto.getTrenes() != null) {
            rutina.getTrenes().clear();
            rutina.getTrenes().addAll(dto.getTrenes());
        }
        if (dto.getDiasSemana() != null) {
            rutina.getDiasSemana().clear();
            rutina.getDiasSemana().addAll(dto.getDiasSemana());
        }

        if (socioId != null) {
            Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RuntimeException("Socio no encontrado con id: " + socioId));
            rutina.setSocio(socio);
        }
        // socioId == null → rutina general sin socio asignado

        return mapearAResponseDTO(rutinaRepository.save(rutina));
    }

    /** Devuelve las rutinas generales (sin socio asignado). Solo para ADMIN. */
    public List<RutinaResponseDTO> listarGenerales() {
        return rutinaRepository.findBySocioIsNull()
            .stream()
            .map(this::mapearAResponseDTO)
            .collect(Collectors.toList());
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
        Rutina rutina = findRutina(rutinaId, socioId);
        return mapearAResponseDTO(rutina);
    }

    @Transactional
    public RutinaResponseDTO actualizarRutina(Long rutinaId, Long socioId, RutinaRequestDTO dto) {
        Rutina rutina = findRutina(rutinaId, socioId);

        rutina.setNombre(dto.getNombre());
        rutina.setDescripcion(dto.getDescripcion());
        rutina.setDificultad(dto.getDificultad());
        if (dto.getTrenes() != null) {
            rutina.getTrenes().clear();
            rutina.getTrenes().addAll(dto.getTrenes());
        }
        if (dto.getDiasSemana() != null) {
            rutina.getDiasSemana().clear();
            rutina.getDiasSemana().addAll(dto.getDiasSemana());
        }

        return mapearAResponseDTO(rutinaRepository.save(rutina));
    }

    @Transactional
    public void eliminarRutina(Long rutinaId, Long socioId) {
        Rutina rutina = findRutina(rutinaId, socioId);
        rutinaRepository.delete(rutina);
    }

    // ──────────────────────────────────────────────
    // EJERCICIOS
    // ──────────────────────────────────────────────

    /**
     * Devuelve los ejercicios de una rutina, validando que el socio sea el propietario.
     */
    public List<EjercicioResponseDTO> obtenerEjercicios(Long rutinaId, Long socioId) {
        findRutina(rutinaId, socioId);
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
        Rutina rutina = findRutina(rutinaId, socioId);

        RutinaEjercicio ej = new RutinaEjercicio();
        ej.setRutina(rutina);
        ej.setNombreEjercicio(dto.getNombreEjercicio());
        ej.setSeries(dto.getSeries());
        ej.setRepeticiones(dto.getRepeticiones());
        ej.setPesoKg(dto.getPesoKg());
        ej.setDescansoSeg(dto.getDescansoSeg());
        ej.setNotas(dto.getNotas());

        // Vincular al catálogo si se provee un ID
        if (dto.getEjercicioCatalogoId() != null) {
            ejercicioRepository.findById(dto.getEjercicioCatalogoId())
                    .ifPresent(ej::setEjercicioCatalogo);
        }

        // Auto-asignar orden si no se provee
        int orden = (dto.getOrden() != null)
            ? dto.getOrden()
            : rutinaEjercicioRepository.countByRutinaId(rutinaId) + 1;
        ej.setOrden(orden);

        return mapearEjercicioADTO(rutinaEjercicioRepository.save(ej));
    }

    /**
     * Actualiza series/reps/peso/descanso/notas de un ejercicio en la rutina.
     */
    @Transactional
    public EjercicioResponseDTO actualizarEjercicio(Long ejercicioId, Long rutinaId, Long socioId, EjercicioUpdateDTO dto) {
        findRutina(rutinaId, socioId);
        RutinaEjercicio ej = rutinaEjercicioRepository.findById(ejercicioId)
            .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
        if (dto.getSeries() != null)       ej.setSeries(dto.getSeries());
        if (dto.getRepeticiones() != null) ej.setRepeticiones(dto.getRepeticiones());
        if (dto.getPesoKg() != null)       ej.setPesoKg(dto.getPesoKg());
        if (dto.getDescansoSeg() != null)  ej.setDescansoSeg(dto.getDescansoSeg());
        if (dto.getNotas() != null)        ej.setNotas(dto.getNotas());
        return mapearEjercicioADTO(rutinaEjercicioRepository.save(ej));
    }

    /**
     * Elimina un ejercicio de la rutina, validando que el socio sea el propietario.
     */
    @Transactional
    public void eliminarEjercicio(Long ejercicioId, Long rutinaId, Long socioId) {
        findRutina(rutinaId, socioId);
        RutinaEjercicio ej = rutinaEjercicioRepository.findById(ejercicioId)
            .orElseThrow(() -> new RuntimeException("Ejercicio no encontrado"));
        rutinaEjercicioRepository.delete(ej);
    }

    // ──────────────────────────────────────────────
    // HELPERS PRIVADOS
    // ──────────────────────────────────────────────

    /**
     * Busca una rutina:
     * - Si socioId != null → valida que pertenezca al socio (rutina de socio)
     * - Si socioId == null → busca solo por ID (rutina general / admin)
     */
    private Rutina findRutina(Long rutinaId, Long socioId) {
        if (socioId != null) {
            return rutinaRepository.findByIdAndSocioId(rutinaId, socioId)
                .orElseThrow(() -> new RuntimeException(
                    "Rutina no encontrada o no pertenece al socio indicado"));
        }
        return rutinaRepository.findById(rutinaId)
            .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
    }

    private RutinaResponseDTO mapearAResponseDTO(Rutina rutina) {
        RutinaResponseDTO dto = new RutinaResponseDTO();
        dto.setId(rutina.getId());
        dto.setNombre(rutina.getNombre());
        dto.setDescripcion(rutina.getDescripcion());
        dto.setTrenes(rutina.getTrenes() != null ? rutina.getTrenes() : new java.util.ArrayList<>());
        dto.setSocioId(rutina.getSocio() != null ? rutina.getSocio().getId() : null);
        dto.setSocioNombre(rutina.getSocio() != null
                ? rutina.getSocio().getNombre() + " " + rutina.getSocio().getApellido()
                : null);
        dto.setFechaCreacion(rutina.getFechaCreacion());
        dto.setDiasSemana(rutina.getDiasSemana());
        dto.setDificultad(rutina.getDificultad());
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
        // Info del catálogo si está vinculado
        Ejercicio cat = ej.getEjercicioCatalogo();
        if (cat != null) {
            dto.setEjercicioCatalogoId(cat.getId());
            dto.setEsMaquina(cat.getEsMaquina());
            dto.setDescripcionEjercicio(cat.getDescripcion());
            dto.setGrupos(cat.getGrupos() != null ? cat.getGrupos() : new java.util.ArrayList<>());
            dto.setImagenUrl(cat.getImagenUrl());
            dto.setMaquinaIds(cat.getMaquinaIds() != null ? cat.getMaquinaIds() : new java.util.ArrayList<>());
        }
        return dto;
    }
}
