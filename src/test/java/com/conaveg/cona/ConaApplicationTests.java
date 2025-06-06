package com.conaveg.cona;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConaApplicationTests {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Test
	void contextLoads() {
		// Verificar que el contexto de Spring arranca correctamente
		assertNotNull(passwordEncoder, "BCryptPasswordEncoder bean debe estar disponible");
	}

	@Test
	void passwordEncoderWorks() {
		// Verificar que el cifrado de contraseñas funciona correctamente
		String plainPassword = "testPassword123";
		String encodedPassword = passwordEncoder.encode(plainPassword);
		
		assertNotNull(encodedPassword, "La contraseña cifrada no debe ser null");
		assertNotEquals(plainPassword, encodedPassword, "La contraseña cifrada debe ser diferente a la texto plano");
		assertTrue(passwordEncoder.matches(plainPassword, encodedPassword), 
			"La validación de contraseña debe funcionar correctamente");
		assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword),
			"Una contraseña incorrecta no debe validar como correcta");
	}

}
