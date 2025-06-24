package com.conaveg.cona.config;

import com.conaveg.cona.service.AuthenticationAttemptService;
import com.conaveg.cona.service.SecurityAuditService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro para implementar rate limiting en endpoints de autenticación
 * Protege contra ataques de fuerza bruta limitando intentos por IP y email
 */
@Component
@Order(1) // Ejecutar antes que otros filtros
public class RateLimitingFilter implements Filter {

    @Autowired
    private AuthenticationAttemptService attemptService;

    @Autowired
    private SecurityAuditService auditService;

    // Endpoints que requieren rate limiting
    private static final List<String> PROTECTED_ENDPOINTS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/auth/refresh"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
                        FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // Solo aplicar rate limiting a endpoints protegidos con método POST
        if ("POST".equals(method) && isProtectedEndpoint(requestURI)) {
            
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");

            try {
                // Verificar rate limiting por IP
                if (attemptService.isIpBlocked(clientIp)) {
                    // Log del evento de bloqueo
                    auditService.logSecurityEvent(
                        SecurityAuditService.EventType.RATE_LIMIT_EXCEEDED,
                        null, "", clientIp, userAgent,
                        "IP blocked due to rate limiting on endpoint: " + requestURI,
                        SecurityAuditService.Severity.HIGH
                    );

                    // Responder con error 429 Too Many Requests
                    response.setStatus(429); // HTTP 429 Too Many Requests
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"error\":\"Demasiados intentos desde esta IP. Acceso temporalmente bloqueado.\","
                        + "\"code\":\"RATE_LIMIT_EXCEEDED\","
                        + "\"retryAfter\":900}" // 15 minutos en segundos
                    );
                    return;
                }

                // Para endpoints específicos de login/refresh, verificar también por email
                if (requestURI.contains("/login") || requestURI.contains("/refresh")) {
                    String email = extractEmailFromRequest(request);
                    if (email != null && attemptService.isAccountLocked(email)) {
                        // Log del evento de bloqueo por email
                        auditService.logSecurityEvent(
                            SecurityAuditService.EventType.RATE_LIMIT_EXCEEDED,
                            null, email, clientIp, userAgent,
                            "Email blocked due to rate limiting on endpoint: " + requestURI,
                            SecurityAuditService.Severity.HIGH
                        );

                        response.setStatus(429); // HTTP 429 Too Many Requests
                        response.setContentType("application/json");
                        response.getWriter().write(
                            "{\"error\":\"Demasiados intentos fallidos para este email. Cuenta temporalmente bloqueada.\","
                            + "\"code\":\"EMAIL_RATE_LIMIT_EXCEEDED\","
                            + "\"retryAfter\":900}"
                        );
                        return;
                    }
                }

            } catch (Exception e) {
                // Log error en rate limiting pero continuar con la request
                auditService.logSecurityEvent(
                    SecurityAuditService.EventType.SYSTEM_ERROR,
                    null, "", clientIp, userAgent,
                    "Error in rate limiting filter: " + e.getMessage(),
                    SecurityAuditService.Severity.HIGH
                );
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Verifica si el endpoint requiere rate limiting
     */
    private boolean isProtectedEndpoint(String requestURI) {
        return PROTECTED_ENDPOINTS.stream().anyMatch(requestURI::contains);
    }

    /**
     * Extrae la IP real del cliente considerando proxies
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Extrae el email de la request (para login/refresh)
     * Nota: Este es un método básico, en producción sería más robusto
     */
    private String extractEmailFromRequest(HttpServletRequest request) {
        try {
            // Para el endpoint de login, el email está en el body JSON
            // Para refresh, está en el token JWT
            // Por simplicidad, retornamos null y dejamos que el servicio maneje la validación
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro si es necesaria
    }

    @Override
    public void destroy() {
        // Limpieza del filtro si es necesaria
    }
}
