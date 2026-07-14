package com.example.gymtrack_backend.web;

import com.example.gymtrack_backend.dto.AsistenciaCheckinDTO;
import com.example.gymtrack_backend.dto.AsistenciaResponseDTO;
import com.example.gymtrack_backend.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService service;

    /** Registrar ingreso/salida de un socio (validando membresía) */
    @PostMapping("/checkin")
    public ResponseEntity<AsistenciaResponseDTO> checkin(@RequestBody AsistenciaCheckinDTO dto) {
        return ResponseEntity.ok(service.registrarCheckin(dto));
    }

    /** Asistencias del día de hoy */
    @GetMapping("/hoy")
    public ResponseEntity<List<AsistenciaResponseDTO>> hoy() {
        return ResponseEntity.ok(service.listarHoy());
    }

    /** Historial de asistencias de un socio */
    @GetMapping("/socio/{socioId}")
    public ResponseEntity<List<AsistenciaResponseDTO>> porSocio(@PathVariable Long socioId) {
        return ResponseEntity.ok(service.listarPorSocio(socioId));
    }

    /** Rango de fechas */
    @GetMapping
    public ResponseEntity<List<AsistenciaResponseDTO>> porRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(service.listarPorRango(desde, hasta));
    }
}
