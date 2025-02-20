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
-- Table structure for table `personal_schedule`
--

DROP TABLE IF EXISTS `personal_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `personal_schedule` (
  `schedule_id` bigint NOT NULL AUTO_INCREMENT,
  `is_public` bit(1) DEFAULT NULL,
  `member_id` bigint NOT NULL,
  PRIMARY KEY (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `personal_schedule`
--

LOCK TABLES `personal_schedule` WRITE;
/*!40000 ALTER TABLE `personal_schedule` DISABLE KEYS */;
INSERT INTO `personal_schedule` VALUES (18,_binary '',2),(19,_binary '\0',1),(20,_binary '',2),(21,_binary '',9),(22,_binary '',9),(23,_binary '',9),(24,_binary '\0',2),(25,_binary '',20),(27,_binary '',9),(28,_binary '',22),(29,_binary '\0',22),(30,_binary '',22),(31,_binary '',22),(32,_binary '\0',22),(33,_binary '',22),(34,_binary '',22),(35,_binary '\0',22),(36,_binary '',22),(37,_binary '',22),(38,_binary '\0',22),(39,_binary '',22),(40,_binary '',22),(41,_binary '',22),(42,_binary '\0',22),(43,_binary '\0',22),(44,_binary '\0',22),(45,_binary '',22),(46,_binary '',22),(47,_binary '',22),(48,_binary '',22),(49,_binary '',22),(50,_binary '',22),(51,_binary '\0',22),(52,_binary '\0',22),(53,_binary '\0',22),(54,_binary '',24),(55,_binary '',24),(56,_binary '',24),(57,_binary '',24),(58,_binary '\0',24),(59,_binary '',24),(60,_binary '',24),(61,_binary '\0',24),(62,_binary '',24),(63,_binary '',24),(64,_binary '\0',24),(65,_binary '',24),(66,_binary '',24),(67,_binary '\0',25),(68,_binary '\0',25),(69,_binary '\0',25),(70,_binary '\0',25),(71,_binary '\0',25),(72,_binary '\0',25),(73,_binary '\0',25),(74,_binary '\0',25),(75,_binary '\0',25),(76,_binary '\0',25),(77,_binary '\0',25),(78,_binary '\0',25),(79,_binary '\0',25),(80,_binary '\0',25),(81,_binary '\0',25),(82,_binary '',26),(83,_binary '\0',26),(84,_binary '',26),(85,_binary '',26),(86,_binary '',26),(87,_binary '\0',26),(88,_binary '',26),(89,_binary '',26),(90,_binary '',26),(91,_binary '',26),(92,_binary '\0',26),(93,_binary '\0',26),(94,_binary '',26),(95,_binary '',26),(96,_binary '\0',26),(97,_binary '',27),(98,_binary '\0',27),(99,_binary '',27),(100,_binary '',27),(101,_binary '',27),(102,_binary '\0',27),(103,_binary '',27),(104,_binary '\0',27),(105,_binary '',27),(106,_binary '',27),(107,_binary '',27),(108,_binary '',28),(109,_binary '\0',28),(110,_binary '',28),(111,_binary '',28),(112,_binary '',28),(113,_binary '',28),(114,_binary '\0',28),(115,_binary '\0',28),(116,_binary '',28),(117,_binary '',28),(118,_binary '',28),(119,_binary '',28),(120,_binary '',28),(121,_binary '\0',28),(122,_binary '\0',28),(123,_binary '',28),(124,_binary '\0',28),(125,_binary '\0',28),(126,_binary '\0',28),(127,_binary '',28),(128,_binary '\0',28),(129,_binary '',28),(130,_binary '',28),(131,_binary '\0',10),(132,_binary '',8),(134,_binary '\0',38),(135,_binary '',10),(137,_binary '',8),(138,_binary '\0',8),(139,_binary '',37),(140,_binary '',31),(141,_binary '',46),(147,_binary '',50),(148,_binary '\0',50),(149,_binary '',54),(150,_binary '',50),(151,_binary '',54),(152,_binary '',54),(153,_binary '',50),(154,_binary '\0',54),(155,_binary '\0',50),(156,_binary '\0',53),(157,_binary '',53),(158,_binary '',53),(159,_binary '',51),(160,_binary '',51),(161,_binary '',51),(162,_binary '',52),(163,_binary '\0',52),(164,_binary '',51),(165,_binary '\0',52),(166,_binary '',51),(167,_binary '',51),(168,_binary '\0',51),(171,_binary '',54),(174,_binary '\0',51);
/*!40000 ALTER TABLE `personal_schedule` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  2:03:20
