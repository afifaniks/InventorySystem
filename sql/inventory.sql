CREATE DATABASE  IF NOT EXISTS `inventory` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
USE `inventory`;
-- MySQL dump 10.13  Distrib 8.0.11, for Win64 (x86_64)
--
-- Host: localhost    Database: inventory
-- ------------------------------------------------------
-- Server version	8.0.11

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `accounts` (
  `acccountID` int(11) NOT NULL,
  `accountName` varchar(45) NOT NULL,
  `accountDetails` varchar(100) DEFAULT NULL,
  `Customers_customerID` int(11) NOT NULL,
  `User_username` varchar(45) DEFAULT NULL,
  `payMethod` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`acccountID`),
  UNIQUE KEY `accountName_UNIQUE` (`accountName`),
  KEY `fk_Accounts_Customers1_idx` (`Customers_customerID`),
  KEY `fk_Accounts_User2_idx` (`User_username`),
  CONSTRAINT `fk_Accounts_Customers1` FOREIGN KEY (`Customers_customerID`) REFERENCES `customers` (`customerid`),
  CONSTRAINT `fk_Accounts_User2` FOREIGN KEY (`User_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
INSERT INTO `accounts` VALUES (1,'Account 1','NONE',1,'admin','Cash'),(2,'Account 2','NONE',1,'admin','Cheque'),(3,'Account 3','NONE',2,'admin','ATM'),(4,'Account 4','NONE',3,'admin','Credit'),(5,'Account 5','NONE',4,'admin','Cash'),(6,'Account 6','NONE',5,'admin','Cash'),(7,'Account 7','NONE',6,'admin','Cheque'),(8,'Account 8','NONE',7,'admin','ATM'),(9,'Account Change','None',8,'admin','Cash'),(10,'Account 10','NONE',9,'admin','Cash'),(11,'New Account','NONE',1,'admin','ATM');
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `customers` (
  `customerID` int(11) NOT NULL,
  `firstName` varchar(45) NOT NULL,
  `lastName` varchar(45) NOT NULL,
  `address` varchar(100) NOT NULL,
  `phone` varchar(11) NOT NULL,
  `email` varchar(45) DEFAULT NULL,
  `photo` varchar(100) DEFAULT NULL,
  `gender` enum('Male','Female') NOT NULL,
  `memberSince` date DEFAULT NULL,
  PRIMARY KEY (`customerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Dr. Professor','Karim','Dhaka','0188999999','dr.karim@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/man3.jpg','Male','2018-08-11'),(2,'Faysal','Khan','Shukrabad','017222222','faysal@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/man1.jpg','Male','2018-08-11'),(3,'Afif','Anik','Dhaka','01722562455','anik@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/man2.jpg','Male','2018-08-11'),(4,'Jubayer','Ahmed','Dhaka','0199999999','jubaer@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/man4.jpg','Male','2018-08-11'),(5,'Niloy','Mia','Jamalpur','01722265222','niloy@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/man5.jpg','Male','2018-08-11'),(6,'Sadia','Zamal','Dhaka','0167799999','zamal@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/woman1.jpg','Female','2018-08-11'),(7,'Hasina','Sheikh','Dhaka','0169999999','sheikh@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/woman2.jpg','Female','2018-08-11'),(8,'John','Anderson','Dhaka','0188999999','john@gmail.com','file:/D:/Project_Final/IntellijIDEA/Images/customers/man5.jpg','Male','2018-08-11'),(9,'Imam','Rayhan','Chittagong','0155999999','imam@gmail.com','','Male','2018-10-01'),(10,'Babul','Kadir','Dhaka','0166999999','babul@gmail.com','','Male','2018-10-01'),(11,'Test','User','Dhaka','017888888','test@email.com','file:/C:/Users/daddy/Desktop/man2.jpg','Male','2018-08-10'),(12,'New','Guy','New','7567','jhnhgh',NULL,'Male','2020-07-10');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financialtronpurchase`
--

DROP TABLE IF EXISTS `financialtronpurchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `financialtronpurchase` (
  `trID` int(11) NOT NULL,
  `trDate` date DEFAULT NULL,
  `Accounts_acccountID` int(11) NOT NULL,
  `TrTypeCode_trTypeCode` int(11) NOT NULL,
  `Purchases_purchaseID` int(11) NOT NULL,
  `User_username` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`trID`),
  KEY `fk_financialTrOnPurchase_Accounts1_idx` (`Accounts_acccountID`),
  KEY `fk_financialTrOnPurchase_TrTypeCode1_idx` (`TrTypeCode_trTypeCode`),
  KEY `fk_financialTrOnPurchase_Purchases1_idx` (`Purchases_purchaseID`),
  KEY `fk_User_idx` (`User_username`),
  CONSTRAINT `fk_User` FOREIGN KEY (`User_username`) REFERENCES `user` (`username`),
  CONSTRAINT `fk_financialTrOnPurchase_Purchases1` FOREIGN KEY (`Purchases_purchaseID`) REFERENCES `purchases` (`purchaseid`),
  CONSTRAINT `fk_financialTrOnPurchase_TrTypeCode1` FOREIGN KEY (`TrTypeCode_trTypeCode`) REFERENCES `trtypecode` (`trtypecode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financialtronpurchase`
--

LOCK TABLES `financialtronpurchase` WRITE;
/*!40000 ALTER TABLE `financialtronpurchase` DISABLE KEYS */;
INSERT INTO `financialtronpurchase` VALUES (1,'2018-07-05',1,1,1,'admin'),(2,'2018-07-06',2,2,2,'admin'),(3,'2018-07-07',3,2,3,'admin'),(4,'2018-07-09',4,2,4,'admin'),(5,'2018-07-11',5,1,5,'admin'),(6,'2018-07-11',6,3,6,'admin'),(7,'2018-07-12',7,4,7,'admin'),(8,'2018-07-12',8,3,8,'admin'),(9,'2018-07-14',9,2,9,'admin'),(10,'2018-07-14',10,2,10,'admin'),(11,'2018-08-06',9,1,11,'admin'),(12,'2018-08-07',7,1,12,'admin'),(13,'2018-08-18',8,1,13,'admin'),(14,'2018-08-09',10,1,14,'admin'),(15,'2018-08-10',5,2,15,'admin'),(16,'2018-08-10',7,1,16,'employee'),(17,'2018-08-10',6,1,17,'admin'),(18,'2018-08-11',3,1,18,'admin'),(19,'2018-08-11',1,1,19,'admin'),(20,'2018-08-11',9,1,20,'admin'),(21,'2018-08-11',5,1,21,'admin'),(22,'2020-07-10',3,1,22,'admin');
/*!40000 ALTER TABLE `financialtronpurchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `financialtronrental`
--

DROP TABLE IF EXISTS `financialtronrental`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `financialtronrental` (
  `trID` int(11) NOT NULL,
  `trDate` date NOT NULL,
  `Accounts_acccountID` int(11) NOT NULL,
  `Rentals_rentalID` int(11) DEFAULT NULL,
  `TrTypeCode_trTypeCode` int(11) NOT NULL,
  `User_username` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`trID`),
  KEY `fk_FinancialTrOnRental_Accounts1_idx` (`Accounts_acccountID`),
  KEY `fk_FinancialTrOnRental_TrTypeCode1_idx` (`TrTypeCode_trTypeCode`),
  KEY `fk_Username_idx` (`User_username`),
  KEY `fk_Rental_idx` (`Rentals_rentalID`),
  CONSTRAINT `fk_FinancialTrOnRental_TrTypeCode1` FOREIGN KEY (`TrTypeCode_trTypeCode`) REFERENCES `trtypecode` (`trtypecode`),
  CONSTRAINT `fk_Rental` FOREIGN KEY (`Rentals_rentalID`) REFERENCES `rentals` (`rentalid`),
  CONSTRAINT `fk_Username` FOREIGN KEY (`User_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `financialtronrental`
--

LOCK TABLES `financialtronrental` WRITE;
/*!40000 ALTER TABLE `financialtronrental` DISABLE KEYS */;
INSERT INTO `financialtronrental` VALUES (1,'2018-07-07',1,1,1,'admin'),(2,'2018-07-07',2,2,2,'admin'),(3,'2018-07-09',3,2,3,'admin'),(4,'2018-07-09',4,2,4,'admin'),(5,'2018-07-11',5,1,2,'admin'),(6,'2018-07-12',6,3,1,'admin'),(7,'2018-07-14',7,4,3,'admin'),(8,'2018-07-16',8,3,4,'admin'),(9,'2018-07-17',9,2,2,'admin'),(10,'2018-07-17',10,2,1,'admin'),(11,'2018-08-10',6,11,1,'admin'),(12,'2018-08-10',4,12,1,'admin'),(13,'2018-08-11',4,13,1,'admin'),(14,'2018-08-11',4,14,1,'admin'),(15,'2018-08-11',10,15,1,'admin');
/*!40000 ALTER TABLE `financialtronrental` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `item` (
  `itemID` int(11) NOT NULL,
  `itemName` varchar(45) NOT NULL,
  `stock` int(11) NOT NULL,
  `rentalOrSale` set('Rental','Sale') NOT NULL,
  `salePrice` double DEFAULT NULL,
  `rentRate` double DEFAULT NULL,
  `photo` varchar(160) DEFAULT NULL,
  `ItemType_itemTypeId` int(11) NOT NULL,
  PRIMARY KEY (`itemID`),
  KEY `fk_Item_ItemType1_idx` (`ItemType_itemTypeId`),
  CONSTRAINT `fk_Item_ItemType1` FOREIGN KEY (`ItemType_itemTypeId`) REFERENCES `itemtype` (`itemtypeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,'Mobile',47,'Sale',2000,0,'file:/D:/Project_Final/IntellijIDEA/Images/items/mobile.jpg',1),(2,'Computer',29,'Sale',15000,0,'file:/D:/Project_Final/IntellijIDEA/Images/items/computer.jpg',1),(3,'Truc',9,'Rental',0,400,'file:/D:/Project_Final/IntellijIDEA/Images/items/truck.jpg',5),(4,'Electric Heater',4,'Rental,Sale',100,800,'file:/D:/Project_Final/IntellijIDEA/Images/items/heater.jpg',2),(5,'Drilling Machine',10,'Sale',100,0,'file:/D:/Project_Final/IntellijIDEA/Images/items/drill.png',3),(6,'SkateBoard',1,'Sale',100,0,'file:/D:/Project_Final/IntellijIDEA/Images/items/skate.jpg',7),(7,'Hammer',4,'Sale',60,0,'file:/D:/Project_Final/IntellijIDEA/Images/items/hammer.jpg',4),(8,'X-mas lights',39,'Rental',0,50,'file:/D:/Project_Final/IntellijIDEA/Images/items/xmas.jpg',9),(9,'Product 2',14,'Sale',70,0,NULL,6),(10,'Product 3',9,'Sale',90,0,NULL,6),(11,'hghf',20,'Sale',44,0,NULL,2);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itemtype`
--

DROP TABLE IF EXISTS `itemtype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `itemtype` (
  `itemTypeId` int(11) NOT NULL,
  `typeName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`itemTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itemtype`
--

LOCK TABLES `itemtype` WRITE;
/*!40000 ALTER TABLE `itemtype` DISABLE KEYS */;
INSERT INTO `itemtype` VALUES (1,'Electronics'),(2,'Home Appliances'),(3,'Tools'),(4,'Machinaries'),(5,'Automobiles'),(6,'Groceries'),(7,'Fun Stuff'),(8,'Sports'),(9,'Decoration'),(10,'Daily Basics');
/*!40000 ALTER TABLE `itemtype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `purchases` (
  `purchaseID` int(11) NOT NULL,
  `purchaseDate` varchar(45) NOT NULL,
  `purchaseQuantity` int(11) NOT NULL,
  `amountDue` double DEFAULT NULL,
  `User_username` varchar(45) NOT NULL,
  `Item_itemID` int(11) NOT NULL,
  `Customers_customerID` int(11) NOT NULL,
  `payAmount` double DEFAULT NULL,
  PRIMARY KEY (`purchaseID`),
  KEY `fk_Purchases_User1_idx` (`User_username`),
  KEY `fk_Purchases_Item1_idx` (`Item_itemID`),
  KEY `fk_Purchases_Customers1_idx` (`Customers_customerID`),
  CONSTRAINT `fk_Purchases_User1` FOREIGN KEY (`User_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchases`
--

LOCK TABLES `purchases` WRITE;
/*!40000 ALTER TABLE `purchases` DISABLE KEYS */;
INSERT INTO `purchases` VALUES (1,'2018-7-5',2,0,'admin',1,1,1000),(2,'2018-7-6',3,0,'admin',2,2,1210),(3,'2018-7-7',1,0,'admin',3,3,920),(4,'2018-7-8',5,10,'admin',3,4,100),(5,'2018-7-9',7,0,'admin',1,6,200),(6,'2018-7-11',7,0,'admin',4,8,600),(7,'2018-7-12',4,0,'admin',5,1,8030),(8,'2018-7-13',9,0,'admin',6,7,800),(9,'2018-7-14',2,0,'admin',7,5,900),(10,'2018-7-17',6,0,'admin',8,5,1120),(11,'2018-08-7',3,0,'admin',7,8,180),(12,'2018-08-8',7,0,'admin',6,6,700),(13,'2018-08-9',5,0,'admin',9,7,70),(14,'2018-08-10',3,0,'admin',4,9,300),(15,'2018-08-10',1,0,'admin',10,4,90),(16,'2018-08-10',3,0,'employee',7,6,180),(17,'2018-08-10',1,0,'admin',6,5,100),(18,'2018-08-11',1,0,'admin',9,2,70),(19,'2018-08-11',3,0,'admin',1,1,6000),(20,'2018-08-11',1,0,'admin',6,8,100),(21,'2018-08-11',1,0,'admin',2,4,15000),(22,'2020-07-10',10,25,'admin',10,2,875);
/*!40000 ALTER TABLE `purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rentals`
--

DROP TABLE IF EXISTS `rentals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `rentals` (
  `rentalID` int(11) NOT NULL,
  `rentalDate` date NOT NULL,
  `returnDate` date DEFAULT NULL,
  `paid` double DEFAULT NULL,
  `amountDue` double DEFAULT NULL,
  `User_username` varchar(45) NOT NULL,
  `Item_itemID` int(11) NOT NULL,
  `Customers_customerID` int(11) NOT NULL,
  PRIMARY KEY (`rentalID`),
  KEY `fk_Rentals_User_idx` (`User_username`),
  KEY `fk_Rentals_Item1_idx` (`Item_itemID`),
  KEY `fk_Rentals_Customers1_idx` (`Customers_customerID`),
  CONSTRAINT `fk_Rentals_User` FOREIGN KEY (`User_username`) REFERENCES `user` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rentals`
--

LOCK TABLES `rentals` WRITE;
/*!40000 ALTER TABLE `rentals` DISABLE KEYS */;
INSERT INTO `rentals` VALUES (1,'2018-07-03','2018-07-12',1000,0,'admin',3,1),(2,'2018-07-04','2018-07-16',620,0,'admin',5,2),(3,'2018-07-05','2018-07-16',550,0,'admin',3,2),(4,'2018-07-05','2018-07-16',600,100,'admin',7,3),(5,'2018-07-06','2018-07-16',420,0,'admin',7,3),(6,'2018-07-07','2018-07-16',800,40,'admin',5,4),(7,'2018-07-07','2018-07-16',600,60,'admin',7,5),(8,'2018-07-08','2018-07-17',200,70,'admin',3,6),(9,'2018-07-09','2018-07-17',1000,80,'admin',5,6),(10,'2018-07-10','2018-07-17',1800,90,'admin',5,1),(11,'2018-07-11','2018-08-16',300,0,'admin',8,5),(12,'2018-07-12','2018-08-11',800,0,'admin',4,3),(13,'2018-07-16','2018-08-12',800,0,'admin',4,3),(14,'2018-07-11','2018-08-12',400,0,'admin',3,3),(15,'2018-08-11','2018-08-15',3200,0,'admin',4,9);
/*!40000 ALTER TABLE `rentals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trtypecode`
--

DROP TABLE IF EXISTS `trtypecode`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `trtypecode` (
  `trTypeCode` int(11) NOT NULL,
  `typeDescription` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`trTypeCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trtypecode`
--

LOCK TABLES `trtypecode` WRITE;
/*!40000 ALTER TABLE `trtypecode` DISABLE KEYS */;
INSERT INTO `trtypecode` VALUES (1,'Cash'),(2,'ATM'),(3,'Credit'),(4,'Others');
/*!40000 ALTER TABLE `trtypecode` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `user` (
  `username` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `accessLevel` enum('Admin','Employee') NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('admin','admin','admin@email.com','Admin'),('employee','employee','employee@email.com','Employee'),('test','test','text@xmail.com','Employee');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usercredents`
--

DROP TABLE IF EXISTS `usercredents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `usercredents` (
  `username` varchar(40) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usercredents`
--

LOCK TABLES `usercredents` WRITE;
/*!40000 ALTER TABLE `usercredents` DISABLE KEYS */;
INSERT INTO `usercredents` VALUES ('admin','admin');
/*!40000 ALTER TABLE `usercredents` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-07-10 23:53:22
