package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.PagoConMembresiaRequestDTO;
import com.example.gymtrack_backend.dto.PagoResponseDTO;
import com.example.gymtrack_backend.entities.enums.MetodoPago;
import com.example.gymtrack_backend.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST Controller para el módulo de Pagos.
 */
@RestController
@RequestMapping("/api/v1/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // GET /api/v1/pagos → listar todos
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listar() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    // POST /api/v1/pagos/con-membresia → registra pago y activa membresía en un paso
    @PostMapping("/con-membresia")
    public ResponseEntity<?> registrarConMembresia(
            @Valid @RequestBody PagoConMembresiaRequestDTO dto) {
        try {
            return ResponseEntity.ok(pagoService.registrarConMembresia(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al registrar el pago")
            );
        }
    }

    // POST /api/v1/pagos/saldar-deuda → paga membresía PENDIENTE_PAGO y la activa
    @PostMapping("/saldar-deuda")
    public ResponseEntity<?> saldarDeuda(@RequestBody Map<String, Object> body) {
        try {
            Long       membresiaId  = Long.valueOf(body.get("membresiaId").toString());
            MetodoPago metodoPago   = MetodoPago.valueOf(body.get("metodoPago").toString());
            String     observaciones = body.containsKey("observaciones")
                                        ? body.get("observaciones").toString() : null;
            return ResponseEntity.ok(pagoService.saldarDeuda(membresiaId, metodoPago, observaciones));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al saldar la deuda"));
        }
    }

    // GET /api/v1/pagos/membresia/{membresiaId} → historial por membresía
    @GetMapping("/membresia/{membresiaId}")
    public ResponseEntity<List<PagoResponseDTO>> porMembresia(
            @PathVariable Long membresiaId) {
        try {
            return ResponseEntity.ok(pagoService.obtenerPorMembresia(membresiaId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
