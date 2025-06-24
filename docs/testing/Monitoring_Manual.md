# 📊 MANUAL DE MONITOREO - SISTEMA CONA

## 📋 **INFORMACIÓN DEL DOCUMENTO**

**Fecha de Creación**: 24 de Junio de 2025  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Fase**: Fase 3 - Funcionalidades Avanzadas de Autenticación  
**Responsable**: Equipo DevOps/SRE  
**Audiencia**: Administradores de Sistema, DevOps, Desarrolladores  

---

## 🎯 **OBJETIVO**

Este manual proporciona procedimientos, métricas y alertas para monitorear efectivamente el sistema CONA, con enfoque especial en las funcionalidades de seguridad implementadas en la Fase 3: rate limiting, auditoría de seguridad, autenticación y tareas programadas.

---

## 🏗️ **ARQUITECTURA DE MONITOREO**

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │────│   Prometheus    │────│    Grafana      │
│     Logs        │    │   Metrics       │    │   Dashboard     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         │              │   AlertManager  │              │
         │              │   (Alerts)      │              │
         │              └─────────────────┘              │
         │                       │                       │
         ▼              ┌─────────────────┐              ▼
┌─────────────────┐    │    Slack/Email   │    ┌─────────────────┐
│   ELK Stack     │    │   Notifications  │    │   Mobile App    │
│ (Log Analysis)  │    └─────────────────┘    │   Monitoring    │
└─────────────────┘                           └─────────────────┘
```

---

## 🎛️ **MÉTRICAS CLAVE A MONITOREAR**

### **1. MÉTRICAS DE AUTENTICACIÓN**

#### **Login Success Rate**
```yaml
Métrica: authentication_success_rate
Descripción: Porcentaje de logins exitosos vs totales
Fórmula: (successful_logins / total_login_attempts) * 100
Umbral Normal: > 85%
Umbral Crítico: < 70%
Frecuencia: Tiempo real
```

#### **Authentication Response Time**
```yaml
Métrica: auth_response_time_ms
Descripción: Tiempo de respuesta promedio del endpoint de login
Umbral Normal: < 500ms
Umbral Warning: > 1000ms
Umbral Crítico: > 2000ms
Frecuencia: Cada request
```

#### **Failed Login Attempts**
```yaml
Métrica: failed_login_attempts_total
Descripción: Contador de intentos de login fallidos
Umbral Warning: > 50 intentos/hora
Umbral Crítico: > 100 intentos/hora
Agregación: Por IP, Por Email, Global
```

### **2. MÉTRICAS DE RATE LIMITING**

#### **Rate Limit Violations**
```yaml
Métrica: rate_limit_violations_total
Descripción: Número de requests bloqueados por rate limiting
Labels: endpoint, ip_address, time_window
Umbral Warning: > 10 violations/hora
Umbral Crítico: > 50 violations/hora
```

#### **IP Blocks Active**
```yaml
Métrica: active_ip_blocks_count
Descripción: Número actual de IPs bloqueadas
Umbral Normal: < 10
Umbral Warning: > 50
Umbral Crítico: > 100
```

#### **Rate Limit Window Utilization**
```yaml
Métrica: rate_limit_window_utilization_percent
Descripción: Porcentaje de utilización de la ventana de rate limiting
Fórmula: (current_requests / max_requests_per_window) * 100
Umbral Warning: > 80%
Umbral Crítico: > 95%
```

### **3. MÉTRICAS DE SEGURIDAD**

#### **Security Audit Events**
```yaml
Métrica: security_audit_events_total
Descripción: Contador de eventos de auditoría por tipo y severidad
Labels: event_type, severity, success
Tipos Monitoreados:
  - LOGIN_SUCCESS/FAILED
  - PASSWORD_RESET_REQUESTED
  - PASSWORD_CHANGED
  - RATE_LIMIT_EXCEEDED
  - SUSPICIOUS_ACTIVITY
