package com.conaveg.cona.controller;

import com.conaveg.cona.dto.AsignacionesProyectosEmpleadoDTO;
import com.conaveg.cona.service.AsignacionesProyectosEmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 * Pruebas de integración para AsignacionesProyectosEmpleadoController.
 * Verifica la correcta integración entre el controller y el servicio de asignaciones de proyectos a empleados.
 */
@WebMvcTest(AsignacionesProyectosEmpleadoController.class)
class AsignacionesProyectosEmpleadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AsignacionesProyectosEmpleadoService asignacionesProyectosEmpleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllAsignacionesProyectosEmpleado_WithData_ShouldReturnAsignacionesList() throws Exception {
        // Given
        AsignacionesProyectosEmpleadoDTO asignacion1 = new AsignacionesProyectosEmpleadoDTO();
        asignacion1.setId(1L);
        asignacion1.setEmpleadoId(101L);
        asignacion1.setProyectoId(201L);
        asignacion1.setFechaAsignacion(LocalDate.of(2025, 6, 1));
        asignacion1.setFechaFinAsignacion(LocalDate.of(2025, 12, 1));
        asignacion1.setRol("OPERARIO");

        AsignacionesProyectosEmpleadoDTO asignacion2 = new AsignacionesProyectosEmpleadoDTO();
        asignacion2.setId(2L);
        asignacion2.setEmpleadoId(102L);
        asignacion2.setProyectoId(202L);
        asignacion2.setFechaAsignacion(LocalDate.of(2025, 6, 5));
        asignacion2.setRol("SUPERVISOR");

        List<AsignacionesProyectosEmpleadoDTO> asignaciones = Arrays.asList(asignacion1, asignacion2);
        when(asignacionesProyectosEmpleadoService.getAllAsignacionesProyectosEmpleado()).thenReturn(asignaciones);

        // When & Then
        mockMvc.perform(get("/api/asignaciones-proyectos-empleado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].empleadoId", is(101)))
                .andExpect(jsonPath("$[0].proyectoId", is(201)))
                .andExpect(jsonPath("$[0].rol", is("OPERARIO")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].empleadoId", is(102)))
                .andExpect(jsonPath("$[1].proyectoId", is(202)))
                .andExpect(jsonPath("$[1].rol", is("SUPERVISOR")));

        verify(asignacionesProyectosEmpleadoService, times(1)).getAllAsignacionesProyectosEmpleado();
    }

    @Test
    void getAllAsignacionesProyectosEmpleado_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(asignacionesProyectosEmpleadoService.getAllAsignacionesProyectosEmpleado()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/asignaciones-proyectos-empleado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(asignacionesProyectosEmpleadoService, times(1)).getAllAsignacionesProyectosEmpleado();
    }

    @Test
    void getAsignacionesProyectosEmpleadoById_WithValidId_ShouldReturnAsignacion() throws Exception {
        // Given
        AsignacionesProyectosEmpleadoDTO asignacion = new AsignacionesProyectosEmpleadoDTO();
        asignacion.setId(1L);
        asignacion.setEmpleadoId(101L);
        asignacion.setProyectoId(201L);
        asignacion.setFechaAsignacion(LocalDate.of(2025, 6, 1));
        asignacion.setFechaFinAsignacion(LocalDate.of(2025, 12, 1));
        asignacion.setRol("OPERARIO");

        when(asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(1L)).thenReturn(asignacion);

        // When & Then
        mockMvc.perform(get("/api/asignaciones-proyectos-empleado/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.empleadoId", is(101)))
                .andExpect(jsonPath("$.proyectoId", is(201)))
                .andExpect(jsonPath("$.rol", is("OPERARIO")));

        verify(asignacionesProyectosEmpleadoService, times(1)).getAsignacionesProyectosEmpleadoById(1L);
    }

