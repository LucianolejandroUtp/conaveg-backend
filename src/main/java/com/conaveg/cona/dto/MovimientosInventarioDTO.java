package com.conaveg.cona.dto;

import java.time.Instant;

/**
 * DTO para MovimientosInventario
 * Expone solo los campos relevantes sin incluir campos generados automáticamente por la BD
 */
public class MovimientosInventarioDTO {
    private Long id;
    private Long inventarioId;
    private Long empleadoIdAsigna;
    private Long empleadoIdRecibe;
    private Long proyectoId;
    private String tipoMovimiento;
    private Integer cantidad;
    private Instant fechaMovimiento;
    private String observacion;

    public MovimientosInventarioDTO() {}

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public Long getInventarioId() { 
        return inventarioId; 
    }
    
    public void setInventarioId(Long inventarioId) { 
        this.inventarioId = inventarioId; 
    }
    
    public Long getEmpleadoIdAsigna() { 
        return empleadoIdAsigna; 
    }
    
    public void setEmpleadoIdAsigna(Long empleadoIdAsigna) { 
        this.empleadoIdAsigna = empleadoIdAsigna; 
    }
    
    public Long getEmpleadoIdRecibe() { 
        return empleadoIdRecibe; 
    }
    
    public void setEmpleadoIdRecibe(Long empleadoIdRecibe) { 
        this.empleadoIdRecibe = empleadoIdRecibe; 
    }
    
    public Long getProyectoId() { 
        return proyectoId; 
    }
    
    public void setProyectoId(Long proyectoId) { 
        this.proyectoId = proyectoId; 
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
}
