package com.conaveg.cona.services;
import com.conaveg.cona.dto.UserCreateDTO;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.models.User;
import com.conaveg.cona.models.Rol;
import com.conaveg.cona.repositories.UserRepository;
import com.conaveg.cona.dto.UserDTO;
import com.conaveg.cona.dto.RolDTO;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public UserDTO saveUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setUserName(userCreateDTO.getUserName());
        user.setEmail(userCreateDTO.getEmail());
        user.setPassword(userCreateDTO.getPassword());
        if (userCreateDTO.getRoleId() != null) {
            Rol rol = new Rol();
            rol.setId(userCreateDTO.getRoleId());
            user.setRole(rol);
        }
        User saved = userRepository.save(user);
        return toDTO(saved);
    }

    public UserDTO updateUser(Long id, UserCreateDTO userCreateDTO) {
        if (userRepository.existsById(id)) {
            User user = userRepository.findById(id).orElse(null);
            if (user == null) return null;
            user.setUserName(userCreateDTO.getUserName());
            user.setEmail(userCreateDTO.getEmail());
            if (userCreateDTO.getPassword() != null) {
                user.setPassword(userCreateDTO.getPassword());
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

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
