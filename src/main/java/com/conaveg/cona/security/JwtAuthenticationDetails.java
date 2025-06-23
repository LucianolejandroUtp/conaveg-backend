package com.conaveg.cona.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * Detalles de autenticación personalizados que incluyen información del JWT
 */
public class JwtAuthenticationDetails extends WebAuthenticationDetails {
    
    private final Long userId;
    private final String role;
    
    public JwtAuthenticationDetails(HttpServletRequest request, Long userId, String role) {
        super(request);
        this.userId = userId;
        this.role = role;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getRole() {
        return role;
    }
}
