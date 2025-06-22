package com.conaveg.cona.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja errores de autenticación cuando un usuario intenta acceder
 * a un recurso protegido sin estar autenticado o con token inválido
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        // Configurar respuesta HTTP
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Crear cuerpo de respuesta JSON estructurado
        Map<String, Object> body = new HashMap<>();
        body.put("status", 401);
        body.put("error", "No autorizado");
        body.put("message", "Se requiere autenticación válida para acceder a este recurso");
        body.put("path", request.getServletPath());
        body.put("timestamp", System.currentTimeMillis());
        
        // Agregar detalles específicos según el tipo de error
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            body.put("details", "Header 'Authorization' no proporcionado. Formato esperado: 'Bearer <token>'");
        } else if (!authHeader.startsWith("Bearer ")) {
            body.put("details", "Formato de Authorization inválido. Use: 'Bearer <token>'");
        } else {
            body.put("details", "Token JWT inválido o expirado");
        }
        
        // Escribir respuesta JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
