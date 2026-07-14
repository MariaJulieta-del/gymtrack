package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.RutinaRequestDTO;
import com.example.gymtrack_backend.dto.RutinaResponseDTO;
import com.example.gymtrack_backend.service.RutinaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller REST para el módulo de Rutinas.
 * Todos los endpoints requieren el header X-Socio-Id para identificar al propietario.
 */
@RestController
@RequestMapping("/api/v1/rutinas")
@CrossOrigin(origins = "*")
public class RutinaController {

    @Autowired
    private RutinaService rutinaService;

    // ──────────────────────────────────────────────
    // GET /api/v1/rutinas
    // Obtener todas las rutinas del socio
    // ──────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<RutinaResponseDTO>> obtenerRutinas(
            @RequestHeader("X-Socio-Id") Long socioId) {
        try {
            List<RutinaResponseDTO> rutinas = rutinaService.obtenerRutinas(socioId);
            return ResponseEntity.ok(rutinas);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ──────────────────────────────────────────────
    // GET /api/v1/rutinas/{id}
    // Obtener una rutina específica
    // ──────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<RutinaResponseDTO> obtenerRutina(
            @PathVariable Long id,
            @RequestHeader("X-Socio-Id") Long socioId) {
        try {
            RutinaResponseDTO rutina = rutinaService.obtenerRutina(id, socioId);
            return ResponseEntity.ok(rutina);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ──────────────────────────────────────────────
    // POST /api/v1/rutinas
    // Crear una nueva rutina
    // ──────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<RutinaResponseDTO> crearRutina(
            @RequestHeader("X-Socio-Id") Long socioId,
            @Valid @RequestBody RutinaRequestDTO requestDTO) {
        try {
            RutinaResponseDTO nueva = rutinaService.crearRutina(socioId, requestDTO);
            return ResponseEntity.ok(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ──────────────────────────────────────────────
    // PUT /api/v1/rutinas/{id}
    // Actualizar una rutina existente
    // ──────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<RutinaResponseDTO> actualizarRutina(
            @PathVariable Long id,
            @RequestHeader("X-Socio-Id") Long socioId,
            @Valid @RequestBody RutinaRequestDTO requestDTO) {
        try {
            RutinaResponseDTO actualizada = rutinaService.actualizarRutina(id, socioId, requestDTO);
            return ResponseEntity.ok(actualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ──────────────────────────────────────────────
    // DELETE /api/v1/rutinas/{id}
    // Eliminar una rutina
    // ──────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRutina(
            @PathVariable Long id,
            @RequestHeader("X-Socio-Id") Long socioId) {
        try {
            rutinaService.eliminarRutina(id, socioId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
