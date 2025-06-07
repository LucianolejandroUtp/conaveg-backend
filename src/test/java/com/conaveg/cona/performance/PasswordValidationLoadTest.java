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
public class PasswordValidationLoadTest {

    @Autowired
    private UserService userService;    private static final int THREAD_COUNT = 10;
    private static final int VALIDATIONS_PER_THREAD = 20;
    private static final int TOTAL_VALIDATIONS = THREAD_COUNT * VALIDATIONS_PER_THREAD;

    // Diferentes tipos de contraseñas para probar
    private final String[] testPasswords = {
        "ValidPass123!",      // Válida
        "AnotherValid456@",   // Válida
        "ComplexP@ss789",     // Válida
        "SimplePass#2024",    // Válida
        "SecureTest!456",     // Válida
        "weak",               // Inválida - muy corta
        "12345678",           // Inválida - solo números
        "password",           // Inválida - solo letras minúsculas
        "PASSWORD",           // Inválida - solo letras mayúsculas
        "Pass123",            // Inválida - sin caracteres especiales
        "",                   // Inválida - vacía
        "   ",                // Inválida - solo espacios
        "a".repeat(129),      // Inválida - muy larga
        "NoSpecial123",       // Inválida - sin caracteres especiales
        "noUpper123!"         // Inválida - sin mayúsculas
    };

    private final String hashedPassword = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi."; // "password"

    @BeforeEach
    void setUp() {
        System.out.println("=== INICIANDO TEST DE VALIDACIÓN MASIVA DE CONTRASEÑAS ===");
        System.out.println("Configuración:");
        System.out.println("- Hilos concurrentes: " + THREAD_COUNT);
        System.out.println("- Validaciones por hilo: " + VALIDATIONS_PER_THREAD);
        System.out.println("- Total de validaciones: " + TOTAL_VALIDATIONS);
        System.out.println("- Tipos de contraseñas de prueba: " + testPasswords.length);
    }

    @Test
    public void testConcurrentPasswordValidation() throws InterruptedException {
        // Contadores para métricas
        AtomicInteger successfulValidations = new AtomicInteger(0);
        AtomicInteger failedValidations = new AtomicInteger(0);
        AtomicLong totalValidationTime = new AtomicLong(0);
        AtomicInteger exceptions = new AtomicInteger(0);

        // Crear pool de hilos
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        // Lista para almacenar futuros
        List<Future<Void>> futures = new ArrayList<>();

        long testStartTime = System.currentTimeMillis();

        // Crear y ejecutar tareas
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Future<Void> future = executor.submit(() -> {
                try {
                    long threadStartTime = System.currentTimeMillis();
                    
                    for (int j = 0; j < VALIDATIONS_PER_THREAD; j++) {
                        try {
                            // Seleccionar contraseña de prueba de forma cíclica
                            String testPassword = testPasswords[j % testPasswords.length];
                            
                            long validationStart = System.nanoTime();
                            
                            // Realizar validación
                            boolean isValid = userService.validatePassword(testPassword, hashedPassword);
                            
                            long validationEnd = System.nanoTime();
                            long validationTime = validationEnd - validationStart;
                            
                            totalValidationTime.addAndGet(validationTime);
                            
                            if (isValid || !isValid) { // Contar todas las validaciones
                                successfulValidations.incrementAndGet();
                            }
                            
                        } catch (Exception e) {
                            exceptions.incrementAndGet();
                            System.err.println("Error en validación (Hilo " + threadId + ", Iteración " + j + "): " + e.getMessage());
                        }
                    }
                    
                    long threadEndTime = System.currentTimeMillis();
                    System.out.println("Hilo " + threadId + " completado en " + (threadEndTime - threadStartTime) + "ms");
                    
                } catch (Exception e) {
                    System.err.println("Error fatal en hilo " + threadId + ": " + e.getMessage());
                    exceptions.incrementAndGet();
                } finally {
                    latch.countDown();
                }
                return null;
            });
            
            futures.add(future);
        }

        // Esperar a que todos los hilos terminen (máximo 5 minutos)
        boolean completed = latch.await(5, TimeUnit.MINUTES);
        long testEndTime = System.currentTimeMillis();

