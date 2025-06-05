package com.conaveg.cona.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.ProveedorDTO;
import com.conaveg.cona.model.Proveedor;
import com.conaveg.cona.repository.ProveedorRepository;

@Service
public class ProveedorService {
    @Autowired
    private ProveedorRepository proveedorRepository;

    public List<ProveedorDTO> getAllProveedores() {
        return proveedorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProveedorDTO getProveedorById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        return proveedor != null ? toDTO(proveedor) : null;
    }

    public ProveedorDTO saveProveedor(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = toEntity(proveedorDTO);
        Proveedor saved = proveedorRepository.save(proveedor);
        return toDTO(saved);
    }

    public ProveedorDTO updateProveedor(Long id, ProveedorDTO proveedorDTO) {
        if (proveedorRepository.existsById(id)) {
            Proveedor proveedor = toEntity(proveedorDTO);
            proveedor.setId(id);
            Proveedor updated = proveedorRepository.save(proveedor);
            return toDTO(updated);
        }
        return null;
    }

    public boolean deleteProveedor(Long id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Métodos de mapeo
    private ProveedorDTO toDTO(Proveedor proveedor) {
        return new ProveedorDTO(
            proveedor.getId(),
            proveedor.getRuc(),
            proveedor.getRazonSocial(),
            proveedor.getDireccion(),
            proveedor.getTelefono()
        );
    }

    private Proveedor toEntity(ProveedorDTO dto) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRuc(dto.getRuc());
        proveedor.setRazonSocial(dto.getRazonSocial());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setTelefono(dto.getTelefono());
        return proveedor;
    }
}
