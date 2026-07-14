package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.PagoRequestDTO;
import com.example.gymtrack_backend.dto.PagoResponseDTO;
import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.Pago;
import com.example.gymtrack_backend.entities.enums.EstadoPago;
import com.example.gymtrack_backend.repository.MembresiaRepository;
import com.example.gymtrack_backend.repository.PagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private MembresiaRepository membresiaRepository;

    @Autowired
    private MembresiaService membresiaService;

    // ──────────────────────────────────────────────
    // REGISTRAR PAGO
    // ──────────────────────────────────────────────

    /**
     * Registra un nuevo pago.
     * Si el pago es COMPLETADO y la membresía estaba en PENDIENTE_PAGO,
     * la membresía pasa automáticamente a ACTIVA.
     */
    @Transactional
    public PagoResponseDTO registrarPago(PagoRequestDTO dto) {
        Membresia membresia = membresiaRepository.findById(dto.getMembresiaId())
            .orElseThrow(() -> new RuntimeException(
                "Membresía no encontrada: " + dto.getMembresiaId()));

        // Crear el pago
        Pago pago = new Pago();
        pago.setMembresia(membresia);
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago(EstadoPago.COMPLETADO); // Por defecto se registra como completado
        pago.setObservaciones(dto.getObservaciones());

        Pago guardado = pagoRepository.save(pago);

        // Si el pago es COMPLETADO → activar membresía si estaba pendiente
        if (guardado.getEstadoPago() == EstadoPago.COMPLETADO) {
            membresiaService.activarMembresia(membresia.getId());
        }

        return mapearAResponseDTO(guardado);
    }

    // ──────────────────────────────────────────────
    // LISTAR
    // ──────────────────────────────────────────────

    /** Lista todos los pagos */
    public List<PagoResponseDTO> listarTodos() {
        return pagoRepository.findAll()
            .stream().map(this::mapearAResponseDTO).collect(Collectors.toList());
    }

    /** Historial de pagos de una membresía específica */
    public List<PagoResponseDTO> obtenerPorMembresia(Long membresiaId) {
        return pagoRepository.findByMembresiaIdOrderByFechaPagoDesc(membresiaId)
            .stream().map(this::mapearAResponseDTO).collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────

    private PagoResponseDTO mapearAResponseDTO(Pago p) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(p.getId());
        dto.setMembresiaId(p.getMembresia().getId());
        dto.setSocioId(p.getMembresia().getSocio().getId());
        dto.setSocioNombre(p.getMembresia().getSocio().getNombre()
            + " " + p.getMembresia().getSocio().getApellido());
        dto.setMonto(p.getMonto());
        dto.setMetodoPago(p.getMetodoPago());
        dto.setEstadoPago(p.getEstadoPago());
        dto.setFechaPago(p.getFechaPago());
        dto.setObservaciones(p.getObservaciones());
        return dto;
    }
}
