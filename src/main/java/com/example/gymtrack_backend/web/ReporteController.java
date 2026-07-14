package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import com.example.gymtrack_backend.entities.enums.EstadoPago;
import com.example.gymtrack_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * Endpoints de reportes y estadísticas para el Dashboard.
 */
@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final SocioRepository      socioRepo;
    private final MembresiaRepository  membresiaRepo;
    private final PagoRepository       pagoRepo;
    private final AsistenciaRepository asistenciaRepo;

    /**
     * Estadísticas principales del Dashboard:
     *  - total socios activos
     *  - membresías activas
     *  - membresías vencidas
     *  - total recaudado este mes
     *  - asistentes hoy
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard() {
        LocalDate hoy      = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);

        long totalSocios      = socioRepo.countByActivoTrue();
        long membresiasActivas = membresiaRepo.findByEstadoMembresia(EstadoMembresia.ACTIVA).size();
        long membresiasVencidas = membresiaRepo.findByEstadoMembresia(EstadoMembresia.VENCIDA).size();
        long membresiasPendientes = membresiaRepo.findByEstadoMembresia(EstadoMembresia.PENDIENTE_PAGO).size();

        // Total recaudado este mes (pagos COMPLETADOS)
        double recaudadoMes = pagoRepo.findAll().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.COMPLETADO)
                .filter(p -> !p.getFechaPago().toLocalDate().isBefore(inicioMes))
                .mapToDouble(p -> p.getMonto().doubleValue())
                .sum();

        long asistentesHoy = asistenciaRepo.countSociosHoy(hoy);

        return ResponseEntity.ok(Map.of(
                "totalSociosActivos",    totalSocios,
                "membresiasActivas",     membresiasActivas,
                "membresiasVencidas",    membresiasVencidas,
                "membresiasPendientes",  membresiasPendientes,
                "totalRecaudadoMes",     recaudadoMes,
                "asistentesHoy",         asistentesHoy,
                "mesReporte",            hoy.getMonth().toString() + " " + hoy.getYear()
        ));
    }

    /**
     * Reporte de ingresos por mes: suma de pagos COMPLETADOS agrupados por mes.
     */
    @GetMapping("/ingresos")
    public ResponseEntity<Object> ingresos(
            @RequestParam(defaultValue = "6") int meses) {
        // Simplificado: retorna totales de los últimos N meses
        LocalDate desde = LocalDate.now().minusMonths(meses).withDayOfMonth(1);

        var pagos = pagoRepo.findAll().stream()
                .filter(p -> p.getEstadoPago() == EstadoPago.COMPLETADO)
                .filter(p -> !p.getFechaPago().toLocalDate().isBefore(desde))
                .toList();

        // Agrupar por mes
        var porMes = pagos.stream().collect(
                java.util.stream.Collectors.groupingBy(
                        p -> p.getFechaPago().getYear() + "-" + String.format("%02d", p.getFechaPago().getMonthValue()),
                        java.util.stream.Collectors.summingDouble(p -> p.getMonto().doubleValue())
                )
        );

        return ResponseEntity.ok(porMes);
    }

    /**
     * Reporte de asistencias por rango de fechas.
     */
    @GetMapping("/asistencias")
    public ResponseEntity<Object> asistencias(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        var asistencias = asistenciaRepo
                .findByFechaBetweenOrderByFechaDescHoraDesc(desde, hasta);

        // Agrupar por fecha
        var porFecha = asistencias.stream().collect(
                java.util.stream.Collectors.groupingBy(
                        a -> a.getFecha().toString(),
                        java.util.stream.Collectors.counting()
                )
        );

        return ResponseEntity.ok(Map.of(
                "total", asistencias.size(),
                "porFecha", porFecha
        ));
    }
}
