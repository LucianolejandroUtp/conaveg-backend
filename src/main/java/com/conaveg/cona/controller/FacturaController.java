package com.conaveg.cona.controller;

import com.conaveg.cona.dto.FacturaDTO;
import com.conaveg.cona.service.FacturaService;
import com.conaveg.cona.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/facturas")
@Tag(name = "Facturas", description = "Gestión de facturas de compras y servicios relacionados al inventario.")
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {
    @Autowired
    private FacturaService facturaService;

    @Autowired
    private FileStorageService fileStorageService;

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

    @Operation(summary = "Crear factura con archivo", description = "Registra una nueva factura con archivo PDF adjunto.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Factura creada exitosamente con archivo"),
        @ApiResponse(responseCode = "400", description = "Error en los datos de la factura o archivo inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(value = "/with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFacturaWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("proveedorId") Long proveedorId,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("nroFactura") String nroFactura,
            @RequestParam("tipoDocumento") String tipoDocumento,
            @RequestParam("fechaEmision") String fechaEmision,
            @RequestParam("fechaVencimiento") String fechaVencimiento,
            @RequestParam("montoTotal") Integer montoTotal,
            @RequestParam("moneda") String moneda,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "estadoFactura", required = false, defaultValue = "PENDIENTE") String estadoFactura) {
        try {
            // Guardar archivo
            FileStorageService.FileInfo fileInfo = fileStorageService.saveFacturaFile(file);
            // Crear DTO de factura
            FacturaDTO facturaDTO = new FacturaDTO();
            facturaDTO.setProveedorId(proveedorId);
            facturaDTO.setUsuarioId(usuarioId);
            facturaDTO.setNroFactura(nroFactura);
            facturaDTO.setTipoDocumento(tipoDocumento);
            facturaDTO.setFechaEmision(java.time.LocalDate.parse(fechaEmision));
            facturaDTO.setFechaVencimiento(java.time.LocalDate.parse(fechaVencimiento));
            facturaDTO.setMontoTotal(montoTotal);
            facturaDTO.setMoneda(moneda);
            facturaDTO.setDescripcion(descripcion);
            facturaDTO.setEstadoFactura(estadoFactura != null ? estadoFactura : "PENDIENTE");
            // Asignar información del archivo
            facturaDTO.setRutaArchivo(fileInfo.getRelativePath());
            facturaDTO.setNombreArchivo(fileInfo.getFilename());
            // Guardar factura
            FacturaDTO created = facturaService.saveFactura(facturaDTO);
            // Respuesta con información adicional del archivo
            Map<String, Object> response = new HashMap<>();
            response.put("factura", created);
            response.put("archivo", Map.of(
                "nombreOriginal", fileInfo.getOriginalFilename(),
                "nombreGuardado", fileInfo.getFilename(),
                "rutaRelativa", fileInfo.getRelativePath(),
                "tamaño", fileInfo.getSize(),
                "tipoContenido", fileInfo.getContentType()
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Error de validación
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error de validación");
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            // Error interno
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            error.put("mensaje", "No se pudo procesar la factura: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Actualizar factura", description = "Actualiza parcialmente los datos de una factura existente. Solo se modifican los campos proporcionados, manteniéndose los valores existentes para campos no especificados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Factura actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<FacturaDTO> updateFactura(
            @Parameter(description = "ID de la factura a actualizar", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos a actualizar de la factura (solo campos proporcionados serán modificados)")
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
            // Eliminar archivo si existe
            if (factura.getRutaArchivo() != null && !factura.getRutaArchivo().isEmpty()) {
                fileStorageService.deleteFile(factura.getRutaArchivo());
            }
            facturaService.deleteFactura(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Descargar archivo de factura", description = "Descarga el archivo PDF asociado a una factura.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo descargado correctamente"),
        @ApiResponse(responseCode = "404", description = "Factura o archivo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error al acceder al archivo")
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFacturaFile(
            @Parameter(description = "ID de la factura", example = "1") @PathVariable Long id) {
        try {
            FacturaDTO factura = facturaService.getFacturaById(id);
            if (factura == null) {
                return ResponseEntity.notFound().build();
            }

            String rutaArchivo = factura.getRutaArchivo();
            if (rutaArchivo == null || rutaArchivo.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Verificar si el archivo existe
            if (!fileStorageService.fileExists(rutaArchivo)) {
                return ResponseEntity.notFound().build();
            }

            // Crear el recurso del archivo
            Path filePath = Paths.get("files").resolve(rutaArchivo);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Configurar headers para la descarga
                String contentType = "application/pdf";
                String filename = factura.getNombreArchivo() != null ? 
                    factura.getNombreArchivo() : "factura_" + id + ".pdf";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
