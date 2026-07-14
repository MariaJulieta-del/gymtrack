package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Membresia;
import com.example.gymtrack_backend.entities.enums.EstadoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    /** Todas las membresías de un socio */
    List<Membresia> findBySocioId(Long socioId);

    /** Membresías de un socio filtradas por estado */
    List<Membresia> findBySocioIdAndEstadoMembresia(Long socioId, EstadoMembresia estado);

    /** Todas las membresías de un estado específico (ej: listar todas las ACTIVAS) */
    List<Membresia> findByEstadoMembresia(EstadoMembresia estado);

    /**
     * Marca como VENCIDAS todas las membresías ACTIVAS cuya fechaVencimiento
     * ya pasó. Se puede llamar desde un @Scheduled diario.
     */
    @Modifying
    @Query("""
        UPDATE Membresia m
        SET m.estadoMembresia = 'VENCIDA'
        WHERE m.estadoMembresia = 'ACTIVA'
          AND m.fechaVencimiento < :hoy
        """)
    int vencerMembresiasExpiradas(@Param("hoy") LocalDate hoy);
}
