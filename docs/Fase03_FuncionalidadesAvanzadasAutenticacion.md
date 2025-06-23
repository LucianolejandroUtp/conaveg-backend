# FASE 3: FUNCIONALIDADES AVANZADAS DE AUTENTICACIÓN - SISTEMA CONA

## 📋 **INFORMACIÓN GENERAL**

**Documento**: Fase 3 - Funcionalidades Avanzadas de Autenticación  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Fecha de Creación**: Junio 2025  
**Estado**: 🔄 **PLANIFICACIÓN DETALLADA**  
**Prioridad**: 🟡 **MEDIA-ALTA**  
**Duración Estimada**: 2-3 semanas  
**Completitud Actual**: **0%** (Fase pendiente de implementación)

---

## 🎯 **OBJETIVO DE LA FASE**

Implementar funcionalidades avanzadas de autenticación que mejoren la seguridad, usabilidad y robustez del sistema de autenticación existente, agregando capacidades como renovación de tokens, recuperación de contraseñas, protección contra ataques de fuerza bruta y auditoría de seguridad.

### **Prerrequisitos Completados** ✅
- ✅ **FASE 1**: AuthController con 4 endpoints básicos (login, me, logout, validate)
- ✅ **FASE 2**: Sistema de autorización por roles con JWT completo
- ✅ **Infraestructura**: SecurityConfig, JwtUtil, AuthenticationService funcionales

---

## 🚀 **IMPLEMENTACIONES REQUERIDAS**

### **3.1 REFRESH TOKEN SYSTEM** 🔄

#### **3.1.1 Endpoint POST /api/auth/refresh**
**Propósito**: Renovar tokens JWT antes de su expiración sin requerir credenciales

**Ubicación**: `src/main/java/com/conaveg/cona/controller/AuthController.java`

**Implementación Requerida**:
```java
/**
 * Endpoint para renovar token JWT
 */
@Operation(summary = "Renovar token", description = "Renueva un token JWT válido antes de su expiración.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
    @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
    @ApiResponse(responseCode = "400", description = "Token no renovable")
})
@PostMapping("/refresh")
public ResponseEntity<?> refreshToken(
        @Parameter(description = "Token JWT actual en el header Authorization")
        @RequestHeader("Authorization") String authHeader) {
    // Implementación aquí
}
```

#### **3.1.2 Mejoras en JwtUtil**
**Ubicación**: `src/main/java/com/conaveg/cona/security/JwtUtil.java`

**Métodos a Implementar**:
```java
/**
 * Verifica si un token puede ser renovado (no está muy próximo a expirar)
 */
public boolean canRefreshToken(String token) {
    // Verificar si el token está en la ventana de renovación
    // Ejemplo: renovable si le quedan menos de 2 horas pero más de 30 minutos
}

/**
 * Genera un nuevo token basado en un token existente válido
 */
public String refreshToken(String oldToken) {
    // Extraer información del token actual
    // Generar nuevo token con tiempo de expiración renovado
}

/**
 * Obtiene el tiempo restante antes de la expiración
 */
public long getTimeToExpiration(String token) {
    // Calcular minutos/horas restantes
}
```

#### **3.1.3 RefreshTokenRequestDTO**
**Ubicación**: `src/main/java/com/conaveg/cona/dto/RefreshTokenRequestDTO.java`

```java
package com.conaveg.cona.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitudes de renovación de token
 */
public class RefreshTokenRequestDTO {
    
    @NotBlank(message = "El token es requerido")
    private String token;
    
    // Getters y setters
}
```

#### **3.1.4 RefreshTokenResponseDTO**
**Ubicación**: `src/main/java/com/conaveg/cona/dto/RefreshTokenResponseDTO.java`

```java
package com.conaveg.cona.dto;

/**
 * DTO de respuesta para renovación de token
 */
public class RefreshTokenResponseDTO {
    private String token;
    private String tokenType = "Bearer";
    private long expiresIn; // Segundos hasta expiración
    private String refreshedAt; // Timestamp de renovación
    
    // Constructores, getters y setters
}
```

