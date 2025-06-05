package com.conaveg.cona.controller;

import com.conaveg.cona.dto.FacturaDTO;
import com.conaveg.cona.service.FacturaService;
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
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Gestión de facturas de compras y servicios relacionados al inventario.")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;

    @Operation(summary = "Listar facturas", description = "Obtiene todas las facturas registradas en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de facturas obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<FacturaDTO>> getAllFacturas() {
        List<FacturaDTO> facturas = facturaService.getAllFacturas();
        return ResponseEntity.ok(facturas);
    }

    @Operation(summary = "Obtener factura por ID", description = "Obtiene una factura específica a partir de su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura encontrada"),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FacturaDTO> getFacturaById(
            @Parameter(description = "ID de la factura a buscar", example = "1") @PathVariable Long id) {
        FacturaDTO factura = facturaService.getFacturaById(id);
        if (factura != null) {
            return ResponseEntity.ok(factura);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear factura", description = "Registra una nueva factura de compra o servicio.")
    @ApiResponse(responseCode = "201", description = "Factura creada exitosamente")
    @PostMapping
    public ResponseEntity<FacturaDTO> createFactura(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Objeto factura a crear")
            @RequestBody FacturaDTO facturaDTO) {
        FacturaDTO created = facturaService.saveFactura(facturaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Actualizar factura", description = "Actualiza los datos de una factura existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(
            @Parameter(description = "ID de la factura a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados de la factura")
            @RequestBody FacturaDTO facturaDTO) {
        FacturaDTO updated = facturaService.updateFactura(id, facturaDTO);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar factura", description = "Elimina una factura por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Factura eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFactura(
            @Parameter(description = "ID de la factura a eliminar", example = "1") @PathVariable Long id) {
        FacturaDTO factura = facturaService.getFacturaById(id);
        if (factura != null) {
            facturaService.deleteFactura(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
