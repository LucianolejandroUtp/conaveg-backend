# 🔧 SCRIPTS DE VERIFICACIÓN AUTOMATIZADA - SISTEMA CONA

## 📋 **INFORMACIÓN DEL DOCUMENTO**

**Fecha de Creación**: 24 de Junio de 2025  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Fase**: Fase 3 - Funcionalidades Avanzadas de Autenticación  
**Responsable**: Equipo DevOps & QA  
**Audiencia**: Administradores de Sistema, DevOps, Desarrolladores, QA  

---

## 🎯 **OBJETIVO**

Proporcionar scripts automatizados para verificar el correcto funcionamiento de todas las funcionalidades implementadas en la Fase 3, incluyendo autenticación, rate limiting, auditoría de seguridad y base de datos.

---

## 📁 **ESTRUCTURA DE ARCHIVOS**

```
docs/testing/scripts/
├── master_verification.sh              # 🎯 Script principal (ejecuta todos)
├── authentication_verification.sh      # 🔐 Verificación de autenticación
├── rate_limiting_verification.sh       # 🚦 Verificación de rate limiting
├── database_verification.sh            # 🗄️ Verificación de base de datos
├── utils/
│   └── common_functions.sh             # 🛠️ Funciones utilitarias
├── reports/                            # 📊 Reportes generados
└── logs/                              # 📝 Logs de ejecución
```

---

## 🚀 **EJECUCIÓN RÁPIDA**

### **Verificación Completa**
```bash
# Ejecutar todos los tests
./master_verification.sh

# Ver resultados
cat /tmp/cona_verification_*.log
```

### **Verificaciones Específicas**
```bash
# Solo autenticación
./authentication_verification.sh

# Solo rate limiting
./rate_limiting_verification.sh

# Solo base de datos
./database_verification.sh
```

---

## 📋 **SCRIPTS DISPONIBLES**

### **1. MASTER VERIFICATION** (`master_verification.sh`)

**Propósito**: Script principal que ejecuta todas las verificaciones y genera reporte consolidado.

**Funcionalidades**:
- ✅ Health check general del sistema
- ✅ Verificación de endpoints de autenticación
- ✅ Testing de rate limiting
- ✅ Verificación de base de datos
- ✅ Verificación de configuración
- ✅ Testing de security headers
- ✅ Generación de reporte HTML

**Uso**:
```bash
chmod +x master_verification.sh
./master_verification.sh
```

**Salida**:
- Log detallado: `/tmp/cona_verification_YYYYMMDD_HHMMSS.log`
- Reporte HTML: `/tmp/cona_verification_report_YYYYMMDD_HHMMSS.html`

**Ejemplo de Output**:
```
🚀 Iniciando verificación completa del sistema CONA...
Log file: /tmp/cona_verification_20250624_193000.log
Report file: /tmp/cona_verification_report_20250624_193000.html

🔍 Verificando prerequisitos...
✅ PASS - Prerequisites: Aplicación accesible en http://localhost:8080/conaveg

🏥 Ejecutando Health Check General...
✅ PASS - General Health: Actuator health endpoint responde UP
✅ PASS - Response Time: Health endpoint responde en 245ms

🔐 Verificando endpoints de autenticación...
✅ PASS - Login Endpoint: Endpoint /api/auth/login disponible (400 Bad Request esperado)
✅ PASS - Forgot Password Endpoint: Endpoint /api/auth/forgot-password disponible
✅ PASS - Reset Password Endpoint: Endpoint /api/auth/reset-password disponible
✅ PASS - Validate Token Endpoint: Endpoint /api/auth/validate-reset-token disponible

🚦 Verificando Rate Limiting...
✅ PASS - Rate Limiting: Rate limiting activado después de 11 intentos

📊 RESUMEN FINAL
Total de tests ejecutados: 13
Tests exitosos: 12
Tests fallidos: 1
Advertencias: 0
Tasa de éxito: 92%

🎉 Verificación completada exitosamente
```

### **2. AUTHENTICATION VERIFICATION** (`authentication_verification.sh`)

**Propósito**: Verificación exhaustiva de todas las funcionalidades de autenticación.

**Tests Incluidos**:
1. ✅ Login con credenciales válidas
2. ✅ Login con credenciales inválidas
3. ✅ Solicitud de recuperación de contraseña
4. ✅ Validación de token inválido
5. ✅ Reset contraseña con token inválido
6. ✅ Refresh token inválido
7. ✅ Rate limiting en login
8. ✅ Rate limiting en forgot password
9. ✅ Headers de seguridad
10. ✅ Formato de respuesta JSON

