package com.conaveg.cona.repository;

import com.conaveg.cona.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca un usuario por su email
     */
    Optional<User> findByEmail(String email);
}
