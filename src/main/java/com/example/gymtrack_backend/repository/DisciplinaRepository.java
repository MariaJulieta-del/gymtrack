package com.example.gymtrack_backend.repository;

import com.example.gymtrack_backend.entities.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    List<Disciplina> findByActivaTrue();
}
