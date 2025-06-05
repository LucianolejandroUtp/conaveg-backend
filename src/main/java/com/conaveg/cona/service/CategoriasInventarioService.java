package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.CategoriasInventarioDTO;
import com.conaveg.cona.model.CategoriasInventario;
import com.conaveg.cona.repository.CategoriasInventarioRepository;

@Service
public class CategoriasInventarioService {
    @Autowired
    private CategoriasInventarioRepository categoriasInventarioRepository;

    public List<CategoriasInventarioDTO> getAllCategoriasInventario() {
        return categoriasInventarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CategoriasInventarioDTO getCategoriasInventarioById(Long id) {
        return categoriasInventarioRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public CategoriasInventarioDTO saveCategoriasInventario(CategoriasInventarioDTO categoriaDTO) {
        CategoriasInventario categoria = convertToEntity(categoriaDTO);
        CategoriasInventario saved = categoriasInventarioRepository.save(categoria);
        return convertToDTO(saved);
    }

    public CategoriasInventarioDTO updateCategoriasInventario(Long id, CategoriasInventarioDTO categoriaDTO) {
        if (categoriasInventarioRepository.existsById(id)) {
            CategoriasInventario categoria = convertToEntity(categoriaDTO);
            categoria.setId(id);
            CategoriasInventario updated = categoriasInventarioRepository.save(categoria);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteCategoriasInventario(Long id) {
        categoriasInventarioRepository.deleteById(id);
    }

    private CategoriasInventarioDTO convertToDTO(CategoriasInventario categoria) {
        CategoriasInventarioDTO dto = new CategoriasInventarioDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }

    private CategoriasInventario convertToEntity(CategoriasInventarioDTO dto) {
        CategoriasInventario categoria = new CategoriasInventario();
        categoria.setId(dto.getId());
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }
}
