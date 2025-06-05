package com.conaveg.cona.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.Inventario;
import com.conaveg.cona.repository.InventarioRepository;

@Service
public class InventarioService {
    @Autowired
    private InventarioRepository inventarioRepository;

    public List<Inventario> getAllInventario() {
        return inventarioRepository.findAll();
    }

    public Inventario getInventarioById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    public Inventario saveInventario(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public Inventario updateInventario(Long id, Inventario inventario) {
        if (inventarioRepository.existsById(id)) {
            inventario.setId(id);
            return inventarioRepository.save(inventario);
        }
        return null;
    }

    public void deleteInventario(Long id) {
        inventarioRepository.deleteById(id);
    }
}
