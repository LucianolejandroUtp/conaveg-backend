# 👤 MANUAL 6: GUÍA DE USUARIO FINAL - SISTEMA CONA

## 📋 **INFORMACIÓN DEL DOCUMENTO**

**Fecha de Creación**: 21 de Julio de 2025  
**Proyecto**: Sistema CONA (Gestión CONAVEG)  
**Audiencia**: Usuarios del sistema, Gerentes, Personal Administrativo  
**Nivel**: Básico - Intermedio  
**Tiempo Estimado**: 1-2 horas  
**Última Actualización**: 21 de Julio de 2025  

---

## 🎯 **OBJETIVOS DE APRENDIZAJE**

Al finalizar este manual, serás capaz de:
- ✅ Entender el propósito y las funcionalidades principales del Sistema CONA.
- ✅ Navegar y utilizar los diferentes módulos del sistema según tu rol.
- ✅ Realizar operaciones comunes como consultar inventarios, registrar asistencias y gestionar proyectos.
- ✅ Subir y gestionar documentos como facturas.
- ✅ Comprender los flujos de trabajo básicos del sistema.

---

## 🚀 **INTRODUCCIÓN AL SISTEMA CONA**

¡Bienvenido al Sistema CONA! Esta es una herramienta diseñada para centralizar y optimizar la gestión de las operaciones de CONAVEG. El sistema te permitirá administrar inventarios, personal, proyectos y documentos de una manera más eficiente y segura.

Este manual está diseñado para ser una guía práctica. No te preocupes por los detalles técnicos; nos enfocaremos en **qué puedes hacer** y **cómo hacerlo**.

### **Acceso al Sistema**
Para acceder, necesitarás un **email** y una **contraseña** proporcionados por tu administrador. La primera vez que inicies sesión, se te podría pedir que cambies tu contraseña por una más segura.

### **Roles de Usuario**
Tu experiencia en el sistema dependerá de tu rol. Los roles principales son:
- **USER**: Puede ver información de proyectos e inventario.
- **EMPLEADO**: Puede gestionar información de empleados y ver proyectos/inventario.
- **GERENTE**: Puede gestionar proyectos e inventario.
- **ADMIN**: Tiene acceso completo a todo el sistema.

Este manual cubrirá las funcionalidades disponibles para todos los roles, indicando cuándo una acción está restringida a roles específicos.

---

## 📦 **MÓDULO 1: GESTIÓN DE INVENTARIOS**

Este módulo te permite llevar un control detallado de todos los activos y productos de la empresa.

### **Consultar el Inventario**
Cualquier usuario autenticado puede ver el inventario.
- **Acción**: Navega a la sección de "Inventario".
- **Resultado**: Verás una lista de todos los productos, con detalles como:
    - **Nombre y Descripción**: Qué es el producto.
    - **Stock**: Cuántas unidades hay disponibles.
    - **Categoría**: A qué tipo de producto pertenece.
    - **Estado de Conservación**: Si está en buen, regular o mal estado.

### **Añadir un Nuevo Producto (Solo Gerentes y Admins)**
- **Acción**:
    1.  Ve a la sección de "Inventario" y busca el botón "Añadir Producto".
    2.  Completa el formulario con los detalles del producto (nombre, código, marca, stock inicial, etc.).
    3.  Guarda el formulario.
- **Resultado**: El nuevo producto aparecerá en la lista del inventario.

### **Actualizar un Producto (Solo Gerentes y Admins)**
- **Acción**:
    1.  En la lista de inventario, selecciona el producto que deseas modificar.
    2.  Haz clic en "Editar".
    3.  Cambia los campos necesarios (por ejemplo, actualiza el stock después de una compra).
    4.  Guarda los cambios.
- **Resultado**: La información del producto se actualizará en tiempo real.

### **Movimientos de Inventario**
El sistema registra automáticamente cada entrada y salida de productos, creando un historial de movimientos para una trazabilidad completa.

---

## 👥 **MÓDULO 2: GESTIÓN DE EMPLEADOS Y ASISTENCIAS**

Este módulo centraliza la información del personal y su registro de asistencia.

### **Consultar Lista de Empleados (Solo Empleados y Admins)**
- **Acción**: Accede a la sección "Empleados".
- **Resultado**: Podrás ver una lista de los empleados de la empresa con su información básica.

### **Registrar Asistencia**
- **Acción**: Dependiendo de la configuración, la asistencia se puede registrar mediante:
    - **Tarjeta**: Pasando tu tarjeta por un lector.
    - **Registro Manual**: A través de una interfaz específica.
- **Resultado**: El sistema guardará la hora de entrada y salida, calculando las horas trabajadas.

### **Consultar Mis Asistencias**
- **Acción**: Ve a tu perfil y busca la sección "Mis Asistencias".
- **Resultado**: Podrás ver tu historial de registros de entrada y salida, así como un resumen de tus horas trabajadas por día, semana o mes.

