package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.DisciplinaRequestDTO;
import com.example.gymtrack_backend.entities.Disciplina;
import com.example.gymtrack_backend.repository.DisciplinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository repo;

    public List<Disciplina> listarActivas() { return repo.findByActivaTrue(); }
    public List<Disciplina> listarTodas()   { return repo.findAll(); }

    public Disciplina obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Disciplina no encontrada: " + id));
    }

    public Disciplina crear(DisciplinaRequestDTO dto) {
        Disciplina d = new Disciplina();
        mapear(dto, d);
        return repo.save(d);
    }

    public Disciplina actualizar(Long id, DisciplinaRequestDTO dto) {
        Disciplina d = obtener(id);
        mapear(dto, d);
        return repo.save(d);
    }

    public void desactivar(Long id) {
        Disciplina d = obtener(id);
        d.setActiva(false);
        repo.save(d);
    }

    private void mapear(DisciplinaRequestDTO dto, Disciplina d) {
        d.setNombre(dto.getNombre());
        d.setDescripcion(dto.getDescripcion());
        d.setInstructor(dto.getInstructor());
        d.setHorario(dto.getHorario());
        d.setCupoMaximo(dto.getCupoMaximo());
        if (dto.getActiva() != null) d.setActiva(dto.getActiva());
    }
}
