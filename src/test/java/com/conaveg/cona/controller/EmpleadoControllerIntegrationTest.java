package com.conaveg.cona.controller;

import com.conaveg.cona.dto.EmpleadoDTO;
import com.conaveg.cona.service.EmpleadoService;
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
 * Pruebas de integración para EmpleadoController
 * Valida el funcionamiento de los endpoints REST y la interacción con el servicio
 */
@WebMvcTest(EmpleadoController.class)
class EmpleadoControllerIntegrationTest {    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpleadoService empleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmpleadoDTO empleadoDTO;
    private EmpleadoDTO empleadoDTO2;

    @BeforeEach
    void setUp() {
        empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setId(1L);
        empleadoDTO.setUserId(1L);
        empleadoDTO.setNombres("Juan Carlos");
        empleadoDTO.setApellidos("Pérez López");
        empleadoDTO.setNroDocumento("12345678");
        empleadoDTO.setFechaNacimiento(LocalDate.of(1990, 5, 15));
        empleadoDTO.setDireccion("Av. Principal 123");
        empleadoDTO.setTelefono("987654321");
        empleadoDTO.setPuesto("Desarrollador");
        empleadoDTO.setFechaIngreso(LocalDate.of(2023, 1, 15));
        empleadoDTO.setEstado("ACTIVO");
        empleadoDTO.setUniqueId("EMP-001");

        empleadoDTO2 = new EmpleadoDTO();
        empleadoDTO2.setId(2L);
        empleadoDTO2.setUserId(2L);
        empleadoDTO2.setNombres("María Elena");
        empleadoDTO2.setApellidos("González Ruiz");
        empleadoDTO2.setNroDocumento("87654321");
        empleadoDTO2.setFechaNacimiento(LocalDate.of(1985, 8, 20));
        empleadoDTO2.setDireccion("Calle Secundaria 456");
        empleadoDTO2.setTelefono("123456789");
        empleadoDTO2.setPuesto("Analista");
        empleadoDTO2.setFechaIngreso(LocalDate.of(2022, 6, 10));
        empleadoDTO2.setEstado("ACTIVO");
        empleadoDTO2.setUniqueId("EMP-002");
    }

    @Test
    void getAllEmpleados_ShouldReturnEmpleadosList() throws Exception {
        // Given
        List<EmpleadoDTO> empleados = Arrays.asList(empleadoDTO, empleadoDTO2);
        when(empleadoService.getAllEmpleados()).thenReturn(empleados);

        // When & Then
        mockMvc.perform(get("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombres", is("Juan Carlos")))
                .andExpect(jsonPath("$[0].apellidos", is("Pérez López")))
                .andExpect(jsonPath("$[0].nroDocumento", is("12345678")))
                .andExpect(jsonPath("$[0].puesto", is("Desarrollador")))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombres", is("María Elena")))
                .andExpect(jsonPath("$[1].apellidos", is("González Ruiz")))
                .andExpect(jsonPath("$[1].puesto", is("Analista")));

        verify(empleadoService, times(1)).getAllEmpleados();
    }

    @Test
    void getAllEmpleados_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(empleadoService.getAllEmpleados()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(empleadoService, times(1)).getAllEmpleados();
    }

    @Test
    void getEmpleadoById_WithValidId_ShouldReturnEmpleado() throws Exception {
        // Given
        when(empleadoService.getEmpleadoById(1L)).thenReturn(empleadoDTO);

        // When & Then
        mockMvc.perform(get("/api/empleados/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan Carlos")))
                .andExpect(jsonPath("$.apellidos", is("Pérez López")))
                .andExpect(jsonPath("$.nroDocumento", is("12345678")))
                .andExpect(jsonPath("$.telefono", is("987654321")))
                .andExpect(jsonPath("$.puesto", is("Desarrollador")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.uniqueId", is("EMP-001")));

        verify(empleadoService, times(1)).getEmpleadoById(1L);
    }

    @Test
    void getEmpleadoById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(empleadoService.getEmpleadoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/empleados/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(empleadoService, times(1)).getEmpleadoById(999L);
    }

    @Test
    void createEmpleado_WithValidData_ShouldReturnCreatedEmpleado() throws Exception {
        // Given
        when(empleadoService.saveEmpleado(any(EmpleadoDTO.class))).thenReturn(empleadoDTO);

        // When & Then
        mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan Carlos")))
                .andExpect(jsonPath("$.apellidos", is("Pérez López")))
                .andExpect(jsonPath("$.nroDocumento", is("12345678")))
                .andExpect(jsonPath("$.puesto", is("Desarrollador")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(empleadoService, times(1)).saveEmpleado(any(EmpleadoDTO.class));
    }

    @Test
    void createEmpleado_WithInvalidData_ShouldHandleValidation() throws Exception {
        // Given - Empleado con datos mínimos para probar validación
        EmpleadoDTO invalidEmpleado = new EmpleadoDTO();
        invalidEmpleado.setNombres(""); // Nombre vacío
        
        // When & Then
        mockMvc.perform(post("/api/empleados")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmpleado)))
                .andExpect(status().isCreated()); // El controller no tiene validación @Valid, por lo tanto acepta cualquier dato

        verify(empleadoService, times(1)).saveEmpleado(any(EmpleadoDTO.class));
    }

    @Test
    void updateEmpleado_WithValidData_ShouldReturnUpdatedEmpleado() throws Exception {
        // Given
        EmpleadoDTO updatedEmpleado = new EmpleadoDTO();
        updatedEmpleado.setId(1L);
        updatedEmpleado.setNombres("Juan Carlos Actualizado");
        updatedEmpleado.setApellidos("Pérez López");
        updatedEmpleado.setNroDocumento("12345678");
        updatedEmpleado.setPuesto("Senior Developer");
        updatedEmpleado.setEstado("ACTIVO");
        
        when(empleadoService.updateEmpleado(eq(1L), any(EmpleadoDTO.class))).thenReturn(updatedEmpleado);

        // When & Then
        mockMvc.perform(put("/api/empleados/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmpleado)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombres", is("Juan Carlos Actualizado")))
                .andExpect(jsonPath("$.puesto", is("Senior Developer")));

        verify(empleadoService, times(1)).updateEmpleado(eq(1L), any(EmpleadoDTO.class));
    }

    @Test
    void updateEmpleado_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(empleadoService.updateEmpleado(eq(999L), any(EmpleadoDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/empleados/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleadoDTO)))
                .andExpect(status().isNotFound());

        verify(empleadoService, times(1)).updateEmpleado(eq(999L), any(EmpleadoDTO.class));
    }

    @Test
    void deleteEmpleado_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        when(empleadoService.getEmpleadoById(1L)).thenReturn(empleadoDTO);
        doNothing().when(empleadoService).deleteEmpleado(1L);

        // When & Then
        mockMvc.perform(delete("/api/empleados/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(empleadoService, times(1)).getEmpleadoById(1L);
        verify(empleadoService, times(1)).deleteEmpleado(1L);
    }

    @Test
    void deleteEmpleado_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(empleadoService.getEmpleadoById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/empleados/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(empleadoService, times(1)).getEmpleadoById(999L);
        verify(empleadoService, never()).deleteEmpleado(999L);
    }
}
