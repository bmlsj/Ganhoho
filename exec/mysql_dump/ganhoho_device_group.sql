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
-- Table structure for table `device_group`
--

DROP TABLE IF EXISTS `device_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device_group` (
  `device_group_id` bigint NOT NULL AUTO_INCREMENT,
  `notification_key` varchar(255) DEFAULT NULL,
  `notification_key_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`device_group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device_group`
--

LOCK TABLES `device_group` WRITE;
/*!40000 ALTER TABLE `device_group` DISABLE KEYS */;
INSERT INTO `device_group` VALUES (1,'APA91bFA97xExCJvB6m9dyCH6yz58KKJmpKtu7zkoP_m9tqx1TB63LBNrDZqgbU4uRPzW-_Cqxp1ZV6I04x_XTmKlLTPekye0a_p9HNm5kckEUP-K1ggv3DV2NutmlcG693xegI3kK67','qhfkaoquddnjs_rksghqudehd'),(2,'APA91bGzwE9gSJdx5y00qIuIgjVopgtnM5KbG7GgNKTVIZv77FxXbZ3k9eQTuAHsbDnwkG257DHsgTbYRM69BwgojUxghV_dTE9rc6_Q6BxDlGGBi3SvVyDEAKlzoB1Kf8VkwUDfhrLX','Tkvlrnalquddnjs_rksghqn'),(3,'APA91bH0xF6p8IgOO7J2UXIae1szPp63HttsJRr20xbIILHAdqviXEfZQmH_ERZ3QlUrMF7gre9Hrgtp_gBsXnV5O8352vyBanNF3xFx9D1bAUgQu4xGZX-ExVeNe2f_bme_FkATtDQh','qhfkaoquddnjs_rksghqn');
/*!40000 ALTER TABLE `device_group` ENABLE KEYS */;
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
