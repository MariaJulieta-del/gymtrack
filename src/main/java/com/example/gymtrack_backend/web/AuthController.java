package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.LoginRequestDTO;
import com.example.gymtrack_backend.dto.LoginResponseDTO;
import com.example.gymtrack_backend.dto.RegisterRequestDTO;
import com.example.gymtrack_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de autenticación — todos públicos excepto /register.
 *
 * POST /api/v1/auth/login    → devuelve JWT + info del usuario
 * POST /api/v1/auth/register → crea usuario (solo ADMIN)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ── Login ────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    // ── Registro (solo ADMIN) ────────────────────────────────────────────────
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Usuario '" + dto.getUsername() + "' creado correctamente"));
    }
}
