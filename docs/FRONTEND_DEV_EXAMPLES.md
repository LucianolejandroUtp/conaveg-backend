# Ejemplos de Uso para Frontend - Modo Desarrollo

## Configuración Inicial

Cuando el backend está en modo desarrollo, el frontend puede trabajar sin implementar autenticación inicialmente.

### 1. Verificar Estado del Backend

```javascript
// Verificar si el backend está en modo desarrollo
fetch('http://localhost:8080/conaveg/api/dev/status')
  .then(response => response.json())
  .then(data => {
    if (data.skipAuthentication) {
      console.log('✅ Backend en modo desarrollo - Sin autenticación requerida');
    }
  });
```

### 2. Simular Login (Opcional)

Si necesitas datos de usuario para la UI:

```javascript
// Simular login para obtener datos de usuario
const simulateLogin = async () => {
  const response = await fetch('http://localhost:8080/conaveg/api/dev/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      email: 'dev@test.com',
      password: 'cualquier-cosa'
    })
  });
  
  const data = await response.json();
  
  // Guardar datos del usuario simulado
  localStorage.setItem('user', JSON.stringify(data.user));
  localStorage.setItem('token', data.token); // Token ficticio
  
  return data;
};
```

### 3. Acceso Directo a Endpoints

Puedes llamar cualquier endpoint sin headers de autenticación:

```javascript
// Obtener empleados - SIN TOKEN
fetch('http://localhost:8080/conaveg/api/empleados')
  .then(response => response.json())
  .then(empleados => {
    console.log('Empleados:', empleados);
  });

// Crear proyecto - SIN TOKEN
const crearProyecto = async (proyecto) => {
  const response = await fetch('http://localhost:8080/conaveg/api/proyectos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
      // NO NECESITAS: 'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(proyecto)
  });
  
  return await response.json();
};

// Actualizar inventario - SIN TOKEN
const actualizarInventario = async (id, datos) => {
  const response = await fetch(`http://localhost:8080/conaveg/api/inventario/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json'
      // NO NECESITAS: 'Authorization': 'Bearer ' + token
    },
    body: JSON.stringify(datos)
  });
  
  return await response.json();
};
```

### 4. Obtener Usuario Actual (Simulado)

```javascript
// Obtener datos del usuario actual (simulado)
fetch('http://localhost:8080/conaveg/api/dev/me')
  .then(response => response.json())
  .then(user => {
    console.log('Usuario actual:', user);
    // user.role será "ADMINISTRADOR"
    // user.userName será "dev-user"
  });
```

## Configuración para Desarrollo vs Producción

```javascript
// Configuración adaptable
const API_CONFIG = {
  development: {
    baseURL: 'http://localhost:8080/conaveg/api',
    skipAuth: true,
    loginEndpoint: '/dev/login',  // Usar endpoint simulado
    userEndpoint: '/dev/me'       // Usar endpoint simulado
  },
  production: {
    baseURL: 'https://tu-servidor.com/api',
    skipAuth: false,
    loginEndpoint: '/auth/login', // Usar endpoint real
    userEndpoint: '/auth/me'      // Usar endpoint real
  }
};

const isDevelopment = process.env.NODE_ENV === 'development';
const config = isDevelopment ? API_CONFIG.development : API_CONFIG.production;

// Función de llamada a API adaptable
const apiCall = async (endpoint, options = {}) => {
  const url = config.baseURL + endpoint;
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers
  };
  
  // Solo agregar token en producción
  if (!config.skipAuth && localStorage.getItem('token')) {
    headers.Authorization = 'Bearer ' + localStorage.getItem('token');
  }
  
  return fetch(url, {
    ...options,
    headers
  });
};
```

## React Hook Ejemplo

```javascript
// Hook personalizado para desarrollo
import { useState, useEffect } from 'react';

const useAuth = () => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  
  useEffect(() => {
    // En desarrollo, simular usuario autenticado
    if (process.env.NODE_ENV === 'development') {
      fetch('http://localhost:8080/conaveg/api/dev/me')
        .then(response => response.json())
        .then(userData => {
          setUser(userData);
          setIsAuthenticated(true);
        })
        .catch(() => {
          // Si falla, usar datos ficticios
          setUser({
            id: 1,
            userName: 'dev-user',
            email: 'dev@test.com',
            role: 'ADMINISTRADOR'
          });
          setIsAuthenticated(true);
        });
    }
  }, []);
  
  const login = async (credentials) => {
    if (process.env.NODE_ENV === 'development') {
      // Login simulado
      const response = await fetch('http://localhost:8080/conaveg/api/dev/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials)
      });
      const data = await response.json();
      setUser(data.user);
      setIsAuthenticated(true);
      return data;
    } else {
      // Implementar login real aquí más tarde
    }
  };
  
  return { user, isAuthenticated, login };
};

export default useAuth;
```

## Ventajas para el Frontend

✅ **Desarrollo más rápido**: No necesitas implementar login inicialmente  
✅ **Datos consistentes**: Usuario simulado siempre disponible  
✅ **Sin bloqueos**: Puedes trabajar en la UI sin esperar la autenticación  
✅ **Fácil transición**: Cambiar a autenticación real solo requiere cambiar la configuración  
✅ **Testing simplificado**: No necesitas mantener tokens válidos durante las pruebas  

## Recordatorios Importantes

⚠️ **Este modo SOLO funciona en desarrollo**  
⚠️ **NO funcionará en producción**  
⚠️ **Recuerda implementar la autenticación real antes del deploy**  
⚠️ **Los endpoints `/api/dev/` no existirán en producción**
