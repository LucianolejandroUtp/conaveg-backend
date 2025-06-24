# 🔧 SCRIPTS DE VERIFICACIÓN - SISTEMA CONA

## 📋 **INFORMACIÓN DEL DOCUMENTO**

**Fecha de Creación**: 24 de Junio de 2025  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Fase**: Fase 3 - Funcionalidades Avanzadas de Autenticación  
**Responsable**: Equipo DevOps & QA  
**Audiencia**: Administradores de Sistema, DevOps, Desarrolladores, QA  

---

## 🎯 **OBJETIVO**

Esta colección de scripts proporciona herramientas automatizadas para verificar el correcto funcionamiento de todas las funcionalidades implementadas en la Fase 3 del sistema CONA, incluyendo autenticación, rate limiting, auditoría de seguridad y tareas programadas.

---

## 📂 **ESTRUCTURA DE SCRIPTS**

```
docs/testing/scripts/
├── health/
│   ├── system_health_check.sh
│   ├── database_health_check.sh
│   └── security_health_check.sh
├── authentication/
│   ├── test_auth_endpoints.sh
│   ├── test_rate_limiting.sh
│   └── test_password_reset.sh
├── monitoring/
│   ├── collect_metrics.sh
│   ├── check_alerts.sh
│   └── performance_test.sh
├── maintenance/
│   ├── cleanup_verification.sh
│   ├── backup_verification.sh
│   └── log_rotation_check.sh
└── utils/
    ├── common_functions.sh
    ├── config_validator.sh
    └── report_generator.sh
```

---

## 🔧 **SCRIPTS PRINCIPALES**

### **1. SCRIPT MAESTRO DE VERIFICACIÓN**

<function_calls>
<invoke name="create_file">
<parameter name="content">#!/bin/bash
# master_verification.sh
# Script maestro para verificar todas las funcionalidades del sistema CONA

# Configuración
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_URL="http://localhost:8080/conaveg"
LOG_FILE="/tmp/cona_verification_$(date +%Y%m%d_%H%M%S).log"
REPORT_FILE="/tmp/cona_verification_report_$(date +%Y%m%d_%H%M%S).html"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Contadores
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
WARNINGS=0

# Función para logging
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" >> "$LOG_FILE"
    echo -e "$1"
}

# Función para reportar resultados
report_result() {
    local test_name="$1"
    local status="$2"
    local message="$3"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    case $status in
        "PASS")
            PASSED_TESTS=$((PASSED_TESTS + 1))
            log "${GREEN}✅ PASS${NC} - $test_name: $message"
            ;;
        "FAIL")
            FAILED_TESTS=$((FAILED_TESTS + 1))
            log "${RED}❌ FAIL${NC} - $test_name: $message"
            ;;
        "WARN")
            WARNINGS=$((WARNINGS + 1))
            log "${YELLOW}⚠️  WARN${NC} - $test_name: $message"
            ;;
        "INFO")
            log "${BLUE}ℹ️  INFO${NC} - $test_name: $message"
            ;;
    esac
}

# Función para verificar prerequisitos
check_prerequisites() {
    log "${BLUE}🔍 Verificando prerequisitos...${NC}"
    
    # Verificar curl
    if ! command -v curl &> /dev/null; then
        report_result "Prerequisites" "FAIL" "curl no está instalado"
        exit 1
    fi
    
    # Verificar jq
    if ! command -v jq &> /dev/null; then
        report_result "Prerequisites" "WARN" "jq no está instalado - algunos tests se saltarán"
    fi
    
    # Verificar mysql
    if ! command -v mysql &> /dev/null; then
        report_result "Prerequisites" "WARN" "mysql client no está instalado - tests de BD se saltarán"
    fi
    
    # Verificar conectividad a la aplicación
    if curl -s --max-time 5 "$BASE_URL/actuator/health" > /dev/null; then
        report_result "Prerequisites" "PASS" "Aplicación accesible en $BASE_URL"
    else
        report_result "Prerequisites" "FAIL" "Aplicación no accesible en $BASE_URL"
        exit 1
    fi
}

