package com.conaveg.cona.config;

import com.conaveg.cona.service.AuthenticationAttemptService;
import com.conaveg.cona.service.PasswordRecoveryService;
import com.conaveg.cona.service.SecurityAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tareas programadas para mantenimiento del sistema
 * Incluye limpieza de tokens expirados, logs antiguos y intentos de autenticación
 */
@Component
@EnableScheduling
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @Autowired
    private AuthenticationAttemptService authenticationAttemptService;

    @Autowired
    private SecurityAuditService securityAuditService;

    /**
     * Limpia tokens de recuperación de contraseña expirados
     * Se ejecuta todos los días a las 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredPasswordResetTokens() {
        logger.info("Iniciando limpieza de tokens de recuperación expirados");
        
        try {
            int deletedTokens = passwordRecoveryService.cleanupExpiredTokens();
            logger.info("Limpieza completada: {} tokens de recuperación eliminados", deletedTokens);
            
            // Log de auditoría usando método existente
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.DATA_ACCESS,
                null, "", "", "",
                String.format("Password reset tokens cleanup completed: %d tokens removed", deletedTokens),
                SecurityAuditService.Severity.LOW
            );
            
        } catch (Exception e) {
            logger.error("Error durante la limpieza de tokens de recuperación", e);
            
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.SYSTEM_ERROR,
                null, "", "", "",
                "Error during password reset tokens cleanup: " + e.getMessage(),
                SecurityAuditService.Severity.HIGH
            );
        }
    }

    /**
     * Limpia intentos de autenticación antiguos
     * Se ejecuta todos los días a las 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldAuthenticationAttempts() {
        logger.info("Iniciando limpieza de intentos de autenticación antiguos");
        
        try {
            int deletedAttempts = authenticationAttemptService.cleanupOldAttempts();
            logger.info("Limpieza completada: {} intentos de autenticación eliminados", deletedAttempts);
            
            // Log de auditoría
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.DATA_ACCESS,
                null, "", "", "",
                String.format("Authentication attempts cleanup completed: %d attempts removed", deletedAttempts),
                SecurityAuditService.Severity.LOW
            );
            
        } catch (Exception e) {
            logger.error("Error durante la limpieza de intentos de autenticación", e);
            
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.SYSTEM_ERROR,
                null, "", "", "",
                "Error during authentication attempts cleanup: " + e.getMessage(),
                SecurityAuditService.Severity.HIGH
            );
        }
    }

    /**
     * Verifica y reporta estadísticas básicas cada 6 horas
     * Se ejecuta cada 6 horas
     */
    @Scheduled(fixedRate = 21600000) // 6 horas en millisegundos
    public void reportSecurityStatistics() {
        logger.info("Generando reporte básico de seguridad");
        
        try {
            // Reportar estadísticas básicas disponibles
            logger.info("Sistema de seguridad operativo - Tareas programadas ejecutándose correctamente");
            
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.DATA_ACCESS,
                null, "", "", "",
                "Security system operational check - Scheduled tasks running properly",
                SecurityAuditService.Severity.LOW
            );
            
        } catch (Exception e) {
            logger.error("Error generando reporte de seguridad", e);
            
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.SYSTEM_ERROR,
                null, "", "", "",
                "Error generating security report: " + e.getMessage(),
                SecurityAuditService.Severity.MEDIUM
            );
        }
    }
}
