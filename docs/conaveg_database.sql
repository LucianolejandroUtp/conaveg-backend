-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: conaveg_db
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `asignaciones_proyectos_empleados`
--

DROP TABLE IF EXISTS `asignaciones_proyectos_empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asignaciones_proyectos_empleados` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asignaciones_proyectos_empleados`
--

LOCK TABLES `asignaciones_proyectos_empleados` WRITE;
/*!40000 ALTER TABLE `asignaciones_proyectos_empleados` DISABLE KEYS */;
INSERT INTO `asignaciones_proyectos_empleados` VALUES (1,1,2,'2025-06-12','2025-09-22','Participante','ACTIVO','4c26f12d-cb6c-4740-a7fc-121435bd4ba8','2025-06-24 02:19:14','2025-06-24 02:19:14'),(2,2,3,'2025-04-15','2025-08-08','Participante','ACTIVO','95f15650-5743-43f5-8c41-4139eef46e31','2025-06-24 02:19:14','2025-06-24 02:19:14'),(3,3,3,'2025-06-07','2025-09-18','Participante','ACTIVO','fd4d089d-b9f5-44a6-906a-acfd687d2927','2025-06-24 02:19:14','2025-06-24 02:19:14'),(4,4,2,'2025-04-30','2025-07-21','Participante','ACTIVO','398c62e8-8602-437a-b159-92550f30edc9','2025-06-24 02:19:14','2025-06-24 02:19:14'),(5,5,3,'2025-04-02','2025-08-23','Participante','ACTIVO','571b052c-8206-4651-9ac2-0c431942b833','2025-06-24 02:19:14','2025-06-24 02:19:14'),(6,6,1,'2025-06-07','2025-08-24','Participante','ACTIVO','da2e5d9b-1d12-489e-be1d-ee01e00c6bc1','2025-06-24 02:19:14','2025-06-24 02:19:14'),(7,7,5,'2025-05-25','2025-09-23','Participante','ACTIVO','f56a7d0b-6473-4884-ae05-b8a89728ff86','2025-06-24 02:19:14','2025-06-24 02:19:14'),(8,8,6,'2025-03-17','2025-07-18','Participante','ACTIVO','8bb0fab0-99f9-48e9-90ff-ab1f5bd30bad','2025-06-24 02:19:14','2025-06-24 02:19:14');
/*!40000 ALTER TABLE `asignaciones_proyectos_empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencias`
--

DROP TABLE IF EXISTS `asistencias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencias` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencias`
--

