package com.conaveg.cona.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilidad para obtener información del usuario autenticado
 * desde cualquier parte de la aplicación
 */
@Component
public class SecurityUtils {
    
    /**
     * Obtiene el email del usuario autenticado actualmente
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            return (String) authentication.getPrincipal();
        }
        
        return null;
    }
    
    /**
     * Obtiene el ID del usuario autenticado desde los atributos de la request
     * (establecido por JwtAuthenticationFilter)
     */
    public static Long getCurrentUserId() {
        // Esta información se obtendría desde la request actual
        // En una implementación más avanzada, se podría extraer del token
        // Por ahora retornamos null, se implementará cuando sea necesario
        return null;
    }
    
    /**
     * Verifica si hay un usuario autenticado
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && 
               !authentication.getPrincipal().equals("anonymousUser");
    }
    
    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getAuthorities() != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
        }
        
        return false;
    }
}
