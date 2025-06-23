package com.conaveg.cona.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para renovación exitosa de token JWT
 */
@Schema(description = "DTO de respuesta para renovación exitosa de token")
public class RefreshTokenResponseDTO {

    @Schema(description = "Nuevo token JWT renovado", 
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Tiempo en segundos hasta la expiración", example = "86400")
    private long expiresIn;

    @Schema(description = "Fecha y hora de emisión del nuevo token")
    private LocalDateTime issuedAt;

    @Schema(description = "Fecha y hora de expiración del nuevo token")
    private LocalDateTime expiresAt;

    @Schema(description = "Hash del token anterior (para auditoría)", example = "abc123...")
    private String refreshedFrom;

    @Schema(description = "Tiempo restante del token anterior en minutos", example = "45")
    private long previousTokenMinutesLeft;

    // Constructor por defecto
    public RefreshTokenResponseDTO() {}

    // Constructor completo
    public RefreshTokenResponseDTO(String token, long expiresIn, LocalDateTime issuedAt, 
                                  LocalDateTime expiresAt, String refreshedFrom, 
                                  long previousTokenMinutesLeft) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.refreshedFrom = refreshedFrom;
        this.previousTokenMinutesLeft = previousTokenMinutesLeft;
    }

    // Constructor básico
    public RefreshTokenResponseDTO(String token, long expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusSeconds(expiresIn);
    }

    // Getters y setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRefreshedFrom() {
        return refreshedFrom;
    }

    public void setRefreshedFrom(String refreshedFrom) {
        this.refreshedFrom = refreshedFrom;
    }

    public long getPreviousTokenMinutesLeft() {
        return previousTokenMinutesLeft;
    }

    public void setPreviousTokenMinutesLeft(long previousTokenMinutesLeft) {
        this.previousTokenMinutesLeft = previousTokenMinutesLeft;
    }

    @Override
    public String toString() {
        return "RefreshTokenResponseDTO{" +
                "tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", refreshedFrom='" + (refreshedFrom != null ? "[HASH]" : null) + '\'' +
                ", previousTokenMinutesLeft=" + previousTokenMinutesLeft +
                '}';
    }
}
