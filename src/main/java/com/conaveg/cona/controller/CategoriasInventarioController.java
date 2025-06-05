package com.conaveg.cona.controller;

import com.conaveg.cona.dto.CategoriasInventarioDTO;
import com.conaveg.cona.service.CategoriasInventarioService;
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
@RequestMapping("/api/categorias-inventario")
@Tag(name = "Categorías de Inventario", description = "Gestión de categorías para clasificar los productos del inventario.")
public class CategoriasInventarioController {
    @Autowired
    private CategoriasInventarioService categoriasInventarioService;

    @Operation(summary = "Listar categorías", description = "Obtiene todas las categorías de inventario registradas.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<CategoriasInventarioDTO>> getAllCategoriasInventario() {
        List<CategoriasInventarioDTO> categorias = categoriasInventarioService.getAllCategoriasInventario();
        return ResponseEntity.ok(categorias);
    }

    @Operation(summary = "Obtener categoría por ID", description = "Obtiene una categoría específica a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriasInventarioDTO> getCategoriasInventarioById(
            @Parameter(description = "ID de la categoría a buscar", example = "1") @PathVariable Long id) {
        CategoriasInventarioDTO categoria = categoriasInventarioService.getCategoriasInventarioById(id);
        if (categoria != null) {
            return ResponseEntity.ok(categoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría de inventario.")
    @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente")
    @PostMapping
    public ResponseEntity<CategoriasInventarioDTO> createCategoriasInventario(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto categoría a crear")
            @RequestBody CategoriasInventarioDTO categoriaDTO) {
        CategoriasInventarioDTO created = categoriasInventarioService.saveCategoriasInventario(categoriaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriasInventarioDTO> updateCategoriasInventario(
            @Parameter(description = "ID de la categoría a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la categoría")
            @RequestBody CategoriasInventarioDTO categoriaDTO) {
        CategoriasInventarioDTO updated = categoriasInventarioService.updateCategoriasInventario(id, categoriaDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría de inventario por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoriasInventario(
            @Parameter(description = "ID de la categoría a eliminar", example = "1") @PathVariable Long id) {
        CategoriasInventarioDTO categoria = categoriasInventarioService.getCategoriasInventarioById(id);
        if (categoria != null) {
            categoriasInventarioService.deleteCategoriasInventario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
