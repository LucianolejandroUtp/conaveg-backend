package com.conaveg.cona.controller;

import com.conaveg.cona.dto.ProveedorDTO;
import com.conaveg.cona.service.ProveedorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para ProveedorController
 * Valida el funcionamiento de los endpoints REST y la interacción con el servicio
 */
@WebMvcTest(ProveedorController.class)
class ProveedorControllerIntegrationTest {    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProveedorService proveedorService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProveedorDTO proveedorDTO;
    private ProveedorDTO proveedorDTO2;

    @BeforeEach
    void setUp() {
        proveedorDTO = new ProveedorDTO();
        proveedorDTO.setId(1L);
        proveedorDTO.setRuc("20123456789");
        proveedorDTO.setRazonSocial("Empresa ABC S.A.C.");
        proveedorDTO.setDireccion("Av. Industrial 123, Lima");
        proveedorDTO.setTelefono("01-2345678");

        proveedorDTO2 = new ProveedorDTO();
        proveedorDTO2.setId(2L);
        proveedorDTO2.setRuc("20987654321");
        proveedorDTO2.setRazonSocial("Distribuidora XYZ E.I.R.L.");
        proveedorDTO2.setDireccion("Jr. Comercio 456, Arequipa");
        proveedorDTO2.setTelefono("054-876543");
    }

    @Test
    void getAllProveedores_ShouldReturnProveedoresList() throws Exception {
        // Given
        List<ProveedorDTO> proveedores = Arrays.asList(proveedorDTO, proveedorDTO2);
        when(proveedorService.getAllProveedores()).thenReturn(proveedores);

        // When & Then
        mockMvc.perform(get("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].ruc", is("20123456789")))
                .andExpect(jsonPath("$[0].razonSocial", is("Empresa ABC S.A.C.")))
                .andExpect(jsonPath("$[0].direccion", is("Av. Industrial 123, Lima")))
                .andExpect(jsonPath("$[0].telefono", is("01-2345678")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].ruc", is("20987654321")))
                .andExpect(jsonPath("$[1].razonSocial", is("Distribuidora XYZ E.I.R.L.")))
                .andExpect(jsonPath("$[1].direccion", is("Jr. Comercio 456, Arequipa")))
                .andExpect(jsonPath("$[1].telefono", is("054-876543")));

        verify(proveedorService, times(1)).getAllProveedores();
    }

    @Test
    void getAllProveedores_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(proveedorService.getAllProveedores()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(proveedorService, times(1)).getAllProveedores();
    }

    @Test
    void getProveedorById_WithValidId_ShouldReturnProveedor() throws Exception {
        // Given
        when(proveedorService.getProveedorById(1L)).thenReturn(proveedorDTO);

        // When & Then
        mockMvc.perform(get("/api/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.ruc", is("20123456789")))
                .andExpect(jsonPath("$.razonSocial", is("Empresa ABC S.A.C.")))
                .andExpect(jsonPath("$.direccion", is("Av. Industrial 123, Lima")))
                .andExpect(jsonPath("$.telefono", is("01-2345678")));

        verify(proveedorService, times(1)).getProveedorById(1L);
    }

    @Test
    void getProveedorById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(proveedorService.getProveedorById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/proveedores/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(proveedorService, times(1)).getProveedorById(999L);
    }

    @Test
    void createProveedor_WithValidData_ShouldReturnCreatedProveedor() throws Exception {
        // Given
        when(proveedorService.saveProveedor(any(ProveedorDTO.class))).thenReturn(proveedorDTO);

        // When & Then
        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedorDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.ruc", is("20123456789")))
                .andExpect(jsonPath("$.razonSocial", is("Empresa ABC S.A.C.")))
                .andExpect(jsonPath("$.direccion", is("Av. Industrial 123, Lima")))
                .andExpect(jsonPath("$.telefono", is("01-2345678")));

        verify(proveedorService, times(1)).saveProveedor(any(ProveedorDTO.class));
    }

    @Test
    void createProveedor_WithMinimalData_ShouldReturnCreatedProveedor() throws Exception {
        // Given
        ProveedorDTO minimalProveedor = new ProveedorDTO();
        minimalProveedor.setRuc("20555666777");
        minimalProveedor.setRazonSocial("Proveedor Mínimo S.A.");
        
        ProveedorDTO savedProveedor = new ProveedorDTO();
        savedProveedor.setId(3L);
        savedProveedor.setRuc("20555666777");
        savedProveedor.setRazonSocial("Proveedor Mínimo S.A.");
        
        when(proveedorService.saveProveedor(any(ProveedorDTO.class))).thenReturn(savedProveedor);

        // When & Then
        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalProveedor)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.ruc", is("20555666777")))
                .andExpect(jsonPath("$.razonSocial", is("Proveedor Mínimo S.A.")));

        verify(proveedorService, times(1)).saveProveedor(any(ProveedorDTO.class));
    }

    @Test
    void updateProveedor_WithValidData_ShouldReturnUpdatedProveedor() throws Exception {
        // Given
        ProveedorDTO updatedProveedor = new ProveedorDTO();
        updatedProveedor.setId(1L);
        updatedProveedor.setRuc("20123456789");
        updatedProveedor.setRazonSocial("Empresa ABC S.A.C. - Actualizada");
        updatedProveedor.setDireccion("Av. Industrial 789, Lima");
        updatedProveedor.setTelefono("01-9876543");
        
        when(proveedorService.updateProveedor(eq(1L), any(ProveedorDTO.class))).thenReturn(updatedProveedor);

        // When & Then
        mockMvc.perform(put("/api/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProveedor)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.ruc", is("20123456789")))
                .andExpect(jsonPath("$.razonSocial", is("Empresa ABC S.A.C. - Actualizada")))
                .andExpect(jsonPath("$.direccion", is("Av. Industrial 789, Lima")))
                .andExpect(jsonPath("$.telefono", is("01-9876543")));

        verify(proveedorService, times(1)).updateProveedor(eq(1L), any(ProveedorDTO.class));
    }

    @Test
    void updateProveedor_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(proveedorService.updateProveedor(eq(999L), any(ProveedorDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/proveedores/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedorDTO)))
                .andExpect(status().isNotFound());

        verify(proveedorService, times(1)).updateProveedor(eq(999L), any(ProveedorDTO.class));
    }

    @Test
    void deleteProveedor_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        when(proveedorService.deleteProveedor(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/proveedores/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(proveedorService, times(1)).deleteProveedor(1L);
    }

    @Test
    void deleteProveedor_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(proveedorService.deleteProveedor(999L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/proveedores/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(proveedorService, times(1)).deleteProveedor(999L);
    }

    @Test
    void createProveedor_WithEmptyRuc_ShouldStillCreateProveedor() throws Exception {
        // Given - El controller no tiene validación @Valid, acepta datos vacíos
        ProveedorDTO proveedorConRucVacio = new ProveedorDTO();
        proveedorConRucVacio.setRuc("");
        proveedorConRucVacio.setRazonSocial("Empresa Sin RUC");
          ProveedorDTO savedProveedor = new ProveedorDTO();
        savedProveedor.setId(4L);
        savedProveedor.setRuc("");
        savedProveedor.setRazonSocial("Empresa Sin RUC");
        when(proveedorService.saveProveedor(any(ProveedorDTO.class))).thenReturn(savedProveedor);

        // When & Then
        mockMvc.perform(post("/api/proveedores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proveedorConRucVacio)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.ruc", is("")))
                .andExpect(jsonPath("$.razonSocial", is("Empresa Sin RUC")));

        verify(proveedorService, times(1)).saveProveedor(any(ProveedorDTO.class));
    }
}
