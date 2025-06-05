package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.Rol;
import com.conaveg.cona.dto.RolDTO;
import com.conaveg.cona.repository.RolRepository;

@Service
public class RolService {
    // Aquí puedes inyectar el repositorio de roles y definir los métodos necesarios
    // para manejar la lógica de negocio relacionada con los roles.
    
    // Por ejemplo, podrías tener métodos como:
    // - getAllRoles()
    // - saveRol(Rol rol)
    // - deleteRol(Long id)
    // - updateRol(Long id, Rol rol)
    
    // Asegúrate de inyectar el RolRepository y utilizarlo en estos métodos.
    @Autowired
    private RolRepository rolRepository;

    public List<RolDTO> getAllRoles() {
        return rolRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public RolDTO getRolById(Long id) {
        return rolRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public RolDTO saveRol(RolDTO rolDTO) {
        Rol rol = toEntity(rolDTO);
        Rol saved = rolRepository.save(rol);
        return toDTO(saved);
    }

    public RolDTO updateRol(Long id, RolDTO rolDTO) {
        if (rolRepository.existsById(id)) {
            Rol rol = toEntity(rolDTO);
            rol.setId(id);
            Rol updated = rolRepository.save(rol);
            return toDTO(updated);
        }
        return null;
    }
    private RolDTO toDTO(Rol rol) {
        RolDTO dto = new RolDTO();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        dto.setEstado(rol.getEstado());
        dto.setUniqueId(rol.getUniqueId());
        return dto;
    }

    private Rol toEntity(RolDTO dto) {
        Rol rol = new Rol();
        rol.setId(dto.getId());
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        rol.setEstado(dto.getEstado());
        rol.setUniqueId(dto.getUniqueId());
        return rol;
    }

    public void deleteRol(Long id) {
        rolRepository.deleteById(id);
    }

}
