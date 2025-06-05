package com.conaveg.cona.dto;

import java.time.LocalDate;

/**
 * DTO para transferencia de datos de Asignaciones de Proyectos a Empleados.
 * Excluye campos generados automáticamente por BD como estado, uniqueId, createdAt, updatedAt.
 */
public class AsignacionesProyectosEmpleadoDTO {
    private Long id;
    private Long empleadoId;
    private Long proyectoId;
    private LocalDate fechaAsignacion;
    private LocalDate fechaFinAsignacion;
    private String rol;

    public AsignacionesProyectosEmpleadoDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public LocalDate getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDate getFechaFinAsignacion() {
        return fechaFinAsignacion;
    }

    public void setFechaFinAsignacion(LocalDate fechaFinAsignacion) {
        this.fechaFinAsignacion = fechaFinAsignacion;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
