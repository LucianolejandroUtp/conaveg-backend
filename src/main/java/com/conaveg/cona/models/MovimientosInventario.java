package com.conaveg.cona.models;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "movimientos_inventario")
public class MovimientosInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "empleados_id_asigna")
    private Empleado empleadosIdAsigna;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "empleados_id_recibe")
    private Empleado empleadosIdRecibe;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "proyectos_id")
    private Proyecto proyectos;

    @Column(name = "tipo_movimiento")
    private String tipoMovimiento;

    @ColumnDefault("0")
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "fecha_movimiento")
    private Instant fechaMovimiento;

    @Lob
    @Column(name = "observacion")
    private String observacion;

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

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public Empleado getEmpleadosIdAsigna() {
        return empleadosIdAsigna;
    }

    public void setEmpleadosIdAsigna(Empleado empleadosIdAsigna) {
        this.empleadosIdAsigna = empleadosIdAsigna;
    }

    public Empleado getEmpleadosIdRecibe() {
        return empleadosIdRecibe;
    }

    public void setEmpleadosIdRecibe(Empleado empleadosIdRecibe) {
        this.empleadosIdRecibe = empleadosIdRecibe;
    }

    public Proyecto getProyectos() {
        return proyectos;
    }

    public void setProyectos(Proyecto proyectos) {
        this.proyectos = proyectos;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Instant getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Instant fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
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