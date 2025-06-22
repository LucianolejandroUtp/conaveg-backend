package com.conaveg.cona.dto;

/**
 * DTO para devolver la respuesta del login
 */
public class LoginResponseDTO {
    
    private String token;
    private String type = "Bearer";
    private UserDTO user;
    private Long expiresIn; // Tiempo de expiración en milisegundos
    
    // Constructors
    public LoginResponseDTO() {}
    
    public LoginResponseDTO(String token, UserDTO user, Long expiresIn) {
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public UserDTO getUser() {
        return user;
    }
    
    public void setUser(UserDTO user) {
        this.user = user;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
