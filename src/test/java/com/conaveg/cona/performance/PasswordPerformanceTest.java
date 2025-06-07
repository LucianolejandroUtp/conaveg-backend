package com.conaveg.cona.performance;

import com.conaveg.cona.dto.UserCreateDTO;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.model.Rol;
import com.conaveg.cona.repository.RolRepository;
import com.conaveg.cona.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test simple para validar la funcionalidad básica antes de tests de carga
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-loadtest.properties")
class PasswordPerformanceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private RolRepository rolRepository;
    
    private Long testRoleId;

    @BeforeEach
    void setUp() {
        // Crear rol de prueba si no existe
        if (rolRepository.count() == 0) {
            Rol testRole = new Rol();
            testRole.setNombre("TEST_ROLE");
            testRole.setDescripcion("Rol para tests");
            testRole.setEstado("A");
            testRole = rolRepository.save(testRole);
            testRoleId = testRole.getId();
        } else {
            testRoleId = rolRepository.findAll().get(0).getId();
        }
    }

    @Test
    void testSingleUserCreation() {
        System.out.println("🧪 Test simple: crear un usuario");
        
        UserCreateDTO user = new UserCreateDTO();
        user.setUserName("test_user_simple");
        user.setEmail("simple@test.com");
        user.setPassword("SimpleTest123!");
        user.setRoleId(testRoleId);
        
        UserDTO createdUser = userService.saveUser(user);
        
        assertNotNull(createdUser, "Usuario debe ser creado");
        assertNotNull(createdUser.getId(), "Usuario debe tener ID");
        assertEquals(user.getUserName(), createdUser.getUserName(), "Nombre debe coincidir");
        
        System.out.println("✅ Usuario creado exitosamente con ID: " + createdUser.getId());
    }

    @Test 
    void testPasswordEncryptionPerformance() {
        System.out.println("⚡ Test de rendimiento de cifrado individual");
        
        String password = "PerformanceTest123!";
        int iterations = 10;
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < iterations; i++) {
            String encoded = passwordEncoder.encode(password);
            assertNotNull(encoded);
            assertTrue(passwordEncoder.matches(password, encoded));
        }
        
        long endTime = System.nanoTime();
        long avgTime = (endTime - startTime) / iterations / 1_000_000; // ms
        
        System.out.println("⏱️  Tiempo promedio por cifrado: " + avgTime + "ms");
        assertTrue(avgTime < 500, "Cifrado debe tomar menos de 500ms (actual: " + avgTime + "ms)");
    }

    @Test
    void testPasswordValidationPerformance() {
        System.out.println("🔍 Test de rendimiento de validación");
        
        String password = "ValidationTest123!";
        String encoded = passwordEncoder.encode(password);
        int validations = 100;
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < validations; i++) {
            boolean result = passwordEncoder.matches(password, encoded);
            assertTrue(result, "Validación debe ser exitosa");
        }
        
        long endTime = System.nanoTime();
        long avgTime = (endTime - startTime) / validations / 1_000_000; // ms
          System.out.println("⏱️  Tiempo promedio por validación: " + avgTime + "ms");
        assertTrue(avgTime < 200, "Validación debe tomar menos de 200ms en promedio (actual: " + avgTime + "ms)");
    }
}
