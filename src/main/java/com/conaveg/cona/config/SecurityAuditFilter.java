package com.conaveg.cona.config;

import com.conaveg.cona.service.SecurityAuditService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Filtro para auditoría automática de requests de autenticación
 * Registra todos los intentos de acceso a endpoints sensibles
 */
@Component
@Order(2) // Ejecutar después del RateLimitingFilter
public class SecurityAuditFilter implements Filter {

    @Autowired
    private SecurityAuditService auditService;

    // Endpoints que requieren auditoría
    private static final List<String> AUDITED_ENDPOINTS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/logout", 
        "/api/auth/refresh",
        "/api/auth/forgot-password",
        "/api/auth/reset-password",
        "/api/auth/validate",
        "/api/auth/me"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, 
                        FilterChain filterChain) throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Solo auditar endpoints sensibles
        if (isAuditedEndpoint(requestURI)) {
            
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            LocalDateTime requestTime = LocalDateTime.now();

            try {
                // Continuar con la cadena de filtros
                filterChain.doFilter(request, response);

                // Auditar después de la respuesta
                auditAfterResponse(request, response, requestTime, clientIp, userAgent);

            } catch (Exception e) {
                // Auditar errores también
                auditService.logSecurityEvent(
                    SecurityAuditService.EventType.SYSTEM_ERROR,
                    null, "", clientIp, userAgent,
                    "Exception during request processing: " + method + " " + requestURI + " - " + e.getMessage(),
                    SecurityAuditService.Severity.HIGH
                );
                throw e;
            }
        } else {
            // Para endpoints no auditados, continuar normalmente
            filterChain.doFilter(request, response);
        }
    }

    /**
     * Audita la respuesta después del procesamiento
     */
    private void auditAfterResponse(HttpServletRequest request, HttpServletResponse response,
                                  LocalDateTime requestTime, String clientIp, String userAgent) {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        int statusCode = response.getStatus();

        // Determinar el tipo de evento basado en endpoint y resultado
        SecurityAuditService.EventType eventType = getEventType(requestURI, statusCode);
        SecurityAuditService.Severity severity = getSeverity(requestURI, statusCode);

        // Construir mensaje de auditoría
        String message = String.format(
            "%s %s - Status: %d - Duration: %dms",
            method, requestURI, statusCode, 
            java.time.Duration.between(requestTime, LocalDateTime.now()).toMillis()
        );

        // Log del evento
        auditService.logSecurityEvent(
            eventType, null, "", clientIp, userAgent, message, severity
        );

        // Log adicional para eventos críticos
        if (statusCode >= 400) {
            String errorMessage = String.format(
                "Authentication endpoint error: %s %s returned %d", 
                method, requestURI, statusCode
            );
            
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.UNAUTHORIZED_ACCESS,
                null, "", clientIp, userAgent, errorMessage,
                statusCode >= 500 ? SecurityAuditService.Severity.HIGH : SecurityAuditService.Severity.MEDIUM
            );
        }
    }

    /**
     * Determina el tipo de evento basado en el endpoint y código de estado
     */
    private SecurityAuditService.EventType getEventType(String requestURI, int statusCode) {
        if (requestURI.contains("/login")) {
            return statusCode == 200 ? SecurityAuditService.EventType.LOGIN_SUCCESS : 
                                     SecurityAuditService.EventType.LOGIN_FAILED;
        } else if (requestURI.contains("/logout")) {
            return SecurityAuditService.EventType.LOGOUT;
        } else if (requestURI.contains("/refresh")) {
            return statusCode == 200 ? SecurityAuditService.EventType.TOKEN_REFRESH_SUCCESS : 
                                     SecurityAuditService.EventType.TOKEN_REFRESH_FAILED;
        } else if (requestURI.contains("/forgot-password")) {
            return SecurityAuditService.EventType.PASSWORD_RESET_REQUESTED;
        } else if (requestURI.contains("/reset-password")) {
            return statusCode == 200 ? SecurityAuditService.EventType.PASSWORD_CHANGED : 
                                     SecurityAuditService.EventType.PASSWORD_RESET_FAILED;
        } else if (requestURI.contains("/validate")) {
            return SecurityAuditService.EventType.DATA_ACCESS;
        } else {
            return SecurityAuditService.EventType.DATA_ACCESS;
        }
    }

    /**
     * Determina la severidad basada en el endpoint y código de estado
     */
    private SecurityAuditService.Severity getSeverity(String requestURI, int statusCode) {
        // Errores críticos
        if (statusCode >= 500) {
            return SecurityAuditService.Severity.HIGH;
        }
        
        // Errores de autenticación/autorización
        if (statusCode == 401 || statusCode == 403 || statusCode == 429) {
            return SecurityAuditService.Severity.MEDIUM;
        }
        
        // Eventos exitosos críticos
        if ((requestURI.contains("/login") || requestURI.contains("/reset-password")) && statusCode == 200) {
            return SecurityAuditService.Severity.MEDIUM;
        }
        
        // Otros eventos
        return SecurityAuditService.Severity.LOW;
    }

    /**
     * Verifica si el endpoint requiere auditoría
     */
    private boolean isAuditedEndpoint(String requestURI) {
        return AUDITED_ENDPOINTS.stream().anyMatch(requestURI::contains);
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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialización del filtro si es necesaria
    }

    @Override
    public void destroy() {
        // Limpieza del filtro si es necesaria
    }
}
