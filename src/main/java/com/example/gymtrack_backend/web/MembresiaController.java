package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.MembresiaResponseDTO;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.service.MembresiaService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST Controller para el módulo de Membresías.
 * La creación de membresías se realiza desde PagoController (POST /pagos/con-membresia),
 * que registra el pago y activa la membresía en una única operación.
 */
@RestController
@RequestMapping("/api/v1/membresias")
@CrossOrigin(origins = "*")
public class MembresiaController {

    @Autowired
    private MembresiaService membresiaService;

    // GET /api/v1/membresias → listar todas
    @GetMapping
    public ResponseEntity<List<MembresiaResponseDTO>> listar() {
        return ResponseEntity.ok(membresiaService.listarTodas());
    }

    // GET /api/v1/membresias/{id} → detalle
    @GetMapping("/{id}")
    public ResponseEntity<MembresiaResponseDTO> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(membresiaService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/v1/membresias/socio/{socioId} → membresías de un socio
    @GetMapping("/socio/{socioId}")
    public ResponseEntity<List<MembresiaResponseDTO>> listarPorSocio(@PathVariable Long socioId) {
        return ResponseEntity.ok(membresiaService.listarPorSocio(socioId));
    }

    // GET /api/v1/membresias/proximas-a-vencer?dias=7 → membresías ACTIVAS que vencen pronto
    @GetMapping("/proximas-a-vencer")
    public ResponseEntity<List<MembresiaResponseDTO>> proximasAVencer(
            @RequestParam(defaultValue = "7") int dias) {
        return ResponseEntity.ok(membresiaService.listarProximasAVencer(dias));
    }

    // POST /api/v1/membresias/pendiente → asignar plan sin pago (fiado)
    @PostMapping("/pendiente")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> crearPendiente(@RequestBody Map<String, Object> body) {
        try {
            Long   socioId  = Long.valueOf(body.get("socioId").toString());
            String planTipo = body.get("planTipo").toString();
            var m = membresiaService.crearMembresiaPendiente(socioId, planTipo);
            return ResponseEntity.ok(membresiaService.mapearAResponseDTO(m));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Error al asignar el plan"));
        }
    }

    // PATCH /api/v1/membresias/{id}/estado → cancelar
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<MembresiaResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoMembresia nuevoEstado) {
        try {
            return ResponseEntity.ok(membresiaService.cambiarEstado(id, nuevoEstado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
