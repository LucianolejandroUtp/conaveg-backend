package com.conaveg.cona.service;

import com.conaveg.cona.dto.LoginRequestDTO;
import com.conaveg.cona.dto.LoginResponseDTO;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.model.User;
import com.conaveg.cona.repository.UserRepository;
import com.conaveg.cona.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio de autenticación para manejo de login y tokens
 */
@Service
public class AuthenticationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    /**
     * Autentica un usuario y genera un token JWT
     */
    public LoginResponseDTO authenticateUser(LoginRequestDTO loginRequest) {
        // Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User user = userOptional.get();
        
        // Verificar contraseña
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
          // Generar token JWT
        String roleName = user.getRole() != null ? user.getRole().getNombre() : "USER";
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), roleName);
        
        // Convertir a DTO
        UserDTO userDTO = userService.getUserById(user.getId());
        
        // Crear respuesta
        return new LoginResponseDTO(token, userDTO, jwtUtil.getExpirationTime());
    }
    
    /**
     * Valida un token JWT
     */
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
      /**
     * Obtiene información del usuario desde un token (versión optimizada)
     */
    public UserDTO getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token inválido");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserDTO user = userService.getUserById(userId);
        
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return user;
    }
    
    /**
     * Obtiene información básica del usuario por email (para el filtro JWT)
     */
    public UserDTO getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        return userService.getUserById(userOptional.get().getId());
    }
}