# Test 1: Health Check General
test_general_health() {
    log "${BLUE}🏥 Ejecutando Health Check General...${NC}"
    
    # Test actuator/health
    local health_response=$(curl -s "$BASE_URL/actuator/health")
    if echo "$health_response" | grep -q '"status":"UP"'; then
        report_result "General Health" "PASS" "Actuator health endpoint responde UP"
    else
        report_result "General Health" "FAIL" "Actuator health endpoint no responde UP"
    fi
    
    # Test response time
    local start_time=$(date +%s%N)
    curl -s "$BASE_URL/actuator/health" > /dev/null
    local end_time=$(date +%s%N)
    local response_time=$(( (end_time - start_time) / 1000000 ))
    
    if [ $response_time -lt 1000 ]; then
        report_result "Response Time" "PASS" "Health endpoint responde en ${response_time}ms"
    elif [ $response_time -lt 3000 ]; then
        report_result "Response Time" "WARN" "Health endpoint responde en ${response_time}ms (lento)"
    else
        report_result "Response Time" "FAIL" "Health endpoint responde en ${response_time}ms (muy lento)"
    fi
}

# Test 2: Endpoints de Autenticación
test_authentication_endpoints() {
    log "${BLUE}🔐 Verificando endpoints de autenticación...${NC}"
    
    # Test login endpoint availability
    local login_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{}')
    
    if [ "$login_status" = "400" ]; then
        report_result "Login Endpoint" "PASS" "Endpoint /api/auth/login disponible (400 Bad Request esperado)"
    else
        report_result "Login Endpoint" "FAIL" "Endpoint /api/auth/login no disponible (recibido: $login_status)"
    fi
    
    # Test forgot password endpoint
    local forgot_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/forgot-password" \
        -H "Content-Type: application/json" \
        -d '{}')
    
    if [ "$forgot_status" = "400" ]; then
        report_result "Forgot Password Endpoint" "PASS" "Endpoint /api/auth/forgot-password disponible"
    else
        report_result "Forgot Password Endpoint" "FAIL" "Endpoint /api/auth/forgot-password no disponible (recibido: $forgot_status)"
    fi
    
    # Test reset password endpoint
    local reset_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/reset-password" \
        -H "Content-Type: application/json" \
        -d '{}')
    
    if [ "$reset_status" = "400" ]; then
        report_result "Reset Password Endpoint" "PASS" "Endpoint /api/auth/reset-password disponible"
    else
        report_result "Reset Password Endpoint" "FAIL" "Endpoint /api/auth/reset-password no disponible (recibido: $reset_status)"
    fi
    
    # Test validate token endpoint
    local validate_status=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/api/auth/validate-reset-token?token=invalid")
    
    if [ "$validate_status" = "200" ] || [ "$validate_status" = "400" ]; then
        report_result "Validate Token Endpoint" "PASS" "Endpoint /api/auth/validate-reset-token disponible"
    else
        report_result "Validate Token Endpoint" "FAIL" "Endpoint /api/auth/validate-reset-token no disponible (recibido: $validate_status)"
    fi
}

# Test 3: Rate Limiting
test_rate_limiting() {
    log "${BLUE}🚦 Verificando Rate Limiting...${NC}"
    
    local rate_limit_triggered=false
    local attempt_count=0
    
    # Realizar múltiples intentos fallidos
    for i in {1..15}; do
        attempt_count=$i
        local status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d '{"email":"test@rate.limit","password":"wrongpassword"}')
        
        if [ "$status" = "429" ]; then
            rate_limit_triggered=true
            break
        fi
        
        sleep 0.1
    done
    
    if [ "$rate_limit_triggered" = true ]; then
        report_result "Rate Limiting" "PASS" "Rate limiting activado después de $attempt_count intentos"
    else
        report_result "Rate Limiting" "FAIL" "Rate limiting no se activó después de $attempt_count intentos"
    fi
}

# Test 4: Base de Datos
test_database_connection() {
    log "${BLUE}🗄️  Verificando conexión a base de datos...${NC}"
    
    if command -v mysql &> /dev/null; then
        # Verificar conexión básica (requiere configuración de credenciales)
        if mysql -h localhost -u root -e "SELECT 1;" &> /dev/null; then
            report_result "Database Connection" "PASS" "Conexión a base de datos exitosa"
            
            # Verificar tablas críticas
            local tables=("empleados" "security_audit_logs" "password_reset_tokens" "authentication_attempts")
            for table in "${tables[@]}"; do
                if mysql -h localhost -u root -e "SELECT COUNT(*) FROM $table;" &> /dev/null; then
                    report_result "Database Table" "PASS" "Tabla $table existe y es accesible"
                else
                    report_result "Database Table" "FAIL" "Tabla $table no existe o no es accesible"
                fi
            done
        else
            report_result "Database Connection" "WARN" "No se pudo verificar conexión a BD (credenciales no configuradas)"
        fi
    else
        report_result "Database Connection" "WARN" "mysql client no disponible - verificación de BD saltada"
    fi
}