```

#### **Password Reset Requests**
```yaml
Métrica: password_reset_requests_rate
Descripción: Tasa de solicitudes de recuperación de contraseña
Umbral Normal: < 10 requests/hora
Umbral Warning: > 20 requests/hora
Umbral Crítico: > 50 requests/hora
```

#### **Token Validation Failures**
```yaml
Métrica: token_validation_failures_total
Descripción: Número de tokens inválidos/expirados
Umbral Warning: > 5% de total de validaciones
Umbral Crítico: > 15% de total de validaciones
```

### **4. MÉTRICAS DE SISTEMA**

#### **Database Connection Pool**
```yaml
Métrica: db_connection_pool_active
Descripción: Conexiones activas del pool de BD
Umbral Normal: < 80% del pool
Umbral Warning: > 90% del pool
Umbral Crítico: > 95% del pool
```

#### **Memory Usage**
```yaml
Métrica: jvm_memory_used_bytes
Descripción: Uso de memoria JVM
Umbral Warning: > 85% del heap
Umbral Crítico: > 95% del heap
```

#### **API Response Times**
```yaml
Métrica: http_request_duration_seconds
Descripción: Tiempos de respuesta por endpoint
Labels: method, endpoint, status
Percentiles: p50, p95, p99
Umbral Warning: p95 > 1s
Umbral Crítico: p99 > 3s
```

---

## 📈 **CONFIGURACIÓN DE ALERTAS**

### **NIVEL 1: ALERTAS INFORMATIVAS (LOW)**

#### **High Authentication Activity**
```yaml
Alert: HighAuthenticationActivity
Expression: rate(authentication_attempts_total[5m]) > 20
For: 2m
Description: "Actividad de autenticación elevada detectada"
Severity: info
Notification: Slack channel #monitoring
```

#### **Password Reset Spike**
```yaml
Alert: PasswordResetSpike
Expression: rate(password_reset_requests_total[15m]) > 0.1
For: 5m
Description: "Incremento en solicitudes de recuperación de contraseña"
Severity: info
```

### **NIVEL 2: ALERTAS DE WARNING (MEDIUM)**

#### **Rate Limiting Active**
```yaml
Alert: RateLimitingActive
Expression: rate_limit_violations_total > 10
For: 1m
Description: "Rate limiting está bloqueando múltiples requests"
Severity: warning
Notification: Slack + Email
Actions:
  - Revisar logs de rate limiting
  - Verificar patrones de tráfico
  - Considerar ajustar límites
```

#### **Authentication Failure Rate High**
```yaml
Alert: AuthFailureRateHigh
Expression: |
  (
    rate(authentication_failures_total[5m]) / 
    rate(authentication_attempts_total[5m])
  ) > 0.3
For: 3m
Description: "Tasa de fallos de autenticación > 30%"
Severity: warning
```

#### **API Response Time Degraded**
```yaml
Alert: APIResponseTimeDegraded
Expression: |
  histogram_quantile(0.95, 
    rate(http_request_duration_seconds_bucket[5m])
  ) > 1
For: 3m
Description: "P95 de tiempos de respuesta > 1s"
Severity: warning
```

### **NIVEL 3: ALERTAS CRÍTICAS (HIGH)**

#### **Security Breach Suspected**
```yaml
Alert: SecurityBreachSuspected
Expression: |
  (
    rate(failed_login_attempts_total[1m]) > 5
  ) and (
    rate(rate_limit_violations_total[1m]) > 10
  )
For: 30s
Description: "Posible ataque de fuerza bruta detectado"
Severity: critical
Notification: Phone call + Email + Slack
Immediate Actions:
  - Revisar logs de auditoría inmediatamente
  - Verificar IPs de origen
  - Considerar bloqueo temporal adicional
  - Notificar al equipo de seguridad
```

#### **Database Connection Pool Exhausted**
```yaml
Alert: DatabaseConnectionPoolExhausted
Expression: db_connection_pool_active / db_connection_pool_max > 0.95
For: 30s
Description: "Pool de conexiones DB > 95%"
Severity: critical
```

#### **Application Down**
```yaml
Alert: ApplicationDown
Expression: up{job="cona-app"} == 0
For: 30s
Description: "Aplicación CONA no responde"
Severity: critical
Notification: Phone call + SMS + Email
```

### **NIVEL 4: ALERTAS DE EMERGENCIA (CRITICAL)**

#### **Mass Authentication Failures**
```yaml
Alert: MassAuthenticationFailures
Expression: |
  rate(authentication_failures_total[1m]) > 50
