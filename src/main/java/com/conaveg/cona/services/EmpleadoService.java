package com.conaveg.cona.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.models.Empleado;
import com.conaveg.cona.repositories.EmpleadoRepository;

@Service
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    public Empleado getEmpleadoById(Long id) {
        return empleadoRepository.findById(id).orElse(null);
    }

    public Empleado saveEmpleado(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    public Empleado updateEmpleado(Long id, Empleado empleado) {
        if (empleadoRepository.existsById(id)) {
            empleado.setId(id);
            return empleadoRepository.save(empleado);
        }
        return null;
    }

    public void deleteEmpleado(Long id) {
        empleadoRepository.deleteById(id);
    }
}