# Test 5: Logs y Auditoría
test_logging_and_audit() {
    log "${BLUE}📝 Verificando logs y auditoría...${NC}"
    
    # Verificar archivos de log
    local log_paths=("/app/logs/spring.log" "/var/log/cona/application.log" "logs/spring.log")
    local log_found=false
    
    for log_path in "${log_paths[@]}"; do
        if [ -f "$log_path" ]; then
            log_found=true
            report_result "Log File" "PASS" "Archivo de log encontrado: $log_path"
            
            # Verificar si hay logs recientes (últimas 24 horas)
            if find "$log_path" -mtime -1 | grep -q .; then
                report_result "Log Activity" "PASS" "Logs recientes encontrados en $log_path"
            else
                report_result "Log Activity" "WARN" "No hay logs recientes en $log_path"
            fi
            break
        fi
    done
    
    if [ "$log_found" = false ]; then
        report_result "Log File" "WARN" "No se encontraron archivos de log en las ubicaciones esperadas"
    fi
}

# Test 6: Configuración
test_configuration() {
    log "${BLUE}⚙️  Verificando configuración...${NC}"
    
    # Verificar archivo de configuración
    local config_paths=("/app/config/application.properties" "src/main/resources/application.properties")
    local config_found=false
    
    for config_path in "${config_paths[@]}"; do
        if [ -f "$config_path" ]; then
            config_found=true
            report_result "Config File" "PASS" "Archivo de configuración encontrado: $config_path"
            
            # Verificar configuraciones críticas
            local required_configs=("jwt.secret" "spring.datasource.url")
            for config in "${required_configs[@]}"; do
                if grep -q "$config" "$config_path"; then
                    report_result "Config Property" "PASS" "Configuración $config presente"
                else
                    report_result "Config Property" "WARN" "Configuración $config no encontrada"
                fi
            done
            break
        fi
    done
    
    if [ "$config_found" = false ]; then
        report_result "Config File" "WARN" "No se encontró archivo de configuración en ubicaciones esperadas"
    fi
}

# Test 7: Endpoints de Refresh Token
test_refresh_token() {
    log "${BLUE}🔄 Verificando endpoint de refresh token...${NC}"
    
    local refresh_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/refresh" \
        -H "Content-Type: application/json" \
        -d '{"token":"invalid_token"}')
    
    if [ "$refresh_status" = "400" ] || [ "$refresh_status" = "401" ]; then
        report_result "Refresh Token Endpoint" "PASS" "Endpoint /api/auth/refresh disponible y validando tokens"
    else
        report_result "Refresh Token Endpoint" "FAIL" "Endpoint /api/auth/refresh no responde correctamente (recibido: $refresh_status)"
    fi
}

# Test 8: Security Headers
test_security_headers() {
    log "${BLUE}🔒 Verificando headers de seguridad...${NC}"
    
    local headers_response=$(curl -s -I "$BASE_URL/actuator/health")
    
    # Verificar headers importantes
    local security_headers=("X-Content-Type-Options" "X-Frame-Options" "X-XSS-Protection")
    for header in "${security_headers[@]}"; do
        if echo "$headers_response" | grep -q "$header"; then
            report_result "Security Header" "PASS" "Header $header presente"
        else
            report_result "Security Header" "WARN" "Header $header no encontrado"
        fi
    done
}

