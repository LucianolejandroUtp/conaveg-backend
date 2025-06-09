package com.conaveg.cona.controller;

import com.conaveg.cona.dto.ProyectoDTO;
import com.conaveg.cona.service.ProyectoService;
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
 * Pruebas de integración para ProyectoController
 * Valida el funcionamiento de los endpoints REST y la interacción con el servicio
 */
@WebMvcTest(ProyectoController.class)
class ProyectoControllerIntegrationTest {    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProyectoService proyectoService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProyectoDTO proyectoDTO;
    private ProyectoDTO proyectoDTO2;

    @BeforeEach
    void setUp() {
        proyectoDTO = new ProyectoDTO();
        proyectoDTO.setId(1L);
        proyectoDTO.setNombre("Construcción de Centro de Salud");
        proyectoDTO.setDescripcion("Proyecto para la construcción de un centro de salud comunitario");
        proyectoDTO.setUbicacion("Lima Norte - Comas");
        proyectoDTO.setFechaInicio(LocalDate.of(2024, 1, 15));
        proyectoDTO.setFechaFin(LocalDate.of(2024, 12, 31));
        proyectoDTO.setEstadoProyecto("EN_PROGRESO");

        proyectoDTO2 = new ProyectoDTO();
        proyectoDTO2.setId(2L);
        proyectoDTO2.setNombre("Instalación de Sistema Eléctrico");
        proyectoDTO2.setDescripcion("Proyecto de instalación y modernización del sistema eléctrico");
        proyectoDTO2.setUbicacion("Arequipa - Cercado");
        proyectoDTO2.setFechaInicio(LocalDate.of(2024, 3, 1));
        proyectoDTO2.setFechaFin(LocalDate.of(2024, 8, 30));
        proyectoDTO2.setEstadoProyecto("PLANIFICACION");
    }

    @Test
    void getAllProyectos_ShouldReturnProyectosList() throws Exception {
        // Given
        List<ProyectoDTO> proyectos = Arrays.asList(proyectoDTO, proyectoDTO2);
        when(proyectoService.getAllProyectos()).thenReturn(proyectos);

        // When & Then
        mockMvc.perform(get("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("Construcción de Centro de Salud")))
                .andExpect(jsonPath("$[0].descripcion", is("Proyecto para la construcción de un centro de salud comunitario")))
                .andExpect(jsonPath("$[0].ubicacion", is("Lima Norte - Comas")))
                .andExpect(jsonPath("$[0].estadoProyecto", is("EN_PROGRESO")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("Instalación de Sistema Eléctrico")))
                .andExpect(jsonPath("$[1].descripcion", is("Proyecto de instalación y modernización del sistema eléctrico")))
                .andExpect(jsonPath("$[1].ubicacion", is("Arequipa - Cercado")))
                .andExpect(jsonPath("$[1].estadoProyecto", is("PLANIFICACION")));

        verify(proyectoService, times(1)).getAllProyectos();
    }

    @Test
    void getAllProyectos_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(proyectoService.getAllProyectos()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(proyectoService, times(1)).getAllProyectos();
    }

    @Test
    void getProyectoById_WithValidId_ShouldReturnProyecto() throws Exception {
        // Given
        when(proyectoService.getProyectoById(1L)).thenReturn(proyectoDTO);

        // When & Then
        mockMvc.perform(get("/api/proyectos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Construcción de Centro de Salud")))
                .andExpect(jsonPath("$.descripcion", is("Proyecto para la construcción de un centro de salud comunitario")))
                .andExpect(jsonPath("$.ubicacion", is("Lima Norte - Comas")))
                .andExpect(jsonPath("$.fechaInicio", is("2024-01-15")))
                .andExpect(jsonPath("$.fechaFin", is("2024-12-31")))
                .andExpect(jsonPath("$.estadoProyecto", is("EN_PROGRESO")));

        verify(proyectoService, times(1)).getProyectoById(1L);
    }

    @Test
    void getProyectoById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(proyectoService.getProyectoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/proyectos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(proyectoService, times(1)).getProyectoById(999L);
    }

    @Test
    void createProyecto_WithValidData_ShouldReturnCreatedProyecto() throws Exception {
        // Given
        when(proyectoService.saveProyecto(any(ProyectoDTO.class))).thenReturn(proyectoDTO);

        // When & Then
        mockMvc.perform(post("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proyectoDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Construcción de Centro de Salud")))
                .andExpect(jsonPath("$.descripcion", is("Proyecto para la construcción de un centro de salud comunitario")))
                .andExpect(jsonPath("$.ubicacion", is("Lima Norte - Comas")))
                .andExpect(jsonPath("$.estadoProyecto", is("EN_PROGRESO")));

        verify(proyectoService, times(1)).saveProyecto(any(ProyectoDTO.class));
    }

