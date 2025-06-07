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
public class BCryptMemoryStabilityTest {

    @Autowired
    private UserService userService;

    private static final int DURATION_MINUTES = 2; // Duración del test en minutos
    private static final int THREAD_COUNT = 5;
    private static final int MEMORY_CHECK_INTERVAL_MS = 5000; // Verificar memoria cada 5 segundos

    private final String[] testPasswords = {
        "TestPassword123!",
        "AnotherTest456@",
        "ComplexPass789#",
        "SecureTest!2024",
        "MemoryTest$567"
    };

    @BeforeEach
    void setUp() {
        System.out.println("=== INICIANDO TEST DE ESTABILIDAD DE MEMORIA BCrypt ===");
        System.out.println("Configuración:");
        System.out.println("- Duración: " + DURATION_MINUTES + " minutos");
        System.out.println("- Hilos concurrentes: " + THREAD_COUNT);
        System.out.println("- Intervalo de verificación de memoria: " + MEMORY_CHECK_INTERVAL_MS + "ms");
        System.out.println("- Contraseñas de prueba: " + testPasswords.length);
        
        // Forzar garbage collection antes de empezar
        System.gc();
        Thread.yield();
        System.gc();
    }

    @Test
    public void testMemoryStabilityDuringContinuousPasswordOperations() throws InterruptedException {
        // Contadores para métricas
        AtomicInteger totalOperations = new AtomicInteger(0);
        AtomicInteger hashOperations = new AtomicInteger(0);
        AtomicInteger validateOperations = new AtomicInteger(0);
        AtomicInteger exceptions = new AtomicInteger(0);
        AtomicLong totalOperationTime = new AtomicLong(0);

        // Lista para monitoreo de memoria
        List<MemorySnapshot> memorySnapshots = new ArrayList<>();

        // Configurar tiempo de ejecución
        long testDurationMs = DURATION_MINUTES * 60 * 1000;
        long testStartTime = System.currentTimeMillis();
        long testEndTime = testStartTime + testDurationMs;

        // ExecutorService para operaciones de contraseña
        ExecutorService passwordExecutor = Executors.newFixedThreadPool(THREAD_COUNT);
        
        // ExecutorService para monitoreo de memoria
        ScheduledExecutorService memoryMonitor = Executors.newScheduledThreadPool(1);

        // Iniciar monitoreo de memoria
        memoryMonitor.scheduleAtFixedRate(() -> {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            
            MemorySnapshot snapshot = new MemorySnapshot(
                System.currentTimeMillis() - testStartTime,
                usedMemory,
                totalMemory,
                maxMemory,
                totalOperations.get(),
                hashOperations.get(),
                validateOperations.get()
            );
            
            synchronized (memorySnapshots) {
                memorySnapshots.add(snapshot);
            }
            
            double usedMemoryMB = usedMemory / (1024.0 * 1024.0);
            double maxMemoryMB = maxMemory / (1024.0 * 1024.0);
            double memoryUsagePercent = (usedMemory * 100.0) / maxMemory;
            
            System.out.printf("Memoria [%ds]: %.2f/%.2f MB (%.1f%%) - Ops: %d (Hash: %d, Validate: %d)%n",
                (System.currentTimeMillis() - testStartTime) / 1000,
                usedMemoryMB, maxMemoryMB, memoryUsagePercent,
                totalOperations.get(), hashOperations.get(), validateOperations.get());
                
        }, 0, MEMORY_CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS);

        // Crear tareas de trabajo continuo
        List<Future<Void>> futures = new ArrayList<>();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Future<Void> future = passwordExecutor.submit(() -> {
                List<String> hashedPasswords = new ArrayList<>();
                
                try {
                    while (System.currentTimeMillis() < testEndTime) {
                        // Alternar entre hash y validación
                        String password = testPasswords[totalOperations.get() % testPasswords.length];
                        
                        long operationStart = System.nanoTime();
                        
                        if (totalOperations.get() % 2 == 0) {
                            // Operación de hash
                            String hashedPassword = userService.hashPassword(password);
                            hashedPasswords.add(hashedPassword);
                            hashOperations.incrementAndGet();
                            
                            // Limpiar lista si crece demasiado (evitar memory leak del test)
                            if (hashedPasswords.size() > 100) {
                                hashedPasswords.clear();
                            }
                        } else {
                            // Operación de validación
                            if (!hashedPasswords.isEmpty()) {
                                String hashedPassword = hashedPasswords.get(
                                    hashedPasswords.size() - 1 - (totalOperations.get() % hashedPasswords.size())
                                );
                                userService.validatePassword(password, hashedPassword);
                                validateOperations.incrementAndGet();
                            } else {
                                // Si no hay hashes disponibles, crear uno
                                String hashedPassword = userService.hashPassword(password);
                                hashedPasswords.add(hashedPassword);
                                hashOperations.incrementAndGet();
                            }
                        }
                        
                        long operationEnd = System.nanoTime();
                        totalOperationTime.addAndGet(operationEnd - operationStart);
                        totalOperations.incrementAndGet();
                        
                        // Pequeña pausa para no sobrecargar el sistema
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                    System.err.println("Error en hilo " + threadId + ": " + e.getMessage());
                }
                
                return null;
            });
            
            futures.add(future);
        }

        // Esperar a que terminen todas las tareas
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                exceptions.incrementAndGet();
                System.err.println("Error en tarea: " + e.getMessage());
            }
        }

        // Detener ejecutores
        passwordExecutor.shutdown();
        memoryMonitor.shutdown();
        
        if (!passwordExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
            passwordExecutor.shutdownNow();
        }
        if (!memoryMonitor.awaitTermination(10, TimeUnit.SECONDS)) {
            memoryMonitor.shutdownNow();
        }

        // Análisis de resultados
        analyzeMemoryStability(memorySnapshots, totalOperations.get(), 
                             hashOperations.get(), validateOperations.get(), 
                             totalOperationTime.get(), exceptions.get());
    }

    private void analyzeMemoryStability(List<MemorySnapshot> snapshots, 
                                      int totalOps, int hashOps, int validateOps,
                                      long totalTime, int exceptions) {
        
        System.out.println("\n=== ANÁLISIS DE ESTABILIDAD DE MEMORIA ===");
        
        if (snapshots.isEmpty()) {
            System.out.println("❌ No se capturaron snapshots de memoria");
            return;
        }

        // Estadísticas básicas
        System.out.println("Operaciones totales: " + totalOps);
        System.out.println("Operaciones de hash: " + hashOps);
        System.out.println("Operaciones de validación: " + validateOps);
        System.out.println("Excepciones: " + exceptions);
        System.out.println("Tiempo promedio por operación: " + 
                         String.format("%.2f", (totalTime / 1_000_000.0) / totalOps) + "ms");

        // Análisis de memoria
        double[] memoryUsages = snapshots.stream()
            .mapToDouble(s -> s.usedMemory / (1024.0 * 1024.0))
            .toArray();

        double minMemory = Double.MAX_VALUE;
        double maxMemory = Double.MIN_VALUE;
        double totalMemory = 0;
        
        for (double usage : memoryUsages) {
            minMemory = Math.min(minMemory, usage);
            maxMemory = Math.max(maxMemory, usage);
            totalMemory += usage;
        }
        
        double avgMemory = totalMemory / memoryUsages.length;
        double memoryRange = maxMemory - minMemory;
        double memoryGrowth = memoryUsages[memoryUsages.length - 1] - memoryUsages[0];

        System.out.println("\nESTADÍSTICAS DE MEMORIA:");
        System.out.println("- Memoria mínima: " + String.format("%.2f", minMemory) + " MB");
        System.out.println("- Memoria máxima: " + String.format("%.2f", maxMemory) + " MB");
        System.out.println("- Memoria promedio: " + String.format("%.2f", avgMemory) + " MB");
        System.out.println("- Rango de memoria: " + String.format("%.2f", memoryRange) + " MB");
        System.out.println("- Crecimiento neto: " + String.format("%.2f", memoryGrowth) + " MB");

        // Análisis de estabilidad
        System.out.println("\nANÁLISIS DE ESTABILIDAD:");
        
        if (memoryRange < 50) {
            System.out.println("✅ Uso de memoria MUY ESTABLE (rango < 50MB)");
        } else if (memoryRange < 100) {
            System.out.println("✅ Uso de memoria ESTABLE (rango < 100MB)");
        } else if (memoryRange < 200) {
            System.out.println("⚠️  Uso de memoria MODERADAMENTE VARIABLE (rango < 200MB)");
        } else {
            System.out.println("⚠️  Uso de memoria VARIABLE (rango >= 200MB)");
        }

        if (Math.abs(memoryGrowth) < 20) {
            System.out.println("✅ Sin crecimiento significativo de memoria");
        } else if (Math.abs(memoryGrowth) < 50) {
            System.out.println("⚠️  Ligero crecimiento de memoria detectado");
        } else {
            System.out.println("❌ Crecimiento considerable de memoria detectado");
        }

        // Verificaciones
        assert exceptions == 0 : "Se encontraron excepciones durante el test: " + exceptions;
        assert totalOps > 0 : "No se realizaron operaciones";
        assert memoryRange < 300 : "Rango de memoria demasiado alto: " + memoryRange + "MB";
        assert Math.abs(memoryGrowth) < 100 : "Crecimiento de memoria excesivo: " + memoryGrowth + "MB";

        System.out.println("\n✅ TEST DE ESTABILIDAD DE MEMORIA COMPLETADO EXITOSAMENTE");
    }

    // Clase para capturar snapshots de memoria
    private static class MemorySnapshot {
        final long timestamp;
        final long usedMemory;
        final long totalMemory;
        final long maxMemory;
        final int totalOperations;
        final int hashOperations;
        final int validateOperations;

        MemorySnapshot(long timestamp, long usedMemory, long totalMemory, long maxMemory,
                      int totalOperations, int hashOperations, int validateOperations) {
            this.timestamp = timestamp;
            this.usedMemory = usedMemory;
            this.totalMemory = totalMemory;
            this.maxMemory = maxMemory;
            this.totalOperations = totalOperations;
            this.hashOperations = hashOperations;
            this.validateOperations = validateOperations;
        }
    }
}
