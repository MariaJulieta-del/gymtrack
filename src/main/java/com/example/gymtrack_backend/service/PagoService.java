package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.PagoConMembresiaRequestDTO;
import com.example.gymtrack_backend.dto.PagoResponseDTO;
import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.Pago;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.entities.enums.EstadoPago;
import com.example.gymtrack_backend.entities.enums.MetodoPago;
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
    // REGISTRAR PAGO + CREAR MEMBRESÍA (flujo principal desde Pagos)
    // ──────────────────────────────────────────────

    /**
     * Crea una membresía ACTIVA para el socio y registra el pago en una
     * única transacción. El precio y la duración salen del tarifario.
     */
    @Transactional
    public PagoResponseDTO registrarConMembresia(PagoConMembresiaRequestDTO dto) {
        // Crea la membresía activa (valida socio activo único, busca tarifa)
        Membresia membresia = membresiaService.crearMembresiaActiva(
                dto.getSocioId(), dto.getPlanTipo(), null);

        // Registra el pago asociado
        Pago pago = new Pago();
        pago.setMembresia(membresia);
        pago.setMonto(membresia.getPrecio());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstadoPago(EstadoPago.COMPLETADO);
        pago.setObservaciones(dto.getObservaciones());

        return mapearAResponseDTO(pagoRepository.save(pago));
    }

    // ──────────────────────────────────────────────
    // SALDAR DEUDA (pagar membresía PENDIENTE_PAGO existente)
    // ──────────────────────────────────────────────

    /**
     * Registra el pago de una membresía que estaba en PENDIENTE_PAGO
     * y la activa. Usado para saldar deudas de "fiado".
     */
    @Transactional
    public PagoResponseDTO saldarDeuda(Long membresiaId, MetodoPago metodoPago, String observaciones) {
        Membresia membresia = membresiaRepository.findById(membresiaId)
            .orElseThrow(() -> new RuntimeException("Membresía no encontrada: " + membresiaId));

        if (membresia.getEstadoMembresia() != EstadoMembresia.PENDIENTE_PAGO) {
            throw new RuntimeException("La membresía no está en estado PENDIENTE_PAGO (estado actual: "
                + membresia.getEstadoMembresia() + ")");
        }

        // Activar la membresía
        membresia.setEstadoMembresia(EstadoMembresia.ACTIVA);
        membresiaRepository.save(membresia);

        // Registrar el pago
        Pago pago = new Pago();
        pago.setMembresia(membresia);
        pago.setMonto(membresia.getPrecio());
        pago.setMetodoPago(metodoPago);
        pago.setEstadoPago(EstadoPago.COMPLETADO);
        pago.setObservaciones(observaciones);

        return mapearAResponseDTO(pagoRepository.save(pago));
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

        if (p.getMembresia() != null) {
            dto.setMembresiaId(p.getMembresia().getId());
            dto.setTipoMembresia(p.getMembresia().getTipoMembresia());
            dto.setEstadoMembresia(p.getMembresia().getEstadoMembresia());

            if (p.getMembresia().getSocio() != null) {
                dto.setSocioId(p.getMembresia().getSocio().getId());
                dto.setSocioNombre(p.getMembresia().getSocio().getNombre()
                    + " " + p.getMembresia().getSocio().getApellido());
            } else {
                dto.setSocioNombre("Socio eliminado");
            }
        }

        dto.setMonto(p.getMonto());
        dto.setMetodoPago(p.getMetodoPago());
        dto.setEstadoPago(p.getEstadoPago());
        dto.setFechaPago(p.getFechaPago());
        dto.setObservaciones(p.getObservaciones());
        return dto;
    }
}
