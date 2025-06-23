package com.conaveg.cona.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servicio para envío de emails
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from:noreply@conaveg.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.security.password-reset-token-validity-hours:24}")
    private int tokenValidityHours;

    /**
     * Envía email de recuperación de contraseña
     */
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Recuperación de Contraseña - Sistema CONA");

            String resetUrl = baseUrl + "/reset-password?token=" + token;
            String content = String.format(
                "Hola,\n\n" +
                "Hemos recibido una solicitud para restablecer tu contraseña en el Sistema CONA.\n\n" +
                "Para restablecer tu contraseña, haz clic en el siguiente enlace:\n" +
                "%s\n\n" +
                "Este enlace es válido por %d horas y solo puede ser usado una vez.\n\n" +
                "Si no solicitaste este cambio, puedes ignorar este correo. Tu contraseña no será modificada.\n\n" +
                "Por tu seguridad, nunca compartas este enlace con nadie.\n\n" +
                "Saludos,\n" +
                "Equipo CONA\n\n" +
                "---\n" +
                "Este es un correo automático, por favor no respondas a este mensaje.",
                resetUrl,
                tokenValidityHours
            );

            message.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el email de recuperación: " + e.getMessage());
        }
    }

    /**
     * Envía notificación de cambio de contraseña exitoso
     */
    public void sendPasswordChangeConfirmation(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Contraseña Cambiada Exitosamente - Sistema CONA");

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));
            
            String content = String.format(
                "Hola,\n\n" +
                "Te confirmamos que tu contraseña en el Sistema CONA ha sido cambiada exitosamente.\n\n" +
                "Detalles del cambio:\n" +
                "- Fecha y hora: %s\n" +
                "- Método: Recuperación por email\n\n" +
                "Si no realizaste este cambio, por favor contacta inmediatamente al administrador del sistema.\n\n" +
                "Para tu seguridad, te recomendamos:\n" +
                "- Usar una contraseña única y segura\n" +
                "- No compartir tus credenciales con nadie\n" +
                "- Cerrar sesión en dispositivos públicos\n\n" +
                "Saludos,\n" +
                "Equipo CONA\n\n" +
                "---\n" +
                "Este es un correo automático, por favor no respondas a este mensaje.",
                timestamp
            );

            message.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar el email de confirmación: " + e.getMessage());
        }
    }

    /**
     * Envía notificación de cuenta bloqueada por intentos fallidos
     */
    public void sendAccountLockedNotification(String toEmail, LocalDateTime lockoutExpiry) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Cuenta Temporalmente Bloqueada - Sistema CONA");

            String expiryTime = lockoutExpiry.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));
            String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));

            String content = String.format(
                "Hola,\n\n" +
                "Tu cuenta en el Sistema CONA ha sido temporalmente bloqueada debido a múltiples intentos de inicio de sesión fallidos.\n\n" +
                "Detalles del bloqueo:\n" +
                "- Hora del bloqueo: %s\n" +
                "- Hora de desbloqueo: %s\n\n" +
                "Si no fuiste tú quien intentó acceder a la cuenta, te recomendamos:\n" +
                "1. Cambiar tu contraseña una vez que puedas acceder\n" +
                "2. Verificar que no compartas tus credenciales\n" +
                "3. Contactar al administrador si sospechas actividad maliciosa\n\n" +
                "Si fuiste tú, asegúrate de usar la contraseña correcta en el próximo intento.\n\n" +
                "Saludos,\n" +
                "Equipo CONA\n\n" +
                "---\n" +
                "Este es un correo automático, por favor no respondas a este mensaje.",
                currentTime,
                expiryTime
            );

            message.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            // No lanzar excepción para no interrumpir el proceso de bloqueo
            System.err.println("Error al enviar notificación de cuenta bloqueada: " + e.getMessage());
        }
    }

    /**
     * Envía notificación de actividad sospechosa
     */
    public void sendSuspiciousActivityAlert(String toEmail, String activityDetails, String ipAddress) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Alerta de Seguridad - Actividad Sospechosa Detectada");

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm"));

            String content = String.format(
                "Hola,\n\n" +
                "Hemos detectado actividad sospechosa en tu cuenta del Sistema CONA.\n\n" +
                "Detalles de la actividad:\n" +
                "- Fecha y hora: %s\n" +
                "- Dirección IP: %s\n" +
                "- Descripción: %s\n\n" +
                "Si reconoces esta actividad, puedes ignorar este mensaje.\n\n" +
                "Si NO reconoces esta actividad, te recomendamos:\n" +
                "1. Cambiar tu contraseña inmediatamente\n" +
                "2. Revisar la actividad de tu cuenta\n" +
                "3. Contactar al administrador del sistema\n\n" +
                "Tu seguridad es nuestra prioridad.\n\n" +
                "Saludos,\n" +
                "Equipo CONA\n\n" +
                "---\n" +
                "Este es un correo automático, por favor no respondas a este mensaje.",
                timestamp,
                ipAddress,
                activityDetails
            );

            message.setText(content);
            mailSender.send(message);

        } catch (Exception e) {
            // No lanzar excepción para no interrumpir el proceso de auditoría
            System.err.println("Error al enviar alerta de actividad sospechosa: " + e.getMessage());
        }
    }

    /**
     * Valida la configuración del servicio de email
     */
    public boolean isEmailServiceConfigured() {
        try {
            return mailSender != null && fromEmail != null && !fromEmail.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Envía email de prueba (para verificar configuración)
     */
    public void sendTestEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Email de Prueba - Sistema CONA");
            message.setText(
                "Este es un email de prueba del Sistema CONA.\n\n" +
                "Si recibes este mensaje, la configuración de email está funcionando correctamente.\n\n" +
                "Saludos,\nEquipo CONA"
            );

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email de prueba: " + e.getMessage());
        }
    }
}
