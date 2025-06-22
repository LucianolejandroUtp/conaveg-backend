package com.conaveg.cona.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el proyecto CONAVEG
 * Incluye configuración de cifrado de contraseñas con BCrypt
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean para cifrado de contraseñas usando BCrypt
     * BCrypt incluye salt automático y es resistente a ataques de fuerza bruta
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }    /**
     * Configuración de filtros de seguridad
     * Permite acceso libre a endpoints de autenticación, otros endpoints requieren revisión futura
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Permitir acceso a endpoints de autenticación
                .anyRequest().permitAll() // Por ahora permitir acceso a todos los endpoints
            )
            .build();
    }
}
