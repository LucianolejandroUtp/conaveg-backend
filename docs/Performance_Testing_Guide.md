# Guía de Tests de Performance BCrypt - Sistema CONA

## Introducción

Esta guía explica cómo ejecutar, mantener y interpretar los tests de performance del sistema de cifrado BCrypt implementado en el Sistema de Gestión CONA.

## Estructura de Tests

### Ubicación de Archivos

```
src/test/java/com/conaveg/cona/performance/
├── BCryptLoadTest.java                    # Test de creación concurrente
├── PasswordValidationLoadTest.java        # Test de validación masiva
├── BCryptMemoryStabilityTest.java         # Test de estabilidad de memoria
├── BCryptStressTest.java                  # Test de estrés extremo
└── BCryptPerformanceSuite.java            # Suite completa de tests

src/test/resources/
└── application-loadtest.properties        # Configuración específica
```

## Tipos de Tests

### 1. BCryptLoadTest - Creación Concurrente
**Propósito**: Validar la creación simultánea de usuarios con cifrado BCrypt

```bash
# Ejecutar test individual
mvn test -Dtest=BCryptLoadTest

# Configuración actual:
# - 10 hilos concurrentes
# - 50 usuarios por hilo (500 total)
# - Validación de integridad referencial
```

**Métricas evaluadas**:
- Tiempo promedio de creación de usuario
- Throughput (usuarios/segundo)
- Tasa de éxito/error
- Gestión de memoria

### 2. PasswordValidationLoadTest - Validación Masiva
**Propósito**: Evaluar rendimiento de validación bajo carga

```bash
# Ejecutar test individual
mvn test -Dtest=PasswordValidationLoadTest

# Configuración actual:
# - 10 hilos concurrentes
# - 20 validaciones por hilo (200 total)
# - Diferentes tipos de contraseñas
```

**Métricas evaluadas**:
- Tiempo promedio de validación
- Validaciones por segundo
- Rendimiento con diferentes complejidades
- Estabilidad bajo concurrencia

### 3. BCryptMemoryStabilityTest - Estabilidad de Memoria
**Propósito**: Monitorear uso de memoria durante operaciones prolongadas

```bash
# Ejecutar test individual
mvn test -Dtest=BCryptMemoryStabilityTest

# Configuración actual:
# - Duración: 2 minutos
# - 5 hilos concurrentes
# - Monitoreo cada 5 segundos
```

**Métricas evaluadas**:
- Uso de memoria en tiempo real
- Detección de memory leaks
- Estabilidad durante operaciones prolongadas
- Patrones de garbage collection

### 4. BCryptStressTest - Estrés Extremo
**Propósito**: Evaluar comportamiento bajo condiciones extremas

```bash
# Ejecutar test individual
mvn test -Dtest=BCryptStressTest

# Configuración actual:
# - 50 hilos concurrentes
# - 60 segundos de estrés continuo
# - Múltiples tipos de operaciones
```

**Métricas evaluadas**:
- Comportamiento bajo carga extrema
- Tasa de éxito/error en condiciones límite
- Tiempo de respuesta bajo estrés
- Recuperación del sistema

### 5. BCryptPerformanceSuite - Suite Completa
**Propósito**: Ejecutar todos los tests en secuencia con reporte consolidado

```bash
# Ejecutar suite completa
mvn test -Dtest=BCryptPerformanceSuite

# Ejecuta todos los tests en orden:
# 1. Performance básica
# 2. Concurrencia
# 3. Complejidades
# 4. Memoria
```

## Ejecución de Tests

### Comandos Maven

```bash
# Ejecutar todos los tests de performance
mvn test -Dtest="com.conaveg.cona.performance.**"

# Ejecutar test específico
mvn test -Dtest=BCryptLoadTest

# Ejecutar con perfil específico
mvn test -Dtest=BCryptLoadTest -Dspring.profiles.active=loadtest

# Ejecutar con configuración de memoria específica
mvn test -Dtest=BCryptStressTest -Xmx2g -Xms512m
```

### Configuración de Entorno

#### application-loadtest.properties
```properties
# Base de datos en memoria para tests
spring.datasource.url=jdbc:h2:mem:loadtestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Logging
logging.level.com.conaveg.cona=INFO
logging.level.org.springframework.security=WARN
logging.level.org.hibernate=WARN
```

#### Configuración JVM para Tests
```bash
# Para tests de memoria
export MAVEN_OPTS="-Xmx4g -Xms1g -XX:+UseG1GC"

# Para tests de estrés
export MAVEN_OPTS="-Xmx8g -Xms2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

## Interpretación de Resultados

### Métricas de Rendimiento

#### Tiempos de Respuesta BCrypt
```
✅ Excelente: < 500ms por operación
✅ Bueno: 500ms - 1000ms por operación  
⚠️  Aceptable: 1000ms - 2000ms por operación
❌ Problemático: > 2000ms por operación
```

#### Throughput
```
✅ Excelente: > 20 operaciones/segundo
✅ Bueno: 10-20 operaciones/segundo
⚠️  Aceptable: 5-10 operaciones/segundo
❌ Problemático: < 5 operaciones/segundo
```

#### Uso de Memoria
```
✅ Estable: Variación < 50MB durante el test
⚠️  Moderado: Variación 50-100MB
❌ Problemático: Variación > 100MB o crecimiento continuo
```

#### Tasa de Éxito
```
✅ Excelente: > 99% éxito
✅ Bueno: 95-99% éxito
⚠️  Aceptable: 90-95% éxito
❌ Problemático: < 90% éxito
```

### Análisis de Outputs

#### Output Típico de BCryptLoadTest
```
=== INICIANDO TEST DE CARGA CONCURRENTE BCRYPT ===
Configuración:
- Hilos concurrentes: 10
- Usuarios por hilo: 50
- Total de usuarios a crear: 500