---

### **3.2 PASSWORD RECOVERY SYSTEM** 🔑

#### **3.2.1 Endpoint POST /api/auth/forgot-password**
**Propósito**: Iniciar proceso de recuperación de contraseña

**Implementación en AuthController**:
```java
/**
 * Endpoint para solicitar recuperación de contraseña
 */
@Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un token de recuperación al email del usuario.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Email de recuperación enviado"),
    @ApiResponse(responseCode = "404", description = "Email no encontrado"),
    @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes")
})
@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequestDTO request) {
    // Implementación aquí
}
```

#### **3.2.2 Endpoint POST /api/auth/reset-password**
**Propósito**: Resetear contraseña usando token temporal

**Implementación en AuthController**:
```java
/**
 * Endpoint para resetear contraseña con token
 */
@Operation(summary = "Resetear contraseña", description = "Cambia la contraseña usando un token de recuperación.")
@ApiResponses({
    @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
    @ApiResponse(responseCode = "400", description = "Token inválido o expirado"),
    @ApiResponse(responseCode = "422", description = "Nueva contraseña no válida")
})
@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(
        @Valid @RequestBody ResetPasswordRequestDTO request) {
    // Implementación aquí
}
```

#### **3.2.3 Modelo PasswordResetToken**
**Ubicación**: `src/main/java/com/conaveg/cona/model/PasswordResetToken.java`

```java
package com.conaveg.cona.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para tokens de recuperación de contraseña
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private boolean used = false;
    
    // Constructores, getters y setters
}
```

#### **3.2.4 Repository PasswordResetTokenRepository**
**Ubicación**: `src/main/java/com/conaveg/cona/repository/PasswordResetTokenRepository.java`

```java
package com.conaveg.cona.repository;

import com.conaveg.cona.model.PasswordResetToken;
import com.conaveg.cona.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);
    
    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);
    
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.user = :user")
    void markAllUserTokensAsUsed(User user);
}
```

#### **3.2.5 Servicio PasswordRecoveryService**
**Ubicación**: `src/main/java/com/conaveg/cona/service/PasswordRecoveryService.java`

```java
package com.conaveg.cona.service;

import com.conaveg.cona.dto.ForgotPasswordRequestDTO;
import com.conaveg.cona.dto.ResetPasswordRequestDTO;
import com.conaveg.cona.model.PasswordResetToken;
import com.conaveg.cona.model.User;
import com.conaveg.cona.repository.PasswordResetTokenRepository;
import com.conaveg.cona.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para recuperación de contraseñas
 */
@Service
public class PasswordRecoveryService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Genera token de recuperación y envía email
     */
    public boolean processForgotPassword(ForgotPasswordRequestDTO request) {
        // Buscar usuario por email
        // Generar token único
        // Guardar token en BD
        // Enviar email con token
        // Return true si exitoso
    }
    
    /**
     * Procesa el reset de contraseña con token
     */
    public boolean processResetPassword(ResetPasswordRequestDTO request) {
        // Validar token
        // Verificar expiración
        // Cambiar contraseña
        // Marcar token como usado
        // Return true si exitoso
    }
    
    /**
     * Genera token aleatorio seguro
     */
    private String generateSecureToken() {
        // Implementar generación de token criptográficamente seguro
    }
    
    /**
     * Limpia tokens expirados (para tarea programada)
     */
    public void cleanupExpiredTokens() {
        // Eliminar tokens expirados de la BD
    }
}
```

#### **3.2.6 DTOs para Password Recovery**

**ForgotPasswordRequestDTO**:
```java
package com.conaveg.cona.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequestDTO {
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "Email inválido")
    private String email;
    
    // Getters y setters
}
```

