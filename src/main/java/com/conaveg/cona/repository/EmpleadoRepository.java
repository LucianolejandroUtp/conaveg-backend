package com.conaveg.cona.repository;

import com.conaveg.cona.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    java.util.Optional<Empleado> findByNroDocumento(String nroDocumento);
}
