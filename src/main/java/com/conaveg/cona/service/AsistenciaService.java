
package com.conaveg.cona.service;

import com.conaveg.cona.dto.AsistenciaRegistroRapidoDTO;
import com.conaveg.cona.config.AttendanceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conaveg.cona.dto.AsistenciaDTO;
import com.conaveg.cona.model.Asistencia;
import com.conaveg.cona.model.Empleado;
import com.conaveg.cona.repository.AsistenciaRepository;
import com.conaveg.cona.repository.EmpleadoRepository;

@Service
public class AsistenciaService {
    
    private static final Logger logger = LoggerFactory.getLogger(AsistenciaService.class);
    
    @Autowired
    private AsistenciaRepository asistenciaRepository;
    
    @Autowired
    private EmpleadoRepository empleadoRepository;
    
    @Autowired
    private AttendanceConfig attendanceConfig;

    /**
     * Registra una asistencia rápida con detección automática de tardanza
     */
    public AsistenciaDTO registrarAsistenciaRapida(AsistenciaRegistroRapidoDTO request) {
        if (request == null || request.getNroDocumento() == null || request.getMetodoRegistro() == null) {
            return null;
        }
        
        Empleado empleado = empleadoRepository.findByNroDocumento(request.getNroDocumento()).orElse(null);
        if (empleado == null) {
            return null;
        }
        
        // Obtener la hora actual en la zona horaria local
        Instant ahoraLocal = getCurrentLocalInstant();
        
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO();
        asistenciaDTO.setEmpleadoId(empleado.getId());
        asistenciaDTO.setEntrada(ahoraLocal);
        asistenciaDTO.setTipoRegistro("ENTRADA");
        asistenciaDTO.setUbicacionRegistro(request.getUbicacionRegistro() != null ? request.getUbicacionRegistro() : "Oficina Central");
        asistenciaDTO.setMetodoRegistro(request.getMetodoRegistro());
        
        // Detectar tardanza y construir observación
        String observacion = buildObservationWithLateCheck(ahoraLocal, request.getObservacion());
        asistenciaDTO.setObservacion(observacion);
        
        return saveAsistencia(asistenciaDTO);
    }

    /**
     * Obtiene el Instant actual ajustado para la zona horaria local
     * Esto corrige el problema de las 5 horas de diferencia
     * 
     * La estrategia es: tomar la hora local actual y convertirla a Instant
     * como si fuera UTC, de esta forma se almacena la hora "real" sin conversión
     */
    private Instant getCurrentLocalInstant() {
        try {
            ZoneId localZone = ZoneId.of(attendanceConfig.getTimezone());
            
            // Obtener la fecha y hora actual en la zona local
            ZonedDateTime ahoraLocal = ZonedDateTime.now(localZone);
            
            // Convertir a LocalDateTime (sin zona horaria)
            java.time.LocalDateTime localDateTime = ahoraLocal.toLocalDateTime();
            
            // Convertir el LocalDateTime a Instant tratándolo como UTC
            // Esto efectivamente "engaña" al sistema para que almacene la hora local
            Instant instantLocal = localDateTime.atZone(ZoneId.of("UTC")).toInstant();
            
            logger.debug("Hora local original: {}", ahoraLocal);
            logger.debug("Instant ajustado: {}", instantLocal);
            
            return instantLocal;
            
        } catch (Exception e) {
            logger.error("Error al obtener hora local, usando hora actual: {}", e.getMessage());
            // Fallback: obtener hora actual sin ajuste
            return Instant.now();
        }
    }

    /**
     * Construye la observación incluyendo detección de tardanza
     */
    private String buildObservationWithLateCheck(Instant entryTime, String originalObservation) {
        StringBuilder observacion = new StringBuilder();
        
        // Verificar si hay tardanza
        String lateInfo = checkForLateness(entryTime);
        if (lateInfo != null) {
            observacion.append(lateInfo);
        }
        
        // Agregar observación original si existe
        if (originalObservation != null && !originalObservation.trim().isEmpty()) {
            if (observacion.length() > 0) {
                observacion.append(" | ");
            }
            observacion.append(originalObservation);
        }
        
        return observacion.length() > 0 ? observacion.toString() : null;
    }

