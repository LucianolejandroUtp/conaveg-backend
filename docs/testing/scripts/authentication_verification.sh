#!/bin/bash
# authentication_verification.sh
# Script específico para verificar todas las funcionalidades de autenticación

# Configuración
BASE_URL="http://localhost:8080/conaveg"
LOG_FILE="/tmp/auth_verification_$(date +%Y%m%d_%H%M%S).log"

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Variables globales para tracking
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

# Test 1: Login con credenciales válidas
test_valid_login() {
    local email="admin@conaveg.com"
    local password="admin123"
    
    local response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\"}")
    
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\"}")
    
    if [ "$status_code" = "200" ]; then
        log "Login exitoso con credenciales válidas"
        
        # Verificar que la respuesta contiene un token
        if echo "$response" | grep -q "token"; then
            log "Token JWT presente en la respuesta"
            return 0
        else
            log "Token JWT no encontrado en la respuesta"
            return 1
        fi
    else
        log "Login falló con credenciales válidas. Status: $status_code"
        return 1
    fi
}

# Test 2: Login con credenciales inválidas
test_invalid_login() {
    local email="invalid@test.com"
    local password="wrongpassword"
    
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\",\"password\":\"$password\"}")
    
    if [ "$status_code" = "401" ] || [ "$status_code" = "400" ]; then
        log "Login correctamente rechazado con credenciales inválidas. Status: $status_code"
        return 0
    else
        log "Login no fue rechazado apropiadamente. Status: $status_code"
        return 1
    fi
}

# Test 3: Solicitud de recuperación de contraseña
test_forgot_password_request() {
    local email="test@conaveg.com"
    
    local response=$(curl -s -X POST "$BASE_URL/api/auth/forgot-password" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\"}")
    
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/forgot-password" \
        -H "Content-Type: application/json" \
        -d "{\"email\":\"$email\"}")
    
    if [ "$status_code" = "200" ]; then
        log "Solicitud de recuperación de contraseña enviada exitosamente"
        
        # Verificar mensaje en la respuesta
        if echo "$response" | grep -q "enviado"; then
            log "Mensaje de confirmación presente en la respuesta"
            return 0
        else
            log "Mensaje de confirmación no encontrado"
            return 1
        fi
    else
        log "Solicitud de recuperación falló. Status: $status_code, Response: $response"
        return 1
    fi
}

# Test 4: Validación de token inválido
test_validate_invalid_token() {
    local invalid_token="invalid_token_12345"
    
    local response=$(curl -s -X GET "$BASE_URL/api/auth/validate-reset-token?token=$invalid_token")
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/api/auth/validate-reset-token?token=$invalid_token")
    
    if [ "$status_code" = "400" ] || [ "$status_code" = "404" ]; then
        log "Token inválido correctamente rechazado. Status: $status_code"
        
        # Verificar que indica token inválido
        if echo "$response" | grep -q -i "invalid\|inválido\|not found"; then
            log "Mensaje de token inválido presente"
            return 0
        else
            log "Mensaje de token inválido no encontrado"
            return 1
        fi
    else
        log "Token inválido no fue rechazado apropiadamente. Status: $status_code"
        return 1
    fi
}

# Test 5: Reset de contraseña con token inválido
test_reset_password_invalid_token() {
    local invalid_token="invalid_token_12345"
    local new_password="NewPassword123!"
    
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/reset-password" \
        -H "Content-Type: application/json" \
        -d "{\"token\":\"$invalid_token\",\"newPassword\":\"$new_password\",\"confirmPassword\":\"$new_password\"}")
    
    if [ "$status_code" = "400" ] || [ "$status_code" = "404" ]; then
        log "Reset de contraseña con token inválido correctamente rechazado. Status: $status_code"
        return 0
    else
        log "Reset de contraseña con token inválido no fue rechazado. Status: $status_code"
        return 1
    fi
}

# Test 6: Refresh token con token inválido
test_refresh_invalid_token() {
    local invalid_token="invalid.jwt.token"
    
    local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/refresh" \
        -H "Content-Type: application/json" \
        -d "{\"token\":\"$invalid_token\"}")
    
    if [ "$status_code" = "400" ] || [ "$status_code" = "401" ]; then
        log "Refresh con token inválido correctamente rechazado. Status: $status_code"
        return 0
    else
        log "Refresh con token inválido no fue rechazado. Status: $status_code"
        return 1
    fi
}

# Test 7: Verificar rate limiting en login
test_login_rate_limiting() {
    local test_email="ratelimit@test.com"
    local wrong_password="wrongpassword"
    local rate_limit_triggered=false
    local attempts=0
    
    log "Iniciando test de rate limiting con $test_email"
    
    # Realizar múltiples intentos fallidos
    for i in {1..12}; do
        attempts=$i
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\",\"password\":\"$wrong_password\"}")
        
        if [ "$status_code" = "429" ]; then
            rate_limit_triggered=true
            log "Rate limiting activado en intento $attempts con status 429"
            break
        fi
        
        # Pequeña pausa entre intentos
        sleep 0.1
    done
    
    if [ "$rate_limit_triggered" = true ]; then
        log "Rate limiting funcionando correctamente"
        return 0
    else
        log "Rate limiting no se activó después de $attempts intentos"
        return 1
    fi
}

