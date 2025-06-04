package com.conaveg.cona.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.models.Rol;
import com.conaveg.cona.repositories.RolRepository;

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

    public List<Rol> getAllRoles() {
        return rolRepository.findAll();
    }

    public Rol getRolById(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    public Rol saveRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol updateRol(Long id, Rol rol) {
        // Primero, verifica si el rol existe
        if (rolRepository.existsById(id)) {
            rol.setId(id);
            return rolRepository.save(rol);
        }
        return null;
    }

    public void deleteRol(Long id) {
        rolRepository.deleteById(id);
    }

}