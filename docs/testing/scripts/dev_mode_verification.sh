#!/bin/bash
# Script de verificación para el modo desarrollo

echo "🔍 Verificando configuración de modo desarrollo..."
echo ""

# Verificar que el perfil dev esté activo
echo "1. Verificando perfil activo..."
if [ "$SPRING_PROFILES_ACTIVE" = "dev" ]; then
    echo "   ✅ Perfil 'dev' está activo"
else
    echo "   ⚠️  Perfil 'dev' NO está activo"
    echo "   💡 Ejecutar: export SPRING_PROFILES_ACTIVE=dev"
fi

echo ""
echo "2. Verificando propiedades de desarrollo..."
echo "   📄 Archivo: application-dev.properties"
if [ -f "src/main/resources/application-dev.properties" ]; then
    echo "   ✅ Archivo de configuración existe"
    
    if grep -q "app.dev.skip-authentication=true" src/main/resources/application-dev.properties; then
        echo "   ✅ skip-authentication está habilitado"
    else
        echo "   ❌ skip-authentication NO está habilitado"
    fi
else
    echo "   ❌ Archivo de configuración NO existe"
fi

echo ""
echo "3. Verificando clases de configuración..."

# DevSecurityConfig
if [ -f "src/main/java/com/conaveg/cona/config/DevSecurityConfig.java" ]; then
    echo "   ✅ DevSecurityConfig.java existe"
else
    echo "   ❌ DevSecurityConfig.java NO existe"
fi

# BaseConfig
if [ -f "src/main/java/com/conaveg/cona/config/BaseConfig.java" ]; then
    echo "   ✅ BaseConfig.java existe (para passwordEncoder)"
else
    echo "   ❌ BaseConfig.java NO existe"
fi

# DevController
if [ -f "src/main/java/com/conaveg/cona/controller/DevController.java" ]; then
    echo "   ✅ DevController.java existe"
else
    echo "   ❌ DevController.java NO existe"
fi

echo ""
echo "4. Verificando documentación..."

if [ -f "docs/DEV_SKIP_AUTH.md" ]; then
    echo "   ✅ Documentación de modo desarrollo existe"
else
    echo "   ❌ Documentación NO existe"
fi

echo ""
echo "📋 Resumen de verificación completado"
echo ""
echo "🚀 Para iniciar en modo desarrollo:"
echo "   export SPRING_PROFILES_ACTIVE=dev"
echo "   mvn spring-boot:run"
echo ""
echo "🔍 Para verificar que funciona:"
echo "   curl http://localhost:8080/conaveg/api/dev/status"
