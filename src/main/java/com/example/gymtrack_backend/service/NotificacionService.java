package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.NotificacionRequestDTO;
import com.example.gymtrack_backend.dto.NotificacionResponseDTO;
import com.example.gymtrack_backend.entities.Notificacion;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.NotificacionRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repo;
    private final SocioRepository        socioRepo;
    private final EmailService           emailService;

    // ── Listar notificaciones activas ────────────────────────────────────────
    public List<NotificacionResponseDTO> listar() {
        return repo.findByActivaTrueOrderByFechaCreacionDesc()
                .stream()
                .map(n -> new NotificacionResponseDTO(
                        n.getId(),
                        n.getTitulo(),
                        n.getMensaje(),
                        n.getTipo(),
                        n.getDestinatario(),
                        n.getSocio() != null ? n.getSocio().getId() : null,
                        n.getFechaCreacion(),
                        n.getActiva()
                ))
                .toList();
    }

    // ── Crear notificación y enviar email según destinatario ─────────────────
    public Notificacion crear(NotificacionRequestDTO dto) {
        Notificacion n = new Notificacion();
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setTipo(dto.getTipo() != null ? dto.getTipo() : "AVISO");
        n.setDestinatario(dto.getDestinatario() != null ? dto.getDestinatario() : "TODOS");

        // Vincular socio si aplica
        if (dto.getSocioId() != null) {
            Socio socio = socioRepo.findById(dto.getSocioId())
                    .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
            n.setSocio(socio);
        }

        Notificacion guardada = repo.save(n);

        // Enviar email según destinatario
        enviarEmail(guardada);

        return guardada;
    }

    // ── Archivar notificación ────────────────────────────────────────────────
    public void archivar(Long id) {
        Notificacion n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        n.setActiva(false);
        repo.save(n);
    }

    // ── Lógica de envío de email ─────────────────────────────────────────────
    private void enviarEmail(Notificacion n) {
        String asunto = "[GymTrack] " + n.getTitulo();
        String cuerpo = construirCuerpo(n);

        switch (n.getDestinatario()) {
            case "TODOS" -> {
                // Enviar a todos los socios activos que tengan email
                List<String> emails = socioRepo.findByActivoTrue().stream()
                        .map(Socio::getEmail)
                        .filter(e -> e != null && !e.isBlank())
                        .toList();
                log.info("[NotificacionService] Enviando a {} socios activos", emails.size());
                emailService.enviarMasivo(emails, asunto, cuerpo);
            }
            case "SOCIO" -> {
                // Enviar solo al socio específico
                if (n.getSocio() != null && n.getSocio().getEmail() != null) {
                    emailService.enviar(n.getSocio().getEmail(), asunto, cuerpo);
                }
            }
            default -> log.info("[NotificacionService] Destinatario '{}' — sin envío de email", n.getDestinatario());
        }
    }

    private String construirCuerpo(Notificacion n) {
        return """
                Hola,

                Tenés una nueva notificación de GymTrack:

                %s

                ---
                Este mensaje fue enviado automáticamente por el sistema GymTrack.
                """.formatted(n.getMensaje());
    }
}
