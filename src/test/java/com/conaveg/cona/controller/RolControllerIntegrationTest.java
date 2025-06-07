package com.conaveg.cona.controller;

import com.conaveg.cona.dto.RolDTO;
import com.conaveg.cona.service.RolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
 * Pruebas de integración para RolController
 * Valida el funcionamiento de los endpoints REST y la interacción con el servicio
 */
@WebMvcTest(RolController.class)
class RolControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    private RolDTO rolDTO;
    private RolDTO rolDTO2;

    @BeforeEach
    void setUp() {
        rolDTO = new RolDTO();
        rolDTO.setId(1L);
        rolDTO.setNombre("ADMIN");
        rolDTO.setDescripcion("Administrador del sistema");
        rolDTO.setEstado("ACTIVO");
        rolDTO.setUniqueId("ROL-001");

        rolDTO2 = new RolDTO();
        rolDTO2.setId(2L);
        rolDTO2.setNombre("USER");
        rolDTO2.setDescripcion("Usuario regular del sistema");
        rolDTO2.setEstado("ACTIVO");
        rolDTO2.setUniqueId("ROL-002");
    }

    @Test
    void getAllRoles_ShouldReturnRolesList() throws Exception {
        // Given
        List<RolDTO> roles = Arrays.asList(rolDTO, rolDTO2);
        when(rolService.getAllRoles()).thenReturn(roles);

        // When & Then
        mockMvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nombre", is("ADMIN")))
                .andExpect(jsonPath("$[0].descripcion", is("Administrador del sistema")))
                .andExpect(jsonPath("$[0].estado", is("ACTIVO")))
                .andExpect(jsonPath("$[0].uniqueId", is("ROL-001")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nombre", is("USER")))
                .andExpect(jsonPath("$[1].descripcion", is("Usuario regular del sistema")))
                .andExpect(jsonPath("$[1].estado", is("ACTIVO")))
                .andExpect(jsonPath("$[1].uniqueId", is("ROL-002")));

        verify(rolService, times(1)).getAllRoles();
    }

    @Test
    void getAllRoles_WhenEmpty_ShouldReturnEmptyList() throws Exception {
        // Given
        when(rolService.getAllRoles()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(rolService, times(1)).getAllRoles();
    }

    @Test
    void getRolById_WithValidId_ShouldReturnRol() throws Exception {
        // Given
        when(rolService.getRolById(1L)).thenReturn(rolDTO);

        // When & Then
        mockMvc.perform(get("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("ADMIN")))
                .andExpect(jsonPath("$.descripcion", is("Administrador del sistema")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.uniqueId", is("ROL-001")));

        verify(rolService, times(1)).getRolById(1L);
    }

    @Test
    void getRolById_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(rolService.getRolById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/roles/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).getRolById(999L);
    }

    @Test
    void createRol_WithValidData_ShouldReturnCreatedRol() throws Exception {
        // Given
        RolDTO newRolRequest = new RolDTO();
        newRolRequest.setNombre("MODERATOR");
        newRolRequest.setDescripcion("Moderador del sistema");

        RolDTO createdRol = new RolDTO();
        createdRol.setId(3L);
        createdRol.setNombre("MODERATOR");
        createdRol.setDescripcion("Moderador del sistema");
        createdRol.setEstado("ACTIVO");
        createdRol.setUniqueId("ROL-003");

        when(rolService.saveRol(any(RolDTO.class))).thenReturn(createdRol);

        // When & Then
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRolRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombre", is("MODERATOR")))
                .andExpect(jsonPath("$.descripcion", is("Moderador del sistema")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.uniqueId", is("ROL-003")));

        verify(rolService, times(1)).saveRol(any(RolDTO.class));
    }

    @Test
    void createRol_WithMinimalData_ShouldReturnCreatedRol() throws Exception {
        // Given
        RolDTO minimalRolRequest = new RolDTO();
        minimalRolRequest.setNombre("GUEST");

        RolDTO createdRol = new RolDTO();
        createdRol.setId(4L);
        createdRol.setNombre("GUEST");
        createdRol.setEstado("ACTIVO");
        createdRol.setUniqueId("ROL-004");

        when(rolService.saveRol(any(RolDTO.class))).thenReturn(createdRol);

        // When & Then
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(minimalRolRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.nombre", is("GUEST")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")))
                .andExpect(jsonPath("$.uniqueId", is("ROL-004")));

        verify(rolService, times(1)).saveRol(any(RolDTO.class));
    }

    @Test
    void updateRol_WithValidData_ShouldReturnUpdatedRol() throws Exception {
        // Given
        RolDTO updateRequest = new RolDTO();
        updateRequest.setNombre("ADMIN");
        updateRequest.setDescripcion("Administrador del sistema - Actualizado");

        RolDTO updatedRol = new RolDTO();
        updatedRol.setId(1L);
        updatedRol.setNombre("ADMIN");
        updatedRol.setDescripcion("Administrador del sistema - Actualizado");
        updatedRol.setEstado("ACTIVO");
        updatedRol.setUniqueId("ROL-001");

        when(rolService.updateRol(eq(1L), any(RolDTO.class))).thenReturn(updatedRol);

        // When & Then
        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("ADMIN")))
                .andExpect(jsonPath("$.descripcion", is("Administrador del sistema - Actualizado")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(rolService, times(1)).updateRol(eq(1L), any(RolDTO.class));
    }

    @Test
    void updateRol_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(rolService.updateRol(eq(999L), any(RolDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/roles/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolDTO)))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).updateRol(eq(999L), any(RolDTO.class));
    }

    @Test
    void deleteRol_WithValidId_ShouldReturnNoContent() throws Exception {
        // Given
        when(rolService.getRolById(1L)).thenReturn(rolDTO);
        doNothing().when(rolService).deleteRol(1L);

        // When & Then
        mockMvc.perform(delete("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(rolService, times(1)).getRolById(1L);
        verify(rolService, times(1)).deleteRol(1L);
    }

    @Test
    void deleteRol_WithInvalidId_ShouldReturnNotFound() throws Exception {
        // Given
        when(rolService.getRolById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/roles/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).getRolById(999L);
        verify(rolService, never()).deleteRol(999L);
    }

    @Test
    void createRol_WithEmptyName_ShouldStillCreateRol() throws Exception {
        // Given - El controller no tiene validación @Valid, acepta nombre vacío
        RolDTO emptyNameRequest = new RolDTO();
        emptyNameRequest.setNombre("");
        emptyNameRequest.setDescripcion("Rol sin nombre");

        RolDTO createdRol = new RolDTO();
        createdRol.setId(5L);
        createdRol.setNombre("");
        createdRol.setDescripcion("Rol sin nombre");
        createdRol.setEstado("ACTIVO");
        createdRol.setUniqueId("ROL-005");

        when(rolService.saveRol(any(RolDTO.class))).thenReturn(createdRol);

        // When & Then
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyNameRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.nombre", is("")))
                .andExpect(jsonPath("$.descripcion", is("Rol sin nombre")))
                .andExpect(jsonPath("$.estado", is("ACTIVO")));

        verify(rolService, times(1)).saveRol(any(RolDTO.class));
    }

    @Test
    void updateRol_ChangeRoleName_ShouldReturnUpdatedRol() throws Exception {
        // Given
        RolDTO changeNameRequest = new RolDTO();
        changeNameRequest.setNombre("SUPER_ADMIN");
        changeNameRequest.setDescripcion("Super Administrador");

        RolDTO updatedRol = new RolDTO();
        updatedRol.setId(1L);
        updatedRol.setNombre("SUPER_ADMIN");
        updatedRol.setDescripcion("Super Administrador");
        updatedRol.setEstado("ACTIVO");
        updatedRol.setUniqueId("ROL-001");

        when(rolService.updateRol(eq(1L), any(RolDTO.class))).thenReturn(updatedRol);

        // When & Then
        mockMvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changeNameRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nombre", is("SUPER_ADMIN")))
                .andExpect(jsonPath("$.descripcion", is("Super Administrador")));

        verify(rolService, times(1)).updateRol(eq(1L), any(RolDTO.class));
    }
}
