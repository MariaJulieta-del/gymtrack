package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.AsistenciaCheckinDTO;
import com.example.gymtrack_backend.dto.AsistenciaResponseDTO;
import com.example.gymtrack_backend.entities.Asistencia;
import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
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
     *
     * Reglas de acceso:
     *  1. Membresía ACTIVA             → acceso permitido, sin observación.
     *  2. Membresía PENDIENTE_PAGO     →
     *       - 0 ingresos con deuda previos: pasa con warning "ÚLTIMO ingreso permitido".
     *       - 1+ ingresos con deuda previos: BLOQUEADO hasta saldar la deuda.
     *  3. Sin membresía válida         → acceso denegado.
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

        String tipo = (dto.getTipo() != null) ? dto.getTipo().toUpperCase() : "ENTRADA";

        // 2. Verificar membresía ACTIVA
        List<Membresia> activas = membresiaRepo
                .findBySocioIdAndEstadoMembresia(socio.getId(), EstadoMembresia.ACTIVA);

        boolean tieneAcceso;
        boolean conDeuda = false;
        String  observacion = null;

        // Campos para info de membresía en la respuesta
        java.time.LocalDate fechaVencimiento  = null;
        String              tipoMembresia     = null;
        Integer             entradasDisponibles = null;
        Integer             entradasUsadas    = null;
        Integer             entradasRestantes = null;

        if (!activas.isEmpty()) {
            // ── Caso 1: tiene membresía activa ──
            Membresia activa = activas.get(0);
            fechaVencimiento    = activa.getFechaVencimiento();
            tipoMembresia       = activa.getTipoMembresia();
            entradasDisponibles = activa.getEntradasDisponibles();

            if (entradasDisponibles != null) {
                // Plan por entradas (punch-card)
                long usadas = asistenciaRepo.countEntradasEnPeriodo(
                        socio.getId(), activa.getFechaInicio(), activa.getFechaVencimiento());
                entradasUsadas = (int) usadas;
                int restantes  = entradasDisponibles - entradasUsadas;
                entradasRestantes = Math.max(0, restantes);

                if (restantes <= 0) {
                    tieneAcceso = false;
                    observacion = "Acceso denegado: agotaste las " + entradasDisponibles + " entradas del pase. Renovar membresía.";
                } else {
                    tieneAcceso = true;
                    if (restantes == 1) {
                        observacion = "⚠️ Última entrada del pase. Quedan 0 luego de este ingreso.";
                    } else if (restantes <= 3) {
                        observacion = "⚠️ Quedan solo " + (restantes - 1) + " entrada" + ((restantes - 1) != 1 ? "s" : "") + " después de este ingreso.";
                    }
                }
            } else {
                // Plan por tiempo (comportamiento estándar)
                tieneAcceso = true;
            }

        } else {
            // 3. Verificar membresía PENDIENTE_PAGO (fiado)
            List<Membresia> pendientes = membresiaRepo
                    .findBySocioIdAndEstadoMembresia(socio.getId(), EstadoMembresia.PENDIENTE_PAGO);

            if (!pendientes.isEmpty()) {
                Membresia pendiente = pendientes.get(0);
                fechaVencimiento = pendiente.getFechaVencimiento();
                tipoMembresia    = pendiente.getTipoMembresia();
                // ── Membresía PENDIENTE_PAGO → acceso bloqueado inmediatamente ──
                tieneAcceso = false;
                observacion = "Acceso denegado: membresía pendiente de pago ($"
                            + pendiente.getPrecio().toPlainString()
                            + "). Debe abonar para ingresar.";
            } else {
                // ── Sin membresía válida ──
                tieneAcceso = false;
                observacion = "Acceso denegado: no tiene membresía activa";
            }
        }

        // 4. Persistir la asistencia
        Asistencia asistencia = new Asistencia();
        asistencia.setSocio(socio);
        asistencia.setFecha(LocalDate.now());
        asistencia.setHora(LocalTime.now());
        asistencia.setTipo(tipo);
        asistencia.setPermitido(tieneAcceso);
        asistencia.setConDeuda(conDeuda);
        asistencia.setObservacion(observacion);
        asistenciaRepo.save(asistencia);

        // Si es un pase por entradas y se permitió el acceso, actualizamos el contador
        // (la nueva asistencia ya fue guardada, así que entradasUsadas aumenta en 1)
        if (entradasDisponibles != null && tieneAcceso && entradasUsadas != null) {
            entradasUsadas    += 1;
            entradasRestantes  = Math.max(0, entradasDisponibles - entradasUsadas);
        }

        return mapearConMembresia(asistencia, fechaVencimiento, tipoMembresia,
                                  entradasDisponibles, entradasUsadas, entradasRestantes);
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
        return mapearConMembresia(a, null, null, null, null, null);
    }

    private AsistenciaResponseDTO mapearConMembresia(
            Asistencia a,
            java.time.LocalDate fechaVencimiento,
            String tipoMembresia,
            Integer entradasDisponibles,
            Integer entradasUsadas,
            Integer entradasRestantes) {
        return new AsistenciaResponseDTO(
                a.getId(),
                a.getSocio().getId(),
                a.getSocio().getNombre() + " " + a.getSocio().getApellido(),
                a.getSocio().getDni(),
                a.getFecha(),
                a.getHora(),
                a.getTipo(),
                a.getPermitido(),
                a.getObservacion(),
                a.getConDeuda(),
                fechaVencimiento,
                tipoMembresia,
                entradasDisponibles,
                entradasUsadas,
                entradasRestantes
        );
    }
}
