package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.TarifaMembresiaResponseDTO;
import com.example.gymtrack_backend.entities.TarifaMembresia;
import com.example.gymtrack_backend.service.TarifaMembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST Controller para el tarifario / planes de membresía.
 * GET  /api/v1/tarifas          → lista todos los planes con socios activos
 * POST /api/v1/tarifas          → crear nuevo plan
 * PUT  /api/v1/tarifas/{tipo}   → actualizar precio de un plan
 */
@RestController
@RequestMapping("/api/v1/tarifas")
@CrossOrigin(origins = "*")
public class TarifaMembresiaController {

    @Autowired
    private TarifaMembresiaService tarifaService;

    @GetMapping
    public ResponseEntity<List<TarifaMembresiaResponseDTO>> listar() {
        return ResponseEntity.ok(tarifaService.listarTodas());
    }

    @PostMapping
    public ResponseEntity<TarifaMembresiaResponseDTO> crear(@RequestBody Map<String, Object> body) {
        try {
            TarifaMembresia plan = new TarifaMembresia();
            plan.setTipo(body.get("tipo").toString());
            plan.setNombre(body.getOrDefault("nombre", body.get("tipo")).toString());
            plan.setPrecio(new BigDecimal(body.get("precio").toString()));
            plan.setDuracionDias(Integer.parseInt(body.get("duracionDias").toString()));
            if (body.containsKey("incluye")) plan.setIncluye(body.get("incluye").toString());
            if (body.containsKey("descripcion")) plan.setDescripcion(body.get("descripcion").toString());
            return ResponseEntity.ok(tarifaService.crearPlan(plan));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{tipo}")
    public ResponseEntity<TarifaMembresiaResponseDTO> actualizarPrecio(
            @PathVariable String tipo,
            @RequestBody Map<String, Object> body) {
        try {
            BigDecimal precio = new BigDecimal(body.get("precio").toString());
            return ResponseEntity.ok(tarifaService.actualizarPrecio(tipo.toUpperCase(), precio));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
