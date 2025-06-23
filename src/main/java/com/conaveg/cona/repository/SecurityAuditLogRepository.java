package com.conaveg.cona.repository;

import com.conaveg.cona.model.SecurityAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para logs de auditoría de seguridad
 */
@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, Long> {

    /**
     * Busca logs por tipo de evento
     */
    List<SecurityAuditLog> findByEventType(String eventType);

    /**
     * Busca logs por usuario
     */
    List<SecurityAuditLog> findByUserId(Long userId);

    /**
     * Busca logs por email
     */
    List<SecurityAuditLog> findByEmail(String email);

    /**
     * Busca logs por IP
     */
    List<SecurityAuditLog> findByIpAddress(String ipAddress);

    /**
     * Busca logs por severidad
     */
    List<SecurityAuditLog> findBySeverity(String severity);

    /**
     * Busca logs en un rango de fechas
     */
    @Query("SELECT s FROM SecurityAuditLog s WHERE s.timestamp BETWEEN :startDate AND :endDate ORDER BY s.timestamp DESC")
    List<SecurityAuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Busca logs por tipo de evento y rango de fechas con paginación
     */
    @Query("SELECT s FROM SecurityAuditLog s WHERE s.eventType = :eventType AND s.timestamp BETWEEN :startDate AND :endDate ORDER BY s.timestamp DESC")
    Page<SecurityAuditLog> findByEventTypeAndDateRange(@Param("eventType") String eventType,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate,
                                                        Pageable pageable);

    /**
     * Busca logs críticos recientes
     */
    @Query("SELECT s FROM SecurityAuditLog s WHERE s.severity = 'CRITICAL' AND s.timestamp > :since ORDER BY s.timestamp DESC")
    List<SecurityAuditLog> findRecentCriticalLogs(@Param("since") LocalDateTime since);

    /**
     * Cuenta logs por severidad en un período
     */
    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.severity = :severity AND s.timestamp > :since")
    long countBySeveritySince(@Param("severity") String severity, @Param("since") LocalDateTime since);

    /**
     * Cuenta intentos fallidos por IP en las últimas horas
     */
    @Query("SELECT COUNT(s) FROM SecurityAuditLog s WHERE s.eventType = 'LOGIN_FAILED' AND s.ipAddress = :ipAddress AND s.timestamp > :since")
    long countFailedLoginsByIpSince(@Param("ipAddress") String ipAddress, @Param("since") LocalDateTime since);

    /**
     * Busca actividad sospechosa por IP (múltiples eventos críticos)
     */
    @Query("SELECT s FROM SecurityAuditLog s WHERE s.ipAddress = :ipAddress AND s.severity IN ('HIGH', 'CRITICAL') AND s.timestamp > :since ORDER BY s.timestamp DESC")
    List<SecurityAuditLog> findSuspiciousActivityByIp(@Param("ipAddress") String ipAddress, 
                                                       @Param("since") LocalDateTime since);

    /**
     * Elimina logs antiguos (para mantenimiento)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SecurityAuditLog s WHERE s.timestamp < :cutoff")
    int deleteOldLogs(@Param("cutoff") LocalDateTime cutoff);

    /**
     * Busca logs con filtros múltiples
     */
    @Query("SELECT s FROM SecurityAuditLog s WHERE " +
           "(:eventType IS NULL OR s.eventType = :eventType) AND " +
           "(:userId IS NULL OR s.userId = :userId) AND " +
           "(:email IS NULL OR s.email = :email) AND " +
           "(:severity IS NULL OR s.severity = :severity) AND " +
           "s.timestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY s.timestamp DESC")
    Page<SecurityAuditLog> findWithFilters(@Param("eventType") String eventType,
                                           @Param("userId") Long userId,
                                           @Param("email") String email,
                                           @Param("severity") String severity,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);
}
