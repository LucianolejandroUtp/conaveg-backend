package com.conaveg.cona.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.Proyecto;
import com.conaveg.cona.repository.ProyectoRepository;

@Service
public class ProyectoService {
    @Autowired
    private ProyectoRepository proyectoRepository;

    public List<Proyecto> getAllProyectos() {
        return proyectoRepository.findAll();
    }

    public Proyecto getProyectoById(Long id) {
        return proyectoRepository.findById(id).orElse(null);
    }

    public Proyecto saveProyecto(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }

    public Proyecto updateProyecto(Long id, Proyecto proyecto) {
        if (proyectoRepository.existsById(id)) {
            proyecto.setId(id);
            return proyectoRepository.save(proyecto);
        }
        return null;
    }

    public void deleteProyecto(Long id) {
        proyectoRepository.deleteById(id);
    }
}
