package com.conaveg.cona.controller;

import com.conaveg.cona.dto.MovimientosInventarioDTO;
import com.conaveg.cona.service.MovimientosInventarioService;
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
@RequestMapping("/api/movimientos-inventario")
@Tag(name = "Movimientos de Inventario", description = "Registro y gestión de entradas y salidas de productos en el inventario.")
public class MovimientosInventarioController {
    @Autowired
    private MovimientosInventarioService movimientosInventarioService;    @Operation(summary = "Listar movimientos", description = "Obtiene todos los movimientos de inventario registrados.")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<MovimientosInventarioDTO>> getAllMovimientosInventario() {
        List<MovimientosInventarioDTO> movimientos = movimientosInventarioService.getAllMovimientosInventario();
        return ResponseEntity.ok(movimientos);
    }    @Operation(summary = "Obtener movimiento por ID", description = "Obtiene un movimiento de inventario específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovimientosInventarioDTO> getMovimientosInventarioById(
            @Parameter(description = "ID del movimiento a buscar", example = "1") @PathVariable Long id) {
        MovimientosInventarioDTO mov = movimientosInventarioService.getMovimientosInventarioById(id);
        if (mov != null) {
            return ResponseEntity.ok(mov);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Crear movimiento", description = "Registra un nuevo movimiento de entrada o salida en el inventario.")
    @ApiResponse(responseCode = "201", description = "Movimiento creado exitosamente")
    @PostMapping
    public ResponseEntity<MovimientosInventarioDTO> createMovimientosInventario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto movimiento a crear")
            @RequestBody MovimientosInventarioDTO mov) {
        MovimientosInventarioDTO created = movimientosInventarioService.saveMovimientosInventario(mov);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }    @Operation(summary = "Actualizar movimiento", description = "Actualiza los datos de un movimiento de inventario existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MovimientosInventarioDTO> updateMovimientosInventario(
            @Parameter(description = "ID del movimiento a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del movimiento")
            @RequestBody MovimientosInventarioDTO mov) {
        MovimientosInventarioDTO updated = movimientosInventarioService.updateMovimientosInventario(id, mov);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Eliminar movimiento", description = "Elimina un movimiento de inventario por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimientosInventario(
            @Parameter(description = "ID del movimiento a eliminar", example = "1") @PathVariable Long id) {
        MovimientosInventarioDTO mov = movimientosInventarioService.getMovimientosInventarioById(id);
        if (mov != null) {
            movimientosInventarioService.deleteMovimientosInventario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
