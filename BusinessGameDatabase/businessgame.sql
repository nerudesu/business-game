-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jul 18, 2012 at 05:32 AM
-- Server version: 5.5.16
-- PHP Version: 5.3.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `businessgame`
--

-- --------------------------------------------------------

--
-- Table structure for table `borrow_bank`
--

CREATE TABLE IF NOT EXISTS `borrow_bank` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `turn` bigint(20) NOT NULL,
  `money` decimal(10,2) NOT NULL,
  `interest` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `info_employee`
--

CREATE TABLE IF NOT EXISTS `info_employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `base_op_cost` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=7 ;

--
-- Dumping data for table `info_employee`
--

INSERT INTO `info_employee` (`id`, `name`, `quality`, `base_price`, `base_op_cost`) VALUES
(1, 'Employee', 1, 4.00, 0.24),
(2, 'Manager', 1, 12.00, 0.56),
(3, 'Employee', 2, 5.00, 0.30),
(4, 'Manager', 2, 15.00, 0.70),
(5, 'Employee', 3, 7.50, 0.45),
(6, 'Manager', 3, 22.50, 1.05);

-- --------------------------------------------------------

--
-- Table structure for table `info_equipment`
--

CREATE TABLE IF NOT EXISTS `info_equipment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `base_op_cost` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `info_equipment`
--

