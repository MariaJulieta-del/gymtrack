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

    // GET - Obtener socios activos
    @GetMapping
    public List<Socio> obtenerTodos() {
        return socioService.obtenerTodos();
    }

    // GET - Obtener TODOS los socios (activos + inactivos)
    @GetMapping("/todos")
    public List<Socio> obtenerTodosConInactivos() {
        return socioService.obtenerTodosConInactivos();
    }

    // GET- read - Obtener socio por ID
    @GetMapping ("/{id}")
    public ResponseEntity<Socio> obtenerPorId(@PathVariable Long id) {
        return socioService.obtenerPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST- create - Crear nuevo socio
    @PostMapping
    public ResponseEntity<Socio> crear(@RequestBody Socio socio) {
        try {
            return ResponseEntity.ok(socioService.crear(socio));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT- update - Actualizar socio
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

    // DELETE - Eliminación física (solo socios inactivos)
    @DeleteMapping("/{id}/permanente")
    public ResponseEntity<Void> eliminarPermanente(@PathVariable Long id) {
        try {
            socioService.eliminarPermanente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT - Reactivar socio dado de baja
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Socio> reactivar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(socioService.reactivar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}