**ResetPasswordRequestDTO**:
```java
package com.conaveg.cona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequestDTO {
    
    @NotBlank(message = "El token es requerido")
    private String token;
    
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
    private String newPassword;
    
    @NotBlank(message = "La confirmación de contraseña es requerida")
    private String confirmPassword;
    
    // Getters y setters
    
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
```

---

### **3.3 RATE LIMITING & BRUTE FORCE PROTECTION** 🛡️

#### **3.3.1 Modelo AuthenticationAttempt**
**Ubicación**: `src/main/java/com/conaveg/cona/model/AuthenticationAttempt.java`

```java
package com.conaveg.cona.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para tracking de intentos de autenticación
 */
@Entity
@Table(name = "authentication_attempts")
public class AuthenticationAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column(nullable = false)
    private LocalDateTime attemptTime;
    
    @Column(nullable = false)
    private boolean successful;
    
    @Column
    private String userAgent;
    
    @Column
    private String failureReason;
    
    // Constructores, getters y setters
}
```

#### **3.3.2 Repository AuthenticationAttemptRepository**
**Ubicación**: `src/main/java/com/conaveg/cona/repository/AuthenticationAttemptRepository.java`

```java
package com.conaveg.cona.repository;

import com.conaveg.cona.model.AuthenticationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuthenticationAttemptRepository extends JpaRepository<AuthenticationAttempt, Long> {
    
    @Query("SELECT COUNT(a) FROM AuthenticationAttempt a WHERE a.email = :email AND a.successful = false AND a.attemptTime > :since")
    int countFailedAttemptsByEmailSince(String email, LocalDateTime since);
    
    @Query("SELECT COUNT(a) FROM AuthenticationAttempt a WHERE a.ipAddress = :ipAddress AND a.successful = false AND a.attemptTime > :since")
    int countFailedAttemptsByIpSince(String ipAddress, LocalDateTime since);
    
    List<AuthenticationAttempt> findByEmailAndAttemptTimeAfterOrderByAttemptTimeDesc(String email, LocalDateTime since);
    
    void deleteByAttemptTimeBefore(LocalDateTime cutoff);
}
```

#### **3.3.3 Servicio AuthenticationAttemptService**
**Ubicación**: `src/main/java/com/conaveg/cona/service/AuthenticationAttemptService.java`

```java
package com.conaveg.cona.service;

import com.conaveg.cona.model.AuthenticationAttempt;
import com.conaveg.cona.repository.AuthenticationAttemptRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para manejo de intentos de autenticación y rate limiting
 */
@Service
public class AuthenticationAttemptService {
    
    private static final int MAX_ATTEMPTS_PER_EMAIL = 5;
    private static final int MAX_ATTEMPTS_PER_IP = 10;
    private static final int LOCKOUT_DURATION_MINUTES = 15;
    
    @Autowired
    private AuthenticationAttemptRepository attemptRepository;
    
    /**
     * Verifica si un email está bloqueado por exceso de intentos fallidos
     */
    public boolean isEmailBlocked(String email) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        int failedAttempts = attemptRepository.countFailedAttemptsByEmailSince(email, since);
        return failedAttempts >= MAX_ATTEMPTS_PER_EMAIL;
    }
    
    /**
     * Verifica si una IP está bloqueada por exceso de intentos fallidos
     */
    public boolean isIpBlocked(String ipAddress) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(LOCKOUT_DURATION_MINUTES);
        int failedAttempts = attemptRepository.countFailedAttemptsByIpSince(ipAddress, since);
        return failedAttempts >= MAX_ATTEMPTS_PER_IP;
    }
    
    /**
     * Registra un intento de autenticación
     */
    public void recordAuthenticationAttempt(String email, String ipAddress, boolean successful, 
                                          String userAgent, String failureReason) {
        AuthenticationAttempt attempt = new AuthenticationAttempt();
        attempt.setEmail(email);
        attempt.setIpAddress(ipAddress);
        attempt.setSuccessful(successful);
        attempt.setAttemptTime(LocalDateTime.now());
        attempt.setUserAgent(userAgent);
        attempt.setFailureReason(failureReason);
        
        attemptRepository.save(attempt);
    }
    
    /**
     * Obtiene la IP del cliente desde el request
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    /**
     * Limpia intentos antiguos (para tarea programada)
     */
    public void cleanupOldAttempts() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        attemptRepository.deleteByAttemptTimeBefore(cutoff);
    }
}
```

