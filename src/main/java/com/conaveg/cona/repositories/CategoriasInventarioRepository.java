package com.conaveg.cona.repositories;

import com.conaveg.cona.models.CategoriasInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriasInventarioRepository extends JpaRepository<CategoriasInventario, Long> {
}
