package com.conaveg.cona.controller;

import com.conaveg.cona.dto.AsignacionesProyectosEmpleadoDTO;
import com.conaveg.cona.service.AsignacionesProyectosEmpleadoService;
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
@RequestMapping("/api/asignaciones-proyectos-empleado")
@Tag(name = "Asignaciones Proyectos-Empleado", description = "Gestión de la asignación de empleados a proyectos o locaciones de trabajo.")
public class AsignacionesProyectosEmpleadoController {
    @Autowired
    private AsignacionesProyectosEmpleadoService asignacionesProyectosEmpleadoService;    @Operation(summary = "Listar asignaciones", description = "Obtiene todas las asignaciones de empleados a proyectos.")
    @ApiResponse(responseCode = "200", description = "Lista de asignaciones obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<AsignacionesProyectosEmpleadoDTO>> getAllAsignacionesProyectosEmpleado() {
        List<AsignacionesProyectosEmpleadoDTO> asignaciones = asignacionesProyectosEmpleadoService.getAllAsignacionesProyectosEmpleado();
        return ResponseEntity.ok(asignaciones);
    }    @Operation(summary = "Obtener asignación por ID", description = "Obtiene una asignación específica a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignación encontrada"),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AsignacionesProyectosEmpleadoDTO> getAsignacionesProyectosEmpleadoById(
            @Parameter(description = "ID de la asignación a buscar", example = "1") @PathVariable Long id) {
        AsignacionesProyectosEmpleadoDTO asignacion = asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(id);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Crear asignación", description = "Asigna un empleado a un proyecto o locación de trabajo.")
    @ApiResponse(responseCode = "201", description = "Asignación creada exitosamente")
    @PostMapping
    public ResponseEntity<AsignacionesProyectosEmpleadoDTO> createAsignacionesProyectosEmpleado(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto asignación a crear")
            @RequestBody AsignacionesProyectosEmpleadoDTO asignacion) {
        AsignacionesProyectosEmpleadoDTO created = asignacionesProyectosEmpleadoService.saveAsignacionesProyectosEmpleado(asignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }    @Operation(summary = "Actualizar asignación", description = "Actualiza los datos de una asignación existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignación actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AsignacionesProyectosEmpleadoDTO> updateAsignacionesProyectosEmpleado(
            @Parameter(description = "ID de la asignación a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la asignación")
            @RequestBody AsignacionesProyectosEmpleadoDTO asignacion) {
        AsignacionesProyectosEmpleadoDTO updated = asignacionesProyectosEmpleadoService.updateAsignacionesProyectosEmpleado(id, asignacion);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Eliminar asignación", description = "Elimina una asignación de empleado a proyecto por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asignación eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Asignación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsignacionesProyectosEmpleado(
            @Parameter(description = "ID de la asignación a eliminar", example = "1") @PathVariable Long id) {
        AsignacionesProyectosEmpleadoDTO asignacion = asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(id);
        if (asignacion != null) {
            asignacionesProyectosEmpleadoService.deleteAsignacionesProyectosEmpleado(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