#### **3.3.4 Actualización del AuthenticationService**
**Ubicación**: `src/main/java/com/conaveg/cona/service/AuthenticationService.java`

**Métodos a Agregar**:
```java
@Autowired
private AuthenticationAttemptService attemptService;

/**
 * Autentica un usuario con protección contra brute force
 */
public LoginResponseDTO authenticateUserWithProtection(LoginRequestDTO loginRequest, 
                                                      HttpServletRequest request) {
    String ipAddress = attemptService.getClientIpAddress(request);
    String userAgent = request.getHeader("User-Agent");
    
    // Verificar rate limiting
    if (attemptService.isEmailBlocked(loginRequest.getEmail())) {
        attemptService.recordAuthenticationAttempt(loginRequest.getEmail(), ipAddress, 
                                                 false, userAgent, "Email bloqueado por rate limiting");
        throw new RuntimeException("Demasiados intentos fallidos. Cuenta temporalmente bloqueada.");
    }
    
    if (attemptService.isIpBlocked(ipAddress)) {
        attemptService.recordAuthenticationAttempt(loginRequest.getEmail(), ipAddress, 
                                                 false, userAgent, "IP bloqueada por rate limiting");
        throw new RuntimeException("Demasiados intentos desde esta IP. Acceso temporalmente bloqueado.");
    }
    
    try {
        // Lógica de autenticación existente
        LoginResponseDTO response = authenticateUser(loginRequest);
        
        // Registrar intento exitoso
        attemptService.recordAuthenticationAttempt(loginRequest.getEmail(), ipAddress, 
                                                 true, userAgent, null);
        
        return response;
    } catch (RuntimeException e) {
        // Registrar intento fallido
        attemptService.recordAuthenticationAttempt(loginRequest.getEmail(), ipAddress, 
                                                 false, userAgent, e.getMessage());
        throw e;
    }
}
```

---

### **3.4 SECURITY AUDIT & LOGGING** 📊

#### **3.4.1 Modelo SecurityAuditLog**
**Ubicación**: `src/main/java/com/conaveg/cona/model/SecurityAuditLog.java`

```java
package com.conaveg.cona.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para logs de auditoría de seguridad
 */
@Entity
@Table(name = "security_audit_logs")
public class SecurityAuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String eventType; // LOGIN, LOGOUT, PASSWORD_CHANGE, FAILED_AUTH, etc.
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Column
    private String userAgent;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(length = 1000)
    private String details;
    
    @Column(nullable = false)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    
    // Constructores, getters y setters
}
```

#### **3.4.2 Servicio SecurityAuditService**
**Ubicación**: `src/main/java/com/conaveg/cona/service/SecurityAuditService.java`