LOCK TABLES `asistencias` WRITE;
/*!40000 ALTER TABLE `asistencias` DISABLE KEYS */;
INSERT INTO `asistencias` VALUES (1,1,'2025-06-23 18:19:14','2025-06-24 00:19:14','Normal','Oficina 3','Manual','Sin novedad','ACTIVO','64dc20ce-0075-4cad-95cc-cdf3bf7dc18a','2025-06-24 02:19:14','2025-06-24 02:19:14'),(2,2,'2025-06-23 18:19:14','2025-06-23 22:19:14','Normal','Oficina 3','Manual','Sin novedad','ACTIVO','f3f5e1d5-a4a7-4f88-8469-453969097438','2025-06-24 02:19:14','2025-06-24 02:19:14'),(3,3,'2025-06-23 18:19:14','2025-06-23 22:19:14','Normal','Oficina 3','Manual','Sin novedad','ACTIVO','bd71b40e-70ea-4ba3-a541-dd92cc7c0d32','2025-06-24 02:19:14','2025-06-24 02:19:14'),(4,4,'2025-06-23 20:19:14','2025-06-23 22:19:14','Normal','Oficina 3','Manual','Sin novedad','ACTIVO','2158fc7c-d9ef-4793-953f-58dc26a48c8d','2025-06-24 02:19:14','2025-06-24 02:19:14'),(5,5,'2025-06-23 18:19:14','2025-06-24 00:19:14','Normal','Oficina 1','Manual','Sin novedad','ACTIVO','7500d10d-fd77-4316-8de9-f033cdc8f0ce','2025-06-24 02:19:14','2025-06-24 02:19:14'),(6,6,'2025-06-23 20:19:14','2025-06-23 22:19:14','Normal','Oficina 3','Manual','Sin novedad','ACTIVO','1be3532f-8709-4629-a469-993cb67050fb','2025-06-24 02:19:14','2025-06-24 02:19:14'),(7,7,'2025-06-23 20:19:14','2025-06-23 23:19:14','Normal','Oficina 2','Manual','Sin novedad','ACTIVO','31aaa6f7-faa1-49c2-9bb5-7f11f85aa2e2','2025-06-24 02:19:14','2025-06-24 02:19:14'),(8,8,'2025-06-23 20:19:14','2025-06-23 23:19:14','Normal','Oficina 2','Manual','Sin novedad','ACTIVO','f0470963-9caf-4435-9f86-49225f97ae45','2025-06-24 02:19:14','2025-06-24 02:19:14');
/*!40000 ALTER TABLE `asistencias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authentication_attempts`
--

DROP TABLE IF EXISTS `authentication_attempts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `authentication_attempts` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `attempt_time` timestamp NOT NULL DEFAULT current_timestamp(),
  `successful` tinyint(1) NOT NULL,
  `user_agent` text DEFAULT NULL,
  `failure_reason` varchar(500) DEFAULT NULL,
  `attempt_type` varchar(50) NOT NULL DEFAULT 'LOGIN',
  PRIMARY KEY (`id`),
  KEY `authentication_attempts_email_attempt_time_index` (`email`,`attempt_time`),
  KEY `authentication_attempts_ip_address_attempt_time_index` (`ip_address`,`attempt_time`),
  KEY `authentication_attempts_attempt_time_index` (`attempt_time`),
  KEY `authentication_attempts_successful_index` (`successful`),
  KEY `authentication_attempts_attempt_type_attempt_time_index` (`attempt_type`,`attempt_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authentication_attempts`
--

LOCK TABLES `authentication_attempts` WRITE;
/*!40000 ALTER TABLE `authentication_attempts` DISABLE KEYS */;
/*!40000 ALTER TABLE `authentication_attempts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias_inventario`
--

DROP TABLE IF EXISTS `categorias_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias_inventario` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias_inventario`
--

LOCK TABLES `categorias_inventario` WRITE;
/*!40000 ALTER TABLE `categorias_inventario` DISABLE KEYS */;
INSERT INTO `categorias_inventario` VALUES (1,'Equipos','Equipos electrónicos y de oficina','ACTIVO','8d28a6d7-f76d-45ab-b620-7ceb1b55c21a','2025-06-24 02:19:10','2025-06-24 02:19:10'),(2,'Herramientas','Herramientas manuales y eléctricas','ACTIVO','f5f81150-2046-4a44-96d6-a21653bb205c','2025-06-24 02:19:10','2025-06-24 02:19:10'),(3,'Materiales','Materiales de construcción y consumo','ACTIVO','bf6fe5da-e5ee-4938-90c4-7b4ae1bf2c4a','2025-06-24 02:19:10','2025-06-24 02:19:10'),(4,'Mobiliario','Muebles y enseres','ACTIVO','b565fe3c-db73-481b-af9f-c7d2372fd2c4','2025-06-24 02:19:10','2025-06-24 02:19:10'),(5,'Vehículos','Vehículos y transporte','ACTIVO','73fe0eba-d13c-4149-918b-479052766b70','2025-06-24 02:19:10','2025-06-24 02:19:10');
/*!40000 ALTER TABLE `categorias_inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleados`
--

DROP TABLE IF EXISTS `empleados`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleados` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleados`
--

LOCK TABLES `empleados` WRITE;
/*!40000 ALTER TABLE `empleados` DISABLE KEYS */;
INSERT INTO `empleados` VALUES (1,1,'Empleado 1','Apellido 1','DOC611232','1984-07-27','Dirección 1','914948799','Puesto 1','2019-06-23','ACTIVO','11feae5b-de59-4e2b-bfa8-6912dc4a01ff','2025-06-24 02:19:14','2025-06-24 02:19:14'),(2,2,'Empleado 2','Apellido 2','DOC302573','2003-01-14','Dirección 2','922043112','Puesto 2','2024-06-23','ACTIVO','593ea555-59b3-4010-a41a-c570f18b4622','2025-06-24 02:19:14','2025-06-24 02:19:14'),(3,3,'Empleado 3','Apellido 3','DOC306201','1993-09-25','Dirección 3','984609816','Puesto 3','2017-06-23','ACTIVO','b05c5d2a-6ebf-4961-9aed-ff8d7addaae8','2025-06-24 02:19:14','2025-06-24 02:19:14'),(4,4,'Empleado 4','Apellido 4','DOC307972','1985-11-26','Dirección 4','910566157','Puesto 4','2020-06-23','ACTIVO','a3940c21-701d-4d79-a1ac-1c98073eab41','2025-06-24 02:19:14','2025-06-24 02:19:14'),(5,5,'Empleado 5','Apellido 5','DOC221515','1993-07-02','Dirección 5','932425775','Puesto 5','2024-06-23','ACTIVO','93527fc2-0791-4bb5-9192-cff4586d82d2','2025-06-24 02:19:14','2025-06-24 02:19:14'),(6,6,'Empleado 6','Apellido 6','DOC885136','1997-04-12','Dirección 6','993401234','Puesto 6','2017-06-23','ACTIVO','a8a1d817-36e4-4275-8128-5c3b46c46bd4','2025-06-24 02:19:14','2025-06-24 02:19:14'),(7,7,'Empleado 7','Apellido 7','DOC762825','1991-05-31','Dirección 7','963121758','Puesto 7','2017-06-23','ACTIVO','367045d4-76eb-4988-9d4c-5146084f4620','2025-06-24 02:19:14','2025-06-24 02:19:14'),(8,8,'Empleado 8','Apellido 8','DOC666191','2004-07-14','Dirección 8','925408503','Puesto 8','2020-06-23','ACTIVO','0a519963-95c4-4e23-8c82-8b35ec00d493','2025-06-24 02:19:14','2025-06-24 02:19:14');
/*!40000 ALTER TABLE `empleados` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facturas`
--

DROP TABLE IF EXISTS `facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturas`
--

LOCK TABLES `facturas` WRITE;
/*!40000 ALTER TABLE `facturas` DISABLE KEYS */;
INSERT INTO `facturas` VALUES (1,1,1,'F00001','Factura','2025-06-10','2025-08-30',3459,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','2e37663a-0190-471f-9cf5-fb7e765126eb','2025-06-24 02:19:15','2025-06-24 02:19:15'),(2,3,5,'F00002','Factura','2025-06-15','2025-07-23',8083,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','5ca704d4-bee6-429a-b9e2-dc351bf456a7','2025-06-24 02:19:15','2025-06-24 02:19:15'),(3,3,3,'F00003','Factura','2025-05-20','2025-09-20',2962,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','0252a95a-f5d7-4d34-a6a3-7dbf2325acd7','2025-06-24 02:19:15','2025-06-24 02:19:15'),(4,6,2,'F00004','Factura','2025-04-28','2025-07-04',1724,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','adb70e75-4a1d-4731-8991-cdd40cb381a1','2025-06-24 02:19:15','2025-06-24 02:19:15'),(5,7,3,'F00005','Factura','2025-05-19','2025-09-25',1105,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','5104a6ed-8551-45b2-83a9-03b820bd0642','2025-06-24 02:19:15','2025-06-24 02:19:15'),(6,1,1,'F00006','Factura','2025-04-05','2025-09-21',2028,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','49fc6575-09d5-4302-82e1-4b920ffed1b4','2025-06-24 02:19:15','2025-06-24 02:19:15'),(7,1,3,'F00007','Factura','2025-05-30','2025-08-11',7922,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','6f6fca83-c089-4ae8-8bf0-ac683e51a402','2025-06-24 02:19:15','2025-06-24 02:19:15'),(8,2,3,'F00008','Factura','2025-05-07','2025-08-13',7272,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','dd9023a8-4ab0-44c6-80ac-f82c418aa92b','2025-06-24 02:19:15','2025-06-24 02:19:15');
/*!40000 ALTER TABLE `facturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
INSERT INTO `inventario` VALUES (1,5,'INV-0001','Item Inventario 1','Descripción del inventario 1','Marca 1','Modelo 1','SN86615',38,'unidad','2025-05-18','Bueno','ACTIVO','99dfe806-1e67-4b91-a63f-9c9069e172a3','2025-06-24 02:19:10','2025-06-24 02:19:10'),(2,4,'INV-0002','Item Inventario 2','Descripción del inventario 2','Marca 2','Modelo 2','SN66473',48,'unidad','2025-04-05','Bueno','ACTIVO','d8ca0f49-f38e-417e-9b7d-357044d8a332','2025-06-24 02:19:10','2025-06-24 02:19:10'),(3,2,'INV-0003','Item Inventario 3','Descripción del inventario 3','Marca 3','Modelo 3','SN40106',23,'unidad','2025-05-29','Bueno','ACTIVO','17c41228-a28d-4fd4-ad82-ca2ed9b5d183','2025-06-24 02:19:10','2025-06-24 02:19:10'),(4,5,'INV-0004','Item Inventario 4','Descripción del inventario 4','Marca 4','Modelo 4','SN57090',26,'unidad','2025-05-28','Bueno','ACTIVO','0c7379a8-5e12-4cb9-80ae-5ef674d5bd94','2025-06-24 02:19:10','2025-06-24 02:19:10'),(5,2,'INV-0005','Item Inventario 5','Descripción del inventario 5','Marca 5','Modelo 5','SN69247',13,'unidad','2025-06-12','Bueno','ACTIVO','050ef1d4-a6ea-4f3f-a193-e6c43adaa7be','2025-06-24 02:19:10','2025-06-24 02:19:10'),(6,1,'INV-0006','Item Inventario 6','Descripción del inventario 6','Marca 6','Modelo 6','SN32203',11,'unidad','2025-05-09','Bueno','ACTIVO','9e4e946f-a555-435e-8852-fa6f80ce48e0','2025-06-24 02:19:10','2025-06-24 02:19:10'),(7,2,'INV-0007','Item Inventario 7','Descripción del inventario 7','Marca 7','Modelo 7','SN46033',1,'unidad','2025-05-24','Bueno','ACTIVO','7de83e4b-5b40-4b38-900c-6cef9a92e123','2025-06-24 02:19:10','2025-06-24 02:19:10'),(8,5,'INV-0008','Item Inventario 8','Descripción del inventario 8','Marca 8','Modelo 8','SN50051',5,'unidad','2025-05-18','Bueno','ACTIVO','d22178d6-023a-427e-81dc-0c14a846e7d3','2025-06-24 02:19:10','2025-06-24 02:19:10');
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `migrations`
--

DROP TABLE IF EXISTS `migrations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `migrations` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `migrations`
--

LOCK TABLES `migrations` WRITE;
/*!40000 ALTER TABLE `migrations` DISABLE KEYS */;
INSERT INTO `migrations` VALUES (86,'2025_05_13_163859_create_roles_table',1),(87,'2025_05_13_203425_create_categorias_inventario_table',1),(88,'2025_05_13_203630_create_inventario_table',1),(89,'2025_05_13_204550_create_proyectos_table',1),(90,'2025_05_13_205045_create_users_table',1),(91,'2025_05_13_205050_create_empleados_table',1),(92,'2025_05_13_205856_create_asistencias_table',1),(93,'2025_05_13_210611_create_asignaciones_proyectos_empleados_table',1),(94,'2025_05_13_211505_create_movimientos_inventario_table',1),(95,'2025_05_13_235303_create_proveedores_table',1),(96,'2025_05_13_235930_create_facturas_table',1),(97,'2025_06_23_175727_create_authentication_attempts_table',1),(98,'2025_06_23_175920_create_security_audit_logs_table',1);
/*!40000 ALTER TABLE `migrations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimientos_inventario`
--

DROP TABLE IF EXISTS `movimientos_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimientos_inventario` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimientos_inventario`
--

LOCK TABLES `movimientos_inventario` WRITE;
/*!40000 ALTER TABLE `movimientos_inventario` DISABLE KEYS */;
INSERT INTO `movimientos_inventario` VALUES (1,1,7,8,1,'Salida',6,'2025-06-20 21:19:14','Movimiento de prueba','ACTIVO','4aa075a6-2cd5-4aa7-8529-1f4b3b54a9f6','2025-06-24 02:19:14','2025-06-24 02:19:14'),(2,2,6,5,4,'Salida',2,'2025-06-20 21:19:14','Movimiento de prueba','ACTIVO','0ee18ade-e9d1-4078-821c-f8e775ceb09b','2025-06-24 02:19:14','2025-06-24 02:19:14'),(3,3,7,4,2,'Salida',3,'2025-06-05 21:19:14','Movimiento de prueba','ACTIVO','763f6ef1-d908-45af-a80a-bb792c0bf5f4','2025-06-24 02:19:14','2025-06-24 02:19:14'),(4,4,5,6,4,'Salida',7,'2025-05-28 21:19:14','Movimiento de prueba','ACTIVO','83137fd2-202f-4cf7-a7e9-c29667020198','2025-06-24 02:19:14','2025-06-24 02:19:14'),(5,5,6,5,2,'Salida',7,'2025-06-14 21:19:14','Movimiento de prueba','ACTIVO','8d07234a-4e7b-4bc5-bc72-af2aa78581b4','2025-06-24 02:19:14','2025-06-24 02:19:14'),(6,6,8,5,7,'Salida',9,'2025-06-03 21:19:14','Movimiento de prueba','ACTIVO','4b8fef2c-453a-4d63-b2a1-27e26141cd93','2025-06-24 02:19:14','2025-06-24 02:19:14'),(7,7,7,8,2,'Salida',10,'2025-06-09 21:19:14','Movimiento de prueba','ACTIVO','4d7e2aaf-8eb1-4a55-9de5-991d28985927','2025-06-24 02:19:14','2025-06-24 02:19:14'),(8,8,2,5,3,'Salida',8,'2025-05-24 21:19:14','Movimiento de prueba','ACTIVO','8541404a-702e-4c7b-b622-1798c8e6ea19','2025-06-24 02:19:14','2025-06-24 02:19:14');
/*!40000 ALTER TABLE `movimientos_inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `token` varchar(255) NOT NULL,
  `user_id` bigint(20) unsigned NOT NULL,
  `expiry_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `used` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `password_reset_tokens_token_unique` (`token`),
  KEY `password_reset_tokens_token_index` (`token`),
  KEY `password_reset_tokens_user_id_index` (`user_id`),
  KEY `password_reset_tokens_expiry_date_index` (`expiry_date`),
  CONSTRAINT `password_reset_tokens_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
INSERT INTO `proveedores` VALUES (1,'RUC39167271','Proveedor 1','Dirección 1','929083128','ACTIVO','a966091c-f869-405c-87de-abc535f863fa','2025-06-24 02:19:14','2025-06-24 02:19:14'),(2,'RUC49945747','Proveedor 2','Dirección 2','919267335','ACTIVO','06c82fa7-54a0-44e6-9488-1fb0dd2d9244','2025-06-24 02:19:14','2025-06-24 02:19:14'),(3,'RUC31160077','Proveedor 3','Dirección 3','910036922','ACTIVO','2182e906-d8c9-4988-82e9-433dc2e83466','2025-06-24 02:19:15','2025-06-24 02:19:15'),(4,'RUC36853694','Proveedor 4','Dirección 4','986086669','ACTIVO','bcf10ecc-9319-4395-a4de-49454f6f2798','2025-06-24 02:19:15','2025-06-24 02:19:15'),(5,'RUC77839761','Proveedor 5','Dirección 5','932890310','ACTIVO','39eb6d42-c712-4539-bd73-956939592d7e','2025-06-24 02:19:15','2025-06-24 02:19:15'),(6,'RUC76452329','Proveedor 6','Dirección 6','981287841','ACTIVO','19e7269a-e3cb-4fd6-8c08-7da9d2cb339a','2025-06-24 02:19:15','2025-06-24 02:19:15'),(7,'RUC30123575','Proveedor 7','Dirección 7','916179788','ACTIVO','64670d55-890c-4c16-a194-97598932d17b','2025-06-24 02:19:15','2025-06-24 02:19:15');
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proyectos`
--

DROP TABLE IF EXISTS `proyectos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proyectos` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proyectos`
--

LOCK TABLES `proyectos` WRITE;
/*!40000 ALTER TABLE `proyectos` DISABLE KEYS */;
INSERT INTO `proyectos` VALUES (1,'Proyecto 1','Descripción del proyecto 1','Ubicación 1','2025-03-29','2025-08-15','En ejecución','ACTIVO','1f6de287-bf39-4614-8009-ac99cdbe4a91','2025-06-24 02:19:10','2025-06-24 02:19:10'),(2,'Proyecto 2','Descripción del proyecto 2','Ubicación 2','2025-03-24','2025-08-12','En ejecución','ACTIVO','99f68d17-8945-47a0-988b-d9832ae30fef','2025-06-24 02:19:10','2025-06-24 02:19:10'),(3,'Proyecto 3','Descripción del proyecto 3','Ubicación 3','2025-03-21','2025-07-27','En ejecución','ACTIVO','fbdc28f1-b31c-4c84-9327-9d47ff19f481','2025-06-24 02:19:10','2025-06-24 02:19:10'),(4,'Proyecto 4','Descripción del proyecto 4','Ubicación 4','2025-05-29','2025-07-21','En ejecución','ACTIVO','cd0e9670-c3af-49a5-a131-e3215a0aeb6f','2025-06-24 02:19:10','2025-06-24 02:19:10'),(5,'Proyecto 5','Descripción del proyecto 5','Ubicación 5','2025-05-31','2025-07-14','En ejecución','ACTIVO','d5c5592c-9396-402b-82d1-d97b1ed5d8d5','2025-06-24 02:19:10','2025-06-24 02:19:10'),(6,'Proyecto 6','Descripción del proyecto 6','Ubicación 6','2025-04-18','2025-08-15','En ejecución','ACTIVO','5954c5d4-9a03-4d0f-a4dd-d66b5acfafeb','2025-06-24 02:19:10','2025-06-24 02:19:10'),(7,'Proyecto 7','Descripción del proyecto 7','Ubicación 7','2025-03-29','2025-09-01','En ejecución','ACTIVO','60ec448b-2bdd-4317-914a-7bfa879f2169','2025-06-24 02:19:10','2025-06-24 02:19:10');
/*!40000 ALTER TABLE `proyectos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMINISTRADOR','Acceso total al sistema, administra todos los recursos','ACTIVO','bf3d32c9-04eb-4d0b-8ccd-e7d59663c9e6','2025-06-24 02:19:10','2025-06-24 02:19:10'),(2,'GERENTE','Gestiona proyectos, empleados y recursos del área','ACTIVO','edd64c62-ed7d-49bc-add4-0d1248abb691','2025-06-24 02:19:10','2025-06-24 02:19:10'),(3,'EMPLEADO','Usuario operativo, acceso limitado a funciones específicas','ACTIVO','72e509a5-2252-4d08-84b4-091d0e2ad165','2025-06-24 02:19:10','2025-06-24 02:19:10'),(4,'USER','Usuario básico con permisos mínimos de lectura','ACTIVO','afe1f802-120b-4a0e-aac1-637e07d1d5a3','2025-06-24 02:19:10','2025-06-24 02:19:10');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `security_audit_logs`
--

DROP TABLE IF EXISTS `security_audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `security_audit_logs` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `event_type` varchar(50) NOT NULL,
  `user_id` bigint(20) unsigned DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `ip_address` varchar(45) NOT NULL,
  `user_agent` text DEFAULT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp(),
  `details` varchar(1000) DEFAULT NULL,
  `severity` enum('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `security_audit_logs_event_type_index` (`event_type`),
  KEY `security_audit_logs_user_id_index` (`user_id`),
  KEY `security_audit_logs_email_index` (`email`),
  KEY `security_audit_logs_timestamp_index` (`timestamp`),
  KEY `security_audit_logs_severity_index` (`severity`),
  KEY `security_audit_logs_email_timestamp_index` (`email`,`timestamp`),
  CONSTRAINT `security_audit_logs_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=140 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security_audit_logs`
--

LOCK TABLES `security_audit_logs` WRITE;
/*!40000 ALTER TABLE `security_audit_logs` DISABLE KEYS */;
INSERT INTO `security_audit_logs` VALUES (1,'TOKEN_REFRESH_OUT_OF_WINDOW',NULL,'unknown','127.0.0.1','API','2025-06-23 21:49:28','Token fuera de ventana de renovación','MEDIUM'),(2,'TOKEN_REFRESH_OUT_OF_WINDOW',NULL,'unknown','192.168.1.100','API','2025-06-23 21:53:46','Token fuera de ventana de renovación','MEDIUM'),(3,'TOKEN_REFRESH_OUT_OF_WINDOW',NULL,'unknown','127.0.0.1','API','2025-06-23 21:54:02','Token fuera de ventana de renovación','MEDIUM'),(4,'DATA_ACCESS',NULL,'','','','2025-06-24 00:20:57','Security system operational check - Scheduled tasks running properly','LOW'),(5,'DATA_ACCESS',NULL,'','','','2025-06-24 00:22:15','Security system operational check - Scheduled tasks running properly','LOW'),(6,'DATA_ACCESS',NULL,'','','','2025-06-24 00:22:26','Security system operational check - Scheduled tasks running properly','LOW'),(7,'DATA_ACCESS',NULL,'','','','2025-06-24 00:26:34','Security system operational check - Scheduled tasks running properly','LOW'),(8,'DATA_ACCESS',NULL,'','','','2025-06-24 00:36:39','Security system operational check - Scheduled tasks running properly','LOW'),(9,'DATA_ACCESS',NULL,'','','','2025-06-24 00:37:34','Security system operational check - Scheduled tasks running properly','LOW'),(10,'DATA_ACCESS',NULL,'','','','2025-06-24 00:42:29','Security system operational check - Scheduled tasks running properly','LOW'),(11,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:45:53','POST /conaveg/api/auth/login - Status: 401 - Duration: 768ms','MEDIUM'),(12,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:45:53','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(13,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:45:57','POST /conaveg/api/auth/login - Status: 401 - Duration: 652ms','MEDIUM'),(14,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:45:57','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(15,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:45:59','POST /conaveg/api/auth/login - Status: 401 - Duration: 490ms','MEDIUM'),(16,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:45:59','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(17,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:01','POST /conaveg/api/auth/login - Status: 401 - Duration: 504ms','MEDIUM'),(18,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:01','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(19,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:02','POST /conaveg/api/auth/login - Status: 401 - Duration: 506ms','MEDIUM'),(20,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:02','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(21,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:03','POST /conaveg/api/auth/login - Status: 401 - Duration: 691ms','MEDIUM'),(22,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:03','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(23,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:04','POST /conaveg/api/auth/login - Status: 401 - Duration: 479ms','MEDIUM'),(24,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:04','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(25,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:07','POST /conaveg/api/auth/login - Status: 401 - Duration: 505ms','MEDIUM'),(26,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:07','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(27,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:08','POST /conaveg/api/auth/login - Status: 401 - Duration: 473ms','MEDIUM'),(28,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:08','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(29,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:09','POST /conaveg/api/auth/login - Status: 401 - Duration: 499ms','MEDIUM'),(30,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:09','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(31,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:11','POST /conaveg/api/auth/login - Status: 401 - Duration: 475ms','MEDIUM'),(32,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:11','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(33,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:12','POST /conaveg/api/auth/login - Status: 401 - Duration: 473ms','MEDIUM'),(34,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:12','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(35,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:13','POST /conaveg/api/auth/login - Status: 401 - Duration: 612ms','MEDIUM'),(36,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:13','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(37,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:14','POST /conaveg/api/auth/login - Status: 401 - Duration: 532ms','MEDIUM'),(38,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:14','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(39,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:15','POST /conaveg/api/auth/login - Status: 401 - Duration: 517ms','MEDIUM'),(40,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:15','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(41,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:16','POST /conaveg/api/auth/login - Status: 401 - Duration: 479ms','MEDIUM'),(42,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:16','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(43,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:18','POST /conaveg/api/auth/login - Status: 401 - Duration: 532ms','MEDIUM'),(44,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:18','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(45,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:19','POST /conaveg/api/auth/login - Status: 401 - Duration: 469ms','MEDIUM'),(46,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:19','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(47,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:20','POST /conaveg/api/auth/login - Status: 401 - Duration: 492ms','MEDIUM'),(48,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:20','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(49,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:21','POST /conaveg/api/auth/login - Status: 401 - Duration: 498ms','MEDIUM'),(50,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:21','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(51,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:22','POST /conaveg/api/auth/login - Status: 401 - Duration: 482ms','MEDIUM'),(52,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:22','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(53,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:23','POST /conaveg/api/auth/login - Status: 401 - Duration: 568ms','MEDIUM'),(54,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:23','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(55,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:24','POST /conaveg/api/auth/login - Status: 401 - Duration: 474ms','MEDIUM'),(56,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:24','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(57,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:25','POST /conaveg/api/auth/login - Status: 401 - Duration: 502ms','MEDIUM'),(58,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:25','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(59,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:26','POST /conaveg/api/auth/login - Status: 401 - Duration: 474ms','MEDIUM'),(60,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:26','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(61,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:28','POST /conaveg/api/auth/login - Status: 401 - Duration: 549ms','MEDIUM'),(62,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:28','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(63,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:29','POST /conaveg/api/auth/login - Status: 401 - Duration: 494ms','MEDIUM'),(64,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:29','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(65,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:30','POST /conaveg/api/auth/login - Status: 401 - Duration: 488ms','MEDIUM'),(66,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:30','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(67,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:31','POST /conaveg/api/auth/login - Status: 401 - Duration: 490ms','MEDIUM'),(68,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:31','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(69,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:32','POST /conaveg/api/auth/login - Status: 401 - Duration: 495ms','MEDIUM'),(70,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:32','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(71,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:33','POST /conaveg/api/auth/login - Status: 401 - Duration: 539ms','MEDIUM'),(72,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:33','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(73,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:34','POST /conaveg/api/auth/login - Status: 401 - Duration: 560ms','MEDIUM'),(74,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:34','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(75,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:35','POST /conaveg/api/auth/login - Status: 401 - Duration: 507ms','MEDIUM'),(76,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:35','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(77,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:37','POST /conaveg/api/auth/login - Status: 401 - Duration: 472ms','MEDIUM'),(78,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:37','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(79,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:38','POST /conaveg/api/auth/login - Status: 401 - Duration: 611ms','MEDIUM'),(80,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:38','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(81,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:39','POST /conaveg/api/auth/login - Status: 401 - Duration: 478ms','MEDIUM'),(82,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:39','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(83,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:40','POST /conaveg/api/auth/login - Status: 401 - Duration: 484ms','MEDIUM'),(84,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:40','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(85,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:41','POST /conaveg/api/auth/login - Status: 401 - Duration: 485ms','MEDIUM'),(86,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:41','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(87,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:42','POST /conaveg/api/auth/login - Status: 401 - Duration: 476ms','MEDIUM'),(88,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:42','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(89,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:43','POST /conaveg/api/auth/login - Status: 401 - Duration: 500ms','MEDIUM'),(90,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:43','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(91,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:45','POST /conaveg/api/auth/login - Status: 401 - Duration: 467ms','MEDIUM'),(92,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:45','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(93,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:46','POST /conaveg/api/auth/login - Status: 401 - Duration: 488ms','MEDIUM'),(94,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:46','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(95,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:47','POST /conaveg/api/auth/login - Status: 401 - Duration: 486ms','MEDIUM'),(96,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:47','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(97,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:48','POST /conaveg/api/auth/login - Status: 401 - Duration: 560ms','MEDIUM'),(98,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:48','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(99,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:49','POST /conaveg/api/auth/login - Status: 401 - Duration: 530ms','MEDIUM'),(100,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:49','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(101,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:50','POST /conaveg/api/auth/login - Status: 401 - Duration: 485ms','MEDIUM'),(102,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:50','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(103,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:52','POST /conaveg/api/auth/login - Status: 401 - Duration: 510ms','MEDIUM'),(104,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:52','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(105,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:53','POST /conaveg/api/auth/login - Status: 401 - Duration: 547ms','MEDIUM'),(106,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:53','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(107,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:54','POST /conaveg/api/auth/login - Status: 401 - Duration: 479ms','MEDIUM'),(108,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:54','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(109,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:55','POST /conaveg/api/auth/login - Status: 401 - Duration: 483ms','MEDIUM'),(110,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:55','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(111,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:56','POST /conaveg/api/auth/login - Status: 401 - Duration: 480ms','MEDIUM'),(112,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:56','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(113,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:57','POST /conaveg/api/auth/login - Status: 401 - Duration: 584ms','MEDIUM'),(114,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:57','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(115,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:59','POST /conaveg/api/auth/login - Status: 401 - Duration: 489ms','MEDIUM'),(116,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:46:59','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(117,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:00','POST /conaveg/api/auth/login - Status: 401 - Duration: 494ms','MEDIUM'),(118,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:00','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(119,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:01','POST /conaveg/api/auth/login - Status: 401 - Duration: 470ms','MEDIUM'),(120,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:01','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(121,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:02','POST /conaveg/api/auth/login - Status: 401 - Duration: 475ms','MEDIUM'),(122,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:02','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(123,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:03','POST /conaveg/api/auth/login - Status: 401 - Duration: 525ms','MEDIUM'),(124,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:03','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(125,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:04','POST /conaveg/api/auth/login - Status: 401 - Duration: 486ms','MEDIUM'),(126,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:04','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(127,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:05','POST /conaveg/api/auth/login - Status: 401 - Duration: 488ms','MEDIUM'),(128,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:05','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(129,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:06','POST /conaveg/api/auth/login - Status: 401 - Duration: 463ms','MEDIUM'),(130,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:07','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(131,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:08','POST /conaveg/api/auth/login - Status: 401 - Duration: 605ms','MEDIUM'),(132,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:08','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(133,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:09','POST /conaveg/api/auth/login - Status: 401 - Duration: 488ms','MEDIUM'),(134,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:09','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(135,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:11','POST /conaveg/api/auth/login - Status: 401 - Duration: 460ms','MEDIUM'),(136,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:11','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(137,'LOGIN_FAILED',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:12','POST /conaveg/api/auth/login - Status: 401 - Duration: 507ms','MEDIUM'),(138,'UNAUTHORIZED_ACCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36 Edg/137.0.0.0','2025-06-24 00:47:12','Authentication endpoint error: POST /conaveg/api/auth/login returned 401','MEDIUM'),(139,'DATA_ACCESS',NULL,'','','','2025-06-24 00:47:47','Security system operational check - Scheduled tasks running properly','LOW');
/*!40000 ALTER TABLE `security_audit_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,1,'admin','admin@admin.com','2025-06-24 02:19:10','$2y$12$e4rSrUPfJvDIQYeYj199EO48tL9JPLKJK0rWHM3zqImfzrLsir8QW','dhkl2Q8LAm','ACTIVO','78e7781f-48c5-4d99-9225-f5175c91af1f','2025-06-24 02:19:11','2025-06-24 02:19:11'),(2,1,'usuario1','usuario1@mail.com','2025-06-24 02:19:11','$2y$12$apU3jY3LFlGX0iNMmqYwxuLMrI2SMhgL/jet/vt0EP2FJsmRlVlCq','a2NfhgoSfu','ACTIVO','71981df4-83f8-4bd8-a510-1b23883109dd','2025-06-24 02:19:11','2025-06-24 02:19:11'),(3,2,'usuario2','usuario2@mail.com','2025-06-24 02:19:11','$2y$12$tVE8ujQyf5LOHdNE5iaO7uAmlDULoISZe5pkS2byYXGWWfLKnW.Gm','piB7abMQE4','ACTIVO','368794f3-b1b4-4e45-bb37-a4f70c7b4496','2025-06-24 02:19:12','2025-06-24 02:19:12'),(4,2,'usuario3','usuario3@mail.com','2025-06-24 02:19:12','$2y$12$lE1nu7gE/PMl3uKLa7eQbe7qG7y6.glbaq6R8BWQ8Ynlpkt3SCQoO','IJHgf6djGT','ACTIVO','bb387e12-004c-4c90-a6de-1f2a2e52c6dc','2025-06-24 02:19:12','2025-06-24 02:19:12'),(5,2,'usuario4','usuario4@mail.com','2025-06-24 02:19:12','$2y$12$6cIHM/M8CC.6De54lgxlN.gM5XlETfb0yzBZYaOo/zSMQGr17y9FS','4b1krHLrRL','ACTIVO','57702b47-bd94-4b58-ab39-562c9aabcade','2025-06-24 02:19:13','2025-06-24 02:19:13'),(6,4,'usuario5','usuario5@mail.com','2025-06-24 02:19:13','$2y$12$t6/vMJdvDPegO76s7iCHTuqgs4PvqUjtHn/4TtjQABT4k16/Xok42','SE9NLY2j8B','ACTIVO','f02603c2-c9b9-4501-bde8-7366b607cc79','2025-06-24 02:19:13','2025-06-24 02:19:13'),(7,4,'usuario6','usuario6@mail.com','2025-06-24 02:19:13','$2y$12$IWSl2Qy9s.EIimAPblUJS.Xn.f7Wlu3rhzjGtapU5/xKgakzCZ/UW','GJhahME4lp','ACTIVO','04dde39f-470e-4a15-abbd-165f152aa943','2025-06-24 02:19:13','2025-06-24 02:19:13'),(8,4,'usuario7','usuario7@mail.com','2025-06-24 02:19:13','$2y$12$eb0IvkNkwY863iBrrckisOjUCpUhCFdbMJB/0DBxHXeBQ.yohc186','IJDcOt570q','ACTIVO','bdc5175b-338e-48dd-97a8-7d84d8fddafd','2025-06-24 02:19:14','2025-06-24 02:19:14'),(9,4,'usuario8','usuario8@mail.com','2025-06-24 02:19:14','$2y$12$erpT5X.kSZINveMaVdaBwerg0PET02UHAsaizrdLNwnAsb4aDNBjy','AGEblg0fmo','ACTIVO','05979ea2-188e-460b-b467-0b9b3871921d','2025-06-24 02:19:14','2025-06-24 02:19:14');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'conaveg_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-26 17:16:02
