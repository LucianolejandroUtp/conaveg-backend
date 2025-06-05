package com.conaveg.cona.controller;

import com.conaveg.cona.dto.ProyectoDTO;
import com.conaveg.cona.service.ProyectoService;
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
@RequestMapping("/api/proyectos")
@Tag(name = "Proyectos", description = "Gestión de proyectos y locaciones de trabajo para asignación de personal.")
public class ProyectoController {
    @Autowired
    private ProyectoService proyectoService;

    @Operation(summary = "Listar proyectos", description = "Obtiene todos los proyectos registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<ProyectoDTO>> getAllProyectos() {
        List<ProyectoDTO> proyectos = proyectoService.getAllProyectos();
        return ResponseEntity.ok(proyectos);
    }

    @Operation(summary = "Obtener proyecto por ID", description = "Obtiene un proyecto específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto encontrado"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDTO> getProyectoById(
            @Parameter(description = "ID del proyecto a buscar", example = "1") @PathVariable Long id) {
        ProyectoDTO proyecto = proyectoService.getProyectoById(id);
        if (proyecto != null) {
            return ResponseEntity.ok(proyecto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear proyecto", description = "Crea un nuevo proyecto o locación de trabajo.")
    @ApiResponse(responseCode = "201", description = "Proyecto creado exitosamente")
    @PostMapping
    public ResponseEntity<ProyectoDTO> createProyecto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto proyecto a crear")
            @RequestBody ProyectoDTO proyectoDTO) {
        ProyectoDTO created = proyectoService.saveProyecto(proyectoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar proyecto", description = "Actualiza los datos de un proyecto existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proyecto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDTO> updateProyecto(
            @Parameter(description = "ID del proyecto a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del proyecto")
            @RequestBody ProyectoDTO proyectoDTO) {
        ProyectoDTO updated = proyectoService.updateProyecto(id, proyectoDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar proyecto", description = "Elimina un proyecto existente por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proyecto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Proyecto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProyecto(
            @Parameter(description = "ID del proyecto a eliminar", example = "1") @PathVariable Long id) {
        ProyectoDTO proyecto = proyectoService.getProyectoById(id);
        if (proyecto != null) {
            proyectoService.deleteProyecto(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
