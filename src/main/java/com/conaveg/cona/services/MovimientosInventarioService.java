package com.conaveg.cona.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.models.MovimientosInventario;
import com.conaveg.cona.repositories.MovimientosInventarioRepository;

@Service
public class MovimientosInventarioService {
    @Autowired
    private MovimientosInventarioRepository movimientosInventarioRepository;

    public List<MovimientosInventario> getAllMovimientosInventario() {
        return movimientosInventarioRepository.findAll();
    }

    public MovimientosInventario getMovimientosInventarioById(Long id) {
        return movimientosInventarioRepository.findById(id).orElse(null);
    }

    public MovimientosInventario saveMovimientosInventario(MovimientosInventario mov) {
        return movimientosInventarioRepository.save(mov);
    }

    public MovimientosInventario updateMovimientosInventario(Long id, MovimientosInventario mov) {
        if (movimientosInventarioRepository.existsById(id)) {
            mov.setId(id);
            return movimientosInventarioRepository.save(mov);
        }
        return null;
    }

    public void deleteMovimientosInventario(Long id) {
        movimientosInventarioRepository.deleteById(id);
    }
}
