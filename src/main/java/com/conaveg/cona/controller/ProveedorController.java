package com.conaveg.cona.controller;

import com.conaveg.cona.dto.ProveedorDTO;
import com.conaveg.cona.service.ProveedorService;
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
@RequestMapping("/api/proveedores")
@Tag(name = "Proveedores", description = "Gestión de proveedores de bienes y servicios para el inventario.")
public class ProveedorController {
    @Autowired
    private ProveedorService proveedorService;

    @Operation(summary = "Listar proveedores", description = "Obtiene todos los proveedores registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida correctamente")
    @GetMapping    public ResponseEntity<List<ProveedorDTO>> getAllProveedores() {
        List<ProveedorDTO> proveedores = proveedorService.getAllProveedores();
        return ResponseEntity.ok(proveedores);
    }

    @Operation(summary = "Obtener proveedor por ID", description = "Obtiene un proveedor específico a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @GetMapping("/{id}")    public ResponseEntity<ProveedorDTO> getProveedorById(
            @Parameter(description = "ID del proveedor a buscar", example = "1") @PathVariable Long id) {
        ProveedorDTO proveedor = proveedorService.getProveedorById(id);
        if (proveedor != null) {
            return ResponseEntity.ok(proveedor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear proveedor", description = "Crea un nuevo proveedor de bienes o servicios.")
    @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente")
    @PostMapping    public ResponseEntity<ProveedorDTO> createProveedor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto proveedor a crear")
            @RequestBody ProveedorDTO proveedor) {
        ProveedorDTO created = proveedorService.saveProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar proveedor", description = "Actualiza los datos de un proveedor existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @PutMapping("/{id}")    public ResponseEntity<ProveedorDTO> updateProveedor(
            @Parameter(description = "ID del proveedor a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del proveedor")
            @RequestBody ProveedorDTO proveedor) {
        ProveedorDTO updated = proveedorService.updateProveedor(id, proveedor);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor existente por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proveedor eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProveedor(
            @Parameter(description = "ID del proveedor a eliminar", example = "1") @PathVariable Long id) {
        boolean deleted = proveedorService.deleteProveedor(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