For: 15s
Description: "Ataque masivo detectado - Múltiples fallos de autenticación"
Severity: emergency
Notification: All channels + Emergency contacts
Immediate Actions:
  - Activar modo de emergencia
  - Bloquear tráfico sospechoso
  - Notificar CSIRT
  - Iniciar procedimiento de incidentes
```

---

## 🖥️ **DASHBOARDS DE MONITOREO**

### **Dashboard Principal: CONA Security Overview**

#### **Panel 1: Authentication Metrics**
```yaml
Queries:
  - Login Success Rate (Last 24h): 
    sum(rate(login_success_total[1h])) / sum(rate(login_attempts_total[1h])) * 100
  
  - Failed Logins by Hour:
    sum(rate(login_failures_total[1h])) by (hour)
  
  - Top Failed Login IPs:
    topk(10, sum(rate(login_failures_total[1h])) by (ip_address))

Visualizations:
  - Gauge: Success rate
  - Time series: Failed logins trend
  - Table: Top offending IPs
```

#### **Panel 2: Rate Limiting Status**
```yaml
Queries:
  - Active IP Blocks:
    sum(active_ip_blocks_total)
  
  - Rate Limit Violations (Last 1h):
    sum(rate(rate_limit_violations_total[1h]))
  
  - Rate Limit Efficiency:
    sum(rate(rate_limit_blocks_total[1h])) / sum(rate(suspicious_requests_total[1h])) * 100

Visualizations:
  - Single stat: Active blocks
  - Heat map: Violations by endpoint
  - Bar chart: Efficiency by time
```

#### **Panel 3: Security Audit Trail**
```yaml
Queries:
  - Security Events by Type:
    sum(rate(security_audit_events_total[1h])) by (event_type)
  
  - High Severity Events:
    sum(rate(security_audit_events_total{severity="HIGH"}[1h]))
  
  - Security Events Timeline:
    security_audit_events_total

Visualizations:
  - Pie chart: Events by type
  - Time series: High severity events
  - Log panel: Recent security events
```

### **Dashboard Secundario: System Performance**

#### **Panel 1: Application Health**
```yaml
Queries:
  - Response Time Percentiles:
    histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m]))
  
  - Throughput:
    sum(rate(http_requests_total[1m]))
  
  - Error Rate:
    sum(rate(http_requests_total{status=~"5.."}[1m])) / sum(rate(http_requests_total[1m])) * 100
```

#### **Panel 2: Database Metrics**
```yaml
Queries:
  - Connection Pool Usage:
    db_connection_pool_active / db_connection_pool_max * 100
  
  - Query Response Time:
    histogram_quantile(0.95, rate(db_query_duration_seconds_bucket[5m]))
  
  - Active Transactions:
    db_active_transactions_total
```

---

## 🔍 **PROCEDIMIENTOS DE MONITOREO**

### **Monitoreo Diario (Automatizado)**

#### **09:00 AM - Reporte Matutino**
```bash
#!/bin/bash
# daily_security_report.sh

echo "🌅 REPORTE MATUTINO DE SEGURIDAD - $(date)"
echo "==============================================="

# Verificar eventos de la noche
echo "📊 Eventos de Seguridad (Últimas 12h):"
curl -s "http://prometheus:9090/api/v1/query?query=sum(increase(security_audit_events_total[12h])) by (event_type)"

echo "🚫 Bloqueos por Rate Limiting:"
curl -s "http://prometheus:9090/api/v1/query?query=sum(increase(rate_limit_violations_total[12h]))"

echo "🔐 Intentos de Login Fallidos:"
curl -s "http://prometheus:9090/api/v1/query?query=sum(increase(login_failures_total[12h]))"

echo "📧 Solicitudes de Recuperación:"
curl -s "http://prometheus:9090/api/v1/query?query=sum(increase(password_reset_requests_total[12h]))"

