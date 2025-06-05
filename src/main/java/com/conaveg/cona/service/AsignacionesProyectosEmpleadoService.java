package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.AsignacionesProyectosEmpleadoDTO;
import com.conaveg.cona.model.AsignacionesProyectosEmpleado;
import com.conaveg.cona.model.Empleado;
import com.conaveg.cona.model.Proyecto;
import com.conaveg.cona.repository.AsignacionesProyectosEmpleadoRepository;
import com.conaveg.cona.repository.EmpleadoRepository;
import com.conaveg.cona.repository.ProyectoRepository;

@Service
public class AsignacionesProyectosEmpleadoService {
    @Autowired
    private AsignacionesProyectosEmpleadoRepository asignacionesProyectosEmpleadoRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<AsignacionesProyectosEmpleadoDTO> getAllAsignacionesProyectosEmpleado() {
        return asignacionesProyectosEmpleadoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AsignacionesProyectosEmpleadoDTO getAsignacionesProyectosEmpleadoById(Long id) {
        return asignacionesProyectosEmpleadoRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public AsignacionesProyectosEmpleadoDTO saveAsignacionesProyectosEmpleado(AsignacionesProyectosEmpleadoDTO asignacionDTO) {
        AsignacionesProyectosEmpleado asignacion = convertToEntity(asignacionDTO);
        AsignacionesProyectosEmpleado saved = asignacionesProyectosEmpleadoRepository.save(asignacion);
        return convertToDTO(saved);
    }

    public AsignacionesProyectosEmpleadoDTO updateAsignacionesProyectosEmpleado(Long id, AsignacionesProyectosEmpleadoDTO asignacionDTO) {
        if (asignacionesProyectosEmpleadoRepository.existsById(id)) {
            AsignacionesProyectosEmpleado asignacion = convertToEntity(asignacionDTO);
            asignacion.setId(id);
            AsignacionesProyectosEmpleado updated = asignacionesProyectosEmpleadoRepository.save(asignacion);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteAsignacionesProyectosEmpleado(Long id) {
        asignacionesProyectosEmpleadoRepository.deleteById(id);
    }

    private AsignacionesProyectosEmpleadoDTO convertToDTO(AsignacionesProyectosEmpleado asignacion) {
        AsignacionesProyectosEmpleadoDTO dto = new AsignacionesProyectosEmpleadoDTO();
        dto.setId(asignacion.getId());
        dto.setEmpleadoId(asignacion.getEmpleados() != null ? asignacion.getEmpleados().getId() : null);
        dto.setProyectoId(asignacion.getProyectos() != null ? asignacion.getProyectos().getId() : null);
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setFechaFinAsignacion(asignacion.getFechaFinAsignacion());
        dto.setRol(asignacion.getRol());
        return dto;
    }

    private AsignacionesProyectosEmpleado convertToEntity(AsignacionesProyectosEmpleadoDTO dto) {
        AsignacionesProyectosEmpleado asignacion = new AsignacionesProyectosEmpleado();
        asignacion.setId(dto.getId());
        
        // Asignar empleado si se proporciona empleadoId
        if (dto.getEmpleadoId() != null) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId()).orElse(null);
            asignacion.setEmpleados(empleado);
        }
        
        // Asignar proyecto si se proporciona proyectoId
        if (dto.getProyectoId() != null) {
            Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId()).orElse(null);
            asignacion.setProyectos(proyecto);
        }
        
        asignacion.setFechaAsignacion(dto.getFechaAsignacion());
        asignacion.setFechaFinAsignacion(dto.getFechaFinAsignacion());
        asignacion.setRol(dto.getRol());
        return asignacion;
    }
}
