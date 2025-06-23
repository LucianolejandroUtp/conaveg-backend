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
            securityAuditService.logSecurityEvent("TOKEN_REFRESH_INVALID", 
                "Intento de renovación con token inválido", clientIp, null, false);
            throw new RuntimeException("Token inválido");
        }
        
        // 2. Verificar si el token puede ser renovado
        if (!jwtUtil.canRefreshToken(token)) {
            securityAuditService.logSecurityEvent("TOKEN_REFRESH_OUT_OF_WINDOW", 
                "Token fuera de ventana de renovación", clientIp, null, false);
            throw new IllegalArgumentException("Token no está en ventana de renovación");
        }
        
        // 3. Aplicar rate limiting por usuario
        String email = jwtUtil.getEmailFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        if (!authenticationAttemptService.canAttemptRefresh(email, clientIp)) {
            securityAuditService.logSecurityEvent("TOKEN_REFRESH_RATE_LIMITED", 
                "Rate limit excedido para renovación", clientIp, email, false);
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
            securityAuditService.logSecurityEvent("TOKEN_REFRESH_SUCCESS", 
                "Token renovado exitosamente", clientIp, email, true);
            
            return response;
            
        } catch (Exception e) {
            // 8. Registrar intento fallido
            authenticationAttemptService.recordRefreshAttempt(email, clientIp, false);
            securityAuditService.logSecurityEvent("TOKEN_REFRESH_ERROR", 
                "Error durante renovación: " + e.getMessage(), clientIp, email, false);
            throw e;
        }
    }
}
