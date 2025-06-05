package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.MovimientosInventarioDTO;
import com.conaveg.cona.model.MovimientosInventario;
import com.conaveg.cona.model.Inventario;
import com.conaveg.cona.model.Empleado;
import com.conaveg.cona.model.Proyecto;
import com.conaveg.cona.repository.MovimientosInventarioRepository;
import com.conaveg.cona.repository.InventarioRepository;
import com.conaveg.cona.repository.EmpleadoRepository;
import com.conaveg.cona.repository.ProyectoRepository;

@Service
public class MovimientosInventarioService {
    @Autowired
    private MovimientosInventarioRepository movimientosInventarioRepository;
    
    @Autowired
    private InventarioRepository inventarioRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<MovimientosInventarioDTO> getAllMovimientosInventario() {
        return movimientosInventarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public MovimientosInventarioDTO getMovimientosInventarioById(Long id) {
        MovimientosInventario movimiento = movimientosInventarioRepository.findById(id).orElse(null);
        return movimiento != null ? convertToDTO(movimiento) : null;
    }

    public MovimientosInventarioDTO saveMovimientosInventario(MovimientosInventarioDTO movimientoDTO) {
        MovimientosInventario movimiento = convertToEntity(movimientoDTO);
        MovimientosInventario saved = movimientosInventarioRepository.save(movimiento);
        return convertToDTO(saved);
    }

    public MovimientosInventarioDTO updateMovimientosInventario(Long id, MovimientosInventarioDTO movimientoDTO) {
        if (movimientosInventarioRepository.existsById(id)) {
            MovimientosInventario movimiento = convertToEntity(movimientoDTO);
            movimiento.setId(id);
            MovimientosInventario updated = movimientosInventarioRepository.save(movimiento);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteMovimientosInventario(Long id) {
        movimientosInventarioRepository.deleteById(id);
    }

    private MovimientosInventarioDTO convertToDTO(MovimientosInventario movimiento) {
        MovimientosInventarioDTO dto = new MovimientosInventarioDTO();
        dto.setId(movimiento.getId());
        dto.setInventarioId(movimiento.getInventario() != null ? movimiento.getInventario().getId() : null);
        dto.setEmpleadoIdAsigna(movimiento.getEmpleadosIdAsigna() != null ? movimiento.getEmpleadosIdAsigna().getId() : null);
        dto.setEmpleadoIdRecibe(movimiento.getEmpleadosIdRecibe() != null ? movimiento.getEmpleadosIdRecibe().getId() : null);
        dto.setProyectoId(movimiento.getProyectos() != null ? movimiento.getProyectos().getId() : null);
        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setCantidad(movimiento.getCantidad());
        dto.setFechaMovimiento(movimiento.getFechaMovimiento());
        dto.setObservacion(movimiento.getObservacion());
        return dto;
    }

    private MovimientosInventario convertToEntity(MovimientosInventarioDTO dto) {
        MovimientosInventario movimiento = new MovimientosInventario();
        movimiento.setId(dto.getId());
        
        if (dto.getInventarioId() != null) {
            Inventario inventario = inventarioRepository.findById(dto.getInventarioId()).orElse(null);
            movimiento.setInventario(inventario);
        }
        
        if (dto.getEmpleadoIdAsigna() != null) {
            Empleado empleadoAsigna = empleadoRepository.findById(dto.getEmpleadoIdAsigna()).orElse(null);
            movimiento.setEmpleadosIdAsigna(empleadoAsigna);
        }
        
        if (dto.getEmpleadoIdRecibe() != null) {
            Empleado empleadoRecibe = empleadoRepository.findById(dto.getEmpleadoIdRecibe()).orElse(null);
            movimiento.setEmpleadosIdRecibe(empleadoRecibe);
        }
        
        if (dto.getProyectoId() != null) {
            Proyecto proyecto = proyectoRepository.findById(dto.getProyectoId()).orElse(null);
            movimiento.setProyectos(proyecto);
        }
        
        movimiento.setTipoMovimiento(dto.getTipoMovimiento());
        movimiento.setCantidad(dto.getCantidad());
        movimiento.setFechaMovimiento(dto.getFechaMovimiento());
        movimiento.setObservacion(dto.getObservacion());
        return movimiento;
    }
}
