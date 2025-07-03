package com.conaveg.cona.config;

import com.conaveg.cona.security.JwtAuthenticationEntryPoint;
import com.conaveg.cona.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Configuración de seguridad para el proyecto CONAVEG
 * Incluye configuración de filtros JWT y autorización por roles
 * 
 * Esta configuración se desactiva automáticamente cuando el perfil 'dev' está activo
 * y app.dev.skip-authentication=true
 * 
 * Nota: El bean passwordEncoder se encuentra en BaseConfig para estar siempre disponible
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilitar anotaciones @PreAuthorize
@ConditionalOnProperty(name = "app.dev.skip-authentication", havingValue = "false", matchIfMissing = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Configuración de filtros de seguridad con middleware JWT
     * Implementa autenticación automática y protección de endpoints
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {        
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sin estado (stateless)
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - NO requieren autenticación
                .requestMatchers("/api/auth/**", "/conaveg/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health", "/conaveg/actuator/health").permitAll()
                .requestMatchers("/error", "/conaveg/error").permitAll()
                // Permitir preflight requests (OPTIONS) para CORS
                .requestMatchers("OPTIONS", "/**").permitAll()
                // Permitir endpoints de usuarios temporalmente para testing
                .requestMatchers("/api/users/**", "/conaveg/api/users/**").permitAll()
                // Todos los demás endpoints REQUIEREN autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Manejo de errores 401
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Agregar filtro JWT
            .build();
    }

    /**
     * Configuración de CORS para permitir conexiones desde el frontend
     * Permite peticiones desde http://localhost:3000 (frontend)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Orígenes permitidos (frontend)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Tiempo de cache para preflight requests
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
