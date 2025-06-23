package com.conaveg.cona.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para la documentación automática de la API
 * Incluye configuración de autenticación JWT Bearer Token
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "CONA - Sistema de Gestión Empresarial",
        version = "1.0.0",
        description = """
            API REST para el Sistema de Gestión Empresarial CONA.
            
            **Funcionalidades principales:**
            - Gestión de usuarios y autenticación JWT
            - Administración de empleados y roles
            - Control de proyectos y asignaciones
            - Gestión de inventario y productos
            - Sistema de autorización granular por roles
            
            **Roles del sistema:**
            - **ADMIN**: Acceso completo a todas las funcionalidades
            - **GERENTE**: Gestión operativa de proyectos e inventario
            - **EMPLEADO**: Acceso a información de empleados y consulta de proyectos/inventario
            - **USER**: Solo lectura en proyectos e inventario, acceso a perfil propio
            
            **Para usar esta API:**
            1. Obtén un token JWT usando el endpoint `/api/auth/login`
            2. Haz clic en el botón "Authorize" arriba a la derecha
            3. Ingresa el token en el formato: `Bearer tu_token_jwt_aqui`
            4. Ya puedes probar todos los endpoints autorizados según tu rol
            """,
        contact = @Contact(
            name = "Equipo de Desarrollo CONA",
            email = "dev@conaveg.com"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080/conaveg",
            description = "Servidor de Desarrollo Local"
        ),
        @Server(
            url = "https://api.conaveg.com",
            description = "Servidor de Producción"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = """
        **Autenticación JWT Bearer Token**
        
        Para autenticarte:
        1. Llama al endpoint `POST /api/auth/login` con tus credenciales
        2. Copia el token JWT de la respuesta
        3. Haz clic en "Authorize" y pega el token (sin 'Bearer ')
        4. El sistema automáticamente agregará 'Bearer ' al token
        
        **Ejemplo de token:**
        ```
        eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
        ```
        
        **El token incluye:**
        - Email del usuario
        - ID del usuario 
        - Rol normalizado (ADMIN, GERENTE, EMPLEADO, USER)
        - Tiempo de expiración (24 horas por defecto)
        """
)
public class OpenApiConfig {
    // Esta clase funciona solo con anotaciones
    // No necesita métodos @Bean adicionales con springdoc-openapi 2.x
}
