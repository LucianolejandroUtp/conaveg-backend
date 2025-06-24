#!/bin/bash
# database_verification.sh
# Script para verificar integridad y funcionamiento de la base de datos

# Configuración
DB_HOST="localhost"
DB_USER="root"
DB_NAME="cona_db"
LOG_FILE="/tmp/db_verification_$(date +%Y%m%d_%H%M%S).log"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Variables
TESTS_PASSED=0
TESTS_FAILED=0
TESTS_TOTAL=0

# Función de logging
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') - $1" | tee -a "$LOG_FILE"
}

# Función para ejecutar test
run_test() {
    local test_name="$1"
    local test_function="$2"
    
    TESTS_TOTAL=$((TESTS_TOTAL + 1))
    log "${BLUE}🧪 Ejecutando: $test_name${NC}"
    
    if $test_function; then
        TESTS_PASSED=$((TESTS_PASSED + 1))
        log "${GREEN}✅ PASS: $test_name${NC}"
        return 0
    else
        TESTS_FAILED=$((TESTS_FAILED + 1))
        log "${RED}❌ FAIL: $test_name${NC}"
        return 1
    fi
}

# Función para ejecutar SQL con manejo de errores
execute_sql() {
    local query="$1"
    local description="$2"
    
    if mysql -h "$DB_HOST" -u "$DB_USER" -e "$query" "$DB_NAME" 2>/dev/null; then
        log "SQL exitoso: $description"
        return 0
    else
        log "SQL falló: $description - Query: $query"
        return 1
    fi
}

# Test 1: Verificar conexión a base de datos
test_database_connection() {
    log "Verificando conexión a base de datos $DB_NAME en $DB_HOST"
    
    if mysql -h "$DB_HOST" -u "$DB_USER" -e "SELECT 1;" "$DB_NAME" &>/dev/null; then
        log "Conexión a base de datos exitosa"
        return 0
    else
        log "No se pudo conectar a la base de datos"
        return 1
    fi
}

# Test 2: Verificar existencia de tablas críticas
test_critical_tables_exist() {
    local tables=("empleados" "security_audit_logs" "password_reset_tokens" "authentication_attempts")
    local all_tables_exist=true
    
    log "Verificando existencia de tablas críticas"
    
    for table in "${tables[@]}"; do
        if execute_sql "DESCRIBE $table;" "Verificar tabla $table"; then
            log "Tabla $table existe"
        else
            log "Tabla $table no existe"
            all_tables_exist=false
        fi
    done
    
    if [ "$all_tables_exist" = true ]; then
        return 0
    else
        return 1
    fi
}

# Test 3: Verificar estructura de tabla security_audit_logs
test_security_audit_logs_structure() {
    log "Verificando estructura de tabla security_audit_logs"
    
    local required_columns=("id" "event_type" "email" "ip_address" "timestamp" "severity" "success")
    local all_columns_exist=true
    
    for column in "${required_columns[@]}"; do
        if mysql -h "$DB_HOST" -u "$DB_USER" -e "SELECT $column FROM security_audit_logs LIMIT 1;" "$DB_NAME" &>/dev/null; then
            log "Columna $column existe en security_audit_logs"
        else
            log "Columna $column no existe en security_audit_logs"
            all_columns_exist=false
        fi
    done
    
    return $([[ "$all_columns_exist" = true ]] && echo 0 || echo 1)
}

# Test 4: Verificar estructura de tabla password_reset_tokens
test_password_reset_tokens_structure() {
    log "Verificando estructura de tabla password_reset_tokens"
    
    local required_columns=("id" "email" "token" "expires_at" "created_at" "used")
    local all_columns_exist=true
    
    for column in "${required_columns[@]}"; do
        if mysql -h "$DB_HOST" -u "$DB_USER" -e "SELECT $column FROM password_reset_tokens LIMIT 1;" "$DB_NAME" &>/dev/null; then
            log "Columna $column existe en password_reset_tokens"
        else
            log "Columna $column no existe en password_reset_tokens"
            all_columns_exist=false
        fi
    done
    
    return $([[ "$all_columns_exist" = true ]] && echo 0 || echo 1)
}

