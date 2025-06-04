package com.conaveg.cona.controllers;

import com.conaveg.cona.models.Inventario;
import com.conaveg.cona.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {
    @Autowired
    private InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<Inventario>> getAllInventario() {
        List<Inventario> inventario = inventarioService.getAllInventario();
        return ResponseEntity.ok(inventario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getInventarioById(@PathVariable Long id) {
        Inventario inv = inventarioService.getInventarioById(id);
        if (inv != null) {
            return ResponseEntity.ok(inv);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Inventario> createInventario(@RequestBody Inventario inventario) {
        Inventario created = inventarioService.saveInventario(inventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inventario> updateInventario(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario updated = inventarioService.updateInventario(id, inventario);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventario(@PathVariable Long id) {
        Inventario inv = inventarioService.getInventarioById(id);
        if (inv != null) {
            inventarioService.deleteInventario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
