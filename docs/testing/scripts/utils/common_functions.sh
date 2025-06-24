#!/bin/bash
# common_functions.sh
# Funciones utilitarias comunes para todos los scripts de verificación

# Configuración global
export SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export BASE_URL="${BASE_URL:-http://localhost:8080/conaveg}"
export DB_HOST="${DB_HOST:-localhost}"
export DB_USER="${DB_USER:-root}"
export DB_NAME="${DB_NAME:-cona_db}"

# Colores para output
export RED='\033[0;31m'
export GREEN='\033[0;32m'
export YELLOW='\033[1;33m'
export BLUE='\033[0;34m'
export PURPLE='\033[0;35m'
export CYAN='\033[0;36m'
export WHITE='\033[1;37m'
export NC='\033[0m' # No Color

# Símbolos
export CHECK_MARK="✅"
export CROSS_MARK="❌"
export WARNING_MARK="⚠️"
export INFO_MARK="ℹ️"
export ROCKET="🚀"
export GEAR="⚙️"
export LOCK="🔒"
export DATABASE="🗄️"

# Variables globales para resultados
export GLOBAL_TESTS_TOTAL=0
export GLOBAL_TESTS_PASSED=0
export GLOBAL_TESTS_FAILED=0
export GLOBAL_WARNINGS=0

# ==========================================
# FUNCIONES DE LOGGING Y OUTPUT
# ==========================================

# Función de logging mejorada
log() {
    local level="$1"
    local message="$2"
    local timestamp="$(date '+%Y-%m-%d %H:%M:%S')"
    
    case $level in
        "INFO")
            echo -e "${timestamp} ${BLUE}[INFO]${NC} $message" | tee -a "$LOG_FILE"
            ;;
        "SUCCESS")
            echo -e "${timestamp} ${GREEN}[SUCCESS]${NC} $message" | tee -a "$LOG_FILE"
            ;;
        "WARNING")
            echo -e "${timestamp} ${YELLOW}[WARNING]${NC} $message" | tee -a "$LOG_FILE"
            ;;
        "ERROR")
            echo -e "${timestamp} ${RED}[ERROR]${NC} $message" | tee -a "$LOG_FILE"
            ;;
        "DEBUG")
            if [ "${DEBUG_MODE:-false}" = "true" ]; then
                echo -e "${timestamp} ${PURPLE}[DEBUG]${NC} $message" | tee -a "$LOG_FILE"
            fi
            ;;
        *)
            echo -e "${timestamp} $level" | tee -a "$LOG_FILE"
            ;;
    esac
}

# Función para imprimir headers sección
print_section_header() {
    local title="$1"
    local icon="${2:-🔧}"
    
    echo ""
    echo -e "${BLUE}================================================${NC}"
    echo -e "${WHITE}$icon $title${NC}"
    echo -e "${BLUE}================================================${NC}"
    echo ""
}

# Función para imprimir resultados de test
print_test_result() {
    local test_name="$1"
    local status="$2"
    local message="$3"
    local duration="${4:-N/A}"
    
    case $status in
        "PASS")
            echo -e "${GREEN}$CHECK_MARK PASS${NC} - $test_name${NC} ${CYAN}(${duration}ms)${NC}"
            if [ -n "$message" ]; then
                echo -e "    ${GREEN}└─${NC} $message"
            fi
            ;;
        "FAIL")
            echo -e "${RED}$CROSS_MARK FAIL${NC} - $test_name${NC} ${CYAN}(${duration}ms)${NC}"
            if [ -n "$message" ]; then
                echo -e "    ${RED}└─${NC} $message"
            fi
            ;;
        "WARN")
            echo -e "${YELLOW}$WARNING_MARK WARN${NC} - $test_name${NC} ${CYAN}(${duration}ms)${NC}"
            if [ -n "$message" ]; then
                echo -e "    ${YELLOW}└─${NC} $message"
            fi
            ;;
        "SKIP")
            echo -e "${CYAN}⏭️  SKIP${NC} - $test_name${NC}"
            if [ -n "$message" ]; then
                echo -e "    ${CYAN}└─${NC} $message"
            fi
            ;;
    esac
}

# ==========================================
# FUNCIONES DE VERIFICACIÓN DE PREREQUISITOS
# ==========================================

