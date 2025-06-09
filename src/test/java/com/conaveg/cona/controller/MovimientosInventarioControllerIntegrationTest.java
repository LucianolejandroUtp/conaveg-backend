package com.conaveg.cona.controller;

import com.conaveg.cona.dto.MovimientosInventarioDTO;
import com.conaveg.cona.service.MovimientosInventarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Pruebas de integración para MovimientosInventarioController.
 * Verifica la correcta integración entre el controller y el servicio de movimientos de inventario.
 */
@WebMvcTest(MovimientosInventarioController.class)
class MovimientosInventarioControllerIntegrationTest {    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovimientosInventarioService movimientosInventarioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllMovimientosInventario_WithData_ShouldReturnMovimientosList() throws Exception {
        // Given
        MovimientosInventarioDTO movimiento1 = new MovimientosInventarioDTO();
        movimiento1.setId(1L);
        movimiento1.setInventarioId(101L);
        movimiento1.setEmpleadoIdAsigna(201L);
        movimiento1.setProyectoId(301L);
        movimiento1.setTipoMovimiento("ENTRADA");
        movimiento1.setCantidad(50);
        movimiento1.setFechaMovimiento(Instant.parse("2025-06-07T10:00:00Z"));
        movimiento1.setObservacion("Entrada inicial de inventario");

        MovimientosInventarioDTO movimiento2 = new MovimientosInventarioDTO();
        movimiento2.setId(2L);
        movimiento2.setInventarioId(102L);
        movimiento2.setEmpleadoIdAsigna(201L);
        movimiento2.setEmpleadoIdRecibe(202L);
        movimiento2.setProyectoId(302L);
        movimiento2.setTipoMovimiento("SALIDA");
        movimiento2.setCantidad(25);
        movimiento2.setFechaMovimiento(Instant.parse("2025-06-07T14:00:00Z"));
        movimiento2.setObservacion("Salida para proyecto");

        List<MovimientosInventarioDTO> movimientos = Arrays.asList(movimiento1, movimiento2);
        when(movimientosInventarioService.getAllMovimientosInventario()).thenReturn(movimientos);

        // When & Then
        mockMvc.perform(get("/api/movimientos-inventario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].inventarioId", is(101)))
                .andExpect(jsonPath("$[0].tipoMovimiento", is("ENTRADA")))
                .andExpect(jsonPath("$[0].cantidad", is(50)))
                .andExpect(jsonPath("$[0].observacion", is("Entrada inicial de inventario")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].inventarioId", is(102)))
                .andExpect(jsonPath("$[1].tipoMovimiento", is("SALIDA")))
                .andExpect(jsonPath("$[1].cantidad", is(25)))
                .andExpect(jsonPath("$[1].observacion", is("Salida para proyecto")));

        verify(movimientosInventarioService, times(1)).getAllMovimientosInventario();
    }

    @Test
    void getAllMovimientosInventario_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(movimientosInventarioService.getAllMovimientosInventario()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/movimientos-inventario")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(movimientosInventarioService, times(1)).getAllMovimientosInventario();
    }

    @Test
    void getMovimientosInventarioById_WithValidId_ShouldReturnMovimiento() throws Exception {
        // Given
        MovimientosInventarioDTO movimiento = new MovimientosInventarioDTO();
        movimiento.setId(1L);
        movimiento.setInventarioId(101L);
        movimiento.setEmpleadoIdAsigna(201L);
        movimiento.setProyectoId(301L);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setCantidad(50);
        movimiento.setObservacion("Entrada inicial de inventario");

        when(movimientosInventarioService.getMovimientosInventarioById(1L)).thenReturn(movimiento);

        // When & Then
        mockMvc.perform(get("/api/movimientos-inventario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.inventarioId", is(101)))
                .andExpect(jsonPath("$.empleadoIdAsigna", is(201)))
                .andExpect(jsonPath("$.proyectoId", is(301)))
                .andExpect(jsonPath("$.tipoMovimiento", is("ENTRADA")))
                .andExpect(jsonPath("$.cantidad", is(50)));

        verify(movimientosInventarioService, times(1)).getMovimientosInventarioById(1L);
    }

    @Test
    void getMovimientosInventarioById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(movimientosInventarioService.getMovimientosInventarioById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/movimientos-inventario/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movimientosInventarioService, times(1)).getMovimientosInventarioById(999L);
    }

