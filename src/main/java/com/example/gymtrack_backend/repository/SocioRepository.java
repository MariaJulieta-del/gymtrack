package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {
    List<Socio> findByActivoTrue();
    boolean existsByDni(String dni);
}