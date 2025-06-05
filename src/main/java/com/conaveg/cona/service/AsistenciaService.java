package com.conaveg.cona.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.model.Asistencia;
import com.conaveg.cona.repository.AsistenciaRepository;

@Service
public class AsistenciaService {
    @Autowired
    private AsistenciaRepository asistenciaRepository;

    public List<Asistencia> getAllAsistencias() {
        return asistenciaRepository.findAll();
    }

    public Asistencia getAsistenciaById(Long id) {
        return asistenciaRepository.findById(id).orElse(null);
    }

    public Asistencia saveAsistencia(Asistencia asistencia) {
        return asistenciaRepository.save(asistencia);
    }

    public Asistencia updateAsistencia(Long id, Asistencia asistencia) {
        if (asistenciaRepository.existsById(id)) {
            asistencia.setId(id);
            return asistenciaRepository.save(asistencia);
        }
        return null;
    }

    public void deleteAsistencia(Long id) {
        asistenciaRepository.deleteById(id);
    }
}