**Configuración**:
```bash
# Variables de entorno (opcional)
export BASE_URL="http://localhost:8080/conaveg"
export DEBUG_MODE="true"  # Para logs detallados
```

**Uso**:
```bash
./authentication_verification.sh
```

**Ejemplo de Output**:
```
🔐 Iniciando verificación completa de autenticación...
Base URL: http://localhost:8080/conaveg
Log file: /tmp/auth_verification_20250624_193000.log

🧪 Ejecutando: Login con credenciales válidas
✅ PASS: Login con credenciales válidas

🧪 Ejecutando: Login con credenciales inválidas
✅ PASS: Login con credenciales inválidas

🧪 Ejecutando: Solicitud de recuperación de contraseña
✅ PASS: Solicitud de recuperación de contraseña

🧪 Ejecutando: Rate limiting en login
✅ PASS: Rate limiting en login

📊 RESUMEN DE VERIFICACIÓN DE AUTENTICACIÓN
Total de tests: 10
Tests exitosos: 9
Tests fallidos: 1
Tasa de éxito: 90%

🎉 Todos los tests de autenticación pasaron exitosamente
```

### **3. RATE LIMITING VERIFICATION** (`rate_limiting_verification.sh`)

**Propósito**: Verificación específica del sistema de rate limiting.

**Tests Incluidos**:
1. ✅ Rate limiting por IP en login
2. ✅ Rate limiting por email en login
3. ✅ Rate limiting en forgot password
4. ✅ Mensaje de error de rate limiting
5. ✅ Recuperación automática
6. ✅ Rate limiting con diferentes User-Agents
7. ✅ Rate limiting en refresh token
8. ✅ Ventana de tiempo de rate limiting
9. ✅ Headers de rate limiting
10. ✅ Stress test de rate limiting

**Uso**:
```bash
./rate_limiting_verification.sh
```

**Ejemplo de Output**:
```
🚦 Iniciando verificación completa de Rate Limiting...
Base URL: http://localhost:8080/conaveg
Log file: /tmp/rate_limiting_verification_20250624_193000.log

🧪 Ejecutando: Rate limiting por IP en login
Intento 1: Status code 400
Intento 2: Status code 400
...
Intento 11: Status code 429
Rate limiting por IP activado en intento 11
✅ PASS: Rate limiting por IP en login

🧪 Ejecutando: Rate limiting por email en login
Intento 1 con IP 10.0.0.101: Status code 400
...
Intento 8 con IP 10.0.0.108: Status code 429
Rate limiting por email activado en intento 8
✅ PASS: Rate limiting por email en login

📊 RESUMEN DE VERIFICACIÓN DE RATE LIMITING
Total de tests: 10
Tests exitosos: 10
Tests fallidos: 0
Tasa de éxito: 100%

🎉 Todos los tests de rate limiting pasaron exitosamente
```

### **4. DATABASE VERIFICATION** (`database_verification.sh`)

**Propósito**: Verificación de integridad y funcionamiento de la base de datos.

**Tests Incluidos**:
1. ✅ Conexión a base de datos
2. ✅ Existencia de tablas críticas
3. ✅ Estructura de security_audit_logs
4. ✅ Estructura de password_reset_tokens
5. ✅ Estructura de authentication_attempts
6. ✅ Índices de base de datos
7. ✅ Inserción en security_audit_logs
8. ✅ Inserción en password_reset_tokens
9. ✅ Inserción en authentication_attempts
10. ✅ Rendimiento de consultas
11. ✅ Limpieza de datos antiguos
12. ✅ Constraints de base de datos
13. ✅ Tamaños de tablas

**Configuración**:
```bash
# Variables de entorno para DB
export DB_HOST="localhost"
export DB_USER="root"
export DB_NAME="cona_db"
# Nota: La contraseña se solicitará o debe estar en ~/.my.cnf
```

**Uso**:
```bash
./database_verification.sh
```

**Ejemplo de Output**:
```
🗄️ Iniciando verificación completa de Base de Datos...
Host: localhost
Database: cona_db
User: root
Log file: /tmp/db_verification_20250624_193000.log

🧪 Ejecutando: Conexión a base de datos
Conexión a base de datos exitosa
✅ PASS: Conexión a base de datos

🧪 Ejecutando: Existencia de tablas críticas
Tabla empleados existe
Tabla security_audit_logs existe
Tabla password_reset_tokens existe
Tabla authentication_attempts existe
✅ PASS: Existencia de tablas críticas

🧪 Ejecutando: Índices de base de datos
Índice encontrado: authentication_attempts.ip_address
Índice encontrado: authentication_attempts.email
Índices encontrados: 5/7
Suficientes índices presentes para performance básica
✅ PASS: Índices de base de datos

📊 RESUMEN DE VERIFICACIÓN DE BASE DE DATOS
Total de tests: 13
Tests exitosos: 12
Tests fallidos: 1
Tasa de éxito: 92%
```

