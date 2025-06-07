package com.conaveg.cona.controller;

import com.conaveg.cona.dto.CategoriasInventarioDTO;
import com.conaveg.cona.service.CategoriasInventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de integración para CategoriasInventarioController.
 * Verifica la correcta integración entre el controller y el servicio de categorías de inventario.
 */
@WebMvcTest(CategoriasInventarioController.class)
class CategoriasInventarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriasInventarioService categoriasInventarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllCategoriasInventario_WithData_ShouldReturnCategoriasList() throws Exception {
        // Given
        CategoriasInventarioDTO categoria1 = new CategoriasInventarioDTO();
        categoria1.setId(1L);
        categoria1.setNombre("Herramientas");
        categoria1.setDescripcion("Herramientas de construcción y mantenimiento");

        CategoriasInventarioDTO categoria2 = new CategoriasInventarioDTO();
        categoria2.setId(2L);
        categoria2.setNombre("Materiales");
        categoria2.setDescripcion("Materiales de construcción básicos");

        List<CategoriasInventarioDTO> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriasInventarioService.getAllCategoriasInventario()).thenReturn(categorias);

        // When & Then
        mockMvc.perform(get("/api/categorias-inventario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Herramientas")))
                .andExpect(jsonPath("$[0].descripcion", is("Herramientas de construcción y mantenimiento")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Materiales")))
                .andExpect(jsonPath("$[1].descripcion", is("Materiales de construcción básicos")));

        verify(categoriasInventarioService, times(1)).getAllCategoriasInventario();
    }

    @Test
    void getAllCategoriasInventario_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(categoriasInventarioService.getAllCategoriasInventario()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/categorias-inventario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(categoriasInventarioService, times(1)).getAllCategoriasInventario();
    }

    @Test
    void getCategoriasInventarioById_WithValidId_ShouldReturnCategoria() throws Exception {
        // Given
        CategoriasInventarioDTO categoria = new CategoriasInventarioDTO();
        categoria.setId(1L);
        categoria.setNombre("Herramientas");
        categoria.setDescripcion("Herramientas de construcción y mantenimiento");

        when(categoriasInventarioService.getCategoriasInventarioById(1L)).thenReturn(categoria);

        // When & Then
        mockMvc.perform(get("/api/categorias-inventario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Herramientas")))
                .andExpect(jsonPath("$.descripcion", is("Herramientas de construcción y mantenimiento")));

        verify(categoriasInventarioService, times(1)).getCategoriasInventarioById(1L);
    }

    @Test
    void getCategoriasInventarioById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(categoriasInventarioService.getCategoriasInventarioById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/categorias-inventario/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoriasInventarioService, times(1)).getCategoriasInventarioById(999L);
    }

    @Test
    void createCategoriasInventario_WithValidData_ShouldReturnCreatedCategoria() throws Exception {
        // Given
        CategoriasInventarioDTO newCategoria = new CategoriasInventarioDTO();
        newCategoria.setNombre("Equipos");
        newCategoria.setDescripcion("Equipos especializados");

        CategoriasInventarioDTO savedCategoria = new CategoriasInventarioDTO();
        savedCategoria.setId(3L);
        savedCategoria.setNombre("Equipos");
        savedCategoria.setDescripcion("Equipos especializados");

        when(categoriasInventarioService.saveCategoriasInventario(any(CategoriasInventarioDTO.class))).thenReturn(savedCategoria);

        // When & Then
        mockMvc.perform(post("/api/categorias-inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoria)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Equipos")))
                .andExpect(jsonPath("$.descripcion", is("Equipos especializados")));

        verify(categoriasInventarioService, times(1)).saveCategoriasInventario(any(CategoriasInventarioDTO.class));
    }

    @Test
    void updateCategoriasInventario_WithValidData_ShouldReturnUpdatedCategoria() throws Exception {
        // Given
        CategoriasInventarioDTO updatedCategoria = new CategoriasInventarioDTO();
        updatedCategoria.setId(1L);
        updatedCategoria.setNombre("Herramientas Avanzadas");
        updatedCategoria.setDescripcion("Herramientas especializadas y avanzadas");

        when(categoriasInventarioService.updateCategoriasInventario(eq(1L), any(CategoriasInventarioDTO.class))).thenReturn(updatedCategoria);

        // When & Then
        mockMvc.perform(put("/api/categorias-inventario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Herramientas Avanzadas")))
                .andExpect(jsonPath("$.descripcion", is("Herramientas especializadas y avanzadas")));

        verify(categoriasInventarioService, times(1)).updateCategoriasInventario(eq(1L), any(CategoriasInventarioDTO.class));
    }

    @Test
    void updateCategoriasInventario_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(categoriasInventarioService.updateCategoriasInventario(eq(999L), any(CategoriasInventarioDTO.class))).thenReturn(null);

        CategoriasInventarioDTO categoriaUpdate = new CategoriasInventarioDTO();
        categoriaUpdate.setNombre("Categoría No Encontrada");

        // When & Then
        mockMvc.perform(put("/api/categorias-inventario/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoriaUpdate)))
                .andExpect(status().isNotFound());

        verify(categoriasInventarioService, times(1)).updateCategoriasInventario(eq(999L), any(CategoriasInventarioDTO.class));
    }

    @Test
    void deleteCategoriasInventario_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        CategoriasInventarioDTO existingCategoria = new CategoriasInventarioDTO();
        existingCategoria.setId(1L);
        when(categoriasInventarioService.getCategoriasInventarioById(1L)).thenReturn(existingCategoria);
        doNothing().when(categoriasInventarioService).deleteCategoriasInventario(1L);

        // When & Then
        mockMvc.perform(delete("/api/categorias-inventario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoriasInventarioService, times(1)).getCategoriasInventarioById(1L);
        verify(categoriasInventarioService, times(1)).deleteCategoriasInventario(1L);
    }

    @Test
    void deleteCategoriasInventario_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(categoriasInventarioService.getCategoriasInventarioById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/categorias-inventario/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(categoriasInventarioService, times(1)).getCategoriasInventarioById(999L);
    }
}
