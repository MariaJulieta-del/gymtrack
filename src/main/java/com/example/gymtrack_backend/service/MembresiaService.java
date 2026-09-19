package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.MembresiaRequestDTO;
import com.example.gymtrack_backend.dto.MembresiaResponseDTO;
import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.entities.TarifaMembresia;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.repository.MembresiaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import com.example.gymtrack_backend.repository.TarifaMembresiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembresiaService {

    @Autowired
    private MembresiaRepository membresiaRepository;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private TarifaMembresiaRepository tarifaRepository;

    // ──────────────────────────────────────────────
    // CREAR (desde el módulo de Pagos — flujo principal)
    // ──────────────────────────────────────────────

    /**
     * Crea una membresía ACTIVA para un socio.
     * El precio y la duración se obtienen del tarifario.
     * El socio solo puede tener 1 membresía ACTIVA a la vez.
     */
    @Transactional
    public Membresia crearMembresiaActiva(Long socioId, String planTipo, LocalDate fechaInicio) {
        Socio socio = socioRepository.findById(socioId)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado: " + socioId));

        // Verificar que no tenga membresía activa
        if (membresiaRepository.existsBySocioIdAndEstadoMembresia(socioId, EstadoMembresia.ACTIVA)) {
            throw new RuntimeException("El socio ya tiene una membresía activa. Cancelala primero.");
        }

        // Obtener datos del plan desde el tarifario
        TarifaMembresia tarifa = tarifaRepository.findById(planTipo.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + planTipo));

        if (tarifa.getDuracionDias() == null || tarifa.getDuracionDias() <= 0) {
            throw new RuntimeException("El plan no tiene duración configurada: " + planTipo);
        }

        LocalDate inicio = fechaInicio != null ? fechaInicio : LocalDate.now();
        LocalDate vencimiento = inicio.plusDays(tarifa.getDuracionDias());

        Membresia membresia = new Membresia();
        membresia.setSocio(socio);
        membresia.setTipoMembresia(planTipo.toUpperCase());
        membresia.setEstadoMembresia(EstadoMembresia.ACTIVA); // directamente activa al registrar pago
        membresia.setFechaInicio(inicio);
        membresia.setFechaVencimiento(vencimiento);
        membresia.setPrecio(tarifa.getPrecio());

        return membresiaRepository.save(membresia);
    }

    /**
     * Asigna un plan al socio SIN cobrar (fiado).
     * La membresía queda en PENDIENTE_PAGO.
     * El socio podrá ingresar 1 vez antes de ser bloqueado.
     */
    @Transactional
    public Membresia crearMembresiaPendiente(Long socioId, String planTipo) {
        Socio socio = socioRepository.findById(socioId)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado: " + socioId));

        if (membresiaRepository.existsBySocioIdAndEstadoMembresia(socioId, EstadoMembresia.ACTIVA)) {
            throw new RuntimeException("El socio ya tiene una membresía activa.");
        }
        if (membresiaRepository.existsBySocioIdAndEstadoMembresia(socioId, EstadoMembresia.PENDIENTE_PAGO)) {
            throw new RuntimeException("El socio ya tiene una membresía pendiente de pago.");
        }

        TarifaMembresia tarifa = tarifaRepository.findById(planTipo.toUpperCase())
            .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + planTipo));

        LocalDate inicio     = LocalDate.now();
        LocalDate vencimiento = inicio.plusDays(tarifa.getDuracionDias());

        Membresia membresia = new Membresia();
        membresia.setSocio(socio);
        membresia.setTipoMembresia(planTipo.toUpperCase());
        membresia.setEstadoMembresia(EstadoMembresia.PENDIENTE_PAGO);
        membresia.setFechaInicio(inicio);
        membresia.setFechaVencimiento(vencimiento);
        membresia.setPrecio(tarifa.getPrecio());

        return membresiaRepository.save(membresia);
    }

    // ──────────────────────────────────────────────
    // LEER
    // ──────────────────────────────────────────────

    /** Lista todas las membresías (admin) */
    public List<MembresiaResponseDTO> listarTodas() {
        return membresiaRepository.findAll()
            .stream().map(this::mapearAResponseDTO).collect(Collectors.toList());
    }

    /** Obtiene una membresía por ID */
    public MembresiaResponseDTO obtenerPorId(Long id) {
        Membresia m = membresiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + id));
        return mapearAResponseDTO(m);
    }

    /** Lista todas las membresías de un socio */
    public List<MembresiaResponseDTO> listarPorSocio(Long socioId) {
        return membresiaRepository.findBySocioId(socioId)
            .stream().map(this::mapearAResponseDTO).collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    // CAMBIO DE ESTADO
    // ──────────────────────────────────────────────

    @Transactional
    public MembresiaResponseDTO cambiarEstado(Long id, EstadoMembresia nuevoEstado) {
        Membresia membresia = membresiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + id));

        if (nuevoEstado == EstadoMembresia.CANCELADA &&
            (membresia.getEstadoMembresia() == EstadoMembresia.CANCELADA ||
             membresia.getEstadoMembresia() == EstadoMembresia.VENCIDA)) {
            throw new RuntimeException("No se puede cancelar una membresía en estado: "
                + membresia.getEstadoMembresia());
        }

        membresia.setEstadoMembresia(nuevoEstado);
        return mapearAResponseDTO(membresiaRepository.save(membresia));
    }

    // ──────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────

    public MembresiaResponseDTO mapearAResponseDTO(Membresia m) {
        MembresiaResponseDTO dto = new MembresiaResponseDTO();
        dto.setId(m.getId());
        dto.setSocioId(m.getSocio().getId());
        dto.setSocioNombreCompleto(m.getSocio().getNombre() + " " + m.getSocio().getApellido());
        dto.setTipoMembresia(m.getTipoMembresia());
        dto.setEstadoMembresia(m.getEstadoMembresia());
        dto.setFechaInicio(m.getFechaInicio());
        dto.setFechaVencimiento(m.getFechaVencimiento());
        dto.setPrecio(m.getPrecio());
        dto.setFechaCreacion(m.getFechaCreacion());
        return dto;
    }
}
