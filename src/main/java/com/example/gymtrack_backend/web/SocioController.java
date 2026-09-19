package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.DisciplinaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import com.example.gymtrack_backend.service.SocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/socios")
@CrossOrigin(origins = "*")
public class SocioController {

    @Autowired
    private SocioService socioService;

    @Autowired
    private SocioRepository socioRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Socio> crear(@RequestBody Socio socio) {
        try {
            return ResponseEntity.ok(socioService.crear(socio));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT- update - Actualizar socio
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Socio> reactivar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(socioService.reactivar(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // PATCH - Asignar disciplina a un socio (disciplinaId=null para quitar)
    @PatchMapping("/{id}/disciplina")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<?> asignarDisciplina(
            @PathVariable Long id,
            @RequestParam(required = false) Long disciplinaId) {
        return socioRepository.findById(id).map(socio -> {
            if (disciplinaId == null) {
                socio.setDisciplina(null);
                return ResponseEntity.ok(socioRepository.save(socio));
            }
            var disciplina = disciplinaRepository.findById(disciplinaId)
                    .orElse(null);
            if (disciplina == null) return ResponseEntity.notFound().build();

            // Verificar cupo disponible (si ya está en esa disciplina, no cuenta doble)
            boolean yaAsignado = disciplinaId.equals(
                    socio.getDisciplina() != null ? socio.getDisciplina().getId() : null);
            if (!yaAsignado && disciplina.getCupoMaximo() != null) {
                long inscritos = socioRepository.countByDisciplinaIdAndActivoTrue(disciplinaId);
                if (inscritos >= disciplina.getCupoMaximo()) {
                    return ResponseEntity.badRequest()
                            .body(java.util.Map.of("error",
                                    "La disciplina está llena (" + inscritos + "/" + disciplina.getCupoMaximo() + " cupos)"));
                }
            }
            socio.setDisciplina(disciplina);
            return ResponseEntity.ok(socioRepository.save(socio));
        }).orElse(ResponseEntity.notFound().build());
    }

    // GET - Info de cupos de todas las disciplinas activas
    @GetMapping("/disciplinas-cupo")
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> disciplinasCupo() {
        var disciplinas = disciplinaRepository.findAll().stream()
                .filter(d -> Boolean.TRUE.equals(d.getActiva()))
                .map(d -> {
                    long inscritos = socioRepository.countByDisciplinaIdAndActivoTrue(d.getId());
                    java.util.Map<String, Object> item = new java.util.LinkedHashMap<>();
                    item.put("id", d.getId());
                    item.put("nombre", d.getNombre());
                    item.put("instructor", d.getInstructor());
                    item.put("horario", d.getHorario());
                    item.put("cupoMaximo", d.getCupoMaximo());
                    item.put("inscriptos", inscritos);
                    item.put("disponibles", d.getCupoMaximo() != null
                            ? Math.max(0, d.getCupoMaximo() - inscritos) : null);
                    item.put("llena", d.getCupoMaximo() != null && inscritos >= d.getCupoMaximo());
                    return item;
                })
                .toList();
        return ResponseEntity.ok(disciplinas);
    }
}