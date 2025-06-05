package com.conaveg.cona.dto;

public class AsignacionesProyectosEmpleadoDTO {
    private Long id;
    private Long empleadoId;
    private Long proyectoId;
    // Agrega aquí los campos relevantes que quieras exponer

    public AsignacionesProyectosEmpleadoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public Long getProyectoId() { return proyectoId; }
    public void setProyectoId(Long proyectoId) { this.proyectoId = proyectoId; }
}
