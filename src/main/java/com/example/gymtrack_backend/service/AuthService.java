package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.LoginRequestDTO;
import com.example.gymtrack_backend.dto.LoginResponseDTO;
import com.example.gymtrack_backend.dto.RegisterRequestDTO;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.entities.Usuario;
import com.example.gymtrack_backend.entities.enums.Rol;
import com.example.gymtrack_backend.repository.SocioRepository;
import com.example.gymtrack_backend.repository.UsuarioRepository;
import com.example.gymtrack_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación:
 *  - login: valida credenciales y devuelve JWT
 *  - register: crea un nuevo usuario (solo ADMIN puede hacerlo vía controller)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository   usuarioRepository;
    private final SocioRepository     socioRepository;
    private final PasswordEncoder     passwordEncoder;
    private final JwtUtil             jwtUtil;
    private final AuthenticationManager authManager;

    // ── Login ────────────────────────────────────────────────────────────────
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Spring Security valida username + password con BCrypt
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = jwtUtil.generateToken(usuario, usuario.getRol().name());

        Long socioId = (usuario.getSocio() != null) ? usuario.getSocio().getId() : null;

        return new LoginResponseDTO(token, usuario.getUsername(), usuario.getRol().name(), socioId);
    }

    // ── Registro ─────────────────────────────────────────────────────────────
    public void register(RegisterRequestDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("El username '" + dto.getUsername() + "' ya está en uso");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        // Encriptar contraseña con BCrypt
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setEmail(dto.getEmail());

        // Asignar rol (por defecto EMPLEADO)
        Rol rol = (dto.getRol() != null) ? Rol.valueOf(dto.getRol().toUpperCase()) : Rol.EMPLEADO;
        usuario.setRol(rol);

        // Vincular a socio si corresponde
        if (dto.getSocioId() != null) {
            Socio socio = socioRepository.findById(dto.getSocioId())
                    .orElseThrow(() -> new RuntimeException("Socio no encontrado: " + dto.getSocioId()));
            usuario.setSocio(socio);
        }

        usuarioRepository.save(usuario);
    }
}
