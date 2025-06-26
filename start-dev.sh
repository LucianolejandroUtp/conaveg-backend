#!/bin/bash
# Script para Linux/Mac - Iniciar en modo desarrollo sin autenticación
# ADVERTENCIA: SOLO PARA DESARROLLO

echo ""
echo "============================================"
echo "  CONA - Iniciando en MODO DESARROLLO"
echo "============================================"
echo ""
echo "ADVERTENCIA: Este modo DESACTIVA la autenticación"
echo "NO usar en producción"
echo ""

# Establecer perfil de desarrollo
export SPRING_PROFILES_ACTIVE=dev

echo "Perfil activo: $SPRING_PROFILES_ACTIVE"
echo ""

# Iniciar aplicación
echo "Iniciando aplicación..."
echo "Presiona Ctrl+C para detener"
echo ""

mvn spring-boot:run

echo ""
echo "Aplicación detenida"
