package com.conaveg.cona.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el entorno de desarrollo
 * PERMITE ACCESO SIN AUTENTICACIÓN PARA FACILITAR EL DESARROLLO
 * 
 * ADVERTENCIA: NUNCA ACTIVAR EN PRODUCCIÓN
 * Esta configuración solo se activa cuando:
 * 1. El perfil 'dev' está activo
 * 2. La propiedad app.dev.skip-authentication=true
 */
@Configuration
@EnableWebSecurity
@Profile("dev") // Solo se activa con el perfil de desarrollo
@ConditionalOnProperty(name = "app.dev.skip-authentication", havingValue = "true")
@Order(1) // Mayor prioridad que SecurityConfig
public class DevSecurityConfig {

    /**
     * Configuración de seguridad para desarrollo que permite acceso sin autenticación
     * Reemplaza completamente la configuración de SecurityConfig cuando está activa
     */
    @Bean
    @Primary
    public SecurityFilterChain devFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // PERMITIR TODO SIN AUTENTICACIÓN
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Permitir frames para H2 console si es necesario
            )
            .build();
    }
}
