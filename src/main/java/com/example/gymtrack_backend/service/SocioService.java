package com.example.gymtrack_backend.service;

import com.example.gymtrack_backend.entities.Socio;
import com.example.gymtrack_backend.repository.SocioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SocioService {

    @Autowired
    private SocioRepository socioRepository;

    // Obtener todos los socios activos
    public List<Socio> obtenerTodos() {
        return socioRepository.findByActivoTrue();
    }

    // Buscar socio por ID
    public Optional<Socio> obtenerPorId(Long id) {
        return socioRepository.findById(id);
    }

    // Crear nuevo socio
    public Socio crear(Socio socio) {
        if (socioRepository.existsByDni(socio.getDni())) {
            throw new RuntimeException("Ya existe un socio con ese DNI");
        }
        socio.setActivo(true);
        return socioRepository.save(socio);
    }

    // Actualizar socio
    public Socio actualizar(Long id, Socio socioActualizado) {
        Socio socio = socioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
        socio.setNombre(socioActualizado.getNombre());
        socio.setApellido(socioActualizado.getApellido());
        socio.setTelefono(socioActualizado.getTelefono());
        socio.setEmail(socioActualizado.getEmail());
        socio.setFechaNacimiento(socioActualizado.getFechaNacimiento());
        socio.setFechaVencimiento(socioActualizado.getFechaVencimiento());
        socio.setEstado(socioActualizado.getEstado());
        return socioRepository.save(socio);
    }

    // Baja lógica (no borra, marca como inactivo)
    public void darDeBaja(Long id) {
        Socio socio = socioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Socio no encontrado"));
        socio.setActivo(false);
        socioRepository.save(socio);
    }
}