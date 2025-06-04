package com.conaveg.cona.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.conaveg.cona.models.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

     // Aquí puedes agregar métodos personalizados si es necesario
    // Por ejemplo, para buscar roles por nombre:
    // List<Roles> findByNombre(String nombre);
    
}
