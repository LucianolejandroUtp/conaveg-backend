package com.conaveg.cona.controller;

import com.conaveg.cona.dto.LoginRequestDTO;
import com.conaveg.cona.dto.LoginResponseDTO;
import com.conaveg.cona.dto.RefreshTokenRequestDTO;
import com.conaveg.cona.dto.RefreshTokenResponseDTO;
import com.conaveg.cona.dto.UserDTO;
// Agregando imports para password recovery
import com.conaveg.cona.dto.ForgotPasswordRequestDTO;
import com.conaveg.cona.dto.ResetPasswordRequestDTO;
import com.conaveg.cona.dto.PasswordResetResponseDTO;
import com.conaveg.cona.service.AuthenticationService;
import com.conaveg.cona.service.PasswordRecoveryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// OpenAPI/Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador de autenticación
 * Maneja login, logout y operaciones relacionadas con tokens JWT
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios, login, logout y gestión de tokens JWT.")
public class AuthController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    @Autowired
    private PasswordRecoveryService passwordRecoveryService;
    
    /**
     * Endpoint de login
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario con email y contraseña, retorna un token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, token generado"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            LoginResponseDTO response = authenticationService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autenticación: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para obtener información del usuario autenticado
     */
    @Operation(summary = "Obtener usuario actual", description = "Obtiene la información del usuario autenticado mediante el token JWT.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Información del usuario obtenida"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            @Parameter(description = "Token JWT en el header Authorization", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer token del header "Bearer <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autorización requerido");
            }
            
            String token = authHeader.substring(7); // Remover "Bearer "
            UserDTO user = authenticationService.getUserFromToken(token);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autorización: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint de logout (básico - en futuras versiones se implementará blacklist de tokens)
     */
    @Operation(summary = "Cerrar sesión", description = "Cierra la sesión del usuario. Nota: En esta versión básica, el cliente debe descartar el token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout exitoso"),
        @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Parameter(description = "Token JWT en el header Authorization")
            @RequestHeader("Authorization") String authHeader) {
        try {
            // Validar que el token sea válido antes de hacer logout
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autorización requerido");
            }
            
            String token = authHeader.substring(7);
            if (!authenticationService.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido");
            }
            
            // En esta versión básica, simplemente confirmamos el logout
            // En futuras versiones se implementará blacklist de tokens
            return ResponseEntity.ok("Logout exitoso. Por favor, descarte el token del lado del cliente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error durante el logout: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para validar un token
     */
    @Operation(summary = "Validar token", description = "Valida si un token JWT es válido y no ha expirado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado")
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(
            @Parameter(description = "Token JWT en el header Authorization")
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autorización requerido");
            }
            
            String token = authHeader.substring(7);
            boolean isValid = authenticationService.validateToken(token);
            
            if (isValid) {
                return ResponseEntity.ok("Token válido");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido o expirado");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de validación: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para renovar token JWT
     */
    @Operation(summary = "Renovar token JWT", 
               description = "Renueva un token JWT válido que esté en ventana de renovación (últimos 15 minutos). " +
                           "Incluye rate limiting y auditoría de seguridad.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Token no válido para renovación"),
        @ApiResponse(responseCode = "401", description = "Token inválido o expirado"),
        @ApiResponse(responseCode = "429", description = "Demasiados intentos de renovación")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshRequest,
                                         HttpServletRequest request) {
        try {
            // Capturar IP del cliente si no se proporcionó
            if (refreshRequest.getClientIp() == null || refreshRequest.getClientIp().isEmpty()) {
                String clientIp = getClientIpAddress(request);
                refreshRequest.setClientIp(clientIp);
            }
            
            RefreshTokenResponseDTO response = authenticationService.refreshToken(refreshRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de renovación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autorización: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para solicitar recuperación de contraseña
     */
    @Operation(summary = "Solicitar recuperación de contraseña", 
               description = "Envía un token de recuperación al email del usuario si existe en el sistema. " +
                           "Por razones de seguridad, siempre devuelve una respuesta exitosa.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Solicitud procesada (se envía email si el usuario existe)"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "429", description = "Demasiadas solicitudes de recuperación")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request,
                                          HttpServletRequest httpRequest) {
        try {
            String clientIp = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            // Procesar solicitud de recuperación
            passwordRecoveryService.initiatePasswordReset(
                request.getEmail(), clientIp, userAgent
            );
            
            // Por seguridad, siempre devolvemos el mismo mensaje
            PasswordResetResponseDTO response = PasswordResetResponseDTO.success(
                "Si el email existe en nuestro sistema, recibirás instrucciones de recuperación en breve."
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Demasiadas solicitudes")) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body(PasswordResetResponseDTO.error(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PasswordResetResponseDTO.error("Error al procesar la solicitud: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para resetear contraseña con token
     */
    @Operation(summary = "Resetear contraseña", 
               description = "Cambia la contraseña del usuario usando un token de recuperación válido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Token inválido, expirado o datos inválidos"),
        @ApiResponse(responseCode = "422", description = "Nueva contraseña no válida o no coincide")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request,
                                         HttpServletRequest httpRequest) {
        try {
            // Validar que las contraseñas coincidan
            if (!request.passwordsMatch()) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(PasswordResetResponseDTO.error("Las contraseñas no coinciden"));
            }
            
            String clientIp = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            // Procesar reset de contraseña
            boolean success = passwordRecoveryService.resetPassword(
                request.getToken(), 
                request.getNewPassword(), 
                clientIp, 
                userAgent
            );
            
            if (success) {
                PasswordResetResponseDTO response = PasswordResetResponseDTO.success(
                    "Contraseña cambiada exitosamente. Ya puedes iniciar sesión con tu nueva contraseña."
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(PasswordResetResponseDTO.error("Token inválido o expirado"));
            }
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PasswordResetResponseDTO.error("Error al cambiar contraseña: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para validar un token de recuperación
     */
    @Operation(summary = "Validar token de recuperación", 
               description = "Verifica si un token de recuperación de contraseña es válido y no ha expirado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token válido"),
        @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@Parameter(description = "Token de recuperación", required = true)
                                              @RequestParam String token) {
        try {
            boolean isValid = passwordRecoveryService.validateResetToken(token);
            
            if (isValid) {
                PasswordResetResponseDTO response = PasswordResetResponseDTO.success(
                    "Token válido", "Puedes proceder a cambiar tu contraseña"
                );
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(PasswordResetResponseDTO.error("Token inválido o expirado"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PasswordResetResponseDTO.error("Error al validar token: " + e.getMessage()));
        }
    }
    
    /**
     * Obtiene la IP real del cliente considerando proxies
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
}
