package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.MembresiaRequestDTO;
import com.example.gymtrack_backend.dto.MembresiaResponseDTO;
import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.entities.enums.TipoMembresia;
import com.example.gymtrack_backend.repository.MembresiaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
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

    // ──────────────────────────────────────────────
    // CREAR
    // ──────────────────────────────────────────────

    /**
     * Crea una membresía nueva en estado PENDIENTE_PAGO.
     * Calcula automáticamente la fechaVencimiento según el TipoMembresia.
     */
    @Transactional
    public MembresiaResponseDTO crearMembresia(MembresiaRequestDTO dto) {
        Socio socio = socioRepository.findById(dto.getSocioId())
            .orElseThrow(() -> new RuntimeException("Socio no encontrado: " + dto.getSocioId()));

        LocalDate inicio = dto.getFechaInicio() != null ? dto.getFechaInicio() : LocalDate.now();
        LocalDate vencimiento = calcularVencimiento(inicio, dto.getTipoMembresia());

        Membresia membresia = new Membresia();
        membresia.setSocio(socio);
        membresia.setTipoMembresia(dto.getTipoMembresia());
        membresia.setEstadoMembresia(EstadoMembresia.PENDIENTE_PAGO);
        membresia.setFechaInicio(inicio);
        membresia.setFechaVencimiento(vencimiento);
        membresia.setPrecio(dto.getPrecio());

        return mapearAResponseDTO(membresiaRepository.save(membresia));
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

    // ──────────────────────────────────────────────
    // CAMBIO DE ESTADO
    // ──────────────────────────────────────────────

    /**
     * Cancela una membresía.
     * No se permite cancelar una membresía ya CANCELADA o VENCIDA.
     * Tampoco se puede eliminar una membresía ACTIVA: solo cancelar.
     */
    @Transactional
    public MembresiaResponseDTO cambiarEstado(Long id, EstadoMembresia nuevoEstado) {
        Membresia membresia = membresiaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + id));

        // Solo se puede cancelar; el resto de transiciones las maneja el sistema
        if (nuevoEstado == EstadoMembresia.CANCELADA &&
            (membresia.getEstadoMembresia() == EstadoMembresia.CANCELADA ||
             membresia.getEstadoMembresia() == EstadoMembresia.VENCIDA)) {
            throw new RuntimeException("No se puede cancelar una membresía en estado: "
                + membresia.getEstadoMembresia());
        }

        membresia.setEstadoMembresia(nuevoEstado);
        return mapearAResponseDTO(membresiaRepository.save(membresia));
    }

    /**
     * Activa una membresía (llamado por PagoService al confirmar un pago).
     */
    @Transactional
    public void activarMembresia(Long membresiaId) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
            .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + membresiaId));

        if (membresia.getEstadoMembresia() == EstadoMembresia.PENDIENTE_PAGO) {
            membresia.setEstadoMembresia(EstadoMembresia.ACTIVA);
            membresiaRepository.save(membresia);
        }
    }

    // ──────────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────────

    /**
     * Calcula la fecha de vencimiento sumando meses según el tipo.
     */
    private LocalDate calcularVencimiento(LocalDate inicio, TipoMembresia tipo) {
        return switch (tipo) {
            case MENSUAL     -> inicio.plusMonths(1);
            case TRIMESTRAL  -> inicio.plusMonths(3);
            case SEMESTRAL   -> inicio.plusMonths(6);
            case ANUAL       -> inicio.plusMonths(12);
        };
    }

    private MembresiaResponseDTO mapearAResponseDTO(Membresia m) {
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
