package com.conaveg.cona.dto;

public class InventarioDTO {
    private Long id;
    private String nombre;
    private Integer cantidad;
    // Agrega aquí los campos relevantes que quieras exponer

    public InventarioDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
