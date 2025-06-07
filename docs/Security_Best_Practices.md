# Mejores Prácticas de Seguridad - Sistema CONA

## Introducción

Esta guía establece las mejores prácticas de seguridad implementadas en el Sistema de Gestión CONA, con enfoque especial en el manejo seguro de contraseñas y autenticación.

## Política de Contraseñas

### Requisitos Obligatorios

Todas las contraseñas en el sistema CONA deben cumplir:

```
✅ Longitud: Entre 8 y 128 caracteres
✅ Complejidad: Al menos 1 minúscula, 1 mayúscula, 1 número, 1 carácter especial
✅ Caracteres permitidos: A-Z, a-z, 0-9, @$!%*?&
✅ Cifrado: BCrypt con costo 12
✅ Almacenamiento: Solo hash, nunca texto plano
```

### Ejemplos de Contraseñas

| Categoría | Ejemplo | Seguridad |
|-----------|---------|-----------|
| **Muy Segura** | `Tr@b4j0Segur0!2024` | ⭐⭐⭐⭐⭐ |
| **Segura** | `MiP@ssw0rd123` | ⭐⭐⭐⭐ |
| **Aceptable** | `Password123!` | ⭐⭐⭐ |
| **Débil** | `password123` | ❌ No cumple requisitos |

### Validación Automática

```java
// Implementado en UserCreateDTO
@Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial"
)
```

## Implementación de BCrypt

### Configuración Segura

```java
@Configuration
public class SecurityConfig {
    
    /**
     * Configuración de BCrypt con costo 12
     * - Costo 12: Balance óptimo seguridad/rendimiento
     * - Tiempo esperado: ~250-500ms por operación
     * - Resistente a ataques de fuerza bruta hasta 2030+
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

### Justificación del Costo

| Costo | Tiempo (aprox) | Seguridad | Uso Recomendado |
|-------|---------------|-----------|-----------------|
| 4 | ~3ms | Baja | Solo tests |
| 10 | ~65ms | Media | Aplicaciones legacy |
| **12** | **~250ms** | **Alta** | **Producción CONA** |
| 14 | ~1000ms | Muy Alta | Datos críticos |

## Manejo Seguro de Contraseñas

### ✅ Prácticas Correctas

```java
// 1. Cifrado automático en creación
@Service
public class UserService {
    
    public UserDTO saveUser(UserCreateDTO userCreateDTO) {
        // ✅ Cifrar antes de guardar
        String encryptedPassword = passwordEncoder.encode(userCreateDTO.getPassword());
        user.setPassword(encryptedPassword);
        
        // ✅ Limpiar contraseña del DTO después del uso
        userCreateDTO.setPassword(null);
        
        return toDTO(userRepository.save(user));
    }
}

// 2. Validación para autenticación
public boolean authenticateUser(String username, String rawPassword) {
    User user = userRepository.findByUserName(username);
    if (user != null) {
        // ✅ Usar validatePassword del servicio
        return userService.validatePassword(rawPassword, user.getPassword());
    }
    return false;
}
```

### ❌ Prácticas a Evitar

```java
// ❌ NUNCA: Almacenar contraseñas en texto plano
user.setPassword(userCreateDTO.getPassword()); // MAL

// ❌ NUNCA: Usar MD5 o SHA1
String weakHash = DigestUtils.md5Hex(password); // MAL

// ❌ NUNCA: Cifrar manualmente sin usar el servicio
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode(password); // MAL - usar UserService

// ❌ NUNCA: Loggear contraseñas
logger.info("Password: " + password); // MAL

// ❌ NUNCA: Enviar contraseñas en respuestas
return new UserDTO(user.getPassword()); // MAL
```

## Validación de Entrada

### DTOs con Validaciones

```java
public class UserCreateDTO {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Solo se permiten letras, números, puntos, guiones y guiones bajos")
    private String userName;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(/* patrón de complejidad */)
    private String password;
}
```

### Sanitización de Datos

```java
@Service
public class UserService {
    
    public UserDTO saveUser(UserCreateDTO userCreateDTO) {
        // ✅ Sanitizar entrada
        String sanitizedUserName = sanitizeInput(userCreateDTO.getUserName());
        String sanitizedEmail = sanitizeEmail(userCreateDTO.getEmail());
        
        // ✅ Validar duplicados
        if (userRepository.existsByUserName(sanitizedUserName)) {
            throw new DuplicateUserException("El nombre de usuario ya existe");
        }
        
        if (userRepository.existsByEmail(sanitizedEmail)) {
            throw new DuplicateEmailException("El email ya está registrado");
        }
        
        // ✅ Proceder con creación segura
        User user = new User();
        user.setUserName(sanitizedUserName);
        user.setEmail(sanitizedEmail);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        
        return toDTO(userRepository.save(user));
    }
    
    private String sanitizeInput(String input) {
        return input.trim().toLowerCase();
    }
    
    private String sanitizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
```

## Prevención de Ataques

### 1. Ataques de Fuerza Bruta

```java
// Implementar rate limiting
@Component
public class AuthenticationAttemptService {
    
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blockTimeCache = new ConcurrentHashMap<>();
    
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_TIME_MINUTES = 15;
    
