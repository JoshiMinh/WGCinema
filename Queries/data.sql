-- MySQL dump 10.16  Distrib 10.1.48-MariaDB, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: db
-- ------------------------------------------------------
-- Server version	10.1.48-MariaDB-0+deb9u2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dbo.Accounts`
--

DROP TABLE IF EXISTS `dbo.Accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbo.Accounts` (
  `account_email` varchar(25) DEFAULT NULL,
  `name` varchar(16) DEFAULT NULL,
  `gender` varchar(4) DEFAULT NULL,
  `date_of_birth` varchar(10) DEFAULT NULL,
  `membership` tinyint(4) DEFAULT NULL,
  `transactions` tinyint(4) DEFAULT NULL,
  `password_hash` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbo.Accounts`
--

LOCK TABLES `dbo.Accounts` WRITE;
/*!40000 ALTER TABLE `dbo.Accounts` DISABLE KEYS */;
INSERT INTO `dbo.Accounts` VALUES ('binhangia241273@gmail.com','Nguyen Binh Minh','Male','2005-10-20',0,0,'2dc168ebe10521fccc912d16622e72ad9b55fdc7f2440c4dc33035873aa71167');
/*!40000 ALTER TABLE `dbo.Accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dbo.MovieInfo`
--

DROP TABLE IF EXISTS `dbo.MovieInfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbo.MovieInfo` (
  `Title` varchar(37) DEFAULT NULL,
  `Director` varchar(54) DEFAULT NULL,
  `ReleaseDate` varchar(10) DEFAULT NULL,
  `Duration` smallint(6) DEFAULT NULL,
  `Language` varchar(41) DEFAULT NULL,
  `Rating` varchar(3) DEFAULT NULL,
  `Link` varchar(169) DEFAULT NULL,
  `id` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbo.MovieInfo`
--

LOCK TABLES `dbo.MovieInfo` WRITE;
/*!40000 ALTER TABLE `dbo.MovieInfo` DISABLE KEYS */;
INSERT INTO `dbo.MovieInfo` VALUES ('ABIGAIL','Matt Bettinelli-Olpin, Tyler Gillett','2024-04-19',110,'English - Vietnamese subtitles','T13','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQdi4ROqYnZwQXXk5Q6EYuPtzQR57wWBmDYzAfpx6Sc2A&s',1),('ANH HÙNG BÀN PHIM','Ahn Guk Jin','2024-04-19',105,'Korean - Vietnamese subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/t/f/tf_teaser-poster.jpg',2),('B4S - TRƯỚC GIỜ \"YÊU\"','Tung Leo, Michael Thai, Huynh Anh Duy','2024-04-12',100,'Vietnamese - English subtitles','T18','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSCG2IVx0RWMzH0BC7QlZwJIi8BlU1ymJuAn-eNUmcjRg&s',3),('BIỆT ĐỘI SĂN MA: KỶ NGUYÊN BĂNG GIÁ','Gil Kenan','2024-04-12',115,'English - Vietnamese subtitles','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/g/b/gbfe_intl_online_1080x1350_cubes_01.jpg',4),('CÁI GIÁ CỦA HẠNH PHÚC','Nguyen Ngoc Lam','2024-04-19',125,'English - Vietnamese subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/7/0/700x1000-cghp-min.jpg',5),('ĐỀN MẠNG','Ekachai Sriwichai','2024-03-22',93,'Thai - Vietnamese subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/4/x/4x5-the-promise_1.jpg',6),('ĐIỀM BÁO CỦA QUỶ','Arkasha Stevenson','2024-04-05',119,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/7/0/700x1000-omen.jpg',7),('ĐÓA HOA MONG MANH','Mai Thu Huyen','2024-04-18',95,'Vietnamese - English subtitles','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/4/7/470x700_doa_hoa_mong_manh.jpg',8),('DUNE: HÀNH TINH CÁT - PHẦN HAI','Denis Villeneuve','2024-03-01',166,'English - Vietnamese subtitles','T16','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPnLQEemHzahcSRyvXgZUD6eoEG4ba2aCK6IgYSsIRTw&s',9),('EXHUMA: QUẬT MỘ TRÙNG MA','Jang Jae Hyun','2024-03-15',133,'Korean - English & Vietnamese subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/7/0/700x1000-exhuma.jpg',10),('GODZILLA X KONG: ĐẾ CHẾ MỚI','Adam Wingard','2024-03-29',115,'English - Vietnamese subtitles, Korean','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/p/o/poster_payoff_godzilla_va_kong_3_1_.jpg',11),('HÀO QUANG ĐẪM MÁU','Yoo Young-su','2024-04-12',98,'Korean - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/w/a/wannabe_-_teaser_poster.jpg',12),('KUNG FU PANDA 4','Mike Mitchell','2024-03-08',94,'English - Vietnamese subtitles; Dubbed','P','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/3/image/c5f0a1eff4c394a251036189ccddaacd/4/7/470x700-kungfupanda4.jpg',13),('MAI','Tran Thanh','2024-02-10',131,'Vietnamese - English subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/3/image/c5f0a1eff4c394a251036189ccddaacd/m/a/mai_teaser_poster_digital_1_.jpg',14),('Mobile Suit Gundam SEED FREEDOM','Mitsuo Fukuda','2024-04-05',125,'Japanese - Vietnamese subtitles, English','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/7/0/700x1000-gundam.jpg',15),('MONKEY MAN','Dev Patel','2024-04-05',121,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/4/7/470x700_monkeyman.jpg',16),('MÙA HÈ CỦA LUCA','Enrico Casarosa','2024-04-19',99,'English Vietnamese subtitles, dubbed','P','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/r/s/rsz_luca_poster.jpg',17),('MUÔN VỊ NHÂN GIAN','Tran Anh Hung','2024-03-22',135,'French - Vietnamese and English subtitles','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/_/s/_size_web_mvng-teaser-poster_1_.jpg',18),('NGÀY TÀN CỦA ĐẾ QUỐC','Alex Garland','2024-04-12',109,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/4/7/470wx700h-civilwar.jpg',19),('NGƯỜI \"BẠN\" TRONG TƯỞNG TƯỢNG','Jeff Wadlow','2024-04-12',110,'English - Vietnamese subtitles','P','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/7/0/700x1000-imaginary.jpg',20),('NGƯỜI CHẾT TRỞ VỀ','Ha Jun Won','2024-03-22',109,'Korean - Vietnamese and English subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/p/o/poster_deadman_6.jpg',21),('QUỶ CÁI','Artistaya & Theerakhaha Arriyawongsa','2024-04-05',90,'Thai - Vietnamese subtitles','T18','https://img-s-msn-com.akamaized.net/tenant/amp/entityid/BB1kUyIf.img?w=612&h=876&m=6.jpg',22),('QUỶ THUẬT','Visal Sem','2024-03-29',106,'Cambodian - Vietnamese subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/4/0/406x600-blacknun.jpg',23),('SÁNG ĐÈN','Hoang Tuan Cuong','2024-03-22',128,'Vietnamese - English subtitles','K','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/s/a/sangden-new_main-gui.jpg',24),('SUGA','JUNSOO PARK','2024-04-10',84,'Korean - Vietnamese subtitles','K','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/s/u/suga_poster_700x1000.jpg',25),('THANH XUÂN 18x2: LỮ TRÌNH HƯỚNG VỀ EM','Fujii Michihito','2024-04-12',123,'Japanese & Chinese - Vietnamese subtitles','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/4/0/406x600-18x2.jpg',26),('TRÒ CHƠI CHẾT CHÓC','Nirun Toomprecha','2024-04-05',90,'Thai - Vietnamese and English subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/t/r/tr_ch_i_ch_t_ch_c_-_poster_kt_facebook.jpg',27),('TU VIỆN MÁU','Michael Mohan','2024-04-19',85,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/p/o/poster_tu_vien_mau_8.jpg',28),('ARTHUR: CHÚ CHÓ KIÊN CƯỜNG','Simon Cellan Jones','2024-05-03',107,'English - Vietnamese subtitles','K','https://metiz.vn/media/poster_film/700x1000-arthur.jpg',29),('NẮM ĐẤM TRỜI BAN','Hoon Koh','2024-05-03',110,'Korean - Vietnamese subtitles','K','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/7/0/700x1000-holy_punch_1_.jpg',30),('NHỮNG KẺ THÁCH ĐẤU','Luca Guadagnino','2024-05-10',130,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/p/o/poster_nhung_ke_thach_dau_2.jpg',31),('Ổ NHỆN','Ricard Cussó, Tania Vincent','2024-05-10',106,'French - Vietnamese and English subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/_/n/_nh_n_-_payoff_poster_-_dkkc_10.05.2024_-_kt_facebook.jpg',32),('FURIOSA: CÂU CHUYỆN TỪ MAX ĐIÊN','George Miller','2024-05-24',121,'English - Vietnamese subtitles','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/v/n/vn_vietnam_friosa_vert_first_poster_2764x4096_intl_1_.jpg',33),('MÈO BÉO SIÊU QUẬY','Mark Dindal','2024-05-31',106,'English - Vietnamese subtitles','K','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/p/o/poster_tam.jpg',34),('Deadpool 3: DEADPOOL & WOLVERINE','Shawn Levy','2024-07-26',126,'English - Vietnamese subtitles','T13','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/3/image/c5f0a1eff4c394a251036189ccddaacd/d/p/dp3.jpg',35),('VÙNG ĐẤT CÂM LẶNG: NGÀY MỘT','Michael Sarnoski','2024-06-28',132,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/a/q/aqpd1_intl_dgtl_online_tsr_1sht_panic_vie_470x700.jpg',36),('NHỮNG KẺ THEO DÕI','Ishana Shyamalan','2024-06-14',110,'English - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/p/o/poster_nhung_ke_theo_doi_1_.jpg',37),('KÍNH VẠN HOA','','2024-05-25',110,'Vietnamese - English subtitles','K','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/3/image/c5f0a1eff4c394a251036189ccddaacd/d/_/d_n_k_nh_v_n_hoa_1_.jpg',38),('LỐC XOÁY TỬ THẦN','Lee Isaac','2024-07-19',125,'English - Vietnamese subtitles','T16','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/1800x/71252117777b696995f01934522c402d/v/n/vn_teaser_poster_twisters_1_.jpg',39),('TRANSFORMERS ONE','Josh Cooley','2024-09-20',101,'English - Vietnamese subtitles','K','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQm2ANROkq6qB7-H4-s602-2htTiX5KhitnKT2g4Jh6KQ&s',40),('NGÔI ĐỀN KỲ QUÁI','Bhuripat Vejvongsatechawat, Phiravich Attachitsataporn','2024-05-31',110,'Thai - Vietnamese subtitles','T18','https://iguov8nhvyobj.vcdn.cloud/media/catalog/product/cache/1/image/c5f0a1eff4c394a251036189ccddaacd/p/e/peenak3-main_poster_1_.jpg',41);
/*!40000 ALTER TABLE `dbo.MovieInfo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dbo.Showrooms`
--

DROP TABLE IF EXISTS `dbo.Showrooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbo.Showrooms` (
  `ShowroomID` tinyint(4) DEFAULT NULL,
  `ShowroomName` varchar(8) DEFAULT NULL,
  `MaxChairs` smallint(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbo.Showrooms`
--

LOCK TABLES `dbo.Showrooms` WRITE;
/*!40000 ALTER TABLE `dbo.Showrooms` DISABLE KEYS */;
INSERT INTO `dbo.Showrooms` VALUES (1,'Mini',100),(2,'Standard',200),(3,'Standard',200),(4,'IMAX',380);
/*!40000 ALTER TABLE `dbo.Showrooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dbo.Showtimes`
--

DROP TABLE IF EXISTS `dbo.Showtimes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbo.Showtimes` (
  `ShowtimeID` smallint(6) DEFAULT NULL,
  `Time` varchar(8) DEFAULT NULL,
  `MovieId` tinyint(4) DEFAULT NULL,
  `Date` varchar(10) DEFAULT NULL,
  `ShowroomID` tinyint(4) DEFAULT NULL,
  `ReservedChairs` tinyint(4) DEFAULT NULL,
  `Chairs_Booked` varchar(16) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbo.Showtimes`
--

LOCK TABLES `dbo.Showtimes` WRITE;
/*!40000 ALTER TABLE `dbo.Showtimes` DISABLE KEYS */;
INSERT INTO `dbo.Showtimes` VALUES (1,'10:00:00',25,'2024-04-23',1,0,''),(2,'13:00:00',4,'2024-04-23',1,0,''),(3,'16:00:00',3,'2024-04-23',1,0,''),(4,'19:00:00',19,'2024-04-23',1,0,''),(5,'10:30:00',12,'2024-04-23',2,0,''),(6,'13:30:00',20,'2024-04-23',2,0,''),(7,'16:30:00',26,'2024-04-23',2,0,''),(8,'19:30:00',8,'2024-04-23',2,0,''),(9,'10:00:00',25,'2024-04-23',3,0,''),(10,'13:00:00',4,'2024-04-23',3,0,''),(11,'16:00:00',3,'2024-04-23',3,0,''),(12,'19:00:00',19,'2024-04-23',3,0,''),(13,'10:30:00',12,'2024-04-23',4,0,''),(14,'13:30:00',20,'2024-04-23',4,0,''),(15,'16:30:00',26,'2024-04-23',4,0,''),(16,'19:30:00',8,'2024-04-23',4,0,''),(17,'10:00:00',25,'2024-04-25',1,0,''),(18,'13:00:00',4,'2024-04-25',1,0,''),(19,'16:00:00',3,'2024-04-25',1,0,''),(20,'19:00:00',19,'2024-04-25',1,0,''),(21,'10:30:00',12,'2024-04-25',2,0,''),(22,'13:30:00',20,'2024-04-25',2,0,''),(23,'16:30:00',26,'2024-04-25',2,0,''),(24,'19:30:00',8,'2024-04-25',2,0,''),(25,'10:00:00',25,'2024-04-25',3,0,''),(26,'13:00:00',4,'2024-04-25',3,0,''),(27,'16:00:00',3,'2024-04-25',3,0,''),(28,'19:00:00',19,'2024-04-25',3,0,''),(29,'10:30:00',12,'2024-04-25',4,0,''),(30,'13:30:00',20,'2024-04-25',4,0,''),(31,'16:30:00',26,'2024-04-25',4,0,''),(32,'19:30:00',8,'2024-04-25',4,0,''),(33,'10:00:00',25,'2024-04-24',1,0,''),(34,'13:00:00',4,'2024-04-24',1,0,''),(35,'16:00:00',3,'2024-04-24',1,0,''),(36,'19:00:00',19,'2024-04-24',1,0,''),(37,'10:30:00',12,'2024-04-24',2,0,''),(38,'13:30:00',20,'2024-04-24',2,0,''),(39,'16:30:00',26,'2024-04-24',2,0,''),(40,'19:30:00',8,'2024-04-24',2,0,''),(41,'10:00:00',25,'2024-04-24',3,0,''),(42,'13:00:00',4,'2024-04-24',3,0,''),(43,'16:00:00',3,'2024-04-24',3,0,''),(44,'19:00:00',19,'2024-04-24',3,0,''),(45,'10:30:00',12,'2024-04-24',4,0,''),(46,'13:30:00',20,'2024-04-24',4,0,''),(47,'16:30:00',26,'2024-04-24',4,0,''),(48,'19:30:00',8,'2024-04-24',4,0,''),(49,'10:00:00',25,'2024-04-28',1,0,''),(50,'13:00:00',4,'2024-04-28',1,0,''),(51,'16:00:00',3,'2024-04-28',1,0,''),(52,'19:00:00',19,'2024-04-28',1,0,''),(53,'10:30:00',12,'2024-04-28',2,0,''),(54,'13:30:00',20,'2024-04-28',2,0,''),(55,'16:30:00',26,'2024-04-28',2,0,''),(56,'19:30:00',8,'2024-04-28',2,0,''),(57,'10:00:00',25,'2024-04-28',3,0,''),(58,'13:00:00',4,'2024-04-28',3,0,''),(59,'16:00:00',3,'2024-04-28',3,0,''),(60,'19:00:00',19,'2024-04-28',3,0,''),(61,'10:30:00',12,'2024-04-28',4,0,''),(62,'13:30:00',20,'2024-04-28',4,0,''),(63,'16:30:00',26,'2024-04-28',4,0,''),(64,'19:30:00',8,'2024-04-28',4,0,''),(65,'10:00:00',25,'2024-04-29',1,0,''),(66,'13:00:00',4,'2024-04-29',1,0,''),(67,'16:00:00',3,'2024-04-29',1,0,''),(68,'19:00:00',19,'2024-04-29',1,0,''),(69,'10:30:00',12,'2024-04-29',2,0,''),(70,'13:30:00',20,'2024-04-29',2,0,''),(71,'16:30:00',26,'2024-04-29',2,0,''),(72,'19:30:00',8,'2024-04-29',2,0,''),(73,'10:00:00',25,'2024-04-29',3,0,''),(74,'13:00:00',4,'2024-04-29',3,0,''),(75,'16:00:00',3,'2024-04-29',3,0,''),(76,'19:00:00',19,'2024-04-29',3,0,''),(77,'10:30:00',12,'2024-04-29',4,0,''),(78,'13:30:00',20,'2024-04-29',4,0,''),(79,'16:30:00',26,'2024-04-29',4,0,''),(80,'19:30:00',8,'2024-04-29',4,0,''),(81,'10:00:00',25,'2024-04-29',1,0,''),(82,'13:00:00',4,'2024-04-29',1,0,''),(83,'16:00:00',3,'2024-04-29',1,0,''),(84,'19:00:00',19,'2024-04-29',1,0,''),(85,'10:30:00',12,'2024-04-29',2,0,''),(86,'13:30:00',20,'2024-04-29',2,0,''),(87,'16:30:00',26,'2024-04-29',2,0,''),(88,'19:30:00',8,'2024-04-29',2,0,''),(89,'10:00:00',25,'2024-04-29',3,0,''),(90,'13:00:00',4,'2024-04-29',3,0,''),(91,'16:00:00',3,'2024-04-29',3,0,''),(92,'19:00:00',19,'2024-04-29',3,0,''),(93,'10:30:00',12,'2024-04-29',4,0,''),(94,'13:30:00',20,'2024-04-29',4,0,''),(95,'16:30:00',26,'2024-04-29',4,0,''),(96,'19:30:00',8,'2024-04-29',4,0,''),(97,'10:00:00',25,'2024-04-30',1,0,''),(98,'13:00:00',4,'2024-04-30',1,0,''),(99,'16:00:00',3,'2024-04-30',1,0,''),(100,'19:00:00',19,'2024-04-30',1,0,'');
/*!40000 ALTER TABLE `dbo.Showtimes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dbo.Transactions`
--

DROP TABLE IF EXISTS `dbo.Transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbo.Transactions` (
  `TransactionID` varchar(0) DEFAULT NULL,
  `TransactionDate` varchar(0) DEFAULT NULL,
  `MovieId` varchar(0) DEFAULT NULL,
  `Amount` varchar(0) DEFAULT NULL,
  `SeatsPreserved` varchar(0) DEFAULT NULL,
  `ShowroomID` varchar(0) DEFAULT NULL,
  `account_email` varchar(0) DEFAULT NULL,
  `ShowtimeID` varchar(0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbo.Transactions`
--

LOCK TABLES `dbo.Transactions` WRITE;
/*!40000 ALTER TABLE `dbo.Transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `dbo.Transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dbo.sysdiagrams`
--

DROP TABLE IF EXISTS `dbo.sysdiagrams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dbo.sysdiagrams` (
  `name` varchar(0) DEFAULT NULL,
  `principal_id` varchar(0) DEFAULT NULL,
  `diagram_id` varchar(0) DEFAULT NULL,
  `version` varchar(0) DEFAULT NULL,
  `definition` varchar(0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dbo.sysdiagrams`
--

LOCK TABLES `dbo.sysdiagrams` WRITE;
/*!40000 ALTER TABLE `dbo.sysdiagrams` DISABLE KEYS */;
/*!40000 ALTER TABLE `dbo.sysdiagrams` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-04-30 16:42:43
