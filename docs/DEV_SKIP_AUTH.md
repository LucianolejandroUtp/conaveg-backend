# Configuración de Desarrollo - Saltarse Autenticación

## ⚠️ ADVERTENCIA IMPORTANTE
**ESTA CONFIGURACIÓN SOLO DEBE USARSE EN DESARROLLO. NUNCA EN PRODUCCIÓN.**

## Descripción
Para facilitar el trabajo del equipo frontend durante el desarrollo, se ha implementado una configuración que permite saltarse completamente la autenticación en el backend.

## Cómo Activar el Modo Desarrollo

### Opción 1: Variable de Entorno
Establecer la variable de entorno al ejecutar la aplicación:
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

### Opción 2: Argument JVM
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Opción 3: IDE (Recomendado)
En tu IDE, configura la variable de entorno:
- **Variable**: `SPRING_PROFILES_ACTIVE`
- **Valor**: `dev`

## Qué Hace Esta Configuración

1. **Desactiva completamente la autenticación JWT**
   - Todos los endpoints son accesibles sin token
   - No se validan permisos ni roles

2. **Proporciona endpoints simulados** en `/api/dev/`:
   - `POST /api/dev/login` - Simula login exitoso
   - `GET /api/dev/me` - Devuelve usuario ficticio
   - `POST /api/dev/refresh` - Simula refresh token
   - `POST /api/dev/logout` - Simula logout
   - `GET /api/dev/status` - Verifica estado del modo dev

3. **Configuraciones relajadas**:
   - Logging detallado habilitado
   - Rate limiting deshabilitado
   - Auditoría deshabilitada
   - Limpieza automática deshabilitada

## Para el Equipo Frontend

### Endpoints Disponibles en Desarrollo

#### Login Simulado
```bash
POST /api/dev/login
Content-Type: application/json

{
  "email": "cualquier@email.com",
  "password": "cualquier-password"
}
```

**Respuesta:**
```json
{
  "token": "dev-mock-jwt-token-12345",
  "type": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "userName": "dev-user",
    "email": "dev@conaveg.com",
    "role": "ADMINISTRADOR",
    "estado": "ACTIVO"
  },
  "message": "Login simulado para desarrollo"
}
```

#### Usuario Actual
```bash
GET /api/dev/me
```

#### Verificar Estado de Desarrollo
```bash
GET /api/dev/status
```

### Acceso a Endpoints Regulares
Cuando el modo desarrollo está activo, **TODOS** los endpoints regulares de la API están disponibles sin autenticación:

- `GET /api/empleados` ✅ Sin token
- `POST /api/proyectos` ✅ Sin token
- `PUT /api/inventario/1` ✅ Sin token
- etc.

## Cómo Verificar que Está Activo

1. **Check del endpoint de estado:**
   ```bash
   GET http://localhost:8080/conaveg/api/dev/status
   ```

2. **Logs de aplicación:**
   Deberías ver en los logs:
   ```
   The following profiles are active: dev
   ```

3. **Acceso directo a endpoints protegidos:**
   ```bash
   GET http://localhost:8080/conaveg/api/empleados
   ```
   Debería funcionar sin token.

## Volver al Modo Normal

Para volver al modo de autenticación normal:

1. **Quitar la variable de entorno:**
   ```bash
   unset SPRING_PROFILES_ACTIVE
   ```

2. **O cambiar el perfil:**
   ```bash
   export SPRING_PROFILES_ACTIVE=prod
   ```

3. **Reiniciar la aplicación**

## Archivos Involucrados

- `application-dev.properties` - Configuración específica de desarrollo
- `BaseConfig.java` - Configuración base (passwordEncoder siempre disponible)
- `DevSecurityConfig.java` - Configuración de seguridad para desarrollo
- `DevController.java` - Endpoints simulados
- `SecurityConfig.java` - Modificado para desactivarse en modo dev

## Troubleshooting

### Problema: Los endpoints siguen pidiendo autenticación
**Solución:** Verificar que:
1. El perfil `dev` está activo
2. La propiedad `app.dev.skip-authentication=true` está configurada
3. La aplicación se reinició correctamente

### Problema: No veo los endpoints `/api/dev/`
**Solución:** 
1. Verificar el perfil activo
2. Comprobar logs de inicio de la aplicación
3. Verificar que `DevController` se está cargando

### Problema: Error "PasswordEncoder bean not found"
**Solución:**
1. Verificar que `BaseConfig.java` existe y contiene el bean `passwordEncoder`
2. Reiniciar la aplicación completamente
3. El bean `passwordEncoder` debe estar siempre disponible, incluso en modo desarrollo

### Problema: Accidentalmente desplegué en producción con modo dev
**Solución de emergencia:**
1. Establecer `SPRING_PROFILES_ACTIVE=prod` inmediatamente
2. Reiniciar la aplicación
3. Verificar que la autenticación funciona normalmente

## Beneficios para el Desarrollo

✅ **Frontend no necesita implementar login inicialmente**  
✅ **Acceso inmediato a todos los endpoints**  
✅ **Datos de usuario consistentes para pruebas**  
✅ **Proceso de desarrollo más rápido**  
✅ **Fácil de activar/desactivar**  

## Seguridad

- ✅ Solo se activa con perfil específico
- ✅ Doble validación (perfil + propiedad)
- ✅ Claramente marcado como desarrollo
- ✅ No afecta configuración de producción
- ✅ Fácil de detectar en logs
