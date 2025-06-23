# CONA - Sistema de Gestión

Backend API REST para la administración de inventarios, asistencia de personal y asignación de personal a diferentes locaciones de trabajo.

## 📋 Tabla de Contenidos
- [Documentación Completa](#-documentación-completa)
- [Descripción General](#descripción-general)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Consideraciones sobre los campos generados automáticamente por la base de datos](#consideraciones-sobre-los-campos-generados-automáticamente-por-la-base-de-datos)
- [Recursos y Endpoints Principales](#recursos-y-endpoints-principales)
- [Ejemplo de Modelos y Endpoints](#ejemplo-de-modelos-y-endpoints)
- [Seguridad](#seguridad)
- [Pruebas](#pruebas)
- [Convenciones de Commits](#convenciones-de-commits)
- [Licencia](#licencia)
- [Documentación automática de la API (Swagger/OpenAPI)](#documentación-automática-de-la-api-swaggeropenapi)

## 📖 Documentación Completa

Toda la documentación técnica del proyecto está organizada en la carpeta [`docs/`](docs/):

### 📚 Documentos Principales
- **[📋 Reporte Final del Proyecto](docs/REPORTE_FINAL_PROYECTO.md)** - Resumen ejecutivo completo del proyecto
- **[🗺️ Roadmap del Proyecto](docs/ROADMAP.md)** - Avances y pendientes del proyecto
- **[📊 Métricas de Rendimiento](docs/PERFORMANCE_METRICS.md)** - Resultados de tests de carga y rendimiento
- **[🔧 Guía de Conventional Commits](docs/GuiaConventionalCommits.md)** - Estándares para mensajes de commit

### 🔒 Documentación de Seguridad
- **[🔐 Mejores Prácticas de Seguridad](docs/Security_Best_Practices.md)** - Guía de seguridad
- **[🔑 Guía de Uso de BCrypt](docs/BCrypt_Usage_Guide.md)** - Implementación de cifrado de contraseñas
- **[🛡️ Matriz de Permisos](docs/MATRIZ_PERMISOS_ACTUALIZADA.md)** - Sistema completo de autorización por roles
- **[🔧 Corrección GERENTE](docs/CORRECCION_GERENTE_USUARIO.md)** - Fix permisos para gestión de perfil propio
- **[⚡ Guía de Tests de Rendimiento](docs/Performance_Testing_Guide.md)** - Tests de carga y estrés

### 📊 Diagramas y Esquemas
- **[🏗️ Diagrama de Clases](docs/ClassDiagram.png)** - Estructura de clases del sistema
- **[🗄️ Diagrama de Base de Datos](docs/DataBaseDiagram.png)** - Esquema de la base de datos
- **[📦 Diagrama de Paquetes](docs/PackageDiagram.png)** - Organización de paquetes
- **[💾 Script de Base de Datos](docs/conaveg_database.sql)** - Esquema SQL para MariaDB

### 📖 Índice de Documentación
- **[📑 Índice General de Documentación](docs/README.md)** - Guía completa con toda la documentación técnica

---

## Descripción General
Sistema backend desarrollado en Spring Boot para gestionar inventarios, asistencia de empleados y asignación de personal a proyectos/locaciones.

## Requisitos
- Java 21
- Maven
- MariaDB

## Instalación
1. Clona el repositorio.
2. Configura la base de datos MariaDB y actualiza las credenciales en `src/main/resources/application.properties`.
3. Ejecuta:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## Configuración de Base de Datos
El sistema utiliza MariaDB. Ejemplo de configuración en `application.properties`:
```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/conaveg_db
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
```
Asegúrate de tener la base de datos creada antes de iniciar la aplicación.

## Estructura del Proyecto
- `config/`: Configuraciones de la aplicación (Jackson, etc.).
- `controller/`: Endpoints REST.
- `dto/`: Objetos de Transferencia de Datos (DTOs) para la API.
- `model/`: Entidades del dominio (JPA).
- `repository/`: Acceso a datos (JPA).
- `service/`: Lógica de negocio.
- `resources/`: Configuración y recursos estáticos.

## Consideraciones sobre los campos generados automáticamente por la base de datos

Todas las tablas de la base de datos incluyen los siguientes campos estándar:

- `id`
- `estado`
- `unique_id`
- `created_at`
- `updated_at`

Estos campos son gestionados automáticamente por la base de datos y **no es necesario incluirlos en las peticiones de creación o actualización** desde el frontend o cualquier cliente del API. Por ejemplo:

- Al crear un registro (POST), la base de datos asigna automáticamente estos valores si no se envían.
- Al actualizar un registro (PUT), tampoco es necesario enviar estos campos. El campo `updated_at` se actualiza automáticamente cada vez que se modifica el registro.

> **Recomendación:** Omite estos campos en los cuerpos de las peticiones de creación y actualización. Solo debes enviar los datos realmente editables por el usuario.

Esta consideración aplica para todos los recursos del sistema (usuarios, roles, empleados, facturas, etc.).

## Recursos y Endpoints Principales
| Recurso                        | Endpoint Base                        |
|--------------------------------|--------------------------------------|
| Roles                          | `/api/roles`                         |
| Usuarios                       | `/api/users`                         |
| Proyectos                      | `/api/proyectos`                     |
| Proveedores                    | `/api/proveedores`                   |
| Movimientos de Inventario      | `/api/movimientos-inventario`        |
| Inventario                     | `/api/inventario`                    |
| Facturas                       | `/api/facturas`                      |
| Empleados                      | `/api/empleados`                     |
| Categorías de Inventario       | `/api/categorias-inventario`         |
| Asistencias                    | `/api/asistencias`                   |
| Asignaciones Proyectos-Empleado| `/api/asignaciones-proyectos-empleado`|

Cada recurso expone operaciones CRUD estándar (GET, POST, PUT, DELETE).


## Ejemplo de Modelos y Endpoints

### Modelo: Rol
```java
public class Rol {
    private Long id;
    private String nombre;
    private String descripcion;
    private String estado; // generado por BD
    private String uniqueId; // generado por BD
    private Instant createdAt; // generado por BD
    private Instant updatedAt; // generado por BD
    // getters y setters
}
```

#### Endpoints Rol
- `GET /api/roles` — Listar todos los roles
- `GET /api/roles/{id}` — Obtener un rol por ID
- `POST /api/roles` — Crear nuevo rol
- `PUT /api/roles/{id}` — Actualizar rol existente
- `DELETE /api/roles/{id}` — Eliminar rol

#### Ejemplo de request para crear un Rol
```json
{
  "nombre": "Administrador",
  "descripcion": "Rol con todos los permisos"
}
```

### Modelo: User
```java
public class User {
    private Long id;
    private Rol role; // relación ManyToOne
    private String userName;
    private String email;
    private Instant emailVerifiedAt;
    private String password;
    private String rememberToken;
    private String estado; // generado por BD
    private String uniqueId; // generado por BD
    private Instant createdAt; // generado por BD
    private Instant updatedAt; // generado por BD
    // getters y setters
}
```

#### Endpoints User
- `GET /api/users` — Listar todos los usuarios
- `GET /api/users/{id}` — Obtener un usuario por ID
- `POST /api/users` — Crear nuevo usuario
- `PUT /api/users/{id}` — Actualizar usuario existente
- `DELETE /api/users/{id}` — Eliminar usuario

#### Ejemplo de request para crear un User
```json
{
  "userName": "jdoe",
  "email": "jdoe@example.com",
  "password": "MiPassword123",
  "roleId": 1
}
```

> **Nota sobre contraseñas**: La contraseña debe tener al menos 8 caracteres, incluyendo una letra minúscula, una mayúscula y un número. Se cifra automáticamente con BCrypt antes de almacenarla.

## Seguridad

El sistema implementa un **sistema completo de autenticación y autorización** usando Spring Security con JWT y control de acceso por roles.

### 🔐 Autenticación JWT
- **Tokens JWT** para autenticación stateless
- **Roles extraídos del token** para autorización granular
- **Filtro personalizado** (`JwtAuthenticationFilter`) para validación automática
- **Detalles de autenticación** personalizados con información del usuario

### 🛡️ Autorización por Roles
El sistema implementa **4 roles principales** con permisos específicos:

- **ADMIN**: Acceso total al sistema, gestión de usuarios y roles
- **GERENTE**: Gestión operativa de proyectos e inventario + su propio perfil
- **EMPLEADO**: Acceso a información de empleados + su propio perfil
- **USER**: Solo lectura en proyectos/inventario + su propio perfil

### 🔑 Guards de Seguridad
Los endpoints están protegidos usando anotaciones `@PreAuthorize`:

```java
// Solo administradores
@PreAuthorize("hasRole('ADMIN')")

// Roles específicos
@PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")

// Ownership - usuario puede ver/editar su propio perfil
@PreAuthorize("hasRole('ADMIN') or (hasAnyRole('GERENTE','EMPLEADO','USER') and @authorizationService.isOwnerOrAdmin(#id))")
```

### 🔒 Cifrado de Contraseñas
El sistema implementa cifrado seguro de contraseñas usando **BCrypt**:

- **Almacenamiento**: Las contraseñas se almacenan cifradas en la base de datos usando BCrypt con salt automático.
- **Creación de usuarios**: Al crear un usuario, la contraseña se cifra automáticamente antes de guardarla.
- **Actualización**: Al actualizar un usuario, si se proporciona una nueva contraseña, se cifra antes de actualizarla.
- **Validación**: El servicio incluye un método `validatePassword()` para futuras implementaciones de autenticación.

#### Características de BCrypt:
- **Salt automático**: Cada contraseña tiene su propio salt único.
- **Resistente a ataques**: Algoritmo lento que dificulta ataques de fuerza bruta.
- **Estándar de la industria**: Ampliamente adoptado y probado.

### 📋 Matriz de Permisos Completa
Para detalles completos sobre permisos por rol y endpoint, consulta:
- **[🛡️ Matriz de Permisos](docs/MATRIZ_PERMISOS_ACTUALIZADA.md)** - Documentación completa del sistema de autorización

#### Ejemplo de uso:
```java
// En UserService, las contraseñas se cifran automáticamente
UserCreateDTO userDto = new UserCreateDTO();
userDto.setPassword("miContraseñaSegura123"); // Texto plano
userService.saveUser(userDto); // Se guarda cifrada en BD

// Para validar contraseñas (útil para login)
boolean isValid = userService.validatePassword("miContraseñaSegura123", hashedPassword);
```

> **Importante**: Las contraseñas nunca se almacenan en texto plano. Una vez cifradas, no pueden ser "descifradas", solo validadas.

## Pruebas
- Puedes probar los endpoints con Postman, Insomnia o curl.
- Para ejecutar pruebas automáticas:
  ```bash
  mvn test
  ```

### Mejoras Técnicas Implementadas ⚡

El proyecto ha incorporado las siguientes mejoras técnicas:

#### Migración de Anotaciones de Testing
- **Actualizado**: `@MockBean` → `@MockitoBean` (Spring Boot 3.5.0+)
- **Import**: `org.springframework.test.context.bean.override.mockito.MockitoBean`
- **Beneficio**: Mejor compatibilidad y preparación para futuras versiones

#### Versiones Actualizadas
- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.0
- **Maven**: Compatible con Java 21

#### Tests Modernizados
- 11 archivos de controllers con `@MockitoBean`
- 119 tests pasando exitosamente
- Arquitectura de testing robusta y actualizada

---

### Estado de Tests ✅ (Actualizado Junio 9, 2025)
- **Tests de integración de controllers**: 11 archivos, 119 tests ✅ PASANDO
- **Tests de servicios**: 5 tests ✅ PASANDO  
- **Total**: 124 tests exitosos, 0 fallos, 0 errores
- **Última ejecución**: BUILD SUCCESS

**Para más detalles**:
- **Tests de integración de controllers**: Ver [documentación técnica](docs/README.md)
- **Tests de rendimiento**: Ver [guía de tests de rendimiento](docs/Performance_Testing_Guide.md)

## Convenciones de Commits
Se utiliza [Conventional Commits](docs/GuiaConventionalCommits.md) con emojis para los mensajes de commit. Ejemplo:
```
📚 docs: agrega guía de Conventional Commits y emojis
```

## Licencia
Este proyecto está bajo la licencia Apache 2.0. Consulta el archivo [LICENSE](LICENSE) para más detalles.

## Documentación automática de la API (Swagger/OpenAPI)

El proyecto incluye documentación interactiva de la API generada automáticamente con **springdoc-openapi**.

- Accede a la documentación en:
  - [http://localhost:8080/conaveg/swagger-ui/index.html](http://localhost:8080/conaveg/swagger-ui/index.html)

Desde esta interfaz puedes:
- Consultar todos los endpoints disponibles y sus detalles.
- Probar los endpoints directamente desde el navegador.
- Descargar la especificación OpenAPI en formato JSON/YAML para integraciones o generación de clientes.

### Personalización
Puedes mejorar la documentación usando anotaciones como:
- `@Operation(summary = "...", description = "...")` en tus métodos de controlador.
- `@Parameter`, `@Schema` para describir parámetros y modelos.

Para más opciones, consulta la [documentación oficial de springdoc-openapi](https://springdoc.org/).