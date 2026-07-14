package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findBySocioIdOrderByFechaDescHoraDesc(Long socioId);

    List<Asistencia> findByFechaOrderByHoraDesc(LocalDate fecha);

    List<Asistencia> findByFechaBetweenOrderByFechaDescHoraDesc(LocalDate desde, LocalDate hasta);

    @Query("SELECT COUNT(DISTINCT a.socio.id) FROM Asistencia a WHERE a.fecha = :fecha AND a.permitido = true")
    Long countSociosHoy(LocalDate fecha);

    boolean existsBySocioIdAndFechaAndTipo(Long socioId, LocalDate fecha, String tipo);
}
