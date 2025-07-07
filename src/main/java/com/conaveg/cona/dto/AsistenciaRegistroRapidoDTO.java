package com.conaveg.cona.dto;

public class AsistenciaRegistroRapidoDTO {
    private String nroDocumento;
    private String metodoRegistro;
    private String ubicacionRegistro; // opcional, puede ser null
    private String observacion; // opcional

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
}
