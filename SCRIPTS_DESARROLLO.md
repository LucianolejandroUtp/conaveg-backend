# Scripts de Inicio Rápido

## Para Windows
```cmd
start-dev.bat
```

## Para Linux/Mac
```bash
chmod +x start-dev.sh
./start-dev.sh
```

## Verificación
Una vez iniciado, verifica que el modo desarrollo esté activo:

```bash
curl http://localhost:8080/conaveg/api/dev/status
```

Deberías ver:
```json
{
  "message": "Modo desarrollo activo - Autenticación deshabilitada",
  "warning": "NO USAR EN PRODUCCIÓN",
  "skipAuthentication": true
}
```
