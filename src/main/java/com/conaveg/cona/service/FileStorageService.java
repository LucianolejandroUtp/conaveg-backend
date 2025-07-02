package com.conaveg.cona.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Servicio para la gestión de archivos del sistema
 * Maneja la subida, validación y almacenamiento de archivos
 */
@Service
public class FileStorageService {

    @Value("${app.file.upload-dir:files}")
    private String baseUploadDir;

    private static final String FACTURAS_DIR = "facturas";
    private static final String[] ALLOWED_EXTENSIONS = {".pdf"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Guarda un archivo de factura en el sistema
     * 
     * @param file El archivo a guardar
     * @return Un objeto con la información del archivo guardado
     * @throws IOException Si hay problemas al guardar el archivo
     * @throws IllegalArgumentException Si el archivo no es válido
     */
    public FileInfo saveFacturaFile(MultipartFile file) throws IOException {
        // Validar archivo
        validateFile(file);
        
        // Crear directorio si no existe
        Path facturasDirPath = createFacturasDirectory();
        
        // Generar nombre único para el archivo
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(extension);
        
        // Ruta completa del archivo
        Path filePath = facturasDirPath.resolve(uniqueFilename);
        
        // Guardar archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Crear información del archivo
        String relativePath = FACTURAS_DIR + "/" + uniqueFilename;
        
        return new FileInfo(
            uniqueFilename,
            relativePath,
            originalFilename,
            file.getSize(),
            file.getContentType()
        );
    }

    /**
     * Valida que el archivo cumpla con los requisitos
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (10MB)");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no es válido");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean isValidExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                isValidExtension = true;
                break;
            }
        }
        
        if (!isValidExtension) {
            throw new IllegalArgumentException("Solo se permiten archivos PDF");
        }
        
        // Validar tipo MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("El tipo de archivo debe ser PDF");
        }
    }

    /**
     * Crea el directorio de facturas si no existe
     */
    private Path createFacturasDirectory() throws IOException {
        Path baseDir = Paths.get(baseUploadDir);
        Path facturasDir = baseDir.resolve(FACTURAS_DIR);
        
        if (!Files.exists(facturasDir)) {
            Files.createDirectories(facturasDir);
        }
        
        return facturasDir;
    }

    /**
     * Extrae la extensión del archivo
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }

    /**
     * Genera un nombre único para el archivo
     * Formato: FACTURA_YYYYMMDD_HHMMSS_UUID.pdf
     */
    private String generateUniqueFilename(String extension) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("FACTURA_%s_%s%s", timestamp, uuid, extension);
    }

    /**
     * Elimina un archivo del sistema
     */
    public boolean deleteFile(String relativePath) {
        try {
            Path filePath = Paths.get(baseUploadDir).resolve(relativePath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Verifica si un archivo existe
     */
    public boolean fileExists(String relativePath) {
        Path filePath = Paths.get(baseUploadDir).resolve(relativePath);
        return Files.exists(filePath);
    }

    /**
     * Clase interna para almacenar información del archivo
     */
    public static class FileInfo {
        private final String filename;
        private final String relativePath;
        private final String originalFilename;
        private final long size;
        private final String contentType;

        public FileInfo(String filename, String relativePath, String originalFilename, long size, String contentType) {
            this.filename = filename;
            this.relativePath = relativePath;
            this.originalFilename = originalFilename;
            this.size = size;
            this.contentType = contentType;
        }

        public String getFilename() { return filename; }
        public String getRelativePath() { return relativePath; }
        public String getOriginalFilename() { return originalFilename; }
        public long getSize() { return size; }
        public String getContentType() { return contentType; }
    }
}
