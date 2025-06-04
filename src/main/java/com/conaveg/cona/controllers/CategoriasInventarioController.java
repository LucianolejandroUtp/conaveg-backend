package com.conaveg.cona.controllers;

import com.conaveg.cona.models.CategoriasInventario;
import com.conaveg.cona.services.CategoriasInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-inventario")
public class CategoriasInventarioController {
    @Autowired
    private CategoriasInventarioService categoriasInventarioService;

    @GetMapping
    public ResponseEntity<List<CategoriasInventario>> getAllCategoriasInventario() {
        List<CategoriasInventario> categorias = categoriasInventarioService.getAllCategoriasInventario();
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriasInventario> getCategoriasInventarioById(@PathVariable Long id) {
        CategoriasInventario categoria = categoriasInventarioService.getCategoriasInventarioById(id);
        if (categoria != null) {
            return ResponseEntity.ok(categoria);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CategoriasInventario> createCategoriasInventario(@RequestBody CategoriasInventario categoria) {
        CategoriasInventario created = categoriasInventarioService.saveCategoriasInventario(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriasInventario> updateCategoriasInventario(@PathVariable Long id, @RequestBody CategoriasInventario categoria) {
        CategoriasInventario updated = categoriasInventarioService.updateCategoriasInventario(id, categoria);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoriasInventario(@PathVariable Long id) {
        CategoriasInventario categoria = categoriasInventarioService.getCategoriasInventarioById(id);
        if (categoria != null) {
            categoriasInventarioService.deleteCategoriasInventario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
