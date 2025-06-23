package com.conaveg.cona.service;

import com.conaveg.cona.model.AuthenticationAttempt;
import com.conaveg.cona.repository.AuthenticationAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio para tracking y rate limiting de intentos de autenticación
 */
@Service
@Transactional
public class AuthenticationAttemptService {

    @Autowired
    private AuthenticationAttemptRepository attemptRepository;

    @Autowired
    private SecurityAuditService auditService;

    @Value("${app.security.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${app.security.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    @Value("${app.security.max-attempts-per-ip-per-hour:20}")
    private int maxAttemptsPerIpPerHour;

    /**
     * Registra un intento de autenticación
     */
    public void recordAuthenticationAttempt(String email, String ipAddress, String userAgent, 
                                          boolean successful, String failureReason) {
        AuthenticationAttempt attempt = new AuthenticationAttempt(
            email, ipAddress, successful, userAgent, failureReason
        );
        attemptRepository.save(attempt);

        // Si el intento falló, verificar si se debe registrar actividad sospechosa
        if (!successful) {
            checkForSuspiciousActivity(email, ipAddress, userAgent);
        }
    }

    /**
     * Verifica si una cuenta está bloqueada por demasiados intentos fallidos
     */
    @Transactional(readOnly = true)
    public boolean isAccountLocked(String email) {
        LocalDateTime lockoutPeriod = LocalDateTime.now().minusMinutes(lockoutDurationMinutes);
        long failedAttempts = attemptRepository.countFailedAttemptsByEmail(email, lockoutPeriod);
        
        return failedAttempts >= maxLoginAttempts;
    }

    /**
     * Verifica si una IP está bloqueada por demasiados intentos
     */
    @Transactional(readOnly = true)
    public boolean isIpBlocked(String ipAddress) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentAttempts = attemptRepository.countAttemptsByIp(ipAddress, oneHourAgo);
        
        return recentAttempts >= maxAttemptsPerIpPerHour;
    }

    /**
     * Obtiene el número de intentos fallidos recientes para un email
     */
    @Transactional(readOnly = true)
    public long getFailedAttemptsCount(String email) {
        LocalDateTime lockoutPeriod = LocalDateTime.now().minusMinutes(lockoutDurationMinutes);
        return attemptRepository.countFailedAttemptsByEmail(email, lockoutPeriod);
    }

    /**
     * Obtiene el número de intentos restantes antes del bloqueo
     */
    @Transactional(readOnly = true)
    public int getRemainingAttempts(String email) {
        long failedAttempts = getFailedAttemptsCount(email);
        return Math.max(0, maxLoginAttempts - (int) failedAttempts);
    }

    /**
     * Calcula el tiempo restante de bloqueo para una cuenta
     */
    @Transactional(readOnly = true)
    public LocalDateTime getLockoutExpiryTime(String email) {
        if (!isAccountLocked(email)) {
            return null;
        }

        AuthenticationAttempt lastFailedAttempt = attemptRepository.findLastFailedAttemptByEmail(email);
        
        if (lastFailedAttempt != null) {
            return lastFailedAttempt.getAttemptTime().plusMinutes(lockoutDurationMinutes);
        }
        
        return null;
    }

    /**
     * Resetea los intentos fallidos para un email (usado después de login exitoso)
     */
    @Transactional
    public void resetFailedAttempts(String email) {
        // Los intentos se mantienen para auditoría, pero se considera que el siguiente
        // período de lockout comenzará desde cero con el próximo intento fallido
        // No es necesario eliminar los registros, solo confiar en el período de tiempo
    }

