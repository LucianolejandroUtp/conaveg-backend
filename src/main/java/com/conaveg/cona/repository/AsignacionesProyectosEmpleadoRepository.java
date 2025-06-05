package com.conaveg.cona.repository;

import com.conaveg.cona.model.AsignacionesProyectosEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionesProyectosEmpleadoRepository extends JpaRepository<AsignacionesProyectosEmpleado, Long> {
}
