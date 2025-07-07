package com.conaveg.cona.dto;

public class AsistenciaRegistroRapidoDTO {
    private String nroDocumento;
    private String metodoRegistro;
    private String ubicacionRegistro; // opcional, puede ser null
    private String observacion; // opcional
    private Double latitud; // opcional, coordenada GPS
    private Double longitud; // opcional, coordenada GPS
    private String estadoAsistencia; // opcional, estado específico

    public AsistenciaRegistroRapidoDTO() {}

    public String getNroDocumento() {
        return nroDocumento;
    }
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }
    public String getMetodoRegistro() {
        return metodoRegistro;
    }
    public void setMetodoRegistro(String metodoRegistro) {
        this.metodoRegistro = metodoRegistro;
    }
    public String getUbicacionRegistro() {
        return ubicacionRegistro;
    }
    public void setUbicacionRegistro(String ubicacionRegistro) {
        this.ubicacionRegistro = ubicacionRegistro;
    }
    public String getObservacion() {
        return observacion;
    }
    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getEstadoAsistencia() {
        return estadoAsistencia;
    }

    public void setEstadoAsistencia(String estadoAsistencia) {
        this.estadoAsistencia = estadoAsistencia;
    }
}
