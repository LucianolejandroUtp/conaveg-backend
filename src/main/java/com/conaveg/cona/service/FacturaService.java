package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.Factura;
import com.conaveg.cona.model.Proveedor;
import com.conaveg.cona.model.User;
import com.conaveg.cona.dto.FacturaDTO;
import com.conaveg.cona.repository.FacturaRepository;

@Service
public class FacturaService {
    @Autowired
    private FacturaRepository facturaRepository;

    public List<FacturaDTO> getAllFacturas() {
        return facturaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public FacturaDTO getFacturaById(Long id) {
        return facturaRepository.findById(id).map(this::toDTO).orElse(null);
    }

    public FacturaDTO saveFactura(FacturaDTO facturaDTO) {
        Factura factura = toEntity(facturaDTO);
        Factura saved = facturaRepository.save(factura);
        return toDTO(saved);
    }

    public FacturaDTO updateFactura(Long id, FacturaDTO facturaDTO) {
        if (facturaRepository.existsById(id)) {
            Factura factura = toEntity(facturaDTO);
            factura.setId(id);
            Factura updated = facturaRepository.save(factura);
            return toDTO(updated);
        }
        return null;
    }
    private FacturaDTO toDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        if (factura.getProveedores() != null) dto.setProveedorId(factura.getProveedores().getId());
        if (factura.getUsuarios() != null) dto.setUsuarioId(factura.getUsuarios().getId());
        dto.setNroFactura(factura.getNroFactura());
        dto.setTipoDocumento(factura.getTipoDocumento());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setFechaVencimiento(factura.getFechaVencimiento());
        dto.setMontoTotal(factura.getMontoTotal());
        dto.setMoneda(factura.getMoneda());
        dto.setDescripcion(factura.getDescripcion());
        dto.setRutaArchivo(factura.getRutaArchivo());
        dto.setNombreArchivo(factura.getNombreArchivo());
        dto.setEstadoFactura(factura.getEstadoFactura());
        return dto;
    }

    private Factura toEntity(FacturaDTO dto) {
        Factura factura = new Factura();
        factura.setId(dto.getId());
        if (dto.getProveedorId() != null) {
            Proveedor proveedor = new Proveedor();
            proveedor.setId(dto.getProveedorId());
            factura.setProveedores(proveedor);
        }
        if (dto.getUsuarioId() != null) {
            User usuario = new User();
            usuario.setId(dto.getUsuarioId());
            factura.setUsuarios(usuario);
        }
        factura.setNroFactura(dto.getNroFactura());
        factura.setTipoDocumento(dto.getTipoDocumento());
        factura.setFechaEmision(dto.getFechaEmision());
        factura.setFechaVencimiento(dto.getFechaVencimiento());
        factura.setMontoTotal(dto.getMontoTotal());
        factura.setMoneda(dto.getMoneda());
        factura.setDescripcion(dto.getDescripcion());
        factura.setRutaArchivo(dto.getRutaArchivo());
        factura.setNombreArchivo(dto.getNombreArchivo());
        factura.setEstadoFactura(dto.getEstadoFactura());
        return factura;
    }

    public void deleteFactura(Long id) {
        facturaRepository.deleteById(id);
    }
}
