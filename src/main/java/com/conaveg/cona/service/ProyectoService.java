package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.ProyectoDTO;
import com.conaveg.cona.model.Proyecto;
import com.conaveg.cona.repository.ProyectoRepository;

@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<ProyectoDTO> getAllProyectos() {
        return proyectoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProyectoDTO getProyectoById(Long id) {
        return proyectoRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public ProyectoDTO saveProyecto(ProyectoDTO proyectoDTO) {
        Proyecto proyecto = convertToEntity(proyectoDTO);
        Proyecto saved = proyectoRepository.save(proyecto);
        return convertToDTO(saved);
    }

    public ProyectoDTO updateProyecto(Long id, ProyectoDTO proyectoDTO) {
        if (proyectoRepository.existsById(id)) {
            Proyecto proyecto = convertToEntity(proyectoDTO);
            proyecto.setId(id);
            Proyecto updated = proyectoRepository.save(proyecto);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteProyecto(Long id) {
        proyectoRepository.deleteById(id);
    }

    private ProyectoDTO convertToDTO(Proyecto proyecto) {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setId(proyecto.getId());
        dto.setNombre(proyecto.getNombre());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setUbicacion(proyecto.getUbicacion());
        dto.setFechaInicio(proyecto.getFechaInicio());
        dto.setFechaFin(proyecto.getFechaFin());
        dto.setEstadoProyecto(proyecto.getEstadoProyecto());
        return dto;
    }

    private Proyecto convertToEntity(ProyectoDTO dto) {
        Proyecto proyecto = new Proyecto();
        proyecto.setId(dto.getId());
        proyecto.setNombre(dto.getNombre());
        proyecto.setDescripcion(dto.getDescripcion());
        proyecto.setUbicacion(dto.getUbicacion());
        proyecto.setFechaInicio(dto.getFechaInicio());
        proyecto.setFechaFin(dto.getFechaFin());
        proyecto.setEstadoProyecto(dto.getEstadoProyecto());
        return proyecto;
    }
}
