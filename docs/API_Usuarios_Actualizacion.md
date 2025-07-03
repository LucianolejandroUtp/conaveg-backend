# API de Usuarios - Actualización con Contraseña Opcional

## Cambio Implementado

Se ha modificado el endpoint de actualización de usuarios para hacer que la contraseña sea **opcional** durante las actualizaciones.

### Endpoint Actualizado

**PUT** `/api/users/{id}`

### Comportamiento Anterior
- La contraseña era **obligatoria** en todas las actualizaciones
- Siempre se tenía que proporcionar una nueva contraseña

### Comportamiento Nuevo
- La contraseña es **opcional** en las actualizaciones
- Si se proporciona contraseña → se actualiza y cifra con BCrypt
- Si NO se proporciona contraseña → se mantiene la contraseña actual sin cambios
- Todos los demás campos se actualizan normalmente

## Estructura del UserUpdateDTO

```json
{
  "userName": "string (obligatorio, 3-50 caracteres)",
  "email": "string (obligatorio, formato email válido)",
  "password": "string (opcional, 8-100 caracteres, debe contener mayúscula, minúscula y número)",
  "roleId": "number (opcional)"
}
```

## Ejemplos de Uso

### Actualizar solo datos básicos (sin contraseña)
```json
{
  "userName": "usuario_actualizado",
  "email": "nuevo@email.com",
  "roleId": 2
}
```
**Resultado:** Se actualizan nombre, email y rol. La contraseña permanece igual.

### Actualizar incluyendo nueva contraseña
```json
{
  "userName": "usuario_actualizado",
  "email": "nuevo@email.com",
  "password": "NuevaPassword123",
  "roleId": 2
}
```
**Resultado:** Se actualizan todos los campos incluyendo la nueva contraseña cifrada.

### Actualizar solo la contraseña
```json
{
  "userName": "usuario_actual",
  "email": "email_actual@example.com",
  "password": "NuevaPassword456"
}
```
**Resultado:** Solo se actualiza la contraseña, los demás campos permanecen igual.

## Validaciones

### Campos Obligatorios
- `userName`: Siempre requerido (3-50 caracteres)
- `email`: Siempre requerido (formato email válido)

### Campos Opcionales
- `password`: Solo se valida si se proporciona
  - Mínimo 8 caracteres, máximo 100
  - Debe contener al menos una mayúscula, una minúscula y un número
- `roleId`: Opcional, debe existir en la base de datos

## Seguridad

- Las contraseñas se cifran automáticamente con BCrypt antes de guardar
- Solo administradores pueden actualizar cualquier usuario
- Usuarios normales solo pueden actualizar su propia información
- El JWT token sigue siendo requerido para la autenticación

## Ventajas del Cambio

1. **Flexibilidad**: Permite actualizar perfil sin cambiar contraseña
2. **Usabilidad**: Mejor experiencia de usuario en formularios de edición
3. **Seguridad**: Mantiene el cifrado BCrypt cuando se actualiza la contraseña
4. **Compatibilidad**: No rompe la funcionalidad existente

## Casos de Uso Típicos

- **Actualizar perfil**: Cambiar nombre o email sin tocar la contraseña
- **Cambio de rol**: Administrador asigna nuevo rol sin resetear contraseña
- **Cambio de contraseña**: Usuario actualiza solo su contraseña
- **Actualización completa**: Cambiar todos los datos incluida la contraseña
