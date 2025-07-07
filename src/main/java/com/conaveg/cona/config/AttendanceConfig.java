package com.conaveg.cona.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.time.ZoneId;

/**
 * Configuración para el sistema de asistencias
 */
@Configuration
@ConfigurationProperties(prefix = "app.attendance")
public class AttendanceConfig {
    
    /**
     * Hora de inicio de la jornada laboral (formato HH:mm)
     * Por defecto: 08:00
     */
    private String startTime = "08:00";
    
    /**
     * Minutos de tolerancia antes de marcar tardanza
     * Por defecto: 0 (sin tolerancia)
     */
    private int lateThresholdMinutes = 0;
    
    /**
     * Zona horaria para cálculos de tiempo
     * Por defecto: America/Lima
     */
    private String timezone = "America/Lima";

    /**
     * Proporciona el ZoneId configurado como Bean
     */
    @Bean("applicationZoneId")
    public ZoneId getZoneId() {
        return ZoneId.of(timezone);
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getLateThresholdMinutes() {
        return lateThresholdMinutes;
    }

    public void setLateThresholdMinutes(int lateThresholdMinutes) {
        this.lateThresholdMinutes = lateThresholdMinutes;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}