```java
package com.conaveg.cona.service;

import com.conaveg.cona.model.SecurityAuditLog;
import com.conaveg.cona.repository.SecurityAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para auditoría de seguridad
 */
@Service
public class SecurityAuditService {
    
    @Autowired
    private SecurityAuditLogRepository auditRepository;
    
    public enum EventType {
        LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, PASSWORD_CHANGED, 
        TOKEN_REFRESHED, PASSWORD_RESET_REQUESTED, PASSWORD_RESET_COMPLETED,
        ACCOUNT_LOCKED, SUSPICIOUS_ACTIVITY
    }
    
    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    /**
     * Registra un evento de seguridad
     */
    public void logSecurityEvent(EventType eventType, Long userId, String email, 
                                String ipAddress, String userAgent, String details, 
                                Severity severity) {
        SecurityAuditLog log = new SecurityAuditLog();
        log.setEventType(eventType.name());
        log.setUserId(userId);
        log.setEmail(email);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);
        log.setSeverity(severity.name());
        
        auditRepository.save(log);
    }
    
    /**
     * Registra login exitoso
     */
    public void logSuccessfulLogin(Long userId, String email, HttpServletRequest request) {
        logSecurityEvent(EventType.LOGIN_SUCCESS, userId, email,
                        getClientIp(request), request.getHeader("User-Agent"),
                        "Usuario autenticado exitosamente", Severity.LOW);
    }
    
    /**
     * Registra login fallido
     */
    public void logFailedLogin(String email, HttpServletRequest request, String reason) {
        logSecurityEvent(EventType.LOGIN_FAILED, null, email,
                        getClientIp(request), request.getHeader("User-Agent"),
                        "Login fallido: " + reason, Severity.MEDIUM);
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
```

---

### **3.5 EMAIL SERVICE** 📧

#### **3.5.1 Servicio EmailService**
**Ubicación**: `src/main/java/com/conaveg/cona/service/EmailService.java`

```java
package com.conaveg.cona.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio para envío de emails
 */
@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.email.from:noreply@conaveg.com}")
    private String fromEmail;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    /**
     * Envía email de recuperación de contraseña
     */
    public void sendPasswordResetEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Recuperación de Contraseña - Sistema CONA");
        
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        String content = String.format(
            "Hola,\n\n" +
            "Has solicitado recuperar tu contraseña en el Sistema CONA.\n\n" +
            "Haz clic en el siguiente enlace para crear una nueva contraseña:\n%s\n\n" +
            "Este enlace es válido por 24 horas.\n\n" +
            "Si no solicitaste este cambio, puedes ignorar este email.\n\n" +
            "Saludos,\nEquipo CONA",
            resetUrl
        );
        
        message.setText(content);
        mailSender.send(message);
    }
    
    /**
     * Envía notificación de cambio de contraseña exitoso
     */
    public void sendPasswordChangeConfirmation(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Contraseña Cambiada - Sistema CONA");
        
        String content = 
            "Hola,\n\n" +
            "Tu contraseña ha sido cambiada exitosamente en el Sistema CONA.\n\n" +
            "Si no realizaste este cambio, contacta inmediatamente al administrador del sistema.\n\n" +
            "Saludos,\nEquipo CONA";
        
        message.setText(content);
        mailSender.send(message);
    }
}
```

#### **3.5.2 Configuración Email en application.properties**
**Ubicación**: `src/main/resources/application.properties`

**Configuraciones a Agregar**:
```properties
# Configuración de Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:}
spring.mail.password=${EMAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Configuraciones de la aplicación
app.email.from=noreply@conaveg.com
app.base-url=http://localhost:8080

# Configuraciones de seguridad
app.security.max-login-attempts=5
app.security.lockout-duration-minutes=15
app.security.password-reset-token-validity-hours=24
```

---

## 📊 **SCRIPTS DE BASE DE DATOS**

### **3.6.1 Migration para nuevas tablas**
**Ubicación**: `docs/database_migrations/fase03_auth_advanced.sql`

```sql
-- Migración Fase 3: Funcionalidades Avanzadas de Autenticación

-- Tabla para tokens de recuperación de contraseña
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expiry_date (expiry_date)
);

-- Tabla para intentos de autenticación
CREATE TABLE authentication_attempts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    attempt_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    successful BOOLEAN NOT NULL,
    user_agent TEXT,
    failure_reason VARCHAR(500),
    
    INDEX idx_email_time (email, attempt_time),
    INDEX idx_ip_time (ip_address, attempt_time),
    INDEX idx_attempt_time (attempt_time)
);

-- Tabla para logs de auditoría de seguridad
CREATE TABLE security_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    user_id BIGINT,
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details VARCHAR(1000),
    severity VARCHAR(20) NOT NULL,
    
    INDEX idx_event_type (event_type),
    INDEX idx_user_id (user_id),
    INDEX idx_email (email),
    INDEX idx_timestamp (timestamp),
    INDEX idx_severity (severity)
);
```

