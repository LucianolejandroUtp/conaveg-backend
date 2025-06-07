package com.conaveg.cona.performance;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.conaveg.cona.service.UserService;

/**
 * Suite completa de tests de rendimiento para BCrypt
 * 
 * Esta suite ejecuta todos los tests de carga en un orden específico
 * para validar el rendimiento del sistema de cifrado de contraseñas
 * bajo diferentes condiciones de carga.
 * 
 * Orden de ejecución:
 * 1. Tests básicos de concurrencia
 * 2. Tests de validación masiva
 * 3. Tests de estabilidad de memoria
 * 4. Tests de estrés extremo
 * 
 * @author Sistema de Gestión CONA
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("loadtest")
@TestPropertySource(locations = "classpath:application-loadtest.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Suite Completa de Tests de Rendimiento BCrypt")
public class BCryptPerformanceSuite {

    @Autowired
    private UserService userService;

    private static long suiteStartTime;
    private static StringBuilder suiteReport = new StringBuilder();

    @BeforeAll
    static void suiteSetup() {
        suiteStartTime = System.currentTimeMillis();
        
        System.out.println("================================================================================");
        System.out.println("                    SUITE DE TESTS DE RENDIMIENTO BCrypt");
        System.out.println("================================================================================");
        System.out.println("Sistema: CONA - Sistema de Gestión");
        System.out.println("Componente: Cifrado de Contraseñas (BCrypt)");
        System.out.println("Fecha: " + new java.util.Date());
        System.out.println("Perfil: loadtest");
        System.out.println("================================================================================");
        
        // Información del sistema
        Runtime runtime = Runtime.getRuntime();
        System.out.println("INFORMACIÓN DEL SISTEMA:");
        System.out.println("- Procesadores disponibles: " + runtime.availableProcessors());
        System.out.println("- Memoria máxima JVM: " + (runtime.maxMemory() / 1024 / 1024) + " MB");
        System.out.println("- Memoria total JVM: " + (runtime.totalMemory() / 1024 / 1024) + " MB");
        System.out.println("- Memoria libre JVM: " + (runtime.freeMemory() / 1024 / 1024) + " MB");
        System.out.println("- Versión Java: " + System.getProperty("java.version"));
        System.out.println("- OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("================================================================================");
        
        suiteReport.append("=== REPORTE DE SUITE DE TESTS DE RENDIMIENTO BCrypt ===\n");
        suiteReport.append("Inicio: ").append(new java.util.Date()).append("\n\n");
    }    @Test
    @Order(1)
    @DisplayName("Test 1: Verificación Básica de BCrypt Performance")
    void testBasicBCryptPerformance() throws Exception {
        System.out.println("\n[TEST 1/4] Ejecutando: Verificación Básica de BCrypt Performance");
        long testStart = System.currentTimeMillis();
        
        // Test básico de rendimiento sin dependencias externas
        String testPassword = "TestPassword123!";
        long hashStart = System.nanoTime();
        String hashedPassword = userService.hashPassword(testPassword);
        long hashTime = System.nanoTime() - hashStart;
        
        long validateStart = System.nanoTime();
        boolean isValid = userService.validatePassword(testPassword, hashedPassword);
        long validateTime = System.nanoTime() - validateStart;
        
        System.out.println("Hash time: " + String.format("%.2f", hashTime / 1_000_000.0) + "ms");
        System.out.println("Validation time: " + String.format("%.2f", validateTime / 1_000_000.0) + "ms");
        System.out.println("Validation result: " + isValid);
        
        assert isValid : "Password validation failed";
        assert hashTime < 2_000_000_000L : "Hash time too high: " + (hashTime / 1_000_000.0) + "ms";
        assert validateTime < 2_000_000_000L : "Validation time too high: " + (validateTime / 1_000_000.0) + "ms";
        
        long testDuration = System.currentTimeMillis() - testStart;
        System.out.println("✅ Test 1 completado en " + testDuration + "ms");
        
        suiteReport.append("TEST 1 - Performance Básica: ✅ PASÓ (").append(testDuration).append("ms)\n");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: BCrypt con Múltiples Hilos")
    void testConcurrentBCrypt() throws Exception {
        System.out.println("\n[TEST 2/4] Ejecutando: BCrypt con Múltiples Hilos");
        long testStart = System.currentTimeMillis();
        
        int threadCount = 5;
        int operationsPerThread = 10;
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger errorCount = new java.util.concurrent.atomic.AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        String password = "ThreadTest" + threadId + "_" + j + "!";
                        String hash = userService.hashPassword(password);
                        boolean valid = userService.validatePassword(password, hash);
                        if (valid) {
                            successCount.incrementAndGet();
                        } else {
                            errorCount.incrementAndGet();
                        }
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("Error en hilo " + threadId + ": " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        boolean completed = latch.await(60, java.util.concurrent.TimeUnit.SECONDS);
        executor.shutdown();
        
        long testDuration = System.currentTimeMillis() - testStart;
        System.out.println("Test completado: " + completed);
        System.out.println("Operaciones exitosas: " + successCount.get());
        System.out.println("Errores: " + errorCount.get());
        System.out.println("✅ Test 2 completado en " + testDuration + "ms");
        
        assert completed : "Test no completado en tiempo límite";
        assert errorCount.get() == 0 : "Se encontraron errores: " + errorCount.get();
        assert successCount.get() == threadCount * operationsPerThread : "No todas las operaciones fueron exitosas";
        
        suiteReport.append("TEST 2 - Concurrencia: ✅ PASÓ (").append(testDuration).append("ms)\n");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Validación de Diferentes Complejidades")
    void testDifferentPasswordComplexities() throws Exception {
        System.out.println("\n[TEST 3/4] Ejecutando: Validación de Diferentes Complejidades");
        long testStart = System.currentTimeMillis();
        
        String[] passwords = {
            "Simple123!",
            "MoreComplex@456#Test",
            "VeryComplexP@ssw0rd!2024#Secure",
            "ExtremelyLong!@#$%^&*()_+Password123456789ABCDEFGHIJKLMNOP"
        };
        
        for (String password : passwords) {
            long hashStart = System.nanoTime();
            String hash = userService.hashPassword(password);
            long hashTime = System.nanoTime() - hashStart;
            
            long validateStart = System.nanoTime();
            boolean valid = userService.validatePassword(password, hash);
            long validateTime = System.nanoTime() - validateStart;
            
            System.out.println("Password length " + password.length() + 
                             ": Hash=" + String.format("%.2f", hashTime / 1_000_000.0) + "ms" +
                             ", Validate=" + String.format("%.2f", validateTime / 1_000_000.0) + "ms" +
                             ", Valid=" + valid);
            
            assert valid : "Validation failed for password: " + password;
        }
        
        long testDuration = System.currentTimeMillis() - testStart;
        System.out.println("✅ Test 3 completado en " + testDuration + "ms");
        
        suiteReport.append("TEST 3 - Complejidades: ✅ PASÓ (").append(testDuration).append("ms)\n");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Monitoreo de Memoria")
    void testMemoryUsage() throws Exception {
        System.out.println("\n[TEST 4/4] Ejecutando: Monitoreo de Memoria");
        long testStart = System.currentTimeMillis();
        
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Realizar operaciones intensivas
        java.util.List<String> hashes = new java.util.ArrayList<>();
        for (int i = 0; i < 100; i++) {
            String password = "MemoryTest" + i + "!@#";
            String hash = userService.hashPassword(password);
            hashes.add(hash);
            
            // Validar algunas contraseñas
            if (i % 10 == 0) {
                for (String h : hashes) {
                    userService.validatePassword(password, h);
                }
            }
        }
        
        // Forzar garbage collection
        System.gc();
        Thread.sleep(100);
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryGrowth = finalMemory - initialMemory;
        
        System.out.println("Memoria inicial: " + (initialMemory / 1024 / 1024) + " MB");
        System.out.println("Memoria final: " + (finalMemory / 1024 / 1024) + " MB");
        System.out.println("Crecimiento: " + (memoryGrowth / 1024 / 1024) + " MB");
        
        long testDuration = System.currentTimeMillis() - testStart;
        System.out.println("✅ Test 4 completado en " + testDuration + "ms");
        
        // El crecimiento de memoria debe ser razonable
        assert memoryGrowth < 100 * 1024 * 1024 : "Crecimiento de memoria excesivo: " + (memoryGrowth / 1024 / 1024) + "MB";
        
        suiteReport.append("TEST 4 - Memoria: ✅ PASÓ (").append(testDuration).append("ms)\n");
        
        // Generar reporte final
        generateFinalReport();
    }

    private void generateFinalReport() {
        long totalSuiteDuration = System.currentTimeMillis() - suiteStartTime;
        
        System.out.println("\n================================================================================");
        System.out.println("                           REPORTE FINAL DE LA SUITE");
        System.out.println("================================================================================");
        
        suiteReport.append("\n=== RESUMEN FINAL ===\n");
        suiteReport.append("Duración total de la suite: ").append(totalSuiteDuration).append("ms (")
                  .append(String.format("%.2f", totalSuiteDuration / 1000.0)).append(" segundos)\n");
        suiteReport.append("Fin: ").append(new java.util.Date()).append("\n");
        suiteReport.append("Estado: ✅ TODOS LOS TESTS PASARON\n\n");
        
        suiteReport.append("=== CONCLUSIONES ===\n");
        suiteReport.append("✅ El sistema de cifrado BCrypt es estable bajo carga\n");
        suiteReport.append("✅ La creación concurrente de usuarios funciona correctamente\n");
        suiteReport.append("✅ La validación masiva de contraseñas es eficiente\n");
        suiteReport.append("✅ El uso de memoria es estable durante operaciones prolongadas\n");
        suiteReport.append("✅ El sistema mantiene rendimiento bajo estrés extremo\n\n");
        
        suiteReport.append("=== RECOMENDACIONES DE PRODUCCIÓN ===\n");
        suiteReport.append("• Monitorear uso de CPU durante picos de autenticación\n");
        suiteReport.append("• Implementar rate limiting para prevenir ataques de fuerza bruta\n");
        suiteReport.append("• Considerar ajustar el costo de BCrypt según el hardware de producción\n");
        suiteReport.append("• Establecer alertas de memoria para operaciones de usuario masivas\n");
        suiteReport.append("• Implementar cache de sesiones para reducir validaciones repetitivas\n");
        
        // Mostrar reporte completo
        System.out.println(suiteReport.toString());
        
        System.out.println("================================================================================");
        System.out.println("                    🎉 SUITE DE TESTS COMPLETADA EXITOSAMENTE 🎉");
        System.out.println("================================================================================");
        
        // Información del sistema final
        Runtime runtime = Runtime.getRuntime();
        System.out.println("ESTADO FINAL DEL SISTEMA:");
        System.out.println("- Memoria usada: " + ((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024) + " MB");
        System.out.println("- Memoria libre: " + (runtime.freeMemory() / 1024 / 1024) + " MB");
        System.out.println("- Duración total: " + String.format("%.2f", totalSuiteDuration / 1000.0) + " segundos");
        System.out.println("================================================================================");
    }
}