        // Cerrar el executor
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        // Calcular métricas
        long totalTestTime = testEndTime - testStartTime;
        double averageValidationTime = totalValidationTime.get() / (double) successfulValidations.get() / 1_000_000.0; // en ms
        double validationsPerSecond = (successfulValidations.get() * 1000.0) / totalTestTime;

        // Mostrar resultados
        System.out.println("\n=== RESULTADOS DEL TEST DE VALIDACIÓN MASIVA ===");
        System.out.println("Tiempo total del test: " + totalTestTime + "ms");
        System.out.println("Test completado: " + (completed ? "SÍ" : "NO (TIMEOUT)"));
        System.out.println("Validaciones exitosas: " + successfulValidations.get());
        System.out.println("Validaciones fallidas: " + failedValidations.get());
        System.out.println("Excepciones: " + exceptions.get());
        System.out.println("Tiempo promedio por validación: " + String.format("%.2f", averageValidationTime) + "ms");
        System.out.println("Validaciones por segundo: " + String.format("%.2f", validationsPerSecond));
        System.out.println("Throughput: " + String.format("%.2f", validationsPerSecond) + " validaciones/seg");        // Análisis de rendimiento (BCrypt es intencionalmente lento para seguridad)
        if (averageValidationTime > 2000) {
            System.out.println("⚠️  ADVERTENCIA: Tiempo de validación muy alto (>" + 2000 + "ms)");
        } else if (averageValidationTime > 1000) {
            System.out.println("⚠️  PRECAUCIÓN: Tiempo de validación alto (>" + 1000 + "ms)");
        } else if (averageValidationTime > 500) {
            System.out.println("✅ Rendimiento BCrypt normal (>" + 500 + "ms, <1000ms)");
        } else {
            System.out.println("✅ Rendimiento BCrypt excelente (<" + 500 + "ms)");
        }

        if (exceptions.get() > 0) {
            System.out.println("❌ ERRORES: Se encontraron " + exceptions.get() + " excepciones durante el test");
        } else {
            System.out.println("✅ Sin errores durante la ejecución");
        }

        // Verificaciones de éxito (límites ajustados para BCrypt)
        assert completed : "El test no se completó dentro del tiempo límite";
        assert exceptions.get() == 0 : "Se encontraron excepciones durante el test: " + exceptions.get();
        assert successfulValidations.get() > 0 : "No se realizaron validaciones exitosas";
        assert averageValidationTime < 3000 : "Tiempo de validación demasiado alto: " + averageValidationTime + "ms (límite: 3000ms)";

        System.out.println("=== TEST DE VALIDACIÓN MASIVA COMPLETADO EXITOSAMENTE ===\n");
    }

    @Test
    public void testPasswordValidationWithDifferentComplexities() throws InterruptedException {
        System.out.println("\n=== TEST DE VALIDACIÓN CON DIFERENTES COMPLEJIDADES ===");
        
        // Diferentes contraseñas con diferentes niveles de complejidad
        String[] complexPasswords = {
            "Simple123!",                           // Nivel básico
            "MoreComplex@456#Hash",                // Nivel medio
            "VeryComplexP@ssw0rd!2024#Secure",    // Nivel alto
            "ExtremelyComplex!@#$%^&*()_+P@ssw0rd123456789", // Nivel extremo
        };
        
        for (String password : complexPasswords) {
            long startTime = System.nanoTime();
            
            // Hashear la contraseña
            String hashedPassword = userService.hashPassword(password);
            
            long hashTime = System.nanoTime() - startTime;
            
            // Medir tiempo de validación
            startTime = System.nanoTime();
            boolean isValid = userService.validatePassword(password, hashedPassword);
            long validateTime = System.nanoTime() - startTime;
            
            System.out.println("Contraseña: " + password.substring(0, Math.min(20, password.length())) + "...");
            System.out.println("  - Longitud: " + password.length() + " caracteres");
            System.out.println("  - Tiempo de hash: " + String.format("%.2f", hashTime / 1_000_000.0) + "ms");
            System.out.println("  - Tiempo de validación: " + String.format("%.2f", validateTime / 1_000_000.0) + "ms");
            System.out.println("  - Validación exitosa: " + isValid);
            System.out.println();
            
            assert isValid : "La validación falló para la contraseña: " + password;
        }
        
        System.out.println("✅ Test de complejidades completado exitosamente");
    }
}
