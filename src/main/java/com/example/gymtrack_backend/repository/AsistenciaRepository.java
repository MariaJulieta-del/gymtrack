package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findBySocioIdOrderByFechaDescHoraDesc(Long socioId);

    List<Asistencia> findByFechaOrderByHoraDesc(LocalDate fecha);

    List<Asistencia> findByFechaBetweenOrderByFechaDescHoraDesc(LocalDate desde, LocalDate hasta);

    @Query("SELECT COUNT(DISTINCT a.socio.id) FROM Asistencia a WHERE a.fecha = :fecha AND a.permitido = true")
    Long countSociosHoy(LocalDate fecha);

    boolean existsBySocioIdAndFechaAndTipo(Long socioId, LocalDate fecha, String tipo);

    /** Cuenta ingresos con deuda de un socio desde una fecha (para membresía PENDIENTE_PAGO activa) */
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.socio.id = :socioId AND a.conDeuda = true AND a.permitido = true AND a.tipo = 'ENTRADA' AND a.fecha >= :desde")
    long countIngresosConDeuda(@Param("socioId") Long socioId, @Param("desde") java.time.LocalDate desde);

    /**
     * Cuenta entradas PERMITIDAS de un socio dentro del período de una membresía.
     * Se usa para punch-cards (planes por cantidad de ingresos).
     */
    @Query("SELECT COUNT(a) FROM Asistencia a WHERE a.socio.id = :socioId AND a.tipo = 'ENTRADA' AND a.permitido = true AND a.fecha BETWEEN :desde AND :hasta")
    long countEntradasEnPeriodo(@Param("socioId") Long socioId, @Param("desde") java.time.LocalDate desde, @Param("hasta") java.time.LocalDate hasta);
}
