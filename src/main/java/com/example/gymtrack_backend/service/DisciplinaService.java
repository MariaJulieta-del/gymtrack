package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.dto.DisciplinaRequestDTO;
import com.example.gymtrack_backend.dto.DisciplinaResponseDTO;
import com.example.gymtrack_backend.entities.Disciplina;
import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.DisciplinaRepository;
import com.example.gymtrack_backend.repository.SocioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository repo;
    private final SocioRepository socioRepo;

    public List<DisciplinaResponseDTO> listarActivas() {
        return repo.findByActivaTrue().stream().map(this::toDTO).toList();
    }
    public List<DisciplinaResponseDTO> listarTodas() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    private DisciplinaResponseDTO toDTO(Disciplina d) {
        List<Socio> inscritos = socioRepo.findByDisciplinaId(d.getId());
        DisciplinaResponseDTO dto = new DisciplinaResponseDTO();
        dto.setId(d.getId());
        dto.setNombre(d.getNombre());
        dto.setDescripcion(d.getDescripcion());
        dto.setInstructor(d.getInstructor());
        dto.setHorario(d.getHorario());
        dto.setCupoMaximo(d.getCupoMaximo());
        dto.setActiva(d.getActiva());
        dto.setDiasSemana(d.getDiasSemana());
        dto.setHoraInicio(d.getHoraInicio());
        dto.setHoraFin(d.getHoraFin());
        dto.setTotalInscritos(inscritos.size());
        dto.setCuposDisponibles(d.getCupoMaximo() != null
                ? Math.max(0, d.getCupoMaximo() - inscritos.size()) : null);
        dto.setSociosInscritos(inscritos.stream()
                .map(s -> s.getNombre() + " " + s.getApellido()).toList());
        return dto;
    }

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

    /** Renueva el mes: desvincula todos los socios inscriptos para liberar cupos */
    @Transactional
    public void renovarMes(Long disciplinaId) {
        obtener(disciplinaId); // valida que existe
        List<Socio> inscritos = socioRepo.findByDisciplinaId(disciplinaId);
        for (Socio s : inscritos) {
            s.setDisciplina(null);
            socioRepo.save(s);
        }
    }

    private void mapear(DisciplinaRequestDTO dto, Disciplina d) {
        d.setNombre(dto.getNombre());
        d.setDescripcion(dto.getDescripcion());
        d.setInstructor(dto.getInstructor());
        d.setHorario(dto.getHorario());
        d.setCupoMaximo(dto.getCupoMaximo());
        if (dto.getActiva() != null) d.setActiva(dto.getActiva());
        if (dto.getDiasSemana() != null) {
            d.getDiasSemana().clear();
            d.getDiasSemana().addAll(dto.getDiasSemana());
        }
        if (dto.getHoraInicio() != null) d.setHoraInicio(dto.getHoraInicio());
        if (dto.getHoraFin()    != null) d.setHoraFin(dto.getHoraFin());
    }
}
