
package com.conaveg.cona.service;

import com.conaveg.cona.dto.AsistenciaRegistroRapidoDTO;
import com.conaveg.cona.config.AttendanceConfig;
import com.conaveg.cona.constants.AttendanceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;
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
     * Registra una asistencia rápida con detección automática de entrada/salida y tardanza
     * - Si no existe entrada para el día: crea registro de ENTRADA
     * - Si ya existe entrada sin salida: actualiza con SALIDA
     * - Si ya existe entrada y salida: retorna error
     */
    public AsistenciaDTO registrarAsistenciaRapida(AsistenciaRegistroRapidoDTO request) {
        logger.info("🎯 INICIANDO registro rápido para documento: {}", request != null ? request.getNroDocumento() : "NULL");
        
        if (request == null || request.getNroDocumento() == null || request.getMetodoRegistro() == null) {
            logger.error("❌ Datos incompletos en request: documento={}, método={}", 
                request != null ? request.getNroDocumento() : "NULL",
                request != null ? request.getMetodoRegistro() : "NULL");
            return null;
        }
        
        Empleado empleado = empleadoRepository.findByNroDocumento(request.getNroDocumento()).orElse(null);
        if (empleado == null) {
            logger.error("❌ Empleado no encontrado con documento: {}", request.getNroDocumento());
            return null;
        }
        
        logger.info("✅ Empleado encontrado: {} - ID: {}", empleado.getNroDocumento(), empleado.getId());
        
        // Obtener la hora actual en la zona horaria local
        Instant ahoraLocal = getCurrentLocalInstant();
        logger.info("⏰ Hora actual calculada: {}", ahoraLocal);
        
        // Validar coordenadas GPS si están presentes
        if (!validateGpsCoordinates(request.getLatitud(), request.getLongitud())) {
            logger.warn("⚠️ Registro de asistencia con coordenadas inválidas para empleado: {}", request.getNroDocumento());
            // Continuar el registro pero sin coordenadas
            request.setLatitud(null);
            request.setLongitud(null);
        }
        
        // Buscar si ya existe un registro de entrada para hoy
        Optional<Asistencia> registroEntradaHoy = buscarEntradaDelDia(empleado, ahoraLocal);
        
        if (registroEntradaHoy.isPresent()) {
            // Ya existe entrada, procesar como SALIDA
            logger.info("📋 FLUJO: SALIDA (ya existe entrada)");
            return procesarRegistroSalida(registroEntradaHoy.get(), ahoraLocal, request);
        } else {
            // No existe entrada, procesar como ENTRADA
            logger.info("📋 FLUJO: ENTRADA (no existe entrada previa)");
            return procesarRegistroEntrada(empleado, ahoraLocal, request);
        }
    }
    
    /**
     * Busca un registro de entrada para el empleado en el día actual
     * Usa la MISMA lógica que getCurrentLocalInstant() para ser consistente
     */
    private Optional<Asistencia> buscarEntradaDelDia(Empleado empleado, Instant momentoActual) {
        try {
            logger.info("🔍 Buscando entrada existente para empleado: {} en momento: {}", 
                empleado.getNroDocumento(), momentoActual);
            
            // Usar la MISMA lógica que getCurrentLocalInstant() para obtener el día
            ZoneId localZone = ZoneId.of(attendanceConfig.getTimezone());
            ZonedDateTime ahoraLocal = ZonedDateTime.now(localZone);
            java.time.LocalDate fechaHoy = ahoraLocal.toLocalDate();
            
            // Calcular inicio y fin del día usando la MISMA estrategia
            // Inicio del día: 00:00:00 tratado como UTC
            java.time.LocalDateTime inicioDiaLocal = fechaHoy.atStartOfDay();
            Instant inicioInstant = inicioDiaLocal.atZone(ZoneId.of("UTC")).toInstant();
            
            // Fin del día: 23:59:59 tratado como UTC  
            java.time.LocalDateTime finDiaLocal = fechaHoy.atTime(23, 59, 59);
            Instant finInstant = finDiaLocal.atZone(ZoneId.of("UTC")).toInstant();
            
            logger.info("📅 Buscando registros del día {} entre {} y {} (usando lógica de getCurrentLocalInstant)", 
                fechaHoy, inicioInstant, finInstant);
            
            Optional<Asistencia> resultado = asistenciaRepository.findEntradaByEmpleadoAndDate(
                empleado, inicioInstant, finInstant);
            
            if (resultado.isPresent()) {
                Asistencia asistencia = resultado.get();
                logger.info("✅ ENTRADA ENCONTRADA - ID: {}, Empleado: {}, Tipo: {}, Entrada: {}, Salida: {}", 
                    asistencia.getId(),
                    empleado.getNroDocumento(), 
                    asistencia.getTipoRegistro(),
                    asistencia.getEntrada(),
                    asistencia.getSalida() != null ? asistencia.getSalida() : "NULL"
                );
            } else {
                logger.info("❌ NO SE ENCONTRÓ entrada para empleado: {} en fecha: {}", 
                    empleado.getNroDocumento(), fechaHoy);
            }
            
            return resultado;
        } catch (Exception e) {
            logger.error("❌ Error al buscar entrada del día para empleado {}: {}", empleado.getNroDocumento(), e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Procesa un registro de ENTRADA (nuevo registro)
     */
    private AsistenciaDTO procesarRegistroEntrada(Empleado empleado, Instant horaEntrada, AsistenciaRegistroRapidoDTO request) {
        logger.info("🚪 Procesando registro de ENTRADA para empleado: {} a las {}", empleado.getNroDocumento(), horaEntrada);
        
        AsistenciaDTO asistenciaDTO = new AsistenciaDTO();
        asistenciaDTO.setEmpleadoId(empleado.getId());
        asistenciaDTO.setEntrada(horaEntrada);
        asistenciaDTO.setTipoRegistro("ENTRADA");
        asistenciaDTO.setUbicacionRegistro(request.getUbicacionRegistro() != null ? request.getUbicacionRegistro() : "Oficina Central");
        asistenciaDTO.setMetodoRegistro(request.getMetodoRegistro());
        asistenciaDTO.setLatitud(request.getLatitud());
        asistenciaDTO.setLongitud(request.getLongitud());
        
        // Detectar tardanza y establecer estado de asistencia
        String estadoAsistencia = determineAttendanceStatus(horaEntrada, request.getEstadoAsistencia());
        asistenciaDTO.setEstadoAsistencia(estadoAsistencia);
        
        // Generar observación con información de tardanza si aplica
        String observacionFinal = generateObservationWithLateness(horaEntrada, request.getObservacion(), estadoAsistencia);
        asistenciaDTO.setObservacion(observacionFinal);
        
        logger.info("💾 Guardando nuevo registro de ENTRADA - EmpleadoId: {}, Estado: {}", empleado.getId(), estadoAsistencia);
        AsistenciaDTO resultado = saveAsistencia(asistenciaDTO);
        
        if (resultado != null) {
            logger.info("✅ ENTRADA creada exitosamente - ID: {}", resultado.getId());
        } else {
            logger.error("❌ Error al crear registro de ENTRADA");
        }
        
        return resultado;
    }
    
    /**
     * Procesa un registro de SALIDA (actualización de registro existente)
     */
    private AsistenciaDTO procesarRegistroSalida(Asistencia registroExistente, Instant horaSalida, AsistenciaRegistroRapidoDTO request) {
        logger.info("🚪 Procesando registro de SALIDA para empleado: {}", registroExistente.getEmpleados().getNroDocumento());
        
        // Verificar si ya tiene salida registrada
        if (registroExistente.getSalida() != null) {
            logger.warn("⚠️ El empleado {} ya tiene salida registrada para hoy: {}", 
                registroExistente.getEmpleados().getNroDocumento(), registroExistente.getSalida());
            return null; // Ya tiene entrada y salida completas
        }
        
        // Validar que la salida sea después de la entrada
        if (horaSalida.isBefore(registroExistente.getEntrada())) {
            logger.warn("⚠️ Intento de registrar salida antes de la entrada para empleado: {} - Entrada: {}, Salida intentada: {}", 
                registroExistente.getEmpleados().getNroDocumento(), registroExistente.getEntrada(), horaSalida);
            return null; // Salida no puede ser antes que entrada
        }
        
        logger.info("✅ Actualizando registro ID: {} - Entrada: {} → Salida: {}", 
            registroExistente.getId(), registroExistente.getEntrada(), horaSalida);
        
        // Actualizar el registro existente con los datos de salida
        registroExistente.setSalida(horaSalida);
        registroExistente.setTipoRegistro("SALIDA");
        
        // Actualizar ubicación y método si se proporcionan
        if (request.getUbicacionRegistro() != null) {
            registroExistente.setUbicacionRegistro(request.getUbicacionRegistro());
        }
        registroExistente.setMetodoRegistro(request.getMetodoRegistro());
        
        // Actualizar coordenadas GPS si están presentes
        if (request.getLatitud() != null && request.getLongitud() != null) {
            registroExistente.setLatitud(request.getLatitud());
            registroExistente.setLongitud(request.getLongitud());
        }
        
        // Calcular horas trabajadas y actualizar observación
        String observacionSalida = generateObservationForExit(
            registroExistente.getEntrada(), 
            horaSalida, 
            request.getObservacion()
        );
        
        // Combinar observación existente con la nueva información
        String observacionFinal = combineObservations(registroExistente.getObservacion(), observacionSalida);
        registroExistente.setObservacion(observacionFinal);
        
        // Guardar y retornar
        Asistencia actualizado = asistenciaRepository.save(registroExistente);
        return convertToDTO(actualizado);
    }
    
    /**
     * Genera observación específica para salidas incluyendo horas trabajadas
     */
    private String generateObservationForExit(Instant entrada, Instant salida, String observacionUsuario) {
        try {
            StringBuilder observacion = new StringBuilder();
            
            // Agregar observación del usuario si existe
            if (observacionUsuario != null && !observacionUsuario.trim().isEmpty()) {
                observacion.append(observacionUsuario.trim());
            }
            
            // Calcular horas trabajadas
            java.time.Duration tiempoTrabajado = java.time.Duration.between(entrada, salida);
            long horasTrabajadas = tiempoTrabajado.toHours();
            long minutosTrabajados = tiempoTrabajado.toMinutesPart();
            
            // Agregar información de tiempo trabajado
            if (observacion.length() > 0) {
                observacion.append(" - ");
            }
            observacion.append(String.format("Tiempo trabajado: %d horas y %d minutos", horasTrabajadas, minutosTrabajados));
            
            return observacion.toString();
            
        } catch (Exception e) {
            logger.error("Error al generar observación de salida: {}", e.getMessage());
            return observacionUsuario != null ? observacionUsuario : "";
        }
    }
    
    /**
     * Combina observaciones existentes con nuevas
     */
    private String combineObservations(String observacionExistente, String nuevaObservacion) {
        if (observacionExistente == null || observacionExistente.trim().isEmpty()) {
            return nuevaObservacion;
        }
        
        if (nuevaObservacion == null || nuevaObservacion.trim().isEmpty()) {
            return observacionExistente;
        }
        
        return observacionExistente + " | " + nuevaObservacion;
    }

    /**
     * Valida y ajusta las coordenadas GPS si están presentes
     * @param latitud Coordenada de latitud
     * @param longitud Coordenada de longitud
     * @return true si las coordenadas son válidas, false en caso contrario
     */
    private boolean validateGpsCoordinates(Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            return true; // Coordenadas opcionales
        }
        
        // Validar rangos válidos de coordenadas
        boolean validLatitud = latitud >= -90.0 && latitud <= 90.0;
        boolean validLongitud = longitud >= -180.0 && longitud <= 180.0;
        
        if (!validLatitud || !validLongitud) {
            logger.warn("Coordenadas GPS inválidas: lat={}, lng={}", latitud, longitud);
            return false;
        }
        
        return true;
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
     * Determina el estado de asistencia basado en la hora de entrada
     * @param entryTime Hora de entrada
     * @param requestedStatus Estado solicitado por el usuario (puede ser null)
     * @return Estado de asistencia final (valores ENUM: PUNTUAL, TARDE, AUSENTE, JUSTIFICADO, OTRO)
     */
    private String determineAttendanceStatus(Instant entryTime, String requestedStatus) {
        try {
            // Si el usuario ya especificó un estado válido, usarlo a menos que sea tardanza
            if (requestedStatus != null && !requestedStatus.trim().isEmpty()) {
                // Validar que el estado solicitado sea uno de los valores ENUM válidos
                if (AttendanceStatus.isValid(requestedStatus)) {
                    // Verificar si hay tardanza independientemente del estado solicitado
                    if (isLateEntry(entryTime)) {
                        return AttendanceStatus.TARDE;
                    }
                    return requestedStatus.toUpperCase();
                } else {
                    logger.warn("Estado de asistencia inválido recibido: {}. Usando detección automática.", requestedStatus);
                }
            }
            
            // Si no hay estado especificado o es inválido, determinar automáticamente
            if (isLateEntry(entryTime)) {
                return AttendanceStatus.TARDE;
            }
            
            return AttendanceStatus.PUNTUAL; // Estado por defecto
            
        } catch (Exception e) {
            logger.error("Error al determinar estado de asistencia: {}", e.getMessage());
            return requestedStatus != null && AttendanceStatus.isValid(requestedStatus) ? requestedStatus.toUpperCase() : AttendanceStatus.PUNTUAL;
        }
    }

    /**
     * Genera la observación final incluyendo información de tiempo de tardanza si aplica
     * @param entryTime Hora de entrada
     * @param originalObservacion Observación original del usuario
     * @param estadoAsistencia Estado de asistencia determinado
     * @return Observación final con información de tiempo si es tardanza
     */
    private String generateObservationWithLateness(Instant entryTime, String originalObservacion, String estadoAsistencia) {
        try {
            StringBuilder observacion = new StringBuilder();
            
            // Agregar observación original si existe
            if (originalObservacion != null && !originalObservacion.trim().isEmpty()) {
                observacion.append(originalObservacion.trim());
            }
            
            // Solo agregar información de tiempo si llegó tarde
            if (AttendanceStatus.TARDE.equals(estadoAsistencia)) {
                String timeInfo = calculateLatenessTimeInfo(entryTime);
                if (timeInfo != null) {
                    if (observacion.length() > 0) {
                        observacion.append(" - ");
                    }
                    observacion.append(timeInfo);
                }
            }
            
            return observacion.toString();
            
        } catch (Exception e) {
            logger.error("Error al generar observación con información de tiempo: {}", e.getMessage());
            // En caso de error, retornar solo la observación original
            return originalObservacion != null ? originalObservacion : "";
        }
    }

    /**
     * Calcula la información de tiempo de tardanza (solo el tiempo, sin etiquetas)
     * @param entryTime Hora de entrada
     * @return Cadena con información de tiempo (minutos y segundos)
     */
    private String calculateLatenessTimeInfo(Instant entryTime) {
        try {
            // Como el entryTime ya está "ajustado" para representar hora local,
            // lo tratamos como UTC para obtener la hora correcta
            ZonedDateTime entryAsLocal = entryTime.atZone(ZoneId.of("UTC"));
            
            // Parsear la hora de inicio configurada
            LocalTime expectedStartTime = LocalTime.parse(attendanceConfig.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            
            // Agregar el threshold de minutos permitidos (hora límite real)
            LocalTime actualStartTime = expectedStartTime.plusMinutes(attendanceConfig.getLateThresholdMinutes());
            
            // Obtener solo la hora de la entrada (sin fecha)
            LocalTime actualEntryTime = entryAsLocal.toLocalTime();
            
            // Verificar si realmente llegó tarde
            if (actualEntryTime.isAfter(actualStartTime)) {
                // Calcular la diferencia en segundos
                java.time.Duration duration = java.time.Duration.between(actualStartTime, actualEntryTime);
                long totalSeconds = duration.getSeconds();
                
                // Convertir a minutos y segundos
                long minutes = totalSeconds / 60;
                long seconds = totalSeconds % 60;
                
                // Formatear solo el tiempo sin etiquetas
                if (minutes > 0) {
                    if (seconds > 0) {
                        return String.format("%d minutos y %d segundos", minutes, seconds);
                    } else {
                        return String.format("%d minutos", minutes);
                    }
                } else {
                    return String.format("%d segundos", seconds);
                }
            }
            
            return null; // No hay tardanza
            
        } catch (Exception e) {
            logger.error("Error al calcular información de tiempo de tardanza: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Verifica si la entrada constituye una tardanza
     * @param entryTime Hora de entrada
     * @return true si es tardanza, false en caso contrario
     */
    private boolean isLateEntry(Instant entryTime) {
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
            boolean isLate = actualEntryTime.isAfter(actualStartTime);
            
            if (isLate) {
                logger.info("Tardanza detectada. Llegada: {}, Horario límite: {}", 
                    actualEntryTime.format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    actualStartTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                );
            }
            
            return isLate;
            
        } catch (Exception e) {
            logger.error("Error al verificar tardanza: {}", e.getMessage());
            return false;
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
        // Si es una entrada y no tiene estado de asistencia o es "PUNTUAL", verificar tardanza
        if ("ENTRADA".equalsIgnoreCase(asistenciaDTO.getTipoRegistro()) && 
            asistenciaDTO.getEntrada() != null) {
            
            String estadoActual = asistenciaDTO.getEstadoAsistencia();
            // Solo verificar tardanza si no hay ya un estado específico o es "PUNTUAL"
            if (estadoActual == null || AttendanceStatus.PUNTUAL.equals(estadoActual)) {
                String estadoFinal = determineAttendanceStatus(
                    asistenciaDTO.getEntrada(), 
                    estadoActual
                );
                asistenciaDTO.setEstadoAsistencia(estadoFinal);
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
        dto.setLatitud(asistencia.getLatitud());
        dto.setLongitud(asistencia.getLongitud());
        dto.setEstadoAsistencia(asistencia.getEstadoAsistencia());
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
        asistencia.setLatitud(dto.getLatitud());
        asistencia.setLongitud(dto.getLongitud());
        asistencia.setEstadoAsistencia(dto.getEstadoAsistencia());
        return asistencia;
    }
}
