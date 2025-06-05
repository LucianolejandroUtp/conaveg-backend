package com.conaveg.cona.dto;

import java.time.Instant;

/**
 * DTO para transferencia de datos de Asistencia.
 * Excluye campos generados automáticamente por BD como estado, uniqueId, createdAt, updatedAt.
 */
public class AsistenciaDTO {
    private Long id;
    private Long empleadoId;
    private Instant entrada;
    private Instant salida;
    private String tipoRegistro;
    private String ubicacionRegistro;
    private String metodoRegistro;
    private String observacion;

    public AsistenciaDTO() {}

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

    public Instant getEntrada() {
        return entrada;
    }

    public void setEntrada(Instant entrada) {
        this.entrada = entrada;
    }

    public Instant getSalida() {
        return salida;
    }

    public void setSalida(Instant salida) {
        this.salida = salida;
    }

    public String getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(String tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getUbicacionRegistro() {
        return ubicacionRegistro;
    }

    public void setUbicacionRegistro(String ubicacionRegistro) {
        this.ubicacionRegistro = ubicacionRegistro;
    }

    public String getMetodoRegistro() {
        return metodoRegistro;
    }

    public void setMetodoRegistro(String metodoRegistro) {
        this.metodoRegistro = metodoRegistro;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
