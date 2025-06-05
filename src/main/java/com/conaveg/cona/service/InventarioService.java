package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.InventarioDTO;
import com.conaveg.cona.model.Inventario;
import com.conaveg.cona.model.CategoriasInventario;
import com.conaveg.cona.repository.InventarioRepository;
import com.conaveg.cona.repository.CategoriasInventarioRepository;

@Service
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;
    
    @Autowired
    private CategoriasInventarioRepository categoriasInventarioRepository;

    public List<InventarioDTO> getAllInventario() {
        return inventarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InventarioDTO getInventarioById(Long id) {
        return inventarioRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public InventarioDTO saveInventario(InventarioDTO inventarioDTO) {
        Inventario inventario = convertToEntity(inventarioDTO);
        Inventario saved = inventarioRepository.save(inventario);
        return convertToDTO(saved);
    }

    public InventarioDTO updateInventario(Long id, InventarioDTO inventarioDTO) {
        if (inventarioRepository.existsById(id)) {
            Inventario inventario = convertToEntity(inventarioDTO);
            inventario.setId(id);
            Inventario updated = inventarioRepository.save(inventario);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteInventario(Long id) {
        inventarioRepository.deleteById(id);
    }

    private InventarioDTO convertToDTO(Inventario inventario) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(inventario.getId());
        dto.setCategoriaId(inventario.getCategoria() != null ? inventario.getCategoria().getId() : null);
        dto.setCodigo(inventario.getCodigo());
        dto.setNombre(inventario.getNombre());
        dto.setDescripcion(inventario.getDescripcion());
        dto.setMarca(inventario.getMarca());
        dto.setModelo(inventario.getModelo());
        dto.setNroSerie(inventario.getNroSerie());
        dto.setStock(inventario.getStock());
        dto.setUnidadMedida(inventario.getUnidadMedida());
        dto.setFechaAquisicion(inventario.getFechaAquisicion());
        dto.setEstadoConservacion(inventario.getEstadoConservacion());
        return dto;
    }

    private Inventario convertToEntity(InventarioDTO dto) {
        Inventario inventario = new Inventario();
        inventario.setId(dto.getId());
        
        // Asignar categoría si se proporciona categoriaId
        if (dto.getCategoriaId() != null) {
            CategoriasInventario categoria = categoriasInventarioRepository.findById(dto.getCategoriaId()).orElse(null);
            inventario.setCategoria(categoria);
        }
        
        inventario.setCodigo(dto.getCodigo());
        inventario.setNombre(dto.getNombre());
        inventario.setDescripcion(dto.getDescripcion());
        inventario.setMarca(dto.getMarca());
        inventario.setModelo(dto.getModelo());
        inventario.setNroSerie(dto.getNroSerie());
        inventario.setStock(dto.getStock());
        inventario.setUnidadMedida(dto.getUnidadMedida());
        inventario.setFechaAquisicion(dto.getFechaAquisicion());
        inventario.setEstadoConservacion(dto.getEstadoConservacion());
        return inventario;
    }
}
