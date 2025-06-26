package com.conaveg.cona.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de desarrollo para simular funcionalidades de autenticación
 * SOLO PARA DESARROLLO - NO USAR EN PRODUCCIÓN
 * 
 * Proporciona endpoints que simulan respuestas de autenticación
 * para facilitar el trabajo del equipo frontend
 */
@RestController
@RequestMapping("/api/dev")
@Profile("dev")
@ConditionalOnProperty(name = "app.dev.skip-authentication", havingValue = "true")
public class DevController {

    /**
     * Simula un login exitoso devolviendo un token y datos de usuario ficticios
     */
    @PostMapping("/login")
    public ResponseEntity<?> simulateLogin(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = new HashMap<>();
        
        // Simular token JWT
        response.put("token", "dev-mock-jwt-token-12345");
        response.put("type", "Bearer");
        response.put("expiresIn", 86400);
        
        // Simular datos de usuario
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("userName", "dev-user");
        user.put("email", "dev@conaveg.com");
        user.put("role", "ADMINISTRADOR");
        user.put("estado", "ACTIVO");
        
        response.put("user", user);
        response.put("message", "Login simulado para desarrollo");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simula obtener el usuario actual
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1);
        user.put("userName", "dev-user");
        user.put("email", "dev@conaveg.com");
        user.put("role", "ADMINISTRADOR");
        user.put("estado", "ACTIVO");
        user.put("createdAt", LocalDateTime.now().minusDays(30));
        user.put("updatedAt", LocalDateTime.now());
        
        return ResponseEntity.ok(user);
    }

    /**
     * Simula refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken() {
        Map<String, Object> response = new HashMap<>();
        response.put("token", "dev-mock-refreshed-jwt-token-67890");
        response.put("type", "Bearer");
        response.put("expiresIn", 86400);
        response.put("message", "Token renovado para desarrollo");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simula logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Logout simulado para desarrollo");
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para verificar el estado del modo desarrollo
     */
    @GetMapping("/status")
    public ResponseEntity<?> getDevStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("message", "Modo desarrollo activo - Autenticación deshabilitada");
        status.put("warning", "NO USAR EN PRODUCCIÓN");
        status.put("timestamp", LocalDateTime.now());
        status.put("skipAuthentication", true);
        
        return ResponseEntity.ok(status);
    }
}
