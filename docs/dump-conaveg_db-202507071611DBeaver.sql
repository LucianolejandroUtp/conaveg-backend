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
INSERT INTO `asignaciones_proyectos_empleados` VALUES (1,1,1,'2025-05-09','2025-07-08','Participante','ACTIVO','2afd938c-da98-446b-935a-f2e8a0e184c6','2025-07-07 23:20:15','2025-07-07 23:20:15'),(2,2,1,'2025-05-10','2025-10-14','Participante','ACTIVO','5e0ecf45-472c-4ce0-9a3a-f826af0f9ea2','2025-07-07 23:20:15','2025-07-07 23:20:15'),(3,3,4,'2025-07-02','2025-09-04','Participante','ACTIVO','fa451741-8609-473f-a30a-42c324b1b412','2025-07-07 23:20:15','2025-07-07 23:20:15'),(4,4,1,'2025-06-18','2025-07-09','Participante','ACTIVO','154e5c06-a9a0-4232-b4c6-9b1d9d55e9cd','2025-07-07 23:20:15','2025-07-07 23:20:15'),(5,5,7,'2025-05-22','2025-09-05','Participante','ACTIVO','e622b36a-dad1-46fe-ba19-ca6abc69d0e1','2025-07-07 23:20:15','2025-07-07 23:20:15'),(6,6,2,'2025-06-17','2025-09-01','Participante','ACTIVO','d1f78858-6e85-4a97-b769-5c4bed2c8353','2025-07-07 23:20:15','2025-07-07 23:20:15'),(7,7,2,'2025-05-05','2025-07-21','Participante','ACTIVO','a813f9f0-710d-4dbe-bfa9-513af58771d6','2025-07-07 23:20:15','2025-07-07 23:20:15'),(8,8,2,'2025-05-20','2025-07-17','Participante','ACTIVO','07d85186-f0e3-429f-ab00-42383c40f3ab','2025-07-07 23:20:15','2025-07-07 23:20:15');
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
  `estado_asistencia` enum('PUNTUAL','TARDE','AUSENTE','JUSTIFICADO','OTRO') DEFAULT 'PUNTUAL',
  `latitud` decimal(10,7) DEFAULT NULL,
  `longitud` decimal(10,7) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO','ELIMINADO') DEFAULT 'ACTIVO',
  `unique_id` char(36) DEFAULT uuid(),
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `asistencias_unique_id_unique` (`unique_id`),
  KEY `asistencias_empleados_id_index` (`empleados_id`),
  CONSTRAINT `asistencias_empleados_id_foreign` FOREIGN KEY (`empleados_id`) REFERENCES `empleados` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencias`
--

LOCK TABLES `asistencias` WRITE;
/*!40000 ALTER TABLE `asistencias` DISABLE KEYS */;
INSERT INTO `asistencias` VALUES (1,1,'2025-07-07 15:20:15','2025-07-07 20:20:15','Normal','Oficina 2','Manual','Sin novedad','JUSTIFICADO',-11.2861760,-76.9114190,'ACTIVO','97c6def5-ace8-4378-b138-1adedf99dbff','2025-07-07 23:20:15','2025-07-07 23:20:15'),(2,2,'2025-07-07 16:20:15','2025-07-07 19:20:15','Normal','Oficina 3','Manual','Sin novedad','AUSENTE',-11.1033200,-76.5011360,'ACTIVO','c91dfa1e-96a8-4608-85e4-25f19e10485a','2025-07-07 23:20:15','2025-07-07 23:20:15'),(3,3,'2025-07-07 15:20:15','2025-07-07 19:20:15','Normal','Oficina 1','Manual','Sin novedad','OTRO',-11.1523130,-76.3384070,'ACTIVO','f2535131-b405-4ae7-b15d-9651ca311e28','2025-07-07 23:20:15','2025-07-07 23:20:15'),(4,4,'2025-07-07 17:20:15','2025-07-07 20:20:15','Normal','Oficina 2','Manual','Sin novedad','PUNTUAL',-11.6681650,-76.6635390,'ACTIVO','d449f43b-4310-4ecb-a3bd-bc48b82db851','2025-07-07 23:20:15','2025-07-07 23:20:15'),(5,5,'2025-07-07 17:20:15','2025-07-07 19:20:15','Normal','Oficina 3','Manual','Sin novedad','PUNTUAL',-11.8061140,-76.0434410,'ACTIVO','bc626a1c-9b1d-4c7c-b2c2-14e7094e5b35','2025-07-07 23:20:15','2025-07-07 23:20:15'),(6,6,'2025-07-07 17:20:15','2025-07-07 19:20:15','Normal','Oficina 3','Manual','Sin novedad','OTRO',-11.5078780,-76.9997480,'ACTIVO','ca51c3cb-f86a-4fc4-b5ee-6399cac5dcca','2025-07-07 23:20:15','2025-07-07 23:20:15'),(7,7,'2025-07-07 15:20:15','2025-07-07 21:20:15','Normal','Oficina 2','Manual','Sin novedad','PUNTUAL',-11.3017810,-76.4955310,'ACTIVO','b33b27d7-3375-46e3-879b-2300b49df1b2','2025-07-07 23:20:15','2025-07-07 23:20:15'),(8,8,'2025-07-07 15:20:15','2025-07-07 21:20:15','Normal','Oficina 3','Manual','Sin novedad','TARDE',-11.5760580,-76.3964980,'ACTIVO','e20ad673-7f4f-4437-94bf-cbd1873186e6','2025-07-07 23:20:15','2025-07-07 23:20:15'),(9,8,'2025-07-07 15:16:26',NULL,'ENTRADA','puerta project 006','auto','TARDANZA: 829 minutos (llegada: 15:16, horario: 01:22) | nn','PUNTUAL',0.1000000,0.1000000,'ACTIVO','4794eb80-5b6f-11f0-836a-54e1ad0af6d3','2025-07-07 20:16:26','2025-07-07 20:16:26'),(10,8,'2025-07-07 15:34:46',NULL,'ENTRADA','puerta project 006','auto','TARDANZA: 847 minutos (llegada: 15:34, horario: 01:22) | nn','PUNTUAL',-16.3950028,-71.5213995,'ACTIVO','d6fe2e3f-5b71-11f0-836a-54e1ad0af6d3','2025-07-07 20:34:46','2025-07-07 20:34:46'),(11,8,'2025-07-07 15:49:05',NULL,'ENTRADA','puerta project 006','auto','nn','TARDE',-16.3950028,-71.5213995,'ACTIVO','d7251eb4-5b73-11f0-836a-54e1ad0af6d3','2025-07-07 20:49:05','2025-07-07 20:49:05'),(12,8,'2025-07-07 15:50:25',NULL,'ENTRADA','puerta project 006','auto','nn','PUNTUAL',-16.3950028,-71.5213995,'ACTIVO','06cf3e88-5b74-11f0-836a-54e1ad0af6d3','2025-07-07 20:50:25','2025-07-07 20:50:25'),(13,8,'2025-07-07 15:57:13',NULL,'ENTRADA','puerta project 006','auto','nn - Tardanza: 472 minutos y 13 segundos','TARDE',-16.3950028,-71.5213995,'ACTIVO','fa327d63-5b74-11f0-836a-54e1ad0af6d3','2025-07-07 20:57:13','2025-07-07 20:57:13'),(14,8,'2025-07-07 15:57:43',NULL,'ENTRADA','puerta project 006','auto','Tardanza: 472 minutos y 43 segundos','TARDE',-16.3950028,-71.5213995,'ACTIVO','0bd39672-5b75-11f0-836a-54e1ad0af6d3','2025-07-07 20:57:43','2025-07-07 20:57:43'),(15,8,'2025-07-07 16:02:27',NULL,'ENTRADA','puerta project 006','auto','477 minutos y 27 segundos','TARDE',-16.3950028,-71.5213995,'ACTIVO','b5071e19-5b75-11f0-836a-54e1ad0af6d3','2025-07-07 21:02:27','2025-07-07 21:02:27'),(16,8,'2025-07-07 16:03:46',NULL,'ENTRADA','puerta project 006','auto','','PUNTUAL',-16.3950028,-71.5213995,'ACTIVO','e4340cd9-5b75-11f0-836a-54e1ad0af6d3','2025-07-07 21:03:46','2025-07-07 21:03:46');
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
INSERT INTO `categorias_inventario` VALUES (1,'Equipos','Equipos electrónicos y de oficina','ACTIVO','83730e77-e4fa-488f-9534-f828fa93a5cc','2025-07-07 23:20:10','2025-07-07 23:20:10'),(2,'Herramientas','Herramientas manuales y eléctricas','ACTIVO','df2a88a3-f28b-4ca0-a413-8d1a5a5e9dbf','2025-07-07 23:20:10','2025-07-07 23:20:10'),(3,'Materiales','Materiales de construcción y consumo','ACTIVO','a5a83a24-c95f-4a0d-b55f-1c9017127a4a','2025-07-07 23:20:10','2025-07-07 23:20:10'),(4,'Mobiliario','Muebles y enseres','ACTIVO','1fd5d6e3-f4dc-4be8-8271-60bc116014c0','2025-07-07 23:20:10','2025-07-07 23:20:10'),(5,'Vehículos','Vehículos y transporte','ACTIVO','8eae04ad-1e01-40dd-a702-b596a04f0ffa','2025-07-07 23:20:10','2025-07-07 23:20:10');
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
INSERT INTO `empleados` VALUES (1,1,'Empleado 1','Apellido 1','DOC524458','1998-10-18','Dirección 1','987037170','Puesto 1','2020-07-07','ACTIVO','99f1f4b2-c525-4527-bbe8-7847ae594998','2025-07-07 23:20:15','2025-07-07 23:20:15'),(2,2,'Empleado 2','Apellido 2','DOC197191','1991-08-25','Dirección 2','962983287','Puesto 2','2022-07-07','ACTIVO','86ae1e44-b28a-4b9d-a565-d798927f5416','2025-07-07 23:20:15','2025-07-07 23:20:15'),(3,3,'Empleado 3','Apellido 3','DOC165871','2003-11-13','Dirección 3','946958974','Puesto 3','2015-07-07','ACTIVO','0b53bc6d-4070-4cc2-8ee0-018082681d4c','2025-07-07 23:20:15','2025-07-07 23:20:15'),(4,4,'Empleado 4','Apellido 4','DOC853021','1987-03-12','Dirección 4','925437792','Puesto 4','2023-07-07','ACTIVO','708c5ce7-64e0-477c-9360-5f622ee17e40','2025-07-07 23:20:15','2025-07-07 23:20:15'),(5,5,'Empleado 5','Apellido 5','DOC870414','1994-12-10','Dirección 5','932561610','Puesto 5','2023-07-07','ACTIVO','d7ed1956-1a3b-4e1e-a651-45b3fa325244','2025-07-07 23:20:15','2025-07-07 23:20:15'),(6,6,'Empleado 6','Apellido 6','DOC897117','2004-03-10','Dirección 6','945966364','Puesto 6','2019-07-07','ACTIVO','73935747-1a08-4572-9bf2-c943aff8e46d','2025-07-07 23:20:15','2025-07-07 23:20:15'),(7,7,'Empleado 7','Apellido 7','DOC185515','1999-01-18','Dirección 7','921860092','Puesto 7','2015-07-07','ACTIVO','c09237af-6e33-4f64-bff6-554c81a05662','2025-07-07 23:20:15','2025-07-07 23:20:15'),(8,8,'Empleado 8','Apellido 8','DOC933137','2002-04-07','Dirección 8','982444259','Puesto 8','2018-07-07','ACTIVO','13bd435a-c3e6-4d4d-a096-3d0ebb9c9a35','2025-07-07 23:20:15','2025-07-07 23:20:15');
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
INSERT INTO `facturas` VALUES (1,4,2,'F00001','Factura','2025-06-17','2025-09-21',7245,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','d34af021-8a78-4f53-9a91-5a5d225f338f','2025-07-07 23:20:15','2025-07-07 23:20:15'),(2,6,8,'F00002','Factura','2025-06-02','2025-07-21',5766,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','49e76496-3bb8-49e6-8dd2-21324253967d','2025-07-07 23:20:15','2025-07-07 23:20:15'),(3,4,7,'F00003','Factura','2025-04-10','2025-07-25',3420,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','77171989-11a2-489c-bb90-00a4517225d6','2025-07-07 23:20:15','2025-07-07 23:20:15'),(4,3,8,'F00004','Factura','2025-06-22','2025-07-31',9403,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','1dab8dee-92e3-4f84-9490-f3120685654a','2025-07-07 23:20:15','2025-07-07 23:20:15'),(5,7,1,'F00005','Factura','2025-05-22','2025-09-03',7957,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','bad8101b-819b-4909-82f1-bcf58d611a2b','2025-07-07 23:20:15','2025-07-07 23:20:15'),(6,7,7,'F00006','Factura','2025-07-01','2025-10-12',1736,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','ba4cd888-d001-41ef-ace4-5f8378958ae4','2025-07-07 23:20:15','2025-07-07 23:20:15'),(7,7,8,'F00007','Factura','2025-04-13','2025-08-26',6924,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','590bff7e-394d-447d-ae97-ee0af985a0ab','2025-07-07 23:20:15','2025-07-07 23:20:15'),(8,4,2,'F00008','Factura','2025-06-01','2025-10-01',2928,'PEN','Factura de prueba',NULL,NULL,'Pendiente','ACTIVO','447fd5db-a931-4e55-addf-5ca578d25145','2025-07-07 23:20:15','2025-07-07 23:20:15');
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
INSERT INTO `inventario` VALUES (1,3,'INV-0001','Item Inventario 1','Descripción del inventario 1','Marca 1','Modelo 1','SN37077',16,'unidad','2025-06-24','Bueno','ACTIVO','0119086d-35df-41b9-8920-4271dd659d9c','2025-07-07 23:20:10','2025-07-07 23:20:10'),(2,1,'INV-0002','Item Inventario 2','Descripción del inventario 2','Marca 2','Modelo 2','SN19990',16,'unidad','2025-04-28','Bueno','ACTIVO','9c524175-f85c-4ad6-b7b8-0651d038881a','2025-07-07 23:20:10','2025-07-07 23:20:10'),(3,5,'INV-0003','Item Inventario 3','Descripción del inventario 3','Marca 3','Modelo 3','SN52817',27,'unidad','2025-05-31','Bueno','ACTIVO','f5b6bfcb-b1c4-41f2-8c5a-dd60d8ab7003','2025-07-07 23:20:10','2025-07-07 23:20:10'),(4,3,'INV-0004','Item Inventario 4','Descripción del inventario 4','Marca 4','Modelo 4','SN67294',8,'unidad','2025-04-07','Bueno','ACTIVO','a6cbb1a8-de8b-448c-9ca2-44423fcce26a','2025-07-07 23:20:10','2025-07-07 23:20:10'),(5,5,'INV-0005','Item Inventario 5','Descripción del inventario 5','Marca 5','Modelo 5','SN41838',14,'unidad','2025-04-09','Bueno','ACTIVO','1965e872-92b2-43fe-ab1a-acaf2b2689dd','2025-07-07 23:20:10','2025-07-07 23:20:10'),(6,4,'INV-0006','Item Inventario 6','Descripción del inventario 6','Marca 6','Modelo 6','SN37802',5,'unidad','2025-04-12','Bueno','ACTIVO','82504b3a-a7cd-4ced-ad29-fc004966bf34','2025-07-07 23:20:10','2025-07-07 23:20:10'),(7,1,'INV-0007','Item Inventario 7','Descripción del inventario 7','Marca 7','Modelo 7','SN81664',23,'unidad','2025-06-09','Bueno','ACTIVO','19f3ad95-0aee-4721-88c9-e40c2379e9c6','2025-07-07 23:20:10','2025-07-07 23:20:10'),(8,1,'INV-0008','Item Inventario 8','Descripción del inventario 8','Marca 8','Modelo 8','SN23835',6,'unidad','2025-04-11','Bueno','ACTIVO','b0906bd3-1dbe-4438-a64b-10bc2e8a4a94','2025-07-07 23:20:10','2025-07-07 23:20:10');
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
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `migrations`
--

LOCK TABLES `migrations` WRITE;
/*!40000 ALTER TABLE `migrations` DISABLE KEYS */;
INSERT INTO `migrations` VALUES (99,'2025_05_13_163859_create_roles_table',1),(100,'2025_05_13_203425_create_categorias_inventario_table',1),(101,'2025_05_13_203630_create_inventario_table',1),(102,'2025_05_13_204550_create_proyectos_table',1),(103,'2025_05_13_205045_create_users_table',1),(104,'2025_05_13_205050_create_empleados_table',1),(105,'2025_05_13_205856_create_asistencias_table',1),(106,'2025_05_13_210611_create_asignaciones_proyectos_empleados_table',1),(107,'2025_05_13_211505_create_movimientos_inventario_table',1),(108,'2025_05_13_235303_create_proveedores_table',1),(109,'2025_05_13_235930_create_facturas_table',1),(110,'2025_06_23_175727_create_authentication_attempts_table',1),(111,'2025_06_23_175920_create_security_audit_logs_table',1);
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
INSERT INTO `movimientos_inventario` VALUES (1,1,3,8,4,'Salida',6,'2025-06-12 18:20:15','Movimiento de prueba','ACTIVO','e9723bb1-6b53-4222-80ee-92a87e1b37a3','2025-07-07 23:20:15','2025-07-07 23:20:15'),(2,2,4,7,7,'Salida',9,'2025-06-07 18:20:15','Movimiento de prueba','ACTIVO','f6b2f484-a08c-49fc-b262-d0aba84f02c5','2025-07-07 23:20:15','2025-07-07 23:20:15'),(3,3,1,1,7,'Salida',6,'2025-06-29 18:20:15','Movimiento de prueba','ACTIVO','b656052a-1587-4577-9d7d-43943abe2b78','2025-07-07 23:20:15','2025-07-07 23:20:15'),(4,4,3,4,2,'Salida',7,'2025-06-18 18:20:15','Movimiento de prueba','ACTIVO','42bf8ed6-f6ea-4f86-ab4b-0b0aa11d257e','2025-07-07 23:20:15','2025-07-07 23:20:15'),(5,5,7,7,4,'Salida',6,'2025-06-14 18:20:15','Movimiento de prueba','ACTIVO','b0fa979a-d482-4042-9150-17ccfa32d3b8','2025-07-07 23:20:15','2025-07-07 23:20:15'),(6,6,6,8,6,'Salida',1,'2025-07-05 18:20:15','Movimiento de prueba','ACTIVO','187fcb44-2686-4b7d-863a-07235e7271fd','2025-07-07 23:20:15','2025-07-07 23:20:15'),(7,7,7,6,5,'Salida',7,'2025-06-28 18:20:15','Movimiento de prueba','ACTIVO','adf39160-2884-4032-a4a2-c3ff294cb539','2025-07-07 23:20:15','2025-07-07 23:20:15'),(8,8,4,3,4,'Salida',4,'2025-07-06 18:20:15','Movimiento de prueba','ACTIVO','9ff88b21-6c2b-4979-b1aa-8f7809aa351d','2025-07-07 23:20:15','2025-07-07 23:20:15');
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
INSERT INTO `proveedores` VALUES (1,'RUC32242878','Proveedor 1','Dirección 1','924199220','ACTIVO','2fe13019-cbae-41df-9b90-10bf8d713aac','2025-07-07 23:20:15','2025-07-07 23:20:15'),(2,'RUC18332423','Proveedor 2','Dirección 2','938290879','ACTIVO','f9884788-2671-42cd-aacc-f6cac99b19bf','2025-07-07 23:20:15','2025-07-07 23:20:15'),(3,'RUC56511919','Proveedor 3','Dirección 3','936658552','ACTIVO','9d04b7cb-2dc5-40d1-94d5-b84c63623e6e','2025-07-07 23:20:15','2025-07-07 23:20:15'),(4,'RUC14193505','Proveedor 4','Dirección 4','946557990','ACTIVO','18c80c4d-9c86-4bcd-983b-cd3bb06ced56','2025-07-07 23:20:15','2025-07-07 23:20:15'),(5,'RUC23830278','Proveedor 5','Dirección 5','923086460','ACTIVO','ea1648b2-6061-48a7-9ce3-f2ffd61f22fc','2025-07-07 23:20:15','2025-07-07 23:20:15'),(6,'RUC54126374','Proveedor 6','Dirección 6','978801694','ACTIVO','79eabfd5-ab98-4b1a-b4bd-2be9d5d6caab','2025-07-07 23:20:15','2025-07-07 23:20:15'),(7,'RUC47535905','Proveedor 7','Dirección 7','939492833','ACTIVO','867ad2c2-bf7c-453d-be15-5fad07fb872f','2025-07-07 23:20:15','2025-07-07 23:20:15');
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
INSERT INTO `proyectos` VALUES (1,'Proyecto 1','Descripción del proyecto 1','Ubicación 1','2025-04-28','2025-08-10','En ejecución','ACTIVO','0a1c36cb-b18c-4ca1-bb25-f3e184ec91e2','2025-07-07 23:20:10','2025-07-07 23:20:10'),(2,'Proyecto 2','Descripción del proyecto 2','Ubicación 2','2025-05-15','2025-08-30','En ejecución','ACTIVO','487e28a2-2e2a-494f-8af5-acedb2f05c74','2025-07-07 23:20:10','2025-07-07 23:20:10'),(3,'Proyecto 3','Descripción del proyecto 3','Ubicación 3','2025-06-27','2025-08-12','En ejecución','ACTIVO','9fabe65b-3397-4ea1-a1d5-cdbe352142d1','2025-07-07 23:20:11','2025-07-07 23:20:11'),(4,'Proyecto 4','Descripción del proyecto 4','Ubicación 4','2025-06-08','2025-08-13','En ejecución','ACTIVO','99ba2651-aa16-4778-a1e8-b12b463072b5','2025-07-07 23:20:11','2025-07-07 23:20:11'),(5,'Proyecto 5','Descripción del proyecto 5','Ubicación 5','2025-03-31','2025-07-20','En ejecución','ACTIVO','407a3f1d-6fe6-4cbb-9173-7f75e5d41969','2025-07-07 23:20:11','2025-07-07 23:20:11'),(6,'Proyecto 6','Descripción del proyecto 6','Ubicación 6','2025-04-14','2025-07-19','En ejecución','ACTIVO','e151f3bf-f6eb-4311-932a-1083abedf2a1','2025-07-07 23:20:11','2025-07-07 23:20:11'),(7,'Proyecto 7','Descripción del proyecto 7','Ubicación 7','2025-04-17','2025-07-31','En ejecución','ACTIVO','d66b0839-c621-4402-8426-6971a0204801','2025-07-07 23:20:11','2025-07-07 23:20:11');
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
INSERT INTO `roles` VALUES (1,'ADMINISTRADOR','Acceso total al sistema, administra todos los recursos','ACTIVO','a32bd280-a2b6-4541-97b7-4b2be76459a2','2025-07-07 23:20:10','2025-07-07 23:20:10'),(2,'GERENTE','Gestiona proyectos, empleados y recursos del área','ACTIVO','c7e62d1a-678e-4950-b998-ecfd7f629304','2025-07-07 23:20:10','2025-07-07 23:20:10'),(3,'EMPLEADO','Usuario operativo, acceso limitado a funciones específicas','ACTIVO','8808df7e-09c2-4dca-ad9f-bd3ed5af0ac0','2025-07-07 23:20:10','2025-07-07 23:20:10'),(4,'USER','Usuario básico con permisos mínimos de lectura','ACTIVO','6b3fac06-5365-430a-9509-a1f228078b47','2025-07-07 23:20:10','2025-07-07 23:20:10');
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `security_audit_logs`
--

LOCK TABLES `security_audit_logs` WRITE;
/*!40000 ALTER TABLE `security_audit_logs` DISABLE KEYS */;
INSERT INTO `security_audit_logs` VALUES (1,'DATA_ACCESS',NULL,'','','','2025-07-07 20:13:10','Security system operational check - Scheduled tasks running properly','LOW'),(2,'LOGIN_SUCCESS',NULL,'','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0','2025-07-07 20:14:24','POST /conaveg/api/auth/login - Status: 200 - Duration: 1067ms','MEDIUM'),(3,'DATA_ACCESS',NULL,'','','','2025-07-07 20:37:39','Security system operational check - Scheduled tasks running properly','LOW'),(4,'DATA_ACCESS',NULL,'','','','2025-07-07 20:38:10','Security system operational check - Scheduled tasks running properly','LOW'),(5,'DATA_ACCESS',NULL,'','','','2025-07-07 20:38:28','Security system operational check - Scheduled tasks running properly','LOW'),(6,'DATA_ACCESS',NULL,'','','','2025-07-07 20:39:43','Security system operational check - Scheduled tasks running properly','LOW'),(7,'DATA_ACCESS',NULL,'','','','2025-07-07 20:44:36','Security system operational check - Scheduled tasks running properly','LOW'),(8,'DATA_ACCESS',NULL,'','','','2025-07-07 20:44:53','Security system operational check - Scheduled tasks running properly','LOW'),(9,'DATA_ACCESS',NULL,'','','','2025-07-07 20:45:08','Security system operational check - Scheduled tasks running properly','LOW'),(10,'DATA_ACCESS',NULL,'','','','2025-07-07 20:45:24','Security system operational check - Scheduled tasks running properly','LOW'),(11,'DATA_ACCESS',NULL,'','','','2025-07-07 20:45:40','Security system operational check - Scheduled tasks running properly','LOW'),(12,'DATA_ACCESS',NULL,'','','','2025-07-07 20:45:51','Security system operational check - Scheduled tasks running properly','LOW'),(13,'DATA_ACCESS',NULL,'','','','2025-07-07 20:46:05','Security system operational check - Scheduled tasks running properly','LOW'),(14,'DATA_ACCESS',NULL,'','','','2025-07-07 20:46:18','Security system operational check - Scheduled tasks running properly','LOW'),(15,'DATA_ACCESS',NULL,'','','','2025-07-07 20:46:29','Security system operational check - Scheduled tasks running properly','LOW'),(16,'DATA_ACCESS',NULL,'','','','2025-07-07 20:48:37','Security system operational check - Scheduled tasks running properly','LOW'),(17,'DATA_ACCESS',NULL,'','','','2025-07-07 20:49:58','Security system operational check - Scheduled tasks running properly','LOW'),(18,'DATA_ACCESS',NULL,'','','','2025-07-07 20:50:04','Security system operational check - Scheduled tasks running properly','LOW'),(19,'DATA_ACCESS',NULL,'','','','2025-07-07 20:50:09','Security system operational check - Scheduled tasks running properly','LOW'),(20,'DATA_ACCESS',NULL,'','','','2025-07-07 20:53:55','Security system operational check - Scheduled tasks running properly','LOW'),(21,'DATA_ACCESS',NULL,'','','','2025-07-07 20:54:16','Security system operational check - Scheduled tasks running properly','LOW'),(22,'DATA_ACCESS',NULL,'','','','2025-07-07 20:54:30','Security system operational check - Scheduled tasks running properly','LOW'),(23,'DATA_ACCESS',NULL,'','','','2025-07-07 20:54:58','Security system operational check - Scheduled tasks running properly','LOW'),(24,'DATA_ACCESS',NULL,'','','','2025-07-07 20:56:05','Security system operational check - Scheduled tasks running properly','LOW'),(25,'DATA_ACCESS',NULL,'','','','2025-07-07 20:59:57','Security system operational check - Scheduled tasks running properly','LOW'),(26,'DATA_ACCESS',NULL,'','','','2025-07-07 21:00:20','Security system operational check - Scheduled tasks running properly','LOW'),(27,'DATA_ACCESS',NULL,'','','','2025-07-07 21:01:59','Security system operational check - Scheduled tasks running properly','LOW'),(28,'DATA_ACCESS',NULL,'','','','2025-07-07 21:03:29','Security system operational check - Scheduled tasks running properly','LOW'),(29,'DATA_ACCESS',NULL,'','','','2025-07-07 21:05:28','Security system operational check - Scheduled tasks running properly','LOW'),(30,'DATA_ACCESS',NULL,'','','','2025-07-07 21:05:33','Security system operational check - Scheduled tasks running properly','LOW'),(31,'DATA_ACCESS',NULL,'','','','2025-07-07 21:05:39','Security system operational check - Scheduled tasks running properly','LOW');
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
INSERT INTO `users` VALUES (1,1,'admin','admin@admin.com','2025-07-07 23:20:11','$2y$12$VbK/a.S9WpcTnXXn1RxUOeb/2.PR4a8CC03QrD6YCZXfjUCoeiNL.','RjiUvMaEFQ','ACTIVO','28b014d7-0d70-4bbb-9e1d-8d5a82e488d9','2025-07-07 23:20:11','2025-07-07 23:20:11'),(2,3,'usuario1','usuario1@mail.com','2025-07-07 23:20:11','$2y$12$F6bkX2laXgPHjUT8UhOZBeFJPPZX4d6vr5lE8RufJ87H.h.9GL3RG','eK6ARIuuGb','ACTIVO','e32f0fe9-d2de-4f14-992a-a2f7ca6d161a','2025-07-07 23:20:12','2025-07-07 23:20:12'),(3,3,'usuario2','usuario2@mail.com','2025-07-07 23:20:12','$2y$12$41mQi6lv1khwap.dges7PO.dULIjmeu23FHMF8.HpB3gQLru16haC','j0WrfB6Jpk','ACTIVO','44eb1afc-2475-4089-aeba-5a16484f5d88','2025-07-07 23:20:12','2025-07-07 23:20:12'),(4,1,'usuario3','usuario3@mail.com','2025-07-07 23:20:12','$2y$12$.5pli8wEVFNmLLLcdhSMnuavH6HjQndlR.yQVvuJ2ief8oTEOxCq6','pJxOd9DIvH','ACTIVO','12d1b5d2-bef9-4e39-8cb5-cf2cdc9d471e','2025-07-07 23:20:12','2025-07-07 23:20:12'),(5,4,'usuario4','usuario4@mail.com','2025-07-07 23:20:12','$2y$12$UfDidwa0yHqpe8r/j5hzzu1UVauETBXdif4//XhGLrK7rTKyxIXP2','qRpiyfX0rB','ACTIVO','c59ff9b6-efe3-4d85-9922-877230e098ec','2025-07-07 23:20:13','2025-07-07 23:20:13'),(6,3,'usuario5','usuario5@mail.com','2025-07-07 23:20:13','$2y$12$iA37cad.pO7tGIYlGrnJjOKWXGM8tisulSP45BUSVXDxAe7GOHkeu','SFzUNb0Vjb','ACTIVO','185f0351-324c-4b3b-8ea3-d7f5f1646e89','2025-07-07 23:20:13','2025-07-07 23:20:13'),(7,3,'usuario6','usuario6@mail.com','2025-07-07 23:20:13','$2y$12$XvRNSIgZhbALvOz.V0Dlbexamcv6K96g2tsYgCrjuxwbnhFR1NVgy','K5VscdUKz2','ACTIVO','f0e76e28-288d-42f4-bee4-f0f5859b667c','2025-07-07 23:20:14','2025-07-07 23:20:14'),(8,4,'usuario7','usuario7@mail.com','2025-07-07 23:20:14','$2y$12$XH/DdTMSr8OoBuyxwOoOiuyDPT1YMwuRBjq8EJheGfkR9kmLSMuru','4aU13F7H3k','ACTIVO','0258dd27-b52f-4d99-afc8-44dd26d97bc7','2025-07-07 23:20:14','2025-07-07 23:20:14'),(9,4,'usuario8','usuario8@mail.com','2025-07-07 23:20:14','$2y$12$sQ7stDM1zz.cJe/ANGnMS.UCIMsBJhsPx.u6GNfpBwtkqP2tni5d2','nYvuY1XCmU','ACTIVO','028a2e2f-c762-4f02-9d46-eecafc8deeb2','2025-07-07 23:20:15','2025-07-07 23:20:15');
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

-- Dump completed on 2025-07-07 16:11:17
