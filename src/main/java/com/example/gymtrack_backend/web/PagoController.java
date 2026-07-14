package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.PagoRequestDTO;
import com.example.gymtrack_backend.dto.PagoResponseDTO;
import com.example.gymtrack_backend.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller para el módulo de Pagos.
 */
@RestController
@RequestMapping("/api/v1/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // POST /api/v1/pagos → registrar pago
    @PostMapping
    public ResponseEntity<PagoResponseDTO> registrar(
            @Valid @RequestBody PagoRequestDTO dto) {
        try {
            return ResponseEntity.ok(pagoService.registrarPago(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/v1/pagos → listar todos
    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> listar() {
        return ResponseEntity.ok(pagoService.listarTodos());
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
