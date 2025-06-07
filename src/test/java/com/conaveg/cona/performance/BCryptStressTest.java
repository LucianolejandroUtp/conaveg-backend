package com.conaveg.cona.performance;

import com.conaveg.cona.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
@ActiveProfiles("loadtest")
@TestPropertySource(locations = "classpath:application-loadtest.properties")
public class BCryptStressTest {

    @Autowired
    private UserService userService;

    // Configuración de estrés - números altos para probar límites
    private static final int MAX_THREADS = 50;
    private static final int STRESS_DURATION_SECONDS = 60; // 1 minuto de estrés intenso
    private static final int OPERATIONS_PER_SECOND_TARGET = 100;

    private final String[] stressPasswords = {
        "StressTest123!",
        "LoadTesting456@",
        "PerformanceTest789#",
        "BCryptStress!2024",
        "IntenseLoad$567",
        "MaxCapacity@890",
        "SystemLimit#123",
        "HighLoad!456",
        "StressValidation@789",
        "ConcurrentTest#012"
    };

    @BeforeEach
    void setUp() {
        System.out.println("=== INICIANDO TEST DE ESTRÉS BCrypt ===");
        System.out.println("⚠️  ADVERTENCIA: Este test aplicará carga extrema al sistema");
        System.out.println("Configuración de estrés:");
        System.out.println("- Hilos máximos: " + MAX_THREADS);
        System.out.println("- Duración: " + STRESS_DURATION_SECONDS + " segundos");
        System.out.println("- Objetivo: " + OPERATIONS_PER_SECOND_TARGET + " operaciones/segundo");
        System.out.println("- Contraseñas de prueba: " + stressPasswords.length);
        
        // Forzar limpieza de memoria antes del estrés
        System.gc();
        Thread.yield();
        System.gc();
    }

    @Test
    public void testBCryptUnderExtremeLoad() throws InterruptedException {
        // Contadores para métricas detalladas
        AtomicInteger totalOperations = new AtomicInteger(0);
        AtomicInteger successfulOperations = new AtomicInteger(0);
        AtomicInteger failedOperations = new AtomicInteger(0);
        AtomicInteger timeouts = new AtomicInteger(0);
        AtomicInteger exceptions = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        AtomicLong maxResponseTime = new AtomicLong(0);
        AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);

        // Configuración de tiempo
        long testStartTime = System.currentTimeMillis();
        long testEndTime = testStartTime + (STRESS_DURATION_SECONDS * 1000L);

        // Crear pool de hilos con tamaño dinámico
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        
        // Lista para monitoreo en tiempo real
        List<StressMetrics> stressSnapshots = new ArrayList<>();
        
        // Monitor de métricas en tiempo real
        ScheduledExecutorService metricsMonitor = Executors.newScheduledThreadPool(1);
        metricsMonitor.scheduleAtFixedRate(() -> {
            long elapsedSeconds = (System.currentTimeMillis() - testStartTime) / 1000;
            int currentOps = totalOperations.get();
            double opsPerSecond = elapsedSeconds > 0 ? currentOps / (double) elapsedSeconds : 0;
            
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            
            StressMetrics metrics = new StressMetrics(
                elapsedSeconds, currentOps, opsPerSecond, usedMemory, maxMemory,
                successfulOperations.get(), failedOperations.get(), exceptions.get()
            );
            
            synchronized (stressSnapshots) {
                stressSnapshots.add(metrics);
            }
            
            System.out.printf("[%02ds] Ops: %d (%.1f/s) | Mem: %dMB/%dMB | Success: %d | Fails: %d | Errors: %d%n",
                elapsedSeconds, currentOps, opsPerSecond, usedMemory, maxMemory,
                successfulOperations.get(), failedOperations.get(), exceptions.get());
                
        }, 5, 5, TimeUnit.SECONDS);

