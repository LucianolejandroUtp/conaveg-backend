@echo off
REM Script para Windows - Iniciar en modo desarrollo sin autenticación
REM ADVERTENCIA: SOLO PARA DESARROLLO

echo.
echo ============================================
echo  CONA - Iniciando en MODO DESARROLLO
echo ============================================
echo.
echo ADVERTENCIA: Este modo DESACTIVA la autenticacion
echo NO usar en produccion
echo.

REM Establecer perfil de desarrollo
set SPRING_PROFILES_ACTIVE=dev

echo Perfil activo: %SPRING_PROFILES_ACTIVE%
echo.

REM Iniciar aplicación
echo Iniciando aplicacion...
echo Presiona Ctrl+C para detener
echo.

mvn spring-boot:run

echo.
echo Aplicacion detenida
pause
