package com.conaveg.cona.dto;

import java.time.Instant;

public class AsistenciaDTO {
    private Long id;
    private Long empleadoId;
    private Instant fecha;
    private String estado;
    // Agrega aquí los campos relevantes que quieras exponer

    public AsistenciaDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Long empleadoId) { this.empleadoId = empleadoId; }
    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
