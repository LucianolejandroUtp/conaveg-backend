package com.conaveg.cona.repository;

import com.conaveg.cona.model.PasswordResetToken;
import com.conaveg.cona.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para tokens de recuperación de contraseña
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token válido (no usado y no expirado) por su valor
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.token = :token AND t.used = false AND t.expiryDate > :now")
    Optional<PasswordResetToken> findValidToken(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Busca un token por su valor
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Busca todos los tokens válidos para un usuario
     */
    @Query("SELECT t FROM PasswordResetToken t WHERE t.user = :user AND t.used = false AND t.expiryDate > :now")
    List<PasswordResetToken> findValidTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Busca todos los tokens de un usuario
     */
    List<PasswordResetToken> findByUser(User user);

    /**
     * Marca todos los tokens de un usuario como usados
     */
    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user = :user AND t.used = false")
    int markAllUserTokensAsUsed(@Param("user") User user);

    /**
     * Elimina tokens expirados (para limpieza periódica)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :cutoff")
    int deleteExpiredTokens(@Param("cutoff") LocalDateTime cutoff);

    /**
     * Cuenta tokens válidos por usuario en las últimas horas
     */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.user = :user AND t.createdAt > :since")
    long countRecentTokensByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Cuenta tokens válidos por IP en las últimas horas (requiere agregar IP al modelo si es necesario)
     */
    @Query("SELECT COUNT(t) FROM PasswordResetToken t WHERE t.createdAt > :since")
    long countRecentTokens(@Param("since") LocalDateTime since);
}
