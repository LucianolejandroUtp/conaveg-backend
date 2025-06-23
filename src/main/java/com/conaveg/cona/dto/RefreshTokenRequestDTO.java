package com.conaveg.cona.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para solicitudes de renovación de token JWT
 */
@Schema(description = "DTO para solicitud de renovación de token")
public class RefreshTokenRequestDTO {

    @NotBlank(message = "El token es requerido")
    @Schema(description = "Token JWT actual que se desea renovar", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", 
            required = true)
    private String token;

    @Schema(description = "IP del cliente (para auditoría y rate limiting)", 
            example = "192.168.1.100")
    private String clientIp;

    // Constructor por defecto
    public RefreshTokenRequestDTO() {}

    // Constructor con parámetros
    public RefreshTokenRequestDTO(String token) {
        this.token = token;
    }

    // Constructor completo
    public RefreshTokenRequestDTO(String token, String clientIp) {
        this.token = token;
        this.clientIp = clientIp;
    }

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        return "RefreshTokenRequestDTO{" +
                "token='[PROTECTED]'" +
                '}';
    }
}
