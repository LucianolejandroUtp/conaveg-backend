package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.Empleado;
import com.conaveg.cona.dto.EmpleadoDTO;
import com.conaveg.cona.repository.EmpleadoRepository;

@Service
public class EmpleadoService {
    public EmpleadoDTO getEmpleadoByNroDocumento(String nroDocumento) {
        return empleadoRepository.findByNroDocumento(nroDocumento)
                .map(this::toDTO)
                .orElse(null);
    }
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<EmpleadoDTO> getAllEmpleados() {
        return empleadoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EmpleadoDTO getEmpleadoById(Long id) {
        return empleadoRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public EmpleadoDTO saveEmpleado(EmpleadoDTO empleadoDTO) {
        Empleado empleado = toEntity(empleadoDTO);
        Empleado saved = empleadoRepository.save(empleado);
        return toDTO(saved);
    }

    public EmpleadoDTO updateEmpleado(Long id, EmpleadoDTO empleadoDTO) {
        if (empleadoRepository.existsById(id)) {
            Empleado empleado = toEntity(empleadoDTO);
            empleado.setId(id);
            Empleado updated = empleadoRepository.save(empleado);
            return toDTO(updated);
        }
        return null;
    }
    private EmpleadoDTO toDTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        if (empleado.getUsers() != null) dto.setUserId(empleado.getUsers().getId());
        dto.setNombres(empleado.getNombres());
        dto.setApellidos(empleado.getApellidos());
        dto.setNroDocumento(empleado.getNroDocumento());
        dto.setFechaNacimiento(empleado.getFechaNacimiento());
        dto.setDireccion(empleado.getDireccion());
        dto.setTelefono(empleado.getTelefono());
        dto.setPuesto(empleado.getPuesto());
        dto.setFechaIngreso(empleado.getFechaIngreso());
        dto.setEstado(empleado.getEstado());
        dto.setUniqueId(empleado.getUniqueId());
        return dto;
    }

    private Empleado toEntity(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setId(dto.getId());
        // Relación con User
        if (dto.getUserId() != null) {
            com.conaveg.cona.model.User user = new com.conaveg.cona.model.User();
            user.setId(dto.getUserId());
            empleado.setUsers(user);
        }
        empleado.setNombres(dto.getNombres());
        empleado.setApellidos(dto.getApellidos());
        empleado.setNroDocumento(dto.getNroDocumento());
        empleado.setFechaNacimiento(dto.getFechaNacimiento());
        empleado.setDireccion(dto.getDireccion());
        empleado.setTelefono(dto.getTelefono());
        empleado.setPuesto(dto.getPuesto());
        empleado.setFechaIngreso(dto.getFechaIngreso());
        empleado.setEstado(dto.getEstado());
        empleado.setUniqueId(dto.getUniqueId());
        return empleado;
    }

    public void deleteEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }
}
