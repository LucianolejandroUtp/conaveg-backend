package com.conaveg.cona.repositories;

import com.conaveg.cona.models.MovimientosInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventario, Long> {
}
