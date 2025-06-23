package com.conaveg.cona.service;

import com.conaveg.cona.model.SecurityAuditLog;
import com.conaveg.cona.repository.SecurityAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para auditoría de seguridad
 */
@Service
@Transactional
public class SecurityAuditService {

    @Autowired
    private SecurityAuditLogRepository auditRepository;

    public enum EventType {
        LOGIN_SUCCESS("LOGIN_SUCCESS"),
        LOGIN_FAILED("LOGIN_FAILED"),
        LOGOUT("LOGOUT"),
        PASSWORD_CHANGED("PASSWORD_CHANGED"),
        PASSWORD_RESET_REQUESTED("PASSWORD_RESET_REQUESTED"),
        PASSWORD_RESET_COMPLETED("PASSWORD_RESET_COMPLETED"),
        PASSWORD_RESET_FAILED("PASSWORD_RESET_FAILED"),
        PASSWORD_RESET_RATE_LIMITED("PASSWORD_RESET_RATE_LIMITED"),
        ACCOUNT_LOCKED("ACCOUNT_LOCKED"),
        ACCOUNT_UNLOCKED("ACCOUNT_UNLOCKED"),
        TOKEN_REFRESH("TOKEN_REFRESH"),
        TOKEN_REFRESH_FAILED("TOKEN_REFRESH_FAILED"),
        TOKEN_REFRESH_SUCCESS("TOKEN_REFRESH_SUCCESS"),
        TOKEN_REFRESH_INVALID("TOKEN_REFRESH_INVALID"),
        TOKEN_REFRESH_OUT_OF_WINDOW("TOKEN_REFRESH_OUT_OF_WINDOW"),
        TOKEN_REFRESH_RATE_LIMITED("TOKEN_REFRESH_RATE_LIMITED"),
        TOKEN_REFRESH_ERROR("TOKEN_REFRESH_ERROR"),
        SUSPICIOUS_ACTIVITY("SUSPICIOUS_ACTIVITY"),
        UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS"),
        PERMISSION_DENIED("PERMISSION_DENIED"),
        DATA_ACCESS("DATA_ACCESS"),
        SYSTEM_ERROR("SYSTEM_ERROR");

        private final String value;