    @Test
    void createProyecto_WithMinimalData_ShouldReturnCreatedProyecto() throws Exception {
        // Given
        ProyectoDTO minimalProyecto = new ProyectoDTO();
        minimalProyecto.setNombre("Proyecto Mínimo");
        minimalProyecto.setUbicacion("Lima");

        ProyectoDTO savedProyecto = new ProyectoDTO();
        savedProyecto.setId(3L);
        savedProyecto.setNombre("Proyecto Mínimo");
        savedProyecto.setUbicacion("Lima");
        savedProyecto.setEstadoProyecto("PLANIFICACION");

        when(proyectoService.saveProyecto(any(ProyectoDTO.class))).thenReturn(savedProyecto);

        // When & Then
        mockMvc.perform(post("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalProyecto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("Proyecto Mínimo")))
                .andExpect(jsonPath("$.ubicacion", is("Lima")))
                .andExpect(jsonPath("$.estadoProyecto", is("PLANIFICACION")));

        verify(proyectoService, times(1)).saveProyecto(any(ProyectoDTO.class));
    }

    @Test
    void createProyecto_WithFutureDates_ShouldReturnCreatedProyecto() throws Exception {
        // Given
        ProyectoDTO futureProyecto = new ProyectoDTO();
        futureProyecto.setNombre("Proyecto Futuro");
        futureProyecto.setDescripcion("Proyecto planificado para el futuro");
        futureProyecto.setUbicacion("Cusco");
        futureProyecto.setFechaInicio(LocalDate.of(2025, 6, 1));
        futureProyecto.setFechaFin(LocalDate.of(2025, 12, 31));

        ProyectoDTO savedProyecto = new ProyectoDTO();
        savedProyecto.setId(4L);
        savedProyecto.setNombre("Proyecto Futuro");
        savedProyecto.setDescripcion("Proyecto planificado para el futuro");
        savedProyecto.setUbicacion("Cusco");
        savedProyecto.setFechaInicio(LocalDate.of(2025, 6, 1));
        savedProyecto.setFechaFin(LocalDate.of(2025, 12, 31));
        savedProyecto.setEstadoProyecto("PLANIFICACION");

        when(proyectoService.saveProyecto(any(ProyectoDTO.class))).thenReturn(savedProyecto);

        // When & Then
        mockMvc.perform(post("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(futureProyecto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.nombre", is("Proyecto Futuro")))
                .andExpect(jsonPath("$.fechaInicio", is("2025-06-01")))
                .andExpect(jsonPath("$.fechaFin", is("2025-12-31")))
                .andExpect(jsonPath("$.estadoProyecto", is("PLANIFICACION")));

        verify(proyectoService, times(1)).saveProyecto(any(ProyectoDTO.class));
    }

    @Test
    void updateProyecto_WithValidData_ShouldReturnUpdatedProyecto() throws Exception {
        // Given
        ProyectoDTO updatedProyecto = new ProyectoDTO();
        updatedProyecto.setId(1L);
        updatedProyecto.setNombre("Construcción de Centro de Salud - Actualizado");
        updatedProyecto.setDescripcion("Proyecto actualizado con nuevos requerimientos");
        updatedProyecto.setUbicacion("Lima Norte - Comas");
        updatedProyecto.setFechaInicio(LocalDate.of(2024, 1, 15));
        updatedProyecto.setFechaFin(LocalDate.of(2025, 6, 30));
        updatedProyecto.setEstadoProyecto("EN_PROGRESO");

        when(proyectoService.updateProyecto(eq(1L), any(ProyectoDTO.class))).thenReturn(updatedProyecto);

        // When & Then
        mockMvc.perform(put("/api/proyectos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProyecto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("Construcción de Centro de Salud - Actualizado")))
                .andExpect(jsonPath("$.descripcion", is("Proyecto actualizado con nuevos requerimientos")))
                .andExpect(jsonPath("$.fechaFin", is("2025-06-30")))
                .andExpect(jsonPath("$.estadoProyecto", is("EN_PROGRESO")));

        verify(proyectoService, times(1)).updateProyecto(eq(1L), any(ProyectoDTO.class));
    }

    @Test
    void updateProyecto_ChangeEstado_ShouldReturnUpdatedProyecto() throws Exception {
        // Given
        ProyectoDTO completedProyecto = new ProyectoDTO();
        completedProyecto.setId(1L);
        completedProyecto.setNombre("Construcción de Centro de Salud");
        completedProyecto.setDescripcion("Proyecto completado exitosamente");
        completedProyecto.setUbicacion("Lima Norte - Comas");
        completedProyecto.setEstadoProyecto("COMPLETADO");

        when(proyectoService.updateProyecto(eq(1L), any(ProyectoDTO.class))).thenReturn(completedProyecto);

        // When & Then
        mockMvc.perform(put("/api/proyectos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completedProyecto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.estadoProyecto", is("COMPLETADO")))
                .andExpect(jsonPath("$.descripcion", is("Proyecto completado exitosamente")));

        verify(proyectoService, times(1)).updateProyecto(eq(1L), any(ProyectoDTO.class));
    }

    @Test
    void updateProyecto_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(proyectoService.updateProyecto(eq(999L), any(ProyectoDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/proyectos/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(proyectoDTO)))
                .andExpect(status().isNotFound());

        verify(proyectoService, times(1)).updateProyecto(eq(999L), any(ProyectoDTO.class));
    }

    @Test
    void deleteProyecto_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        when(proyectoService.getProyectoById(1L)).thenReturn(proyectoDTO);
        doNothing().when(proyectoService).deleteProyecto(1L);

        // When & Then
        mockMvc.perform(delete("/api/proyectos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(proyectoService, times(1)).getProyectoById(1L);
        verify(proyectoService, times(1)).deleteProyecto(1L);
    }

    @Test
    void deleteProyecto_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(proyectoService.getProyectoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/proyectos/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(proyectoService, times(1)).getProyectoById(999L);
        verify(proyectoService, never()).deleteProyecto(999L);
    }

    @Test
    void createProyecto_WithEmptyNombre_ShouldStillCreateProyecto() throws Exception {
        // Given - El controller no tiene validación @Valid, acepta nombre vacío
        ProyectoDTO emptyNameProyecto = new ProyectoDTO();
        emptyNameProyecto.setNombre("");
        emptyNameProyecto.setUbicacion("Sin ubicación específica");

        ProyectoDTO savedProyecto = new ProyectoDTO();
        savedProyecto.setId(5L);
        savedProyecto.setNombre("");
        savedProyecto.setUbicacion("Sin ubicación específica");
        savedProyecto.setEstadoProyecto("PLANIFICACION");

        when(proyectoService.saveProyecto(any(ProyectoDTO.class))).thenReturn(savedProyecto);

        // When & Then
        mockMvc.perform(post("/api/proyectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameProyecto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nombre", is("")))
                .andExpect(jsonPath("$.ubicacion", is("Sin ubicación específica")));

        verify(proyectoService, times(1)).saveProyecto(any(ProyectoDTO.class));
    }
}
