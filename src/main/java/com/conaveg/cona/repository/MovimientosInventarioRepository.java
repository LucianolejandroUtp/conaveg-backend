package com.conaveg.cona.repository;

import com.conaveg.cona.model.MovimientosInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventario, Long> {
}
