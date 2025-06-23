package com.conaveg.cona.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conaveg.cona.dto.RolDTO;
import com.conaveg.cona.service.RolService;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Operaciones CRUD para roles de usuario")
@SecurityRequirement(name = "bearerAuth")
public class RolController {
    @Autowired
    private RolService rolService;    @Operation(summary = "Listar todos los roles", description = "Obtiene una lista de todos los roles registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de roles obtenida correctamente")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<RolDTO>> getAllRoles() {
        List<RolDTO> roles = rolService.getAllRoles();
        return ResponseEntity.ok(roles);
    }    @Operation(summary = "Obtener rol por ID", description = "Obtiene un rol específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol encontrado"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RolDTO> getRolById(
            @Parameter(description = "ID del rol a buscar", example = "1") @PathVariable Long id) {
        RolDTO rol = rolService.getRolById(id);
        if (rol != null) {
            return ResponseEntity.ok(rol);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Crear nuevo rol", description = "Crea un nuevo rol en el sistema.")
    @ApiResponse(responseCode = "201", description = "Rol creado exitosamente")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RolDTO> createRol(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto rol a crear")
            @RequestBody RolDTO rolDTO) {
        RolDTO createdRol = rolService.saveRol(rolDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRol);
    }    @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RolDTO> updateRol(
            @Parameter(description = "ID del rol a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del rol")
            @RequestBody RolDTO rolDTO) {
        RolDTO updatedRol = rolService.updateRol(id, rolDTO);
        if (updatedRol != null) {
            return ResponseEntity.ok(updatedRol);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Eliminar rol", description = "Elimina un rol existente por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRol(
            @Parameter(description = "ID del rol a eliminar", example = "1") @PathVariable Long id) {
        RolDTO rol = rolService.getRolById(id);
        if (rol != null) {
            rolService.deleteRol(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
