package com.conaveg.cona.controllers;

import com.conaveg.cona.models.MovimientosInventario;
import com.conaveg.cona.services.MovimientosInventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientosInventarioController {
    @Autowired
    private MovimientosInventarioService movimientosInventarioService;

    @GetMapping
    public ResponseEntity<List<MovimientosInventario>> getAllMovimientosInventario() {
        List<MovimientosInventario> movimientos = movimientosInventarioService.getAllMovimientosInventario();
        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientosInventario> getMovimientosInventarioById(@PathVariable Long id) {
        MovimientosInventario mov = movimientosInventarioService.getMovimientosInventarioById(id);
        if (mov != null) {
            return ResponseEntity.ok(mov);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<MovimientosInventario> createMovimientosInventario(@RequestBody MovimientosInventario mov) {
        MovimientosInventario created = movimientosInventarioService.saveMovimientosInventario(mov);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientosInventario> updateMovimientosInventario(@PathVariable Long id, @RequestBody MovimientosInventario mov) {
        MovimientosInventario updated = movimientosInventarioService.updateMovimientosInventario(id, mov);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovimientosInventario(@PathVariable Long id) {
        MovimientosInventario mov = movimientosInventarioService.getMovimientosInventarioById(id);
        if (mov != null) {
            movimientosInventarioService.deleteMovimientosInventario(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
