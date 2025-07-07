package com.conaveg.cona.constants;

/**
 * Constantes para los estados de asistencia según el ENUM de la base de datos
 */
public final class AttendanceStatus {
    
    /**
     * Empleado llegó a tiempo
     */
    public static final String PUNTUAL = "PUNTUAL";
    
    /**
     * Empleado llegó tarde
     */
    public static final String TARDE = "TARDE";
    
    /**
     * Empleado no asistió
     */
    public static final String AUSENTE = "AUSENTE";
    
    /**
     * Ausencia justificada (permisos, licencias, etc.)
     */
    public static final String JUSTIFICADO = "JUSTIFICADO";
    
    /**
     * Otros estados especiales
     */
    public static final String OTRO = "OTRO";
    
    /**
     * Array con todos los valores válidos del ENUM
     */
    public static final String[] VALID_STATUSES = {
        PUNTUAL, TARDE, AUSENTE, JUSTIFICADO, OTRO
    };
    
    // Constructor privado para prevenir instanciación
    private AttendanceStatus() {
        throw new UnsupportedOperationException("Esta es una clase de constantes");
    }
    
    /**
     * Valida si un estado es válido según el ENUM de la base de datos
     * @param status Estado a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValid(String status) {
        if (status == null) return false;
        String upperStatus = status.toUpperCase();
        for (String validStatus : VALID_STATUSES) {
            if (validStatus.equals(upperStatus)) {
                return true;
            }
        }
        return false;
    }
}
