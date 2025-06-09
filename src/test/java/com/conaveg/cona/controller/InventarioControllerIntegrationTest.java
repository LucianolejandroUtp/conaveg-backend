package com.conaveg.cona.controller;

import com.conaveg.cona.dto.InventarioDTO;
import com.conaveg.cona.service.InventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para InventarioController
 * Valida el funcionamiento de los endpoints REST y la interacción con el servicio
 */
@WebMvcTest(InventarioController.class)
class InventarioControllerIntegrationTest {    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService inventarioService;

    @Autowired
    private ObjectMapper objectMapper;

    private InventarioDTO inventarioDTO;
    private InventarioDTO inventarioDTO2;

    @BeforeEach
    void setUp() {
        inventarioDTO = new InventarioDTO();
        inventarioDTO.setId(1L);
        inventarioDTO.setCategoriaId(1L);
        inventarioDTO.setCodigo("INV-001");
        inventarioDTO.setNombre("Laptop Dell");
        inventarioDTO.setDescripcion("Laptop Dell Inspiron 15 para trabajo de oficina");
        inventarioDTO.setMarca("Dell");
        inventarioDTO.setModelo("Inspiron 15 3000");
        inventarioDTO.setNroSerie("DL123456789");
        inventarioDTO.setStock(5);
        inventarioDTO.setUnidadMedida("UNIDAD");
        inventarioDTO.setFechaAquisicion(LocalDate.of(2023, 6, 15));
        inventarioDTO.setEstadoConservacion("BUENO");

        inventarioDTO2 = new InventarioDTO();
        inventarioDTO2.setId(2L);
        inventarioDTO2.setCategoriaId(2L);
        inventarioDTO2.setCodigo("INV-002");
        inventarioDTO2.setNombre("Impresora HP");
        inventarioDTO2.setDescripcion("Impresora multifuncional HP LaserJet");
        inventarioDTO2.setMarca("HP");
        inventarioDTO2.setModelo("LaserJet Pro MFP M428fdw");
        inventarioDTO2.setNroSerie("HP987654321");
        inventarioDTO2.setStock(2);
        inventarioDTO2.setUnidadMedida("UNIDAD");
        inventarioDTO2.setFechaAquisicion(LocalDate.of(2023, 8, 20));
        inventarioDTO2.setEstadoConservacion("EXCELENTE");
    }

    @Test
    void getAllInventario_ShouldReturnInventarioList() throws Exception {
        // Given
        List<InventarioDTO> inventarios = Arrays.asList(inventarioDTO, inventarioDTO2);
        when(inventarioService.getAllInventario()).thenReturn(inventarios);

        // When & Then
        mockMvc.perform(get("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].codigo", is("INV-001")))
                .andExpect(jsonPath("$[0].nombre", is("Laptop Dell")))
                .andExpect(jsonPath("$[0].marca", is("Dell")))
                .andExpect(jsonPath("$[0].modelo", is("Inspiron 15 3000")))
                .andExpect(jsonPath("$[0].stock", is(5)))
                .andExpect(jsonPath("$[0].estadoConservacion", is("BUENO")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].codigo", is("INV-002")))
                .andExpect(jsonPath("$[1].nombre", is("Impresora HP")))
                .andExpect(jsonPath("$[1].marca", is("HP")))
                .andExpect(jsonPath("$[1].stock", is(2)))
                .andExpect(jsonPath("$[1].estadoConservacion", is("EXCELENTE")));

        verify(inventarioService, times(1)).getAllInventario();
    }

    @Test
    void getAllInventario_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(inventarioService.getAllInventario()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(inventarioService, times(1)).getAllInventario();
    }

    @Test
    void getInventarioById_WithValidId_ShouldReturnInventario() throws Exception {
        // Given
        when(inventarioService.getInventarioById(1L)).thenReturn(inventarioDTO);

        // When & Then
        mockMvc.perform(get("/api/inventario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.codigo", is("INV-001")))
                .andExpect(jsonPath("$.nombre", is("Laptop Dell")))
                .andExpect(jsonPath("$.descripcion", is("Laptop Dell Inspiron 15 para trabajo de oficina")))
                .andExpect(jsonPath("$.marca", is("Dell")))
                .andExpect(jsonPath("$.modelo", is("Inspiron 15 3000")))
                .andExpect(jsonPath("$.nroSerie", is("DL123456789")))
                .andExpect(jsonPath("$.stock", is(5)))
                .andExpect(jsonPath("$.unidadMedida", is("UNIDAD")))
                .andExpect(jsonPath("$.estadoConservacion", is("BUENO")));

        verify(inventarioService, times(1)).getInventarioById(1L);
    }

    @Test
    void getInventarioById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(inventarioService.getInventarioById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/inventario/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(inventarioService, times(1)).getInventarioById(999L);
    }

    @Test
    void createInventario_WithValidData_ShouldReturnCreatedInventario() throws Exception {
        // Given
        when(inventarioService.saveInventario(any(InventarioDTO.class))).thenReturn(inventarioDTO);

        // When & Then
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.codigo", is("INV-001")))
                .andExpect(jsonPath("$.nombre", is("Laptop Dell")))
                .andExpect(jsonPath("$.marca", is("Dell")))
                .andExpect(jsonPath("$.modelo", is("Inspiron 15 3000")))
                .andExpect(jsonPath("$.stock", is(5)))
                .andExpect(jsonPath("$.estadoConservacion", is("BUENO")));

        verify(inventarioService, times(1)).saveInventario(any(InventarioDTO.class));
    }

    @Test
    void createInventario_WithMinimalData_ShouldReturnCreatedInventario() throws Exception {
        // Given
        InventarioDTO minimalInventario = new InventarioDTO();
        minimalInventario.setCodigo("INV-MIN");
        minimalInventario.setNombre("Producto Mínimo");
        minimalInventario.setStock(1);

        InventarioDTO savedInventario = new InventarioDTO();
        savedInventario.setId(3L);
        savedInventario.setCodigo("INV-MIN");
        savedInventario.setNombre("Producto Mínimo");
        savedInventario.setStock(1);

        when(inventarioService.saveInventario(any(InventarioDTO.class))).thenReturn(savedInventario);

        // When & Then
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalInventario)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.codigo", is("INV-MIN")))
                .andExpect(jsonPath("$.nombre", is("Producto Mínimo")))
                .andExpect(jsonPath("$.stock", is(1)));

        verify(inventarioService, times(1)).saveInventario(any(InventarioDTO.class));
    }

