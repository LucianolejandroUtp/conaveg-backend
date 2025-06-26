package com.conaveg.cona.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración base que siempre está disponible
 * Contiene beans necesarios independientemente del modo de desarrollo
 */
@Configuration
public class BaseConfig {

    /**
     * Bean para cifrado de contraseñas usando BCrypt
     * BCrypt incluye salt automático y es resistente a ataques de fuerza bruta
     * Este bean debe estar siempre disponible, incluso en modo desarrollo
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
