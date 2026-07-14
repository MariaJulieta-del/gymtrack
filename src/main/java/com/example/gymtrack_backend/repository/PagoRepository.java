package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /** Historial de pagos de una membresía, ordenado por más reciente */
    List<Pago> findByMembresiaIdOrderByFechaPagoDesc(Long membresiaId);

    /** Todos los pagos de un socio (a través de sus membresías) */
    @Query("""
        SELECT p FROM Pago p
        WHERE p.membresia.socio.id = :socioId
        ORDER BY p.fechaPago DESC
        """)
    List<Pago> findBySocioId(@Param("socioId") Long socioId);

    /** Suma total de pagos COMPLETADOS de una membresía */
    @Query("""
        SELECT COALESCE(SUM(p.monto), 0)
        FROM Pago p
        WHERE p.membresia.id = :membresiaId
          AND p.estadoPago = 'COMPLETADO'
        """)
    BigDecimal sumMontoCompletoByMembresiaId(@Param("membresiaId") Long membresiaId);
}
