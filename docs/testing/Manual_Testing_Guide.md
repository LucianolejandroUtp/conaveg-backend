# 🧪 GUÍA DE TESTING MANUAL - FASE 3: FUNCIONALIDADES AVANZADAS DE AUTENTICACIÓN

## 📋 **INFORMACIÓN DEL DOCUMENTO**

**Fecha de Creación**: 24 de Junio de 2025  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Fase**: Fase 3 - Funcionalidades Avanzadas de Autenticación  
**Responsable**: Equipo de Desarrollo  
**Audiencia**: QA, Desarrolladores, DevOps  

---

## 🎯 **OBJETIVO**

Esta guía proporciona procedimientos paso a paso para realizar testing manual de todas las funcionalidades implementadas en la Fase 3, incluyendo recuperación de contraseñas, rate limiting, auditoría de seguridad y tareas programadas.

---

## 🔧 **PREPARACIÓN DEL ENTORNO DE TESTING**

### **Prerrequisitos**
- ✅ Aplicación ejecutándose en `http://localhost:8080`
- ✅ Base de datos MariaDB conectada y sincronizada
- ✅ Configuración SMTP (opcional para tests de email)
- ✅ Herramientas: Postman, curl, o navegador web
- ✅ Acceso a logs de la aplicación

### **Configuración Inicial**
```bash
# 1. Verificar que el servidor esté ejecutándose
curl -s http://localhost:8080/conaveg/actuator/health

# 2. Verificar logs de inicio
tail -f logs/spring.log | grep "Started ConaApplication"
```

---

## 🔑 **TESTING SUITE 1: SISTEMA DE RECUPERACIÓN DE CONTRASEÑAS**

### **Test Case 1.1: Solicitar Recuperación de Contraseña**

#### **Objetivo**: Verificar endpoint POST `/api/auth/forgot-password`

#### **Datos de Prueba**:
```json
{
  "email": "usuario.existente@conaveg.com"
}
```

#### **Procedimiento**:
1. **Enviar Request POST**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/forgot-password \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario.existente@conaveg.com"}'
   ```

2. **Verificar Respuesta Esperada**:
   ```json
   {
     "message": "Se ha enviado un enlace de recuperación a tu email",
     "success": true
   }
   ```

3. **Verificar en Base de Datos**:
   ```sql
   SELECT * FROM password_reset_tokens 
   WHERE email = 'usuario.existente@conaveg.com' 
   ORDER BY created_at DESC LIMIT 1;
   ```

4. **Verificar en Logs**:
   ```
   [PASSWORD_RESET_REQUESTED] Email: usuario.existente@conaveg.com
   ```

#### **Casos Edge**:
- ❌ **Email inexistente**: `{"error": "Email no encontrado"}`
- ❌ **Email inválido**: `{"error": "Formato de email inválido"}`
- ❌ **Rate limiting**: `{"error": "Demasiadas solicitudes"}`

### **Test Case 1.2: Validar Token de Recuperación**

#### **Objetivo**: Verificar endpoint GET `/api/auth/validate-reset-token`

#### **Procedimiento**:
1. **Obtener token de BD**:
   ```sql
   SELECT token FROM password_reset_tokens WHERE used = false LIMIT 1;
   ```

2. **Enviar Request GET**:
   ```bash
   curl -X GET "http://localhost:8080/conaveg/api/auth/validate-reset-token?token=TOKEN_AQUI"
   ```

3. **Verificar Respuesta Válida**:
   ```json
   {
     "valid": true,
     "message": "Token válido"
   }
   ```

#### **Casos Edge**:
- ❌ **Token expirado**: `{"valid": false, "message": "Token expirado"}`
- ❌ **Token inválido**: `{"valid": false, "message": "Token no encontrado"}`
- ❌ **Token usado**: `{"valid": false, "message": "Token ya utilizado"}`

### **Test Case 1.3: Resetear Contraseña**

#### **Objetivo**: Verificar endpoint POST `/api/auth/reset-password`

#### **Datos de Prueba**:
```json
{
  "token": "TOKEN_VALIDO_AQUI",
  "newPassword": "NuevaPassword123!",
  "confirmPassword": "NuevaPassword123!"
}
```

#### **Procedimiento**:
1. **Enviar Request POST**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/reset-password \
   -H "Content-Type: application/json" \
   -d '{"token":"TOKEN_AQUI","newPassword":"NuevaPassword123!","confirmPassword":"NuevaPassword123!"}'
   ```

