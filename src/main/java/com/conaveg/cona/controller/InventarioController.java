package com.conaveg.cona.controller;

import com.conaveg.cona.dto.InventarioDTO;
import com.conaveg.cona.service.InventarioService;
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
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Gestión de productos y bienes almacenados en el inventario.")
@SecurityRequirement(name = "bearerAuth")
public class InventarioController {
    @Autowired
    private InventarioService inventarioService;    @Operation(summary = "Listar inventario", description = "Obtiene todos los productos registrados en el inventario.")
    @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida correctamente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('EMPLEADO') or hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<InventarioDTO>> getAllInventario() {
        List<InventarioDTO> inventario = inventarioService.getAllInventario();
        return ResponseEntity.ok(inventario);
    }    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico del inventario a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('EMPLEADO') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<InventarioDTO> getInventarioById(
            @Parameter(description = "ID del producto a buscar", example = "1") @PathVariable Long id) {
        InventarioDTO inv = inventarioService.getInventarioById(id);
        if (inv != null) {
            return ResponseEntity.ok(inv);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Crear producto", description = "Agrega un nuevo producto o bien al inventario.")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    @PostMapping
    public ResponseEntity<InventarioDTO> createInventario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto producto a crear")
            @RequestBody InventarioDTO inventarioDTO) {
        InventarioDTO created = inventarioService.saveInventario(inventarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente en el inventario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<InventarioDTO> updateInventario(
            @Parameter(description = "ID del producto a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del producto")
            @RequestBody InventarioDTO inventarioDTO) {
        InventarioDTO updated = inventarioService.updateInventario(id, inventarioDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }    @Operation(summary = "Eliminar producto", description = "Elimina un producto del inventario por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventario(
            @Parameter(description = "ID del producto a eliminar", example = "1") @PathVariable Long id) {
        InventarioDTO inv = inventarioService.getInventarioById(id);
        if (inv != null) {
            inventarioService.deleteInventario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