# Generar reporte y enviar por email
```

### **Monitoreo Semanal (Manual)**

#### **Lunes 10:00 AM - Revisión Semanal**
1. **Revisar Tendencias de Seguridad**:
   - Análisis de patrones de autenticación
   - Identificación de IPs problemáticas recurrentes
   - Evaluación de efectividad de rate limiting

2. **Verificar Salud de Sistema**:
   - Rendimiento de base de datos
   - Uso de recursos del sistema
   - Tiempo de respuesta de APIs

3. **Auditoría de Configuración**:
   - Verificar umbrales de alertas
   - Revisar logs de tareas programadas
   - Validar copias de seguridad de configuración

### **Monitoreo de Incidentes (Reactivo)**

#### **Procedimiento de Respuesta a Alertas**

**STEP 1: Verificación Inicial (< 2 min)**
```bash
# 1. Verificar estado general de la aplicación
curl -s http://localhost:8080/conaveg/actuator/health

# 2. Verificar últimos logs
tail -n 50 /app/logs/spring.log | grep -E "(ERROR|WARN|SECURITY)"

# 3. Verificar métricas básicas
curl -s http://localhost:8080/conaveg/actuator/metrics/system.cpu.usage
```

**STEP 2: Análisis Detallado (< 5 min)**
```sql
-- Verificar actividad reciente sospechosa
SELECT event_type, COUNT(*) as count, MAX(timestamp) as last_event
FROM security_audit_logs 
WHERE timestamp > NOW() - INTERVAL 15 MINUTE
GROUP BY event_type
ORDER BY count DESC;

-- Verificar IPs problemáticas
SELECT ip_address, COUNT(*) as attempts
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 15 MINUTE
AND successful = false
GROUP BY ip_address
HAVING attempts > 5
ORDER BY attempts DESC;
```

**STEP 3: Acciones Correctivas (< 10 min)**
- Aplicar bloqueos temporales adicionales si es necesario
- Escalar a administrador de sistemas si el problema persiste
- Documentar el incidente para análisis posterior

---

## 📋 **QUERIES DE MONITOREO ÚTILES**

### **Prometheus Queries**

#### **Autenticación**
```promql
# Tasa de éxito de login (última hora)
sum(rate(login_success_total[1h])) / sum(rate(login_attempts_total[1h])) * 100

# Top 10 IPs con más fallos de login
topk(10, sum(rate(login_failures_total[1h])) by (ip_address))

# Tiempo promedio de respuesta de autenticación
avg(rate(auth_response_time_sum[5m])) / avg(rate(auth_response_time_count[5m]))
```

#### **Rate Limiting**
```promql
# Número de violaciones de rate limit por endpoint
sum(rate(rate_limit_violations_total[5m])) by (endpoint)

# Porcentaje de requests bloqueados
sum(rate(rate_limit_blocks_total[5m])) / sum(rate(http_requests_total[5m])) * 100

# IPs actualmente bloqueadas
count(rate_limit_blocked_ips)
```

#### **Seguridad**
```promql
# Eventos de alta severidad (última hora)
sum(rate(security_audit_events_total{severity="HIGH"}[1h]))

# Distribución de eventos de seguridad por tipo
sum(rate(security_audit_events_total[5m])) by (event_type)

# Intentos de acceso después de bloqueo
sum(rate(access_after_block_total[5m]))
```

### **SQL Queries para Análisis**

#### **Análisis de Patrones de Ataque**
```sql
-- Detectar posibles ataques coordinados
SELECT 
    DATE(attempt_time) as date,
    HOUR(attempt_time) as hour,
    COUNT(DISTINCT ip_address) as unique_ips,
    COUNT(*) as total_attempts,
    COUNT(*) / COUNT(DISTINCT ip_address) as avg_attempts_per_ip
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 7 DAY
AND successful = false
GROUP BY DATE(attempt_time), HOUR(attempt_time)
HAVING avg_attempts_per_ip > 10
ORDER BY date DESC, hour DESC;
```

#### **Análisis de Efectividad de Rate Limiting**
```sql
-- Verificar cuántos ataques fueron bloqueados
SELECT 
    ip_address,
    COUNT(*) as total_attempts,
    SUM(CASE WHEN blocked_by_rate_limit = true THEN 1 ELSE 0 END) as blocked_attempts,
    (SUM(CASE WHEN blocked_by_rate_limit = true THEN 1 ELSE 0 END) / COUNT(*)) * 100 as block_percentage
