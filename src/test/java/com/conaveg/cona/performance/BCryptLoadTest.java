package com.conaveg.cona.performance;

import com.conaveg.cona.dto.UserCreateDTO;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.model.Rol;
import com.conaveg.cona.repository.RolRepository;
import com.conaveg.cona.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de carga para evaluar el rendimiento del cifrado BCrypt
 * bajo condiciones de alta concurrencia
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-loadtest.properties")
class BCryptLoadTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private RolRepository rolRepository;

    private static final int CONCURRENT_USERS = 50;
    private static final int THREAD_POOL_SIZE = 10;
    private static final int TIMEOUT_SECONDS = 60;
    
    private Long testRoleId;

    @BeforeEach
    void setUp() {
        // Crear rol de prueba si no existe
        if (rolRepository.count() == 0) {
            Rol testRole = new Rol();
            testRole.setNombre("LOAD_TEST_ROLE");
            testRole.setDescripcion("Rol para tests de carga");
            testRole.setEstado("A");
            testRole = rolRepository.save(testRole);
            testRoleId = testRole.getId();
        } else {
            testRoleId = rolRepository.findAll().get(0).getId();
        }
        
        // Limpiar cualquier estado previo si es necesario
        System.gc(); // Sugerir garbage collection antes de cada test
    }

    @Test
    @Timeout(value = TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    void testConcurrentUserCreation() {
        System.out.println("🚀 Iniciando test de carga: " + CONCURRENT_USERS + " usuarios concurrentes");
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // Crear usuarios concurrentemente
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    UserCreateDTO user = createTestUser(userId);
                    
                    // Medir tiempo individual de creación
                    long userStartTime = System.nanoTime();
                    UserDTO createdUser = userService.saveUser(user);
                    long userEndTime = System.nanoTime();
                    long userCreationTime = (userEndTime - userStartTime) / 1_000_000; // ms
                    
                    assertNotNull(createdUser, "Usuario debe ser creado exitosamente");
                    assertNotNull(createdUser.getId(), "Usuario debe tener ID asignado");
                    assertEquals(user.getUserName(), createdUser.getUserName(), "Nombre de usuario debe coincidir");
                    
                    successCount.incrementAndGet();
                    
                    // Log de progreso cada 10 usuarios
                    if (userId % 10 == 0) {
                        System.out.println("✅ Usuario " + userId + " creado en " + userCreationTime + "ms");
                    }
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("❌ Error creando usuario " + userId + ": " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            boolean completed = latch.await(TIMEOUT_SECONDS - 5, TimeUnit.SECONDS);
            assertTrue(completed, "Todos los usuarios deben procesarse dentro del tiempo límite");
        } catch (InterruptedException e) {
            fail("Test interrumpido: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        
        executor.shutdown();
        
        // Cálculo de métricas
        double throughput = (double) successCount.get() / (totalTime / 1000.0);
        double errorRate = (double) errorCount.get() / CONCURRENT_USERS * 100;
        
        // Reporte de resultados
        System.out.println("\n📊 RESULTADOS DEL TEST DE CARGA:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👥 Usuarios objetivo: " + CONCURRENT_USERS);
        System.out.println("✅ Usuarios creados exitosamente: " + successCount.get());
        System.out.println("❌ Errores: " + errorCount.get());
        System.out.println("⏱️  Tiempo total: " + totalTime + "ms");
        System.out.println("🚀 Throughput: " + String.format("%.2f", throughput) + " usuarios/segundo");
        System.out.println("📈 Tasa de error: " + String.format("%.2f", errorRate) + "%");
        System.out.println("🧵 Threads utilizados: " + THREAD_POOL_SIZE);
        
        // Verificaciones de rendimiento
        assertEquals(CONCURRENT_USERS, successCount.get(), "Todos los usuarios deben crearse exitosamente");
        assertEquals(0, errorCount.get(), "No debe haber errores durante la creación");
        assertTrue(totalTime < 45000, "Debe completarse en menos de 45 segundos (actual: " + totalTime + "ms)");
        assertTrue(throughput > 2.0, "Throughput debe ser mayor a 2 usuarios/segundo (actual: " + throughput + ")");
        assertTrue(errorRate == 0.0, "Tasa de error debe ser 0% (actual: " + errorRate + "%)");
        
        System.out.println("🎯 Test de carga completado exitosamente!");
    }

    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    void testConcurrentUserCreationStressTest() {
        System.out.println("🔥 Iniciando test de estrés con mayor carga");
        
        final int STRESS_USERS = 20; // Menor cantidad pero más intensivo
        final int STRESS_THREADS = 20; // Más threads para mayor presión
        
        ExecutorService executor = Executors.newFixedThreadPool(STRESS_THREADS);
        CountDownLatch latch = new CountDownLatch(STRESS_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < STRESS_USERS; i++) {
            final int userId = i + 1000; // Evitar conflictos con test anterior
            executor.submit(() -> {
                try {
                    UserCreateDTO user = createTestUser(userId);
                    UserDTO createdUser = userService.saveUser(user);
                    assertNotNull(createdUser);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("❌ Error en test de estrés: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await(25, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            fail("Test de estrés interrumpido");
        }
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double throughput = (double) successCount.get() / (totalTime / 1000.0);
        
        executor.shutdown();
        
        System.out.println("\n🔥 RESULTADOS DEL TEST DE ESTRÉS:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ Usuarios creados: " + successCount.get() + "/" + STRESS_USERS);
        System.out.println("⏱️  Tiempo total: " + totalTime + "ms");
        System.out.println("🚀 Throughput estrés: " + String.format("%.2f", throughput) + " usuarios/segundo");
        
        // Verificaciones más relajadas para test de estrés
        assertTrue(successCount.get() >= STRESS_USERS * 0.9, "Al menos 90% de usuarios deben crearse en test de estrés");
        assertTrue(totalTime < 20000, "Test de estrés debe completarse en menos de 20 segundos");
    }

    @Test
    void testMemoryUsageDuringLoad() {
        System.out.println("🧠 Iniciando test de uso de memoria durante carga");
        
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("💾 Memoria inicial: " + (initialMemory / 1024 / 1024) + "MB");
        
        final int MEMORY_TEST_USERS = 30;
        
        // Crear usuarios y medir incremento de memoria
        for (int i = 0; i < MEMORY_TEST_USERS; i++) {
            UserCreateDTO user = createTestUser(i + 2000); // Evitar conflictos
            UserDTO createdUser = userService.saveUser(user);
            assertNotNull(createdUser);
            
            if (i % 10 == 0) {
                long currentMemory = runtime.totalMemory() - runtime.freeMemory();
                System.out.println("💾 Memoria tras " + i + " usuarios: " + (currentMemory / 1024 / 1024) + "MB");
            }
        }
        
        // Forzar garbage collection para medición más precisa
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        System.out.println("\n🧠 RESULTADOS DEL TEST DE MEMORIA:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💾 Memoria inicial: " + (initialMemory / 1024 / 1024) + "MB");
        System.out.println("💾 Memoria final: " + (finalMemory / 1024 / 1024) + "MB");
        System.out.println("📈 Incremento: " + (memoryIncrease / 1024 / 1024) + "MB");
        System.out.println("👥 Usuarios creados: " + MEMORY_TEST_USERS);
        
        // Verificar que el incremento de memoria sea razonable
        assertTrue(memoryIncrease < 100 * 1024 * 1024, "Incremento de memoria debe ser < 100MB (actual: " + (memoryIncrease / 1024 / 1024) + "MB)");
    }    /**
     * Crea un usuario de prueba con datos únicos
     */
    private UserCreateDTO createTestUser(int id) {
        UserCreateDTO user = new UserCreateDTO();
        user.setUserName("loadtest_user_" + id);
        user.setEmail("loadtest" + id + "@performance.test");
        user.setPassword("LoadTest123!"); // Cumple con las validaciones de seguridad
        user.setRoleId(testRoleId); // Usar el rol creado dinámicamente
        return user;
    }
}
