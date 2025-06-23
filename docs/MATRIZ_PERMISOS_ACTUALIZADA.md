# Matriz de Permisos - Sistema CONA (Actualizada)

## Resumen de Cambios
- **Fecha:** Diciembre 2024
- **Versión:** 1.1
- **Cambios principales:**
  - Rol USER ahora tiene acceso de solo lectura a proyectos e inventario
  - Eliminados métodos duplicados en AuthorizationService
  - Corregida la lógica de autorización para ser más granular

## Matriz de Permisos por Rol

### Leyenda:
- ✅ **Acceso Total** (lectura y escritura)
- 👁️ **Solo Lectura** (consulta únicamente)
- ❌ **Sin Acceso**

| Endpoint | Método | ADMIN | GERENTE | EMPLEADO | USER |
|----------|--------|-------|---------|----------|------|
| **Usuarios** | | | | | |
| /api/users | GET | ✅ | ❌ | ❌ | ❌ |
| /api/users/{id} | GET | ✅ | 👁️* | 👁️* | 👁️* |
| /api/users | POST | ✅ | ❌ | ❌ | ❌ |
| /api/users/{id} | PUT | ✅ | ✅* | ✅* | ✅* |
| /api/users/{id} | DELETE | ✅ | ❌ | ❌ | ❌ |
| **Empleados** | | | | | |
| /api/empleados | GET | ✅ | ❌ | ✅ | ❌ |
| /api/empleados/{id} | GET | ✅ | ❌ | ✅ | ❌ |
| /api/empleados | POST | ✅ | ❌ | ❌ | ❌ |
| /api/empleados/{id} | PUT | ✅ | ❌ | ❌ | ❌ |
| /api/empleados/{id} | DELETE | ✅ | ❌ | ❌ | ❌ |
| **Proyectos** | | | | | |
| /api/proyectos | GET | ✅ | ✅ | 👁️ | 👁️ |
| /api/proyectos/{id} | GET | ✅ | ✅ | 👁️ | 👁️ |
| /api/proyectos | POST | ✅ | ✅ | ❌ | ❌ |
| /api/proyectos/{id} | PUT | ✅ | ✅ | ❌ | ❌ |
| /api/proyectos/{id} | DELETE | ✅ | ❌ | ❌ | ❌ |
| **Inventario** | | | | | |
| /api/inventario | GET | ✅ | ✅ | 👁️ | 👁️ |
| /api/inventario/{id} | GET | ✅ | ✅ | 👁️ | 👁️ |
| /api/inventario | POST | ✅ | ✅ | ❌ | ❌ |
| /api/inventario/{id} | PUT | ✅ | ✅ | ❌ | ❌ |
| /api/inventario/{id} | DELETE | ✅ | ❌ | ❌ | ❌ |
| **Roles** | | | | | |
| /api/roles | GET | ✅ | ❌ | ❌ | ❌ |
| /api/roles/{id} | GET | ✅ | ❌ | ❌ | ❌ |
| /api/roles | POST | ✅ | ❌ | ❌ | ❌ |
| /api/roles/{id} | PUT | ✅ | ❌ | ❌ | ❌ |
| /api/roles/{id} | DELETE | ✅ | ❌ | ❌ | ❌ |

*Solo pueden acceder a su propio perfil de usuario

## Descripción de Roles

### ADMIN (Administrador)
- **Permisos completos** en todo el sistema
- Gestión de usuarios, empleados, roles, proyectos e inventario
- Único rol con capacidad de eliminación en todos los módulos
- Acceso a configuraciones del sistema y datos financieros

### GERENTE
- **Gestión operativa** de proyectos e inventario
- Puede crear, modificar proyectos e inventario
- **Puede ver y editar** su propio perfil de usuario
- **Sin acceso** a gestión de usuarios de otros ni empleados
- **Sin capacidad** de eliminación (excepto sus propios registros)

### EMPLEADO
- **Acceso completo** a información de empleados
- **Solo lectura** en proyectos e inventario
- **Puede ver y editar** su propio perfil de usuario
- **Sin capacidades** de modificación en proyectos/inventario

### USER
- **Solo lectura** en proyectos e inventario
- **Puede ver y editar** su propio perfil de usuario
- **Sin acceso** a gestión de empleados
- Rol básico para consultas generales

## Implementación Técnica

### Guards de Autorización
```java
// Endpoints de solo lectura que incluyen USER
@PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('EMPLEADO') or hasRole('USER')")

// Endpoints de modificación (excluyen USER)
@PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")

// Endpoints administrativos (solo ADMIN)
@PreAuthorize("hasRole('ADMIN')")
```

### Métodos en AuthorizationService
- `canViewProjects()` - Incluye todos los roles autenticados
- `canViewInventory()` - Incluye todos los roles autenticados  
- `canManageProjects()` - Solo ADMIN y GERENTE
- `canManageInventory()` - Solo ADMIN y GERENTE
- `canAccessEmployeeData()` - Solo ADMIN y EMPLEADO
- `canManageUsers()` - Solo ADMIN

### Mapeo de Roles de Base de Datos
```java
// En JwtUtil.normalizeRole()
"Administrador" -> "ADMIN"
"Gerente" -> "GERENTE" 
"Empleado" -> "EMPLEADO"
// Otros valores -> "USER" (por defecto)
```

## Notas de Seguridad

1. **Principio de Menor Privilegio**: Cada rol tiene solo los permisos mínimos necesarios
2. **Separación de Responsabilidades**: Roles claramente diferenciados por función
3. **Acceso Granular**: Distinción entre operaciones de lectura y escritura
4. **Escalabilidad**: Estructura que permite agregar nuevos roles fácilmente

## Pruebas Recomendadas

### Para cada rol:
1. **Autenticación exitosa** con credenciales válidas
2. **Acceso permitido** a endpoints autorizados
3. **Bloqueo correcto** en endpoints no autorizados
4. **Respuestas HTTP apropiadas** (200, 403, 401)

### Casos específicos:
- USER accediendo a GET /api/proyectos (debe funcionar)
- USER intentando POST /api/proyectos (debe fallar con 403)
- EMPLEADO accediendo a GET /api/empleados (debe funcionar)
- GERENTE intentando DELETE /api/roles (debe fallar con 403)

---
**Documento generado automáticamente - Fase 1 Autenticación y Autorización**
