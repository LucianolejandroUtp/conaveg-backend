package com.conaveg.cona.dto;

public class ProveedorDTO {
    private Long id;
    private String nombre;
    private String contacto;
    // Agrega aquí los campos relevantes que quieras exponer

    public ProveedorDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
}
