package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.MantenimientoRequestDTO;
import com.example.gymtrack_backend.dto.MantenimientoResponseDTO;
import com.example.gymtrack_backend.service.MantenimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/mantenimientos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    /** GET /api/v1/mantenimientos — todos los mantenimientos */
    @GetMapping
    public List<MantenimientoResponseDTO> listarTodos() {
        return mantenimientoService.listarTodos();
    }

    /** GET /api/v1/mantenimientos/maquina/{maquinaId} — por máquina */
    @GetMapping("/maquina/{maquinaId}")
    public List<MantenimientoResponseDTO> listarPorMaquina(@PathVariable Long maquinaId) {
        return mantenimientoService.listarPorMaquina(maquinaId);
    }

    /** POST /api/v1/mantenimientos — crear */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> crear(@RequestBody MantenimientoRequestDTO dto) {
        try {
            return ResponseEntity.ok(mantenimientoService.crear(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** PUT /api/v1/mantenimientos/{id} — actualizar */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MantenimientoRequestDTO dto) {
        try {
            return ResponseEntity.ok(mantenimientoService.actualizar(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** DELETE /api/v1/mantenimientos/{id} — eliminar */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            mantenimientoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
