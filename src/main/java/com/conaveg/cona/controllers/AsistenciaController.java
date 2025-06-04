package com.conaveg.cona.controllers;

import com.conaveg.cona.models.Asistencia;
import com.conaveg.cona.services.AsistenciaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
public class AsistenciaController {
    @Autowired
    private AsistenciaService asistenciaService;

    @GetMapping
    public ResponseEntity<List<Asistencia>> getAllAsistencias() {
        List<Asistencia> asistencias = asistenciaService.getAllAsistencias();
        return ResponseEntity.ok(asistencias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> getAsistenciaById(@PathVariable Long id) {
        Asistencia asistencia = asistenciaService.getAsistenciaById(id);
        if (asistencia != null) {
            return ResponseEntity.ok(asistencia);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Asistencia> createAsistencia(@RequestBody Asistencia asistencia) {
        Asistencia created = asistenciaService.saveAsistencia(asistencia);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asistencia> updateAsistencia(@PathVariable Long id, @RequestBody Asistencia asistencia) {
        Asistencia updated = asistenciaService.updateAsistencia(id, asistencia);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsistencia(@PathVariable Long id) {
        Asistencia asistencia = asistenciaService.getAsistenciaById(id);
        if (asistencia != null) {
            asistenciaService.deleteAsistencia(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
