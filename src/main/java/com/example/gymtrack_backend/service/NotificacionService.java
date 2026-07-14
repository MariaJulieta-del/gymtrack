package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.NotificacionRequestDTO;
import com.example.gymtrack_backend.entities.Notificacion;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.NotificacionRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository repo;
    private final SocioRepository        socioRepo;

    public List<Notificacion> listar() {
        return repo.findByActivaTrueOrderByFechaCreacionDesc();
    }

    public Notificacion crear(NotificacionRequestDTO dto) {
        Notificacion n = new Notificacion();
        n.setTitulo(dto.getTitulo());
        n.setMensaje(dto.getMensaje());
        n.setTipo(dto.getTipo() != null ? dto.getTipo() : "AVISO");
        n.setDestinatario(dto.getDestinatario() != null ? dto.getDestinatario() : "TODOS");
        if (dto.getSocioId() != null) {
            Socio socio = socioRepo.findById(dto.getSocioId())
                    .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
            n.setSocio(socio);
        }
        return repo.save(n);
    }

    public void archivar(Long id) {
        Notificacion n = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        n.setActiva(false);
        repo.save(n);
    }
}
