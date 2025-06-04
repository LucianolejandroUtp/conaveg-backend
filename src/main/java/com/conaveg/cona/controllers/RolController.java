package com.conaveg.cona.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conaveg.cona.models.Rol;
import com.conaveg.cona.services.RolService;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    // Aquí puedes definir los endpoints relacionados con los roles.
    // Por ejemplo, podrías tener métodos para obtener todos los roles,
    // crear un nuevo rol, actualizar un rol existente, etc.
    // Asegúrate de inyectar el RolService y utilizarlo en estos métodos.
    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> getAllRoles() {
        List<Rol> roles = rolService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getRolById(@PathVariable Long id) {
        Rol rol = rolService.getRolById(id);
        if (rol != null) {
            return ResponseEntity.ok(rol);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Rol> createRol(@RequestBody Rol rol) {
        Rol createdRol = rolService.saveRol(rol);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRol);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> updateRol(@PathVariable Long id, @RequestBody Rol rol) {
        Rol updatedRol = rolService.updateRol(id, rol);
        if (updatedRol != null) {
            return ResponseEntity.ok(updatedRol);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(@PathVariable Long id) {
        Rol rol = rolService.getRolById(id);
        if (rol != null) {
            rolService.deleteRol(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
