package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.service.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/socios")
@CrossOrigin(origins = "*")
public class SocioController {

    @Autowired
    private SocioService socioService;

    // GET - Obtener todos los socios
    @GetMapping
    public List<Socio> obtenerTodos() {
        return socioService.obtenerTodos();
    }

    // GET - Obtener socio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Socio> obtenerPorId(@PathVariable Long id) {
        return socioService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear nuevo socio
    @PostMapping
    public ResponseEntity<Socio> crear(@RequestBody Socio socio) {
        try {
            return ResponseEntity.ok(socioService.crear(socio));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT - Actualizar socio
    @PutMapping("/{id}")
    public ResponseEntity<Socio> actualizar(@PathVariable Long id, 
                                             @RequestBody Socio socio) {
        try {
            return ResponseEntity.ok(socioService.actualizar(id, socio));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE - Baja lógica
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Long id) {
        try {
            socioService.darDeBaja(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}