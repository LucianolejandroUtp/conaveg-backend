package com.conaveg.cona.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para operaciones de recuperación de contraseña
 */
@Schema(description = "Response para operaciones de recuperación de contraseña")
public class PasswordResetResponseDTO {
    
    @Schema(description = "Indica si la operación fue exitosa", example = "true")
    private boolean success;
    
    @Schema(description = "Mensaje informativo sobre el resultado de la operación", 
            example = "Si el email existe en nuestro sistema, recibirás instrucciones de recuperación")
    private String message;
    
    @Schema(description = "Timestamp de cuando se procesó la solicitud")
    private LocalDateTime timestamp;
    
    @Schema(description = "Información adicional (solo para validación de tokens)", 
            example = "Token válido")
    private String details;
    
    // Constructores
    public PasswordResetResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }
    
    public PasswordResetResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
    
    public PasswordResetResponseDTO(boolean success, String message, String details) {
        this.success = success;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    // Métodos estáticos para respuestas comunes
    public static PasswordResetResponseDTO success(String message) {
        return new PasswordResetResponseDTO(true, message);
    }
    
    public static PasswordResetResponseDTO success(String message, String details) {
        return new PasswordResetResponseDTO(true, message, details);
    }
    
    public static PasswordResetResponseDTO error(String message) {
        return new PasswordResetResponseDTO(false, message);
    }
    
    public static PasswordResetResponseDTO error(String message, String details) {
        return new PasswordResetResponseDTO(false, message, details);
    }
    
    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "PasswordResetResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", details='" + details + '\'' +
                '}';
    }
}
