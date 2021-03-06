-- MySQL dump 10.13  Distrib 8.0.19, for Linux (x86_64)
--
-- Host: localhost    Database: kofify
-- ------------------------------------------------------
-- Server version	8.0.19

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
-- Table structure for table `AK_AUTHORITY`
--

DROP TABLE IF EXISTS `AK_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_AUTHORITY` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `NAME` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `AUTHORITY_NAME_U` (`NAME`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_AUTHORITY`
--

LOCK TABLES `AK_AUTHORITY` WRITE;
/*!40000 ALTER TABLE `AK_AUTHORITY` DISABLE KEYS */;
INSERT INTO `AK_AUTHORITY` VALUES (2,'ADMIN'),(1,'USER');
/*!40000 ALTER TABLE `AK_AUTHORITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_CHAT`
--

DROP TABLE IF EXISTS `AK_CHAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_CHAT` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `TITLE` varchar(255) DEFAULT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_CHAT`
--

LOCK TABLES `AK_CHAT` WRITE;
/*!40000 ALTER TABLE `AK_CHAT` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_CHAT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_CHAT_LINE`
--

DROP TABLE IF EXISTS `AK_CHAT_LINE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_CHAT_LINE` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `CHAT_USER_ID` int NOT NULL,
  `CONTENT` text NOT NULL,
  `TYPE` int NOT NULL DEFAULT '0',
  `READ_BY` varchar(255) NOT NULL DEFAULT '[]',
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `CHAT_LINE_USER_ID_FK` (`CHAT_USER_ID`),
  CONSTRAINT `CHAT_LINE_USER_ID_FK` FOREIGN KEY (`CHAT_USER_ID`) REFERENCES `AK_CHAT_USER` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_CHAT_LINE`
--

LOCK TABLES `AK_CHAT_LINE` WRITE;
/*!40000 ALTER TABLE `AK_CHAT_LINE` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_CHAT_LINE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_CHAT_USER`
--

DROP TABLE IF EXISTS `AK_CHAT_USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_CHAT_USER` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `CHAT_ID` int NOT NULL,
  `USER_ID` int NOT NULL,
  `ENABLED` tinyint(1) DEFAULT '0',
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CHAT_USER_CHAT_ID_USER_ID_U` (`CHAT_ID`,`USER_ID`),
  KEY `CHAT_USER_USER_ID_FK` (`USER_ID`),
  CONSTRAINT `CHAT_USER_CHAT_ID_FK` FOREIGN KEY (`CHAT_ID`) REFERENCES `AK_CHAT` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `CHAT_USER_USER_ID_FK` FOREIGN KEY (`USER_ID`) REFERENCES `AK_USER` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_CHAT_USER`
--

LOCK TABLES `AK_CHAT_USER` WRITE;
/*!40000 ALTER TABLE `AK_CHAT_USER` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_CHAT_USER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_CREDIT_LOG`
--

DROP TABLE IF EXISTS `AK_CREDIT_LOG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_CREDIT_LOG` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `CREDIT_TYPE` int NOT NULL,
  `CREDIT_OPERATION` int NOT NULL,
  `STORE` int DEFAULT NULL,
  `AMOUNT` int NOT NULL DEFAULT '0',
  `USER_ID` int NOT NULL,
  `PAYLOAD` varchar(1023) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  KEY `AK_CREDIT_LOG_AK_USER_ID_FK` (`USER_ID`),
  CONSTRAINT `AK_CREDIT_LOG_AK_USER_ID_FK` FOREIGN KEY (`USER_ID`) REFERENCES `AK_USER` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_CREDIT_LOG`
--

LOCK TABLES `AK_CREDIT_LOG` WRITE;
/*!40000 ALTER TABLE `AK_CREDIT_LOG` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_CREDIT_LOG` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_DELETED_USER`
--

DROP TABLE IF EXISTS `AK_DELETED_USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_DELETED_USER` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `FIRST_NAME` varchar(255) NOT NULL,
  `LAST_NAME` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `BIRTHDAY` date NOT NULL,
  `BIO` varchar(255) DEFAULT NULL,
  `GENDER` int DEFAULT NULL COMMENT '1-male,0-female',
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREDITS` int NOT NULL DEFAULT '0',
  `CREDITS_UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `INVITED_BY` int DEFAULT NULL,
  `REASON` varchar(255) DEFAULT NULL,
  `USER_CREATED_AT` timestamp NOT NULL,
  `USER_UPDATED_AT` timestamp NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_DELETED_USER`
--

LOCK TABLES `AK_DELETED_USER` WRITE;
/*!40000 ALTER TABLE `AK_DELETED_USER` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_DELETED_USER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_NOTIFICATION`
--

DROP TABLE IF EXISTS `AK_NOTIFICATION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_NOTIFICATION` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `TITLE` tinytext NOT NULL,
  `DESCRIPTION` text,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_NOTIFICATION`
--

LOCK TABLES `AK_NOTIFICATION` WRITE;
/*!40000 ALTER TABLE `AK_NOTIFICATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_NOTIFICATION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_POSITION`
--

DROP TABLE IF EXISTS `AK_POSITION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_POSITION` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `LAT` decimal(9,6) NOT NULL,
  `LON` decimal(9,6) NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=337 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_POSITION`
--

LOCK TABLES `AK_POSITION` WRITE;
/*!40000 ALTER TABLE `AK_POSITION` DISABLE KEYS */;
INSERT INTO `AK_POSITION` VALUES (1,45.475057,9.174874,'2019-07-26 17:36:30','2019-07-26 17:36:30'),(2,45.474834,9.174608,'2019-07-26 17:36:30','2019-07-26 17:36:30'),(3,45.574834,9.174608,'2019-07-26 17:36:30','2019-07-26 17:36:30'),(4,45.470634,9.150838,'2019-07-26 19:36:46','2019-11-24 19:34:43'),(5,45.522744,9.254658,'2019-07-26 19:36:47','2019-07-26 19:36:47'),(6,45.437907,9.267163,'2019-07-26 19:36:48','2019-07-26 19:36:48'),(7,45.451482,9.128948,'2019-07-26 19:36:49','2019-07-26 19:36:49'),(8,45.407570,9.154317,'2019-07-26 19:36:50','2019-07-26 19:36:50'),(9,45.550353,9.143456,'2019-07-26 19:36:51','2019-07-26 19:36:51'),(10,45.417459,9.234909,'2019-07-26 19:36:52','2019-07-26 19:36:52'),(11,45.474589,9.202327,'2019-07-26 19:36:53','2019-07-26 19:36:53'),(12,45.533610,9.198904,'2019-07-26 19:36:54','2019-07-26 19:36:54'),(13,45.547280,9.135692,'2019-07-26 19:36:55','2019-07-26 19:36:55'),(14,45.534162,9.167101,'2019-07-26 19:36:56','2019-07-26 19:36:56'),(15,45.553579,9.165277,'2019-07-26 19:36:57','2019-07-26 19:36:57'),(16,45.519396,9.175326,'2019-07-26 19:36:58','2019-07-26 19:36:58'),(17,45.438526,9.139893,'2019-07-26 19:36:59','2019-07-26 19:36:59'),(18,45.527370,9.141682,'2019-07-26 19:36:59','2019-07-26 19:36:59'),(19,45.456264,9.127782,'2019-07-26 19:37:00','2019-07-26 19:37:00'),(20,45.392437,9.166322,'2019-07-26 19:37:01','2019-07-26 19:37:01'),(21,45.537403,9.170672,'2019-07-26 19:37:02','2019-07-26 19:37:02'),(22,45.562821,9.195047,'2019-07-26 19:37:03','2019-07-26 19:37:03'),(23,45.426109,9.125745,'2019-07-26 19:37:04','2019-07-26 19:37:04'),(24,45.500861,9.204435,'2019-07-26 19:37:05','2019-07-26 19:37:05'),(25,45.498055,9.134188,'2019-07-26 19:37:06','2019-07-26 19:37:06'),(26,45.466432,9.137062,'2019-07-26 19:37:07','2019-07-26 19:37:07'),(27,45.479091,9.176091,'2019-07-26 19:37:08','2019-07-26 19:37:08'),(28,45.486635,9.262034,'2019-07-26 19:37:10','2019-07-26 19:37:10'),(29,45.418640,9.236550,'2019-07-26 19:37:11','2019-07-26 19:37:11'),(30,45.547511,9.203178,'2019-07-26 19:37:11','2019-07-26 19:37:11'),(31,45.474096,9.150232,'2019-07-26 19:37:12','2019-07-26 19:37:12'),(32,45.419640,9.169569,'2019-07-26 19:37:13','2019-07-26 19:37:13'),(33,45.518244,9.244915,'2019-07-26 19:37:14','2019-07-26 19:37:14'),(34,45.481595,9.221272,'2019-07-26 19:37:15','2019-07-26 19:37:15'),(35,45.553080,9.207862,'2019-07-26 19:37:16','2019-07-26 19:37:16'),(36,45.478442,9.183615,'2019-07-26 19:37:17','2019-07-26 19:37:17'),(37,45.530485,9.173613,'2019-07-26 19:37:18','2019-07-26 19:37:18'),(38,45.393280,9.197217,'2019-07-26 19:37:18','2019-07-26 19:37:18'),(39,45.420541,9.177546,'2019-07-26 19:37:19','2019-07-26 19:37:19'),(40,45.539294,9.222033,'2019-07-26 19:37:20','2019-07-26 19:37:20'),(41,45.433246,9.111456,'2019-07-26 19:37:21','2019-07-26 19:37:21'),(42,45.529769,9.116081,'2019-07-26 19:37:22','2019-07-26 19:37:22'),(43,45.484838,9.203737,'2019-07-26 19:37:23','2019-07-26 19:37:23'),(44,45.489890,9.204586,'2019-07-26 19:37:24','2019-07-26 19:37:24'),(45,45.504419,9.206398,'2019-07-26 19:37:25','2019-07-26 19:37:25'),(46,45.423686,9.186025,'2019-07-26 19:37:26','2019-07-26 19:37:26'),(47,45.452981,9.235300,'2019-07-26 19:37:27','2019-07-26 19:37:27'),(48,45.507164,9.118770,'2019-07-26 19:37:29','2019-07-26 19:37:29'),(49,45.486010,9.204052,'2019-07-26 19:37:31','2019-07-26 19:37:31'),(50,45.501752,9.265383,'2019-07-26 19:37:32','2019-07-26 19:37:32'),(51,45.527099,9.142093,'2019-07-26 19:37:33','2019-07-26 19:37:33'),(52,45.473952,9.248784,'2019-07-26 19:37:34','2019-07-26 19:37:34'),(53,45.473003,9.258972,'2019-07-26 19:37:35','2019-07-26 19:37:35'),(54,45.496569,9.189169,'2019-07-26 19:37:36','2019-07-26 19:37:36'),(55,45.477012,9.099390,'2019-07-26 19:37:36','2019-07-26 19:37:36'),(56,45.468570,9.263797,'2019-07-26 19:37:37','2019-07-26 19:37:37'),(57,45.517534,9.204000,'2019-07-26 19:37:38','2019-07-26 19:37:38'),(58,45.484826,9.122104,'2019-07-26 19:37:39','2019-07-26 19:37:39'),(59,45.487521,9.229715,'2019-07-26 19:37:40','2019-07-26 19:37:40'),(60,45.450470,9.194932,'2019-07-26 19:37:41','2019-07-26 19:37:41'),(61,45.499521,9.131548,'2019-07-26 19:37:42','2019-07-26 19:37:42'),(62,45.490219,9.124607,'2019-07-26 19:37:42','2019-07-26 19:37:42'),(63,45.425996,9.128640,'2019-07-26 19:37:43','2019-07-26 19:37:43'),(64,45.425817,9.257868,'2019-07-26 19:37:44','2019-07-26 19:37:44'),(65,45.465616,9.251693,'2019-07-26 19:37:45','2019-07-26 19:37:45'),(66,45.530385,9.157709,'2019-07-26 19:37:46','2019-07-26 19:37:46'),(67,45.543164,9.192415,'2019-07-26 19:37:47','2019-07-26 19:37:47'),(68,45.517282,9.116748,'2019-07-26 19:37:48','2019-07-26 19:37:48'),(69,45.534001,9.210431,'2019-07-26 19:37:48','2019-07-26 19:37:48'),(70,45.433500,9.252573,'2019-07-26 19:37:49','2019-07-26 19:37:49'),(71,45.454264,9.181657,'2019-07-26 19:37:50','2019-07-26 19:37:50'),(72,45.518873,9.144019,'2019-07-26 19:37:51','2019-07-26 19:37:51'),(73,45.548576,9.191185,'2019-07-26 19:37:52','2019-07-26 19:37:52'),(74,45.422223,9.243143,'2019-07-26 19:37:53','2019-07-26 19:37:53'),(75,45.508624,9.175102,'2019-07-26 19:37:54','2019-07-26 19:37:54'),(76,45.491940,9.248892,'2019-07-26 19:37:55','2019-07-26 19:37:55'),(77,45.522564,9.139946,'2019-07-26 19:37:56','2019-07-26 19:37:56'),(78,45.397892,9.138972,'2019-07-26 19:37:57','2019-07-26 19:37:57'),(79,45.447709,9.115002,'2019-07-26 19:37:58','2019-07-26 19:37:58'),(80,45.515693,9.213119,'2019-07-26 19:37:59','2019-07-26 19:37:59'),(81,45.532899,9.184822,'2019-07-26 19:38:00','2019-07-26 19:38:00'),(82,45.498620,9.157745,'2019-07-26 19:38:00','2019-07-26 19:38:00'),(83,45.445098,9.113408,'2019-07-26 19:38:01','2019-07-26 19:38:01'),(84,45.510829,9.179784,'2019-07-26 19:38:02','2019-07-26 19:38:02'),(85,45.383925,9.172451,'2019-07-26 19:38:03','2019-07-26 19:38:03'),(86,45.445097,9.177824,'2019-07-26 19:38:04','2019-07-26 19:38:04'),(87,45.466657,9.239390,'2019-07-26 19:38:05','2019-07-26 19:38:05'),(88,45.487812,9.206321,'2019-07-26 19:38:05','2019-07-26 19:38:05'),(89,45.521074,9.212596,'2019-07-26 19:38:06','2019-07-26 19:38:06'),(90,45.427266,9.209207,'2019-07-26 19:38:07','2019-07-26 19:38:07'),(91,45.415002,9.236322,'2019-07-26 19:38:08','2019-07-26 19:38:08'),(92,45.544655,9.168616,'2019-07-26 19:38:09','2019-07-26 19:38:09'),(93,45.393448,9.188271,'2019-07-26 19:38:09','2019-07-26 19:38:09'),(94,45.529155,9.128648,'2019-07-26 19:38:10','2019-07-26 19:38:10'),(95,45.432466,9.180394,'2019-07-26 19:38:11','2019-07-26 19:38:11'),(96,45.478263,9.168669,'2019-07-26 19:38:12','2019-07-26 19:38:12'),(97,45.446680,9.268610,'2019-07-26 19:38:13','2019-07-26 19:38:13'),(98,45.450943,9.113463,'2019-07-26 19:38:14','2019-07-26 19:38:14'),(99,45.527907,9.154691,'2019-07-26 19:38:15','2019-07-26 19:38:15'),(100,45.534030,9.130584,'2019-07-26 19:38:15','2019-07-26 19:38:15'),(101,45.517823,9.171603,'2019-07-26 19:38:16','2019-07-26 19:38:16'),(102,45.489921,9.173565,'2019-07-26 19:38:17','2019-07-26 19:38:17'),(103,45.506543,9.218016,'2019-07-26 19:38:18','2019-07-26 19:38:18'),(104,45.475057,9.174874,'2019-07-26 17:36:30','2019-07-26 17:36:30'),(105,45.474834,9.174608,'2019-07-26 17:36:30','2019-07-26 17:36:30'),(106,45.574834,9.174608,'2019-07-26 17:36:30','2019-07-26 17:36:30'),(107,45.491604,9.123895,'2019-07-26 19:36:46','2019-07-26 19:36:46'),(108,45.522744,9.254658,'2019-07-26 19:36:47','2019-07-26 19:36:47'),(109,45.437907,9.267163,'2019-07-26 19:36:48','2019-07-26 19:36:48'),(110,45.451482,9.128948,'2019-07-26 19:36:49','2019-07-26 19:36:49'),(111,45.407570,9.154317,'2019-07-26 19:36:50','2019-07-26 19:36:50'),(112,45.550353,9.143456,'2019-07-26 19:36:51','2019-07-26 19:36:51'),(113,45.417459,9.234909,'2019-07-26 19:36:52','2019-07-26 19:36:52'),(114,45.474589,9.202327,'2019-07-26 19:36:53','2019-07-26 19:36:53'),(115,45.533610,9.198904,'2019-07-26 19:36:54','2019-07-26 19:36:54'),(116,45.547280,9.135692,'2019-07-26 19:36:55','2019-07-26 19:36:55'),(117,45.534162,9.167101,'2019-07-26 19:36:56','2019-07-26 19:36:56'),(118,45.553579,9.165277,'2019-07-26 19:36:57','2019-07-26 19:36:57'),(119,45.519396,9.175326,'2019-07-26 19:36:58','2019-07-26 19:36:58'),(120,45.438526,9.139893,'2019-07-26 19:36:59','2019-07-26 19:36:59'),(121,45.527370,9.141682,'2019-07-26 19:36:59','2019-07-26 19:36:59'),(122,45.456264,9.127782,'2019-07-26 19:37:00','2019-07-26 19:37:00'),(123,45.392437,9.166322,'2019-07-26 19:37:01','2019-07-26 19:37:01');
/*!40000 ALTER TABLE `AK_POSITION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_PRODUCT`
--

DROP TABLE IF EXISTS `AK_PRODUCT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_PRODUCT` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `PRODUCT_ID` varchar(255) NOT NULL,
  `NAME` varchar(255) NOT NULL,
  `STORE` int NOT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `CREDITS` int NOT NULL,
  `CREATED_AT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `AK_PRODUCT_PRODUCT_ID_uindex` (`PRODUCT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_PRODUCT`
--

LOCK TABLES `AK_PRODUCT` WRITE;
/*!40000 ALTER TABLE `AK_PRODUCT` DISABLE KEYS */;
INSERT INTO `AK_PRODUCT` VALUES (1,'android.test.purchased','android test purchase',0,'android test purchase',10,'2019-09-14 14:35:53','2019-09-14 14:35:53'),(2,'credits10','10 credits',0,'invite out for coffee, start conversation, discover who likes you.',10,'2019-09-14 14:35:53','2019-09-14 14:35:53'),(3,'credits50','50 credits',0,'save 40% invite out for coffee, start conversation, discover who likes you',50,'2019-09-14 14:35:53','2019-09-14 14:35:53'),(4,'credits100','100 credits',0,'save 60% invite out for coffee, start conversation, discover who likes you',100,'2019-11-09 13:04:36','2019-11-09 13:04:36');
/*!40000 ALTER TABLE `AK_PRODUCT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_PUSH`
--

DROP TABLE IF EXISTS `AK_PUSH`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_PUSH` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `USER_ID` varchar(255) NOT NULL,
  `PUSH_TOKEN` varchar(255) NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_PUSH`
--

LOCK TABLES `AK_PUSH` WRITE;
/*!40000 ALTER TABLE `AK_PUSH` DISABLE KEYS */;
INSERT INTO `AK_PUSH` VALUES (1,'05e9a45a-e78e-4acb-a49f-39fdb476d631','d7UWOwNMoMk:APA91bGvSaz1S8_jBbGr0OmdOazdWMVHCd0nwdxCKFMh6H6RyjvYYKRtPrKzV0VUprsI5rl-75M7ctVSaaWDLVYxZFiK5NjTfLCTqv4Hw-NTTJhNATDoFNzL3oYzvdgWeVdBnCYsf_D3','2019-08-10 17:07:02','2019-11-24 18:42:38');
/*!40000 ALTER TABLE `AK_PUSH` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_SOCIAL`
--

DROP TABLE IF EXISTS `AK_SOCIAL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_SOCIAL` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `USER_ID` int NOT NULL,
  `SOCIAL_ID` varchar(255) NOT NULL,
  `SOCIAL_TYPE` int NOT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SOCIAL_USER_ID_SOCIAL_ID_U` (`USER_ID`,`SOCIAL_ID`),
  UNIQUE KEY `SOCIAL_EMAIL_U` (`EMAIL`),
  CONSTRAINT `SOCIAL_USER_ID_FK` FOREIGN KEY (`USER_ID`) REFERENCES `AK_USER` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_SOCIAL`
--

LOCK TABLES `AK_SOCIAL` WRITE;
/*!40000 ALTER TABLE `AK_SOCIAL` DISABLE KEYS */;
/*!40000 ALTER TABLE `AK_SOCIAL` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_USER`
--

DROP TABLE IF EXISTS `AK_USER`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_USER` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `FIRST_NAME` varchar(255) NOT NULL,
  `LAST_NAME` varchar(255) DEFAULT NULL,
  `EMAIL` varchar(255) NOT NULL,
  `PASSWORD_HASH` varchar(255) DEFAULT NULL,
  `IMG_URL` varchar(255) DEFAULT NULL,
  `BIRTHDAY` date NOT NULL,
  `BIO` varchar(255) DEFAULT NULL,
  `GENDER` int DEFAULT NULL COMMENT '1-male,0-female',
  `CREATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ACCOUNT_EXPIRED` tinyint(1) DEFAULT '0',
  `ACCOUNT_LOCKED` tinyint(1) DEFAULT '0',
  `CREDENTIALS_EXPIRED` tinyint(1) DEFAULT '0',
  `ENABLED` tinyint(1) DEFAULT '1',
  `POSITION_ID` int DEFAULT NULL,
  `PUSH_ID` int DEFAULT NULL,
  `LAST_SEEN` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `CREDITS` int NOT NULL DEFAULT '5',
  `CREDITS_UPDATED_AT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ADMOB_COUNT` int DEFAULT '0',
  `ADMOB_UPDATED_AT` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `INVITED_BY` int DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_EMAIL_U` (`EMAIL`),
  KEY `USER_ID_FK` (`POSITION_ID`),
  KEY `PUSH_ID_FK` (`PUSH_ID`),
  KEY `USER_USER_ID_FK` (`INVITED_BY`),
  CONSTRAINT `USER_POSITION_ID_FK` FOREIGN KEY (`POSITION_ID`) REFERENCES `AK_POSITION` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `USER_PUSH_ID_FK` FOREIGN KEY (`PUSH_ID`) REFERENCES `AK_PUSH` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `USER_USER_ID_FK` FOREIGN KEY (`INVITED_BY`) REFERENCES `AK_USER` (`ID`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_USER`
--

LOCK TABLES `AK_USER` WRITE;
/*!40000 ALTER TABLE `AK_USER` DISABLE KEYS */;
INSERT INTO `AK_USER` VALUES (1,'FirstName 1','LastName 1','email1@gmail.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',NULL,'2007-06-18',NULL,NULL,'2019-07-26 17:36:30','2019-12-18 19:55:50',0,0,0,1,1,NULL,'2019-12-18 19:55:47',123,'2019-12-16 19:35:25',0,'2019-09-08 15:36:06',2),(2,'FirstName 2','LastName 2','email2@gmail.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',NULL,'2009-06-18',NULL,NULL,'2019-07-26 17:36:30','2019-12-22 16:07:42',0,0,0,1,2,NULL,'2019-12-22 16:07:42',100,'2019-12-18 20:26:48',0,'2019-09-08 15:36:06',NULL),(3,'FirstName 3','LastName 3','email3@gmail.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW',NULL,'2010-06-18',NULL,NULL,'2019-07-26 17:36:30','2019-07-26 17:36:30',0,0,0,1,3,NULL,'2019-07-26 17:36:30',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(4,'Ilaria','Monti','ilaria.monti@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_83105e98-8a29-4d99-848e-e443043b4ec5.jpg','1989-10-31','Tv guru. Introvert. Social media expert. Travel fan. Alcohol enthusiast. Twitter ninja.',0,'2019-07-26 19:36:46','2019-12-22 14:56:10',0,0,0,1,4,1,'2019-12-22 14:56:10',264,'2019-11-24 19:51:36',3,'2019-10-20 17:41:09',2),(5,'Jessica De','Luca','jessica de.luca@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_58cc76b7-22e1-49af-8523-ecc08378c88d.jpg','1990-12-08','General analyst. Music guru. Social mediaholic. Alcohol fan. Bacon geek. Pop culture lover. Explorer.',0,'2019-07-26 19:36:47','2019-12-18 19:22:03',0,0,0,1,5,NULL,'2019-12-18 19:22:03',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(6,'Francesca','Barone','francesca.barone@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_10a9f943-eafb-42c3-88c9-9f6ce7afcbb9.jpg','1992-04-20','Bacon lover. Organizer. Unapologetic pop culture expert. Avid alcohol ninja. Travel enthusiast. Friendly gamer. Zombie junkie. Tv geek.',0,'2019-07-26 19:36:48','2019-12-18 20:27:01',0,0,0,1,6,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(7,'Giulia','Coppola','giulia.coppola@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_5db563d3-c055-4331-b6d2-e5085cd2e7f9.jpg','1996-09-07','Evil student. Lifelong introvert. Problem solver. Gamer. Food junkie. Freelance alcohol specialist. Incurable travel aficionado.',0,'2019-07-26 19:36:49','2019-12-18 19:04:33',0,0,0,1,7,NULL,'2019-12-18 19:04:33',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(8,'Nicole','Marino','nicole.marino@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c871508e-ffb5-4649-9f3c-0c645c19b5b6.jpg','1998-04-06','Coffee ninja. Music junkie. Tv lover. Communicator. Devoted web scholar. Twitter fanatic. Entrepreneur.',0,'2019-07-26 19:36:50','2019-12-22 14:56:10',0,0,0,1,8,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(9,'Valeria','Greco','valeria.greco@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_7e673b43-2229-481d-b05a-82e42e0767e5.jpg','1996-12-15','Analyst. Coffeeaholic. Creator. Unapologetic gamer. Web expert. Hipster-friendly travel fanatic. Organizer. Thinker.',0,'2019-07-26 19:36:51','2019-12-16 19:17:17',0,0,0,1,9,NULL,'2019-12-16 19:17:17',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(10,'Eleonora','Piras','eleonora.piras@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8894b032-4b4b-490b-ace3-035716c59320.jpg','1998-01-07','Web practitioner. Hardcore problem solver. Troublemaker. Infuriatingly humble pop culture expert. Bacon maven. Amateur music scholar.',0,'2019-07-26 19:36:52','2019-12-22 16:06:41',0,0,0,1,10,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(11,'Cristina','Rinaldi','cristina.rinaldi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_85708b2e-d33b-4a5b-8d3d-4ffd4510b70e.jpg','1996-11-04','Hipster-friendly writer. Certified organizer. Zombie practitioner. Tv trailblazer. Alcohol scholar.',0,'2019-07-26 19:36:53','2019-12-18 20:27:02',0,0,0,1,11,NULL,'2019-12-18 20:27:02',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(12,'Giada','Milani','giada.milani@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_66b5ef8b-2c19-49be-9ff6-4f47896d4770.jpg','1994-08-30','Coffee specialist. Subtly charming travel practitioner. Thinker. Communicator. Certified food ninja.',0,'2019-07-26 19:36:54','2019-12-22 14:56:10',0,0,0,1,12,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(13,'Maria','Caruso','maria.caruso@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_ae2ebed8-cc83-4f32-9d32-35b6ad12b32b.jpg','1997-08-09','Explorer. Alcohol junkie. Music nerd. Food specialist. Amateur web practitioner. Tv maven.',0,'2019-07-26 19:36:55','2019-12-18 20:27:01',0,0,0,1,13,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(14,'Paola','Russo','paola.russo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c076980f-9f53-4e70-aed2-3c73ca628b7d.jpg','1998-02-08','Twitter fanatic. Alcohol evangelist. Thinker. Introvert. Pop culture geek. Web ninja. Music junkie. Baconaholic. Writer.',0,'2019-07-26 19:36:56','2019-12-22 14:56:10',0,0,0,1,14,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(15,'Ilaria','Esposito','ilaria.esposito@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_788f13bb-d62e-4715-9433-23fab458af9a.jpg','1989-10-19','Total webaholic. Twitter maven. Certified food junkie. Alcohol fanatic. Music enthusiast. Pop culture specialist. Incurable organizer.',0,'2019-07-26 19:36:57','2019-12-18 19:04:32',0,0,0,1,15,NULL,'2019-12-18 19:04:32',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(16,'Erica','Ferrari','erica.ferrari@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b18add39-d86e-4776-8f8a-89b5f6e4b7e9.jpg','1989-05-30','Entrepreneur. Beer geek. Coffee fan. Infuriatingly humble creator. General twitter nerd. Gamer.',0,'2019-07-26 19:36:58','2019-12-22 16:06:41',0,0,0,1,16,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(17,'Caterina','Esposito','caterina.esposito@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_5a2b7e65-9c27-4181-a977-d606eb11b56a.jpg','1994-07-08','Alcohol fan. Proud internet guru. Creator. Analyst. Thinker. Pop culture fanatic. Writer.',0,'2019-07-26 19:36:59','2019-12-22 16:06:41',0,0,0,1,17,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(18,'Chiara','Gallo','chiara.gallo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_62e63f92-30d5-445f-a119-baec821e6c36.jpg','1989-11-05','Falls down a lot. Writer. Student. Music scholar. Explorer. Friendly internet fanatic. Analyst. Devoted creator. Twitter fan.',0,'2019-07-26 19:36:59','2019-12-22 14:56:10',0,0,0,1,18,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(19,'Erika','Sanna','erika.sanna@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_e9af5e2d-6e6e-4a4a-b81d-420ee8ec9b8c.jpg','1990-10-16','Falls down a lot. Pop culture buff. Friendly beer ninja. Social media trailblazer. Twitter expert.',0,'2019-07-26 19:37:00','2019-12-22 16:06:41',0,0,0,1,19,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(20,'Martina De','Luca','martina de.luca@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b934af00-5c1e-4585-abca-f192a1c17e72.jpg','1993-11-20','Avid pop cultureaholic. Student. Bacon fan. Music buff. Tv fanatic. Alcohol junkie. Beer advocate. Twitter maven. Wannabe zombie practitioner.',0,'2019-07-26 19:37:01','2019-12-22 14:56:10',0,0,0,1,20,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(21,'Ilaria','Parisi','ilaria.parisi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_775bd6c1-0587-4b3c-b021-fc9089fe4158.jpg','1999-06-12','Alcohol fanatic. Hardcore internet scholar. Incurable thinker. Prone to fits of apathy. Web maven.',0,'2019-07-26 19:37:02','2019-12-16 19:17:00',0,0,0,1,21,NULL,'2019-12-16 19:17:00',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(22,'Nicole','Conti','nicole.conti@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_7d5f0ccf-bfcd-4a6e-8d2a-2e193673d55a.jpg','1990-11-13','Coffee maven. Infuriatingly humble bacon specialist. Food geek. Hardcore internet aficionado.',0,'2019-07-26 19:37:03','2019-12-22 16:06:41',0,0,0,1,22,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(23,'Jessica','Leone','jessica.leone@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_2a561fcf-df3a-4c2f-9c42-a1ec246c6178.jpg','1989-10-29','Reader. General tv expert. Music advocate. Entrepreneur. Lifelong gamer. Twitter guru.',0,'2019-07-26 19:37:04','2019-12-18 19:55:15',0,0,0,1,23,NULL,'2019-12-18 19:55:15',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(24,'Valentina','Fiore','valentina.fiore@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_2a681a33-8c62-4856-83a9-1cc5f7968056.jpg','1991-03-01','Award-winning pop culture enthusiast. Travel aficionado. Thinker. Writer. Twitter expert.',0,'2019-07-26 19:37:05','2019-12-22 14:56:10',0,0,0,1,24,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(25,'Alessia','Messina','alessia.messina@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_a813f505-c9cc-4e6c-b2a4-85a4ee7cbe2d.jpg','1995-12-10','Social media specialist. Pop culture aficionado. Analyst. Problem solver. Internet advocate.',0,'2019-07-26 19:37:06','2019-12-16 19:17:17',0,0,0,1,25,NULL,'2019-12-16 19:17:17',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(26,'Michela','Amato','michela.amato@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_45d528aa-ac9f-42b2-b1f3-b38e4613b4a2.jpg','1991-10-01','Extreme tv fanatic. Wannabe zombie expert. Proud music aficionado. Incurable social media scholar.',0,'2019-07-26 19:37:07','2019-12-22 16:06:40',0,0,0,1,26,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(27,'Chiara','Bellini','chiara.bellini@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_292ba292-61eb-4145-b60d-09de1db6bfb4.jpg','1999-08-06','Entrepreneur. Student. Wannabe bacon enthusiast. Award-winning tvaholic. Creator. Twitter lover.',0,'2019-07-26 19:37:08','2019-12-22 16:06:41',0,0,0,1,27,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(28,'Michela','Martino','michela.martino@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c004ea7b-81e9-499f-a651-01927be3ec60.jpg','1992-12-09','Writer. Tv nerd. Problem solver. Pop culture enthusiast. Internet scholar. Evil music fanatic. Alcohol maven.',0,'2019-07-26 19:37:10','2019-12-22 14:56:10',0,0,0,1,28,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(29,'Alessandra','Giannini','alessandra.giannini@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4ff53492-cd04-4c51-9138-4166281f2b54.jpg','1996-03-11','Entrepreneur. Music scholar. Bacon practitioner. Internet enthusiast. Analyst. Introvert. Student.',0,'2019-07-26 19:37:11','2019-12-22 14:56:10',0,0,0,1,29,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(30,'Lisa','Sartori','lisa.sartori@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_5b790bc7-079f-44fc-b874-56cd8afc4d18.jpg','1999-02-10','Music buff. Alcohol junkie. Devoted travel specialist. Pop culture fanatic. Passionate reader. Extreme web fan.',0,'2019-07-26 19:37:11','2019-12-18 20:27:02',0,0,0,1,30,NULL,'2019-12-18 20:27:02',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(31,'Valentina','Salvi','valentina.salvi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_19c1a48e-17a2-46b0-a337-c71745d2a0f9.jpg','1991-09-01','Professional beer guru. Thinker. Troublemaker. Zombie lover. Pop culture fan. Travel aficionado.',0,'2019-07-26 19:37:12','2019-12-16 19:17:17',0,0,0,1,31,NULL,'2019-12-16 19:17:17',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(32,'Roberta','Fabbri','roberta.fabbri@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_9123c900-aba0-47f7-8a42-189a6afbd533.jpg','1997-10-12','Devoted coffee specialist. Entrepreneur. Zombie guru. Internet geek. Music trailblazer.',0,'2019-07-26 19:37:13','2019-12-16 19:13:41',0,0,0,1,32,NULL,'2019-12-16 19:13:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(33,'Ilaria','Pagano','ilaria.pagano@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_40b3ad4b-2f2e-499d-b2e0-434b77091a2d.jpg','1990-04-21','Food maven. Entrepreneur. Organizer. Coffee aficionado. Alcohol fanatic. Tv trailblazer. Future teen idol. Creator.',0,'2019-07-26 19:37:14','2019-12-22 14:56:10',0,0,0,1,33,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(34,'Nicole','Palazzo','nicole.palazzo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_5849d78f-41f8-4f97-80f0-47c0849c7c97.jpg','1992-10-25','Social media maven. Beeraholic. Music fanatic. Proud alcohol fan. Twitter practitioner. Problem solver. Creator.',0,'2019-07-26 19:37:15','2019-12-16 19:36:05',0,0,0,1,34,NULL,'2019-12-16 19:36:05',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(35,'Caterina','Testa','caterina.testa@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_d7f1a2da-33c9-4ccb-8db6-d66026c6056d.jpg','1994-03-21','Beer nerd. Proud coffee advocate. Problem solver. Pop culture geek. Lifelong creator. Bacon guru. Award-winning entrepreneur.',0,'2019-07-26 19:37:16','2019-12-22 16:06:41',0,0,0,1,35,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(36,'Monica','Fiore','monica.fiore@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_e35ed960-ec4b-4a99-93e8-2daf35f8a32c.jpg','1994-02-24','Freelance organizer. Internet evangelist. Proud thinker. Coffee practitioner. Problem solver. Pop culture geek.',0,'2019-07-26 19:37:17','2019-12-18 19:55:16',0,0,0,1,36,NULL,'2019-12-18 19:55:16',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(37,'Michela','Rizzo','michela.rizzo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_709504b5-8ded-4147-8af0-0f2ba121eec7.jpg','1991-09-19','Writer. Internet nerd. Evil alcohol scholar. Organizer. Coffee expert. Analyst. Proud food advocate.',0,'2019-07-26 19:37:18','2019-12-22 16:06:40',0,0,0,1,37,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(38,'Erika','Bernardi','erika.bernardi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_db8d7a4e-d78a-48fe-a03b-11fa044a28ed.jpg','1989-02-12','Social media fanatic. Hipster-friendly writer. Proud coffee maven. Total pop culture ninja.',0,'2019-07-26 19:37:18','2019-12-16 19:17:17',0,0,0,1,38,NULL,'2019-12-16 19:17:17',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(39,'Roberta','Ferretti','roberta.ferretti@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c61064f8-ab9c-4dff-889f-88d8df0f1220.jpg','1994-09-10','Bacon fanatic. Tv junkie. Wannabe problem solver. Award-winning food evangelist.',0,'2019-07-26 19:37:19','2019-12-22 16:06:41',0,0,0,1,39,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(40,'Francesca','Marchetti','francesca.marchetti@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_57af94c8-3d45-49c4-8f35-a8ef9685c12d.jpg','1997-05-07','Coffee maven. Travel lover. Tv ninja. Evil thinker. Extreme bacon expert.',0,'2019-07-26 19:37:20','2019-12-22 16:06:41',0,0,0,1,40,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(41,'Angela','Fontana','angela.fontana@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_0e1f3a8c-a7c4-4863-ae96-ec89c6df47f5.jpg','1998-02-06','Bacon evangelist. Zombie fanatic. Internet advocate. Alcohol trailblazer. Total web practitioner.',0,'2019-07-26 19:37:21','2019-12-18 20:27:01',0,0,0,1,41,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(42,'Valentina','Giuliani','valentina.giuliani@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_cad719c9-5e92-4c0f-b087-f597b3ef7149.jpg','1997-01-11','Zombie practitioner. Organizer. Proud social media fanatic. Award-winning creator. Twitter guru.',0,'2019-07-26 19:37:22','2019-12-22 14:56:10',0,0,0,1,42,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(43,'Vanessa','Giannini','vanessa.giannini@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_139fe53a-0947-4f54-a279-22b8bd6063bb.jpg','1991-12-01','Lifelong writer. Zombie lover. Baconaholic. Internet guru. Social media practitioner. Avid twitter expert.',0,'2019-07-26 19:37:23','2019-12-22 14:56:10',0,0,0,1,43,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(44,'Giorgia','Carbone','giorgia.carbone@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_48a67e8f-faab-4c8a-8372-6ec26a3530ce.jpg','1990-02-19','Writer. Evil communicator. Bacon fanatic. Infuriatingly humble twitter specialist. Social media lover.',0,'2019-07-26 19:37:24','2019-12-22 14:56:10',0,0,0,1,44,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(45,'Greta','Sala','greta.sala@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8c8ea25c-737d-40f9-959f-c4f20cfdad91.jpg','1997-05-16','Wannabe music enthusiast. Incurable tvaholic. Thinker. Alcohol evangelist. Coffee specialist.',0,'2019-07-26 19:37:25','2019-12-22 14:56:10',0,0,0,1,45,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(46,'Valeria','Palmieri','valeria.palmieri@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_ed43a885-ca09-42df-9833-384798863b91.jpg','1993-10-29','Beer enthusiast. Entrepreneur. Bacon expert. Gamer. Web geek. Music maven. Travel specialist.',0,'2019-07-26 19:37:26','2019-12-22 16:06:41',0,0,0,1,46,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(47,'Irene','Adami','irene.adami@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b7c2e804-6b65-43bf-82ea-82cf3953dd6d.jpg','1999-06-03','Writer. Infuriatingly humble tv enthusiast. Explorer. General analyst. Troublemaker. Web maven. Travel specialist.',0,'2019-07-26 19:37:27','2019-12-16 19:17:00',0,0,0,1,47,NULL,'2019-12-16 19:17:00',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(48,'Sofia','Ferrari','sofia.ferrari@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_08a3d188-18e8-4716-ba04-d186e707d020.jpg','1998-10-07','Lifelong internet ninja. Typical twitter trailblazer. Proud travel buff. Freelance bacon advocate.',0,'2019-07-26 19:37:29','2019-12-18 19:04:33',0,0,0,1,48,NULL,'2019-12-18 19:04:33',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(49,'Alice','Testa','alice.testa@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_32b2985d-7233-4d61-bb84-3920f4ff5ae5.jpg','1989-01-24','General problem solver. Bacon fanatic. Lifelong writer. Web evangelist. Beer ninja. Alcohol guru.',0,'2019-07-26 19:37:31','2019-12-18 19:55:16',0,0,0,1,49,NULL,'2019-12-18 19:55:16',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(50,'Laura','Giannini','laura.giannini@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c97e9ba5-937c-41f0-b03a-18bf7bc156f8.jpg','1992-10-06','Social media fanatic. Avid writer. Music evangelist. Web geek. Food advocate. Zombie maven.',0,'2019-07-26 19:37:32','2019-12-18 20:27:01',0,0,0,1,50,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(51,'Giada','Piras','giada.piras@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_28e91472-64fa-4dd8-a113-cedcee9a8745.jpg','1997-09-15','Proud travel geek. Award-winning web fan. Organizer. Twitter guru. Professional writer. Beer lover.',0,'2019-07-26 19:37:33','2019-12-22 16:06:40',0,0,0,1,51,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(52,'Gaia','Vitali','gaia.vitali@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_72663beb-95a1-445f-a25b-f341bab2adab.jpg','1992-10-16','Alcohol practitioner. Problem solver. Internet geek. Beer aficionado. Travel advocate. Passionate zombieaholic. Twitter fanatic.',0,'2019-07-26 19:37:34','2019-12-22 14:56:10',0,0,0,1,52,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(53,'Chiara','Montanari','chiara.montanari@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_0e2a05b8-591e-4633-bb44-074eafec2f62.jpg','1989-12-10','Bacon buff. Problem solver. Passionate music fanatic. Infuriatingly humble tv trailblazer. Freelance alcohol scholar.',0,'2019-07-26 19:37:35','2019-12-16 19:06:40',0,0,0,1,53,NULL,'2019-12-16 19:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(54,'Valentina','Cattaneo','valentina.cattaneo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_de15dfcb-3338-4e8d-9888-b428d4055ea9.jpg','1993-06-13','Amateur internet maven. Hardcore tv junkie. Social media evangelist. Twitter enthusiast. Typical pop culture fanatic.',0,'2019-07-26 19:37:36','2019-12-22 16:06:41',0,0,0,1,54,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(55,'Stefania','Cattaneo','stefania.cattaneo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_0d319934-314e-482e-af00-992dc544166c.jpg','1993-03-27','Prone to fits of apathy. Analyst. Pop culture geek. Reader. Bacon buff. Organizer. Coffee fan.',0,'2019-07-26 19:37:36','2019-12-22 16:06:41',0,0,0,1,55,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(56,'Erica','Vitali','erica.vitali@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8019941a-1947-4296-88da-f180d3faa0e7.jpg','1994-02-14','Internet guru. Hardcore student. Travel trailblazer. Devoted twitter practitioner. Analyst. Food fan.',0,'2019-07-26 19:37:37','2019-12-22 14:56:10',0,0,0,1,56,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(57,'Valentina','Ferrari','valentina.ferrari@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_5321a0b0-3c10-443b-a1a9-61ba7a6da1bc.jpg','1996-06-20','Unable to type with boxing gloves on. Award-winning zombie scholar. Alcohol guru.',0,'2019-07-26 19:37:38','2019-12-18 20:27:01',0,0,0,1,57,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(58,'Valentina','Santoro','valentina.santoro@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_f9460665-8144-43db-bb0b-7f90e5aead98.jpg','1990-10-01','Organizer. Introvert. Communicator. Food maven. Award-winning problem solver. Web specialist. Gamer.',0,'2019-07-26 19:37:39','2019-12-22 14:56:10',0,0,0,1,58,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(59,'Anna','Mariani','anna.mariani@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_975ee4b5-b983-4160-aed5-ecb5e405c221.jpg','1993-10-02','Hardcore troublemaker. Organizer. Zombie enthusiast. Thinker. Problem solver. Proud introvert.',0,'2019-07-26 19:37:40','2019-12-22 16:06:40',0,0,0,1,59,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(60,'Gaia','Colombo','gaia.colombo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_e0b9f7eb-4a76-4672-a334-0d24ea881068.jpg','1990-08-17','Tv buff. Hardcore twitter nerd. Introvert. Certified organizer. Food guru. Bacon junkie.',0,'2019-07-26 19:37:41','2019-12-22 14:56:10',0,0,0,1,60,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(61,'Nicole','Barbieri','nicole.barbieri@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_d7c92199-fc35-442e-8586-917ef00be4ea.jpg','1997-07-04','Tv advocate. Devoted entrepreneur. Evil beer specialist. Zombie evangelist. Writer.',0,'2019-07-26 19:37:42','2019-12-18 19:22:03',0,0,0,1,61,NULL,'2019-12-18 19:22:03',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(62,'Veronica','Rizzi','veronica.rizzi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8e2a8f76-8c5c-4b31-a90b-968a1b4ec403.jpg','1999-12-14','Travel ninja. Hardcore alcohol geek. Web fanatic. Passionate internet enthusiast.',0,'2019-07-26 19:37:42','2019-12-22 16:06:41',0,0,0,1,62,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(63,'Erica','Vitale','erica.vitale@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_a2289cd5-f2f2-44f1-80f9-86834819ddca.jpg','1992-07-26','Internet junkie. Total pop culture buff. Social media fanatic. Passionate alcoholaholic. Tv aficionado. Communicator. Zombie lover.',0,'2019-07-26 19:37:43','2019-12-22 16:06:40',0,0,0,1,63,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(64,'Софья','Никитинa','sofja.nikitina@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4e64bc89-8e82-4411-a8e3-173d2dbc8e44.jpg','1991-07-15','Writer. Zombie fan. Troublemaker. Certified explorer. Alcohol advocate. Friendly thinker. Beer maven. Food junkie.',0,'2019-07-26 19:37:44','2019-12-18 20:27:01',0,0,0,1,64,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(65,'Александра','Ермаковa','aleksandra.ermakova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_de3b4865-b3d7-4bfc-bbaa-08eed3396d05.jpg','1994-06-13','Problem solver. Pop culture fanatic. Tv buff. Travel lover. Thinker. General student.',0,'2019-07-26 19:37:45','2019-12-18 20:27:02',0,0,0,1,65,NULL,'2019-12-18 20:27:02',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(66,'Марина','Буркавцовa','marina.burkavtsova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4e3406e1-7d74-4d4b-8d52-b0a23e9a4763.jpg','1996-12-30','Tv lover. Coffee nerd. Hipster-friendly web geek. Social media fanatic. Beer practitioner.',0,'2019-07-26 19:37:46','2019-12-18 19:04:32',0,0,0,1,66,NULL,'2019-12-18 19:04:32',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(67,'Алина','Зиминa','alina.zimina@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8d91a646-2863-4d7e-bfc3-4dc17ecb8999.jpg','1999-09-13','Proud tv ninja. Travel fanatic. Infuriatingly humble entrepreneur. Thinker. Zombie enthusiast. Hipster-friendly music junkie. Internet fan.',0,'2019-07-26 19:37:47','2019-12-22 14:56:10',0,0,0,1,67,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(68,'Инесса','Зайцевa','inessa.zaytseva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_2196168f-2334-4e34-96fa-9d3750be2f64.jpg','1992-11-17','Friend of animals everywhere. Troublemaker. Proud travel maven. Evil twitter fan. Introvert. Internet fanatic.',0,'2019-07-26 19:37:48','2019-12-22 16:06:40',0,0,0,1,68,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(69,'Жанна','Юшковa','zhanna.jushkova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_cbe0be09-3931-450a-8eb1-e21ab91fa22a.jpg','1993-06-20','Coffee geek. Amateur zombie fan. Thinker. Pop culture evangelist. Web lover. Unapologetic bacon practitioner. Hipster-friendly organizer.',0,'2019-07-26 19:37:48','2019-07-26 19:37:48',0,0,0,1,69,NULL,'2019-07-26 19:37:48',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(70,'Валерия','Авдеевa','valerija.avdeeva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_e1779904-57c0-420c-9374-7480d45fa99e.jpg','1997-07-12','Unapologetic bacon specialist. Internet evangelist. Pop culture aficionado. Avid twitter expert. Food scholar. Troublemaker.',0,'2019-07-26 19:37:49','2019-12-18 20:27:01',0,0,0,1,70,NULL,'2019-12-18 20:27:01',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(71,'Оксана','Фёдоровa','oksana.fedorova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_48175559-8f1b-4a99-b427-263688d32352.jpg','1997-04-22','Typical pop culture buff. Analyst. Beeraholic. Freelance bacon junkie. Problem solver. Internet aficionado. Gamer.',0,'2019-07-26 19:37:50','2019-12-22 14:56:10',0,0,0,1,71,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(72,'Александра','Орловa','aleksandra.orlova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_90c32d10-abe3-4592-86e6-04b2c0a41156.jpg','1999-03-19','Prone to fits of apathy. Explorer. Total social media fanatic. Bacon scholar. Coffee expert. Alcohol evangelist. Travel trailblazer.',0,'2019-07-26 19:37:51','2019-07-26 19:37:51',0,0,0,1,72,NULL,'2019-07-26 19:37:51',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(73,'Софья','Кац','sofja.kats@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_2512c4ce-198c-4269-8143-d737aae13f76.jpg','1993-08-23','Extreme zombie nerd. Beeraholic. Friendly tv scholar. Freelance food ninja. Twitter specialist. Passionate bacon guru.',0,'2019-07-26 19:37:52','2019-12-18 19:55:16',0,0,0,1,73,NULL,'2019-12-18 19:55:16',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(74,'Снежана','Угловa','snezhana.uglova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b100174f-d07c-40fe-81b2-607d2d47f175.jpg','1989-12-04','Beer geek. Subtly charming web aficionado. Reader. Professional tv fanatic. Social media lover.',0,'2019-07-26 19:37:53','2019-12-18 19:04:33',0,0,0,1,74,NULL,'2019-12-18 19:04:33',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(75,'Алина','Царёвa','alina.tsareva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4a787a16-e6c3-44ff-916b-8126f40dd870.jpg','1991-09-14','Student. Troublemaker. Communicator. Bacon scholar. Organizer. Problem solver. Creator. Zombie enthusiast. Certified analyst.',0,'2019-07-26 19:37:54','2019-12-18 20:27:02',0,0,0,1,75,NULL,'2019-12-18 20:27:02',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(76,'Елизавета','Щёголевa','elizaveta.schegoleva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_93e57a58-60da-4624-ba1f-6af758967a3f.jpg','1989-11-01','Organizer. Bacon maven. Lifelong food evangelist. Troublemaker. Certified zombie lover.',0,'2019-07-26 19:37:55','2019-12-22 14:56:10',0,0,0,1,76,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(77,'Арина','Абрамовa','arina.abramova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4b163cfa-0e38-4dea-9945-ebfcb326f24d.jpg','1991-02-10','Hardcore music evangelist. Wannabe troublemaker. Twitter guru. Travel expert. Freelance tv maven.',0,'2019-07-26 19:37:56','2019-12-18 19:04:33',0,0,0,1,77,NULL,'2019-12-18 19:04:33',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(78,'Зина','Яковлевa','zina.jakovleva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_1032ca91-6fb5-41e5-803a-3d1158f72454.jpg','1990-07-07','Beer nerd. Student. Pop culture junkie. Typical food practitioner. Social media aficionado.',0,'2019-07-26 19:37:57','2019-12-22 16:06:41',0,0,0,1,78,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(79,'Эльза','Поповa','elza.popova@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_a4619bae-52cf-4a8f-89b0-e7d8db92bbb0.jpg','1990-06-07','Proud social media enthusiast. Web expert. Troublemaker. Avid travel fan.',0,'2019-07-26 19:37:58','2019-07-26 19:37:58',0,0,0,1,79,NULL,'2019-07-26 19:37:58',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(80,'Кристина','Царёвa','kristina.tsareva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_010c87b6-f725-470f-ab65-8c83735cfd2b.jpg','1998-12-27','Student. Web ninja. Bacon geek. Zombie junkie. Avid gamer. Twitter fanatic. Music trailblazer. Pop culture guru. Travel nerd. Amateur tv enthusiast.',0,'2019-07-26 19:37:59','2019-12-18 20:27:02',0,0,0,1,80,NULL,'2019-12-18 20:27:02',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(81,'Софья','Ермолинa','sofja.ermolina@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_d74bd2e2-1904-49a0-9578-aa5945e9d194.jpg','1997-11-27','Entrepreneur. Music guru. Bacon enthusiast. Zombie trailblazer. Coffee buff. Internet fanatic. Gamer. Travel fan.',0,'2019-07-26 19:38:00','2019-12-18 19:22:03',0,0,0,1,81,NULL,'2019-12-18 19:22:03',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(82,'Тамара','Лаврентьевa','tamara.lavrenteva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_cd5fd16d-b3c7-40d2-8fd3-3e8430dd6013.jpg','1990-03-12','General travel expert. Analyst. Beer junkie. Tv lover. Music evangelist. Avid introvert.',0,'2019-07-26 19:38:00','2019-12-22 14:56:10',0,0,0,1,82,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(83,'Галина','Дмитриевa','galina.dmitrieva@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8fd049fd-2843-4240-93c3-eb871391312b.jpg','1999-07-14','Internet specialist. Hipster-friendly twitter junkie. Social media fan. Proud music evangelist.',0,'2019-07-26 19:38:01','2019-12-22 16:06:41',0,0,0,1,83,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(84,'Aimee','Marshall','aimee.marshall@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_078c4c36-a3c3-4103-934f-6e3e36bdbb8e.jpg','1994-11-14','Professional bacon lover. Total food nerd. Beer geek. Social media scholar. Student. General gamer.',0,'2019-07-26 19:38:02','2019-12-18 19:04:33',0,0,0,1,84,NULL,'2019-12-18 19:04:33',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(85,'Mia','Fox','mia.fox@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_29e3355e-f09c-445a-9d79-215aa8052dc8.jpg','1993-09-01','Travel advocate. Reader. Organizer. Tv enthusiast. Alcohol aficionado. Writer. Student.',0,'2019-07-26 19:38:03','2019-07-26 19:38:03',0,0,0,1,85,NULL,'2019-07-26 19:38:03',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(86,'Molly','Morgan','molly.morgan@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_17bebeba-f924-4b5d-bf7c-03d57c306759.jpg','1992-04-27','Internet advocate. Social media maven. Bacon lover. Entrepreneur. Pop culture fanatic.',0,'2019-07-26 19:38:04','2019-12-22 16:06:41',0,0,0,1,86,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(87,'Kiera','Price','kiera.price@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c30ffd69-9829-4659-99b4-cd53544b5831.jpg','1992-10-12','Zombie enthusiast. Troublemaker. Travel ninja. Proud beer advocate. Unable to type with boxing gloves on. Lifelong creator.',0,'2019-07-26 19:38:05','2019-12-18 19:22:03',0,0,0,1,87,NULL,'2019-12-18 19:22:03',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(88,'Millie','Evans','millie.evans@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_25a6f108-78fc-4480-b83c-fa238af26faa.jpg','1996-11-09','Alcohol lover. Internet nerd. Twitter evangelist. Incurable explorer. Creator. Food ninja. Bacon guru. Friendly music maven. Coffee aficionado. Student.',0,'2019-07-26 19:38:05','2019-12-22 16:06:41',0,0,0,1,88,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(89,'Faith','Thomas','faith.thomas@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_a82546dd-0b92-47a8-8379-d9d4358e01db.jpg','1994-05-13','Devoted beer guru. Introvert. Web practitioner. Tv aficionado. Freelance coffee fanatic.',0,'2019-07-26 19:38:06','2019-12-22 16:06:41',0,0,0,1,89,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(90,'Amy','Allen','amy.allen@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_54eb83d1-25f1-4002-8d00-e5b9baa33e71.jpg','1994-06-17','Subtly charming thinker. Explorer. Zombie practitioner. Pop culture lover. Gamer. Web scholar.',0,'2019-07-26 19:38:07','2019-12-22 16:06:40',0,0,0,1,90,NULL,'2019-12-22 16:06:40',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(91,'Amber','Thomas','amber.thomas@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b5f7bcfd-19fd-4997-9e78-3118c2bda861.jpg','1993-07-01','Tv maven. Proud alcohol enthusiast. Internet specialist. Problem solver. Bacon guru.',0,'2019-07-26 19:38:08','2019-12-22 16:06:41',0,0,0,1,91,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(92,'Alexandra','Davis','alexandra.davis@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c78e0969-b55d-407e-969a-c7818a0e781c.jpg','1996-09-01','Food advocate. Student. Social media lover. Zombie expert. Internet fan. Certified problem solver. Coffee aficionado. Music scholar.',0,'2019-07-26 19:38:09','2019-12-22 16:06:41',0,0,0,1,92,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(93,'Alicia','Saunders','alicia.saunders@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_9e0861bd-bceb-4c12-8da8-fa0eb6f0adbe.jpg','1996-10-22','Subtly charming writer. Typical travel specialist. Organizer. Pop culture ninja.',0,'2019-07-26 19:38:09','2019-12-22 16:06:41',0,0,0,1,93,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(94,'Poppy','Knight','poppy.knight@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_ac027e0a-220a-46b1-af7d-ed8d58d5db64.jpg','1996-02-26','Web fan. Social media trailblazer. Amateur zombie lover. Music aficionado. Beer advocate. Certified twitter scholar. Tv geek. Coffee specialist. Alcohol junkie.',0,'2019-07-26 19:38:10','2019-12-22 16:06:41',0,0,0,1,94,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(95,'Amber','Wright','amber.wright@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b3b92d72-d9db-4e19-838c-a8bec9f79b45.jpg','1999-06-16','Tv nerd. Devoted music trailblazer. Amateur thinker. Unable to type with boxing gloves on.',0,'2019-07-26 19:38:11','2019-12-22 16:06:41',0,0,0,1,95,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(96,'Scarlett','Griffiths','scarlett.griffiths@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_8df8ce0f-afce-44fd-aa9a-533f46a9f6a9.jpg','1995-12-17','Zombie nerd. Gamer. Twitter geek. Evil thinker. Hardcore bacon expert. Beer fanatic. Pop culture junkie. Music lover.',0,'2019-07-26 19:38:12','2019-12-18 20:27:02',0,0,0,1,96,NULL,'2019-12-18 20:27:02',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(97,'Lilly','Anderson','lilly.anderson@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_7c5271ad-ddee-43e6-b34d-12c18579594a.jpg','1999-01-16','Web nerd. Coffee scholar. Bacon trailblazer. Infuriatingly humble writer. Gamer. Tv practitioner.',0,'2019-07-26 19:38:13','2019-12-22 16:06:41',0,0,0,1,97,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(98,'Isobel','Richardson','isobel.richardson@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_778103a4-35d3-40ce-9170-ea1f90f35064.jpg','1991-03-18','Award-winning food geek. Tv fanatic. Travel expert. Proud beer trailblazer. Organizer. Bacon enthusiast. Communicator. Internet aficionado.',0,'2019-07-26 19:38:14','2019-12-22 14:56:10',0,0,0,1,98,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(99,'Emily','Cox','emily.cox@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_1272ad7f-aa84-4faa-adae-303511f7f1a9.jpg','1993-10-11','Unable to type with boxing gloves on. Beer expert. Troublemaker. Freelance introvert. Web advocate.',0,'2019-07-26 19:38:15','2019-12-18 19:55:16',0,0,0,1,99,NULL,'2019-12-18 19:55:16',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(100,'Eloise','Thomson','eloise.thomson@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c3bbca8c-6b0a-490c-b6ae-ea1a472d2e55.jpg','1998-04-01','Food guru. Subtly charming troublemaker. Writer. Infuriatingly humble reader. General internet geek. Zombie ninja.',0,'2019-07-26 19:38:15','2019-12-18 19:22:03',0,0,0,1,100,NULL,'2019-12-18 19:22:03',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(101,'Alexandra','Richardson','alexandra.richardson@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_5ec3ad36-3e5d-4874-b3f2-c99e9ac5afea.jpg','1996-08-15','Music fanatic. Student. Pop culture expert. Bacon enthusiast. Food geek. Tv guru. Devoted creator.',0,'2019-07-26 19:38:16','2019-12-22 16:06:41',0,0,0,1,101,NULL,'2019-12-22 16:06:41',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(102,'Evie','Lloyd','evie.lloyd@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_2cb477fe-c6a3-4577-a4e7-e45cdc55bd32.jpg','1992-07-13','Student. Gamer. General travel specialist. Food aficionado. Award-winning organizer. Evil analyst.',0,'2019-07-26 19:38:17','2019-12-22 14:56:10',0,0,0,1,102,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(103,'Alice','King','alice.king@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_1a1e6817-ddad-4440-a789-421226015eea.jpg','1993-04-20','Food geek. Infuriatingly humble pop culture fanatic. Certified music ninja. Communicator. Organizer. Web buff.',0,'2019-07-26 19:38:18','2019-12-22 14:56:10',0,0,0,1,103,NULL,'2019-12-22 14:56:10',5,'2019-08-10 14:58:20',0,'2019-09-08 15:36:06',NULL),(104,'Salvatore','Rinaldi','salvatore.rinaldi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_469316fc-e022-4ea4-958a-8ee69ef49419.jpg','1994-02-26','Avid bacon trailblazer. Social media guru. Beer expert. Twitter nerd. Alcohol ninja. Web aficionado.',1,'2019-08-10 17:48:34','2019-12-18 20:27:01',0,0,0,1,104,NULL,'2019-12-18 20:27:01',0,'2019-08-10 17:48:33',0,'2019-09-08 15:36:06',NULL),(105,'Davide','Caputo','davide.caputo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4c8a75d5-7da2-447c-9b30-1f4f6815b7a3.jpg','1992-11-13','Organizer. Wannabe beer ninja. Future teen idol. Creator. Professional tv nerd.',1,'2019-08-10 17:48:37','2019-12-22 16:06:41',0,0,0,1,105,NULL,'2019-12-22 16:06:41',0,'2019-08-10 17:48:37',0,'2019-09-08 15:36:06',NULL),(106,'Alex','Silvestri','alex.silvestri@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_f6d8dcfc-09de-4c3d-8d40-15f45d238e80.jpg','1995-02-24','Wannabe twitter geek. Beer scholar. Zombieaholic. Food expert. Alcohol specialist. Social media lover. Web fanatic.',1,'2019-08-10 17:48:39','2019-12-22 16:06:41',0,0,0,1,106,NULL,'2019-12-22 16:06:41',0,'2019-08-10 17:48:39',0,'2019-09-08 15:36:06',NULL),(107,'Davide','Sala','davide.sala@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_76349ac9-0111-456c-83ba-6872de180c6f.jpg','1991-10-11','Beer scholar. Amateur problem solver. Web ninja. Typical alcohol junkie. Incurable coffee buff. Reader. Twitter fanatic.',1,'2019-08-10 17:48:40','2019-12-22 14:56:10',0,0,0,1,107,NULL,'2019-12-22 14:56:10',0,'2019-08-10 17:48:40',0,'2019-09-08 15:36:06',NULL),(108,'Gianluca','Gentile','gianluca.gentile@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_d1666f7b-848f-4a72-b551-807931c6ffc6.jpg','1992-10-22','Social media aficionado. Communicator. Organizer. Alcohol specialist. Twitter buff. Introvert. Award-winning writer. Web scholar.',1,'2019-08-10 17:48:41','2019-12-18 19:55:15',0,0,0,1,108,NULL,'2019-12-18 19:55:15',0,'2019-08-10 17:48:41',0,'2019-09-08 15:36:06',NULL),(109,'Fabio','Guerra','fabio.guerra@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_c6692f0f-d6f1-45f4-aa19-5b5cd06ca4c3.jpg','1990-11-13','Organizer. Evil beer fan. Explorer. Music ninja. Pop culture scholar. Food lover.',1,'2019-08-10 17:48:42','2019-12-18 20:27:02',0,0,0,1,109,NULL,'2019-12-18 20:27:02',0,'2019-08-10 17:48:42',0,'2019-09-08 15:36:06',NULL),(110,'Fabio','Coppola','fabio.coppola@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_a7092f0e-fd28-42f0-b9d7-db8c78acade6.jpg','1994-08-21','Coffee fanatic. Internet scholar. Organizer. Food fan. Explorer. Subtly charming beer advocate.',1,'2019-08-10 17:48:43','2019-12-18 20:27:02',0,0,0,1,110,NULL,'2019-12-18 20:27:02',0,'2019-08-10 17:48:43',0,'2019-09-08 15:36:06',NULL),(111,'Pietro','Salvi','pietro.salvi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_74e54b62-1a52-48a3-a682-ea42811f7936.jpg','1992-11-27','Devoted twitter fan. Professional reader. Zombie lover. Beer practitioner. Incurable creator.',1,'2019-08-10 17:48:44','2019-12-22 16:06:41',0,0,0,1,111,NULL,'2019-12-22 16:06:41',0,'2019-08-10 17:48:44',0,'2019-09-08 15:36:06',NULL),(112,'Paolo','Rossi','paolo.rossi@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_b0b7687d-58b5-4224-893f-1c471e1c1cba.jpg','1995-07-29','Alcohol aficionado. Lifelong creator. Entrepreneur. Amateur food fan. Pop culture specialist. Proud reader. Thinker.',1,'2019-08-10 17:48:45','2019-12-22 16:06:41',0,0,0,1,112,NULL,'2019-12-22 16:06:41',0,'2019-08-10 17:48:45',0,'2019-09-08 15:36:06',NULL),(113,'Andrea','Longo','andrea.longo@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_98dd4a2e-a2fa-4879-8a81-1b0804e81a9b.jpg','1994-11-18','Social media expert. Evil entrepreneur. Lifelong zombie lover. Unable to type with boxing gloves on. Proud foodaholic. Amateur writer.',1,'2019-08-10 17:48:46','2019-12-18 20:27:02',0,0,0,1,113,NULL,'2019-12-18 20:27:02',0,'2019-08-10 17:48:46',0,'2019-09-08 15:36:06',NULL),(114,'Олег','Юрченко','oleg.jurchenko@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_bbe8017b-5c58-4269-97a4-c225047b58fd.jpg','1995-02-25','Troublemaker. Tv specialist. Typical writer. Beer fanatic. Creator. Web geek. Zombie maven.',1,'2019-08-10 17:48:47','2019-12-22 14:56:10',0,0,0,1,114,NULL,'2019-12-22 14:56:10',0,'2019-08-10 17:48:47',0,'2019-09-08 15:36:06',NULL),(115,'Оскар','Харинов','oskar.harinov@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_518db44e-f081-476a-9362-3c6ced34ab25.jpg','1993-05-26','Food buff. Analyst. Tv ninja. Future teen idol. Reader. Professional gamer. Hipster-friendly alcohol junkie.',1,'2019-08-10 17:48:48','2019-12-22 16:06:41',0,0,0,1,115,NULL,'2019-12-22 16:06:41',0,'2019-08-10 17:48:48',0,'2019-09-08 15:36:06',NULL),(116,'Владимир','Цивилёв','vladimir.tsivilev@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_6099cc48-a8a1-4565-9a9a-dc74c468e1c0.jpg','1995-03-04','Alcohol fanatic. Webaholic. Social media lover. Thinker. Wannabe music junkie.',1,'2019-08-10 17:48:49','2019-12-22 16:06:40',0,0,0,1,116,NULL,'2019-12-22 16:06:40',0,'2019-08-10 17:48:49',0,'2019-09-08 15:36:06',NULL),(117,'Олег','Фомин','oleg.fomin@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_9f13ef61-7f77-42ba-8072-64e1d3cab358.jpg','1992-06-19','Bacon practitioner. Pop culture junkie. Writer. Extreme reader. Food geek. Tv buff. Twitter maven. Internet lover. Entrepreneur.',1,'2019-08-10 17:48:51','2019-12-22 16:06:41',0,0,0,1,117,NULL,'2019-12-22 16:06:41',0,'2019-08-10 17:48:51',0,'2019-09-08 15:36:06',NULL),(118,'Андрей','Якушев','andrey.jakushev@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_af471d2f-ed82-4133-a3c4-593ef3f52955.jpg','1992-04-19','Alcohol scholar. Web trailblazer. Entrepreneur. Music guru. Coffee junkie. Social media buff.',1,'2019-08-10 17:48:52','2019-12-18 19:55:16',0,0,0,1,118,NULL,'2019-12-18 19:55:16',0,'2019-08-10 17:48:52',0,'2019-09-08 15:36:06',NULL),(119,'Bailey','Murphy','bailey.murphy@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_a0a88af7-4702-42c7-bcaf-8a1d4d22bef6.jpg','1991-09-16','Hipster-friendly alcohol nerd. Food geek. Explorer. Pop culture maven. Tv guru. Creator. Friendly twitter evangelist.',1,'2019-08-10 17:48:53','2019-12-22 16:06:40',0,0,0,1,119,NULL,'2019-12-22 16:06:40',0,'2019-08-10 17:48:53',0,'2019-09-08 15:36:06',NULL),(120,'Joshua','Mason','joshua.mason@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_d74e7019-b860-4dac-b64c-7e444e0f24c0.jpg','1999-07-29','General internet geek. Avid zombie practitioner. Tv aficionado. Food specialist. Beer expert. Twitter fan. Subtly charming music scholar.',1,'2019-08-10 17:48:53','2019-08-10 17:48:53',0,0,0,1,120,NULL,'2019-08-10 17:48:53',0,'2019-08-10 17:48:53',0,'2019-09-08 15:36:06',NULL),(121,'Zak','Wright','zak.wright@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_4815f728-9857-4f4b-8e8e-1b95d272d568.jpg','1989-02-11','Pop culture ninja. Amateur creator. Total analyst. Friendly web specialist. Social media buff. Infuriatingly humble thinker. Travel fanatic.',1,'2019-08-10 17:48:54','2019-08-10 17:48:54',0,0,0,1,121,NULL,'2019-08-10 17:48:54',0,'2019-08-10 17:48:54',0,'2019-09-08 15:36:06',NULL),(122,'Liam','Robinson','liam.robinson@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_3efbed10-2bec-4862-bacc-f6feaeea8733.jpg','1990-08-10','Unapologetic music fan. Food guru. Travel specialist. Social media practitioner.',1,'2019-08-10 17:48:56','2019-12-18 20:27:01',0,0,0,1,122,NULL,'2019-12-18 20:27:01',0,'2019-08-10 17:48:56',0,'2019-09-08 15:36:06',NULL),(123,'Michael','Kelly','michael.kelly@email.com','$2a$08$EfoMXblxEFhBxSCOxI/bju0G4oVwGgtCig6INKKOJbchvNw5XhoTW','th_01681c15-9cb5-4a64-ab2e-5cdc391e4669.jpg','1990-06-19','Subtly charming troublemaker. Communicator. Analyst. Pop culture scholar. Total coffee buff.',1,'2019-08-10 17:48:57','2019-08-10 17:48:57',0,0,0,1,123,NULL,'2019-08-10 17:48:57',0,'2019-08-10 17:48:57',0,'2019-09-08 15:36:06',NULL);
/*!40000 ALTER TABLE `AK_USER` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_USER_AUTHORITY`
--

DROP TABLE IF EXISTS `AK_USER_AUTHORITY`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_USER_AUTHORITY` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `USER_ID` int NOT NULL,
  `AUTHORITY_ID` int NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_AUTHORITY_USER_ID_AUTHORITY_ID_U` (`USER_ID`,`AUTHORITY_ID`),
  KEY `USER_AUTHORITY_AUTHORITY_ID_FK` (`AUTHORITY_ID`),
  CONSTRAINT `USER_AUTHORITY_AUTHORITY_ID_FK` FOREIGN KEY (`AUTHORITY_ID`) REFERENCES `AK_AUTHORITY` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `USER_AUTHORITY_USER_ID_FK` FOREIGN KEY (`USER_ID`) REFERENCES `AK_USER` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=340 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_USER_AUTHORITY`
--

LOCK TABLES `AK_USER_AUTHORITY` WRITE;
/*!40000 ALTER TABLE `AK_USER_AUTHORITY` DISABLE KEYS */;
INSERT INTO `AK_USER_AUTHORITY` VALUES (1,1,1),(2,1,2),(3,2,1),(4,4,1),(5,5,1),(6,6,1),(7,7,1),(8,8,1),(9,9,1),(10,10,1),(11,11,1),(12,12,1),(13,13,1),(14,14,1),(15,15,1),(16,16,1),(17,17,1),(18,18,1),(19,19,1),(20,20,1),(21,21,1),(22,22,1),(23,23,1),(24,24,1),(25,25,1),(26,26,1),(27,27,1),(28,28,1),(29,29,1),(30,30,1),(31,31,1),(32,32,1),(33,33,1),(34,34,1),(35,35,1),(36,36,1),(37,37,1),(38,38,1),(39,39,1),(40,40,1),(41,41,1),(42,42,1),(43,43,1),(44,44,1),(45,45,1),(46,46,1),(47,47,1),(48,48,1),(49,49,1),(50,50,1),(51,51,1),(52,52,1),(53,53,1),(54,54,1),(55,55,1),(56,56,1),(57,57,1),(58,58,1),(59,59,1),(60,60,1),(61,61,1),(62,62,1),(63,63,1),(64,64,1),(65,65,1),(66,66,1),(67,67,1),(68,68,1),(69,69,1),(70,70,1),(71,71,1),(72,72,1),(73,73,1),(74,74,1),(75,75,1),(76,76,1),(77,77,1),(78,78,1),(79,79,1),(80,80,1),(81,81,1),(82,82,1),(83,83,1),(84,84,1),(85,85,1),(86,86,1),(87,87,1),(88,88,1),(89,89,1),(90,90,1),(91,91,1),(92,92,1),(93,93,1),(94,94,1),(95,95,1),(96,96,1),(97,97,1),(98,98,1),(99,99,1),(100,100,1),(101,101,1),(102,102,1),(103,103,1),(104,104,1),(105,105,1),(106,106,1),(107,107,1),(108,108,1),(109,109,1),(110,110,1),(111,111,1),(112,112,1),(113,113,1),(114,114,1),(115,115,1),(116,116,1),(117,117,1),(118,118,1),(119,119,1),(120,120,1),(121,121,1),(122,122,1),(123,123,1);
/*!40000 ALTER TABLE `AK_USER_AUTHORITY` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AK_USER_IMAGE`
--

DROP TABLE IF EXISTS `AK_USER_IMAGE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `AK_USER_IMAGE` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `USER_ID` int NOT NULL,
  `IMAGE_NAME` varchar(255) DEFAULT NULL,
  `POSITION` int NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_IMAGE_USER_ID_POSITION_U` (`USER_ID`,`POSITION`),
  CONSTRAINT `USER_IMAGE_USER_ID_FK` FOREIGN KEY (`USER_ID`) REFERENCES `AK_USER` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=341 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AK_USER_IMAGE`
--

LOCK TABLES `AK_USER_IMAGE` WRITE;
/*!40000 ALTER TABLE `AK_USER_IMAGE` DISABLE KEYS */;
INSERT INTO `AK_USER_IMAGE` VALUES (3,4,'83105e98-8a29-4d99-848e-e443043b4ec5.jpg',1),(4,5,'58cc76b7-22e1-49af-8523-ecc08378c88d.jpg',1),(5,6,'10a9f943-eafb-42c3-88c9-9f6ce7afcbb9.jpg',1),(6,7,'5db563d3-c055-4331-b6d2-e5085cd2e7f9.jpg',1),(7,8,'c871508e-ffb5-4649-9f3c-0c645c19b5b6.jpg',1),(8,9,'7e673b43-2229-481d-b05a-82e42e0767e5.jpg',1),(9,10,'8894b032-4b4b-490b-ace3-035716c59320.jpg',1),(10,11,'85708b2e-d33b-4a5b-8d3d-4ffd4510b70e.jpg',1),(11,12,'66b5ef8b-2c19-49be-9ff6-4f47896d4770.jpg',1),(12,13,'ae2ebed8-cc83-4f32-9d32-35b6ad12b32b.jpg',1),(13,14,'c076980f-9f53-4e70-aed2-3c73ca628b7d.jpg',1),(14,15,'788f13bb-d62e-4715-9433-23fab458af9a.jpg',1),(15,16,'b18add39-d86e-4776-8f8a-89b5f6e4b7e9.jpg',1),(16,17,'5a2b7e65-9c27-4181-a977-d606eb11b56a.jpg',1),(17,18,'62e63f92-30d5-445f-a119-baec821e6c36.jpg',1),(18,19,'e9af5e2d-6e6e-4a4a-b81d-420ee8ec9b8c.jpg',1),(19,20,'b934af00-5c1e-4585-abca-f192a1c17e72.jpg',1),(20,21,'775bd6c1-0587-4b3c-b021-fc9089fe4158.jpg',1),(21,22,'7d5f0ccf-bfcd-4a6e-8d2a-2e193673d55a.jpg',1),(22,23,'2a561fcf-df3a-4c2f-9c42-a1ec246c6178.jpg',1),(23,24,'2a681a33-8c62-4856-83a9-1cc5f7968056.jpg',1),(24,25,'a813f505-c9cc-4e6c-b2a4-85a4ee7cbe2d.jpg',1),(25,26,'45d528aa-ac9f-42b2-b1f3-b38e4613b4a2.jpg',1),(26,27,'292ba292-61eb-4145-b60d-09de1db6bfb4.jpg',1),(27,28,'c004ea7b-81e9-499f-a651-01927be3ec60.jpg',1),(28,29,'4ff53492-cd04-4c51-9138-4166281f2b54.jpg',1),(29,30,'5b790bc7-079f-44fc-b874-56cd8afc4d18.jpg',1),(30,31,'19c1a48e-17a2-46b0-a337-c71745d2a0f9.jpg',1),(31,32,'9123c900-aba0-47f7-8a42-189a6afbd533.jpg',1),(32,33,'40b3ad4b-2f2e-499d-b2e0-434b77091a2d.jpg',1),(33,34,'5849d78f-41f8-4f97-80f0-47c0849c7c97.jpg',1),(34,35,'d7f1a2da-33c9-4ccb-8db6-d66026c6056d.jpg',1),(35,36,'e35ed960-ec4b-4a99-93e8-2daf35f8a32c.jpg',1),(36,37,'709504b5-8ded-4147-8af0-0f2ba121eec7.jpg',1),(37,38,'db8d7a4e-d78a-48fe-a03b-11fa044a28ed.jpg',1),(38,39,'c61064f8-ab9c-4dff-889f-88d8df0f1220.jpg',1),(39,40,'57af94c8-3d45-49c4-8f35-a8ef9685c12d.jpg',1),(40,41,'0e1f3a8c-a7c4-4863-ae96-ec89c6df47f5.jpg',1),(41,42,'cad719c9-5e92-4c0f-b087-f597b3ef7149.jpg',1),(42,43,'139fe53a-0947-4f54-a279-22b8bd6063bb.jpg',1),(43,44,'48a67e8f-faab-4c8a-8372-6ec26a3530ce.jpg',1),(44,45,'8c8ea25c-737d-40f9-959f-c4f20cfdad91.jpg',1),(45,46,'ed43a885-ca09-42df-9833-384798863b91.jpg',1),(46,47,'b7c2e804-6b65-43bf-82ea-82cf3953dd6d.jpg',1),(47,48,'08a3d188-18e8-4716-ba04-d186e707d020.jpg',1),(48,49,'32b2985d-7233-4d61-bb84-3920f4ff5ae5.jpg',1),(49,50,'c97e9ba5-937c-41f0-b03a-18bf7bc156f8.jpg',1),(50,51,'28e91472-64fa-4dd8-a113-cedcee9a8745.jpg',1),(51,52,'72663beb-95a1-445f-a25b-f341bab2adab.jpg',1),(52,53,'0e2a05b8-591e-4633-bb44-074eafec2f62.jpg',1),(53,54,'de15dfcb-3338-4e8d-9888-b428d4055ea9.jpg',1),(54,55,'0d319934-314e-482e-af00-992dc544166c.jpg',1),(55,56,'8019941a-1947-4296-88da-f180d3faa0e7.jpg',1),(56,57,'5321a0b0-3c10-443b-a1a9-61ba7a6da1bc.jpg',1),(57,58,'f9460665-8144-43db-bb0b-7f90e5aead98.jpg',1),(58,59,'975ee4b5-b983-4160-aed5-ecb5e405c221.jpg',1),(59,60,'e0b9f7eb-4a76-4672-a334-0d24ea881068.jpg',1),(60,61,'d7c92199-fc35-442e-8586-917ef00be4ea.jpg',1),(61,62,'8e2a8f76-8c5c-4b31-a90b-968a1b4ec403.jpg',1),(62,63,'a2289cd5-f2f2-44f1-80f9-86834819ddca.jpg',1),(63,64,'4e64bc89-8e82-4411-a8e3-173d2dbc8e44.jpg',1),(64,65,'de3b4865-b3d7-4bfc-bbaa-08eed3396d05.jpg',1),(65,66,'4e3406e1-7d74-4d4b-8d52-b0a23e9a4763.jpg',1),(66,67,'8d91a646-2863-4d7e-bfc3-4dc17ecb8999.jpg',1),(67,68,'2196168f-2334-4e34-96fa-9d3750be2f64.jpg',1),(68,69,'cbe0be09-3931-450a-8eb1-e21ab91fa22a.jpg',1),(69,70,'e1779904-57c0-420c-9374-7480d45fa99e.jpg',1),(70,71,'48175559-8f1b-4a99-b427-263688d32352.jpg',1),(71,72,'90c32d10-abe3-4592-86e6-04b2c0a41156.jpg',1),(72,73,'2512c4ce-198c-4269-8143-d737aae13f76.jpg',1),(73,74,'b100174f-d07c-40fe-81b2-607d2d47f175.jpg',1),(74,75,'4a787a16-e6c3-44ff-916b-8126f40dd870.jpg',1),(75,76,'93e57a58-60da-4624-ba1f-6af758967a3f.jpg',1),(76,77,'4b163cfa-0e38-4dea-9945-ebfcb326f24d.jpg',1),(77,78,'1032ca91-6fb5-41e5-803a-3d1158f72454.jpg',1),(78,79,'a4619bae-52cf-4a8f-89b0-e7d8db92bbb0.jpg',1),(79,80,'010c87b6-f725-470f-ab65-8c83735cfd2b.jpg',1),(80,81,'d74bd2e2-1904-49a0-9578-aa5945e9d194.jpg',1),(81,82,'cd5fd16d-b3c7-40d2-8fd3-3e8430dd6013.jpg',1),(82,83,'8fd049fd-2843-4240-93c3-eb871391312b.jpg',1),(83,84,'078c4c36-a3c3-4103-934f-6e3e36bdbb8e.jpg',1),(84,85,'29e3355e-f09c-445a-9d79-215aa8052dc8.jpg',1),(85,86,'17bebeba-f924-4b5d-bf7c-03d57c306759.jpg',1),(86,87,'c30ffd69-9829-4659-99b4-cd53544b5831.jpg',1),(87,88,'25a6f108-78fc-4480-b83c-fa238af26faa.jpg',1),(88,89,'a82546dd-0b92-47a8-8379-d9d4358e01db.jpg',1),(89,90,'54eb83d1-25f1-4002-8d00-e5b9baa33e71.jpg',1),(90,91,'b5f7bcfd-19fd-4997-9e78-3118c2bda861.jpg',1),(91,92,'c78e0969-b55d-407e-969a-c7818a0e781c.jpg',1),(92,93,'9e0861bd-bceb-4c12-8da8-fa0eb6f0adbe.jpg',1),(93,94,'ac027e0a-220a-46b1-af7d-ed8d58d5db64.jpg',1),(94,95,'b3b92d72-d9db-4e19-838c-a8bec9f79b45.jpg',1),(95,96,'8df8ce0f-afce-44fd-aa9a-533f46a9f6a9.jpg',1),(96,97,'7c5271ad-ddee-43e6-b34d-12c18579594a.jpg',1),(97,98,'778103a4-35d3-40ce-9170-ea1f90f35064.jpg',1),(98,99,'1272ad7f-aa84-4faa-adae-303511f7f1a9.jpg',1),(99,100,'c3bbca8c-6b0a-490c-b6ae-ea1a472d2e55.jpg',1),(100,101,'5ec3ad36-3e5d-4874-b3f2-c99e9ac5afea.jpg',1),(101,102,'2cb477fe-c6a3-4577-a4e7-e45cdc55bd32.jpg',1),(102,103,'1a1e6817-ddad-4440-a789-421226015eea.jpg',1),(104,104,'469316fc-e022-4ea4-958a-8ee69ef49419.jpg',1),(105,105,'4c8a75d5-7da2-447c-9b30-1f4f6815b7a3.jpg',1),(106,106,'f6d8dcfc-09de-4c3d-8d40-15f45d238e80.jpg',1),(107,107,'76349ac9-0111-456c-83ba-6872de180c6f.jpg',1),(108,108,'d1666f7b-848f-4a72-b551-807931c6ffc6.jpg',1),(109,109,'c6692f0f-d6f1-45f4-aa19-5b5cd06ca4c3.jpg',1),(110,110,'a7092f0e-fd28-42f0-b9d7-db8c78acade6.jpg',1),(111,111,'74e54b62-1a52-48a3-a682-ea42811f7936.jpg',1),(112,112,'b0b7687d-58b5-4224-893f-1c471e1c1cba.jpg',1),(113,113,'98dd4a2e-a2fa-4879-8a81-1b0804e81a9b.jpg',1),(114,114,'bbe8017b-5c58-4269-97a4-c225047b58fd.jpg',1),(115,115,'518db44e-f081-476a-9362-3c6ced34ab25.jpg',1),(116,116,'6099cc48-a8a1-4565-9a9a-dc74c468e1c0.jpg',1),(117,117,'9f13ef61-7f77-42ba-8072-64e1d3cab358.jpg',1),(118,118,'af471d2f-ed82-4133-a3c4-593ef3f52955.jpg',1),(119,119,'a0a88af7-4702-42c7-bcaf-8a1d4d22bef6.jpg',1),(120,120,'d74e7019-b860-4dac-b64c-7e444e0f24c0.jpg',1),(121,121,'4815f728-9857-4f4b-8e8e-1b95d272d568.jpg',1),(122,122,'3efbed10-2bec-4862-bacc-f6feaeea8733.jpg',1),(123,123,'01681c15-9cb5-4a64-ab2e-5cdc391e4669.jpg',1);
/*!40000 ALTER TABLE `AK_USER_IMAGE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_access_token`
--

DROP TABLE IF EXISTS `oauth_access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth_access_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` varbinary(1024) DEFAULT NULL,
  `authentication_id` varchar(256) NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  `authentication` varbinary(4096) DEFAULT NULL,
  `refresh_token` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_access_token`
--

LOCK TABLES `oauth_access_token` WRITE;
/*!40000 ALTER TABLE `oauth_access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_access_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_approvals`
--

DROP TABLE IF EXISTS `oauth_approvals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth_approvals` (
  `userId` varchar(256) DEFAULT NULL,
  `clientId` varchar(256) DEFAULT NULL,
  `scope` varchar(256) DEFAULT NULL,
  `status` varchar(10) DEFAULT NULL,
  `expiresAt` timestamp NULL DEFAULT NULL,
  `lastModifiedAt` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_approvals`
--

LOCK TABLES `oauth_approvals` WRITE;
/*!40000 ALTER TABLE `oauth_approvals` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_approvals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_client_details`
--

DROP TABLE IF EXISTS `oauth_client_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `client_secret` varchar(255) NOT NULL,
  `web_server_redirect_uri` varchar(2048) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `access_token_validity` int DEFAULT NULL,
  `refresh_token_validity` int DEFAULT NULL,
  `resource_ids` varchar(1024) DEFAULT NULL,
  `authorized_grant_types` varchar(1024) DEFAULT NULL,
  `authorities` varchar(1024) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_client_details`
--

LOCK TABLES `oauth_client_details` WRITE;
/*!40000 ALTER TABLE `oauth_client_details` DISABLE KEYS */;
INSERT INTO `oauth_client_details` VALUES ('spring-security-oauth-actuator-client','$2a$04$soeOR.QFmClXeFIrhJVLWOQxfHjsJLSpWrU1iGxcMGdu.a5hvfY4W',NULL,'actuator',2592000,2592000,'resource-server-rest-api','password','USER',NULL,NULL),('spring-security-oauth2-read-client','$2a$04$WGq2P9egiOYoOFemBRfsiO9qTcyJtNRnPKNBl5tokP7IP.eZn93km',NULL,'read',2592000,2592000,'resource-server-rest-api','password,refresh_token,social','USER',NULL,NULL),('spring-security-oauth2-read-write-client','$2a$04$soeOR.QFmClXeFIrhJVLWOQxfHjsJLSpWrU1iGxcMGdu.a5hvfY4W',NULL,'read,write',2592000,2592000,'resource-server-rest-api','password,refresh_token,social','USER',NULL,NULL);
/*!40000 ALTER TABLE `oauth_client_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_client_token`
--

DROP TABLE IF EXISTS `oauth_client_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth_client_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` varbinary(1024) DEFAULT NULL,
  `authentication_id` varchar(256) NOT NULL,
  `user_name` varchar(256) DEFAULT NULL,
  `client_id` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_client_token`
--

LOCK TABLES `oauth_client_token` WRITE;
/*!40000 ALTER TABLE `oauth_client_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_client_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_code`
--

DROP TABLE IF EXISTS `oauth_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth_code` (
  `code` varchar(256) DEFAULT NULL,
  `authentication` varbinary(4096) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_code`
--

LOCK TABLES `oauth_code` WRITE;
/*!40000 ALTER TABLE `oauth_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oauth_refresh_token`
--

DROP TABLE IF EXISTS `oauth_refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oauth_refresh_token` (
  `token_id` varchar(256) DEFAULT NULL,
  `token` varbinary(1024) DEFAULT NULL,
  `authentication` varbinary(4096) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oauth_refresh_token`
--

LOCK TABLES `oauth_refresh_token` WRITE;
/*!40000 ALTER TABLE `oauth_refresh_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `oauth_refresh_token` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-01 17:05:15
