package com.conaveg.cona.dto;

import java.time.Instant;

public class UserDTO {
    private Long id;
    private String userName;
    private String email;
    private Instant emailVerifiedAt;
    private String estado;
    private String uniqueId;
    private Instant createdAt;
    private Instant updatedAt;
    private RolDTO role;

    public UserDTO() {}

    public UserDTO(Long id, String userName, String email, Instant emailVerifiedAt, String estado, String uniqueId, Instant createdAt, Instant updatedAt, RolDTO role) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.emailVerifiedAt = emailVerifiedAt;
        this.estado = estado;
        this.uniqueId = uniqueId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getEmailVerifiedAt() { return emailVerifiedAt; }
    public void setEmailVerifiedAt(Instant emailVerifiedAt) { this.emailVerifiedAt = emailVerifiedAt; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public RolDTO getRole() { return role; }
    public void setRole(RolDTO role) { this.role = role; }
}
