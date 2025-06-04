package com.conaveg.cona.controllers;

import com.conaveg.cona.models.Empleado;
import com.conaveg.cona.services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados", description = "Gestión de empleados y su información personal y laboral.")
public class EmpleadoController {
    @Autowired
    private EmpleadoService empleadoService;

    @Operation(summary = "Listar empleados", description = "Obtiene todos los empleados registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de empleados obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = empleadoService.getAllEmpleados();
        return ResponseEntity.ok(empleados);
    }

    @Operation(summary = "Obtener empleado por ID", description = "Obtiene un empleado específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(
            @Parameter(description = "ID del empleado a buscar", example = "1") @PathVariable Long id) {
        Empleado empleado = empleadoService.getEmpleadoById(id);
        if (empleado != null) {
            return ResponseEntity.ok(empleado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear empleado", description = "Registra un nuevo empleado en el sistema.")
    @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente")
    @PostMapping
    public ResponseEntity<Empleado> createEmpleado(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto empleado a crear")
            @RequestBody Empleado empleado) {
        Empleado created = empleadoService.saveEmpleado(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar empleado", description = "Actualiza los datos de un empleado existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Empleado actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> updateEmpleado(
            @Parameter(description = "ID del empleado a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del empleado")
            @RequestBody Empleado empleado) {
        Empleado updated = empleadoService.updateEmpleado(id, empleado);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(
            @Parameter(description = "ID del empleado a eliminar", example = "1") @PathVariable Long id) {
        Empleado empleado = empleadoService.getEmpleadoById(id);
        if (empleado != null) {
            empleadoService.deleteEmpleado(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
