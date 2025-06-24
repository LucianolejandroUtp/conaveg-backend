# 🛠️ GUÍA DE TROUBLESHOOTING - SISTEMA CONA

## 📋 **INFORMACIÓN DEL DOCUMENTO**

**Fecha de Creación**: 24 de Junio de 2025  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Fase**: Fase 3 - Funcionalidades Avanzadas de Autenticación  
**Responsable**: Equipo de Desarrollo & DevOps  
**Audiencia**: Desarrolladores, Administradores de Sistema, Soporte Técnico  

---

## 🎯 **OBJETIVO**

Esta guía proporciona procedimientos paso a paso para diagnosticar y resolver problemas comunes en el sistema CONA, con enfoque específico en las funcionalidades de seguridad avanzada implementadas en la Fase 3.

---

## 🗂️ **ÍNDICE DE PROBLEMAS**

| 🔢 | Problema | Severidad | Página |
|----|----------|-----------|--------|
| 1 | [Problemas de Autenticación](#1-problemas-de-autenticación) | 🔴 Alta | - |
| 2 | [Rate Limiting Malfuncionando](#2-rate-limiting-malfuncionando) | 🟠 Media | - |
| 3 | [Recuperación de Contraseña Fallando](#3-recuperación-de-contraseña-fallando) | 🟠 Media | - |
| 4 | [Problemas de Auditoría de Seguridad](#4-problemas-de-auditoría-de-seguridad) | 🟡 Baja | - |
| 5 | [Tareas Programadas No Ejecutándose](#5-tareas-programadas-no-ejecutándose) | 🟠 Media | - |
| 6 | [Problemas de Performance](#6-problemas-de-performance) | 🔴 Alta | - |
| 7 | [Errores de Base de Datos](#7-errores-de-base-de-datos) | 🔴 Alta | - |
| 8 | [Problemas de Configuración](#8-problemas-de-configuración) | 🟠 Media | - |

---

## 1. PROBLEMAS DE AUTENTICACIÓN

### **🚨 PROBLEMA 1.1: "Login Endpoint Devuelve 500 Internal Server Error"**

#### **Síntomas**:
- Status Code: `500 Internal Server Error`
- Response: `{"error": "Error interno del servidor"}`
- Logs muestran excepciones en `AuthController`

#### **Posibles Causas**:
1. Error en servicio de autenticación
2. Problema de conexión a base de datos
3. Configuración de seguridad incorrecta
4. Error en validación de DTOs

#### **Diagnóstico Paso a Paso**:

**STEP 1: Verificar Logs de Aplicación**
```bash
# Buscar errores recientes en logs
tail -n 100 /app/logs/spring.log | grep -E "(ERROR|Exception)"

# Buscar errores específicos de autenticación
grep -A 10 -B 5 "AuthController\|AuthenticationService" /app/logs/spring.log | tail -50
```

**STEP 2: Verificar Estado de Base de Datos**
```bash
# Probar conexión a BD
mysql -h localhost -u cona_user -p cona_db -e "SELECT 1;"

# Verificar tabla de usuarios
mysql -u cona_user -p cona_db -e "SELECT COUNT(*) FROM empleados;"
```

**STEP 3: Verificar Configuración**
```bash
# Verificar configuración de autenticación
cat /app/config/application.properties | grep -E "(jwt|auth|security)"

# Verificar variables de entorno
env | grep -E "(JWT|AUTH|DB)"
```

#### **Soluciones**:

**Si es problema de BD**:
```bash
# Reiniciar servicio de BD
sudo systemctl restart mariadb

# Verificar logs de BD
sudo tail -f /var/log/mysql/error.log
```

**Si es problema de JWT**:
```bash
# Verificar JWT Secret configurado
grep jwt.secret /app/config/application.properties

# Regenerar secret si es necesario
echo "jwt.secret=$(openssl rand -base64 64)" >> application.properties
```

**Si es problema de configuración de seguridad**:
```java
// Verificar SecurityConfig.java
// Asegurar que /api/auth/login esté permitido
.requestMatchers("/api/auth/**").permitAll()
```

### **🚨 PROBLEMA 1.2: "JWT Token Inválido o Expirado"**

#### **Síntomas**:
- Status Code: `401 Unauthorized`
- Response: `{"error": "Token inválido o expirado"}`
- Usuario no puede acceder a endpoints protegidos

#### **Diagnóstico**:

**STEP 1: Verificar Token**
```bash
# Decodificar JWT token (usa una herramienta online o jwt-cli)
echo "TOKEN_AQUI" | base64 -d

# Verificar fecha de expiración
node -e "console.log(new Date(JSON.parse(atob('PAYLOAD_PART')).exp * 1000))"
```

**STEP 2: Verificar Configuración JWT**
```bash
# Verificar tiempo de expiración configurado
grep jwt.expiration /app/config/application.properties

# Verificar secret usado
grep jwt.secret /app/config/application.properties
```

#### **Soluciones**:

**Token Expirado (Comportamiento Normal)**:
```bash
# Usar endpoint de refresh
curl -X POST http://localhost:8080/conaveg/api/auth/refresh \
-H "Content-Type: application/json" \
-d '{"token":"TOKEN_EXPIRADO"}'
```

**Secret Incorrecto**:
```bash
# Verificar que todos los servicios usen el mismo secret
# Reiniciar aplicación después de cambiar secret
sudo systemctl restart cona-app
```

### **🚨 PROBLEMA 1.3: "Rate Limiting Bloqueando Usuarios Legítimos"**

#### **Síntomas**:
- Status Code: `429 Too Many Requests`
- Usuarios legítimos no pueden hacer login
- Logs muestran rate limiting activo

#### **Diagnóstico**:

**STEP 1: Verificar Estado de Rate Limiting**
```sql
-- Verificar IPs bloqueadas
SELECT ip_address, COUNT(*) as attempts, MAX(attempt_time) as last_attempt
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 1 HOUR
AND successful = false
GROUP BY ip_address
ORDER BY attempts DESC;

-- Verificar emails bloqueados
SELECT email, COUNT(*) as attempts 
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 1 HOUR
AND successful = false
GROUP BY email
HAVING attempts >= 5;
```

**STEP 2: Verificar Configuración de Rate Limiting**
```bash
# Verificar límites configurados
grep -E "(rate.limit|max.attempts)" /app/config/application.properties

# Verificar logs de rate limiting
grep "RATE_LIMIT" /app/logs/spring.log | tail -20
```

#### **Soluciones**:

**Desbloquear IP Específica**:
```sql
-- Marcar intentos como exitosos para resetear contador
UPDATE authentication_attempts 
SET successful = true 
WHERE ip_address = 'IP_A_DESBLOQUEAR' 
AND attempt_time > NOW() - INTERVAL 1 HOUR;
```

**Ajustar Límites Temporalmente**:
```properties
# En application.properties - aumentar límites
auth.rate-limit.max-attempts-per-ip=20
auth.rate-limit.max-attempts-per-email=10
auth.rate-limit.time-window-minutes=60
```

**Reiniciar Rate Limiting**:
```bash
# Reiniciar aplicación para limpiar caché en memoria
sudo systemctl restart cona-app
```

---

## 2. RATE LIMITING MALFUNCIONANDO

### **🚨 PROBLEMA 2.1: "Rate Limiting No Está Bloqueando Ataques"**

#### **Síntomas**:
- Múltiples intentos fallidos no son bloqueados
- No se generan logs de rate limiting
- Base de datos muestra muchos intentos sin bloqueo

#### **Diagnóstico**:

**STEP 1: Verificar Filtro Activo**
```bash
# Verificar que RateLimitingFilter esté registrado
grep -A 5 -B 5 "RateLimitingFilter" /app/logs/spring.log

# Verificar orden de filtros
curl -X GET http://localhost:8080/conaveg/actuator/filters
```

**STEP 2: Verificar Lógica de Rate Limiting**
```java
// Verificar RateLimitingFilter.java
// Asegurar que el filtro esté interceptando requests correctamente
@Override
protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
    
    String ipAddress = getClientIpAddress(request);
    String endpoint = request.getRequestURI();
    
    if (isRateLimitExceeded(ipAddress, endpoint)) {
        // Código de bloqueo debe estar aquí
    }
}
```

#### **Soluciones**:

**Verificar Configuración del Filtro**:
```java
// En SecurityConfig.java
@Bean
public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilter() {
    FilterRegistrationBean<RateLimitingFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new RateLimitingFilter());
    registrationBean.addUrlPatterns("/api/auth/*");
    registrationBean.setOrder(1); // Asegurar que se ejecute temprano
    return registrationBean;
}
```

**Reiniciar Servicios**:
```bash
# Reiniciar aplicación
sudo systemctl restart cona-app

# Verificar que el filtro se inicialice
grep "RateLimitingFilter initialized" /app/logs/spring.log
```

### **🚨 PROBLEMA 2.2: "Rate Limiting Muy Agresivo"**

#### **Síntomas**:
- Usuarios legítimos bloqueados frecuentemente
- Límites alcanzados muy rápidamente
- Quejas de usuarios sobre accesibilidad

#### **Soluciones**:

**Ajustar Configuración**:
```properties
# Aumentar límites en application.properties
auth.rate-limit.max-attempts-per-ip=15
auth.rate-limit.max-attempts-per-email=8
auth.rate-limit.time-window-minutes=30
auth.rate-limit.block-duration-minutes=10
```

**Implementar Whitelist para IPs Confiables**:
```java
// En RateLimitingFilter.java
private static final Set<String> WHITELISTED_IPS = Set.of(
    "192.168.1.0/24",  // Red interna
    "10.0.0.0/8"       // VPN corporativa
);

private boolean isWhitelistedIP(String ipAddress) {
    return WHITELISTED_IPS.stream()
        .anyMatch(range -> isInRange(ipAddress, range));
}
```

---

## 3. RECUPERACIÓN DE CONTRASEÑA FALLANDO

### **🚨 PROBLEMA 3.1: "Endpoint forgot-password Devuelve Error"**

#### **Síntomas**:
- Status Code: `500` en `/api/auth/forgot-password`
- Emails no se envían
- Tokens no se generan en base de datos

#### **Diagnóstico**:

**STEP 1: Verificar Logs de Email**
```bash
# Buscar errores de SMTP
grep -i -A 5 -B 5 "smtp\|mail\|email" /app/logs/spring.log

# Verificar configuración de email
grep -E "(mail|smtp)" /app/config/application.properties
```

**STEP 2: Verificar Generación de Tokens**
```sql
-- Verificar si se están creando tokens
SELECT COUNT(*) FROM password_reset_tokens 
WHERE created_at > NOW() - INTERVAL 1 HOUR;

-- Verificar último token creado
SELECT * FROM password_reset_tokens 
ORDER BY created_at DESC LIMIT 1;
```

#### **Soluciones**:

**Si es problema de SMTP**:
```properties
# Configurar SMTP correctamente
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Si es problema de base de datos**:
```sql
-- Crear tabla si no existe
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used BOOLEAN DEFAULT FALSE,
    used_at TIMESTAMP NULL
);
```

### **🚨 PROBLEMA 3.2: "Tokens de Reset Expiran Muy Rápido"**

#### **Síntomas**:
- Usuarios reportan que tokens no funcionan
- Verificación muestra tokens expirados inmediatamente

#### **Diagnóstico**:
```bash
# Verificar configuración de expiración
grep "password.reset.token.expiration" /app/config/application.properties

# Verificar timezone del servidor
date
timedatectl status
```

#### **Soluciones**:
```properties
# Aumentar tiempo de expiración (en minutos)
password.reset.token.expiration=30

# Verificar configuración de timezone
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

---

## 4. PROBLEMAS DE AUDITORÍA DE SEGURIDAD

### **🚨 PROBLEMA 4.1: "Eventos No Se Están Registrando"**

#### **Síntomas**:
- Tabla `security_audit_logs` vacía o sin registros recientes
- No hay logs de eventos de seguridad
- Auditoría no funciona

#### **Diagnóstico**:

**STEP 1: Verificar Filtro de Auditoría**
```bash
# Verificar que SecurityAuditFilter esté activo
grep "SecurityAuditFilter" /app/logs/spring.log

# Verificar configuración del filtro
grep -A 10 -B 5 "SecurityAuditFilter" /app/config/*.java
```

**STEP 2: Verificar Base de Datos**
```sql
-- Verificar estructura de tabla
DESCRIBE security_audit_logs;

-- Verificar permisos
SHOW GRANTS FOR 'cona_user'@'localhost';
```

#### **Soluciones**:

**Crear Tabla de Auditoría**:
```sql
CREATE TABLE IF NOT EXISTS security_audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    email VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    endpoint VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    severity VARCHAR(20) DEFAULT 'LOW',
    success BOOLEAN,
    details TEXT
);
```

**Verificar Registro del Filtro**:
```java
// En SecurityConfig.java o FilterConfig.java
@Bean
public FilterRegistrationBean<SecurityAuditFilter> securityAuditFilter() {
    FilterRegistrationBean<SecurityAuditFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(new SecurityAuditFilter());
    registrationBean.addUrlPatterns("/api/*");
    registrationBean.setOrder(2);
    return registrationBean;
}
```

---

## 5. TAREAS PROGRAMADAS NO EJECUTÁNDOSE

### **🚨 PROBLEMA 5.1: "Cleanup Tasks No Se Ejecutan"**

#### **Síntomas**:
- Tokens expirados acumulándose en BD
- Logs antiguos no se eliminan
- Tabla `authentication_attempts` creciendo indefinidamente

#### **Diagnóstico**:

**STEP 1: Verificar Scheduler**
```bash
# Buscar logs de tareas programadas
grep -i -A 5 -B 5 "scheduled\|cleanup\|cron" /app/logs/spring.log

# Verificar configuración de scheduling
grep -E "@EnableScheduling|@Scheduled" /app/src/main/java/com/conaveg/cona/config/*.java
```

**STEP 2: Verificar Estado de las Tareas**
```sql
-- Verificar tokens antiguos
SELECT COUNT(*) FROM password_reset_tokens 
WHERE created_at < NOW() - INTERVAL 7 DAY;

-- Verificar intentos antiguos
SELECT COUNT(*) FROM authentication_attempts 
WHERE attempt_time < NOW() - INTERVAL 30 DAY;

-- Verificar logs antiguos
SELECT COUNT(*) FROM security_audit_logs 
WHERE timestamp < NOW() - INTERVAL 30 DAY;
```

#### **Soluciones**:

**Habilitar Scheduling**:
```java
// En ConaApplication.java o SchedulingConfig.java
@SpringBootApplication
@EnableScheduling  // Asegurar que está presente
public class ConaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConaApplication.class, args);
    }
}
```

**Verificar Configuración de Tareas**:
```java
// En ScheduledTasks.java
@Component
public class ScheduledTasks {
    
    @Scheduled(cron = "0 0 2 * * ?") // 2 AM diariamente
    public void cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired password reset tokens...");
        // Lógica de limpieza
    }
}
```

**Ejecutar Limpieza Manual**:
```sql
-- Limpiar tokens expirados manualmente
DELETE FROM password_reset_tokens 
WHERE expires_at < NOW() OR created_at < NOW() - INTERVAL 7 DAY;

-- Limpiar intentos antiguos
DELETE FROM authentication_attempts 
WHERE attempt_time < NOW() - INTERVAL 30 DAY;

-- Limpiar logs antiguos
DELETE FROM security_audit_logs 
WHERE timestamp < NOW() - INTERVAL 30 DAY;
```

---

## 6. PROBLEMAS DE PERFORMANCE

### **🚨 PROBLEMA 6.1: "API Response Times Muy Lentos"**

#### **Síntomas**:
- Tiempos de respuesta > 3 segundos
- Timeouts frecuentes
- Usuarios reportan lentitud

#### **Diagnóstico**:

**STEP 1: Identificar Endpoints Lentos**
```bash
# Analizar logs de response time
grep -E "took|duration|ms" /app/logs/spring.log | tail -50

# Verificar uso de CPU y memoria
top -p $(pgrep java)
```

**STEP 2: Analizar Queries de Base de Datos**
```sql
-- Verificar queries lentas
SHOW PROCESSLIST;

-- Verificar queries que toman tiempo
SELECT * FROM information_schema.processlist 
WHERE command != 'Sleep' AND time > 1;

-- Verificar índices faltantes
SHOW INDEX FROM authentication_attempts;
SHOW INDEX FROM security_audit_logs;
```

#### **Soluciones**:

**Optimizar Índices de Base de Datos**:
```sql
-- Crear índices para mejorar performance
CREATE INDEX idx_auth_attempts_ip_time ON authentication_attempts(ip_address, attempt_time);
CREATE INDEX idx_auth_attempts_email_time ON authentication_attempts(email, attempt_time);
CREATE INDEX idx_audit_logs_timestamp ON security_audit_logs(timestamp);
CREATE INDEX idx_audit_logs_event_type ON security_audit_logs(event_type);
CREATE INDEX idx_password_reset_email ON password_reset_tokens(email);
CREATE INDEX idx_password_reset_token ON password_reset_tokens(token);
```

**Optimizar Pool de Conexiones**:
```properties
# En application.properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
```

**Implementar Cache**:
```java
// Agregar cache para rate limiting
@Cacheable("rateLimitCache")
public boolean isRateLimitExceeded(String ip, String endpoint) {
    // Implementación con cache
}
```

### **🚨 PROBLEMA 6.2: "Memory Leaks o High Memory Usage"**

#### **Diagnóstico**:
```bash
# Verificar uso de memoria JVM
jstat -gc $(pgrep java)

# Generar heap dump para análisis
jmap -dump:format=b,file=heapdump.hprof $(pgrep java)

# Verificar garbage collection
jstat -gcutil $(pgrep java) 5s
```

#### **Soluciones**:
```bash
# Optimizar configuración JVM
export JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Reiniciar aplicación con nueva configuración
sudo systemctl restart cona-app
```

---

## 7. ERRORES DE BASE DE DATOS

### **🚨 PROBLEMA 7.1: "Connection Pool Exhausted"**

#### **Síntomas**:
- Error: `Unable to acquire JDBC Connection`
- API endpoints returning 500 errors
- Database connection timeouts

#### **Diagnóstico**:
```bash
# Verificar conexiones activas
mysql -e "SHOW PROCESSLIST;" | wc -l

# Verificar configuración de pool
grep -E "(datasource|pool)" /app/config/application.properties
```

#### **Soluciones**:
```properties
# Aumentar pool de conexiones
spring.datasource.hikari.maximum-pool-size=25
spring.datasource.hikari.connection-timeout=30000

# Configurar detección de conexiones perdidas
spring.datasource.hikari.leak-detection-threshold=60000
```

### **🚨 PROBLEMA 7.2: "Deadlocks en Base de Datos"**

#### **Diagnóstico**:
```sql
-- Verificar deadlocks recientes
SHOW ENGINE INNODB STATUS;

-- Verificar locks activos
SELECT * FROM information_schema.INNODB_LOCKS;
```

#### **Soluciones**:
```sql
-- Optimizar queries para evitar deadlocks
-- Asegurar que las transacciones sean cortas
-- Usar consistent locking order
```

---

## 8. PROBLEMAS DE CONFIGURACIÓN

### **🚨 PROBLEMA 8.1: "Variables de Entorno No Se Cargan"**

#### **Diagnóstico**:
```bash
# Verificar variables de entorno
env | grep -i cona

# Verificar archivo de configuración
cat /app/config/application.properties | grep -v "^#"
```

#### **Soluciones**:
```bash
# Verificar archivo .env o variables de sistema
sudo systemctl edit cona-app

# Agregar variables necesarias
[Service]
Environment="JWT_SECRET=your-secret-here"
Environment="DB_PASSWORD=your-password"
```

---

## 🔧 **HERRAMIENTAS DE DIAGNÓSTICO**

### **Script de Diagnóstico Rápido**
```bash
#!/bin/bash
# quick_diagnosis.sh

echo "🔍 DIAGNÓSTICO RÁPIDO DEL SISTEMA CONA"
echo "======================================"

echo "1. Estado de la aplicación:"
curl -s http://localhost:8080/conaveg/actuator/health || echo "❌ Aplicación no responde"

echo -e "\n2. Estado de base de datos:"
mysql -e "SELECT 1;" 2>/dev/null && echo "✅ BD conectada" || echo "❌ BD no conectada"

echo -e "\n3. Últimos errores en logs:"
tail -n 20 /app/logs/spring.log | grep -E "(ERROR|Exception)" | tail -5

echo -e "\n4. Uso de memoria:"
free -h

echo -e "\n5. Conexiones de base de datos activas:"
mysql -e "SHOW PROCESSLIST;" | wc -l

echo -e "\n6. Últimos eventos de seguridad:"
mysql -e "SELECT event_type, COUNT(*) FROM security_audit_logs WHERE timestamp > NOW() - INTERVAL 1 HOUR GROUP BY event_type;" 2>/dev/null

echo -e "\n✅ Diagnóstico completado"
```

### **Health Check Completo**
```bash
#!/bin/bash
# health_check.sh

check_endpoint() {
    local endpoint=$1
    local expected_status=$2
    
    local actual_status=$(curl -s -o /dev/null -w "%{http_code}" "$endpoint")
    
    if [ "$actual_status" -eq "$expected_status" ]; then
        echo "✅ $endpoint - Status: $actual_status"
    else
        echo "❌ $endpoint - Expected: $expected_status, Got: $actual_status"
    fi
}

echo "🏥 HEALTH CHECK COMPLETO"
echo "======================="

BASE_URL="http://localhost:8080/conaveg"

# Verificar endpoints críticos
check_endpoint "$BASE_URL/actuator/health" 200
check_endpoint "$BASE_URL/api/auth/login" 400  # Bad request por falta de datos
check_endpoint "$BASE_URL/api/auth/forgot-password" 400  # Bad request por falta de datos

echo -e "\nVerificando servicios internos..."

# Verificar rate limiting
echo "🚦 Testing rate limiting..."
for i in {1..12}; do
    status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"email":"test@test.com","password":"wrong"}')
    
    if [ "$status" -eq 429 ]; then
        echo "✅ Rate limiting activo en intento $i"
        break
    fi
done

echo -e "\n🔚 Health check completado"
```

---

## 📞 **CONTACTOS DE SOPORTE**

### **Escalación de Problemas**:

**NIVEL 1 - Soporte Técnico** (0-15 min):
- 📧 Email: soporte@conaveg.com
- 💬 Slack: #soporte-tecnico
- ⏰ Horario: Lunes a Viernes 8:00-18:00

**NIVEL 2 - Desarrolladores** (15-60 min):
- 📧 Email: desarrollo@conaveg.com  
- 💬 Slack: #desarrollo-urgente
- ⏰ Horario: 24/7 para problemas críticos

**NIVEL 3 - Arquitecto de Sistemas** (1-4 horas):
- 📧 Email: arquitectura@conaveg.com
- 📱 Teléfono: +XX-XXXX-XXXX (emergencias)
- ⏰ Horario: 24/7 para problemas críticos

**NIVEL 4 - Gerencia Técnica** (4+ horas):
- 📧 Email: gerencia.tecnica@conaveg.com
- ⚠️ Solo para problemas que afecten producción

---

## 📚 **DOCUMENTACIÓN RELACIONADA**

- 📖 [Manual de Testing](./Manual_Testing_Guide.md)
- 📊 [Manual de Monitoreo](./Monitoring_Manual.md)
- 🔧 [Scripts de Verificación](./Verification_Scripts.md)
- 📋 [Análisis de Completitud Fase 3](../Analisis_Completitud_Fase3.md)

---

**📅 Fecha de Creación**: 24 de Junio de 2025  
**👨‍💻 Responsable**: Equipo de Desarrollo CONA  
**🔄 Próxima Revisión**: 24 de Julio de 2025  
**📋 Estado**: Documentación Activa
