package com.conaveg.cona.controller;

import com.conaveg.cona.dto.AsistenciaRegistroRapidoDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import com.conaveg.cona.dto.AsistenciaDTO;
import com.conaveg.cona.service.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/asistencias")
@Tag(name = "Asistencias", description = "Registro y gestión de la asistencia del personal.")
@SecurityRequirement(name = "bearerAuth")
public class AsistenciaController {

    @Operation(summary = "Registro rápido de asistencia", description = "Registra una asistencia solo con el número de documento y el método de registro. El backend resuelve el empleado y guarda la entrada.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asistencia registrada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    @PostMapping("/registro-rapido")
    public ResponseEntity<AsistenciaDTO> registrarAsistenciaRapida(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos mínimos para registrar asistencia")
            @RequestBody AsistenciaRegistroRapidoDTO request) {
        AsistenciaDTO created = asistenciaService.registrarAsistenciaRapida(request);
        if (created != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @Autowired
    private AsistenciaService asistenciaService;

    @Operation(summary = "Listar asistencias", description = "Obtiene todos los registros de asistencia del personal.")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<AsistenciaDTO>> getAllAsistencias() {
        List<AsistenciaDTO> asistencias = asistenciaService.getAllAsistencias();
        return ResponseEntity.ok(asistencias);
    }

    @Operation(summary = "Obtener asistencia por ID", description = "Obtiene un registro de asistencia específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia encontrada"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> getAsistenciaById(
            @Parameter(description = "ID de la asistencia a buscar", example = "1") @PathVariable Long id) {
        AsistenciaDTO asistencia = asistenciaService.getAsistenciaById(id);
        if (asistencia != null) {
            return ResponseEntity.ok(asistencia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Registrar asistencia", description = "Registra una nueva asistencia para un empleado.")
    @ApiResponse(responseCode = "201", description = "Asistencia registrada exitosamente")
    @PostMapping
    public ResponseEntity<AsistenciaDTO> createAsistencia(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto asistencia a registrar")
            @RequestBody AsistenciaDTO asistenciaDTO) {
        AsistenciaDTO created = asistenciaService.saveAsistencia(asistenciaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar asistencia", description = "Actualiza los datos de un registro de asistencia existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> updateAsistencia(
            @Parameter(description = "ID de la asistencia a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la asistencia")
            @RequestBody AsistenciaDTO asistenciaDTO) {
        AsistenciaDTO updated = asistenciaService.updateAsistencia(id, asistenciaDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar asistencia", description = "Elimina un registro de asistencia por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asistencia eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Asistencia no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsistencia(
            @Parameter(description = "ID de la asistencia a eliminar", example = "1") @PathVariable Long id) {
        AsistenciaDTO asistencia = asistenciaService.getAsistenciaById(id);
        if (asistencia != null) {
            asistenciaService.deleteAsistencia(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
