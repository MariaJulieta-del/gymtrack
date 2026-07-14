package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.service.ExerciseFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Proxy hacia la API externa wger.de.
 * El frontend llama a estos endpoints para obtener sugerencias
 * de ejercicios al armar una rutina, sin exponer la API externa.
 *
 * GET /api/v1/ejercicios-externos/categorias
 * GET /api/v1/ejercicios-externos?categoria=8
 */
@RestController
@RequestMapping("/api/v1/ejercicios-externos")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseFetchService exerciseFetchService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarEjercicios(
            @RequestParam(required = false) Integer categoria) {
        return ResponseEntity.ok(exerciseFetchService.getExercisesByCategory(categoria));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<Map<String, Object>>> listarCategorias() {
        return ResponseEntity.ok(exerciseFetchService.getCategories());
    }
}
