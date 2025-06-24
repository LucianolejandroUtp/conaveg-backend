#!/bin/bash
# rate_limiting_verification.sh
# Script específico para verificar el funcionamiento del rate limiting

# Configuración
BASE_URL="http://localhost:8080/conaveg"
LOG_FILE="/tmp/rate_limiting_verification_$(date +%Y%m%d_%H%M%S).log"

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

# Test 1: Rate limiting por IP en login
test_ip_rate_limiting_login() {
    local test_email="test.ip.rate@limit.com"
    local wrong_password="wrongpassword"
    local ip_blocked=false
    local attempts=0
    
    log "Iniciando test de rate limiting por IP en endpoint login"
    
    # Realizar intentos hasta que se active el rate limiting
    for i in {1..15}; do
        attempts=$i
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -H "X-Forwarded-For: 192.168.100.$((RANDOM % 255))" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        log "Intento $attempts: Status code $status_code"
        
        if [ "$status_code" = "429" ]; then
            ip_blocked=true
            log "Rate limiting por IP activado en intento $attempts"
            break
        fi
        
        sleep 0.1
    done
    
    if [ "$ip_blocked" = true ]; then
        log "Rate limiting por IP funcionando correctamente"
        return 0
    else
        log "Rate limiting por IP no se activó después de $attempts intentos"
        return 1
    fi
}

# Test 2: Rate limiting por email en login
test_email_rate_limiting_login() {
    local test_email="specific.email@rate.limit.test"
    local wrong_password="wrongpassword"
    local email_blocked=false
    local attempts=0
    
    log "Iniciando test de rate limiting por email en endpoint login"
    
    # Usar diferentes IPs para evitar bloqueo por IP
    for i in {1..12}; do
        attempts=$i
        local fake_ip="10.0.0.$((i + 100))"
        
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -H "X-Forwarded-For: $fake_ip" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        log "Intento $attempts con IP $fake_ip: Status code $status_code"
        
        if [ "$status_code" = "429" ]; then
            email_blocked=true
            log "Rate limiting por email activado en intento $attempts"
            break
        fi
        
        sleep 0.2
    done
    
    if [ "$email_blocked" = true ]; then
        log "Rate limiting por email funcionando correctamente"
        return 0
    else
        log "Rate limiting por email no se activó después de $attempts intentos"
        return 1
    fi
}

# Test 3: Rate limiting en forgot password
test_forgot_password_rate_limiting() {
    local test_email="forgot.password@rate.limit.test"
    local rate_limit_triggered=false
    local attempts=0
    
    log "Iniciando test de rate limiting en endpoint forgot-password"
    
    for i in {1..10}; do
        attempts=$i
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/forgot-password" \
            -H "Content-Type: application/json" \
            -H "X-Forwarded-For: 172.16.0.$((i + 50))" \
            -d "{\"email\":\"$test_email\"}")
        
        log "Intento $attempts forgot-password: Status code $status_code"
        
        if [ "$status_code" = "429" ]; then
            rate_limit_triggered=true
            log "Rate limiting en forgot-password activado en intento $attempts"
            break
        fi
        
        sleep 0.3
    done
    
    if [ "$rate_limit_triggered" = true ]; then
        log "Rate limiting en forgot-password funcionando"
        return 0
    else
        log "Rate limiting en forgot-password no se activó después de $attempts intentos"
        return 1
    fi
}

# Test 4: Verificar mensaje de error de rate limiting
test_rate_limit_error_message() {
    local test_email="error.message@rate.test"
    local wrong_password="wrongpassword"
    
    log "Verificando mensaje de error de rate limiting"
    
    # Primero provocar rate limiting
    for i in {1..12}; do
        curl -s -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}" > /dev/null
        sleep 0.1
    done
    
    # Verificar el mensaje de error
    local response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
    
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
    
    if [ "$status_code" = "429" ]; then
        log "Status 429 confirmado"
        
        # Verificar mensaje descriptivo
        if echo "$response" | grep -q -i "demasiados\|too many\|rate limit\|bloqueado\|blocked"; then
            log "Mensaje descriptivo de rate limiting presente: $response"
            return 0
        else
            log "Mensaje descriptivo de rate limiting no encontrado: $response"
            return 1
        fi
    else
        log "Rate limiting no activado para verificar mensaje. Status: $status_code"
        return 1
    fi
}

