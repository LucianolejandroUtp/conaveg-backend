/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE IF NOT EXISTS `conaveg_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */;
USE `conaveg_db`;

CREATE TABLE IF NOT EXISTS `asignaciones_proyectos_empleados` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `empleados_id` bigint(20) unsigned DEFAULT NULL,
  `proyectos_id` bigint(20) unsigned DEFAULT NULL,
  `fecha_asignacion` date DEFAULT NULL,
  `fecha_fin_asignacion` date DEFAULT NULL,
  `rol` varchar(255) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `asignaciones_proyectos_empleados_unique_id_unique` (`unique_id`),
  KEY `asignaciones_proyectos_empleados_empleados_id_index` (`empleados_id`),
  KEY `asignaciones_proyectos_empleados_proyectos_id_index` (`proyectos_id`),
  CONSTRAINT `asignaciones_proyectos_empleados_empleados_id_foreign` FOREIGN KEY (`empleados_id`) REFERENCES `empleados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `asignaciones_proyectos_empleados_proyectos_id_foreign` FOREIGN KEY (`proyectos_id`) REFERENCES `proyectos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `asignaciones_proyectos_empleados` (`id`, `empleados_id`, `proyectos_id`, `fecha_asignacion`, `fecha_fin_asignacion`, `rol`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 1, 7, '2025-03-08', '2025-07-31', 'Participante', 'ACTIVO', '2c29c590-6ce7-427e-b1c7-be050396338a', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(2, 2, 6, '2025-03-20', '2025-09-04', 'Participante', 'ACTIVO', 'fb0b4e76-3929-44d2-a602-a0d40cf51b05', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(3, 3, 2, '2025-03-09', '2025-07-11', 'Participante', 'ACTIVO', 'e191ee8f-e7a4-44de-8c26-51c2f78f83df', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(4, 4, 6, '2025-05-08', '2025-07-09', 'Participante', 'ACTIVO', '1b5a45b2-ddc1-4f16-9ab8-78bb77de59ee', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(5, 5, 1, '2025-03-07', '2025-08-29', 'Participante', 'ACTIVO', '3f00d2e3-b40a-4fd9-98a2-5b32866005a1', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(6, 6, 2, '2025-02-26', '2025-07-26', 'Participante', 'ACTIVO', 'ded79df9-8883-4a23-add7-e512a008abca', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(7, 7, 2, '2025-05-16', '2025-08-17', 'Participante', 'ACTIVO', '70c63e58-2338-4a12-be1e-d8dfe8ad23f2', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(8, 8, 4, '2025-03-20', '2025-06-14', 'Participante', 'ACTIVO', '1c948025-cc32-468b-aec9-9809b7333a20', '2025-06-05 02:38:49', '2025-06-05 02:38:49');

CREATE TABLE IF NOT EXISTS `asistencias` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `empleados_id` bigint(20) unsigned DEFAULT NULL,
  `entrada` datetime DEFAULT NULL,
  `salida` datetime DEFAULT NULL,
  `tipo_registro` varchar(255) DEFAULT NULL,
  `ubicacion_registro` varchar(255) DEFAULT NULL,
  `metodo_registro` varchar(255) DEFAULT NULL,
  `observacion` text DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `asistencias_unique_id_unique` (`unique_id`),
  KEY `asistencias_empleados_id_index` (`empleados_id`),
  CONSTRAINT `asistencias_empleados_id_foreign` FOREIGN KEY (`empleados_id`) REFERENCES `empleados` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `asistencias` (`id`, `empleados_id`, `entrada`, `salida`, `tipo_registro`, `ubicacion_registro`, `metodo_registro`, `observacion`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 1, '2025-06-04 18:38:48', '2025-06-04 22:38:48', 'Normal', 'Oficina 2', 'Manual', 'Sin novedad', 'ACTIVO', '152c4fbb-4fad-4007-9f56-0c6758d5124d', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(2, 2, '2025-06-04 18:38:48', '2025-06-05 00:38:48', 'Normal', 'Oficina 2', 'Manual', 'Sin novedad', 'ACTIVO', '8098d8b5-5138-480e-9572-34d1e9f01c1f', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(3, 3, '2025-06-04 20:38:49', '2025-06-04 22:38:49', 'Normal', 'Oficina 1', 'Manual', 'Sin novedad', 'ACTIVO', '21998009-7426-4196-9d3d-40d5a6ab27d2', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(4, 4, '2025-06-04 20:38:49', '2025-06-05 00:38:49', 'Normal', 'Oficina 1', 'Manual', 'Sin novedad', 'ACTIVO', '983a6439-c51a-4553-a7ac-7f5009e4dec9', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(5, 5, '2025-06-04 18:38:49', '2025-06-04 23:38:49', 'Normal', 'Oficina 3', 'Manual', 'Sin novedad', 'ACTIVO', 'b828cf3f-63cc-4ced-b36b-2b51e9e911d5', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(6, 6, '2025-06-04 19:38:49', '2025-06-05 00:38:49', 'Normal', 'Oficina 1', 'Manual', 'Sin novedad', 'ACTIVO', '47a118e1-36b5-472f-86b2-d58363a95c34', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(7, 7, '2025-06-04 18:38:49', '2025-06-04 23:38:49', 'Normal', 'Oficina 3', 'Manual', 'Sin novedad', 'ACTIVO', '22a95b0e-8a55-4213-9395-3f5e839454e6', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(8, 8, '2025-06-04 20:38:49', '2025-06-05 00:38:49', 'Normal', 'Oficina 3', 'Manual', 'Sin novedad', 'ACTIVO', 'bfcc9415-c286-42ec-b76f-2f93b664a599', '2025-06-05 02:38:49', '2025-06-05 02:38:49');

CREATE TABLE IF NOT EXISTS `categorias_inventario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `categorias_inventario_nombre_unique` (`nombre`),
  UNIQUE KEY `categorias_inventario_unique_id_unique` (`unique_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `categorias_inventario` (`id`, `nombre`, `descripcion`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 'Equipos', 'Equipos electrónicos y de oficina', 'ACTIVO', '00c93d40-0089-4e34-ba09-310b6d33c687', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(2, 'Herramientas', 'Herramientas manuales y eléctricas', 'ACTIVO', 'fc4ee334-fa65-4afd-9af6-c700fe92c062', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(3, 'Materiales', 'Materiales de construcción y consumo', 'ACTIVO', '58e2d424-e8b8-4b5a-b014-fa3dcfea3ada', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(4, 'Mobiliario', 'Muebles y enseres', 'ACTIVO', '8e55481c-0fdc-4200-a97a-29307487b208', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(5, 'Vehículos', 'Vehículos y transporte', 'ACTIVO', '0d8607c6-a069-42e4-bb58-4c9aab9e68bd', '2025-06-05 02:38:45', '2025-06-05 02:38:45');

CREATE TABLE IF NOT EXISTS `empleados` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `users_id` bigint(20) unsigned DEFAULT NULL,
  `nombres` varchar(255) NOT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `nro_documento` varchar(255) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `puesto` varchar(255) DEFAULT NULL,
  `fecha_ingreso` date DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `empleados_nombres_unique` (`nombres`),
  UNIQUE KEY `empleados_nro_documento_unique` (`nro_documento`),
  UNIQUE KEY `empleados_unique_id_unique` (`unique_id`),
  KEY `empleados_users_id_index` (`users_id`),
  CONSTRAINT `empleados_users_id_foreign` FOREIGN KEY (`users_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `empleados` (`id`, `users_id`, `nombres`, `apellidos`, `nro_documento`, `fecha_nacimiento`, `direccion`, `telefono`, `puesto`, `fecha_ingreso`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 1, 'Empleado 1', 'Apellido 1', 'DOC916176', '1996-06-24', 'Dirección 1', '965848583', 'Puesto 1', '2018-06-04', 'ACTIVO', '6779a76b-5d7c-4168-8d6c-37c8399284c8', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(2, 2, 'Empleado 2', 'Apellido 2', 'DOC825045', '1998-01-16', 'Dirección 2', '931288479', 'Puesto 2', '2022-06-04', 'ACTIVO', '4463c51d-2613-4f06-9fe4-09f21d922306', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(3, 3, 'Empleado 3', 'Apellido 3', 'DOC952474', '1997-11-26', 'Dirección 3', '990777219', 'Puesto 3', '2022-06-04', 'ACTIVO', '06f56305-c8d2-40cc-9040-8bbad5d7a320', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(4, 4, 'Empleado 4', 'Apellido 4', 'DOC465042', '1988-01-19', 'Dirección 4', '971666350', 'Puesto 4', '2019-06-04', 'ACTIVO', '4a1ab38e-6c17-48cf-bb74-a2746d5a5b0b', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(5, 5, 'Empleado 5', 'Apellido 5', 'DOC567827', '2005-03-08', 'Dirección 5', '954023276', 'Puesto 5', '2015-06-04', 'ACTIVO', 'e9e34b91-e346-4ed8-95e8-4aa96924672d', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(6, 6, 'Empleado 6', 'Apellido 6', 'DOC518042', '1986-09-14', 'Dirección 6', '972667944', 'Puesto 6', '2018-06-04', 'ACTIVO', 'aaddb167-60ee-4b01-a0f9-e41badc47781', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(7, 7, 'Empleado 7', 'Apellido 7', 'DOC196702', '2002-08-19', 'Dirección 7', '964776032', 'Puesto 7', '2022-06-04', 'ACTIVO', 'da8d9820-6431-4f3f-89fa-526cbe3e2701', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(8, 8, 'Empleado 8', 'Apellido 8', 'DOC413759', '1996-07-22', 'Dirección 8', '921753461', 'Puesto 8', '2023-06-04', 'ACTIVO', '6269a1be-5ac2-47ff-9c83-8c89513aa4af', '2025-06-05 02:38:48', '2025-06-05 02:38:48');

CREATE TABLE IF NOT EXISTS `facturas` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `proveedores_id` bigint(20) unsigned DEFAULT NULL,
  `usuarios_id` bigint(20) unsigned DEFAULT NULL,
  `nro_factura` varchar(255) DEFAULT NULL,
  `tipo_documento` varchar(255) DEFAULT NULL,
  `fecha_emision` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `monto_total` int(11) NOT NULL DEFAULT 0,
  `moneda` varchar(255) DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  `ruta_archivo` varchar(255) DEFAULT NULL,
  `nombre_archivo` varchar(255) DEFAULT NULL,
  `estado_factura` varchar(255) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `facturas_nro_factura_unique` (`nro_factura`),
  UNIQUE KEY `facturas_unique_id_unique` (`unique_id`),
  KEY `facturas_proveedores_id_index` (`proveedores_id`),
  KEY `facturas_usuarios_id_index` (`usuarios_id`),
  CONSTRAINT `facturas_proveedores_id_foreign` FOREIGN KEY (`proveedores_id`) REFERENCES `proveedores` (`id`) ON DELETE CASCADE,
  CONSTRAINT `facturas_usuarios_id_foreign` FOREIGN KEY (`usuarios_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `facturas` (`id`, `proveedores_id`, `usuarios_id`, `nro_factura`, `tipo_documento`, `fecha_emision`, `fecha_vencimiento`, `monto_total`, `moneda`, `descripcion`, `ruta_archivo`, `nombre_archivo`, `estado_factura`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 2, 7, 'F00001', 'Factura', '2025-04-05', '2025-09-11', 1041, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', '03eb9174-62eb-48d6-9624-1715ad1b06cc', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(2, 5, 8, 'F00002', 'Factura', '2025-04-03', '2025-06-29', 9009, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', '7c6815e6-3b54-4fa1-96d5-0f285e750319', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(3, 2, 2, 'F00003', 'Factura', '2025-05-31', '2025-08-30', 2463, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', '6a6df8b0-e593-4d88-9b00-511d3fea5486', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(4, 4, 5, 'F00004', 'Factura', '2025-03-06', '2025-08-31', 7050, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', '534fbcbc-c400-4232-98de-fdb540f9e3d9', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(5, 4, 8, 'F00005', 'Factura', '2025-05-15', '2025-08-21', 2737, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', 'bb8dbb22-6cc0-4410-9317-b8db43aee59d', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(6, 6, 8, 'F00006', 'Factura', '2025-04-14', '2025-09-04', 5495, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', 'd6647d56-455c-41ce-99db-b4cfefdd3499', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(7, 3, 7, 'F00007', 'Factura', '2025-04-25', '2025-06-13', 6546, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', 'bc699618-f5b9-4c67-9918-fae92b4d8ce9', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(8, 7, 6, 'F00008', 'Factura', '2025-04-04', '2025-07-27', 8197, 'PEN', 'Factura de prueba', NULL, NULL, 'Pendiente', 'ACTIVO', '7005c3e6-d253-4653-be56-fbfb33a2a7b7', '2025-06-05 02:38:49', '2025-06-05 02:38:49');

CREATE TABLE IF NOT EXISTS `inventario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `categoria_id` bigint(20) unsigned DEFAULT NULL,
  `codigo` varchar(255) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `marca` varchar(255) DEFAULT NULL,
  `modelo` varchar(255) DEFAULT NULL,
  `nro_serie` varchar(255) DEFAULT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `unidad_medida` varchar(255) DEFAULT NULL,
  `fecha_aquisicion` date DEFAULT NULL,
  `estado_conservacion` varchar(255) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `inventario_codigo_unique` (`codigo`),
  UNIQUE KEY `inventario_unique_id_unique` (`unique_id`),
  KEY `inventario_categoria_id_index` (`categoria_id`),
  CONSTRAINT `inventario_categoria_id_foreign` FOREIGN KEY (`categoria_id`) REFERENCES `categorias_inventario` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `inventario` (`id`, `categoria_id`, `codigo`, `nombre`, `descripcion`, `marca`, `modelo`, `nro_serie`, `stock`, `unidad_medida`, `fecha_aquisicion`, `estado_conservacion`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 2, 'INV-0001', 'Item Inventario 1', 'Descripción del inventario 1', 'Marca 1', 'Modelo 1', 'SN93719', 24, 'unidad', '2025-03-02', 'Bueno', 'ACTIVO', '855c9d1b-fca5-46cb-bd64-5203e3104697', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(2, 4, 'INV-0002', 'Item Inventario 2', 'Descripción del inventario 2', 'Marca 2', 'Modelo 2', 'SN99519', 40, 'unidad', '2025-03-07', 'Bueno', 'ACTIVO', '66e654ad-37fb-4a55-960e-92bb732d85bb', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(3, 1, 'INV-0003', 'Item Inventario 3', 'Descripción del inventario 3', 'Marca 3', 'Modelo 3', 'SN28580', 32, 'unidad', '2025-05-05', 'Bueno', 'ACTIVO', 'e120a6eb-590e-4817-b6ef-79dc50d11cc8', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(4, 2, 'INV-0004', 'Item Inventario 4', 'Descripción del inventario 4', 'Marca 4', 'Modelo 4', 'SN40603', 29, 'unidad', '2025-04-23', 'Bueno', 'ACTIVO', '577b3d7c-d174-47cd-9eb0-9d1c8e434052', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(5, 5, 'INV-0005', 'Item Inventario 5', 'Descripción del inventario 5', 'Marca 5', 'Modelo 5', 'SN70727', 5, 'unidad', '2025-03-25', 'Bueno', 'ACTIVO', '1123e5cc-02f0-4d4e-9233-439bd88277cd', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(6, 5, 'INV-0006', 'Item Inventario 6', 'Descripción del inventario 6', 'Marca 6', 'Modelo 6', 'SN39432', 35, 'unidad', '2025-05-15', 'Bueno', 'ACTIVO', 'cb2529f0-204d-483a-9fc1-0267d6dd3db1', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(7, 2, 'INV-0007', 'Item Inventario 7', 'Descripción del inventario 7', 'Marca 7', 'Modelo 7', 'SN65441', 30, 'unidad', '2025-04-24', 'Bueno', 'ACTIVO', '8256e2d1-c136-46bb-9486-b2e3e549fa4a', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(8, 2, 'INV-0008', 'Item Inventario 8', 'Descripción del inventario 8', 'Marca 8', 'Modelo 8', 'SN79507', 23, 'unidad', '2025-03-11', 'Bueno', 'ACTIVO', 'a2da4c30-41ee-4491-a502-456dab278fab', '2025-06-05 02:38:45', '2025-06-05 02:38:45');

CREATE TABLE IF NOT EXISTS `migrations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
	(1, '2025_05_13_163859_create_roles_table', 1),
	(2, '2025_05_13_203425_create_categorias_inventario_table', 1),
	(3, '2025_05_13_203630_create_inventario_table', 1),
	(4, '2025_05_13_204550_create_proyectos_table', 1),
	(5, '2025_05_13_205045_create_users_table', 1),
	(6, '2025_05_13_205050_create_empleados_table', 1),
	(7, '2025_05_13_205856_create_asistencias_table', 1),
	(8, '2025_05_13_210611_create_asignaciones_proyectos_empleados_table', 1),
	(9, '2025_05_13_211505_create_movimientos_inventario_table', 1),
	(10, '2025_05_13_235303_create_proveedores_table', 1),
	(11, '2025_05_13_235930_create_facturas_table', 1);

CREATE TABLE IF NOT EXISTS `movimientos_inventario` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `inventario_id` bigint(20) unsigned DEFAULT NULL,
  `empleados_id_asigna` bigint(20) unsigned DEFAULT NULL,
  `empleados_id_recibe` bigint(20) unsigned DEFAULT NULL,
  `proyectos_id` bigint(20) unsigned DEFAULT NULL,
  `tipo_movimiento` varchar(255) DEFAULT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 0,
  `fecha_movimiento` datetime DEFAULT NULL,
  `observacion` text DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `movimientos_inventario_unique_id_unique` (`unique_id`),
  KEY `movimientos_inventario_inventario_id_index` (`inventario_id`),
  KEY `movimientos_inventario_empleados_id_asigna_index` (`empleados_id_asigna`),
  KEY `movimientos_inventario_empleados_id_recibe_index` (`empleados_id_recibe`),
  KEY `movimientos_inventario_proyectos_id_index` (`proyectos_id`),
  CONSTRAINT `movimientos_inventario_empleados_id_asigna_foreign` FOREIGN KEY (`empleados_id_asigna`) REFERENCES `empleados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `movimientos_inventario_empleados_id_recibe_foreign` FOREIGN KEY (`empleados_id_recibe`) REFERENCES `empleados` (`id`) ON DELETE CASCADE,
  CONSTRAINT `movimientos_inventario_inventario_id_foreign` FOREIGN KEY (`inventario_id`) REFERENCES `inventario` (`id`) ON DELETE CASCADE,
  CONSTRAINT `movimientos_inventario_proyectos_id_foreign` FOREIGN KEY (`proyectos_id`) REFERENCES `proyectos` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `movimientos_inventario` (`id`, `inventario_id`, `empleados_id_asigna`, `empleados_id_recibe`, `proyectos_id`, `tipo_movimiento`, `cantidad`, `fecha_movimiento`, `observacion`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 1, 7, 5, 2, 'Salida', 10, '2025-05-23 21:38:49', 'Movimiento de prueba', 'ACTIVO', '8e2978a2-8598-477b-8dc5-c0d9e3104a02', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(2, 2, 6, 5, 4, 'Salida', 9, '2025-06-02 21:38:49', 'Movimiento de prueba', 'ACTIVO', 'b8c6b1db-efd9-4f3a-857a-41fb6f19fc97', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(3, 3, 4, 7, 2, 'Salida', 4, '2025-05-26 21:38:49', 'Movimiento de prueba', 'ACTIVO', '98f70537-4a91-43a9-98a2-3c9484eca5a6', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(4, 4, 6, 4, 1, 'Salida', 3, '2025-05-14 21:38:49', 'Movimiento de prueba', 'ACTIVO', '8b0f2490-b4b2-40f1-baa4-0a771d638eb3', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(5, 5, 2, 7, 6, 'Salida', 4, '2025-06-02 21:38:49', 'Movimiento de prueba', 'ACTIVO', 'd55a02e0-1918-4326-84e2-d93c14214190', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(6, 6, 1, 8, 3, 'Salida', 3, '2025-05-07 21:38:49', 'Movimiento de prueba', 'ACTIVO', '0f52edc0-7988-4b0c-ba19-07378e661721', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(7, 7, 2, 6, 7, 'Salida', 7, '2025-05-19 21:38:49', 'Movimiento de prueba', 'ACTIVO', 'f9550c9f-352e-410e-b137-02f0ae2055e5', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(8, 8, 1, 7, 6, 'Salida', 2, '2025-05-14 21:38:49', 'Movimiento de prueba', 'ACTIVO', '29cf2c6d-c17b-4f56-95ca-8d958dcca922', '2025-06-05 02:38:49', '2025-06-05 02:38:49');

CREATE TABLE IF NOT EXISTS `proveedores` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `ruc` varchar(255) DEFAULT NULL,
  `razon_social` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `proveedores_ruc_unique` (`ruc`),
  UNIQUE KEY `proveedores_unique_id_unique` (`unique_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `proveedores` (`id`, `ruc`, `razon_social`, `direccion`, `telefono`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 'RUC44599299', 'Proveedor 1', 'Dirección 1', '982881729', 'ACTIVO', '1c905d06-5acb-464d-87dd-ba1e3229a0da', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(2, 'RUC82977101', 'Proveedor 2', 'Dirección 2', '985595496', 'ACTIVO', 'e3b74f8f-3426-444f-b98e-a109e899f571', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(3, 'RUC91433110', 'Proveedor 3', 'Dirección 3', '910124105', 'ACTIVO', 'dbb7e50c-fa39-4596-8af2-42222e4a1535', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(4, 'RUC55689214', 'Proveedor 4', 'Dirección 4', '979936779', 'ACTIVO', 'c0339ef0-60f2-4d3f-83a2-1430c2b0a5dc', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(5, 'RUC69061478', 'Proveedor 5', 'Dirección 5', '981486182', 'ACTIVO', '0f14064d-2a6f-4480-b42d-e143681307c0', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(6, 'RUC57292079', 'Proveedor 6', 'Dirección 6', '998935789', 'ACTIVO', '92db6126-dd36-4279-8a5f-8733779a50c6', '2025-06-05 02:38:49', '2025-06-05 02:38:49'),
	(7, 'RUC35124346', 'Proveedor 7', 'Dirección 7', '915842010', 'ACTIVO', '0bd0b8f5-e1bb-4e98-baed-d55b719b5b4b', '2025-06-05 02:38:49', '2025-06-05 02:38:49');

CREATE TABLE IF NOT EXISTS `proyectos` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `ubicacion` varchar(255) DEFAULT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_fin` date DEFAULT NULL,
  `estado_proyecto` varchar(255) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `proyectos_nombre_unique` (`nombre`),
  UNIQUE KEY `proyectos_unique_id_unique` (`unique_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `proyectos` (`id`, `nombre`, `descripcion`, `ubicacion`, `fecha_inicio`, `fecha_fin`, `estado_proyecto`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 'Proyecto 1', 'Descripción del proyecto 1', 'Ubicación 1', '2025-03-28', '2025-08-05', 'En ejecución', 'ACTIVO', 'a9b141bf-6451-450e-95e2-912d14cc3a77', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(2, 'Proyecto 2', 'Descripción del proyecto 2', 'Ubicación 2', '2025-03-10', '2025-08-23', 'En ejecución', 'ACTIVO', 'bf406730-352a-42d9-9f7b-54bf8936a787', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(3, 'Proyecto 3', 'Descripción del proyecto 3', 'Ubicación 3', '2025-03-26', '2025-08-22', 'En ejecución', 'ACTIVO', '4bfb657c-60d6-4517-b69c-59706154bef4', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(4, 'Proyecto 4', 'Descripción del proyecto 4', 'Ubicación 4', '2025-04-12', '2025-07-22', 'En ejecución', 'ACTIVO', '0ef33b3e-18c5-4ba7-bfb5-493e3a56cb38', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(5, 'Proyecto 5', 'Descripción del proyecto 5', 'Ubicación 5', '2025-04-21', '2025-06-17', 'En ejecución', 'ACTIVO', '970a2456-c5b6-43ee-a403-67554cfd32d0', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(6, 'Proyecto 6', 'Descripción del proyecto 6', 'Ubicación 6', '2025-03-19', '2025-08-26', 'En ejecución', 'ACTIVO', '1d19f528-3d92-4a25-90dc-6753f56a5f38', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(7, 'Proyecto 7', 'Descripción del proyecto 7', 'Ubicación 7', '2025-02-28', '2025-08-28', 'En ejecución', 'ACTIVO', 'f71a8249-570a-4724-96f7-ef6567448522', '2025-06-05 02:38:45', '2025-06-05 02:38:45');

CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `roles_nombre_unique` (`nombre`),
  UNIQUE KEY `roles_unique_id_unique` (`unique_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `roles` (`id`, `nombre`, `descripcion`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 'Administrador', 'Acceso total al sistema', 'ACTIVO', 'a12cf9fb-a93d-4dc9-b493-c38a47228b11', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(2, 'Supervisor', 'Supervisa procesos y usuarios', 'ACTIVO', '4b3f4a8e-c7ae-483d-87a6-bc2585890934', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(3, 'Almacenero', 'Gestiona inventario', 'ACTIVO', 'b023afd6-15f1-4683-8b7e-f8ab21c5d1b0', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(4, 'Empleado', 'Usuario estándar', 'ACTIVO', '77aea573-fdd5-495a-a948-0e7bb40fd4c1', '2025-06-05 02:38:45', '2025-06-05 02:38:45'),
	(5, 'Invitado', 'Acceso limitado', 'ACTIVO', '3fbc8a60-4f9b-4f84-87c0-9bdbf323f7f2', '2025-06-05 02:38:45', '2025-06-05 02:38:45');

CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) unsigned DEFAULT NULL,
  `user_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_email_unique` (`email`),
  UNIQUE KEY `users_unique_id_unique` (`unique_id`),
  KEY `users_role_id_index` (`role_id`),
  CONSTRAINT `users_role_id_foreign` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `users` (`id`, `role_id`, `user_name`, `email`, `email_verified_at`, `password`, `remember_token`, `estado`, `unique_id`, `created_at`, `updated_at`) VALUES
	(1, 3, 'usuario1', 'usuario1@mail.com', '2025-06-05 02:38:45', '$2y$12$xvlAax.vLHWk3nDIdwjY.O75S0JMxjzZRms0nAF0m3B/aLZfWQJ3K', 'lnIqUQea5v', 'ACTIVO', 'f3418a20-68df-4f7c-a4c4-7390db482e2b', '2025-06-05 02:38:46', '2025-06-05 02:38:46'),
	(2, 3, 'usuario2', 'usuario2@mail.com', '2025-06-05 02:38:46', '$2y$12$qjz1vtGOHi6PeKoXXUlVL.6J7CHgp/W397..VYBmicj2fTlvrpyBi', '0AH4er6Gv5', 'ACTIVO', '7dc56b10-8e14-4f84-93d3-ced08bb228ea', '2025-06-05 02:38:46', '2025-06-05 02:38:46'),
	(3, 5, 'usuario3', 'usuario3@mail.com', '2025-06-05 02:38:46', '$2y$12$IxFtlG4fzj2xJZTffDhw3e9G.NNHjDT4RztYa.7vzZaf8ziNIru..', 'HbwbipSUnf', 'ACTIVO', '434f0580-fdde-48c1-b6ab-2e1347c606ca', '2025-06-05 02:38:46', '2025-06-05 02:38:46'),
	(4, 1, 'usuario4', 'usuario4@mail.com', '2025-06-05 02:38:46', '$2y$12$XxdKoD2CgZk84JVlr1tBHO3A6H9R45KAfZeJUd3Gvk.YUih7Jo8pi', 'NvGZ4tCWQo', 'ACTIVO', '290fb409-914f-4672-bdb5-729acd8a45b1', '2025-06-05 02:38:47', '2025-06-05 02:38:47'),
	(5, 4, 'usuario5', 'usuario5@mail.com', '2025-06-05 02:38:47', '$2y$12$OB19wrVEX4GlSp0e6b/LCe6u3aFY4Z.udU5WdsLevZC1ybtorfnEG', 'eeDcyHcSfu', 'ACTIVO', '466a180d-72e5-4c80-8009-39a68632a177', '2025-06-05 02:38:47', '2025-06-05 02:38:47'),
	(6, 3, 'usuario6', 'usuario6@mail.com', '2025-06-05 02:38:47', '$2y$12$/YSYPzDsZW2DCEInQ1key.2HXjUuis3FHqM280dJ7LVzRmAgGSz4G', 'XavjjSrJR5', 'ACTIVO', 'd4d5ff5e-8f56-48ba-be4a-e5011a24928c', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(7, 1, 'usuario7', 'usuario7@mail.com', '2025-06-05 02:38:48', '$2y$12$xlKn6bRuVYm/re.nauBvWeDNEZ8JAntq5wCFey8A6jOO5xOAToNFy', 'jJ0zu5i5Gv', 'ACTIVO', '0b89f5a9-304c-4970-889a-c0f8fcd3e78b', '2025-06-05 02:38:48', '2025-06-05 02:38:48'),
	(8, 2, 'usuario8', 'usuario8@mail.com', '2025-06-05 02:38:48', '$2y$12$B/hN/uwFZh7hngl6fiE95e5YU0VcoCmtP0ofubkw9azPM61pgurFS', 'F4XoIMBtTW', 'ACTIVO', '11a56882-8043-43cb-8698-21da51632f9d', '2025-06-05 02:38:48', '2025-06-05 02:38:48');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
