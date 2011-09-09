-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.41-log


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema landanalysis
--

CREATE DATABASE IF NOT EXISTS landanalysis;
USE landanalysis;

--
-- Definition of table `landanalysis`.`ATTRTYPES`
--

DROP TABLE IF EXISTS `landanalysis`.`ATTRTYPES`;
CREATE TABLE  `landanalysis`.`ATTRTYPES` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `col_order` int(11) DEFAULT NULL,
  `col_size` int(11) DEFAULT NULL,
  `editable` bit(1) DEFAULT NULL,
  `featuretype_id` int(11) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  `hint` varchar(255) NOT NULL,
  `label` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `visible` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`ATTRTYPES`
--

/*!40000 ALTER TABLE `ATTRTYPES` DISABLE KEYS */;
LOCK TABLES `ATTRTYPES` WRITE;
INSERT INTO `landanalysis`.`ATTRTYPES` VALUES  (1,NULL,100,0x00,2,NULL,'_City_title_hint','_City_title','title',0x01),
 (2,NULL,100,0x00,2,NULL,'_City_description_hint','_City_description','description',0x01),
 (3,NULL,100,0x00,2,NULL,'_City_category_hint','_City_category','category',0x01),
 (4,NULL,100,0x00,3,NULL,'_Road_title_hint','_Road_title','title',0x01),
 (5,NULL,100,0x00,3,NULL,'_Road_description_hint','_Road_description','description',0x01),
 (6,NULL,100,0x00,3,NULL,'_Road_category_hint','_Road_category','category',0x01);
UNLOCK TABLES;
/*!40000 ALTER TABLE `ATTRTYPES` ENABLE KEYS */;


--
-- Definition of table `landanalysis`.`CONNECTS_GROUP_PRIVILEGES`
--

DROP TABLE IF EXISTS `landanalysis`.`CONNECTS_GROUP_PRIVILEGES`;
CREATE TABLE  `landanalysis`.`CONNECTS_GROUP_PRIVILEGES` (
  `GROUP_ID` bigint(20) NOT NULL,
  `PRIVILEGES_ID` bigint(20) NOT NULL,
  KEY `FKBF2C9A78F814E34A` (`PRIVILEGES_ID`),
  KEY `FKBF2C9A78CC352D5B` (`GROUP_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`CONNECTS_GROUP_PRIVILEGES`
--

/*!40000 ALTER TABLE `CONNECTS_GROUP_PRIVILEGES` DISABLE KEYS */;
LOCK TABLES `CONNECTS_GROUP_PRIVILEGES` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `CONNECTS_GROUP_PRIVILEGES` ENABLE KEYS */;


--
-- Definition of table `landanalysis`.`FEATURETYPES`
--

DROP TABLE IF EXISTS `landanalysis`.`FEATURETYPES`;
CREATE TABLE  `landanalysis`.`FEATURETYPES` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hasGeometry` bit(1) DEFAULT NULL,
  `idAttribute` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `searchable` bit(1) DEFAULT NULL,
  `table_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`FEATURETYPES`
--

/*!40000 ALTER TABLE `FEATURETYPES` DISABLE KEYS */;
LOCK TABLES `FEATURETYPES` WRITE;
INSERT INTO `landanalysis`.`FEATURETYPES` VALUES  (1,0x01,'id','_note','Note',0x01,'note'),
 (2,0x01,'id','_City','City',0x01,'lbs_municipality'),
 (3,0x01,'id','_Road','Road',0x01,'rue');
UNLOCK TABLES;
/*!40000 ALTER TABLE `FEATURETYPES` ENABLE KEYS */;


--
-- Definition of table `landanalysis`.`GROUPS`
--

DROP TABLE IF EXISTS `landanalysis`.`GROUPS`;
CREATE TABLE  `landanalysis`.`GROUPS` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  `parent_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7DD0CDD4A19D7550` (`parent_group_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`GROUPS`
--

/*!40000 ALTER TABLE `GROUPS` DISABLE KEYS */;
LOCK TABLES `GROUPS` WRITE;
INSERT INTO `landanalysis`.`GROUPS` VALUES  (1,'admin',NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `GROUPS` ENABLE KEYS */;


--
-- Definition of table `landanalysis`.`GROUP_PRIVILEGES`
--

DROP TABLE IF EXISTS `landanalysis`.`GROUP_PRIVILEGES`;
CREATE TABLE  `landanalysis`.`GROUP_PRIVILEGES` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`GROUP_PRIVILEGES`
--

/*!40000 ALTER TABLE `GROUP_PRIVILEGES` DISABLE KEYS */;
LOCK TABLES `GROUP_PRIVILEGES` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `GROUP_PRIVILEGES` ENABLE KEYS */;


--
-- Definition of table `landanalysis`.`USERS`
--

DROP TABLE IF EXISTS `landanalysis`.`USERS`;
CREATE TABLE  `landanalysis`.`USERS` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(50) DEFAULT NULL,
  `expireDate` varchar(50) DEFAULT NULL,
  `fullName` varchar(150) DEFAULT NULL,
  `moreInfo` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  `group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4D495E8CC352D5B` (`group_id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`USERS`
--

/*!40000 ALTER TABLE `USERS` DISABLE KEYS */;
LOCK TABLES `USERS` WRITE;
INSERT INTO `landanalysis`.`USERS` VALUES  (1,NULL,NULL,NULL,NULL,'kwadmin','admin',1),
 (2,NULL,NULL,NULL,NULL,'toto','jeff',1);
UNLOCK TABLES;
/*!40000 ALTER TABLE `USERS` ENABLE KEYS */;


--
-- Definition of table `landanalysis`.`USER_ROLES`
--

DROP TABLE IF EXISTS `landanalysis`.`USER_ROLES`;
CREATE TABLE  `landanalysis`.`USER_ROLES` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) DEFAULT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `landanalysis`.`USER_ROLES`
--

/*!40000 ALTER TABLE `USER_ROLES` DISABLE KEYS */;
LOCK TABLES `USER_ROLES` WRITE;
INSERT INTO `landanalysis`.`USER_ROLES` VALUES  (1,'manager','admin'),
 (2,'user_role','admin'),
 (3,'admin_role','admin');
UNLOCK TABLES;
/*!40000 ALTER TABLE `USER_ROLES` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