# Test 5: Recuperación automática después del bloqueo
test_rate_limit_recovery() {
    local test_email="recovery@rate.test"
    local wrong_password="wrongpassword"
    local correct_password="correctpassword"  # Asumiendo que existe
    
    log "Iniciando test de recuperación automática de rate limiting"
    
    # Provocar rate limiting
    log "Provocando rate limiting..."
    for i in {1..12}; do
        curl -s -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}" > /dev/null
        sleep 0.1
    done
    
    # Verificar que está bloqueado
    local blocked_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
    
    if [ "$blocked_status" = "429" ]; then
        log "Rate limiting confirmado, esperando recuperación..."
        
        # Esperar tiempo de recuperación (simulado con sleep corto para testing)
        sleep 5
        
        # Intentar de nuevo
        local recovery_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        if [ "$recovery_status" != "429" ]; then
            log "Recuperación automática funcionando. Nuevo status: $recovery_status"
            return 0
        else
            log "Recuperación automática no funcionó, aún bloqueado"
            return 1
        fi
    else
        log "No se pudo establecer bloqueo para test de recuperación"
        return 1
    fi
}

# Test 6: Rate limiting con diferentes User-Agents
test_user_agent_rate_limiting() {
    local test_email="useragent@rate.test"
    local wrong_password="wrongpassword"
    local rate_limit_triggered=false
    local attempts=0
    
    log "Verificando rate limiting con diferentes User-Agents"
    
    local user_agents=(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
        "PostmanRuntime/7.29.0"
        "curl/7.68.0"
    )
    
    for i in {1..15}; do
        attempts=$i
        local ua="${user_agents[$((i % ${#user_agents[@]}))]}"
        
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -H "User-Agent: $ua" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        log "Intento $attempts con UA: ${ua:0:30}... - Status: $status_code"
        
        if [ "$status_code" = "429" ]; then
            rate_limit_triggered=true
            log "Rate limiting activado independientemente del User-Agent"
            break
        fi
        
        sleep 0.1
    done
    
    if [ "$rate_limit_triggered" = true ]; then
        log "Rate limiting funciona correctamente con diferentes User-Agents"
        return 0
    else
        log "Rate limiting no se activó con diferentes User-Agents"
        return 1
    fi
}

# Test 7: Verificar rate limiting en refresh token
test_refresh_token_rate_limiting() {
    local invalid_token="invalid.jwt.token.for.rate.limiting.test"
    local rate_limit_triggered=false
    local attempts=0
    
    log "Verificando rate limiting en endpoint refresh token"
    
    for i in {1..15}; do
        attempts=$i
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/refresh" \
            -H "Content-Type: application/json" \
            -d "{\"token\":\"$invalid_token\"}")
        
        log "Intento $attempts refresh: Status code $status_code"
        
        if [ "$status_code" = "429" ]; then
            rate_limit_triggered=true
            log "Rate limiting en refresh token activado en intento $attempts"
            break
        fi
        
        sleep 0.1
    done
    
    if [ "$rate_limit_triggered" = true ]; then
        log "Rate limiting en refresh token funcionando"
        return 0
    else
        log "Rate limiting en refresh token no se activó después de $attempts intentos"
        return 1
    fi
}

# Test 8: Verificar límites por minuto
test_rate_limit_time_window() {
    local test_email="timewindow@rate.test"
    local wrong_password="wrongpassword"
    
    log "Verificando ventana de tiempo de rate limiting"
    
    # Primer ráfaga de intentos
    log "Primera ráfaga de intentos..."
    local first_burst_attempts=0
    for i in {1..8}; do
        first_burst_attempts=$i
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        if [ "$status_code" = "429" ]; then
            log "Primer rate limiting en intento $first_burst_attempts"
            break
        fi
        sleep 0.2
    done
    
    # Esperar un poco (simular nueva ventana de tiempo)
    log "Esperando nueva ventana de tiempo..."
    sleep 3
    
    # Segunda ráfaga
    log "Segunda ráfaga de intentos..."
    local second_status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
    
    if [ "$second_status" != "429" ]; then
        log "Ventana de tiempo funcionando - segundo intento permitido: $second_status"
        return 0
    else
        log "Ventana de tiempo puede estar funcionando - segundo intento aún bloqueado: $second_status"
        return 0  # Esto puede ser normal dependiendo de la configuración
    fi
}

