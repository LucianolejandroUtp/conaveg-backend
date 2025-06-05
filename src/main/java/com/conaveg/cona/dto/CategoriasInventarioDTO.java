package com.conaveg.cona.dto;

/**
 * DTO para transferencia de datos de Categorías de Inventario.
 * Excluye campos generados automáticamente por BD como estado, uniqueId, createdAt, updatedAt.
 */
public class CategoriasInventarioDTO {
    private Long id;
    private String nombre;
    private String descripcion;

    public CategoriasInventarioDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