2. **Verificar Respuesta Exitosa**:
   ```json
   {
     "message": "Contraseña cambiada exitosamente",
     "success": true
   }
   ```

3. **Verificar Login con Nueva Contraseña**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario@conaveg.com","password":"NuevaPassword123!"}'
   ```

4. **Verificar Token Marcado como Usado**:
   ```sql
   SELECT used, used_at FROM password_reset_tokens WHERE token = 'TOKEN_AQUI';
   ```

---

## 🔒 **TESTING SUITE 2: SISTEMA DE RATE LIMITING**

### **Test Case 2.1: Rate Limiting por IP**

#### **Objetivo**: Verificar bloqueo automático después de intentos excesivos

#### **Procedimiento**:
1. **Realizar 10 intentos fallidos rápidamente**:
   ```bash
   for i in {1..12}; do
     echo "Intento $i"
     curl -X POST http://localhost:8080/conaveg/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"test@fake.com","password":"wrongpassword"}'
     echo ""
   done
   ```

2. **Verificar Respuesta de Bloqueo** (intento 11-12):
   ```json
   {
     "error": "Demasiados intentos desde esta IP. Acceso temporalmente bloqueado.",
     "code": "RATE_LIMIT_EXCEEDED",
     "retryAfter": 900
   }
   ```

3. **Verificar Status Code**: `429 Too Many Requests`

4. **Verificar en Logs de Auditoría**:
   ```
   [RATE_LIMIT_EXCEEDED] IP xxx.xxx.xxx.xxx blocked on endpoint /api/auth/login
   ```

### **Test Case 2.2: Rate Limiting por Email**

#### **Objetivo**: Verificar bloqueo específico por cuenta de email

#### **Procedimiento**:
1. **Realizar intentos fallidos con email específico**:
   ```bash
   for i in {1..8}; do
     echo "Intento para email específico: $i"
     curl -X POST http://localhost:8080/conaveg/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"usuario.especifico@conaveg.com","password":"wrongpassword"}'
   done
   ```

2. **Verificar Bloqueo de Email**:
   ```json
   {
     "error": "Demasiados intentos fallidos para este email. Cuenta temporalmente bloqueada.",
     "code": "EMAIL_RATE_LIMIT_EXCEEDED",
     "retryAfter": 900
   }
   ```

3. **Verificar Base de Datos**:
   ```sql
   SELECT COUNT(*) FROM authentication_attempts 
   WHERE email = 'usuario.especifico@conaveg.com' 
   AND successful = false 
   AND attempt_time > NOW() - INTERVAL 1 HOUR;
   ```

### **Test Case 2.3: Recuperación Automática**

#### **Objetivo**: Verificar que el bloqueo se levanta automáticamente

#### **Procedimiento**:
1. **Esperar período de bloqueo** (15 minutos en configuración)
2. **Intentar login nuevamente**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario@conaveg.com","password":"password_correcto"}'
   ```

3. **Verificar Login Exitoso**: Status `200 OK`

---

## 📊 **TESTING SUITE 3: SISTEMA DE AUDITORÍA DE SEGURIDAD**

### **Test Case 3.1: Auditoría de Login Exitoso**

#### **Objetivo**: Verificar logging automático de autenticación exitosa

#### **Procedimiento**:
1. **Realizar Login Exitoso**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario@conaveg.com","password":"password_correcto"}'
   ```

2. **Verificar en Base de Datos**:
   ```sql
   SELECT * FROM security_audit_logs 
   WHERE event_type = 'LOGIN_SUCCESS' 
   AND email = 'usuario@conaveg.com'
   ORDER BY timestamp DESC LIMIT 1;
   ```

3. **Verificar Campos Esperados**:
   - `event_type`: "LOGIN_SUCCESS"
   - `email`: Email del usuario
   - `ip_address`: IP del cliente
   - `user_agent`: Información del navegador
   - `severity`: "LOW"
   - `success`: true

### **Test Case 3.2: Auditoría de Login Fallido**

#### **Objetivo**: Verificar logging de intentos fallidos

#### **Procedimiento**:
1. **Realizar Login Fallido**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario@conaveg.com","password":"password_incorrecto"}'
   ```

