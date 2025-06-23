package com.conaveg.cona.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para solicitar recuperación de contraseña
 */
@Schema(description = "Request para solicitar recuperación de contraseña")
public class ForgotPasswordRequestDTO {
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    @Schema(description = "Email del usuario que solicita recuperar su contraseña", 
            example = "usuario@ejemplo.com")
    private String email;
    
    // Constructores
    public ForgotPasswordRequestDTO() {}
    
    public ForgotPasswordRequestDTO(String email) {
        this.email = email;
    }
    
    // Getters y Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "ForgotPasswordRequestDTO{" +
                "email='" + email + '\'' +
                '}';
    }
}
