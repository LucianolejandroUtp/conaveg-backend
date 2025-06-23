package com.conaveg.cona.repository;

import com.conaveg.cona.model.AuthenticationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Repository para intentos de autenticación
 */
@Repository
public interface AuthenticationAttemptRepository extends JpaRepository<AuthenticationAttempt, Long> {

    /**
     * Cuenta intentos fallidos por email en un período específico
     */
    @Query("SELECT COUNT(a) FROM AuthenticationAttempt a WHERE a.email = :email AND a.successful = false AND a.attemptTime > :since")
    long countFailedAttemptsByEmail(@Param("email") String email, @Param("since") LocalDateTime since);

    /**
     * Cuenta intentos fallidos por IP en un período específico
     */
    @Query("SELECT COUNT(a) FROM AuthenticationAttempt a WHERE a.ipAddress = :ipAddress AND a.successful = false AND a.attemptTime > :since")
    long countFailedAttemptsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Cuenta todos los intentos por email en un período específico
     */
    @Query("SELECT COUNT(a) FROM AuthenticationAttempt a WHERE a.email = :email AND a.attemptTime > :since")
    long countAttemptsByEmail(@Param("email") String email, @Param("since") LocalDateTime since);

    /**
     * Cuenta todos los intentos por IP en un período específico
     */
    @Query("SELECT COUNT(a) FROM AuthenticationAttempt a WHERE a.ipAddress = :ipAddress AND a.attemptTime > :since")
    long countAttemptsByIp(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Encuentra el último intento exitoso por email
     */
    @Query("SELECT a FROM AuthenticationAttempt a WHERE a.email = :email AND a.successful = true ORDER BY a.attemptTime DESC")
    AuthenticationAttempt findLastSuccessfulAttemptByEmail(@Param("email") String email);

    /**
     * Encuentra el último intento fallido por email
     */
    @Query("SELECT a FROM AuthenticationAttempt a WHERE a.email = :email AND a.successful = false ORDER BY a.attemptTime DESC")
    AuthenticationAttempt findLastFailedAttemptByEmail(@Param("email") String email);

    /**
     * Elimina intentos antiguos (para tarea programada de limpieza)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM AuthenticationAttempt a WHERE a.attemptTime < :cutoff")
    int deleteOldAttempts(@Param("cutoff") LocalDateTime cutoff);

    /**
     * Verifica si existe al menos un intento exitoso para un email
     */
    @Query("SELECT COUNT(a) > 0 FROM AuthenticationAttempt a WHERE a.email = :email AND a.successful = true")
    boolean hasSuccessfulAttempts(@Param("email") String email);
}
