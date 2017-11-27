-- phpMyAdmin SQL Dump
-- version 4.7.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Nov 25, 2017 at 04:34 PM
-- Server version: 5.7.19
-- PHP Version: 5.6.31

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `snapit`
--

-- --------------------------------------------------------

--
-- Table structure for table `slike`
--

DROP TABLE IF EXISTS `slike`;
CREATE TABLE IF NOT EXISTS `slike` (
  `androidID` varchar(30) NOT NULL,
  `naziv` varchar(30) NOT NULL,
  `putanja` varchar(30) NOT NULL,
  PRIMARY KEY (`naziv`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `verifikacijakorisnika`
--

DROP TABLE IF EXISTS `verifikacijakorisnika`;
CREATE TABLE IF NOT EXISTS `verifikacijakorisnika` (
  `kod` varchar(60) NOT NULL,
  `androidID` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`kod`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `verifikacijakorisnika`
--

INSERT INTO `verifikacijakorisnika` (`kod`, `androidID`) VALUES
('51ef186e18dc00c2d31982567235c559', NULL),
('f1c1592588411002af340cbaedd6fc33', NULL),
('0336dcbab05b9d5ad24f4333c7658a0e', '4b68ac6e7ce5ed1f'),
('1700002963a49da13542e0726b7bb758', NULL),
('c86a7ee3d8ef0b551ed58e354a836f2b', NULL),
('7d04bbbe5494ae9d2f5a76aa1c00fa2f', NULL),
('9ad6aaed513b73148b7d49f70afcfb32', NULL),
('fccb3cdc9acc14a6e70a12f74560c026', NULL),
('6602294be910b1e3c4571bd98c4d5484', NULL),
('53adaf494dc89ef7196d73636eb2451b', NULL),
('a3fb4fbf9a6f9cf09166aa9c20cbc1ad', NULL),
('dc4c44f624d600aa568390f1f1104aa0', NULL),
('8d3369c4c086f236fabf61d614a32818', NULL),
('5e76bef6e019b2541ff53db39f407a98', NULL),
('18ead4c77c3f40dabf9735432ac9d97a', NULL);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