    /**
     * Verifica si la entrada constituye una tardanza
     * @param entryTime Hora de entrada (como Instant ajustado)
     * @return Mensaje de tardanza o null si es puntual
     */
    private String checkForLateness(Instant entryTime) {
        try {
            // Como el entryTime ya está "ajustado" para representar hora local,
            // lo tratamos como UTC para obtener la hora correcta
            ZonedDateTime entryAsLocal = entryTime.atZone(ZoneId.of("UTC"));
            
            // Parsear la hora de inicio configurada
            LocalTime expectedStartTime = LocalTime.parse(attendanceConfig.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            
            // Agregar el threshold de minutos permitidos
            LocalTime actualStartTime = expectedStartTime.plusMinutes(attendanceConfig.getLateThresholdMinutes());
            
            // Obtener solo la hora de la entrada (sin fecha)
            LocalTime actualEntryTime = entryAsLocal.toLocalTime();
            
            // Verificar si llegó tarde
            if (actualEntryTime.isAfter(actualStartTime)) {
                // Calcular minutos de tardanza
                long minutesLate = java.time.Duration.between(actualStartTime, actualEntryTime).toMinutes();
                
                String lateMessage = String.format("TARDANZA: %d minutos (llegada: %s, horario: %s)", 
                    minutesLate, 
                    actualEntryTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    expectedStartTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                );
                
                logger.info("Tardanza detectada: {} - {}", entryTime, lateMessage);
                return lateMessage;
            }
            
            return null; // No hay tardanza
            
        } catch (Exception e) {
            logger.error("Error al verificar tardanza: {}", e.getMessage());
            return null;
        }
    }

    public List<AsistenciaDTO> getAllAsistencias() {
        return asistenciaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AsistenciaDTO getAsistenciaById(Long id) {
        return asistenciaRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public AsistenciaDTO saveAsistencia(AsistenciaDTO asistenciaDTO) {
        // Si es una entrada y no tiene observación de tardanza, verificar tardanza
        if ("ENTRADA".equalsIgnoreCase(asistenciaDTO.getTipoRegistro()) && 
            asistenciaDTO.getEntrada() != null) {
            
            String observacionActual = asistenciaDTO.getObservacion();
            // Solo verificar tardanza si no hay ya una observación de tardanza
            if (observacionActual == null || !observacionActual.contains("TARDANZA")) {
                String observacionConTardanza = buildObservationWithLateCheck(
                    asistenciaDTO.getEntrada(), 
                    observacionActual
                );
                asistenciaDTO.setObservacion(observacionConTardanza);
            }
        }
        
        Asistencia asistencia = convertToEntity(asistenciaDTO);
        Asistencia saved = asistenciaRepository.save(asistencia);
        return convertToDTO(saved);
    }

    public AsistenciaDTO updateAsistencia(Long id, AsistenciaDTO asistenciaDTO) {
        if (asistenciaRepository.existsById(id)) {
            Asistencia asistencia = convertToEntity(asistenciaDTO);
            asistencia.setId(id);
            Asistencia updated = asistenciaRepository.save(asistencia);
            return convertToDTO(updated);
        }
        return null;
    }

    public void deleteAsistencia(Long id) {
        asistenciaRepository.deleteById(id);
    }

    private AsistenciaDTO convertToDTO(Asistencia asistencia) {
        AsistenciaDTO dto = new AsistenciaDTO();
        dto.setId(asistencia.getId());
        dto.setEmpleadoId(asistencia.getEmpleados() != null ? asistencia.getEmpleados().getId() : null);
        dto.setEntrada(asistencia.getEntrada());
        dto.setSalida(asistencia.getSalida());
        dto.setTipoRegistro(asistencia.getTipoRegistro());
        dto.setUbicacionRegistro(asistencia.getUbicacionRegistro());
        dto.setMetodoRegistro(asistencia.getMetodoRegistro());
        dto.setObservacion(asistencia.getObservacion());
        return dto;
    }

    private Asistencia convertToEntity(AsistenciaDTO dto) {
        Asistencia asistencia = new Asistencia();
        asistencia.setId(dto.getId());
        
        // Asignar empleado si se proporciona empleadoId
        if (dto.getEmpleadoId() != null) {
            Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId()).orElse(null);
            asistencia.setEmpleados(empleado);
        }
        
        asistencia.setEntrada(dto.getEntrada());
        asistencia.setSalida(dto.getSalida());
        asistencia.setTipoRegistro(dto.getTipoRegistro());
        asistencia.setUbicacionRegistro(dto.getUbicacionRegistro());
        asistencia.setMetodoRegistro(dto.getMetodoRegistro());
        asistencia.setObservacion(dto.getObservacion());
        return asistencia;
    }
}
