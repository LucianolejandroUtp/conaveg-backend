package com.conaveg.cona.dto;

public class ProveedorDTO {
    private Long id;
    private String ruc;
    private String razonSocial;
    private String direccion;
    private String telefono;

    public ProveedorDTO() {}

    public ProveedorDTO(Long id, String ruc, String razonSocial, String direccion, String telefono) {
        this.id = id;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
