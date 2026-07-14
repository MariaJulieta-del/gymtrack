package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.AsistenciaCheckinDTO;
import com.example.gymtrack_backend.dto.AsistenciaResponseDTO;
import com.example.gymtrack_backend.entities.Asistencia;
import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.AsistenciaRepository;
import com.example.gymtrack_backend.repository.MembresiaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepo;
    private final SocioRepository      socioRepo;
    private final MembresiaRepository  membresiaRepo;

    /**
     * Registra el ingreso (o salida) de un socio.
     * Valida que tenga membresía ACTIVA; si no, registra el acceso como denegado.
     */
    public AsistenciaResponseDTO registrarCheckin(AsistenciaCheckinDTO dto) {
        // 1. Buscar el socio
        Socio socio;
        if (dto.getDni() != null && !dto.getDni().isBlank()) {
            socio = socioRepo.findByDni(dto.getDni().trim())
                    .orElseThrow(() -> new RuntimeException("No existe un socio con DNI: " + dto.getDni()));
        } else if (dto.getSocioId() != null) {
            socio = socioRepo.findById(dto.getSocioId())
                    .orElseThrow(() -> new RuntimeException("Socio no encontrado: " + dto.getSocioId()));
        } else {
            throw new IllegalArgumentException("Debe ingresar el DNI o el ID del socio");
        }

        // 2. Verificar membresía activa
        List<Membresia> membresiasActivas = membresiaRepo
                .findBySocioIdAndEstadoMembresia(socio.getId(), com.example.gymtrack_backend.entities.enums.EstadoMembresia.ACTIVA);

        boolean tieneAcceso = !membresiasActivas.isEmpty();
        String tipo = (dto.getTipo() != null) ? dto.getTipo().toUpperCase() : "ENTRADA";

        // 3. Registrar asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setSocio(socio);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHora(LocalTime.now());
        asistencia.setTipo(tipo);
        asistencia.setPermitido(tieneAcceso);
        asistencia.setObservacion(tieneAcceso ? null : "Acceso denegado: membresía sin estado ACTIVA");
        asistenciaRepo.save(asistencia);

        return mapear(asistencia);
    }

    public List<AsistenciaResponseDTO> listarHoy() {
        return asistenciaRepo.findByFechaOrderByHoraDesc(LocalDate.now())
                .stream().map(this::mapear).toList();
    }

    public List<AsistenciaResponseDTO> listarPorSocio(Long socioId) {
        return asistenciaRepo.findBySocioIdOrderByFechaDescHoraDesc(socioId)
                .stream().map(this::mapear).toList();
    }

    public List<AsistenciaResponseDTO> listarPorRango(LocalDate desde, LocalDate hasta) {
        return asistenciaRepo.findByFechaBetweenOrderByFechaDescHoraDesc(desde, hasta)
                .stream().map(this::mapear).toList();
    }

    public Long countHoy() {
        return asistenciaRepo.countSociosHoy(LocalDate.now());
    }

    private AsistenciaResponseDTO mapear(Asistencia a) {
        return new AsistenciaResponseDTO(
                a.getId(),
                a.getSocio().getId(),
                a.getSocio().getNombre() + " " + a.getSocio().getApellido(),
                a.getSocio().getDni(),
                a.getFecha(),
                a.getHora(),
                a.getTipo(),
                a.getPermitido(),
                a.getObservacion()
        );
    }
}