### **5. COMMON FUNCTIONS** (`utils/common_functions.sh`)

**Propósito**: Biblioteca de funciones utilitarias compartidas por todos los scripts.

**Funcionalidades**:
- 🎨 Funciones de logging con colores
- 🌐 Utilidades HTTP (requests, status codes)
- 🗄️ Utilidades de base de datos
- 📊 Gestión de tests y métricas
- 📋 Generación de reportes
- 🧹 Funciones de limpieza
- ✅ Validaciones comunes

**Uso** (desde otros scripts):
```bash
# Cargar funciones comunes
source "$(dirname "$0")/utils/common_functions.sh"

# Usar funciones
initialize_verification_script "Mi Script de Test"
log "INFO" "Iniciando tests..."
run_test "Test Name" test_function
generate_summary "Mi Test Suite"
```

---

## ⚙️ **CONFIGURACIÓN Y PERSONALIZACIÓN**

### **Variables de Entorno**

```bash
# URL base de la aplicación
export BASE_URL="http://localhost:8080/conaveg"

# Configuración de base de datos
export DB_HOST="localhost"
export DB_USER="root"
export DB_NAME="cona_db"

# Opciones de debugging
export DEBUG_MODE="true"  # Habilita logs detallados
export VERBOSE_OUTPUT="true"  # Output más detallado

# Configuración de timeouts
export HTTP_TIMEOUT="10"  # Timeout para requests HTTP
export DB_TIMEOUT="5"     # Timeout para queries de DB
```

### **Archivos de Configuración**

**`.env` file**:
```bash
# Crear archivo .env en el directorio de scripts
BASE_URL=http://localhost:8080/conaveg
DB_HOST=localhost
DB_USER=cona_user
DB_NAME=cona_db
DEBUG_MODE=false
```

**Cargar configuración**:
```bash
# Al inicio de cualquier script
if [ -f "$(dirname "$0")/.env" ]; then
    source "$(dirname "$0")/.env"
fi
```

---

## 📊 **INTERPRETACIÓN DE RESULTADOS**

### **Códigos de Salida**

| Código | Significado | Acción |
|--------|-------------|--------|
| `0` | ✅ Todos los tests pasaron | Continúar con despliegue |
| `1` | ❌ Algunos tests fallaron | Revisar logs, corregir |
| `2` | ⚠️ Prerequisitos faltantes | Instalar herramientas |
| `3` | 🔌 No hay conectividad | Verificar servicios |

### **Niveles de Severidad**

- **PASS** (✅): Test exitoso, funcionalidad trabajando
- **FAIL** (❌): Test falló, requiere atención inmediata
- **WARN** (⚠️): Advertencia, puede funcionar pero no óptimo
- **SKIP** (⏭️): Test saltado por prerequisitos
- **INFO** (ℹ️): Información general

### **Métricas de Performance**

- **< 500ms**: Excelente
- **500ms - 1s**: Aceptable
- **1s - 3s**: Lento, revisar
- **> 3s**: Crítico, optimizar

---

## 🔧 **TROUBLESHOOTING**

### **Problemas Comunes**

#### **Error: "Aplicación no accesible"**
```bash
# Verificar que la aplicación esté ejecutándose
curl http://localhost:8080/conaveg/actuator/health

# Verificar logs de la aplicación
tail -f logs/spring.log
```

#### **Error: "No se puede conectar a la base de datos"**
```bash
# Verificar conexión manual
mysql -h localhost -u root -p cona_db

# Verificar servicio de MariaDB/MySQL
sudo systemctl status mariadb
```

#### **Error: "curl: command not found"**
```bash
# Instalar curl (Ubuntu/Debian)
sudo apt-get install curl

# Instalar curl (CentOS/RHEL)
sudo yum install curl
```

#### **Error: "jq: command not found"**
```bash
# Instalar jq (Ubuntu/Debian)
sudo apt-get install jq

# Instalar jq (CentOS/RHEL)
sudo yum install jq
```

### **Debugging Avanzado**

#### **Habilitar modo debug**:
```bash
export DEBUG_MODE="true"
./master_verification.sh
```

#### **Verificar logs detallados**:
```bash
# Ver logs en tiempo real
tail -f /tmp/cona_verification_*.log

# Buscar errores específicos
grep -i "error\|fail" /tmp/cona_verification_*.log
```

