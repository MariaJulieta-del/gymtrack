package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Maquina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {
    List<Maquina> findByActivaTrue();
    boolean existsByNroMaquina(String nroMaquina);
}