INSERT INTO `info_equipment` (`id`, `name`, `quality`, `base_price`, `base_op_cost`, `size`) VALUES
(1, 'Drill', 1, 9.60, 0.05, 8.00),
(2, 'Furnace', 1, 16.00, 0.05, 4.00),
(3, 'Generator', 1, 6.40, 0.04, 6.00),
(4, 'Machine', 1, 6.00, 0.01, 6.00),
(5, 'Pump', 1, 7.20, 0.03, 6.00),
(6, 'Drill', 2, 12.00, 0.06, 8.00),
(7, 'Furnace', 2, 20.00, 0.06, 4.00),
(8, 'Generator', 2, 8.00, 0.05, 6.00),
(9, 'Machine', 2, 7.50, 0.02, 6.00),
(10, 'Pump', 2, 9.00, 0.04, 6.00),
(11, 'Drill', 3, 18.00, 0.09, 8.00),
(12, 'Furnace', 3, 30.00, 0.09, 4.00),
(13, 'Generator', 3, 12.00, 0.08, 6.00),
(14, 'Machine', 3, 11.25, 0.03, 6.00),
(15, 'Pump', 3, 13.50, 0.06, 6.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_product`
--

CREATE TABLE IF NOT EXISTS `info_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=28 ;

--
-- Dumping data for table `info_product`
--

INSERT INTO `info_product` (`id`, `name`, `quality`, `base_price`) VALUES
(1, 'Chemical', 1, 14.44),
(2, 'Crude Oil', 1, 2.40),
(3, 'Energy', 1, 2.40),
(4, 'Gasoline', 1, 16.80),
(5, 'Iron', 1, 1.52),
(6, 'Silica', 1, 2.00),
(7, 'Trash', 1, -0.39),
(8, 'Wastewater', 1, -0.65),
(9, 'Water', 1, 0.80),
(10, 'Chemical', 2, 18.05),
(11, 'Crude Oil', 2, 3.00),
(12, 'Energy', 2, 3.00),
(13, 'Gasoline', 2, 21.00),
(14, 'Iron', 2, 1.90),
(15, 'Silica', 2, 2.50),
(16, 'Trash', 2, -0.30),
(17, 'Wastewater', 2, -0.50),
(18, 'Water', 2, 1.00),
(19, 'Chemical', 3, 27.07),
(20, 'Crude Oil', 3, 4.50),
(21, 'Energy', 3, 4.50),
(22, 'Gasoline', 3, 31.50),
(23, 'Iron', 3, 2.85),
(24, 'Silica', 3, 3.75),
(25, 'Trash', 3, -0.15),
(26, 'Wastewater', 3, -0.25),
(27, 'Water', 3, 1.50);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector`
--

CREATE TABLE IF NOT EXISTS `info_sector` (
  `name` varchar(25) NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  `prob` decimal(10,2) NOT NULL,
  `level` bigint(20) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector`
--

INSERT INTO `info_sector` (`name`, `cost`, `prob`, `level`) VALUES
('Chemical Plant', 100.00, 0.70, 1),
('Iron Mine', 200.00, 0.45, 2),
('Oil Refinery', 100.00, 0.70, 1),
('Oil Well', 200.00, 0.45, 2),
('Petrol Power Plant', 300.00, 0.20, 3),
('Silica Mine', 200.00, 0.45, 2),
('Water Well', 200.00, 0.45, 2);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_employee`
--

CREATE TABLE IF NOT EXISTS `info_sector_employee` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `employee_type` varchar(20) NOT NULL,
  `items` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=15 ;

--
-- Dumping data for table `info_sector_employee`
--

INSERT INTO `info_sector_employee` (`id`, `name`, `employee_type`, `items`) VALUES
(1, 'Water Well', 'Manager', 2),
(2, 'Water Well', 'Employee', 8),
(3, 'Oil Well', 'Manager', 3),
(4, 'Oil Well', 'Employee', 12),
(5, 'Oil Refinery', 'Manager', 4),
(6, 'Oil Refinery', 'Employee', 16),
(7, 'Silica Mine', 'Manager', 2),
(8, 'Silica Mine', 'Employee', 6),
(9, 'Iron Mine', 'Manager', 2),
(10, 'Iron Mine', 'Employee', 6),
(11, 'Chemical Plant', 'Manager', 4),
(12, 'Chemical Plant', 'Employee', 12),
(13, 'Petrol Power Plant', 'Manager', 5),
(14, 'Petrol Power Plant', 'Employee', 25);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_equipment`
--

CREATE TABLE IF NOT EXISTS `info_sector_equipment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `equipment_type` varchar(20) NOT NULL,
  `items` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=26 ;

--
-- Dumping data for table `info_sector_equipment`
--

INSERT INTO `info_sector_equipment` (`id`, `name`, `equipment_type`, `items`) VALUES
(1, 'Water Well', 'Machine', 50),
(2, 'Water Well', 'Pump', 150),
(3, 'Water Well', 'Generator', 10),
(4, 'Water Well', 'Drill', 20),
(5, 'Oil Well', 'Machine', 60),
(6, 'Oil Well', 'Pump', 150),
(7, 'Oil Well', 'Generator', 15),
(8, 'Oil Well', 'Drill', 33),
(9, 'Oil Refinery', 'Machine', 60),
(10, 'Oil Refinery', 'Pump', 120),
(11, 'Oil Refinery', 'Generator', 12),
(12, 'Silica Mine', 'Machine', 120),
(13, 'Silica Mine', 'Generator', 60),
(14, 'Silica Mine', 'Drill', 240),
(15, 'Iron Mine', 'Machine', 40),
(16, 'Iron Mine', 'Generator', 20),
(17, 'Iron Mine', 'Drill', 300),
(18, 'Chemical Plant', 'Machine', 28),
(19, 'Chemical Plant', 'Pump', 36),
(20, 'Chemical Plant', 'Generator', 8),
(21, 'Chemical Plant', 'Furnace', 52),
(22, 'Petrol Power Plant', 'Machine', 200),
(23, 'Petrol Power Plant', 'Pump', 50),
(24, 'Petrol Power Plant', 'Generator', 45),
(25, 'Petrol Power Plant', 'Furnace', 100);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_input`
--

CREATE TABLE IF NOT EXISTS `info_sector_input` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `input_type` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `info_sector_input`
--

INSERT INTO `info_sector_input` (`id`, `name`, `input_type`, `size`) VALUES
(1, 'Water Well', 'Energy', 100.00),
(2, 'Oil Well', 'Energy', 180.00),
(3, 'Oil Refinery', 'Energy', 120.00),
(4, 'Oil Refinery', 'Crude Oil', 120.00),
(5, 'Silica Mine', 'Energy', 70.00),
(6, 'Silica Mine', 'Water', 350.00),
(7, 'Iron Mine', 'Energy', 70.00),
(8, 'Iron Mine', 'Water', 350.00),
(9, 'Chemical Plant', 'Energy', 100.00),
(10, 'Chemical Plant', 'Crude Oil', 60.00),
(11, 'Chemical Plant', 'Silica', 160.00),
(12, 'Chemical Plant', 'Iron', 200.00),
(13, 'Petrol Power Plant', 'Water', 350.00),
(14, 'Petrol Power Plant', 'Gasoline', 95.00),
(15, 'Petrol Power Plant', 'Chemical', 85.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_output`
--

CREATE TABLE IF NOT EXISTS `info_sector_output` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(25) NOT NULL,
  `output_type` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=16 ;

--
-- Dumping data for table `info_sector_output`
--

INSERT INTO `info_sector_output` (`id`, `name`, `output_type`, `size`) VALUES
(1, 'Water Well', 'Water', 600.00),
(2, 'Water Well', 'Trash', 100.00),
(3, 'Oil Well', 'Crude Oil', 300.00),
(4, 'Oil Well', 'Trash', 180.00),
(5, 'Oil Refinery', 'Gasoline', 44.00),
(6, 'Oil Refinery', 'Wastewater', 28.00),
(7, 'Silica Mine', 'Silica', 450.00),
(8, 'Silica Mine', 'Trash', 100.00),
(9, 'Silica Mine', 'Wastewater', 26.00),
(10, 'Iron Mine', 'Iron', 600.00),
(11, 'Iron Mine', 'Trash', 180.00),
(12, 'Iron Mine', 'Wastewater', 80.00),
(13, 'Chemical Plant', 'Chemical', 80.00),
(14, 'Petrol Power Plant', 'Energy', 1500.00),
(15, 'Petrol Power Plant', 'Wastewater', 100.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_values`
--

CREATE TABLE IF NOT EXISTS `info_values` (
  `name` varchar(15) NOT NULL,
  `value` varchar(15) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_values`
--

INSERT INTO `info_values` (`name`, `value`) VALUES
('interest', '0.12'),
('sector', '1000'),
('storage', '1000'),
('storage_inc', '500'),
('turn', '710');

-- --------------------------------------------------------

--
-- Table structure for table `info_zone`
--

CREATE TABLE IF NOT EXISTS `info_zone` (
  `id` varchar(5) NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  `transport_in` decimal(10,2) NOT NULL,
  `transport_out` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_zone`
--

INSERT INTO `info_zone` (`id`, `cost`, `transport_in`, `transport_out`) VALUES
('A', 100.00, 0.05, 0.20),
('B', 100.00, 0.05, 0.20);

-- --------------------------------------------------------

--
-- Table structure for table `installment`
--

CREATE TABLE IF NOT EXISTS `installment` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `type` varchar(25) NOT NULL,
  `supply` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment`
--

INSERT INTO `installment` (`id`, `user`, `zone`, `type`, `supply`) VALUES
('IN1707120001', 'ardhi', 'A', 'Chemical Plant', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `installment_employee`
--

CREATE TABLE IF NOT EXISTS `installment_employee` (
  `id` varchar(15) NOT NULL,
  `installment` varchar(15) NOT NULL,
  `employee` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment_employee`
--

INSERT INTO `installment_employee` (`id`, `installment`, `employee`, `quality`, `operational`) VALUES
('EM1707120001', 'IN1707120001', 'Manager', 2, 0.70),
('EM1707120002', 'IN1707120001', 'Manager', 2, 0.70),
('EM1707120003', 'IN1707120001', 'Manager', 2, 0.70),
('EM1707120004', 'IN1707120001', 'Employee', 2, 0.30),
('EM1707120005', 'IN1707120001', 'Employee', 2, 0.30),
('EM1707120006', 'IN1707120001', 'Employee', 2, 0.30),
('EM1707120007', 'IN1707120001', 'Employee', 2, 0.30),
('EM1707120008', 'IN1707120001', 'Employee', 2, 0.30),
('EM1707120009', 'IN1707120001', 'Employee', 2, 0.30),
('EM1707120010', 'IN1707120001', 'Employee', 2, 0.30);

-- --------------------------------------------------------

--
-- Table structure for table `installment_equipment`
--

CREATE TABLE IF NOT EXISTS `installment_equipment` (
  `id` varchar(15) NOT NULL,
  `installment` varchar(15) NOT NULL,
  `equipment` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `durability` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment_equipment`
--

INSERT INTO `installment_equipment` (`id`, `installment`, `equipment`, `quality`, `durability`, `size`, `operational`) VALUES
('EQ1707120001', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120002', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120003', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120004', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120005', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120006', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120007', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120008', 'IN1707120001', 'Machine', 2, 75.50, 6.00, 0.02),
('EQ1707120009', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120010', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120011', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120012', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120013', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120014', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120015', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120016', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120017', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120018', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120019', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120020', 'IN1707120001', 'Pump', 2, 75.25, 6.00, 0.04),
('EQ1707120021', 'IN1707120001', 'Generator', 2, 75.75, 6.00, 0.05),
('EQ1707120022', 'IN1707120001', 'Generator', 2, 75.75, 6.00, 0.05),
('EQ1707120023', 'IN1707120001', 'Generator', 2, 75.75, 6.00, 0.05),
('EQ1707120024', 'IN1707120001', 'Generator', 2, 75.75, 6.00, 0.05),
('EQ1707120025', 'IN1707120001', 'Furnace', 2, 75.50, 4.00, 0.06),
('EQ1707120026', 'IN1707120001', 'Furnace', 2, 75.50, 4.00, 0.06),
('EQ1707120027', 'IN1707120001', 'Furnace', 2, 75.50, 4.00, 0.06),
('EQ1707120028', 'IN1707120001', 'Furnace', 2, 75.50, 4.00, 0.06),
('EQ1707120029', 'IN1707120001', 'Furnace', 2, 75.50, 4.00, 0.06),
('EQ1707120030', 'IN1707120001', 'Furnace', 2, 75.50, 4.00, 0.06);

-- --------------------------------------------------------

--
-- Table structure for table `market`
--

CREATE TABLE IF NOT EXISTS `market` (
  `id` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `zone` (`zone`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market`
--

INSERT INTO `market` (`id`, `zone`) VALUES
('MR2806120001', 'A'),
('MR2806120002', 'B');

-- --------------------------------------------------------

--
-- Table structure for table `market_employee`
--

CREATE TABLE IF NOT EXISTS `market_employee` (
  `id` varchar(15) NOT NULL,
  `market` varchar(15) NOT NULL,
  `employee` varchar(25) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quality` int(11) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_employee`
--

INSERT INTO `market_employee` (`id`, `market`, `employee`, `price`, `quality`, `operational`) VALUES
('EM0207120001', 'MR2806120001', 'Employee', 5.00, 2, 0.30),
('EM0207120002', 'MR2806120001', 'Manager', 22.50, 3, 1.05);

-- --------------------------------------------------------

--
-- Table structure for table `market_equipment`
--

CREATE TABLE IF NOT EXISTS `market_equipment` (
  `id` varchar(15) NOT NULL,
  `market` varchar(15) NOT NULL,
  `storage` varchar(15) NOT NULL,
  `user` varchar(15) DEFAULT NULL,
  `equipment` varchar(25) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quality` int(11) NOT NULL,
  `durability` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_equipment`
--

INSERT INTO `market_equipment` (`id`, `market`, `storage`, `user`, `equipment`, `price`, `quality`, `durability`, `size`, `operational`) VALUES
('EQ0207120001', 'MR2806120001', 'ST1906120001', 'ardhi', 'Furnace', 28.50, 3, 76.00, 4.00, 0.09),
('EQ0207120002', 'MR2806120001', 'ST1906120001', 'ardhi', 'Generator', 5.90, 1, 67.95, 6.00, 0.08),
('EQ0207120003', 'MR2806120001', '', '', 'Drill', 9.60, 1, 100.00, 8.00, 0.05);

-- --------------------------------------------------------

--
-- Table structure for table `market_product`
--

CREATE TABLE IF NOT EXISTS `market_product` (
  `id` varchar(15) NOT NULL,
  `market` varchar(15) NOT NULL,
  `storage` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `product` varchar(20) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quality` int(11) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_product`
--

INSERT INTO `market_product` (`id`, `market`, `storage`, `user`, `product`, `price`, `quality`, `size`) VALUES
('PR0207120001', 'MR2806120001', 'ST1906120001', 'ardhi', 'Gasoline', 22.05, 2, 12.00),
('PR0907120001', 'MR2806120001', 'ST1706120001', 'bagus', 'Gasoline', 21.00, 2, 6.07);

-- --------------------------------------------------------

--
-- Table structure for table `req_borrow_bank`
--

CREATE TABLE IF NOT EXISTS `req_borrow_bank` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `turn` bigint(20) NOT NULL,
  `money` decimal(10,2) NOT NULL,
  `prob` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `storage`
--

CREATE TABLE IF NOT EXISTS `storage` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `level` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storage`
--

INSERT INTO `storage` (`id`, `user`, `zone`, `level`) VALUES
('ST1706120001', 'bagus', 'A', 1),
('ST1906120001', 'ardhi', 'A', 1);

-- --------------------------------------------------------

--
-- Table structure for table `storage_equipment`
--

CREATE TABLE IF NOT EXISTS `storage_equipment` (
  `id` varchar(15) NOT NULL,
  `storage` varchar(15) NOT NULL,
  `equipment` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `durability` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  `offer` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storage_equipment`
--

INSERT INTO `storage_equipment` (`id`, `storage`, `equipment`, `quality`, `durability`, `size`, `operational`, `offer`) VALUES
('EQ0107120001 ', 'ST1906120001', 'Furnace', 3, 76.00, 4.00, 0.09, 1),
('EQ0107120002', 'ST1906120001', 'Generator', 1, 67.95, 6.00, 0.08, 1),
('EQ1806120001', 'ST1706120001', 'Furnace', 1, 35.00, 4.00, 0.05, 0),
('EQ1806120002', 'ST1706120001', 'Machine', 2, 85.00, 6.00, 0.02, 0),
('EQ1806120003', 'ST1706120001', 'Furnace', 3, 20.00, 4.00, 0.09, 0),
('EQ1806120004', 'ST1706120001', 'Furnace', 3, 60.00, 4.00, 0.09, 0),
('EQ1806120005', 'ST1706120001', 'Machine', 3, 45.00, 6.00, 0.03, 0);

-- --------------------------------------------------------

--
-- Table structure for table `storage_product`
--

CREATE TABLE IF NOT EXISTS `storage_product` (
  `id` varchar(15) NOT NULL,
  `storage` varchar(15) NOT NULL,
  `product` varchar(20) NOT NULL,
  `quality` int(11) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  `offer` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storage_product`
--

INSERT INTO `storage_product` (`id`, `storage`, `product`, `quality`, `size`, `offer`) VALUES
('PR0107120001', 'ST1906120001', 'Gasoline', 2, 17.00, 0),
('PR0107120002', 'ST1906120001', 'Gasoline', 2, 12.00, 1),
('PR0307120001', 'ST1706120001', 'Gasoline', 2, 18.07, 0),
('PR0907120001', 'ST1706120001', 'Gasoline', 2, 6.07, 1),
('PR1806120001', 'ST1706120001', 'Crude Oil', 2, 100.50, 0),
('PR1806120002', 'ST1706120001', 'Water', 3, 50.25, 0),
('PR1806120003', 'ST1706120001', 'Crude Oil', 1, 20.75, 0),
('PR1806120004', 'ST1706120001', 'Energy', 2, 130.75, 0),
('PR1806120005', 'ST1706120001', 'Silica', 1, 25.00, 0),
('PR1806120006', 'ST1706120001', 'Iron', 2, 10.00, 0),
('PR1806120007', 'ST1706120001', 'Trash', 1, 50.00, 0);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `name` varchar(15) NOT NULL,
  `pass` varchar(15) NOT NULL,
  `email` varchar(25) NOT NULL,
  `dob` varchar(50) NOT NULL,
  `about` text NOT NULL,
  `avatar` text NOT NULL,
  `money` decimal(10,2) NOT NULL,
  `rep` bigint(20) NOT NULL,
  `zone` varchar(5) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`name`, `pass`, `email`, `dob`, `about`, `avatar`, `money`, `rep`, `zone`) VALUES
('aaa', 'aaa', 'aaa', 'Oct 1 1985', 'This is me', '', 0.00, 0, 'A'),
('ardhi', 'ardhi', 'ardhi@ardhi', 'Apr 4 1990', 'This is me', '', 466.13, 0, 'A'),
('bagus', 'bagus', 'bagus@bagus', 'Jan 1 1985', 'This is me', '', 533.87, 0, 'A');

-- --------------------------------------------------------

--
-- Table structure for table `user_market_license`
--

CREATE TABLE IF NOT EXISTS `user_market_license` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `market` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_market_license`
--

INSERT INTO `user_market_license` (`id`, `user`, `market`) VALUES
('UM1806120001', 'ardhi', 'MR2806120001'),
('UM1806120002', 'bagus', 'MR2806120001'),
('UM1806120003', 'ardhi', 'MR2806120002');

-- --------------------------------------------------------

--
-- Table structure for table `user_sector_license`
--

CREATE TABLE IF NOT EXISTS `user_sector_license` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `sector` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_sector_license`
--

INSERT INTO `user_sector_license` (`id`, `user`, `sector`) VALUES
('US1806120001', 'bagus', 'Oil Refinery');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