---

## 🏗️ **MÓDULO 3: GESTIÓN DE PROYECTOS Y ASIGNACIONES**

Este módulo permite organizar los proyectos de la empresa y asignar personal a cada uno de ellos.

### **Consultar Proyectos**
Todos los usuarios pueden ver la lista de proyectos activos.
- **Acción**: Navega a la sección "Proyectos".
- **Resultado**: Verás una lista de proyectos con su estado (ej. "En Progreso", "Completado"), fechas de inicio y fin, y una descripción.

### **Crear un Nuevo Proyecto (Solo Gerentes y Admins)**
- **Acción**:
    1.  En la sección "Proyectos", haz clic en "Nuevo Proyecto".
    2.  Rellena la información del proyecto: nombre, cliente, presupuesto, fechas, etc.
    3.  Guarda el proyecto.
- **Resultado**: El nuevo proyecto estará disponible para que se le asignen empleados y recursos.

### **Asignar Empleados a un Proyecto (Solo Gerentes y Admins)**
- **Acción**:
    1.  Abre la vista de detalle de un proyecto.
    2.  Busca la opción "Asignar Empleado".
    3.  Selecciona uno o más empleados de la lista y asígnales un rol dentro del proyecto (ej. "Supervisor", "Técnico").
- **Resultado**: Los empleados asignados podrán ver el proyecto en su lista de "Mis Proyectos".

---

## 📄 **MÓDULO 4: GESTIÓN DE FACTURAS Y DOCUMENTOS**

Este módulo facilita la gestión de facturas de proveedores, permitiendo adjuntar los documentos PDF correspondientes.

### **Subir una Nueva Factura con Archivo (Roles Autorizados)**
- **Acción**:
    1.  Ve a la sección "Facturas" y selecciona "Subir Factura".
    2.  Aparecerá un formulario donde deberás:
        - **Adjuntar el archivo PDF** de la factura.
        - Rellenar los datos de la factura: número, proveedor, fecha de emisión, monto, etc.
    3.  Haz clic en "Guardar".
- **Resultado**: La factura se creará en el sistema y el archivo PDF quedará asociado a ella. El archivo se renombra automáticamente para una organización segura.

### **Consultar Facturas**
- **Acción**: En la sección "Facturas", podrás ver una lista de todas las facturas registradas.
- **Resultado**: Verás los detalles de cada factura y un enlace para descargar el PDF adjunto.

### **Descargar el PDF de una Factura**
- **Acción**:
    1.  En la lista de facturas, encuentra la que te interesa.
    2.  Haz clic en el icono de "Descargar" o en el nombre del archivo.
- **Resultado**: El archivo PDF de la factura se descargará a tu dispositivo.

---

## 📈 **MÓDULO 5: REPORTES Y CONSULTAS**

El sistema ofrece capacidades para generar reportes y realizar consultas, aunque las funcionalidades avanzadas de reportes (como exportar a PDF/Excel) están planificadas para futuras versiones.

### **Consultas Disponibles**:
- **Inventario Bajo**: Puedes filtrar el inventario para ver qué productos tienen un stock por debajo de un umbral mínimo.
- **Proyectos por Estado**: Filtrar los proyectos para ver cuáles están activos, completados o pendientes.
- **Asistencias por Rango de Fechas**: Consultar las horas trabajadas por un empleado en un período específico.

---

## 🤔 **PREGUNTAS FRECUENTES (FAQ)**

**P: ¿Qué hago si olvido mi contraseña?**
**R:** En la pantalla de login, busca un enlace que diga "¿Olvidaste tu contraseña?". Sigue las instrucciones para recibir un enlace de recuperación en tu correo electrónico.

**P: ¿Por qué no puedo editar un proyecto?**
**R:** La edición de proyectos está restringida a los roles de **Gerente** y **Admin**. Si tienes un rol de **Empleado** o **User**, solo tendrás permiso de lectura.

**P: El sistema dice que mi archivo de factura no es válido. ¿Por qué?**
**R:** Asegúrate de que el archivo que estás intentando subir sea un **PDF** y que no supere los **10 MB** de tamaño.

**P: ¿Cómo puedo ver a qué proyectos estoy asignado?**
**R:** En tu perfil de usuario, debería haber una sección de "Mis Proyectos" que lista todos los proyectos en los que estás participando.

---

## 📞 **SOPORTE Y CONTACTO**

Si tienes problemas que no puedes resolver con esta guía, no dudes en contactar al equipo de soporte:
- 📧 **Email**: soporte-cona@conaveg.com
- 💬 **Canal de Slack**: #ayuda-sistema-cona

---

**📅 Fecha de Creación**: 21 de Julio de 2025  
**👨‍💻 Responsable**: Equipo de Documentación CONA  
**📋 Estado**: Manual Completo y Validado  
**🔄 Próxima Revisión**: 21 de Agosto de 2025
