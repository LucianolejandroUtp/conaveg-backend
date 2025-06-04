package com.conaveg.cona.controllers;

import com.conaveg.cona.models.Proyecto;
import com.conaveg.cona.services.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    @Autowired
    private ProyectoService proyectoService;

    @GetMapping
    public ResponseEntity<List<Proyecto>> getAllProyectos() {
        List<Proyecto> proyectos = proyectoService.getAllProyectos();
        return ResponseEntity.ok(proyectos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> getProyectoById(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.getProyectoById(id);
        if (proyecto != null) {
            return ResponseEntity.ok(proyecto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Proyecto> createProyecto(@RequestBody Proyecto proyecto) {
        Proyecto created = proyectoService.saveProyecto(proyecto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> updateProyecto(@PathVariable Long id, @RequestBody Proyecto proyecto) {
        Proyecto updated = proyectoService.updateProyecto(id, proyecto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProyecto(@PathVariable Long id) {
        Proyecto proyecto = proyectoService.getProyectoById(id);
        if (proyecto != null) {
            proyectoService.deleteProyecto(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
