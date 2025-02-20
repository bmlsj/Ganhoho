-- MySQL dump 10.13  Distrib 8.0.33, for macos13 (arm64)
--
-- Host: 13.125.239.53    Database: ganhoho
-- ------------------------------------------------------
-- Server version	8.0.41

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend` (
  `friend_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `friend_login_id` varchar(255) DEFAULT NULL,
  `is_favorite` bit(1) DEFAULT NULL,
  `request_status` enum('ACCEPTED','PENDING') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `member_id` bigint DEFAULT NULL,
  PRIMARY KEY (`friend_id`),
  KEY `FKp4mgplr22rspgiwep39a3d2sm` (`member_id`),
  CONSTRAINT `FKp4mgplr22rspgiwep39a3d2sm` FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
/*!40000 ALTER TABLE `friend` DISABLE KEYS */;
INSERT INTO `friend` VALUES (1,NULL,'test2',_binary '','ACCEPTED',NULL,2),(2,NULL,'test3',_binary '\0','ACCEPTED',NULL,2),(3,NULL,'testUser7',_binary '\0','PENDING',NULL,2),(4,NULL,'test',_binary '','ACCEPTED',NULL,18),(5,NULL,'ssafy',_binary '','ACCEPTED',NULL,9),(6,NULL,'string',_binary '\0','PENDING',NULL,8),(7,NULL,'test2',_binary '\0','ACCEPTED',NULL,20),(8,NULL,'test2',_binary '\0','ACCEPTED',NULL,9),(9,NULL,'test144',_binary '\0','PENDING',NULL,9),(10,NULL,'dkssud',_binary '','ACCEPTED',NULL,9),(11,NULL,'test144',_binary '\0','PENDING',NULL,8),(12,NULL,'testUser7',_binary '\0','PENDING',NULL,8),(13,NULL,'jeonghu',_binary '\0','PENDING',NULL,8),(14,NULL,'user42144',_binary '\0','PENDING',NULL,8),(15,NULL,'hit1',_binary '\0','PENDING',NULL,8),(16,NULL,'newUser15',_binary '\0','PENDING',NULL,8),(17,NULL,'test5',_binary '','ACCEPTED',NULL,8),(18,NULL,'test',_binary '','ACCEPTED',NULL,37),(19,NULL,'test4',_binary '\0','ACCEPTED',NULL,9),(20,NULL,'test2',_binary '\0','ACCEPTED',NULL,18),(21,NULL,'testM3',_binary '\0','ACCEPTED',NULL,22),(22,NULL,'testM4',_binary '\0','ACCEPTED',NULL,31),(23,NULL,'hit1',_binary '\0','PENDING',NULL,40),(24,NULL,'hit2',_binary '\0','PENDING',NULL,46),(25,NULL,'testM2',_binary '\0','PENDING',NULL,22),(26,NULL,'testM4',_binary '\0','PENDING',NULL,22),(27,NULL,'testUser10',_binary '\0','PENDING',NULL,10),(28,NULL,'hit3',_binary '\0','PENDING',NULL,24),(29,NULL,'hit2',_binary '\0','PENDING',NULL,47),(30,NULL,'mit1',_binary '\0','ACCEPTED',NULL,54),(31,NULL,'mit2',_binary '\0','ACCEPTED',NULL,54),(32,NULL,'mit3',_binary '','ACCEPTED',NULL,54),(33,NULL,'mit4',_binary '\0','PENDING',NULL,54),(34,NULL,'mit5',_binary '','ACCEPTED',NULL,54),(35,NULL,'mit1',_binary '\0','ACCEPTED',NULL,50),(36,NULL,'mit4',_binary '\0','PENDING',NULL,53),(37,NULL,'mit3',_binary '\0','ACCEPTED',NULL,53),(38,NULL,'mit2',_binary '\0','ACCEPTED',NULL,53),(39,NULL,'mit1',_binary '\0','ACCEPTED',NULL,53),(40,NULL,'mit2',_binary '\0','ACCEPTED',NULL,52),(41,NULL,'mit3',_binary '\0','PENDING',NULL,52),(42,NULL,'testUser7',_binary '\0','PENDING',NULL,54),(43,NULL,'mit3',_binary '\0','PENDING',NULL,55),(44,NULL,'testM5',_binary '\0','PENDING',NULL,51);
/*!40000 ALTER TABLE `friend` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  2:03:19
