package com.conaveg.cona.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RolDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String nombre;
    private String descripcion;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String estado;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String uniqueId;

    public RolDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUniqueId() { return uniqueId; }
    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }
}
