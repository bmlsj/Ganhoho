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
-- Table structure for table `group`
--

DROP TABLE IF EXISTS `group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group` (
  `group_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `group_icon_type` int DEFAULT NULL,
  `group_invite_link` varchar(50) DEFAULT NULL,
  `group_member_count` int DEFAULT NULL,
  `group_name` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `group_deep_link` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group`
--

LOCK TABLES `group` WRITE;
/*!40000 ALTER TABLE `group` DISABLE KEYS */;
INSERT INTO `group` VALUES (1,NULL,1073741824,'aeb87336',0,'간호호호호',NULL,NULL),(2,NULL,1,'b6e306cc',1,'간호호호호',NULL,NULL),(3,NULL,1,'33a698fa',1,'teset',NULL,NULL),(4,NULL,1,'4d85187b',0,'tt',NULL,NULL),(5,NULL,5,'a9ced11d',0,'퇴근하자',NULL,NULL),(6,NULL,6,'663bc719',0,'SSAFY',NULL,NULL),(7,NULL,6,'3552ed72',0,'SSAFY',NULL,NULL),(8,NULL,4,'02fdf87b',1,'testtest',NULL,NULL),(9,NULL,4,'1717b8fc',1,'SSAFY',NULL,NULL),(10,NULL,1,'618a9152',5,'간호호호홓',NULL,NULL),(11,NULL,1,'4954074b',1,'tt',NULL,NULL),(12,NULL,1,'3a0c9ac7',1,'test',NULL,NULL),(13,NULL,3,'2639f3e4',0,'ssafy',NULL,NULL),(14,NULL,3,'06423dfd',1,'test',NULL,NULL),(15,NULL,3,'b8d6f8ad',1,'test',NULL,NULL),(16,NULL,5,'92dbe0f4',1,'퇴근시켜주세요',NULL,NULL),(17,NULL,1,'e18bd80c',1,'간호호호홓',NULL,'ssafyd209://ganhoho/group?groupCode=e18bd80c'),(18,NULL,1,'9c396e8b',1,'간호호호홓1234',NULL,'ssafyd209://ganhoho/group?groupCode=9c396e8b'),(19,NULL,1,'9990046b',1,'간호호호홓12345',NULL,'ssafyd209://ganhoho/group?groupCode=9990046b'),(20,NULL,1,'52daa66c',1,'link test',NULL,'ssafyd209://ganhoho/group?groupCode=52daa66c'),(21,NULL,1,'2863bacf',1,'간호호홓134',NULL,'ssafyd209://ganhoho/group?groupCode=2863bacf'),(22,NULL,2,'8513b2d5',2,'응급실',NULL,'ssafyd209://ganhoho/group?groupCode=8513b2d5'),(23,NULL,4,'6f808610',1,'\n간호호',NULL,'ssafyd209://ganhoho/group?groupCode=6f808610'),(24,NULL,5,'5593584e',1,'퇵은',NULL,'ssafyd209://ganhoho/group?groupCode=5593584e'),(25,NULL,1,'183599c8',2,'수정사항이많네요',NULL,'ssafyd209://ganhoho/group?groupCode=183599c8'),(26,NULL,1,'759a6e57',1,'그룹 추가 새로고침 테스트',NULL,'ssafyd209://ganhoho/group?groupCode=759a6e57'),(27,NULL,3,'c12c0c08',0,'긴 제목을 테스트하는 용도입니다아아아아아아아앙아아아아',NULL,'ssafyd209://ganhoho/group?groupCode=c12c0c08'),(28,NULL,4,'77548800',1,'link test',NULL,'ssafyd209://ganhoho/group?groupCode=77548800'),(29,NULL,1,'f7f447f8',0,'test',NULL,'ssafyd209://ganhoho/group?groupCode=f7f447f8'),(30,NULL,1,'eac50fcb',0,'0',NULL,'ssafyd209://ganhoho/group?groupCode=eac50fcb'),(31,NULL,1,'cc2abea0',1,'스크롤',NULL,'ssafyd209://ganhoho/group?groupCode=cc2abea0'),(32,NULL,1,'69560bdd',1,'스크롤',NULL,'ssafyd209://ganhoho/group?groupCode=69560bdd'),(33,NULL,4,'7f1e8174',0,'스크롤',NULL,'ssafyd209://ganhoho/group?groupCode=7f1e8174'),(34,NULL,6,'5485dcf9',1,'스크롤테스트',NULL,'ssafyd209://ganhoho/group?groupCode=5485dcf9'),(35,NULL,1,'8028e050',1,'테스트',NULL,'ssafyd209://ganhoho/group?groupCode=8028e050'),(36,NULL,1,'5a332d39',6,'싸피병원동기회',NULL,'ssafyd209://ganhoho/group?groupCode=5a332d39'),(37,NULL,1,'6cae25dc',5,'빵빵비긴즈',NULL,'ssafyd209://ganhoho/group?groupCode=6cae25dc'),(38,NULL,4,'bc06bf45',4,'컴포즈',NULL,'ssafyd209://ganhoho/group?groupCode=bc06bf45'),(39,NULL,4,'4d13d70b',6,'빵빵비건즈',NULL,'ssafyd209://ganhoho/group?groupCode=4d13d70b'),(40,NULL,1,'c11ac2d5',2,'냠냠육식즈',NULL,'ssafyd209://ganhoho/group?groupCode=c11ac2d5'),(41,NULL,4,'a9a97490',2,'하나영님의 그룹',NULL,'ssafyd209://ganhoho/group?groupCode=a9a97490'),(42,NULL,6,'a83f58c7',2,'숭디숭디',NULL,'ssafyd209://ganhoho/group?groupCode=a83f58c7'),(43,NULL,1,'01729ffd',1,'에바는에바다',NULL,'ssafyd209://ganhoho/group?groupCode=01729ffd'),(44,NULL,1,'c84be3f6',2,'에바에...',NULL,'ssafyd209://ganhoho/group?groupCode=c84be3f6'),(45,NULL,6,'892266a4',1,'공공칠빵',NULL,'ssafyd209://ganhoho/group?groupCode=892266a4'),(46,NULL,6,'549d3867',1,'기획폭발단',NULL,'ssafyd209://ganhoho/group?groupCode=549d3867'),(47,NULL,6,'ee642de0',1,'기획폭발단',NULL,'ssafyd209://ganhoho/group?groupCode=ee642de0'),(48,NULL,6,'fa6f1a9e',1,'기획폭발단',NULL,'ssafyd209://ganhoho/group?groupCode=fa6f1a9e'),(49,NULL,1,'979d1b21',1,'그룹',NULL,'ssafyd209://ganhoho/group?groupCode=979d1b21'),(50,NULL,5,'89b06543',1,'블루버디',NULL,'ssafyd209://ganhoho/group?groupCode=89b06543'),(51,NULL,1,'1041cacb',1,'ddd',NULL,'ssafyd209://ganhoho/group?groupCode=1041cacb'),(52,NULL,1,'81eb5a97',1,'gf',NULL,'ssafyd209://ganhoho/group?groupCode=81eb5a97'),(53,NULL,5,'7ae61e69',6,'공주들',NULL,'ssafyd209://ganhoho/group?groupCode=7ae61e69'),(54,NULL,1,'ae9adff4',1,'보라매동기',NULL,'ssafyd209://ganhoho/group?groupCode=ae9adff4'),(55,NULL,2,'4f3bf8eb',4,'풋살',NULL,'ssafyd209://ganhoho/group?groupCode=4f3bf8eb'),(56,NULL,6,'4742fbd6',1,'포항항',NULL,'ssafyd209://ganhoho/group?groupCode=4742fbd6'),(57,NULL,6,'ed87e134',1,'롤짓남',NULL,'ssafyd209://ganhoho/group?groupCode=ed87e134'),(58,NULL,6,'583dfb0a',1,'gd 왕밤빵',NULL,'ssafyd209://ganhoho/group?groupCode=583dfb0a');
/*!40000 ALTER TABLE `group` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-21  2:03:18
