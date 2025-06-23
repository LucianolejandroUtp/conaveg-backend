package com.conaveg.cona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para resetear contraseña usando token de recuperación
 */
@Schema(description = "Request para resetear contraseña con token de recuperación")
public class ResetPasswordRequestDTO {
    
    @NotBlank(message = "El token es requerido")
    @Schema(description = "Token de recuperación de contraseña recibido por email", 
            example = "abc123def456ghi789")
    private String token;
    
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial")
    @Schema(description = "Nueva contraseña que cumple con los requisitos de seguridad", 
            example = "MiNuevaContraseña123!")
    private String newPassword;
    
    @NotBlank(message = "La confirmación de contraseña es requerida")
    @Schema(description = "Confirmación de la nueva contraseña (debe coincidir)", 
            example = "MiNuevaContraseña123!")
    private String confirmPassword;
    
    // Constructores
    public ResetPasswordRequestDTO() {}
    
    public ResetPasswordRequestDTO(String token, String newPassword, String confirmPassword) {
        this.token = token;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getConfirmPassword() {
        return confirmPassword;
    }
    
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    /**
     * Valida que las contraseñas coincidan
     */
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
    
    @Override
    public String toString() {
        return "ResetPasswordRequestDTO{" +
                "token='" + token + '\'' +
                ", passwordsMatch=" + passwordsMatch() +
                '}';
    }
}
