package com.conaveg.cona.controller;

import com.conaveg.cona.dto.UserCreateDTO;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.dto.RolDTO;
import com.conaveg.cona.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
 * Tests de integración para UserController
 * Prueba la capa de controller con validaciones y serialización JSON
 * Usa @WebMvcTest para cargar solo el contexto web mínimo
 */
@WebMvcTest(UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO userDTO;
    private UserCreateDTO userCreateDTO;
    private RolDTO rolDTO;

    @BeforeEach
    void setUp() {
        // Configurar rol de prueba
        rolDTO = new RolDTO();
        rolDTO.setId(1L);
        rolDTO.setNombre("USER");
        rolDTO.setDescripcion("Usuario estándar");
        rolDTO.setEstado("A");

        // Configurar DTO de respuesta
        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUserName("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setEstado("A");
        userDTO.setUniqueId("user-123");
        userDTO.setCreatedAt(Instant.now());
        userDTO.setUpdatedAt(Instant.now());
        userDTO.setRole(rolDTO);

        // Configurar DTO de creación
        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUserName("testuser");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("ValidPassword123!");
        userCreateDTO.setRoleId(1L);
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        // Given
        List<UserDTO> users = Arrays.asList(userDTO);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].userName", is("testuser")))
                .andExpect(jsonPath("$[0].email", is("test@example.com")))
                .andExpect(jsonPath("$[0].role.nombre", is("USER")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role.nombre", is("USER")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {        // Given
        when(userService.saveUser(any(UserCreateDTO.class))).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.role.nombre", is("USER")));

        verify(userService, times(1)).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithInvalidUserName_ShouldReturnBadRequest() throws Exception {
        // Given
        userCreateDTO.setUserName(""); // Usuario vacío

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // Given
        userCreateDTO.setEmail("invalid-email"); // Email inválido

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithWeakPassword_ShouldReturnBadRequest() throws Exception {
        // Given
        userCreateDTO.setPassword("weak"); // Contraseña débil

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithPasswordWithoutUppercase_ShouldReturnBadRequest() throws Exception {
        // Given
        userCreateDTO.setPassword("onlylowercase123"); // Sin mayúsculas

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        // Given
        when(userService.updateUser(eq(1L), any(UserCreateDTO.class))).thenReturn(userDTO);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userName", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService, times(1)).updateUser(eq(1L), any(UserCreateDTO.class));
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        when(userService.updateUser(eq(999L), any(UserCreateDTO.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(eq(999L), any(UserCreateDTO.class));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnNoContent() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(userDTO);
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getUserById(1L);
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldReturn404() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUserById(999L);
        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    void createUser_WithNullRequestBody_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // Given
        UserCreateDTO incompleteUser = new UserCreateDTO();
        // Sin setear campos obligatorios

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteUser)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithUserNameTooShort_ShouldReturnBadRequest() throws Exception {
        // Given
        userCreateDTO.setUserName("ab"); // Solo 2 caracteres, mínimo es 3

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }

    @Test
    void createUser_WithPasswordTooShort_ShouldReturnBadRequest() throws Exception {
        // Given
        userCreateDTO.setPassword("Short1"); // Solo 6 caracteres, mínimo es 8

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(UserCreateDTO.class));
    }
}