    /**
     * Verifica actividad sospechosa y la registra
     */
    private void checkForSuspiciousActivity(String email, String ipAddress, String userAgent) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        // Verificar múltiples intentos fallidos desde la misma IP
        long ipFailedAttempts = attemptRepository.countFailedAttemptsByIp(ipAddress, oneHourAgo);
        if (ipFailedAttempts >= 10) { // Umbral para actividad sospechosa
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.SUSPICIOUS_ACTIVITY,
                null, email, ipAddress, userAgent,
                String.format("Multiple failed login attempts from IP: %d attempts in last hour", ipFailedAttempts),
                SecurityAuditService.Severity.HIGH
            );
        }

        // Verificar si la cuenta está siendo atacada
        long emailFailedAttempts = attemptRepository.countFailedAttemptsByEmail(email, oneHourAgo);
        if (emailFailedAttempts >= maxLoginAttempts) {
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.ACCOUNT_LOCKED,
                null, email, ipAddress, userAgent,
                String.format("Account locked due to %d failed attempts", emailFailedAttempts),
                SecurityAuditService.Severity.MEDIUM
            );
        }
    }

    /**
     * Obtiene estadísticas de intentos de autenticación
     */
    @Transactional(readOnly = true)
    public AuthenticationStats getAuthenticationStats(String email) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        long attemptsLastHour = attemptRepository.countAttemptsByEmail(email, oneHourAgo);
        long failedAttemptsLastHour = attemptRepository.countFailedAttemptsByEmail(email, oneHourAgo);
        long attemptsLastDay = attemptRepository.countAttemptsByEmail(email, oneDayAgo);
        
        boolean isLocked = isAccountLocked(email);
        LocalDateTime lockoutExpiry = getLockoutExpiryTime(email);
        int remainingAttempts = getRemainingAttempts(email);
        
        return new AuthenticationStats(
            attemptsLastHour,
            failedAttemptsLastHour,
            attemptsLastDay,
            isLocked,
            lockoutExpiry,
            remainingAttempts
        );
    }

    /**
     * Limpia intentos antiguos (para tarea programada)
     */
    @Transactional
    public int cleanupOldAttempts() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30); // Mantener por 30 días
        return attemptRepository.deleteOldAttempts(cutoffDate);
    }

    /**
     * Verifica si un usuario puede intentar renovar token (rate limiting específico)
     */
    public boolean canAttemptRefresh(String email, String ipAddress) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        
        // Máximo 10 intentos de refresh por hora por usuario
        long userRefreshAttempts = attemptRepository.countByEmailAndAttemptTimeAfterAndAttemptType(
            email, oneHourAgo, "REFRESH"
        );
        
        // Máximo 20 intentos de refresh por hora por IP
        long ipRefreshAttempts = attemptRepository.countByIpAddressAndAttemptTimeAfterAndAttemptType(
            ipAddress, oneHourAgo, "REFRESH"  
        );
        
        return userRefreshAttempts < 10 && ipRefreshAttempts < 20;
    }
    
    /**
     * Registra un intento de renovación de token
     */
    public void recordRefreshAttempt(String email, String ipAddress, boolean successful) {
        AuthenticationAttempt attempt = new AuthenticationAttempt();
        attempt.setEmail(email);
        attempt.setIpAddress(ipAddress);
        attempt.setSuccessful(successful);
        attempt.setAttemptTime(LocalDateTime.now());
        attempt.setAttemptType("REFRESH");
        attempt.setUserAgent("API");
        
        if (!successful) {
            attempt.setFailureReason("Token refresh failed");
        }
        
        attemptRepository.save(attempt);
    }

    /**
     * Clase para estadísticas de autenticación
     */
    public static class AuthenticationStats {
        private final long attemptsLastHour;
        private final long failedAttemptsLastHour;
        private final long attemptsLastDay;
        private final boolean isLocked;
        private final LocalDateTime lockoutExpiry;
        private final int remainingAttempts;

        public AuthenticationStats(long attemptsLastHour, long failedAttemptsLastHour, 
                                 long attemptsLastDay, boolean isLocked, 
                                 LocalDateTime lockoutExpiry, int remainingAttempts) {
            this.attemptsLastHour = attemptsLastHour;
            this.failedAttemptsLastHour = failedAttemptsLastHour;
            this.attemptsLastDay = attemptsLastDay;
            this.isLocked = isLocked;
            this.lockoutExpiry = lockoutExpiry;
            this.remainingAttempts = remainingAttempts;
        }

        // Getters
        public long getAttemptsLastHour() { return attemptsLastHour; }
        public long getFailedAttemptsLastHour() { return failedAttemptsLastHour; }
        public long getAttemptsLastDay() { return attemptsLastDay; }
        public boolean isLocked() { return isLocked; }
        public LocalDateTime getLockoutExpiry() { return lockoutExpiry; }
        public int getRemainingAttempts() { return remainingAttempts; }
    }
}
