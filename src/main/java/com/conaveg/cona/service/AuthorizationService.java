package com.conaveg.cona.service;

import com.conaveg.cona.security.SecurityUtils;
import org.springframework.stereotype.Service;

/**
 * Servicio de autorización para verificaciones de permisos complejas
 * Utilizado en anotaciones @PreAuthorize para lógica de autorización personalizada
 */
@Service("authorizationService")
public class AuthorizationService {
    
    /**
     * Verifica si el usuario actual tiene rol de administrador
     */
    public boolean isAdmin() {
        return SecurityUtils.hasRole("ADMIN");
    }
    
    /**
     * Verifica si el usuario actual es empleado o administrador
     */
    public boolean isEmployeeOrAdmin() {
        return SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("EMPLEADO");
    }
      /**
     * Verifica si el usuario actual es el propietario del recurso, administrador o gerente
     * Útil para endpoints donde el usuario puede ver/editar solo sus propios datos
     * Los gerentes también pueden ver y editar su propio perfil
     */
    public boolean isOwnerOrAdmin(Long userId) {
        return SecurityUtils.isCurrentUser(userId) || isAdmin() || 
               (SecurityUtils.hasRole("GERENTE") && SecurityUtils.isCurrentUser(userId));
    }
      /**
     * Verifica si el usuario puede acceder a datos de empleados
     */
    public boolean canAccessEmployeeData() {
        return SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("EMPLEADO");
    }
    
    /**
     * Verifica si el usuario puede ver información de proyectos (solo lectura)
     * Incluye todos los roles autenticados para fomentar transparencia
     */
    public boolean canViewProjects() {
        return SecurityUtils.hasRole("ADMIN") || 
               SecurityUtils.hasRole("GERENTE") || 
               SecurityUtils.hasRole("EMPLEADO") || 
               SecurityUtils.hasRole("USER");
    }
    
    /**
     * Verifica si el usuario puede ver información de inventario (solo lectura)
     * Incluye todos los roles autenticados para consultas básicas
     */
    public boolean canViewInventory() {
        return SecurityUtils.hasRole("ADMIN") || 
               SecurityUtils.hasRole("GERENTE") || 
               SecurityUtils.hasRole("EMPLEADO") || 
               SecurityUtils.hasRole("USER");
    }
    
    /**
     * Verifica si el usuario puede realizar operaciones de modificación en proyectos
     * Excluye USER (solo lectura)
     */
    public boolean canManageProjects() {
        return SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("GERENTE");
    }
    
    /**
     * Verifica si el usuario puede realizar operaciones de modificación en inventario
     * Excluye USER y EMPLEADO (solo lectura para ellos)
     */
    public boolean canManageInventory() {
        return SecurityUtils.hasRole("ADMIN") || SecurityUtils.hasRole("GERENTE");
    }
    
    /**
     * Verifica si el usuario puede acceder a datos financieros
     */
    public boolean canAccessFinancialData() {
        return SecurityUtils.hasRole("ADMIN");
    }
    
    /**
     * Verifica si el usuario puede gestionar otros usuarios
     */
    public boolean canManageUsers() {
        return SecurityUtils.hasRole("ADMIN");
    }
      /**
     * Verifica si el usuario puede acceder a configuraciones del sistema
     */
    public boolean canAccessSystemConfig() {
        return SecurityUtils.hasRole("ADMIN");
    }
}
