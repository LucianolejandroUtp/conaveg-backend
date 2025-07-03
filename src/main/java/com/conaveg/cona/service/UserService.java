package com.conaveg.cona.service;
import com.conaveg.cona.dto.UserCreateDTO;
import com.conaveg.cona.dto.UserUpdateDTO;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.User;
import com.conaveg.cona.model.Rol;
import com.conaveg.cona.repository.UserRepository;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.dto.RolDTO;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO).orElse(null);
    }    public UserDTO saveUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setUserName(userCreateDTO.getUserName());
        user.setEmail(userCreateDTO.getEmail());
        
        // Cifrar contraseña con BCrypt antes de guardar
        String encryptedPassword = passwordEncoder.encode(userCreateDTO.getPassword());
        user.setPassword(encryptedPassword);
        
        if (userCreateDTO.getRoleId() != null) {
            Rol rol = new Rol();
            rol.setId(userCreateDTO.getRoleId());
            user.setRole(rol);
        }
        User saved = userRepository.save(user);
        return toDTO(saved);
    }    public UserDTO updateUser(Long id, UserCreateDTO userCreateDTO) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) return null;
            user.setUserName(userCreateDTO.getUserName());
            user.setEmail(userCreateDTO.getEmail());
            
            // Solo cifrar y actualizar contraseña si se proporciona una nueva
            if (userCreateDTO.getPassword() != null && !userCreateDTO.getPassword().trim().isEmpty()) {
                String encryptedPassword = passwordEncoder.encode(userCreateDTO.getPassword());
                user.setPassword(encryptedPassword);
            }
            
            if (userCreateDTO.getRoleId() != null) {
                Rol rol = new Rol();
                rol.setId(userCreateDTO.getRoleId());
                user.setRole(rol);
            }
            User updated = userRepository.save(user);
            return toDTO(updated);
        }
        return null;
    }

    /**
     * Actualiza un usuario usando UserUpdateDTO (contraseña opcional)
     * 
     * @param id ID del usuario a actualizar
     * @param userUpdateDTO DTO con los datos a actualizar
     * @return UserDTO actualizado o null si no existe
     */
    public UserDTO updateUserWithOptionalPassword(Long id, UserUpdateDTO userUpdateDTO) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) return null;
            
            user.setUserName(userUpdateDTO.getUserName());
            user.setEmail(userUpdateDTO.getEmail());
            
            // Solo cifrar y actualizar contraseña si se proporciona una nueva
            if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().trim().isEmpty()) {
                String encryptedPassword = passwordEncoder.encode(userUpdateDTO.getPassword());
                user.setPassword(encryptedPassword);
            }
            // Si no se proporciona contraseña, se mantiene la existente
            
            if (userUpdateDTO.getRoleId() != null) {
                Rol rol = new Rol();
                rol.setId(userUpdateDTO.getRoleId());
                user.setRole(rol);
            }
            
            User updated = userRepository.save(user);
            return toDTO(updated);
        }
        return null;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Valida una contraseña en texto plano contra el hash almacenado
     * Útil para futuras funcionalidades de login/autenticación
     * 
     * @param rawPassword Contraseña en texto plano
     * @param encodedPassword Hash almacenado en base de datos
     * @return true si la contraseña coincide, false si no
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * Cifra una contraseña usando BCrypt
     * Útil para tests de rendimiento y validaciones
     * 
     * @param rawPassword Contraseña en texto plano
     * @return Hash BCrypt de la contraseña
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private UserDTO toDTO(User user) {
        Rol rol = user.getRole();
        RolDTO rolDTO = null;
        if (rol != null) {
            rolDTO = new RolDTO();
            rolDTO.setId(rol.getId());
            rolDTO.setNombre(rol.getNombre());
            rolDTO.setDescripcion(rol.getDescripcion());
            rolDTO.setEstado(rol.getEstado());
            rolDTO.setUniqueId(rol.getUniqueId());
        }
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getEmailVerifiedAt(),
                user.getEstado(),
                user.getUniqueId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                rolDTO
        );
    }
}
