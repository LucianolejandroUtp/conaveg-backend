package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.AsistenciaDTO;
import com.conaveg.cona.model.Asistencia;
import com.conaveg.cona.model.Empleado;
import com.conaveg.cona.repository.AsistenciaRepository;
import com.conaveg.cona.repository.EmpleadoRepository;

@Service
public class AsistenciaService {
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<AsistenciaDTO> getAllAsistencias() {
        return asistenciaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AsistenciaDTO getAsistenciaById(Long id) {
        return asistenciaRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public AsistenciaDTO saveAsistencia(AsistenciaDTO asistenciaDTO) {
        Asistencia asistencia = convertToEntity(asistenciaDTO);
        Asistencia saved = asistenciaRepository.save(asistencia);
        return convertToDTO(saved);
    }

    public AsistenciaDTO updateAsistencia(Long id, AsistenciaDTO asistenciaDTO) {
        if (asistenciaRepository.existsById(id)) {
            Asistencia asistencia = convertToEntity(asistenciaDTO);
            asistencia.setId(id);
            Asistencia updated = asistenciaRepository.save(asistencia);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteAsistencia(Long id) {
        asistenciaRepository.deleteById(id);
    }

    private AsistenciaDTO convertToDTO(Asistencia asistencia) {
        AsistenciaDTO dto = new AsistenciaDTO();
        dto.setId(asistencia.getId());
        dto.setEmpleadoId(asistencia.getEmpleados() != null ? asistencia.getEmpleados().getId() : null);
        dto.setEntrada(asistencia.getEntrada());
        dto.setSalida(asistencia.getSalida());
        dto.setTipoRegistro(asistencia.getTipoRegistro());
        dto.setUbicacionRegistro(asistencia.getUbicacionRegistro());
        dto.setMetodoRegistro(asistencia.getMetodoRegistro());
        dto.setObservacion(asistencia.getObservacion());
        return dto;
    }

    private Asistencia convertToEntity(AsistenciaDTO dto) {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(dto.getId());
        
        // Asignar empleado si se proporciona empleadoId
        if (dto.getEmpleadoId() != null) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId()).orElse(null);
            asistencia.setEmpleados(empleado);
        }
        
        asistencia.setEntrada(dto.getEntrada());
        asistencia.setSalida(dto.getSalida());
        asistencia.setTipoRegistro(dto.getTipoRegistro());
        asistencia.setUbicacionRegistro(dto.getUbicacionRegistro());
        asistencia.setMetodoRegistro(dto.getMetodoRegistro());
        asistencia.setObservacion(dto.getObservacion());
        return asistencia;
    }
}
