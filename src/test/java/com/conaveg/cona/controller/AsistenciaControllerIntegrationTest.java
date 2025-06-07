package com.conaveg.cona.controller;

import com.conaveg.cona.dto.AsistenciaDTO;
import com.conaveg.cona.service.AsistenciaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 * Pruebas de integración para AsistenciaController.
 * Verifica la correcta integración entre el controller y el servicio de asistencias.
 */
@WebMvcTest(AsistenciaController.class)
class AsistenciaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsistenciaService asistenciaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllAsistencias_WithData_ShouldReturnAsistenciasList() throws Exception {
        // Given
        AsistenciaDTO asistencia1 = new AsistenciaDTO();
        asistencia1.setId(1L);
        asistencia1.setEmpleadoId(101L);
        asistencia1.setEntrada(Instant.parse("2025-06-07T08:00:00Z"));
        asistencia1.setTipoRegistro("ENTRADA");
        asistencia1.setMetodoRegistro("BIOMETRICO");

        AsistenciaDTO asistencia2 = new AsistenciaDTO();
        asistencia2.setId(2L);
        asistencia2.setEmpleadoId(102L);
        asistencia2.setEntrada(Instant.parse("2025-06-07T08:30:00Z"));
        asistencia2.setSalida(Instant.parse("2025-06-07T17:30:00Z"));
        asistencia2.setTipoRegistro("COMPLETO");
        asistencia2.setMetodoRegistro("TARJETA");

        List<AsistenciaDTO> asistencias = Arrays.asList(asistencia1, asistencia2);
        when(asistenciaService.getAllAsistencias()).thenReturn(asistencias);

        // When & Then
        mockMvc.perform(get("/api/asistencias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].empleadoId", is(101)))
                .andExpect(jsonPath("$[0].tipoRegistro", is("ENTRADA")))
                .andExpect(jsonPath("$[0].metodoRegistro", is("BIOMETRICO")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].empleadoId", is(102)))
                .andExpect(jsonPath("$[1].tipoRegistro", is("COMPLETO")))
                .andExpect(jsonPath("$[1].metodoRegistro", is("TARJETA")));

        verify(asistenciaService, times(1)).getAllAsistencias();
    }

    @Test
    void getAllAsistencias_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(asistenciaService.getAllAsistencias()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/asistencias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(asistenciaService, times(1)).getAllAsistencias();
    }

    @Test
    void getAsistenciaById_WithValidId_ShouldReturnAsistencia() throws Exception {
        // Given
        AsistenciaDTO asistencia = new AsistenciaDTO();
        asistencia.setId(1L);
        asistencia.setEmpleadoId(101L);
        asistencia.setEntrada(Instant.parse("2025-06-07T08:00:00Z"));
        asistencia.setTipoRegistro("ENTRADA");
        asistencia.setUbicacionRegistro("Oficina Principal");
        asistencia.setObservacion("Entrada normal");

        when(asistenciaService.getAsistenciaById(1L)).thenReturn(asistencia);

        // When & Then
        mockMvc.perform(get("/api/asistencias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.empleadoId", is(101)))
                .andExpect(jsonPath("$.tipoRegistro", is("ENTRADA")))
                .andExpect(jsonPath("$.ubicacionRegistro", is("Oficina Principal")))
                .andExpect(jsonPath("$.observacion", is("Entrada normal")));

        verify(asistenciaService, times(1)).getAsistenciaById(1L);
    }

    @Test
    void getAsistenciaById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(asistenciaService.getAsistenciaById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/asistencias/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(asistenciaService, times(1)).getAsistenciaById(999L);
    }

    @Test
    void createAsistencia_WithValidData_ShouldReturnCreatedAsistencia() throws Exception {
        // Given
        AsistenciaDTO newAsistencia = new AsistenciaDTO();
        newAsistencia.setEmpleadoId(101L);
        newAsistencia.setEntrada(Instant.parse("2025-06-07T08:00:00Z"));
        newAsistencia.setTipoRegistro("ENTRADA");
        newAsistencia.setMetodoRegistro("BIOMETRICO");
        newAsistencia.setUbicacionRegistro("Oficina Principal");

        AsistenciaDTO savedAsistencia = new AsistenciaDTO();
        savedAsistencia.setId(3L);
        savedAsistencia.setEmpleadoId(101L);
        savedAsistencia.setEntrada(Instant.parse("2025-06-07T08:00:00Z"));
        savedAsistencia.setTipoRegistro("ENTRADA");
        savedAsistencia.setMetodoRegistro("BIOMETRICO");
        savedAsistencia.setUbicacionRegistro("Oficina Principal");

        when(asistenciaService.saveAsistencia(any(AsistenciaDTO.class))).thenReturn(savedAsistencia);

        // When & Then
        mockMvc.perform(post("/api/asistencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAsistencia)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.empleadoId", is(101)))
                .andExpect(jsonPath("$.tipoRegistro", is("ENTRADA")))
                .andExpect(jsonPath("$.metodoRegistro", is("BIOMETRICO")))
                .andExpect(jsonPath("$.ubicacionRegistro", is("Oficina Principal")));

        verify(asistenciaService, times(1)).saveAsistencia(any(AsistenciaDTO.class));
    }

    @Test
    void updateAsistencia_WithValidData_ShouldReturnUpdatedAsistencia() throws Exception {
        // Given
        AsistenciaDTO updatedAsistencia = new AsistenciaDTO();
        updatedAsistencia.setId(1L);
        updatedAsistencia.setEmpleadoId(101L);
        updatedAsistencia.setEntrada(Instant.parse("2025-06-07T08:00:00Z"));
        updatedAsistencia.setSalida(Instant.parse("2025-06-07T17:00:00Z"));
        updatedAsistencia.setTipoRegistro("COMPLETO");
        updatedAsistencia.setObservacion("Jornada completada");

        when(asistenciaService.updateAsistencia(eq(1L), any(AsistenciaDTO.class))).thenReturn(updatedAsistencia);

        // When & Then
        mockMvc.perform(put("/api/asistencias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAsistencia)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.empleadoId", is(101)))
                .andExpect(jsonPath("$.tipoRegistro", is("COMPLETO")))
                .andExpect(jsonPath("$.observacion", is("Jornada completada")));

        verify(asistenciaService, times(1)).updateAsistencia(eq(1L), any(AsistenciaDTO.class));
    }

    @Test
    void updateAsistencia_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(asistenciaService.updateAsistencia(eq(999L), any(AsistenciaDTO.class))).thenReturn(null);

        AsistenciaDTO asistenciaUpdate = new AsistenciaDTO();
        asistenciaUpdate.setTipoRegistro("COMPLETO");

        // When & Then
        mockMvc.perform(put("/api/asistencias/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asistenciaUpdate)))
                .andExpect(status().isNotFound());

        verify(asistenciaService, times(1)).updateAsistencia(eq(999L), any(AsistenciaDTO.class));
    }

    @Test
    void deleteAsistencia_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        AsistenciaDTO existingAsistencia = new AsistenciaDTO();
        existingAsistencia.setId(1L);
        when(asistenciaService.getAsistenciaById(1L)).thenReturn(existingAsistencia);
        doNothing().when(asistenciaService).deleteAsistencia(1L);

        // When & Then
        mockMvc.perform(delete("/api/asistencias/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(asistenciaService, times(1)).getAsistenciaById(1L);
        verify(asistenciaService, times(1)).deleteAsistencia(1L);
    }

    @Test
    void deleteAsistencia_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(asistenciaService.getAsistenciaById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/asistencias/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(asistenciaService, times(1)).getAsistenciaById(999L);
    }
}