    @Test
    void updateInventario_WithValidData_ShouldReturnUpdatedInventario() throws Exception {
        // Given
        InventarioDTO updatedInventario = new InventarioDTO();
        updatedInventario.setId(1L);
        updatedInventario.setCodigo("INV-001");
        updatedInventario.setNombre("Laptop Dell Actualizada");
        updatedInventario.setDescripcion("Laptop Dell Inspiron 15 - Actualizada con SSD");
        updatedInventario.setMarca("Dell");
        updatedInventario.setModelo("Inspiron 15 3000");
        updatedInventario.setStock(8);
        updatedInventario.setEstadoConservacion("EXCELENTE");

        when(inventarioService.updateInventario(eq(1L), any(InventarioDTO.class))).thenReturn(updatedInventario);

        // When & Then
        mockMvc.perform(put("/api/inventario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedInventario)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Laptop Dell Actualizada")))
                .andExpect(jsonPath("$.descripcion", is("Laptop Dell Inspiron 15 - Actualizada con SSD")))
                .andExpect(jsonPath("$.stock", is(8)))
                .andExpect(jsonPath("$.estadoConservacion", is("EXCELENTE")));

        verify(inventarioService, times(1)).updateInventario(eq(1L), any(InventarioDTO.class));
    }

    @Test
    void updateInventario_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(inventarioService.updateInventario(eq(999L), any(InventarioDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/inventario/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioDTO)))
                .andExpect(status().isNotFound());

        verify(inventarioService, times(1)).updateInventario(eq(999L), any(InventarioDTO.class));
    }

    @Test
    void updateInventario_WithStockZero_ShouldReturnUpdatedInventario() throws Exception {
        // Given
        InventarioDTO inventarioSinStock = new InventarioDTO();
        inventarioSinStock.setId(1L);
        inventarioSinStock.setCodigo("INV-001");
        inventarioSinStock.setNombre("Laptop Dell");
        inventarioSinStock.setStock(0); // Stock agotado

        when(inventarioService.updateInventario(eq(1L), any(InventarioDTO.class))).thenReturn(inventarioSinStock);

        // When & Then
        mockMvc.perform(put("/api/inventario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioSinStock)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.stock", is(0)));

        verify(inventarioService, times(1)).updateInventario(eq(1L), any(InventarioDTO.class));
    }

    @Test
    void deleteInventario_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        when(inventarioService.getInventarioById(1L)).thenReturn(inventarioDTO);
        doNothing().when(inventarioService).deleteInventario(1L);

        // When & Then
        mockMvc.perform(delete("/api/inventario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(inventarioService, times(1)).getInventarioById(1L);
        verify(inventarioService, times(1)).deleteInventario(1L);
    }

    @Test
    void deleteInventario_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(inventarioService.getInventarioById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/inventario/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(inventarioService, times(1)).getInventarioById(999L);
        verify(inventarioService, never()).deleteInventario(999L);
    }

    @Test
    void createInventario_WithNegativeStock_ShouldStillCreateInventario() throws Exception {
        // Given - El controller no tiene validación @Valid, acepta stock negativo
        InventarioDTO inventarioStockNegativo = new InventarioDTO();
        inventarioStockNegativo.setCodigo("INV-NEG");
        inventarioStockNegativo.setNombre("Producto Negativo");
        inventarioStockNegativo.setStock(-5);

        InventarioDTO savedInventario = new InventarioDTO();
        savedInventario.setId(4L);
        savedInventario.setCodigo("INV-NEG");
        savedInventario.setNombre("Producto Negativo");
        savedInventario.setStock(-5);

        when(inventarioService.saveInventario(any(InventarioDTO.class))).thenReturn(savedInventario);

        // When & Then
        mockMvc.perform(post("/api/inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inventarioStockNegativo)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.codigo", is("INV-NEG")))
                .andExpect(jsonPath("$.stock", is(-5)));

        verify(inventarioService, times(1)).saveInventario(any(InventarioDTO.class));
    }
}
