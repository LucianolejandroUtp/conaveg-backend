package com.conaveg.cona.repositories;

import com.conaveg.cona.models.AsignacionesProyectosEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionesProyectosEmpleadoRepository extends JpaRepository<AsignacionesProyectosEmpleado, Long> {
}