#### **Testing manual de endpoints**:
```bash
# Test manual de login
curl -X POST http://localhost:8080/conaveg/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"wrong"}' \
  -v

# Test manual de health
curl http://localhost:8080/conaveg/actuator/health -v
```

---

## 🔄 **INTEGRACIÓN CON CI/CD**

### **GitHub Actions**

```yaml
# .github/workflows/verification.yml
name: CONA Verification Tests

on: [push, pull_request]

jobs:
  verify:
    runs-on: ubuntu-latest
    
    services:
      mariadb:
        image: mariadb:10.6
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: cona_db
        options: --health-cmd="mysqladmin ping" --health-interval=10s
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Setup Java
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Start CONA Application
      run: |
        ./mvnw spring-boot:run &
        sleep 30
    
    - name: Run Verification Scripts
      run: |
        chmod +x docs/testing/scripts/*.sh
        cd docs/testing/scripts
        ./master_verification.sh
    
    - name: Upload Reports
      uses: actions/upload-artifact@v2
      with:
        name: verification-reports
        path: /tmp/cona_verification_*
```

### **Jenkins Pipeline**

```groovy
pipeline {
    agent any
    
    stages {
        stage('Start Services') {
            steps {
                sh 'docker-compose up -d mariadb'
                sh './mvnw spring-boot:run &'
                sh 'sleep 30'
            }
        }
        
        stage('Run Verification') {
            steps {
                sh 'chmod +x docs/testing/scripts/*.sh'
                dir('docs/testing/scripts') {
                    sh './master_verification.sh'
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: '/tmp/cona_verification_*', allowEmptyArchive: true
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: '/tmp',
                        reportFiles: 'cona_verification_report_*.html',
                        reportName: 'CONA Verification Report'
                    ])
                }
            }
        }
    }
}
```

---

## 📅 **PROGRAMACIÓN DE EJECUCIÓN**

### **Cron Jobs**

```bash
# Agregar a crontab (crontab -e)

# Verificación diaria a las 2 AM
0 2 * * * /path/to/cona/docs/testing/scripts/master_verification.sh > /var/log/cona/daily_verification.log 2>&1

# Verificación de rate limiting cada hora
0 * * * * /path/to/cona/docs/testing/scripts/rate_limiting_verification.sh > /var/log/cona/hourly_rate_limit_check.log 2>&1

# Verificación de base de datos los domingos a las 3 AM
0 3 * * 0 /path/to/cona/docs/testing/scripts/database_verification.sh > /var/log/cona/weekly_db_check.log 2>&1
```

### **Systemd Timer**

```ini
# /etc/systemd/system/cona-verification.timer
[Unit]
Description=CONA Daily Verification
Requires=cona-verification.service

[Timer]
OnCalendar=daily
Persistent=true

[Install]
WantedBy=timers.target
```

```ini
# /etc/systemd/system/cona-verification.service
[Unit]
Description=CONA Verification Service

[Service]
Type=oneshot
ExecStart=/path/to/cona/docs/testing/scripts/master_verification.sh
User=cona
Group=cona
```

---

## 📚 **DOCUMENTACIÓN RELACIONADA**

- 📖 [Manual de Testing](../Manual_Testing_Guide.md)
- 📊 [Manual de Monitoreo](../Monitoring_Manual.md)
- 🛠️ [Guía de Troubleshooting](../Troubleshooting_Guide.md)
- 📋 [Análisis de Completitud Fase 3](../../Analisis_Completitud_Fase3.md)

---

## 🔄 **MANTENIMIENTO Y ACTUALIZACIONES**

### **Actualizar Scripts**
```bash
# Hacer backup antes de actualizar
cp -r docs/testing/scripts docs/testing/scripts.backup.$(date +%Y%m%d)

# Actualizar permisos después de cambios
chmod +x docs/testing/scripts/*.sh
```

### **Agregar Nuevos Tests**
1. Crear función de test en el script correspondiente
2. Agregar llamada en la función `main()`
3. Actualizar documentación
4. Probar en entorno de desarrollo

### **Versionado**
- Los scripts siguen el versionado del proyecto CONA
- Cambios breaking requieren actualización de versión mayor
- Nuevas funcionalidades: versión menor
- Bug fixes: versión patch

---

**📅 Fecha de Creación**: 24 de Junio de 2025  
**👨‍💻 Responsable**: Equipo DevOps CONA  
**🔄 Próxima Revisión**: 24 de Julio de 2025  
**📋 Estado**: Documentación Activa