        EventType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum Severity {
        LOW("LOW"),
        MEDIUM("MEDIUM"),
        HIGH("HIGH"),
        CRITICAL("CRITICAL"),
        INFO("INFO"),
        WARNING("WARNING");

        private final String value;

        Severity(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Registra un evento de seguridad
     */
    public void logSecurityEvent(EventType eventType, Long userId, String email, 
                                String ipAddress, String userAgent, String details, 
                                Severity severity) {
        SecurityAuditLog log = new SecurityAuditLog(
            eventType.getValue(),
            userId,
            email,
            ipAddress,
            userAgent,
            details,
            severity.getValue()
        );
        
        auditRepository.save(log);
    }

    /**
     * Registra login exitoso
     */
    public void logSuccessfulLogin(Long userId, String email, HttpServletRequest request) {
        logSecurityEvent(
            EventType.LOGIN_SUCCESS,
            userId,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "User successfully logged in",
            Severity.LOW
        );
    }

    /**
     * Registra login fallido
     */
    public void logFailedLogin(String email, HttpServletRequest request, String reason) {
        logSecurityEvent(
            EventType.LOGIN_FAILED,
            null,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "Login failed: " + reason,
            Severity.MEDIUM
        );
    }

    /**
     * Registra logout
     */
    public void logLogout(Long userId, String email, HttpServletRequest request) {
        logSecurityEvent(
            EventType.LOGOUT,
            userId,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "User logged out",
            Severity.LOW
        );
    }

    /**
     * Registra cambio de contraseña
     */
    public void logPasswordChange(Long userId, String email, HttpServletRequest request, String method) {
        logSecurityEvent(
            EventType.PASSWORD_CHANGED,
            userId,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "Password changed via " + method,
            Severity.MEDIUM
        );
    }

    /**
     * Registra refresh de token
     */
    public void logTokenRefresh(Long userId, String email, HttpServletRequest request, boolean successful) {
        EventType eventType = successful ? EventType.TOKEN_REFRESH : EventType.TOKEN_REFRESH_FAILED;
        Severity severity = successful ? Severity.LOW : Severity.MEDIUM;
        String details = successful ? "Token refreshed successfully" : "Token refresh failed";

        logSecurityEvent(
            eventType,
            userId,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            details,
            severity
        );
    }

    /**
     * Registra acceso no autorizado
     */
    public void logUnauthorizedAccess(String email, HttpServletRequest request, String resource) {
        logSecurityEvent(
            EventType.UNAUTHORIZED_ACCESS,
            null,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "Unauthorized access attempt to: " + resource,
            Severity.HIGH
        );
    }

    /**
     * Registra acceso denegado por permisos
     */
    public void logPermissionDenied(Long userId, String email, HttpServletRequest request, String resource) {
        logSecurityEvent(
            EventType.PERMISSION_DENIED,
            userId,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "Permission denied for resource: " + resource,
            Severity.MEDIUM
        );
    }

    /**
     * Registra acceso a datos sensibles
     */
    public void logDataAccess(Long userId, String email, HttpServletRequest request, String dataType) {
        logSecurityEvent(
            EventType.DATA_ACCESS,
            userId,
            email,
            getClientIp(request),
            request.getHeader("User-Agent"),
            "Accessed sensitive data: " + dataType,
            Severity.LOW
        );
    }

    /**
     * Registra errores del sistema
     */
    public void logSystemError(String email, HttpServletRequest request, String error) {
        logSecurityEvent(
            EventType.SYSTEM_ERROR,
            null,
            email != null ? email : "system",
            request != null ? getClientIp(request) : "internal",
            request != null ? request.getHeader("User-Agent") : "system",
            "System error: " + error,
            Severity.HIGH
        );
    }

    /**
     * Busca logs con filtros
     */
    @Transactional(readOnly = true)
    public Page<SecurityAuditLog> findLogsWithFilters(String eventType, Long userId, String email, 
                                                     String severity, LocalDateTime startDate, 
                                                     LocalDateTime endDate, Pageable pageable) {
        return auditRepository.findWithFilters(
            eventType, userId, email, severity, 
            startDate != null ? startDate : LocalDateTime.now().minusDays(30),
            endDate != null ? endDate : LocalDateTime.now(),
            pageable
        );
    }

    /**
     * Busca logs críticos recientes
     */
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findRecentCriticalLogs(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditRepository.findRecentCriticalLogs(since);
    }

    /**
     * Busca actividad sospechosa por IP
     */
    @Transactional(readOnly = true)
    public List<SecurityAuditLog> findSuspiciousActivityByIp(String ipAddress, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditRepository.findSuspiciousActivityByIp(ipAddress, since);
    }

    /**
     * Obtiene estadísticas de eventos por severidad
     */
    @Transactional(readOnly = true)
    public AuditStats getAuditStats(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        
        long lowSeverityCount = auditRepository.countBySeveritySince(Severity.LOW.getValue(), since);
        long mediumSeverityCount = auditRepository.countBySeveritySince(Severity.MEDIUM.getValue(), since);
        long highSeverityCount = auditRepository.countBySeveritySince(Severity.HIGH.getValue(), since);
        long criticalSeverityCount = auditRepository.countBySeveritySince(Severity.CRITICAL.getValue(), since);
        
        return new AuditStats(lowSeverityCount, mediumSeverityCount, highSeverityCount, criticalSeverityCount);
    }

    /**
     * Limpia logs antiguos (para tarea programada)
     */
    @Transactional
    public int cleanupOldLogs() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90); // Mantener por 90 días
        return auditRepository.deleteOldLogs(cutoffDate);
    }

    /**
     * Obtiene la dirección IP del cliente
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Clase para estadísticas de auditoría
     */
    public static class AuditStats {
        private final long lowSeverityCount;
        private final long mediumSeverityCount;
        private final long highSeverityCount;
        private final long criticalSeverityCount;

        public AuditStats(long lowSeverityCount, long mediumSeverityCount, 
                         long highSeverityCount, long criticalSeverityCount) {
            this.lowSeverityCount = lowSeverityCount;
            this.mediumSeverityCount = mediumSeverityCount;
            this.highSeverityCount = highSeverityCount;
            this.criticalSeverityCount = criticalSeverityCount;
        }

        // Getters
        public long getLowSeverityCount() { return lowSeverityCount; }
        public long getMediumSeverityCount() { return mediumSeverityCount; }
        public long getHighSeverityCount() { return highSeverityCount; }
        public long getCriticalSeverityCount() { return criticalSeverityCount; }
        public long getTotalCount() { 
            return lowSeverityCount + mediumSeverityCount + highSeverityCount + criticalSeverityCount; 
        }
    }

    /**
     * Registra un evento de seguridad simplificado (para refresh tokens)
     */
    public void logSecurityEvent(String eventType, String description, String ipAddress, String email, boolean success) {
        EventType type;
        try {
            type = EventType.valueOf(eventType);
        } catch (IllegalArgumentException e) {
            // Si no existe el tipo, usar un tipo genérico
            type = success ? EventType.DATA_ACCESS : EventType.SYSTEM_ERROR;
        }
        
        // Usar severidades más cortas para evitar truncamiento
        Severity severity = success ? Severity.LOW : Severity.MEDIUM;
        
        // Llamar al método principal con los parámetros en el orden correcto
        logSecurityEvent(type, null, email != null ? email : "unknown", ipAddress != null ? ipAddress : "unknown", "API", description, severity);
    }
}