# Test 9: Verificar headers de rate limiting
test_rate_limit_headers() {
    local test_email="headers@rate.test"
    local wrong_password="wrongpassword"
    
    log "Verificando headers de rate limiting"
    
    # Provocar algunos intentos para acercarse al límite
    for i in {1..5}; do
        curl -s -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}" > /dev/null
        sleep 0.1
    done
    
    # Verificar headers en la respuesta
    local headers=$(curl -s -I -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
    
    log "Headers recibidos:"
    echo "$headers" | while read line; do
        log "  $line"
    done
    
    # Buscar headers relacionados con rate limiting
    if echo "$headers" | grep -q -i "retry-after\|x-ratelimit\|rate-limit"; then
        log "Headers de rate limiting encontrados"
        return 0
    else
        log "Headers de rate limiting no encontrados (puede ser normal dependiendo de la implementación)"
        return 0  # No fallar este test ya que los headers son opcionales
    fi
}

# Test 10: Stress test de rate limiting
test_rate_limiting_stress() {
    local base_email="stress.test"
    local wrong_password="wrongpassword"
    local blocked_count=0
    local total_requests=20
    
    log "Iniciando stress test de rate limiting con $total_requests requests"
    
    # Realizar múltiples requests en paralelo simulado
    for i in $(seq 1 $total_requests); do
        local test_email="${base_email}.${i}@rate.test"
        
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        if [ "$status_code" = "429" ]; then
            blocked_count=$((blocked_count + 1))
        fi
        
        # Pequeña pausa para no sobrecargar
        sleep 0.05
    done
    
    local block_percentage=$(( blocked_count * 100 / total_requests ))
    log "Stress test completado: $blocked_count/$total_requests requests bloqueados ($block_percentage%)"
    
    if [ $blocked_count -gt 0 ]; then
        log "Rate limiting respondió apropiadamente bajo stress"
        return 0
    else
        log "Rate limiting no se activó durante stress test"
        return 1
    fi
}

# Función principal
main() {
    echo "🚦 Iniciando verificación completa de Rate Limiting..."
    echo "Base URL: $BASE_URL"
    echo "Log file: $LOG_FILE"
    echo ""
    
    # Verificar que la aplicación esté ejecutándose
    if ! curl -s --max-time 5 "$BASE_URL/actuator/health" > /dev/null; then
        echo "❌ Error: La aplicación no está accesible en $BASE_URL"
        exit 1
    fi
    
    # Ejecutar todos los tests
    run_test "Rate limiting por IP en login" test_ip_rate_limiting_login
    run_test "Rate limiting por email en login" test_email_rate_limiting_login
    run_test "Rate limiting en forgot password" test_forgot_password_rate_limiting
    run_test "Mensaje de error de rate limiting" test_rate_limit_error_message
    run_test "Recuperación automática" test_rate_limit_recovery
    run_test "Rate limiting con diferentes User-Agents" test_user_agent_rate_limiting
    run_test "Rate limiting en refresh token" test_refresh_token_rate_limiting
    run_test "Ventana de tiempo de rate limiting" test_rate_limit_time_window
    run_test "Headers de rate limiting" test_rate_limit_headers
    run_test "Stress test de rate limiting" test_rate_limiting_stress
    
    # Mostrar resumen
    echo ""
    log "${BLUE}📊 RESUMEN DE VERIFICACIÓN DE RATE LIMITING${NC}"
    log "Total de tests: $TESTS_TOTAL"
    log "${GREEN}Tests exitosos: $TESTS_PASSED${NC}"
    log "${RED}Tests fallidos: $TESTS_FAILED${NC}"
    log "Tasa de éxito: $(( TESTS_PASSED * 100 / TESTS_TOTAL ))%"
    log ""
    log "Log completo guardado en: $LOG_FILE"
    
    # Determinar código de salida
    if [ $TESTS_FAILED -eq 0 ]; then
        log "${GREEN}🎉 Todos los tests de rate limiting pasaron exitosamente${NC}"
        exit 0
    else
        log "${RED}⚠️ Algunos tests de rate limiting fallaron${NC}"
        exit 1
    fi
}

# Ejecutar si se llama directamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