        // Crear múltiples tareas de estrés
        List<Future<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < MAX_THREADS; i++) {
            final int threadId = i;
            Future<Void> future = executor.submit(() -> {
                List<String> localHashedPasswords = new ArrayList<>();
                
                try {
                    while (System.currentTimeMillis() < testEndTime) {
                        String password = stressPasswords[totalOperations.get() % stressPasswords.length];
                        
                        long operationStart = System.nanoTime();
                        boolean operationSuccessful = false;
                        
                        try {
                            // Alternar entre diferentes tipos de operaciones
                            int operationType = totalOperations.get() % 4;
                            
                            switch (operationType) {
                                case 0: // Hash simple
                                    String hashedPassword = userService.hashPassword(password);
                                    localHashedPasswords.add(hashedPassword);
                                    operationSuccessful = true;
                                    break;
                                    
                                case 1: // Validación con hash existente
                                    if (!localHashedPasswords.isEmpty()) {
                                        String existingHash = localHashedPasswords.get(
                                            localHashedPasswords.size() - 1
                                        );
                                        boolean isValid = userService.validatePassword(password, existingHash);
                                        operationSuccessful = true;
                                    } else {
                                        // Fallback: crear hash
                                        String newHash = userService.hashPassword(password);
                                        localHashedPasswords.add(newHash);
                                        operationSuccessful = true;
                                    }
                                    break;
                                    
                                case 2: // Hash + validación inmediata
                                    String hashForValidation = userService.hashPassword(password);
                                    boolean validationResult = userService.validatePassword(password, hashForValidation);
                                    operationSuccessful = validationResult;
                                    break;
                                    
                                case 3: // Validación con hash incorrecto (debe fallar)
                                    if (!localHashedPasswords.isEmpty()) {
                                        String wrongPassword = stressPasswords[(totalOperations.get() + 1) % stressPasswords.length];
                                        String existingHash = localHashedPasswords.get(0);
                                        boolean shouldBeFalse = userService.validatePassword(wrongPassword, existingHash);
                                        operationSuccessful = !shouldBeFalse; // Esperamos que sea false
                                    } else {
                                        // Fallback
                                        String newHash = userService.hashPassword(password);
                                        localHashedPasswords.add(newHash);
                                        operationSuccessful = true;
                                    }
                                    break;
                            }
                            
                        } catch (Exception e) {
                            exceptions.incrementAndGet();
                            System.err.println("Error en operación (Hilo " + threadId + "): " + e.getMessage());
                            operationSuccessful = false;
                        }
                        
                        long operationEnd = System.nanoTime();
                        long responseTime = operationEnd - operationStart;
                        
                        // Actualizar métricas
                        totalResponseTime.addAndGet(responseTime);
                        updateMinMax(maxResponseTime, minResponseTime, responseTime);
                        
                        if (operationSuccessful) {
                            successfulOperations.incrementAndGet();
                        } else {
                            failedOperations.incrementAndGet();
                        }
                        
                        totalOperations.incrementAndGet();
                        
                        // Gestionar memoria: limpiar cache local si crece demasiado
                        if (localHashedPasswords.size() > 50) {
                            localHashedPasswords.subList(0, 25).clear();
                        }
                        
                        // Pequeña pausa para no sobrecargar completamente el sistema
                        Thread.sleep(1);
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                    System.err.println("Error fatal en hilo " + threadId + ": " + e.getMessage());
                }
                
                return null;
            });
            
            futures.add(future);
        }

        // Esperar a que terminen todas las tareas
        for (Future<Void> future : futures) {
            try {
                future.get(STRESS_DURATION_SECONDS + 30, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                timeouts.incrementAndGet();
                future.cancel(true);
            } catch (ExecutionException e) {
                exceptions.incrementAndGet();
                System.err.println("Error en tarea de estrés: " + e.getMessage());
            }
        }

        // Detener monitores
        metricsMonitor.shutdown();
        executor.shutdown();
        
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        if (!metricsMonitor.awaitTermination(10, TimeUnit.SECONDS)) {
            metricsMonitor.shutdownNow();
        }

        // Análisis final
        analyzeStressTestResults(stressSnapshots, totalOperations.get(), 
                               successfulOperations.get(), failedOperations.get(),
                               exceptions.get(), timeouts.get(),
                               totalResponseTime.get(), minResponseTime.get(), maxResponseTime.get());
    }

    private void updateMinMax(AtomicLong maxValue, AtomicLong minValue, long newValue) {
        // Actualizar máximo
        long currentMax = maxValue.get();
        while (newValue > currentMax && !maxValue.compareAndSet(currentMax, newValue)) {
            currentMax = maxValue.get();
        }
        
        // Actualizar mínimo
        long currentMin = minValue.get();
        while (newValue < currentMin && !minValue.compareAndSet(currentMin, newValue)) {
            currentMin = minValue.get();
        }
    }

    private void analyzeStressTestResults(List<StressMetrics> snapshots,
                                        int totalOps, int successOps, int failedOps,
                                        int exceptions, int timeouts,
                                        long totalResponseTime, long minResponse, long maxResponse) {
        
        System.out.println("\n=== ANÁLISIS DE RESULTADOS DEL TEST DE ESTRÉS ===");
        
        // Estadísticas básicas
        System.out.println("OPERACIONES:");
        System.out.println("- Total de operaciones: " + totalOps);
        System.out.println("- Operaciones exitosas: " + successOps);
        System.out.println("- Operaciones fallidas: " + failedOps);
        System.out.println("- Excepciones: " + exceptions);
        System.out.println("- Timeouts: " + timeouts);
        
        if (totalOps > 0) {
            double successRate = (successOps * 100.0) / totalOps;
            double errorRate = (exceptions * 100.0) / totalOps;
            double avgResponseTime = (totalResponseTime / 1_000_000.0) / totalOps;
            double throughput = totalOps / (double) STRESS_DURATION_SECONDS;
            
            System.out.println("\nRENDIMIENTO:");
            System.out.println("- Tasa de éxito: " + String.format("%.2f", successRate) + "%");
            System.out.println("- Tasa de error: " + String.format("%.2f", errorRate) + "%");
            System.out.println("- Throughput: " + String.format("%.2f", throughput) + " ops/segundo");
            System.out.println("- Tiempo de respuesta promedio: " + String.format("%.2f", avgResponseTime) + "ms");
            System.out.println("- Tiempo de respuesta mínimo: " + String.format("%.2f", minResponse / 1_000_000.0) + "ms");
            System.out.println("- Tiempo de respuesta máximo: " + String.format("%.2f", maxResponse / 1_000_000.0) + "ms");
        }

        // Análisis de memoria si hay snapshots
        if (!snapshots.isEmpty()) {
            System.out.println("\nUSO DE MEMORIA:");
            StressMetrics lastSnapshot = snapshots.get(snapshots.size() - 1);
            System.out.println("- Memoria final: " + lastSnapshot.usedMemory + "MB / " + lastSnapshot.maxMemory + "MB");
            System.out.println("- Porcentaje de uso: " + String.format("%.1f", (lastSnapshot.usedMemory * 100.0) / lastSnapshot.maxMemory) + "%");
        }

        // Evaluación de estabilidad
        System.out.println("\nEVALUACIÓN DE ESTABILIDAD BAJO ESTRÉS:");
        
        if (exceptions == 0 && timeouts == 0) {
            System.out.println("✅ EXCELENTE: Sin errores ni timeouts durante el estrés");
        } else if (exceptions < 5 && timeouts < 3) {
            System.out.println("✅ BUENO: Pocos errores bajo estrés extremo");
        } else if (exceptions < 20 && timeouts < 10) {
            System.out.println("⚠️  ACEPTABLE: Algunos errores bajo estrés, pero manejable");
        } else {
            System.out.println("❌ PROBLEMÁTICO: Muchos errores bajo estrés");
        }

        if (totalOps > 0) {
            double successRate = (successOps * 100.0) / totalOps;
            if (successRate > 95) {
                System.out.println("✅ TASA DE ÉXITO EXCELENTE (>" + 95 + "%)");
            } else if (successRate > 90) {
                System.out.println("✅ TASA DE ÉXITO BUENA (>" + 90 + "%)");
            } else if (successRate > 80) {
                System.out.println("⚠️  TASA DE ÉXITO ACEPTABLE (>" + 80 + "%)");
            } else {
                System.out.println("❌ TASA DE ÉXITO BAJA (<" + 80 + "%)");
            }
        }

        // Verificaciones de éxito del test
        assert totalOps > 0 : "No se realizaron operaciones durante el test";
        assert exceptions < totalOps * 0.1 : "Demasiadas excepciones: " + exceptions + " de " + totalOps;
        assert timeouts < MAX_THREADS * 0.2 : "Demasiados timeouts: " + timeouts;
        
        if (totalOps > 0) {
            double successRate = (successOps * 100.0) / totalOps;
            assert successRate > 70 : "Tasa de éxito muy baja: " + successRate + "%";
        }

        System.out.println("\n✅ TEST DE ESTRÉS COMPLETADO - SISTEMA ESTABLE BAJO CARGA EXTREMA");
    }

    // Clase para métricas de estrés
    private static class StressMetrics {
        final long elapsedSeconds;
        final int totalOperations;
        final double operationsPerSecond;
        final long usedMemory;
        final long maxMemory;
        final int successfulOperations;
        final int failedOperations;
        final int exceptions;

        StressMetrics(long elapsedSeconds, int totalOperations, double operationsPerSecond,
                     long usedMemory, long maxMemory, int successfulOperations, 
                     int failedOperations, int exceptions) {
            this.elapsedSeconds = elapsedSeconds;
            this.totalOperations = totalOperations;
            this.operationsPerSecond = operationsPerSecond;
            this.usedMemory = usedMemory;
            this.maxMemory = maxMemory;
            this.successfulOperations = successfulOperations;
            this.failedOperations = failedOperations;
            this.exceptions = exceptions;
        }
    }
}