    @Test
    void createMovimientosInventario_WithValidData_ShouldReturnCreatedMovimiento() throws Exception {
        // Given
        MovimientosInventarioDTO newMovimiento = new MovimientosInventarioDTO();
        newMovimiento.setInventarioId(101L);
        newMovimiento.setEmpleadoIdAsigna(201L);
        newMovimiento.setProyectoId(301L);
        newMovimiento.setTipoMovimiento("ENTRADA");
        newMovimiento.setCantidad(75);
        newMovimiento.setObservacion("Nueva entrada de materiales");

        MovimientosInventarioDTO savedMovimiento = new MovimientosInventarioDTO();
        savedMovimiento.setId(3L);
        savedMovimiento.setInventarioId(101L);
        savedMovimiento.setEmpleadoIdAsigna(201L);
        savedMovimiento.setProyectoId(301L);
        savedMovimiento.setTipoMovimiento("ENTRADA");
        savedMovimiento.setCantidad(75);
        savedMovimiento.setFechaMovimiento(Instant.now());
        savedMovimiento.setObservacion("Nueva entrada de materiales");

        when(movimientosInventarioService.saveMovimientosInventario(any(MovimientosInventarioDTO.class))).thenReturn(savedMovimiento);

        // When & Then
        mockMvc.perform(post("/api/movimientos-inventario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMovimiento)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.inventarioId", is(101)))
                .andExpect(jsonPath("$.tipoMovimiento", is("ENTRADA")))
                .andExpect(jsonPath("$.cantidad", is(75)))
                .andExpect(jsonPath("$.observacion", is("Nueva entrada de materiales")));

        verify(movimientosInventarioService, times(1)).saveMovimientosInventario(any(MovimientosInventarioDTO.class));
    }

    @Test
    void updateMovimientosInventario_WithValidData_ShouldReturnUpdatedMovimiento() throws Exception {
        // Given
        MovimientosInventarioDTO updatedMovimiento = new MovimientosInventarioDTO();
        updatedMovimiento.setId(1L);
        updatedMovimiento.setInventarioId(101L);
        updatedMovimiento.setTipoMovimiento("ENTRADA");
        updatedMovimiento.setCantidad(100);
        updatedMovimiento.setObservacion("Cantidad corregida");

        when(movimientosInventarioService.updateMovimientosInventario(eq(1L), any(MovimientosInventarioDTO.class))).thenReturn(updatedMovimiento);

        // When & Then
        mockMvc.perform(put("/api/movimientos-inventario/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedMovimiento)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.cantidad", is(100)))
                .andExpect(jsonPath("$.observacion", is("Cantidad corregida")));

        verify(movimientosInventarioService, times(1)).updateMovimientosInventario(eq(1L), any(MovimientosInventarioDTO.class));
    }

    @Test
    void updateMovimientosInventario_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(movimientosInventarioService.updateMovimientosInventario(eq(999L), any(MovimientosInventarioDTO.class))).thenReturn(null);

        MovimientosInventarioDTO movimientoUpdate = new MovimientosInventarioDTO();
        movimientoUpdate.setCantidad(50);

        // When & Then
        mockMvc.perform(put("/api/movimientos-inventario/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movimientoUpdate)))
                .andExpect(status().isNotFound());

        verify(movimientosInventarioService, times(1)).updateMovimientosInventario(eq(999L), any(MovimientosInventarioDTO.class));
    }

    @Test
    void deleteMovimientosInventario_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        MovimientosInventarioDTO existingMovimiento = new MovimientosInventarioDTO();
        existingMovimiento.setId(1L);
        when(movimientosInventarioService.getMovimientosInventarioById(1L)).thenReturn(existingMovimiento);
        doNothing().when(movimientosInventarioService).deleteMovimientosInventario(1L);

        // When & Then
        mockMvc.perform(delete("/api/movimientos-inventario/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(movimientosInventarioService, times(1)).getMovimientosInventarioById(1L);
        verify(movimientosInventarioService, times(1)).deleteMovimientosInventario(1L);
    }

    @Test
    void deleteMovimientosInventario_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(movimientosInventarioService.getMovimientosInventarioById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/movimientos-inventario/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(movimientosInventarioService, times(1)).getMovimientosInventarioById(999L);
    }
}
