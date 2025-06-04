package com.conaveg.cona.controllers;

import com.conaveg.cona.models.AsignacionesProyectosEmpleado;
import com.conaveg.cona.services.AsignacionesProyectosEmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones-proyectos-empleado")
public class AsignacionesProyectosEmpleadoController {
    @Autowired
    private AsignacionesProyectosEmpleadoService asignacionesProyectosEmpleadoService;

    @GetMapping
    public ResponseEntity<List<AsignacionesProyectosEmpleado>> getAllAsignacionesProyectosEmpleado() {
        List<AsignacionesProyectosEmpleado> asignaciones = asignacionesProyectosEmpleadoService.getAllAsignacionesProyectosEmpleado();
        return ResponseEntity.ok(asignaciones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsignacionesProyectosEmpleado> getAsignacionesProyectosEmpleadoById(@PathVariable Long id) {
        AsignacionesProyectosEmpleado asignacion = asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(id);
        if (asignacion != null) {
            return ResponseEntity.ok(asignacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<AsignacionesProyectosEmpleado> createAsignacionesProyectosEmpleado(@RequestBody AsignacionesProyectosEmpleado asignacion) {
        AsignacionesProyectosEmpleado created = asignacionesProyectosEmpleadoService.saveAsignacionesProyectosEmpleado(asignacion);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsignacionesProyectosEmpleado> updateAsignacionesProyectosEmpleado(@PathVariable Long id, @RequestBody AsignacionesProyectosEmpleado asignacion) {
        AsignacionesProyectosEmpleado updated = asignacionesProyectosEmpleadoService.updateAsignacionesProyectosEmpleado(id, asignacion);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsignacionesProyectosEmpleado(@PathVariable Long id) {
        AsignacionesProyectosEmpleado asignacion = asignacionesProyectosEmpleadoService.getAsignacionesProyectosEmpleadoById(id);
        if (asignacion != null) {
            asignacionesProyectosEmpleadoService.deleteAsignacionesProyectosEmpleado(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
