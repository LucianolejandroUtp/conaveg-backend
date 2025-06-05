package com.conaveg.cona.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.CategoriasInventario;
import com.conaveg.cona.repository.CategoriasInventarioRepository;

@Service
public class CategoriasInventarioService {
    @Autowired
    private CategoriasInventarioRepository categoriasInventarioRepository;

    public List<CategoriasInventario> getAllCategoriasInventario() {
        return categoriasInventarioRepository.findAll();
    }

    public CategoriasInventario getCategoriasInventarioById(Long id) {
        return categoriasInventarioRepository.findById(id).orElse(null);
    }

    public CategoriasInventario saveCategoriasInventario(CategoriasInventario categoria) {
        return categoriasInventarioRepository.save(categoria);
    }

    public CategoriasInventario updateCategoriasInventario(Long id, CategoriasInventario categoria) {
        if (categoriasInventarioRepository.existsById(id)) {
            categoria.setId(id);
            return categoriasInventarioRepository.save(categoria);
        }
        return null;
    }

    public void deleteCategoriasInventario(Long id) {
        categoriasInventarioRepository.deleteById(id);
    }
}
