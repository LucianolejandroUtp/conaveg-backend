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

## Ejemplo de Modelo y Endpoint
### Modelo: Inventario (fragmento)
```java
public class Inventario {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    // ...otros campos
}
```

### Endpoint: Inventario
- `GET /api/inventario` — Listar inventario
- `GET /api/inventario/{id}` — Obtener un ítem por ID
- `POST /api/inventario` — Crear nuevo ítem
- `PUT /api/inventario/{id}` — Actualizar ítem
- `DELETE /api/inventario/{id}` — Eliminar ítem

#### Ejemplo de request para crear inventario
```json
{
  "codigo": "EQ-001",
  "nombre": "Laptop Dell",
  "descripcion": "Laptop para oficina",
  "marca": "Dell",
  "modelo": "Inspiron 15"
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