2. **Verificar en Base de Datos**:
   ```sql
   SELECT * FROM security_audit_logs 
   WHERE event_type = 'LOGIN_FAILED' 
   AND email = 'usuario@conaveg.com'
   ORDER BY timestamp DESC LIMIT 1;
   ```

3. **Verificar Campos**:
   - `event_type`: "LOGIN_FAILED"
   - `severity`: "MEDIUM"
   - `success`: false
   - `details`: Información del error

### **Test Case 3.3: Auditoría de Recuperación de Contraseña**

#### **Objetivo**: Verificar logging de eventos de recuperación

#### **Procedimiento**:
1. **Solicitar Recuperación**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/forgot-password \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario@conaveg.com"}'
   ```

2. **Verificar Eventos en BD**:
   ```sql
   SELECT * FROM security_audit_logs 
   WHERE event_type = 'PASSWORD_RESET_REQUESTED' 
   AND email = 'usuario@conaveg.com'
   ORDER BY timestamp DESC LIMIT 1;
   ```

---

## 🔄 **TESTING SUITE 4: SISTEMA DE REFRESH TOKENS**

### **Test Case 4.1: Renovación Exitosa de Token**

#### **Objetivo**: Verificar renovación de JWT en ventana válida

#### **Procedimiento**:
1. **Obtener Token JWT** (via login):
   ```bash
   TOKEN=$(curl -s -X POST http://localhost:8080/conaveg/api/auth/login \
   -H "Content-Type: application/json" \
   -d '{"email":"usuario@conaveg.com","password":"password"}' | jq -r '.token')
   ```

2. **Esperar hasta ventana de refresh** (últimos 15 minutos antes de expirar)

3. **Solicitar Refresh**:
   ```bash
   curl -X POST http://localhost:8080/conaveg/api/auth/refresh \
   -H "Content-Type: application/json" \
   -d '{"token":"'$TOKEN'"}'
   ```

4. **Verificar Respuesta Exitosa**:
   ```json
   {
     "token": "nuevo_jwt_token",
     "tokenType": "Bearer",
     "expiresIn": 86400,
     "refreshedAt": "2025-06-24T19:30:00"
   }
   ```

### **Test Case 4.2: Rate Limiting en Refresh**

#### **Objetivo**: Verificar límites en renovación de tokens

#### **Procedimiento**:
1. **Realizar 12 refresh requests rápidamente**:
   ```bash
   for i in {1..12}; do
     echo "Refresh attempt $i"
     curl -X POST http://localhost:8080/conaveg/api/auth/refresh \
     -H "Content-Type: application/json" \
     -d '{"token":"'$TOKEN'"}'
   done
   ```

2. **Verificar Bloqueo**: Status `429` en intentos excesivos

---

## ⏰ **TESTING SUITE 5: TAREAS PROGRAMADAS**

### **Test Case 5.1: Limpieza de Tokens Expirados**

#### **Objetivo**: Verificar limpieza automática de tokens antiguos

#### **Preparación**:
1. **Insertar token expirado manualmente**:
   ```sql
   INSERT INTO password_reset_tokens (email, token, expires_at, created_at, used) 
   VALUES ('test@old.com', 'old_token_123', '2025-06-01 00:00:00', '2025-06-01 00:00:00', false);
   ```

#### **Procedimiento**:
1. **Forzar ejecución de tarea** (en desarrollo):
   ```bash
   # Esperar ejecución nocturna o triggear manualmente si hay endpoint
   ```

2. **Verificar Limpieza**:
   ```sql
   SELECT COUNT(*) FROM password_reset_tokens 
   WHERE created_at < NOW() - INTERVAL 7 DAY;
   ```

3. **Verificar Logs**:
   ```
   [SCHEDULED] Cleaning up expired password reset tokens...
   [SCHEDULED] Removed X expired tokens
   ```

### **Test Case 5.2: Limpieza de Intentos Antiguos**

#### **Objetivo**: Verificar limpieza de authentication_attempts

#### **Procedimiento**:
1. **Insertar intentos antiguos**:
   ```sql
   INSERT INTO authentication_attempts (email, ip_address, attempt_time, successful) 
   VALUES ('test@old.com', '192.168.1.1', '2025-05-01 00:00:00', false);
   ```

2. **Verificar Limpieza**:
   ```sql
   SELECT COUNT(*) FROM authentication_attempts 
   WHERE attempt_time < NOW() - INTERVAL 30 DAY;
   ```

---

## 🛠️ **HERRAMIENTAS Y UTILIDADES**

### **Scripts de Testing Automatizado**

#### **Script 1: Verificación de Endpoints**
```bash
#!/bin/bash
# test_endpoints.sh
echo "Testing all authentication endpoints..."

