package com.conaveg.cona.dto;

import java.time.Instant;

public class MovimientosInventarioDTO {
    private Long id;
    private Long inventarioId;
    private Integer cantidad;
    private Instant fecha;
    // Agrega aquí los campos relevantes que quieras exponer

    public MovimientosInventarioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInventarioId() { return inventarioId; }
    public void setInventarioId(Long inventarioId) { this.inventarioId = inventarioId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public Instant getFecha() { return fecha; }
    public void setFecha(Instant fecha) { this.fecha = fecha; }
}