# Test 5: Verificar estructura de tabla authentication_attempts
test_authentication_attempts_structure() {
    log "Verificando estructura de tabla authentication_attempts"
    
    local required_columns=("id" "email" "ip_address" "attempt_time" "successful")
    local all_columns_exist=true
    
    for column in "${required_columns[@]}"; do
        if mysql -h "$DB_HOST" -u "$DB_USER" -e "SELECT $column FROM authentication_attempts LIMIT 1;" "$DB_NAME" &>/dev/null; then
            log "Columna $column existe en authentication_attempts"
        else
            log "Columna $column no existe en authentication_attempts"
            all_columns_exist=false
        fi
    done
    
    return $([[ "$all_columns_exist" = true ]] && echo 0 || echo 1)
}

# Test 6: Verificar índices para performance
test_database_indexes() {
    log "Verificando índices de base de datos para performance"
    
    local important_indexes=(
        "authentication_attempts:ip_address"
        "authentication_attempts:email"
        "authentication_attempts:attempt_time"
        "security_audit_logs:timestamp"
        "security_audit_logs:event_type"
        "password_reset_tokens:token"
        "password_reset_tokens:email"
    )
    
    local indexes_found=0
    local total_indexes=${#important_indexes[@]}
    
    for index_info in "${important_indexes[@]}"; do
        local table=$(echo "$index_info" | cut -d':' -f1)
        local column=$(echo "$index_info" | cut -d':' -f2)
        
        if mysql -h "$DB_HOST" -u "$DB_USER" -e "SHOW INDEX FROM $table WHERE Column_name='$column';" "$DB_NAME" | grep -q "$column"; then
            log "Índice encontrado: $table.$column"
            indexes_found=$((indexes_found + 1))
        else
            log "Índice faltante: $table.$column"
        fi
    done
    
    log "Índices encontrados: $indexes_found/$total_indexes"
    
    if [ $indexes_found -ge $((total_indexes / 2)) ]; then
        log "Suficientes índices presentes para performance básica"
        return 0
    else
        log "Faltan índices críticos para performance"
        return 1
    fi
}

# Test 7: Verificar capacidad de inserción en security_audit_logs
test_insert_security_audit_log() {
    log "Verificando capacidad de inserción en security_audit_logs"
    
    local test_query="INSERT INTO security_audit_logs (event_type, email, ip_address, timestamp, severity, success) VALUES ('TEST_EVENT', 'test@verify.com', '127.0.0.1', NOW(), 'LOW', true);"
    
    if execute_sql "$test_query" "Insertar log de prueba"; then
        # Limpiar el registro de prueba
        execute_sql "DELETE FROM security_audit_logs WHERE event_type='TEST_EVENT' AND email='test@verify.com';" "Limpiar registro de prueba"
        return 0
    else
        return 1
    fi
}

# Test 8: Verificar capacidad de inserción en password_reset_tokens
test_insert_password_reset_token() {
    log "Verificando capacidad de inserción en password_reset_tokens"
    
    local test_token="test_token_$(date +%s)"
    local test_query="INSERT INTO password_reset_tokens (email, token, expires_at, created_at, used) VALUES ('test@verify.com', '$test_token', DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), false);"
    
    if execute_sql "$test_query" "Insertar token de prueba"; then
        # Limpiar el registro de prueba
        execute_sql "DELETE FROM password_reset_tokens WHERE token='$test_token';" "Limpiar token de prueba"
        return 0
    else
        return 1
    fi
}

# Test 9: Verificar capacidad de inserción en authentication_attempts
test_insert_authentication_attempt() {
    log "Verificando capacidad de inserción en authentication_attempts"
    
    local test_query="INSERT INTO authentication_attempts (email, ip_address, attempt_time, successful) VALUES ('test@verify.com', '127.0.0.1', NOW(), false);"
    
    if execute_sql "$test_query" "Insertar intento de prueba"; then
        # Limpiar el registro de prueba
        execute_sql "DELETE FROM authentication_attempts WHERE email='test@verify.com' AND ip_address='127.0.0.1';" "Limpiar intento de prueba"
        return 0
    else
        return 1
    fi
}

# Test 10: Verificar rendimiento de consultas críticas
test_query_performance() {
    log "Verificando rendimiento de consultas críticas"
    
    local queries=(
        "SELECT COUNT(*) FROM authentication_attempts WHERE ip_address='127.0.0.1' AND attempt_time > NOW() - INTERVAL 1 HOUR;"
        "SELECT COUNT(*) FROM authentication_attempts WHERE email='test@test.com' AND attempt_time > NOW() - INTERVAL 1 HOUR;"
        "SELECT * FROM password_reset_tokens WHERE token='nonexistent' LIMIT 1;"
        "SELECT COUNT(*) FROM security_audit_logs WHERE timestamp > NOW() - INTERVAL 1 HOUR;"
    )
    
    local fast_queries=0
    local total_queries=${#queries[@]}
    
    for query in "${queries[@]}"; do
        local start_time=$(date +%s%N)
        
        if mysql -h "$DB_HOST" -u "$DB_USER" -e "$query" "$DB_NAME" &>/dev/null; then
            local end_time=$(date +%s%N)
            local duration_ms=$(( (end_time - start_time) / 1000000 ))
            
            log "Query ejecutada en ${duration_ms}ms"
            
            if [ $duration_ms -lt 1000 ]; then
                fast_queries=$((fast_queries + 1))
                log "Query rápida: ${duration_ms}ms"
            else
                log "Query lenta: ${duration_ms}ms"
            fi
        else
            log "Query falló"
        fi
    done
    
    log "Queries rápidas: $fast_queries/$total_queries"
    
    if [ $fast_queries -ge $((total_queries / 2)) ]; then
        return 0
    else
        return 1
    fi
}

# Test 11: Verificar limpieza de datos antiguos
test_old_data_cleanup() {
    log "Verificando capacidad de limpieza de datos antiguos"
    
    # Insertar datos de prueba antiguos
    local old_date="DATE_SUB(NOW(), INTERVAL 60 DAY)"
    local test_queries=(
        "INSERT INTO password_reset_tokens (email, token, expires_at, created_at, used) VALUES ('old@test.com', 'old_token_test', $old_date, $old_date, false);"
        "INSERT INTO authentication_attempts (email, ip_address, attempt_time, successful) VALUES ('old@test.com', '192.168.1.1', $old_date, false);"
        "INSERT INTO security_audit_logs (event_type, email, ip_address, timestamp, severity, success) VALUES ('OLD_TEST_EVENT', 'old@test.com', '192.168.1.1', $old_date, 'LOW', true);"
    )
    
    # Insertar datos de prueba
    for query in "${test_queries[@]}"; do
        execute_sql "$query" "Insertar datos antiguos de prueba"
    done
    
    # Verificar que se pueden eliminar
    local cleanup_queries=(
        "DELETE FROM password_reset_tokens WHERE email='old@test.com' AND token='old_token_test';"
        "DELETE FROM authentication_attempts WHERE email='old@test.com' AND ip_address='192.168.1.1';"
        "DELETE FROM security_audit_logs WHERE event_type='OLD_TEST_EVENT' AND email='old@test.com';"
    )
    
    local successful_cleanups=0
    for query in "${cleanup_queries[@]}"; do
        if execute_sql "$query" "Limpiar datos antiguos"; then
            successful_cleanups=$((successful_cleanups + 1))
        fi
    done
    
    if [ $successful_cleanups -eq ${#cleanup_queries[@]} ]; then
        log "Limpieza de datos antiguos funciona correctamente"
        return 0
    else
        log "Problemas con limpieza de datos antiguos"
        return 1
    fi
}

# Test 12: Verificar constraints y validaciones
test_database_constraints() {
    log "Verificando constraints y validaciones de base de datos"
    
    local constraint_tests=0
    local total_constraint_tests=3
    
    # Test 1: Intentar insertar token duplicado
    local unique_token="unique_test_token_$(date +%s)"
    if execute_sql "INSERT INTO password_reset_tokens (email, token, expires_at, created_at, used) VALUES ('test1@test.com', '$unique_token', DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), false);" "Insertar primer token"; then
        # Intentar insertar token duplicado (debería fallar)
        if ! execute_sql "INSERT INTO password_reset_tokens (email, token, expires_at, created_at, used) VALUES ('test2@test.com', '$unique_token', DATE_ADD(NOW(), INTERVAL 1 HOUR), NOW(), false);" "Insertar token duplicado"; then
            log "Constraint de token único funcionando"
            constraint_tests=$((constraint_tests + 1))
        fi
        # Limpiar
        execute_sql "DELETE FROM password_reset_tokens WHERE token='$unique_token';" "Limpiar tokens de prueba"
    fi
    
    # Test 2: Verificar campos NOT NULL
    if ! execute_sql "INSERT INTO security_audit_logs (event_type, email, timestamp) VALUES (NULL, 'test@test.com', NOW());" "Insertar con event_type NULL"; then
        log "Constraint NOT NULL en event_type funcionando"
        constraint_tests=$((constraint_tests + 1))
    fi
    
    # Test 3: Verificar tipos de datos
    if ! execute_sql "INSERT INTO authentication_attempts (email, ip_address, attempt_time, successful) VALUES ('test@test.com', '127.0.0.1', 'invalid_date', false);" "Insertar con fecha inválida"; then
        log "Validación de tipo de dato en attempt_time funcionando"
        constraint_tests=$((constraint_tests + 1))
    fi
    
    log "Constraints funcionando: $constraint_tests/$total_constraint_tests"
    
    if [ $constraint_tests -ge 2 ]; then
        return 0
    else
        return 1
    fi
}

# Test 13: Verificar tamaño y crecimiento de tablas
test_table_sizes() {
    log "Verificando tamaños de tablas y estadísticas"
    
    local tables=("empleados" "security_audit_logs" "password_reset_tokens" "authentication_attempts")
    
    for table in "${tables[@]}"; do
        local row_count=$(mysql -h "$DB_HOST" -u "$DB_USER" -se "SELECT COUNT(*) FROM $table;" "$DB_NAME" 2>/dev/null)
        local table_size=$(mysql -h "$DB_HOST" -u "$DB_USER" -se "SELECT ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)' FROM information_schema.TABLES WHERE table_schema = '$DB_NAME' AND table_name = '$table';" 2>/dev/null)
        
        if [ -n "$row_count" ] && [ -n "$table_size" ]; then
            log "Tabla $table: $row_count filas, ${table_size}MB"
        else
            log "No se pudieron obtener estadísticas para tabla $table"
        fi
    done
    
    return 0  # Este test es informativo
}

# Función principal
main() {
    echo "🗄️ Iniciando verificación completa de Base de Datos..."
    echo "Host: $DB_HOST"
    echo "Database: $DB_NAME"
    echo "User: $DB_USER"
    echo "Log file: $LOG_FILE"
    echo ""
    
    # Verificar que mysql esté disponible
    if ! command -v mysql &> /dev/null; then
        echo "❌ Error: mysql client no está instalado"
        exit 1
    fi
    
    # Verificar acceso básico
    if ! mysql -h "$DB_HOST" -u "$DB_USER" -e "SELECT 1;" &>/dev/null; then
        echo "❌ Error: No se puede conectar a la base de datos. Verifique credenciales."
        echo "Intente: mysql -h $DB_HOST -u $DB_USER -p"
        exit 1
    fi
    
    # Ejecutar todos los tests
    run_test "Conexión a base de datos" test_database_connection
    run_test "Existencia de tablas críticas" test_critical_tables_exist
    run_test "Estructura de security_audit_logs" test_security_audit_logs_structure
    run_test "Estructura de password_reset_tokens" test_password_reset_tokens_structure
    run_test "Estructura de authentication_attempts" test_authentication_attempts_structure
    run_test "Índices de base de datos" test_database_indexes
    run_test "Inserción en security_audit_logs" test_insert_security_audit_log
    run_test "Inserción en password_reset_tokens" test_insert_password_reset_token
    run_test "Inserción en authentication_attempts" test_insert_authentication_attempt
    run_test "Rendimiento de consultas" test_query_performance
    run_test "Limpieza de datos antiguos" test_old_data_cleanup
    run_test "Constraints de base de datos" test_database_constraints
    run_test "Tamaños de tablas" test_table_sizes
    
    # Mostrar resumen
    echo ""
    log "${BLUE}📊 RESUMEN DE VERIFICACIÓN DE BASE DE DATOS${NC}"
    log "Total de tests: $TESTS_TOTAL"
    log "${GREEN}Tests exitosos: $TESTS_PASSED${NC}"
    log "${RED}Tests fallidos: $TESTS_FAILED${NC}"
    log "Tasa de éxito: $(( TESTS_PASSED * 100 / TESTS_TOTAL ))%"
    log ""
    log "Log completo guardado en: $LOG_FILE"
    
    # Determinar código de salida
    if [ $TESTS_FAILED -eq 0 ]; then
        log "${GREEN}🎉 Todos los tests de base de datos pasaron exitosamente${NC}"
        exit 0
    else
        log "${RED}⚠️ Algunos tests de base de datos fallaron${NC}"
        exit 1
    fi
}

# Ejecutar si se llama directamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
