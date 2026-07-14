package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.MembresiaRequestDTO;
import com.example.gymtrack_backend.dto.MembresiaResponseDTO;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.service.MembresiaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller para el módulo de Membresías.
 */
@RestController
@RequestMapping("/api/v1/membresias")
@CrossOrigin(origins = "*")
public class MembresiaController {

    @Autowired
    private MembresiaService membresiaService;

    // POST /api/v1/membresias → crear membresía
    @PostMapping
    public ResponseEntity<MembresiaResponseDTO> crear(
            @Valid @RequestBody MembresiaRequestDTO dto) {
        try {
            return ResponseEntity.ok(membresiaService.crearMembresia(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

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

    // PATCH /api/v1/membresias/{id}/estado → cancelar (o cambiar estado)
    @PatchMapping("/{id}/estado")
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