FROM authentication_attempts 
WHERE attempt_time > NOW() - INTERVAL 24 HOUR
GROUP BY ip_address
HAVING total_attempts > 5
ORDER BY total_attempts DESC;
```

---

## 🚨 **BOOK DE RUNBOOKS**

### **Runbook 1: Ataque de Fuerza Bruta Detectado**

**Síntomas**:
- Alertas de `SecurityBreachSuspected`
- Incremento masivo en fallos de autenticación
- Rate limiting activándose frecuentemente

**Diagnosis**:
```bash
# 1. Verificar IPs atacantes
kubectl exec -it cona-app -- grep "AUTHENTICATION_FAILED" /app/logs/audit.log | tail -100

# 2. Verificar patrones
mysql -e "SELECT ip_address, COUNT(*) FROM authentication_attempts WHERE attempt_time > NOW() - INTERVAL 1 HOUR GROUP BY ip_address ORDER BY COUNT(*) DESC LIMIT 10;"
```

**Acciones**:
1. Bloquear IPs atacantes en firewall
2. Reducir temporalmente umbrales de rate limiting
3. Monitorear durante 30 minutos
4. Generar reporte post-incidente

### **Runbook 2: Alto Uso de CPU/Memoria**

**Síntomas**:
- Alertas de rendimiento
- Tiempos de respuesta elevados
- Timeouts en base de datos

**Diagnosis**:
```bash
# 1. Verificar uso de recursos
kubectl top pods
htop

# 2. Verificar queries lentas
mysql -e "SHOW PROCESSLIST;"

# 3. Verificar garbage collection
jstat -gc $(pgrep java)
```

**Acciones**:
1. Identificar queries problemáticas
2. Considerar escalado horizontal
3. Optimizar consultas si es necesario
4. Verificar memory leaks

### **Runbook 3: Falla en Tareas Programadas**

**Síntomas**:
- Acumulación de tokens expirados
- Logs de error en scheduled tasks
- Crecimiento anormal de tablas de auditoría

**Diagnosis**:
```bash
# 1. Verificar logs de tareas
grep -i "scheduled" /app/logs/spring.log | tail -50

# 2. Verificar estado de BD
mysql -e "SELECT COUNT(*) FROM password_reset_tokens WHERE expires_at < NOW();"
mysql -e "SELECT COUNT(*) FROM security_audit_logs WHERE timestamp < NOW() - INTERVAL 30 DAY;"
```

**Acciones**:
1. Ejecutar limpieza manual si es necesario
2. Verificar configuración de cron
3. Reiniciar scheduler si está bloqueado
4. Monitorear próximas ejecuciones

---

## 📚 **DOCUMENTACIÓN ADICIONAL**

### **Enlaces de Referencia**:
- 📖 [Guía de Troubleshooting](./Troubleshooting_Guide.md)
- 🧪 [Manual de Testing](./Manual_Testing_Guide.md)  
- 🔧 [Scripts de Verificación](./Verification_Scripts.md)
- 📊 [Métricas y SLAs](../PERFORMANCE_METRICS.md)

### **Contactos de Emergencia**:
```
🔴 NIVEL 1 - DevOps Team
   - Slack: #devops-alerts
   - Email: devops@conaveg.com

🟠 NIVEL 2 - Tech Lead
   - Phone: +XX-XXXX-XXXX
   - Email: techlead@conaveg.com

🔴 NIVEL 3 - Security Team
   - Phone: +XX-XXXX-XXXX
   - Email: security@conaveg.com
```

---

**📅 Fecha de Creación**: 24 de Junio de 2025  
**👨‍💻 Responsable**: Equipo DevOps CONA  
**🔄 Próxima Revisión**: 24 de Julio de 2025  
**📋 Estado**: Documentación Activa
