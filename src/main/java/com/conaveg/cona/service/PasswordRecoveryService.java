package com.conaveg.cona.service;

import com.conaveg.cona.model.PasswordResetToken;
import com.conaveg.cona.model.User;
import com.conaveg.cona.repository.PasswordResetTokenRepository;
import com.conaveg.cona.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Servicio para recuperación de contraseñas
 */
@Service
@Transactional
public class PasswordRecoveryService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecurityAuditService auditService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.security.password-reset-token-validity-hours:24}")
    private int tokenValidityHours;

    @Value("${app.security.max-password-reset-requests-per-hour:3}")
    private int maxResetRequestsPerHour;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Inicia el proceso de recuperación de contraseña
     */
    public boolean initiatePasswordReset(String email, String ipAddress, String userAgent) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                // Por seguridad, no revelamos si el email existe o no
                auditService.logSecurityEvent(
                    SecurityAuditService.EventType.PASSWORD_RESET_REQUESTED,
                    null, email, ipAddress, userAgent,
                    "Password reset requested for non-existent email",
                    SecurityAuditService.Severity.LOW
                );
                return true; // Retornamos true para no revelar información
            }

            User user = userOpt.get();

            // Verificar rate limiting
            if (!checkRateLimiting(user)) {
                auditService.logSecurityEvent(
                    SecurityAuditService.EventType.PASSWORD_RESET_RATE_LIMITED,
                    user.getId(), email, ipAddress, userAgent,
                    "Password reset rate limit exceeded",
                    SecurityAuditService.Severity.MEDIUM
                );
                throw new RuntimeException("Demasiadas solicitudes de recuperación. Intente más tarde.");
            }

            // Invalidar tokens existentes del usuario
            tokenRepository.markAllUserTokensAsUsed(user);

            // Generar nuevo token
            String token = generateSecureToken();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(tokenValidityHours);

            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
            tokenRepository.save(resetToken);

            // Enviar email
            emailService.sendPasswordResetEmail(email, token);

            // Log del evento
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.PASSWORD_RESET_REQUESTED,
                user.getId(), email, ipAddress, userAgent,
                "Password reset token generated and sent",
                SecurityAuditService.Severity.LOW
            );

            return true;

        } catch (Exception e) {
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.PASSWORD_RESET_FAILED,
                null, email, ipAddress, userAgent,
                "Error during password reset initiation: " + e.getMessage(),
                SecurityAuditService.Severity.HIGH
            );
            throw new RuntimeException("Error al procesar la solicitud de recuperación");
        }
    }

    /**
     * Valida un token de recuperación
     */
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findValidToken(token, LocalDateTime.now());
        return tokenOpt.isPresent();
    }

    /**
     * Resetea la contraseña usando un token válido
     */
    public boolean resetPassword(String token, String newPassword, String ipAddress, String userAgent) {
        try {
            Optional<PasswordResetToken> tokenOpt = tokenRepository.findValidToken(token, LocalDateTime.now());
            
            if (tokenOpt.isEmpty()) {
                auditService.logSecurityEvent(
                    SecurityAuditService.EventType.PASSWORD_RESET_FAILED,
                    null, "", ipAddress, userAgent,
                    "Invalid or expired password reset token used",
                    SecurityAuditService.Severity.HIGH
                );
                return false;
            }

            PasswordResetToken resetToken = tokenOpt.get();
            User user = resetToken.getUser();

            // Validar que la nueva contraseña no sea igual a la actual
            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
            }

            // Actualizar contraseña
            user.setPassword(passwordEncoder.encode(newPassword));
            // Nota: updated_at se actualiza automáticamente por la base de datos
            userRepository.save(user);

            // Marcar token como usado
            resetToken.setUsed(true);
            tokenRepository.save(resetToken);

            // Invalidar todos los otros tokens del usuario
            tokenRepository.markAllUserTokensAsUsed(user);

            // Enviar confirmación por email
            emailService.sendPasswordChangeConfirmation(user.getEmail());

            // Log del evento exitoso
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.PASSWORD_CHANGED,
                user.getId(), user.getEmail(), ipAddress, userAgent,
                "Password successfully changed via reset token",
                SecurityAuditService.Severity.MEDIUM
            );

            return true;

        } catch (Exception e) {
            auditService.logSecurityEvent(
                SecurityAuditService.EventType.PASSWORD_RESET_FAILED,
                null, "", ipAddress, userAgent,
                "Error during password reset: " + e.getMessage(),
                SecurityAuditService.Severity.HIGH
            );
            throw new RuntimeException("Error al cambiar la contraseña: " + e.getMessage());
        }
    }

    /**
     * Obtiene información del token (sin datos sensibles)
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByResetToken(String token) {
        return tokenRepository.findValidToken(token, LocalDateTime.now())
                .map(PasswordResetToken::getUser);
    }

    /**
     * Verifica rate limiting para solicitudes de reset
     */
    private boolean checkRateLimiting(User user) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long recentRequests = tokenRepository.countRecentTokensByUser(user, oneHourAgo);
        return recentRequests < maxResetRequestsPerHour;
    }

    /**
     * Genera un token seguro para recuperación
     */
    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Limpia tokens expirados (para tarea programada)
     */
    @Transactional
    public int cleanupExpiredTokens() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7); // Mantener por 7 días para auditoría
        return tokenRepository.deleteExpiredTokens(cutoffDate);
    }

    /**
     * Revoca todos los tokens de un usuario (útil para cambio de contraseña manual)
     */
    @Transactional
    public void revokeAllUserTokens(User user) {
        tokenRepository.markAllUserTokensAsUsed(user);
    }
}
