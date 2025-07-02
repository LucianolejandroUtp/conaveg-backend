# API de Gestión de Archivos en Facturas

## Descripción General

Este documento describe cómo utilizar la nueva funcionalidad de subida de archivos PDF para facturas en el sistema CONA.

## Endpoints Disponibles

### 1. Crear Factura con Archivo

**POST** `/api/facturas/with-file`

Permite crear una nueva factura con un archivo PDF adjunto.

#### Parámetros (form-data)

| Parámetro | Tipo | Requerido | Descripción |
|-----------|------|-----------|-------------|
| `file` | File | Sí | Archivo PDF de la factura (máx. 10MB) |
| `proveedorId` | Long | Sí | ID del proveedor |
| `usuarioId` | Long | Sí | ID del usuario que registra |
| `nroFactura` | String | Sí | Número de la factura |
| `tipoDocumento` | String | Sí | Tipo de documento |
| `fechaEmision` | String | Sí | Fecha de emisión (YYYY-MM-DD) |
| `fechaVencimiento` | String | Sí | Fecha de vencimiento (YYYY-MM-DD) |
| `montoTotal` | Integer | Sí | Monto total en centavos |
| `moneda` | String | Sí | Código de moneda (ej: USD, PEN) |
| `descripcion` | String | No | Descripción adicional |
| `estadoFactura` | String | No | Estado (default: PENDIENTE) |

#### Ejemplo de Respuesta Exitosa (201)

```json
{
  "factura": {
    "id": 1,
    "proveedorId": 5,
    "usuarioId": 2,
    "nroFactura": "F-2024-001",
    "tipoDocumento": "FACTURA",
    "fechaEmision": "2024-07-02",
    "fechaVencimiento": "2024-07-32",
    "montoTotal": 150000,
    "moneda": "PEN",
    "descripcion": "Compra de materiales",
    "rutaArchivo": "facturas/FACTURA_20240702_143052_a1b2c3d4.pdf",
    "nombreArchivo": "FACTURA_20240702_143052_a1b2c3d4.pdf",
    "estadoFactura": "PENDIENTE"
  },
  "archivo": {
    "nombreOriginal": "factura_proveedor_abc.pdf",
    "nombreGuardado": "FACTURA_20240702_143052_a1b2c3d4.pdf",
    "rutaRelativa": "facturas/FACTURA_20240702_143052_a1b2c3d4.pdf",
    "tamaño": 1024000,
    "tipoContenido": "application/pdf"
  }
}
```

#### Ejemplo de Error de Validación (400)

```json
{
  "error": "Error de validación",
  "mensaje": "Solo se permiten archivos PDF"
}
```

### 2. Descargar Archivo de Factura

**GET** `/api/facturas/{id}/download`

Permite descargar el archivo PDF asociado a una factura.

#### Parámetros

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `id` | Long | ID de la factura |

#### Respuesta

- **200**: Archivo PDF para descarga
- **404**: Factura o archivo no encontrado
- **500**: Error interno del servidor

## Validaciones Implementadas

### Validaciones de Archivo

1. **Formato**: Solo se permiten archivos PDF
2. **Tamaño**: Máximo 10MB por archivo
3. **Tipo MIME**: Debe ser `application/pdf`
4. **Nombre**: El archivo debe tener un nombre válido

### Nombrado de Archivos

Los archivos se renombran automáticamente con el formato:
```
FACTURA_YYYYMMDD_HHMMSS_UUID.pdf
```

Ejemplo: `FACTURA_20240702_143052_a1b2c3d4.pdf`

### Estructura de Directorios

```
files/
├── .gitignore
└── facturas/
    ├── FACTURA_20240702_143052_a1b2c3d4.pdf
    ├── FACTURA_20240702_144103_b2c3d4e5.pdf
    └── ...
```

## Ejemplos de Uso

### Ejemplo con cURL

```bash
curl -X POST "http://localhost:8080/conaveg/api/facturas/with-file" \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/ruta/a/mi/factura.pdf" \
  -F "proveedorId=5" \
  -F "usuarioId=2" \
  -F "nroFactura=F-2024-001" \
  -F "tipoDocumento=FACTURA" \
  -F "fechaEmision=2024-07-02" \
  -F "fechaVencimiento=2024-08-02" \
  -F "montoTotal=150000" \
  -F "moneda=PEN" \
  -F "descripcion=Compra de materiales" \
  -F "estadoFactura=PENDIENTE"
```

### Ejemplo con JavaScript (Frontend)

```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('proveedorId', '5');
formData.append('usuarioId', '2');
formData.append('nroFactura', 'F-2024-001');
formData.append('tipoDocumento', 'FACTURA');
formData.append('fechaEmision', '2024-07-02');
formData.append('fechaVencimiento', '2024-08-02');
formData.append('montoTotal', '150000');
formData.append('moneda', 'PEN');
formData.append('descripcion', 'Compra de materiales');
formData.append('estadoFactura', 'PENDIENTE');

fetch('/conaveg/api/facturas/with-file', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data))
.catch(error => console.error('Error:', error));
```

## Configuración

### Variables de Configuración

En `application.properties`:

```properties
# Directorio base para almacenamiento de archivos
app.file.upload-dir=files

# Tamaño máximo de archivo (10MB)
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## Consideraciones de Seguridad

1. **Validación de tipos**: Solo se permiten archivos PDF
2. **Límite de tamaño**: Máximo 10MB por archivo
3. **Nombres únicos**: Se generan nombres únicos para evitar conflictos
4. **Ruta segura**: Los archivos se almacenan en un directorio específico y controlado

## Mantenimiento

### Limpieza de Archivos Huérfanos

En caso de que una factura se elimine, el archivo asociado también se elimina automáticamente. Para limpiar archivos huérfanos manualmente, se puede ejecutar:

```bash
# Listar archivos en el directorio
ls -la files/facturas/

# Eliminar archivos específicos si es necesario
rm files/facturas/archivo_a_eliminar.pdf
```

## Limitaciones Conocidas

1. **Almacenamiento local**: Los archivos se almacenan en el servidor local
2. **Escalabilidad**: Para entornos distribuidos, considerar almacenamiento en nube
3. **Backup**: Los archivos deben incluirse en las estrategias de backup del sistema
