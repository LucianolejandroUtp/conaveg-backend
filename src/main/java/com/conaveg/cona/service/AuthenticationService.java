package com.conaveg.cona.service;

import com.conaveg.cona.dto.LoginRequestDTO;
import com.conaveg.cona.dto.LoginResponseDTO;
import com.conaveg.cona.dto.RefreshTokenRequestDTO;
import com.conaveg.cona.dto.RefreshTokenResponseDTO;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.model.User;
import com.conaveg.cona.repository.UserRepository;
import com.conaveg.cona.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio de autenticación para manejo de login y tokens
 */
@Service
public class AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationAttemptService authenticationAttemptService;
    
    @Autowired
    private SecurityAuditService securityAuditService;
    
    /**
     * Autentica un usuario y genera un token JWT
     */
    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        // Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User user = userOptional.get();
        
        // Verificar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        // Generar token JWT
        String roleName = user.getRole() != null ? user.getRole().getNombre() : "USER";
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), roleName);
        
        // Convertir a DTO
        UserDTO userDTO = userService.getUserById(user.getId());
        
        // Crear respuesta
        return new LoginResponseDTO(token, userDTO, jwtUtil.getExpirationTime());
    }

    /**
     * Autentica un usuario con protección de rate limiting integrada
     */
    public LoginResponseDTO authenticateUserWithProtection(LoginRequestDTO loginRequest, 
                                                         String clientIp, String userAgent) {
        try {
            // El rate limiting ya se maneja en el RateLimitingFilter
            // Aquí solo procesamos la autenticación normal
            
            LoginResponseDTO response = authenticateUser(loginRequest);
            
            // Registrar intento exitoso para estadísticas
            authenticationAttemptService.recordAuthenticationAttempt(
                loginRequest.getEmail(), clientIp, userAgent, true, null
            );
            
            // Log de auditoría
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.LOGIN_SUCCESS,
                response.getUser().getId(),
                loginRequest.getEmail(),
                clientIp, userAgent,
                "Successful login for user: " + loginRequest.getEmail(),
                SecurityAuditService.Severity.MEDIUM
            );
            
            return response;
            
        } catch (RuntimeException e) {
            // Registrar intento fallido
            authenticationAttemptService.recordAuthenticationAttempt(
                loginRequest.getEmail(), clientIp, userAgent, false, e.getMessage()
            );
            
            // Log de auditoría de fallo
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.LOGIN_FAILED,
                null,
                loginRequest.getEmail(),
                clientIp, userAgent,
                "Failed login attempt: " + e.getMessage(),
                SecurityAuditService.Severity.MEDIUM
            );
            
            throw e; // Re-lanzar la excepción
        }
    }
    
    /**
     * Valida un token JWT
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
      /**
     * Obtiene información del usuario desde un token (versión optimizada)
     */
    public UserDTO getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token inválido");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserDTO user = userService.getUserById(userId);
        
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return user;
    }
    
    /**
     * Obtiene información básica del usuario por email (para el filtro JWT)
     */
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return userService.getUserById(userOptional.get().getId());
    }
    
    /**
     * Renueva un token JWT válido
     */
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO refreshRequest) {
        String token = refreshRequest.getToken();
        String clientIp = refreshRequest.getClientIp();
        
        // 1. Validar token
        if (!jwtUtil.validateToken(token)) {
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.TOKEN_REFRESH_INVALID,
                null, "", clientIp, "",
                "Intento de renovación con token inválido",
                SecurityAuditService.Severity.HIGH
            );
            throw new RuntimeException("Token inválido");
        }
        
        // 2. Verificar si el token puede ser renovado
        if (!jwtUtil.canRefreshToken(token)) {
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.TOKEN_REFRESH_OUT_OF_WINDOW,
                null, "", clientIp, "",
                "Token fuera de ventana de renovación",
                SecurityAuditService.Severity.MEDIUM
            );
            throw new IllegalArgumentException("Token no está en ventana de renovación");
        }
        
        // 3. Aplicar rate limiting por usuario
        String email = jwtUtil.getEmailFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        if (!authenticationAttemptService.canAttemptRefresh(email, clientIp)) {
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.TOKEN_REFRESH_RATE_LIMITED,
                null, email, clientIp, "",
                "Rate limit excedido para renovación",
                SecurityAuditService.Severity.HIGH
            );
            throw new RuntimeException("Demasiados intentos de renovación");
        }
        
        try {
            // 4. Obtener información del usuario
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                throw new RuntimeException("Usuario no encontrado");
            }
            
            User user = userOptional.get();
            String roleName = user.getRole() != null ? user.getRole().getNombre() : "USER";
            
            // 5. Generar nuevo token
            String newToken = jwtUtil.generateToken(email, userId, roleName);
            
            // 6. Preparar respuesta con información de auditoría
            long expiresIn = jwtUtil.getExpirationTime() / 1000; // en segundos
            LocalDateTime issuedAt = LocalDateTime.now();
            LocalDateTime expiresAt = issuedAt.plusSeconds(expiresIn);
            String oldTokenHash = Integer.toHexString(token.hashCode()); // Hash simple para auditoría
            long minutesLeft = jwtUtil.getTimeToExpirationMinutes(token);
            
            RefreshTokenResponseDTO response = new RefreshTokenResponseDTO(
                newToken, expiresIn, issuedAt, expiresAt, oldTokenHash, minutesLeft
            );
            
            // 7. Registrar intento exitoso y auditoría
            authenticationAttemptService.recordRefreshAttempt(email, clientIp, true);
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.TOKEN_REFRESH_SUCCESS,
                userId, email, clientIp, "",
                "Token renovado exitosamente",
                SecurityAuditService.Severity.MEDIUM
            );
            
            return response;
            
        } catch (Exception e) {
            // 8. Registrar intento fallido
            authenticationAttemptService.recordRefreshAttempt(email, clientIp, false);
            securityAuditService.logSecurityEvent(
                SecurityAuditService.EventType.TOKEN_REFRESH_ERROR,
                userId, email, clientIp, "",
                "Error durante renovación: " + e.getMessage(),
                SecurityAuditService.Severity.HIGH
            );
            throw e;
        }
    }
}
