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

# conaveg-backend

Backend API REST para la administración de inventarios, asistencia de personal y asignación de personal a diferentes locaciones de trabajo.

## Tabla de Contenidos
- [Descripción General](#descripción-general)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Configuración de Base de Datos](#configuración-de-base-de-datos)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Recursos y Endpoints Principales](#recursos-y-endpoints-principales)
- [Ejemplo de Modelo y Endpoint](#ejemplo-de-modelo-y-endpoint)
- [Seguridad](#seguridad)
- [Pruebas](#pruebas)
- [Convenciones de Commits](#convenciones-de-commits)
- [Licencia](#licencia)
- [Documentación automática de la API (Swagger/OpenAPI)](#documentación-automática-de-la-api-swaggeropenapi)

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
- `controllers/`: Endpoints REST.
- `services/`: Lógica de negocio.
- `repositories/`: Acceso a datos (JPA).
- `models/`: Entidades del dominio.
- `resources/`: Configuración y recursos estáticos.

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
  "role": { "id": 1 },
  "userName": "jdoe",
  "email": "jdoe@example.com",
  "password": "12345678"
}
```

## Seguridad
El proyecto utiliza Spring Security, pero actualmente está deshabilitado para facilitar el desarrollo. Se recomienda habilitarlo antes de producción.

## Pruebas
- Puedes probar los endpoints con Postman, Insomnia o curl.
- Para ejecutar pruebas automáticas:
  ```bash
  mvn test
  ```

## Convenciones de Commits
Se utiliza [Conventional Commits](./GuiaConventionalCommits.md) con emojis para los mensajes de commit. Ejemplo:
```
📚 docs: agrega guía de Conventional Commits y emojis
```

## Licencia
Este proyecto está bajo la licencia MIT.

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