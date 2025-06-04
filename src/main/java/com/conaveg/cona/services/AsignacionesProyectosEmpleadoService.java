package com.conaveg.cona.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.models.AsignacionesProyectosEmpleado;
import com.conaveg.cona.repositories.AsignacionesProyectosEmpleadoRepository;

@Service
public class AsignacionesProyectosEmpleadoService {
    @Autowired
    private AsignacionesProyectosEmpleadoRepository asignacionesProyectosEmpleadoRepository;

    public List<AsignacionesProyectosEmpleado> getAllAsignacionesProyectosEmpleado() {
        return asignacionesProyectosEmpleadoRepository.findAll();
    }

    public AsignacionesProyectosEmpleado getAsignacionesProyectosEmpleadoById(Long id) {
        return asignacionesProyectosEmpleadoRepository.findById(id).orElse(null);
    }

    public AsignacionesProyectosEmpleado saveAsignacionesProyectosEmpleado(AsignacionesProyectosEmpleado asignacion) {
        return asignacionesProyectosEmpleadoRepository.save(asignacion);
    }

    public AsignacionesProyectosEmpleado updateAsignacionesProyectosEmpleado(Long id, AsignacionesProyectosEmpleado asignacion) {
        if (asignacionesProyectosEmpleadoRepository.existsById(id)) {
            asignacion.setId(id);
            return asignacionesProyectosEmpleadoRepository.save(asignacion);
        }
        return null;
    }

    public void deleteAsignacionesProyectosEmpleado(Long id) {
        asignacionesProyectosEmpleadoRepository.deleteById(id);
    }
}
