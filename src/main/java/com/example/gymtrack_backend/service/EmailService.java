package com.example.gymtrack_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de envío de email vía Resend SMTP.
 *
 * Configuración en application.properties:
 *   spring.mail.host=smtp.resend.com
 *   spring.mail.port=587
 *   spring.mail.username=resend
 *   spring.mail.password=<RESEND_API_KEY>
 *   mail.from=GymTrack <notificaciones@tudominio.com>
 *   mail.enabled=true
 *
 * Si mail.enabled=false, los métodos no hacen nada (útil para tests locales).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.enabled:false}")
    private boolean enabled;

    /**
     * Envía un email a un destinatario único.
     */
    public void enviar(String para, String asunto, String cuerpo) {
        if (!enabled) {
            log.info("[EmailService] Email deshabilitado. No se envía a: {}", para);
            return;
        }
        if (para == null || para.isBlank()) {
            log.warn("[EmailService] Email de destino vacío — se omite envío");
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(para);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            mailSender.send(msg);
            log.info("[EmailService] Email enviado a: {}", para);
        } catch (MailException e) {
            log.error("[EmailService] Error al enviar a {}: {}", para, e.getMessage());
        }
    }

    /**
     * Envía el mismo email a múltiples destinatarios (uno por uno, no en CC).
     */
    public void enviarMasivo(List<String> destinatarios, String asunto, String cuerpo) {
        if (!enabled) {
            log.info("[EmailService] Email deshabilitado. Se omiten {} envíos.", destinatarios.size());
            return;
        }
        int enviados = 0;
        for (String email : destinatarios) {
            enviar(email, asunto, cuerpo);
            enviados++;
        }
        log.info("[EmailService] Envío masivo completado: {}/{} emails procesados.", enviados, destinatarios.size());
    }
}