    public boolean isBlocked(String username) {
        LocalDateTime blockTime = blockTimeCache.get(username);
        if (blockTime != null && LocalDateTime.now().isBefore(blockTime)) {
            return true;
        }
        
        // Limpiar bloqueo expirado
        if (blockTime != null) {
            blockTimeCache.remove(username);
            attemptsCache.remove(username);
        }
        
        return false;
    }
    
    public void registerFailedAttempt(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0) + 1;
        attemptsCache.put(username, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            blockTimeCache.put(username, LocalDateTime.now().plusMinutes(BLOCK_TIME_MINUTES));
        }
    }
    
    public void registerSuccessfulAttempt(String username) {
        attemptsCache.remove(username);
        blockTimeCache.remove(username);
    }
}
```

### 2. Inyección SQL

```java
// ✅ Usar JPA/Hibernate con parámetros
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // ✅ Query method seguro
    Optional<User> findByUserName(String userName);
    
    // ✅ Query nativo seguro con parámetros
    @Query(value = "SELECT * FROM users WHERE email = :email", nativeQuery = true)
    Optional<User> findByEmailSecure(@Param("email") String email);
}

// ❌ NUNCA concatenar strings en queries
// String query = "SELECT * FROM users WHERE name = '" + userName + "'"; // MAL
```

### 3. Cross-Site Scripting (XSS)

```java
// ✅ Sanitización de salida en DTOs
public class UserDTO {
    
    public String getUserName() {
        // Escapar caracteres HTML si se muestra en web
        return HtmlUtils.htmlEscape(this.userName);
    }
    
    public String getEmail() {
        return HtmlUtils.htmlEscape(this.email);
    }
    
    // ✅ NUNCA exponer contraseña
    // No hay getter para password
}
```

## Auditoría y Logging

### Eventos de Seguridad a Loggear

```java
@Service
public class SecurityAuditService {
    
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY");
    
    // ✅ Loggear eventos importantes
    public void logUserCreation(String userName, String adminUser) {
        securityLogger.info("Usuario creado - Usuario: {}, Admin: {}, IP: {}", 
            userName, adminUser, getCurrentIP());
    }
    
    public void logFailedLogin(String userName, String ip) {
        securityLogger.warn("Intento de login fallido - Usuario: {}, IP: {}", 
            userName, ip);
    }
    
    public void logSuccessfulLogin(String userName, String ip) {
        securityLogger.info("Login exitoso - Usuario: {}, IP: {}", 
            userName, ip);
    }
    
    public void logPasswordChange(String userName, String adminUser) {
        securityLogger.info("Contraseña cambiada - Usuario: {}, Admin: {}", 
            userName, adminUser);
    }
    
    // ❌ NUNCA loggear contraseñas
    // securityLogger.info("Password: {}", password); // MAL
}
```

### Configuración de Logs

```xml
<!-- logback-spring.xml -->
<configuration>
    <!-- Log específico para seguridad -->
    <appender name="SECURITY_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/security.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/security.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="SECURITY" level="INFO" additivity="false">
        <appender-ref ref="SECURITY_LOG"/>
    </logger>
</configuration>
```

## Configuración de Producción

### Variables de Entorno

```properties
# application-prod.properties

# Base de datos segura
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Cifrado de conexión
spring.datasource.hikari.connection-test-query=SELECT 1
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# Configuración JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Seguridad adicional
server.error.include-stacktrace=never
server.error.include-message=never

# Logging seguro
logging.level.org.springframework.security=WARN
logging.level.org.hibernate.SQL=WARN
```

### Headers de Seguridad

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
        return http.build();
    }
}
```

## Checklist de Seguridad

### Desarrollo ✅

- [ ] Contraseñas cifradas con BCrypt costo 12
- [ ] Validaciones de entrada implementadas
- [ ] DTOs utilizados en lugar de entidades
- [ ] Sanitización de datos de entrada
- [ ] Logs de seguridad configurados
- [ ] Tests de seguridad ejecutándose

### Testing ✅

- [ ] Tests unitarios para cifrado/validación
- [ ] Tests de integración para endpoints
- [ ] Tests de carga para rendimiento
- [ ] Tests de penetración básicos
- [ ] Validación de configuraciones

### Producción 📋

- [ ] Variables de entorno configuradas
- [ ] Base de datos con conexión cifrada
- [ ] Logs de seguridad monitoreados
- [ ] Rate limiting implementado
- [ ] Headers de seguridad configurados
- [ ] Backup y recuperación probados
- [ ] Monitoreo de intentos de acceso
- [ ] Plan de respuesta a incidentes

## Recursos y Referencias

### Documentación Oficial
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security](https://spring.io/projects/spring-security)
- [BCrypt Specification](https://en.wikipedia.org/wiki/Bcrypt)

### Herramientas de Seguridad
- **SAST**: SonarQube, Checkmarx
- **DAST**: OWASP ZAP, Burp Suite
- **Dependency Check**: OWASP Dependency Check
- **Secrets**: GitLeaks, TruffleHog

### Contactos de Seguridad
```
Equipo de Seguridad: security@conaveg.com
Reporte de Vulnerabilidades: security-reports@conaveg.com
Documentación: https://docs.conaveg.com/security
```

---
**Documento actualizado**: Junio 2025  
**Versión**: 1.0  
**Sistema**: CONA - Sistema de Gestión  
**Clasificación**: Interno - Sensible