BASE_URL="http://localhost:8080/conaveg/api/auth"

# Test login
echo "1. Testing login..."
curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/login \
-H "Content-Type: application/json" \
-d '{"email":"test@test.com","password":"wrong"}'

echo "2. Testing forgot password..."
curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/forgot-password \
-H "Content-Type: application/json" \
-d '{"email":"test@test.com"}'

echo "Testing completed."
```

#### **Script 2: Verificación de Rate Limiting**
```bash
#!/bin/bash
# test_rate_limiting.sh
echo "Testing rate limiting..."

for i in {1..15}; do
  echo "Attempt $i"
  RESPONSE=$(curl -s -w "%{http_code}" -X POST http://localhost:8080/conaveg/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"rate@test.com","password":"wrong"}')
  
  if [[ $RESPONSE == *"429"* ]]; then
    echo "Rate limiting activated at attempt $i"
    break
  fi
done
```

### **Consultas SQL de Verificación**

#### **Verificar Estado de Rate Limiting**:
```sql
-- Intentos por IP en última hora
SELECT ip_address, COUNT(*) as attempts 
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 1 HOUR 
GROUP BY ip_address 
ORDER BY attempts DESC;

-- Intentos por email en última hora
SELECT email, COUNT(*) as attempts 
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 1 HOUR 
AND successful = false
GROUP BY email 
ORDER BY attempts DESC;
```

#### **Verificar Logs de Auditoría**:
```sql
-- Eventos recientes por severidad
SELECT event_type, severity, COUNT(*) as count
FROM security_audit_logs 
WHERE timestamp > NOW() - INTERVAL 1 HOUR
GROUP BY event_type, severity
ORDER BY count DESC;

-- Rate limiting events
SELECT * FROM security_audit_logs 
WHERE event_type = 'RATE_LIMIT_EXCEEDED'
ORDER BY timestamp DESC LIMIT 10;
```

---

## ✅ **CHECKLIST DE TESTING COMPLETO**

### **Pre-Testing**
- [ ] Servidor ejecutándose
- [ ] Base de datos conectada
- [ ] Logs accesibles
- [ ] Herramientas preparadas

### **Funcionalidades Core**
- [ ] Login exitoso
- [ ] Login fallido
- [ ] Refresh token exitoso
- [ ] Forgot password
- [ ] Reset password
- [ ] Validate token

### **Seguridad**
- [ ] Rate limiting por IP
- [ ] Rate limiting por email
- [ ] Auditoría de eventos
- [ ] Bloqueos temporales
- [ ] Recuperación automática

### **Mantenimiento**
- [ ] Limpieza de tokens
- [ ] Limpieza de intentos
- [ ] Limpieza de logs
- [ ] Verificación de logs de tareas

### **Edge Cases**
- [ ] Tokens expirados
- [ ] Emails inexistentes
- [ ] Contraseñas débiles
- [ ] Ataques de fuerza bruta
- [ ] Concurrencia de requests

---

## 📊 **REPORTE DE RESULTADOS**

### **Template de Reporte**
```
TESTING REPORT - FASE 3
Fecha: [FECHA]
Tester: [NOMBRE]
Entorno: [DESARROLLO/STAGING]

RESULTADOS:
✅ Passed: X tests
❌ Failed: Y tests
⚠️  Warnings: Z tests

DETALLES:
[Describir issues encontrados]

RECOMENDACIONES:
[Acciones sugeridas]
```

---

**📅 Fecha de Creación**: 24 de Junio de 2025  
**👨‍💻 Responsable**: Equipo de Desarrollo CONA  
**📋 Estado**: Documentación Completa  
**🎯 Uso**: Testing Manual de Fase 3
