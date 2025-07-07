package com.conaveg.cona.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "asistencias")
public class Asistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "empleados_id")
    private Empleado empleados;

    @Column(name = "entrada")
    private Instant entrada;

    @Column(name = "salida")
    private Instant salida;

    @Column(name = "tipo_registro")
    private String tipoRegistro;

    @Column(name = "ubicacion_registro")
    private String ubicacionRegistro;

    @Column(name = "metodo_registro")
    private String metodoRegistro;

    @Lob
    @Column(name = "observacion")
    private String observacion;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "estado_asistencia")
    private String estadoAsistencia;

    @ColumnDefault("'ACTIVO'")
    @Lob
    @Column(name = "estado", insertable = false, updatable = false)
    private String estado;
    @ColumnDefault("uuid()")
    @Column(name = "unique_id", length = 36, insertable = false, updatable = false)
    private String uniqueId;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @ColumnDefault("current_timestamp()")
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empleado getEmpleados() {
        return empleados;
    }

    public void setEmpleados(Empleado empleados) {
        this.empleados = empleados;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}