---

## 🧪 **TESTING STRATEGY**

### **3.7.1 Tests para Refresh Token**
**Ubicación**: `src/test/java/com/conaveg/cona/controller/RefreshTokenTest.java`

```java
@WebMvcTest(AuthController.class)
class RefreshTokenTest {
    
    @Test
    void refreshToken_WithValidToken_ShouldReturnNewToken() {
        // Test para token válido
    }
    
    @Test
    void refreshToken_WithExpiredToken_ShouldReturnUnauthorized() {
        // Test para token expirado
    }
    
    @Test
    void refreshToken_WithNonRefreshableToken_ShouldReturnBadRequest() {
        // Test para token no renovable
    }
}
```

### **3.7.2 Tests para Password Recovery**
**Ubicación**: `src/test/java/com/conaveg/cona/service/PasswordRecoveryServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class PasswordRecoveryServiceTest {
    
    @Test
    void forgotPassword_WithValidEmail_ShouldSendEmail() {
        // Test para email válido
    }
    
    @Test
    void resetPassword_WithValidToken_ShouldChangePassword() {
        // Test para reset exitoso
    }
    
    @Test
    void resetPassword_WithExpiredToken_ShouldFail() {
        // Test para token expirado
    }
}
```

### **3.7.3 Tests para Rate Limiting**
**Ubicación**: `src/test/java/com/conaveg/cona/service/AuthenticationAttemptServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class AuthenticationAttemptServiceTest {
    
    @Test
    void isEmailBlocked_AfterMaxAttempts_ShouldReturnTrue() {
        // Test para bloqueo por email
    }
    
    @Test
    void isIpBlocked_AfterMaxAttempts_ShouldReturnTrue() {
        // Test para bloqueo por IP
    }
}
```

---

## 📋 **CHECKLIST DE IMPLEMENTACIÓN**

### **✅ PREREQUISITOS**
- [ ] Verificar que las Fases 1 y 2 estén 100% completadas
- [ ] Confirmar que el AuthController actual funciona correctamente
- [ ] Validar que el sistema JWT está operativo
- [ ] Verificar conectividad de base de datos

### **🔄 REFRESH TOKEN SYSTEM (Semana 1)**
- [ ] Agregar métodos a JwtUtil (canRefreshToken, refreshToken, getTimeToExpiration)
- [ ] Crear RefreshTokenRequestDTO y RefreshTokenResponseDTO
- [ ] Implementar endpoint POST /api/auth/refresh en AuthController
- [ ] Actualizar AuthenticationService con lógica de refresh
- [ ] Crear tests unitarios e integración
- [ ] Documentar endpoint en Swagger
- [ ] Validar funcionamiento con Postman/Swagger UI

### **🔑 PASSWORD RECOVERY SYSTEM (Semana 2)**
- [ ] Crear modelo PasswordResetToken
- [ ] Implementar PasswordResetTokenRepository
- [ ] Crear ForgotPasswordRequestDTO y ResetPasswordRequestDTO
- [ ] Implementar PasswordRecoveryService
- [ ] Crear endpoints forgot-password y reset-password en AuthController
- [ ] Configurar EmailService con propiedades SMTP
- [ ] Ejecutar script de migración de BD
- [ ] Crear tests para el flujo completo
- [ ] Validar envío de emails (usar Mailtrap para testing)

### **🛡️ RATE LIMITING & BRUTE FORCE PROTECTION (Semana 2-3)**
- [ ] Crear modelo AuthenticationAttempt
- [ ] Implementar AuthenticationAttemptRepository
- [ ] Desarrollar AuthenticationAttemptService
- [ ] Actualizar AuthenticationService con protección
- [ ] Modificar AuthController para usar nueva lógica
- [ ] Crear tests para rate limiting
- [ ] Validar bloqueos por IP y email
- [ ] Configurar límites en application.properties