# Verificar que las herramientas necesarias están instaladas
check_prerequisites() {
    local tools=("curl" "jq" "mysql")
    local missing_tools=()
    
    log "INFO" "Verificando prerequisitos..."
    
    for tool in "${tools[@]}"; do
        if ! command -v "$tool" &> /dev/null; then
            missing_tools+=("$tool")
            log "WARNING" "$tool no está instalado"
        else
            log "SUCCESS" "$tool está disponible"
        fi
    done
    
    if [ ${#missing_tools[@]} -gt 0 ]; then
        log "WARNING" "Algunas herramientas no están disponibles: ${missing_tools[*]}"
        return 1
    else
        log "SUCCESS" "Todos los prerequisitos están disponibles"
        return 0
    fi
}

# Verificar conectividad a la aplicación
check_application_connectivity() {
    log "INFO" "Verificando conectividad a la aplicación en $BASE_URL"
    
    local start_time=$(date +%s%N)
    if curl -s --max-time 5 "$BASE_URL/actuator/health" > /dev/null; then
        local end_time=$(date +%s%N)
        local duration=$(( (end_time - start_time) / 1000000 ))
        log "SUCCESS" "Aplicación accesible (${duration}ms)"
        return 0
    else
        log "ERROR" "Aplicación no accesible en $BASE_URL"
        return 1
    fi
}

# Verificar conectividad a base de datos
check_database_connectivity() {
    log "INFO" "Verificando conectividad a base de datos $DB_NAME en $DB_HOST"
    
    if command -v mysql &> /dev/null; then
        if mysql -h "$DB_HOST" -u "$DB_USER" -e "SELECT 1;" "$DB_NAME" &>/dev/null; then
            log "SUCCESS" "Base de datos accesible"
            return 0
        else
            log "ERROR" "No se puede conectar a la base de datos"
            return 1
        fi
    else
        log "WARNING" "mysql client no disponible - verificación de BD saltada"
        return 1
    fi
}

# ==========================================
# FUNCIONES DE UTILIDAD HTTP
# ==========================================

# Realizar request HTTP con métricas
http_request() {
    local method="$1"
    local url="$2"
    local headers="$3"
    local data="$4"
    local timeout="${5:-10}"
    
    local start_time=$(date +%s%N)
    local curl_cmd="curl -s --max-time $timeout"
    
    # Agregar método
    curl_cmd="$curl_cmd -X $method"
    
    # Agregar headers si se proporcionan
    if [ -n "$headers" ]; then
        while IFS='|' read -ra HEADER_ARRAY; do
            for header in "${HEADER_ARRAY[@]}"; do
                curl_cmd="$curl_cmd -H \"$header\""
            done
        done <<< "$headers"
    fi
    
    # Agregar datos si se proporcionan
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -d '$data'"
    fi
    
    # Ejecutar request
    local response=$(eval "$curl_cmd $url")
    local status_code=$(eval "$curl_cmd -o /dev/null -w \"%{http_code}\" $url")
    local end_time=$(date +%s%N)
    local duration=$(( (end_time - start_time) / 1000000 ))
    
    # Crear objeto de respuesta
    cat << EOF
{
    "status_code": "$status_code",
    "response": "$response",
    "duration_ms": $duration,
    "url": "$url",
    "method": "$method"
}
EOF
}

# Obtener solo el status code de un request
get_status_code() {
    local method="$1"
    local url="$2"
    local headers="$3"
    local data="$4"
    
    local curl_cmd="curl -s -o /dev/null -w \"%{http_code}\""
    
    curl_cmd="$curl_cmd -X $method"
    
    if [ -n "$headers" ]; then
        while IFS='|' read -ra HEADER_ARRAY; do
            for header in "${HEADER_ARRAY[@]}"; do
                curl_cmd="$curl_cmd -H \"$header\""
            done
        done <<< "$headers"
    fi
    
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -d '$data'"
    fi
    
    eval "$curl_cmd $url"
}

# ==========================================
# FUNCIONES DE UTILIDAD DE BASE DE DATOS
# ==========================================

# Ejecutar query SQL con manejo de errores
execute_sql() {
    local query="$1"
    local description="${2:-SQL Query}"
    local silent="${3:-false}"
    
    if [ "$silent" != "true" ]; then
        log "DEBUG" "Ejecutando SQL: $description"
    fi
    
    local start_time=$(date +%s%N)
    local result
    
    if result=$(mysql -h "$DB_HOST" -u "$DB_USER" -e "$query" "$DB_NAME" 2>&1); then
        local end_time=$(date +%s%N)
        local duration=$(( (end_time - start_time) / 1000000 ))
        
        if [ "$silent" != "true" ]; then
            log "SUCCESS" "$description completado (${duration}ms)"
        fi
        
        echo "$result"
        return 0
    else
        local end_time=$(date +%s%N)
        local duration=$(( (end_time - start_time) / 1000000 ))
        
        log "ERROR" "$description falló (${duration}ms): $result"
        return 1
    fi
}

# Verificar si una tabla existe
table_exists() {
    local table_name="$1"
    
    local result=$(mysql -h "$DB_HOST" -u "$DB_USER" -e "SHOW TABLES LIKE '$table_name';" "$DB_NAME" 2>/dev/null)
    
    if [ -n "$result" ]; then
        return 0
    else
        return 1
    fi
}

# Obtener conteo de filas de una tabla
get_table_row_count() {
    local table_name="$1"
    
    local count=$(mysql -h "$DB_HOST" -u "$DB_USER" -se "SELECT COUNT(*) FROM $table_name;" "$DB_NAME" 2>/dev/null)
    
    if [ -n "$count" ]; then
        echo "$count"
        return 0
    else
        echo "0"
        return 1
    fi
}

# ==========================================
# FUNCIONES DE GESTIÓN DE TESTS
# ==========================================

# Ejecutar un test con métricas
run_test() {
    local test_name="$1"
    local test_function="$2"
    local critical="${3:-false}"
    
    GLOBAL_TESTS_TOTAL=$((GLOBAL_TESTS_TOTAL + 1))
    
    log "INFO" "Ejecutando test: $test_name"
    
    local start_time=$(date +%s%N)
    local test_result
    
    if test_result=$($test_function 2>&1); then
        local end_time=$(date +%s%N)
        local duration=$(( (end_time - start_time) / 1000000 ))
        
        GLOBAL_TESTS_PASSED=$((GLOBAL_TESTS_PASSED + 1))
        print_test_result "$test_name" "PASS" "$test_result" "$duration"
        log "SUCCESS" "Test '$test_name' PASÓ"
        return 0
    else
        local end_time=$(date +%s%N)
        local duration=$(( (end_time - start_time) / 1000000 ))
        
        if [ "$critical" = "true" ]; then
            GLOBAL_TESTS_FAILED=$((GLOBAL_TESTS_FAILED + 1))
            print_test_result "$test_name" "FAIL" "$test_result" "$duration"
            log "ERROR" "Test crítico '$test_name' FALLÓ: $test_result"
        else
            GLOBAL_WARNINGS=$((GLOBAL_WARNINGS + 1))
            print_test_result "$test_name" "WARN" "$test_result" "$duration"
            log "WARNING" "Test '$test_name' falló (no crítico): $test_result"
        fi
        return 1
    fi
}

# Saltear un test con razón
skip_test() {
    local test_name="$1"
    local reason="$2"
    
    print_test_result "$test_name" "SKIP" "$reason"
    log "INFO" "Test '$test_name' saltado: $reason"
}

# ==========================================
# FUNCIONES DE GENERACIÓN DE REPORTES
# ==========================================

# Generar resumen de resultados
generate_summary() {
    local test_suite_name="${1:-Test Suite}"
    
    echo ""
    print_section_header "RESUMEN DE RESULTADOS - $test_suite_name" "📊"
    
    echo -e "${WHITE}Total de tests ejecutados:${NC} $GLOBAL_TESTS_TOTAL"
    echo -e "${GREEN}Tests exitosos:${NC} $GLOBAL_TESTS_PASSED"
    echo -e "${RED}Tests fallidos:${NC} $GLOBAL_TESTS_FAILED"
    echo -e "${YELLOW}Advertencias:${NC} $GLOBAL_WARNINGS"
    
    if [ $GLOBAL_TESTS_TOTAL -gt 0 ]; then
        local success_rate=$(( GLOBAL_TESTS_PASSED * 100 / GLOBAL_TESTS_TOTAL ))
        echo -e "${WHITE}Tasa de éxito:${NC} ${success_rate}%"
        
        if [ $success_rate -ge 90 ]; then
            echo -e "${GREEN}🎉 Excelente! Más del 90% de tests pasaron${NC}"
        elif [ $success_rate -ge 75 ]; then
            echo -e "${YELLOW}👍 Bueno. Más del 75% de tests pasaron${NC}"
        elif [ $success_rate -ge 50 ]; then
            echo -e "${YELLOW}⚠️ Regular. Solo el ${success_rate}% de tests pasaron${NC}"
        else
            echo -e "${RED}❌ Crítico. Menos del 50% de tests pasaron${NC}"
        fi
    fi
    
    echo ""
}

# Generar reporte JSON
generate_json_report() {
    local output_file="$1"
    local test_suite_name="${2:-Test Suite}"
    
    cat > "$output_file" << EOF
{
    "test_suite": "$test_suite_name",
    "timestamp": "$(date -u +%Y-%m-%dT%H:%M:%SZ)",
    "summary": {
        "total_tests": $GLOBAL_TESTS_TOTAL,
        "passed": $GLOBAL_TESTS_PASSED,
        "failed": $GLOBAL_TESTS_FAILED,
        "warnings": $GLOBAL_WARNINGS,
        "success_rate": $(( GLOBAL_TESTS_TOTAL > 0 ? GLOBAL_TESTS_PASSED * 100 / GLOBAL_TESTS_TOTAL : 0 ))
    },
    "environment": {
        "base_url": "$BASE_URL",
        "database_host": "$DB_HOST",
        "database_name": "$DB_NAME"
    },
    "log_file": "$LOG_FILE"
}
EOF
    
    log "INFO" "Reporte JSON generado: $output_file"
}

# ==========================================
# FUNCIONES DE VALIDACIÓN Y UTILIDAD
# ==========================================

# Validar formato de email
validate_email() {
    local email="$1"
    
    if [[ $email =~ ^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$ ]]; then
        return 0
    else
        return 1
    fi
}

# Validar formato de JWT token (básico)
validate_jwt_format() {
    local token="$1"
    
    # JWT tiene formato: header.payload.signature
    if [[ $token =~ ^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$ ]]; then
        return 0
    else
        return 1
    fi
}

# Generar email de test único
generate_test_email() {
    local prefix="${1:-test}"
    echo "${prefix}.$(date +%s).$(( RANDOM % 1000 ))@verification.test"
}

# Generar token de test único
generate_test_token() {
    echo "test_token_$(date +%s)_$(( RANDOM % 10000 ))"
}

# Esperar con countdown
wait_with_countdown() {
    local seconds="$1"
    local message="${2:-Esperando}"
    
    for ((i=seconds; i>0; i--)); do
        echo -ne "\r${message} ${i}s..."
        sleep 1
    done
    echo -e "\r${message} completado.                    "
}

# ==========================================
# FUNCIONES DE LIMPIEZA
# ==========================================

# Limpiar datos de test de la base de datos
cleanup_test_data() {
    log "INFO" "Limpiando datos de test de la base de datos..."
    
    local cleanup_queries=(
        "DELETE FROM password_reset_tokens WHERE email LIKE '%.verification.test';"
        "DELETE FROM authentication_attempts WHERE email LIKE '%.verification.test';"
        "DELETE FROM security_audit_logs WHERE email LIKE '%.verification.test';"
        "DELETE FROM password_reset_tokens WHERE token LIKE 'test_token_%';"
    )
    
    for query in "${cleanup_queries[@]}"; do
        execute_sql "$query" "Limpiar datos de test" "true"
    done
    
    log "SUCCESS" "Limpieza de datos de test completada"
}

# Función de cleanup al salir
cleanup_on_exit() {
    log "INFO" "Ejecutando limpieza al salir..."
    cleanup_test_data
    log "INFO" "Limpieza completada"
}

# ==========================================
# INICIALIZACIÓN
# ==========================================

# Configurar trap para cleanup
trap cleanup_on_exit EXIT

# Función para inicializar un script de verificación
initialize_verification_script() {
    local script_name="$1"
    local log_prefix="${2:-verification}"
    
    # Configurar archivo de log
    export LOG_FILE="/tmp/${log_prefix}_$(date +%Y%m%d_%H%M%S).log"
    
    # Resetear contadores globales
    export GLOBAL_TESTS_TOTAL=0
    export GLOBAL_TESTS_PASSED=0
    export GLOBAL_TESTS_FAILED=0
    export GLOBAL_WARNINGS=0
    
    # Imprimir header
    print_section_header "$script_name" "$ROCKET"
    log "INFO" "Iniciando $script_name"
    log "INFO" "Log file: $LOG_FILE"
    log "INFO" "Base URL: $BASE_URL"
    log "INFO" "Timestamp: $(date)"
    
    # Verificar prerequisitos básicos
    check_prerequisites
}

# ==========================================
# EXPORTAR FUNCIONES
# ==========================================

# Hacer que todas las funciones estén disponibles para otros scripts
export -f log print_section_header print_test_result
export -f check_prerequisites check_application_connectivity check_database_connectivity
export -f http_request get_status_code
export -f execute_sql table_exists get_table_row_count
export -f run_test skip_test
export -f generate_summary generate_json_report
export -f validate_email validate_jwt_format generate_test_email generate_test_token
export -f wait_with_countdown cleanup_test_data cleanup_on_exit
export -f initialize_verification_script

log "INFO" "Funciones comunes cargadas exitosamente"
