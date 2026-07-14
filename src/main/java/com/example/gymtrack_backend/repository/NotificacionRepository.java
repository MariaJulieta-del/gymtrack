package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByActivaTrueOrderByFechaCreacionDesc();
    List<Notificacion> findBySocioIdAndActivaTrueOrderByFechaCreacionDesc(Long socioId);
}