### **📊 SECURITY AUDIT & LOGGING (Semana 3)**
- [ ] Crear modelo SecurityAuditLog
- [ ] Implementar SecurityAuditLogRepository
- [ ] Desarrollar SecurityAuditService
- [ ] Integrar logging en todos los endpoints de auth
- [ ] Crear tests para auditoría
- [ ] Configurar rotación de logs si es necesario

### **🧪 TESTING & VALIDATION (Semana 3)**
- [ ] Ejecutar todos los tests unitarios
- [ ] Realizar tests de integración completos
- [ ] Validar endpoints con Swagger UI
- [ ] Probar rate limiting con scripts automatizados
- [ ] Verificar logs de auditoría
- [ ] Documentar nuevos endpoints
- [ ] Actualizar documentación de API

---

## 🚀 **CRITERIOS DE ACEPTACIÓN**

### **Funcionales**
1. ✅ **Refresh Token**: Los usuarios pueden renovar tokens JWT antes de expiración
2. ✅ **Password Recovery**: Sistema completo de recuperación por email funcional
3. ✅ **Rate Limiting**: Protección contra brute force por email e IP
4. ✅ **Audit Logging**: Todos los eventos de seguridad se registran correctamente
5. ✅ **Email Service**: Envío de emails de recuperación funcional

### **No Funcionales**
1. ✅ **Performance**: Endpoints responden en menos de 500ms bajo carga normal
2. ✅ **Security**: Tokens de recuperación expiran en 24 horas máximo
3. ✅ **Reliability**: Rate limiting bloquea efectivamente después de 5 intentos fallidos
4. ✅ **Usability**: Mensajes de error son claros y específicos
5. ✅ **Maintainability**: Código bien documentado y testeado

### **Técnicos**
1. ✅ **Test Coverage**: Mínimo 80% de cobertura en nuevos componentes
2. ✅ **Documentation**: Swagger UI actualizado con nuevos endpoints
3. ✅ **Database**: Migraciones ejecutadas sin errores
4. ✅ **Configuration**: Propiedades configurables vía environment variables
5. ✅ **Logging**: Logs estructurados para facilitar debugging

---

## 📈 **IMPACTO EN EL PROYECTO**

### **Completitud Esperada Post-Fase 3**
- **Autenticación Avanzada**: De 0% a **100%** (+100%)
- **Seguridad General**: De 98% a **100%** (+2%)
- **Completitud General**: De 85% a **90%** (+5%)

### **Beneficios Esperados**
1. **🔒 Seguridad Mejorada**: Protección robusta contra ataques comunes
2. **👥 Mejor UX**: Usuarios pueden recuperar contraseñas fácilmente
3. **📊 Visibilidad**: Logs de auditoría para compliance y debugging
4. **⚡ Performance**: Refresh tokens reducen re-autenticaciones
5. **🛡️ Compliance**: Sistema cumple estándares de seguridad empresariales

---

## 🔗 **DEPENDENCIAS Y PRÓXIMOS PASOS**

### **Dependencias de Esta Fase**
- ✅ FASE 1: AuthController básico (COMPLETADO)
- ✅ FASE 2: Sistema de autorización por roles (COMPLETADO)
- ✅ Base de datos MariaDB configurada
- ✅ Servidor SMTP disponible (para emails)

### **Habilita las Siguientes Fases**
- **FASE 4**: Gestión de errores y validaciones avanzadas
- **FASE 5**: Funcionalidades de negocio específicas
- **FASE 6**: Performance optimization y caching
- **FASE 7**: Frontend con autenticación completa

---

**📅 Fecha de Actualización**: Junio 2025  
**👨‍💻 Responsable**: Equipo de Desarrollo CONA  
**📋 Estado**: **LISTO PARA IMPLEMENTACIÓN**
