package com.conaveg.cona.controller;

import com.conaveg.cona.dto.FacturaDTO;
import com.conaveg.cona.service.FacturaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de integración para FacturaController.
 * Verifica la correcta integración entre el controller y el servicio de facturas.
 */
@WebMvcTest(FacturaController.class)
class FacturaControllerIntegrationTest {    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaService facturaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllFacturas_WithData_ShouldReturnFacturasList() throws Exception {
        // Given
        FacturaDTO factura1 = new FacturaDTO();
        factura1.setId(1L);
        factura1.setProveedorId(101L);
        factura1.setUsuarioId(201L);
        factura1.setNroFactura("F001-00001");
        factura1.setTipoDocumento("FACTURA");
        factura1.setFechaEmision(LocalDate.of(2025, 6, 1));
        factura1.setMontoTotal(15000);
        factura1.setMoneda("PEN");
        factura1.setEstadoFactura("PENDIENTE");

        FacturaDTO factura2 = new FacturaDTO();
        factura2.setId(2L);
        factura2.setProveedorId(102L);
        factura2.setUsuarioId(201L);
        factura2.setNroFactura("F001-00002");
        factura2.setTipoDocumento("BOLETA");
        factura2.setFechaEmision(LocalDate.of(2025, 6, 2));
        factura2.setMontoTotal(8500);
        factura2.setMoneda("PEN");
        factura2.setEstadoFactura("PAGADA");

        List<FacturaDTO> facturas = Arrays.asList(factura1, factura2);
        when(facturaService.getAllFacturas()).thenReturn(facturas);

        // When & Then
        mockMvc.perform(get("/api/facturas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nroFactura", is("F001-00001")))
                .andExpect(jsonPath("$[0].tipoDocumento", is("FACTURA")))
                .andExpect(jsonPath("$[0].montoTotal", is(15000)))
                .andExpect(jsonPath("$[0].estadoFactura", is("PENDIENTE")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nroFactura", is("F001-00002")))
                .andExpect(jsonPath("$[1].tipoDocumento", is("BOLETA")))
                .andExpect(jsonPath("$[1].montoTotal", is(8500)))
                .andExpect(jsonPath("$[1].estadoFactura", is("PAGADA")));

        verify(facturaService, times(1)).getAllFacturas();
    }

    @Test
    void getAllFacturas_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(facturaService.getAllFacturas()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/facturas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(facturaService, times(1)).getAllFacturas();
    }

    @Test
    void getFacturaById_WithValidId_ShouldReturnFactura() throws Exception {
        // Given
        FacturaDTO factura = new FacturaDTO();
        factura.setId(1L);
        factura.setProveedorId(101L);
        factura.setNroFactura("F001-00001");
        factura.setTipoDocumento("FACTURA");
        factura.setFechaEmision(LocalDate.of(2025, 6, 1));
        factura.setFechaVencimiento(LocalDate.of(2025, 7, 1));
        factura.setMontoTotal(15000);
        factura.setDescripcion("Compra de suministros");

        when(facturaService.getFacturaById(1L)).thenReturn(factura);

        // When & Then
        mockMvc.perform(get("/api/facturas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.proveedorId", is(101)))
                .andExpect(jsonPath("$.nroFactura", is("F001-00001")))
                .andExpect(jsonPath("$.tipoDocumento", is("FACTURA")))
                .andExpect(jsonPath("$.montoTotal", is(15000)))
                .andExpect(jsonPath("$.descripcion", is("Compra de suministros")));

        verify(facturaService, times(1)).getFacturaById(1L);
    }

    @Test
    void getFacturaById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(facturaService.getFacturaById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/facturas/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(facturaService, times(1)).getFacturaById(999L);
    }

    @Test
    void createFactura_WithValidData_ShouldReturnCreatedFactura() throws Exception {
        // Given
        FacturaDTO newFactura = new FacturaDTO();
        newFactura.setProveedorId(101L);
        newFactura.setUsuarioId(201L);
        newFactura.setNroFactura("F001-00003");
        newFactura.setTipoDocumento("FACTURA");
        newFactura.setFechaEmision(LocalDate.of(2025, 6, 7));
        newFactura.setMontoTotal(12000);
        newFactura.setMoneda("PEN");
        newFactura.setDescripcion("Compra de materiales");

        FacturaDTO savedFactura = new FacturaDTO();
        savedFactura.setId(3L);
        savedFactura.setProveedorId(101L);
        savedFactura.setUsuarioId(201L);
        savedFactura.setNroFactura("F001-00003");
        savedFactura.setTipoDocumento("FACTURA");
        savedFactura.setFechaEmision(LocalDate.of(2025, 6, 7));
        savedFactura.setMontoTotal(12000);
        savedFactura.setMoneda("PEN");
        savedFactura.setDescripcion("Compra de materiales");
        savedFactura.setEstadoFactura("PENDIENTE");

        when(facturaService.saveFactura(any(FacturaDTO.class))).thenReturn(savedFactura);

        // When & Then
        mockMvc.perform(post("/api/facturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newFactura)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.proveedorId", is(101)))
                .andExpect(jsonPath("$.nroFactura", is("F001-00003")))
                .andExpect(jsonPath("$.tipoDocumento", is("FACTURA")))
                .andExpect(jsonPath("$.montoTotal", is(12000)))
                .andExpect(jsonPath("$.estadoFactura", is("PENDIENTE")));

        verify(facturaService, times(1)).saveFactura(any(FacturaDTO.class));
    }

    @Test
    void updateFactura_WithValidData_ShouldReturnUpdatedFactura() throws Exception {
        // Given
        FacturaDTO updatedFactura = new FacturaDTO();
        updatedFactura.setId(1L);
        updatedFactura.setProveedorId(101L);
        updatedFactura.setNroFactura("F001-00001");
        updatedFactura.setEstadoFactura("PAGADA");
        updatedFactura.setMontoTotal(15000);
        updatedFactura.setDescripcion("Compra de suministros - Pagada");

        when(facturaService.updateFactura(eq(1L), any(FacturaDTO.class))).thenReturn(updatedFactura);

        // When & Then
        mockMvc.perform(put("/api/facturas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFactura)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoFactura", is("PAGADA")))
                .andExpect(jsonPath("$.descripcion", is("Compra de suministros - Pagada")));

        verify(facturaService, times(1)).updateFactura(eq(1L), any(FacturaDTO.class));
    }

    @Test
    void updateFactura_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(facturaService.updateFactura(eq(999L), any(FacturaDTO.class))).thenReturn(null);

        FacturaDTO facturaUpdate = new FacturaDTO();
        facturaUpdate.setEstadoFactura("PAGADA");

        // When & Then
        mockMvc.perform(put("/api/facturas/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(facturaUpdate)))
                .andExpect(status().isNotFound());

        verify(facturaService, times(1)).updateFactura(eq(999L), any(FacturaDTO.class));
    }

    @Test
    void deleteFactura_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        FacturaDTO existingFactura = new FacturaDTO();
        existingFactura.setId(1L);
        when(facturaService.getFacturaById(1L)).thenReturn(existingFactura);
        doNothing().when(facturaService).deleteFactura(1L);

        // When & Then
        mockMvc.perform(delete("/api/facturas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(facturaService, times(1)).getFacturaById(1L);
        verify(facturaService, times(1)).deleteFactura(1L);
    }

    @Test
    void deleteFactura_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(facturaService.getFacturaById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/facturas/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(facturaService, times(1)).getFacturaById(999L);
    }
}
