package com.conaveg.cona.repository;

import com.conaveg.cona.model.CategoriasInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriasInventarioRepository extends JpaRepository<CategoriasInventario, Long> {
}
