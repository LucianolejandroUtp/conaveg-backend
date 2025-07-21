package com.conaveg.cona.repository;

import com.conaveg.cona.model.Asistencia;
import com.conaveg.cona.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    
    /**
     * Busca un registro de asistencia de ENTRADA para un empleado en un rango de fechas
     * @param empleado El empleado
     * @param fechaInicio Inicio del día
     * @param fechaFin Fin del día  
     * @return Optional con el registro de entrada si existe
     */
    @Query("SELECT a FROM Asistencia a WHERE a.empleados = :empleado " +
           "AND a.tipoRegistro = 'ENTRADA' " +
           "AND a.entrada >= :fechaInicio AND a.entrada <= :fechaFin " +
           "ORDER BY a.entrada DESC")
    Optional<Asistencia> findEntradaByEmpleadoAndDate(
        @Param("empleado") Empleado empleado,
        @Param("fechaInicio") Instant fechaInicio,
        @Param("fechaFin") Instant fechaFin
    );
}
