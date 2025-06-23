package com.conaveg.cona.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utilidad para generar y validar tokens JWT
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatIsLongEnoughForSecurity}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private Long jwtExpiration;
    
    /**
     * Genera la clave secreta para firmar los tokens
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
      /**
     * Normaliza los nombres de roles de la base de datos a los códigos usados en la aplicación
     */
    private String normalizeRole(String role) {
        if (role == null || role.isEmpty()) {
            return "USER";
        }
        
        switch (role.toUpperCase()) {
            case "ADMINISTRADOR":
                return "ADMIN";
            case "GERENTE":
                return "GERENTE";
            case "EMPLEADO":
                return "EMPLEADO";
            default:
                return "USER";
        }
    }
    
    /**
     * Genera un token JWT para un usuario
     */
    public String generateToken(String email, Long userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        // Normalizar el rol antes de incluirlo en el token
        String normalizedRole = normalizeRole(role);
        
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", normalizedRole)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * Extrae el email del token
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getSubject();
    }
      /**
     * Extrae el userId del token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("userId", Long.class);
    }
    
    /**
     * Extrae el rol del usuario desde el token JWT
     */
    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.get("role", String.class);
    }
    
    /**
     * Valida si el token es válido
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            System.err.println("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            System.err.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.err.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.err.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.err.println("JWT claims string is empty");
        }
        return false;
    }
    
    /**
     * Obtiene el tiempo de expiración configurado
     */
    public Long getExpirationTime() {
        return jwtExpiration;
    }

    // ===== MÉTODOS PARA REFRESH TOKEN SYSTEM =====

    @Value("${jwt.refresh-window-hours:2}")
    private int refreshWindowHours;

    @Value("${jwt.refresh-minimum-minutes:30}")
    private int refreshMinimumMinutes;

    /**
     * Verifica si un token puede ser renovado
     * Un token es renovable si:
     * - Es válido (no expirado, bien formado)
     * - Le quedan menos de refreshWindowHours para expirar
     * - Pero más de refreshMinimumMinutes para evitar renovación excesiva
     */
    public boolean canRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            // Calcular tiempo restante en milisegundos
            long timeToExpiration = expiration.getTime() - now.getTime();
            
            // Convertir a minutos
            long minutesToExpiration = timeToExpiration / (1000 * 60);
            long hoursToExpiration = minutesToExpiration / 60;
            
            // Verificar que esté en la ventana de renovación
            return hoursToExpiration < refreshWindowHours && minutesToExpiration > refreshMinimumMinutes;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera un nuevo token basado en un token existente válido
     * Extrae la información del token actual y crea uno nuevo con tiempo renovado
     */
    public String refreshToken(String oldToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(oldToken)
                    .getPayload();

            String email = claims.getSubject();
            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);

            // Generar nuevo token con la misma información pero tiempo renovado
            return generateToken(email, userId, role);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al renovar token: " + e.getMessage());
        }
    }

    /**
     * Obtiene el tiempo restante antes de la expiración en minutos
     */
    public long getTimeToExpirationMinutes(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            long timeToExpiration = expiration.getTime() - now.getTime();
            return Math.max(0, timeToExpiration / (1000 * 60)); // Convertir a minutos
            
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Verifica si el token está en ventana de renovación
     */
    public boolean isInRefreshWindow(String token) {
        return canRefreshToken(token);
    }

    /**
     * Obtiene información del token para auditoría (sin datos sensibles)
     */
    public TokenInfo getTokenInfo(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return new TokenInfo(
                claims.getIssuedAt(),
                claims.getExpiration(),
                getTimeToExpirationMinutes(token),
                canRefreshToken(token)
            );
            
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clase para información del token
     */
    public static class TokenInfo {
        private final Date issuedAt;
        private final Date expiresAt;
        private final long minutesToExpiration;
        private final boolean canRefresh;

        public TokenInfo(Date issuedAt, Date expiresAt, long minutesToExpiration, boolean canRefresh) {
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
            this.minutesToExpiration = minutesToExpiration;
            this.canRefresh = canRefresh;
        }

        // Getters
        public Date getIssuedAt() { return issuedAt; }
        public Date getExpiresAt() { return expiresAt; }
        public long getMinutesToExpiration() { return minutesToExpiration; }
        public boolean canRefresh() { return canRefresh; }
    }
}
