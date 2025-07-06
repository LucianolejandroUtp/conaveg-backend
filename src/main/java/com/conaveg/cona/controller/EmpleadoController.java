package com.conaveg.cona.controller;

import com.conaveg.cona.dto.EmpleadoDTO;
import com.conaveg.cona.service.EmpleadoService;
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
@RequestMapping("/api/empleados")
@Tag(name = "Empleados", description = "Gestión de empleados y su información personal y laboral.")
@SecurityRequirement(name = "bearerAuth")
public class EmpleadoController {
    @Autowired
    private EmpleadoService empleadoService;

    @Operation(summary = "Buscar empleado por número de documento", description = "Obtiene un empleado a partir de su número de documento (DNI, CI, etc).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO') or hasRole('GERENTE')")
    @GetMapping("/documento/{nroDocumento}")
    public ResponseEntity<EmpleadoDTO> getEmpleadoByNroDocumento(
            @Parameter(description = "Número de documento del empleado", example = "12345678") @PathVariable String nroDocumento) {
        EmpleadoDTO empleado = empleadoService.getEmpleadoByNroDocumento(nroDocumento);
        if (empleado != null) {
            return ResponseEntity.ok(empleado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Listar empleados", description = "Obtiene todos los empleados registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida correctamente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> getAllEmpleados() {
        List<EmpleadoDTO> empleados = empleadoService.getAllEmpleados();
        return ResponseEntity.ok(empleados);
    }

    @Operation(summary = "Obtener empleado por ID", description = "Obtiene un empleado específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLEADO')")
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> getEmpleadoById(
            @Parameter(description = "ID del empleado a buscar", example = "1") @PathVariable Long id) {
        EmpleadoDTO empleado = empleadoService.getEmpleadoById(id);
        if (empleado != null) {
            return ResponseEntity.ok(empleado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear empleado", description = "Registra un nuevo empleado en el sistema.")
    @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    @PostMapping
    public ResponseEntity<EmpleadoDTO> createEmpleado(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto empleado a crear")
            @RequestBody EmpleadoDTO empleadoDTO) {
        EmpleadoDTO created = empleadoService.saveEmpleado(empleadoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleado actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or @authorizationService.isCurrentUser(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> updateEmpleado(
            @Parameter(description = "ID del empleado a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del empleado")
            @RequestBody EmpleadoDTO empleadoDTO) {
        EmpleadoDTO updated = empleadoService.updateEmpleado(id, empleadoDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar empleado", description = "Elimina un empleado existente por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Empleado eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(
            @Parameter(description = "ID del empleado a eliminar", example = "1") @PathVariable Long id) {
        EmpleadoDTO empleado = empleadoService.getEmpleadoById(id);
        if (empleado != null) {
            empleadoService.deleteEmpleado(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
