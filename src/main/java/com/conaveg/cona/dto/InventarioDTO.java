package com.conaveg.cona.dto;

import java.time.LocalDate;

/**
 * DTO para transferencia de datos de Inventario.
 * Excluye campos generados automáticamente por BD como estado, uniqueId, createdAt, updatedAt.
 */
public class InventarioDTO {
    private Long id;
    private Long categoriaId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String marca;
    private String modelo;
    private String nroSerie;
    private Integer stock;
    private String unidadMedida;
    private LocalDate fechaAquisicion;
    private String estadoConservacion;

    public InventarioDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNroSerie() {
        return nroSerie;
    }

    public void setNroSerie(String nroSerie) {
        this.nroSerie = nroSerie;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public LocalDate getFechaAquisicion() {
        return fechaAquisicion;
    }

    public void setFechaAquisicion(LocalDate fechaAquisicion) {
        this.fechaAquisicion = fechaAquisicion;
    }

    public String getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(String estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }
}