Hilo 0 completado en 15234ms
Hilo 1 completado en 15456ms
...

=== RESULTADOS DEL TEST DE CARGA ===
Tiempo total del test: 16789ms
Test completado: SÍ
Usuarios creados exitosamente: 500
Usuarios fallidos: 0
Tiempo promedio por usuario: 335.78ms
Throughput: 29.78 usuarios/segundo
✅ Rendimiento BCrypt excelente
✅ Sin errores durante la ejecución
```

#### Interpretación de Fallos
```bash
# Error común: Timeout
"El test no se completó dentro del tiempo límite"
→ Solución: Aumentar tiempo límite o reducir carga

# Error común: Memory
"Crecimiento de memoria excesivo: 150MB"
→ Solución: Revisar memory leaks, ajustar GC

# Error común: Integridad
"could not execute statement; SQL [insert into..."
→ Solución: Verificar configuración de BD de test
```

## Configuración de Tests

### Ajustar Parámetros de Carga

#### BCryptLoadTest
```java
// Modificar en BCryptLoadTest.java
private static final int THREAD_COUNT = 20;           // Más hilos
private static final int USERS_PER_THREAD = 100;      // Más usuarios
private static final int TOTAL_USERS = THREAD_COUNT * USERS_PER_THREAD;
```

#### PasswordValidationLoadTest
```java
// Modificar en PasswordValidationLoadTest.java
private static final int THREAD_COUNT = 20;           // Más hilos
private static final int VALIDATIONS_PER_THREAD = 100; // Más validaciones
```

#### BCryptMemoryStabilityTest
```java
// Modificar en BCryptMemoryStabilityTest.java
private static final int DURATION_MINUTES = 5;        // Mayor duración
private static final int THREAD_COUNT = 10;           // Más hilos
```

#### BCryptStressTest
```java
// Modificar en BCryptStressTest.java
private static final int MAX_THREADS = 100;           // Más hilos
private static final int STRESS_DURATION_SECONDS = 120; // Mayor duración
```

### Configuración de Timeouts

```java
// Ajustar timeouts en tests
boolean completed = latch.await(5, TimeUnit.MINUTES);  // Más tiempo

// Configurar timeout de operaciones individuales
CompletableFuture.supplyAsync(() -> {
    // operación
}).get(30, TimeUnit.SECONDS);  // Timeout por operación
```

## Automatización en CI/CD

### Pipeline de Jenkins
```groovy
pipeline {
    agent any
    stages {
        stage('Performance Tests') {
            steps {
                script {
                    sh 'mvn test -Dtest=BCryptPerformanceSuite'
                }
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/surefire-reports/**'
                }
            }
        }
    }
}
```

### GitHub Actions
```yaml
name: Performance Tests
on:
  schedule:
    - cron: '0 2 * * *'  # Diario a las 2 AM
  workflow_dispatch:

jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '11'
      - name: Run Performance Tests
        run: mvn test -Dtest=BCryptPerformanceSuite
      - name: Upload Results
        uses: actions/upload-artifact@v3
        with:
          name: performance-results
          path: target/surefire-reports/
```

## Monitoreo y Alertas

### Configuración de Alertas
```yaml
# alerts.yml
performance_alerts:
  bcrypt_slow:
    condition: avg_response_time > 2000ms
    action: notify_team
    
  high_memory_usage:
    condition: memory_growth > 100MB
    action: investigate
    
  low_throughput:
    condition: operations_per_second < 5
    action: performance_review
```

### Métricas para Monitoreo
```java
// Métricas para Micrometer/Prometheus
@Timed(name = "bcrypt.hash.duration", description = "BCrypt hash duration")
public String hashPassword(String password) {
    return passwordEncoder.encode(password);
}

@Counted(name = "bcrypt.validation.total", description = "BCrypt validations")
public boolean validatePassword(String raw, String encoded) {
    return passwordEncoder.matches(raw, encoded);
}
```

## Troubleshooting

### Problemas Comunes

#### Tests Muy Lentos
```bash
# Causa: Configuración de BCrypt muy alta
# Solución: Usar configuración de test
@TestConfiguration
public class TestSecurityConfig {
    @Bean
    @Primary
    public BCryptPasswordEncoder testEncoder() {
        return new BCryptPasswordEncoder(4); // Menor costo
    }
}
```

#### OutOfMemoryError
```bash
# Causa: Heap insuficiente
# Solución: Aumentar memoria
mvn test -Dtest=BCryptStressTest -Xmx8g

# O ajustar parámetros del test
private static final int MAX_THREADS = 25; // Reducir hilos
```

#### Tests Intermitentes
```bash
# Causa: Condiciones de carrera
# Solución: Aumentar timeouts, usar CountDownLatch
boolean completed = latch.await(10, TimeUnit.MINUTES); // Más tiempo
```

---
**Documento actualizado**: Junio 2025  
**Versión**: 1.0  
**Sistema**: CONA - Sistema de Gestión
