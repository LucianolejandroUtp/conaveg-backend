package com.conaveg.cona.controller;

import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.dto.UserCreateDTO;
import com.conaveg.cona.dto.UserUpdateDTO;
import com.conaveg.cona.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema, incluyendo autenticación y roles.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados en el sistema. Solo disponible para administradores.")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico. Los administradores pueden ver cualquier usuario, gerentes/empleados/usuarios solo pueden ver su propia información.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo puedes ver tu propia información"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('GERENTE','EMPLEADO','USER') and @authorizationService.isOwnerOrAdmin(#id))")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID del usuario a buscar", example = "1") @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema. Solo disponible para administradores. Las contraseñas se cifran automáticamente con BCrypt.")
    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente")
    @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto usuario a crear")
            @Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO created = userService.saveUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente. La contraseña es opcional - si no se proporciona, se mantiene la actual. Los administradores pueden actualizar cualquier usuario, gerentes/empleados/usuarios solo pueden actualizar su propia información.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo puedes actualizar tu propia información"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('GERENTE','EMPLEADO','USER') and @authorizationService.isOwnerOrAdmin(#id))")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID del usuario a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del usuario. La contraseña es opcional.")
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updated = userService.updateUserWithOptionalPassword(id, userUpdateDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario existente por su ID. Solo disponible para administradores.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", example = "1") @PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