# Test 8: Verificar rate limiting en forgot password
test_forgot_password_rate_limiting() {
    local test_email="ratelimit.forgot@test.com"
    local rate_limit_triggered=false
    local attempts=0
    
    log "Iniciando test de rate limiting para forgot password con $test_email"
    
    # Realizar múltiples solicitudes
    for i in {1..8}; do
        attempts=$i
        local status_code=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/forgot-password" \
            -H "Content-Type: application/json" \
            -d "{\"email\":\"$test_email\"}")
        
        if [ "$status_code" = "429" ]; then
            rate_limit_triggered=true
            log "Rate limiting para forgot password activado en intento $attempts"
            break
        fi
        
        sleep 0.2
    done
    
    if [ "$rate_limit_triggered" = true ]; then
        log "Rate limiting para forgot password funcionando"
        return 0
    else
        log "Rate limiting para forgot password no se activó después de $attempts intentos"
        return 1
    fi
}

# Test 9: Verificar headers de seguridad
test_security_headers() {
    local headers=$(curl -s -I "$BASE_URL/api/auth/login" | tr -d '\r')
    
    local required_headers=("X-Content-Type-Options" "X-Frame-Options" "X-XSS-Protection")
    local headers_found=0
    
    for header in "${required_headers[@]}"; do
        if echo "$headers" | grep -q "$header"; then
            log "Header de seguridad encontrado: $header"
            headers_found=$((headers_found + 1))
        else
            log "Header de seguridad faltante: $header"
        fi
    done
    
    if [ $headers_found -ge 2 ]; then
        log "Suficientes headers de seguridad presentes ($headers_found/3)"
        return 0
    else
        log "Insuficientes headers de seguridad ($headers_found/3)"
        return 1
    fi
}

# Test 10: Verificar formato de respuesta JSON
test_json_response_format() {
    local response=$(curl -s -X POST "$BASE_URL/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{"email":"test@test.com","password":"wrong"}')
    
    # Verificar que la respuesta es JSON válido
    if echo "$response" | jq . > /dev/null 2>&1; then
        log "Respuesta en formato JSON válido"
        
        # Verificar estructura básica de respuesta de error
        if echo "$response" | jq -e '.error' > /dev/null 2>&1; then
            log "Estructura de respuesta de error presente"
            return 0
        else
            log "Estructura de respuesta de error no encontrada"
            return 1
        fi
    else
        log "Respuesta no es JSON válido: $response"
        return 1
    fi
}

# Función principal
main() {
    echo "🔐 Iniciando verificación completa de autenticación..."
    echo "Base URL: $BASE_URL"
    echo "Log file: $LOG_FILE"
    echo ""
    
    # Verificar que la aplicación esté ejecutándose
    if ! curl -s --max-time 5 "$BASE_URL/actuator/health" > /dev/null; then
        echo "❌ Error: La aplicación no está accesible en $BASE_URL"
        exit 1
    fi
    
    # Ejecutar todos los tests
    run_test "Login con credenciales válidas" test_valid_login
    run_test "Login con credenciales inválidas" test_invalid_login
    run_test "Solicitud de recuperación de contraseña" test_forgot_password_request
    run_test "Validación de token inválido" test_validate_invalid_token
    run_test "Reset contraseña con token inválido" test_reset_password_invalid_token
    run_test "Refresh token inválido" test_refresh_invalid_token
    run_test "Rate limiting en login" test_login_rate_limiting
    run_test "Rate limiting en forgot password" test_forgot_password_rate_limiting
    run_test "Headers de seguridad" test_security_headers
    run_test "Formato de respuesta JSON" test_json_response_format
    
    # Mostrar resumen
    echo ""
    log "${BLUE}📊 RESUMEN DE VERIFICACIÓN DE AUTENTICACIÓN${NC}"
    log "Total de tests: $TESTS_TOTAL"
    log "${GREEN}Tests exitosos: $TESTS_PASSED${NC}"
    log "${RED}Tests fallidos: $TESTS_FAILED${NC}"
    log "Tasa de éxito: $(( TESTS_PASSED * 100 / TESTS_TOTAL ))%"
    log ""
    log "Log completo guardado en: $LOG_FILE"
    
    # Determinar código de salida
    if [ $TESTS_FAILED -eq 0 ]; then
        log "${GREEN}🎉 Todos los tests de autenticación pasaron exitosamente${NC}"
        exit 0
    else
        log "${RED}⚠️ Algunos tests de autenticación fallaron${NC}"
        exit 1
    fi
}

# Ejecutar si se llama directamente
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
