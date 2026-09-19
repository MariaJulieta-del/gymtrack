package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.EjercicioRequestDTO;
import com.example.gymtrack_backend.dto.EjercicioResponseDTO;
import com.example.gymtrack_backend.dto.EjercicioUpdateDTO;
import com.example.gymtrack_backend.dto.RutinaRequestDTO;
import com.example.gymtrack_backend.dto.RutinaResponseDTO;
import com.example.gymtrack_backend.service.RutinaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para el módulo de Rutinas.
 *
 * Endpoints que usan X-Socio-Id validan que la rutina pertenezca al socio.
 * GET /todas no requiere X-Socio-Id y es para uso del ADMIN.
 */
@RestController
@RequestMapping("/api/v1/rutinas")
@CrossOrigin(origins = "*")
public class RutinaController {

    @Autowired
    private RutinaService rutinaService;

    // ──────────────────────────────────────────────
    // RUTINAS
    // ──────────────────────────────────────────────

    /** GET /api/v1/rutinas — rutinas del socio autenticado */
    @GetMapping
    public ResponseEntity<List<RutinaResponseDTO>> obtenerRutinas(
            @RequestHeader("X-Socio-Id") Long socioId) {
        try {
            return ResponseEntity.ok(rutinaService.obtenerRutinas(socioId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** GET /api/v1/rutinas/todas — todas las rutinas (solo ADMIN) */
    @GetMapping("/todas")
    public ResponseEntity<List<RutinaResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(rutinaService.listarTodas());
    }

    /** GET /api/v1/rutinas/generales — rutinas sin socio asignado (solo ADMIN) */
    @GetMapping("/generales")
    public ResponseEntity<List<RutinaResponseDTO>> obtenerGenerales() {
        return ResponseEntity.ok(rutinaService.listarGenerales());
    }

    /** GET /api/v1/rutinas/{id} — detalle de una rutina */
    @GetMapping("/{id}")
    public ResponseEntity<RutinaResponseDTO> obtenerRutina(
            @PathVariable Long id,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId) {
        try {
            return ResponseEntity.ok(rutinaService.obtenerRutina(id, socioId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/v1/rutinas — crear una nueva rutina (socioId opcional para rutinas generales) */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> crearRutina(
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId,
            @Valid @RequestBody RutinaRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(rutinaService.crearRutina(socioId, requestDTO));
        } catch (RuntimeException e) {
            System.err.println("[RutinaController] Error al crear rutina: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage() != null ? e.getMessage() : "Error interno"));
        }
    }

    /** PUT /api/v1/rutinas/{id} — actualizar una rutina */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<RutinaResponseDTO> actualizarRutina(
            @PathVariable Long id,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId,
            @Valid @RequestBody RutinaRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(rutinaService.actualizarRutina(id, socioId, requestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** DELETE /api/v1/rutinas/{id} — eliminar una rutina */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Void> eliminarRutina(
            @PathVariable Long id,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId) {
        try {
            rutinaService.eliminarRutina(id, socioId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ──────────────────────────────────────────────
    // EJERCICIOS
    // ──────────────────────────────────────────────

    /** GET /api/v1/rutinas/{id}/ejercicios — lista de ejercicios de la rutina */
    @GetMapping("/{id}/ejercicios")
    public ResponseEntity<List<EjercicioResponseDTO>> obtenerEjercicios(
            @PathVariable Long id,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId) {
        try {
            return ResponseEntity.ok(rutinaService.obtenerEjercicios(id, socioId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /** POST /api/v1/rutinas/{id}/ejercicios — agregar un ejercicio a la rutina */
    @PostMapping("/{id}/ejercicios")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<EjercicioResponseDTO> agregarEjercicio(
            @PathVariable Long id,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId,
            @Valid @RequestBody EjercicioRequestDTO dto) {
        try {
            return ResponseEntity.ok(rutinaService.agregarEjercicio(id, socioId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** PATCH /api/v1/rutinas/{id}/ejercicios/{ejId} — actualizar series/reps/peso/descanso */
    @PatchMapping("/{id}/ejercicios/{ejId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<EjercicioResponseDTO> actualizarEjercicio(
            @PathVariable Long id,
            @PathVariable Long ejId,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId,
            @RequestBody EjercicioUpdateDTO dto) {
        try {
            return ResponseEntity.ok(rutinaService.actualizarEjercicio(ejId, id, socioId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /** DELETE /api/v1/rutinas/{id}/ejercicios/{ejId} — eliminar un ejercicio */
    @DeleteMapping("/{id}/ejercicios/{ejId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Void> eliminarEjercicio(
            @PathVariable Long id,
            @PathVariable Long ejId,
            @RequestHeader(value = "X-Socio-Id", required = false) Long socioId) {
        try {
            rutinaService.eliminarEjercicio(ejId, id, socioId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
