package com.conaveg.cona.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad para tracking de intentos de autenticación
 */
@Entity
@Table(name = "authentication_attempts", indexes = {
    @Index(name = "idx_email_time", columnList = "email, attempt_time"),
    @Index(name = "idx_ip_time", columnList = "ip_address, attempt_time"),
    @Index(name = "idx_attempt_time", columnList = "attempt_time")
})
public class AuthenticationAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "email", nullable = false)
    private String email;

    @Size(max = 45)
    @NotNull
    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @NotNull
    @CreationTimestamp
    @Column(name = "attempt_time", nullable = false, updatable = false)
    private LocalDateTime attemptTime;

    @NotNull
    @Column(name = "successful", nullable = false)
    private Boolean successful = false;

    @Lob
    @Column(name = "user_agent")
    private String userAgent;

    @Size(max = 500)
    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Size(max = 50)
    @Column(name = "attempt_type", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'LOGIN'")
    private String attemptType = "LOGIN";

    // Constructores
    public AuthenticationAttempt() {}

    public AuthenticationAttempt(String email, String ipAddress, Boolean successful, 
                               String userAgent, String failureReason) {
        this.email = email;
        this.ipAddress = ipAddress;
        this.successful = successful;
        this.userAgent = userAgent;
        this.failureReason = failureReason;
        this.attemptType = "LOGIN";
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(LocalDateTime attemptTime) {
        this.attemptTime = attemptTime;
    }

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(String attemptType) {
        this.attemptType = attemptType;
    }

}