    @Test
    void getAsignacionesProyectosEmpleadoById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/asignaciones-proyectos-empleado/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(asignacionesProyectosEmpleadoService, times(1)).getAsignacionesProyectosEmpleadoById(999L);
    }

    @Test
    void createAsignacionesProyectosEmpleado_WithValidData_ShouldReturnCreatedAsignacion() throws Exception {
        // Given
        AsignacionesProyectosEmpleadoDTO newAsignacion = new AsignacionesProyectosEmpleadoDTO();
        newAsignacion.setEmpleadoId(103L);
        newAsignacion.setProyectoId(203L);
        newAsignacion.setFechaAsignacion(LocalDate.of(2025, 6, 7));
        newAsignacion.setRol("TECNICO");

        AsignacionesProyectosEmpleadoDTO savedAsignacion = new AsignacionesProyectosEmpleadoDTO();
        savedAsignacion.setId(3L);
        savedAsignacion.setEmpleadoId(103L);
        savedAsignacion.setProyectoId(203L);
        savedAsignacion.setFechaAsignacion(LocalDate.of(2025, 6, 7));
        savedAsignacion.setRol("TECNICO");

        when(asignacionesProyectosEmpleadoService.saveAsignacionesProyectosEmpleado(any(AsignacionesProyectosEmpleadoDTO.class))).thenReturn(savedAsignacion);

        // When & Then
        mockMvc.perform(post("/api/asignaciones-proyectos-empleado")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newAsignacion)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.empleadoId", is(103)))
                .andExpect(jsonPath("$.proyectoId", is(203)))
                .andExpect(jsonPath("$.rol", is("TECNICO")));

        verify(asignacionesProyectosEmpleadoService, times(1)).saveAsignacionesProyectosEmpleado(any(AsignacionesProyectosEmpleadoDTO.class));
    }

    @Test
    void updateAsignacionesProyectosEmpleado_WithValidData_ShouldReturnUpdatedAsignacion() throws Exception {
        // Given
        AsignacionesProyectosEmpleadoDTO updatedAsignacion = new AsignacionesProyectosEmpleadoDTO();
        updatedAsignacion.setId(1L);
        updatedAsignacion.setEmpleadoId(101L);
        updatedAsignacion.setProyectoId(201L);
        updatedAsignacion.setRol("SUPERVISOR");
        updatedAsignacion.setFechaFinAsignacion(LocalDate.of(2025, 10, 1));

        when(asignacionesProyectosEmpleadoService.updateAsignacionesProyectosEmpleado(eq(1L), any(AsignacionesProyectosEmpleadoDTO.class))).thenReturn(updatedAsignacion);

        // When & Then
        mockMvc.perform(put("/api/asignaciones-proyectos-empleado/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAsignacion)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.rol", is("SUPERVISOR")));

        verify(asignacionesProyectosEmpleadoService, times(1)).updateAsignacionesProyectosEmpleado(eq(1L), any(AsignacionesProyectosEmpleadoDTO.class));
    }

    @Test
    void updateAsignacionesProyectosEmpleado_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(asignacionesProyectosEmpleadoService.updateAsignacionesProyectosEmpleado(eq(999L), any(AsignacionesProyectosEmpleadoDTO.class))).thenReturn(null);

        AsignacionesProyectosEmpleadoDTO asignacionUpdate = new AsignacionesProyectosEmpleadoDTO();
        asignacionUpdate.setRol("SUPERVISOR");

        // When & Then
        mockMvc.perform(put("/api/asignaciones-proyectos-empleado/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignacionUpdate)))
                .andExpect(status().isNotFound());

        verify(asignacionesProyectosEmpleadoService, times(1)).updateAsignacionesProyectosEmpleado(eq(999L), any(AsignacionesProyectosEmpleadoDTO.class));
    }

    @Test
    void deleteAsignacionesProyectosEmpleado_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        AsignacionesProyectosEmpleadoDTO existingAsignacion = new AsignacionesProyectosEmpleadoDTO();
        existingAsignacion.setId(1L);
        when(asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(1L)).thenReturn(existingAsignacion);
        doNothing().when(asignacionesProyectosEmpleadoService).deleteAsignacionesProyectosEmpleado(1L);

        // When & Then
        mockMvc.perform(delete("/api/asignaciones-proyectos-empleado/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(asignacionesProyectosEmpleadoService, times(1)).getAsignacionesProyectosEmpleadoById(1L);
        verify(asignacionesProyectosEmpleadoService, times(1)).deleteAsignacionesProyectosEmpleado(1L);
    }

    @Test
    void deleteAsignacionesProyectosEmpleado_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/asignaciones-proyectos-empleado/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(asignacionesProyectosEmpleadoService, times(1)).getAsignacionesProyectosEmpleadoById(999L);
    }
}