# Función para generar reporte HTML
generate_html_report() {
    log "${BLUE}📊 Generando reporte HTML...${NC}"
    
    cat > "$REPORT_FILE" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>CONA Verification Report - $(date)</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .header { background-color: #f0f0f0; padding: 15px; border-radius: 5px; }
        .summary { background-color: #e8f5e8; padding: 10px; border-radius: 5px; margin: 10px 0; }
        .test-pass { color: #008000; }
        .test-fail { color: #ff0000; }
        .test-warn { color: #ff8c00; }
        .test-info { color: #0066cc; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔧 CONA System Verification Report</h1>
        <p><strong>Fecha:</strong> $(date)</p>
        <p><strong>Sistema:</strong> CONA - Gestión CONAVEG</p>
        <p><strong>Fase:</strong> Fase 3 - Funcionalidades Avanzadas de Autenticación</p>
    </div>
    
    <div class="summary">
        <h2>📊 Resumen de Resultados</h2>
        <p><strong>Total de Tests:</strong> $TOTAL_TESTS</p>
        <p class="test-pass"><strong>Exitosos:</strong> $PASSED_TESTS</p>
        <p class="test-fail"><strong>Fallidos:</strong> $FAILED_TESTS</p>
        <p class="test-warn"><strong>Advertencias:</strong> $WARNINGS</p>
        <p><strong>Tasa de Éxito:</strong> $(( PASSED_TESTS * 100 / TOTAL_TESTS ))%</p>
    </div>
    
    <h2>📋 Detalle de Resultados</h2>
    <table>
        <tr>
            <th>Estado</th>
            <th>Componente</th>
            <th>Descripción</th>
        </tr>
EOF
    
    # Agregar resultados del log al HTML
    grep -E "(PASS|FAIL|WARN|INFO)" "$LOG_FILE" | while read line; do
        if echo "$line" | grep -q "PASS"; then
            echo "<tr><td class='test-pass'>✅ PASS</td><td>$(echo "$line" | cut -d'-' -f3 | cut -d':' -f1 | xargs)</td><td>$(echo "$line" | cut -d':' -f3- | xargs)</td></tr>" >> "$REPORT_FILE"
        elif echo "$line" | grep -q "FAIL"; then
            echo "<tr><td class='test-fail'>❌ FAIL</td><td>$(echo "$line" | cut -d'-' -f3 | cut -d':' -f1 | xargs)</td><td>$(echo "$line" | cut -d':' -f3- | xargs)</td></tr>" >> "$REPORT_FILE"
        elif echo "$line" | grep -q "WARN"; then
            echo "<tr><td class='test-warn'>⚠️ WARN</td><td>$(echo "$line" | cut -d'-' -f3 | cut -d':' -f1 | xargs)</td><td>$(echo "$line" | cut -d':' -f3- | xargs)</td></tr>" >> "$REPORT_FILE"
        fi
    done
    
    cat >> "$REPORT_FILE" << EOF
    </table>
    
    <h2>📝 Log Completo</h2>
    <pre style="background-color: #f0f0f0; padding: 10px; border-radius: 5px; overflow-x: auto;">
$(cat "$LOG_FILE")
    </pre>
    
    <div class="header">
        <p><strong>Reporte generado el:</strong> $(date)</p>
        <p><strong>Archivo de log:</strong> $LOG_FILE</p>
    </div>
</body>
</html>
EOF
}

# Función principal
main() {
    echo "🚀 Iniciando verificación completa del sistema CONA..."
    echo "Log file: $LOG_FILE"
    echo "Report file: $REPORT_FILE"
    echo ""
    
    # Ejecutar todas las verificaciones
    check_prerequisites
    test_general_health
    test_authentication_endpoints
    test_rate_limiting
    test_database_connection
    test_logging_and_audit
    test_configuration
    test_refresh_token
    test_security_headers
    
    # Generar reporte
    generate_html_report
    
    # Mostrar resumen
    echo ""
    log "${BLUE}📊 RESUMEN FINAL${NC}"
    log "Total de tests ejecutados: $TOTAL_TESTS"
    log "${GREEN}Tests exitosos: $PASSED_TESTS${NC}"
    log "${RED}Tests fallidos: $FAILED_TESTS${NC}"
    log "${YELLOW}Advertencias: $WARNINGS${NC}"
    log "Tasa de éxito: $(( PASSED_TESTS * 100 / TOTAL_TESTS ))%"
    log ""
    log "📄 Log completo: $LOG_FILE"
    log "📊 Reporte HTML: $REPORT_FILE"
    
    # Retornar código de salida apropiado
    if [ $FAILED_TESTS -eq 0 ]; then
        log "${GREEN}🎉 Verificación completada exitosamente${NC}"
        exit 0
    else
        log "${RED}⚠️  Verificación completada con errores${NC}"
        exit 1
    fi
}

# Ejecutar si se llama directamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
