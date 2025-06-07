# Guía de Uso de BCrypt - Sistema CONA

## Introducción

Esta guía proporciona información completa sobre el uso del sistema de cifrado de contraseñas BCrypt implementado en el Sistema de Gestión CONA.

## Arquitectura de Seguridad

### Configuración BCrypt

El sistema utiliza BCrypt con las siguientes configuraciones:

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Costo 12 para balance seguridad/rendimiento
    }
}
```

### Ubicación de Componentes

```
src/main/java/com/conaveg/cona/
├── config/SecurityConfig.java          # Configuración de BCrypt
├── service/UserService.java            # Servicios de cifrado
├── controller/UserController.java      # Endpoints seguros
└── dto/UserCreateDTO.java              # Validaciones de entrada
```

## Uso del UserService

### 1. Cifrado de Contraseñas

```java
@Autowired
private UserService userService;

// Cifrar una nueva contraseña
String rawPassword = "MiContraseña123!";
String hashedPassword = userService.hashPassword(rawPassword);
```

### 2. Validación de Contraseñas

```java
// Validar contraseña durante login
String rawPassword = "MiContraseña123!";
String storedHash = "$2a$12$..."; // Hash almacenado en BD

boolean isValid = userService.validatePassword(rawPassword, storedHash);
if (isValid) {
    // Contraseña correcta - proceder con autenticación
} else {
    // Contraseña incorrecta - denegar acceso
}
```

### 3. Creación de Usuarios

```java
// Crear usuario con contraseña cifrada automáticamente
UserCreateDTO userDto = new UserCreateDTO();
userDto.setUserName("nuevo_usuario");
userDto.setEmail("usuario@ejemplo.com");
userDto.setPassword("ContraseñaSegura123!");
userDto.setRoleId(1L);

UserDTO createdUser = userService.saveUser(userDto);
// La contraseña se cifra automáticamente antes de guardar
```

## Validaciones de Contraseña

### Reglas Implementadas

El sistema valida automáticamente que las contraseñas cumplan:

```java
public class UserCreateDTO {
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "La contraseña debe contener al menos: 1 minúscula, 1 mayúscula, 1 número y 1 carácter especial"
    )
    private String password;
}
```

### Ejemplos de Contraseñas

| ✅ Válidas | ❌ Inválidas |
|------------|--------------|
| `MiPass123!` | `password` (sin mayúsculas, números, especiales) |
| `Segura@2024` | `PASSWORD123` (sin minúsculas, especiales) |
| `Test$456` | `Pass123` (sin caracteres especiales) |
| `Complex!Pass9` | `123456789` (sin letras) |

## Endpoints de Usuario

### Crear Usuario

```http
POST /api/users
Content-Type: application/json

{
    "userName": "nuevo_usuario",
    "email": "usuario@ejemplo.com",
    "password": "ContraseñaSegura123!",
    "roleId": 1
}
```

### Actualizar Usuario

```http
PUT /api/users/{id}
Content-Type: application/json

{
    "userName": "usuario_actualizado",
    "email": "nuevo_email@ejemplo.com",
    "password": "NuevaContraseña456@", // Opcional
    "roleId": 2
}
```

## Mejores Prácticas para Desarrolladores

### 1. Manejo de Contraseñas

```java
// ✅ CORRECTO: Usar servicios del sistema
String hashedPassword = userService.hashPassword(rawPassword);

// ❌ INCORRECTO: Cifrar manualmente
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String wrongHash = encoder.encode(rawPassword);
```

### 2. Validación de Entrada

```java
// ✅ CORRECTO: Usar DTOs con validaciones
@PostMapping
public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
    // Las validaciones se ejecutan automáticamente
}

// ❌ INCORRECTO: Recibir Entity directamente
@PostMapping
public ResponseEntity<User> createUser(@RequestBody User user) {
    // Sin validaciones automáticas
}
```

### 3. Manejo de Errores

```java
@PostMapping
public ResponseEntity<?> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
    try {
        UserDTO user = userService.saveUser(userCreateDTO);
        return ResponseEntity.ok(user);
    } catch (DataIntegrityViolationException e) {
        return ResponseEntity.badRequest()
            .body("Error: Usuario o email ya existe");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error interno del servidor");
    }
}
```

## Configuración de Desarrollo

### application.properties

```properties
# Configuración para desarrollo
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# Logging de seguridad
logging.level.com.conaveg.cona.service.UserService=DEBUG
logging.level.org.springframework.security=DEBUG
```

### application-test.properties

```properties
# Base de datos en memoria para tests
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop

# Desactivar logging en tests
logging.level.com.conaveg.cona=WARN
```

## Debugging y Troubleshooting

### Problemas Comunes

#### 1. Contraseña no se cifra
```java
// Verificar que se use el servicio correcto
@Autowired
private UserService userService; // ✅ Correcto

// No usar BCryptPasswordEncoder directamente
@Autowired 
private BCryptPasswordEncoder encoder; // ❌ Evitar
```

#### 2. Validación falla silenciosamente
```java
// Asegurar que @Valid esté presente
@PostMapping
public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
    // @Valid es obligatorio para activar validaciones
}
```

#### 3. Tests de BCrypt lentos
```java
// Para tests, usar configuración de menor costo
@TestConfiguration
public class TestSecurityConfig {
    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Menor costo para tests
    }
}
```

### Logs Útiles

```java
// Agregar logging para debugging
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public UserDTO saveUser(UserCreateDTO userCreateDTO) {
        logger.debug("Iniciando creación de usuario: {}", userCreateDTO.getUserName());
        
        // Cifrar contraseña
        String encryptedPassword = passwordEncoder.encode(userCreateDTO.getPassword());
        logger.debug("Contraseña cifrada exitosamente");
        
        // ... resto del código
    }
}
```

## Migración y Actualizaciones

### Cambiar Costo de BCrypt

```java
// 1. Actualizar configuración
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(14); // Aumentar costo
}

// 2. Migrar contraseñas existentes (si es necesario)
public void migratePolicyPasswords() {
    // Solo re-cifrar en próximo login del usuario
    // BCrypt maneja automáticamente diferentes costos
}
```

### Compatibilidad con Versiones Anteriores

BCrypt mantiene automáticamente la compatibilidad:
- Hashes con costo 10 siguen funcionando
- Hashes con costo 12 (actual) funcionan
- Mezcla de costos es soportada

## Recursos Adicionales

- [Spring Security BCrypt Documentation](https://docs.spring.io/spring-security/reference/)
- [BCrypt Specification](https://en.wikipedia.org/wiki/Bcrypt)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

---
**Documento actualizado**: Junio 2025  
**Versión**: 1.0  
**Sistema**: CONA - Sistema de Gestión
