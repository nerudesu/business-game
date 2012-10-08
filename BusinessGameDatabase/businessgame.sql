-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 08, 2012 at 10:48 AM
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
  `borrow` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `borrow_bank`
--

INSERT INTO `borrow_bank` (`id`, `user`, `turn`, `borrow`) VALUES
('BB0810120000', 'Pioneer', 12, 2700.79),
('BB0810120001', 'bagus', 7, 2700.79);

-- --------------------------------------------------------

--
-- Table structure for table `desc_advertisement`
--

CREATE TABLE IF NOT EXISTS `desc_advertisement` (
  `id` varchar(15) NOT NULL,
  `name` varchar(25) NOT NULL,
  `multiplier` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desc_advertisement`
--

INSERT INTO `desc_advertisement` (`id`, `name`, `multiplier`) VALUES
('ADINET', 'Internet', 3),
('ADNEWS', 'Newspaper', 1),
('ADTLVS', 'Television', 6);

-- --------------------------------------------------------

--
-- Table structure for table `desc_employee`
--

CREATE TABLE IF NOT EXISTS `desc_employee` (
  `id` varchar(15) NOT NULL,
  `employee` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desc_employee`
--

INSERT INTO `desc_employee` (`id`, `employee`, `quality`, `price`, `operational`) VALUES
('EMEMPL01', 'Employee', 1, 4.00, 0.24),
('EMEMPL02', 'Employee', 2, 5.00, 0.30),
('EMEMPL03', 'Employee', 3, 7.50, 0.45),
('EMMNGR01', 'Manager', 1, 12.00, 0.56),
('EMMNGR02', 'Manager', 2, 15.00, 0.70),
('EMMNGR03', 'Manager', 3, 22.50, 1.05);

-- --------------------------------------------------------

--
-- Table structure for table `desc_equipment`
--

CREATE TABLE IF NOT EXISTS `desc_equipment` (
  `id` varchar(15) NOT NULL,
  `equipment` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `operational` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desc_equipment`
--

INSERT INTO `desc_equipment` (`id`, `equipment`, `quality`, `price`, `operational`, `size`) VALUES
('EQDRIL01', 'Drill', 1, 9.60, 0.05, 8.00),
('EQDRIL02', 'Drill', 2, 12.00, 0.06, 8.00),
('EQDRIL03', 'Drill', 3, 18.00, 0.09, 8.00),
('EQFRNC01', 'Furnace', 1, 16.00, 0.05, 4.00),
('EQFRNC02', 'Furnace', 2, 20.00, 0.06, 4.00),
('EQFRNC03', 'Furnace', 3, 30.00, 0.09, 4.00),
('EQGNTR01', 'Generator', 1, 6.40, 0.04, 6.00),
('EQGNTR02', 'Generator', 2, 8.00, 0.05, 6.00),
('EQGNTR03', 'Generator', 3, 12.00, 0.08, 6.00),
('EQMCHN01', 'Machine', 1, 6.00, 0.01, 6.00),
('EQMCHN02', 'Machine', 2, 7.50, 0.02, 6.00),
('EQMCHN03', 'Machine', 3, 11.25, 0.03, 6.00),
('EQPUMP01', 'Pump', 1, 7.20, 0.03, 6.00),
('EQPUMP02', 'Pump', 2, 9.00, 0.04, 6.00),
('EQPUMP03', 'Pump', 3, 13.50, 0.06, 6.00);

-- --------------------------------------------------------

--
-- Table structure for table `desc_product`
--

CREATE TABLE IF NOT EXISTS `desc_product` (
  `id` varchar(15) NOT NULL,
  `product` varchar(25) NOT NULL,
  `quality` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desc_product`
--

INSERT INTO `desc_product` (`id`, `product`, `quality`, `price`) VALUES
('PRCMCL01', 'Chemical', 1, 14.44),
('PRCMCL02', 'Chemical', 2, 18.05),
('PRCMCL03', 'Chemical', 3, 27.07),
('PRCROL01', 'Crude Oil', 1, 2.40),
('PRCROL02', 'Crude Oil', 2, 3.00),
('PRCROL03', 'Crude Oil', 3, 4.50),
('PRENRG00', 'Energy', 0, 3.00),
('PRGSLN01', 'Gasoline', 1, 16.80),
('PRGSLN02', 'Gasoline', 2, 21.00),
('PRGSLN03', 'Gasoline', 3, 31.50),
('PRIRON01', 'Iron', 1, 1.52),
('PRIRON02', 'Iron', 2, 1.90),
('PRIRON03', 'Iron', 3, 2.85),
('PRSLCA01', 'Silica', 1, 2.00),
('PRSLCA02', 'Silica', 2, 2.50),
('PRSLCA03', 'Silica', 3, 3.75),
('PRTRSH01', 'Trash', 1, -0.39),
('PRTRSH02', 'Trash', 2, -0.30),
('PRTRSH03', 'Trash', 3, -0.15),
('PRWATR01', 'Water', 1, 0.80),
('PRWATR02', 'Water', 2, 1.00),
('PRWATR03', 'Water', 3, 1.50),
('PRWWTR01', 'Wastewater', 1, -0.65),
('PRWWTR02', 'Wastewater', 2, -0.50),
('PRWWTR03', 'Wastewater', 3, -0.25);

-- --------------------------------------------------------

--
-- Table structure for table `info_employee`
--

CREATE TABLE IF NOT EXISTS `info_employee` (
  `name` varchar(25) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `base_operational` decimal(10,2) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_employee`
--

INSERT INTO `info_employee` (`name`, `base_price`, `base_operational`) VALUES
('Employee', 5.00, 0.30),
('Manager', 15.00, 0.70);

-- --------------------------------------------------------

--
-- Table structure for table `info_equipment`
--

CREATE TABLE IF NOT EXISTS `info_equipment` (
  `name` varchar(25) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `base_operational` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_equipment`
--

INSERT INTO `info_equipment` (`name`, `base_price`, `base_operational`, `size`) VALUES
('Drill', 12.00, 0.06, 8.00),
('Furnace', 20.00, 0.06, 4.00),
('Generator', 8.00, 0.05, 6.00),
('Machine', 7.50, 0.02, 6.00),
('Pump', 9.00, 0.04, 6.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_product`
--

CREATE TABLE IF NOT EXISTS `info_product` (
  `name` varchar(25) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_product`
--

INSERT INTO `info_product` (`name`, `base_price`) VALUES
('Chemical', 18.05),
('Crude Oil', 3.00),
('Energy', 3.00),
('Gasoline', 21.00),
('Iron', 1.90),
('Silica', 2.50),
('Trash', -0.30),
('Wastewater', -0.50),
('Water', 1.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_quality`
--

CREATE TABLE IF NOT EXISTS `info_quality` (
  `quality` int(11) NOT NULL,
  `lowest_bound` bigint(20) NOT NULL,
  `highest_bound` bigint(20) NOT NULL,
  `multiplier` bigint(20) NOT NULL,
  `from_base` decimal(10,2) NOT NULL,
  PRIMARY KEY (`quality`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_quality`
--

INSERT INTO `info_quality` (`quality`, `lowest_bound`, `highest_bound`, `multiplier`, `from_base`) VALUES
(1, 0, 29, 20, -0.20),
(2, 30, 39, 30, 1.00),
(3, 40, 50, 50, 0.50);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector`
--

CREATE TABLE IF NOT EXISTS `info_sector` (
  `name` varchar(25) NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  `prob` decimal(10,2) NOT NULL,
  `level` int(11) NOT NULL,
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
  `id` varchar(15) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `employee_type` varchar(20) NOT NULL,
  `items` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_employee`
--

INSERT INTO `info_sector_employee` (`id`, `sector`, `employee_type`, `items`) VALUES
('SM0610120000', 'Chemical Plant', 'Manager', 1),
('SM0610120001', 'Chemical Plant', 'Employee', 3),
('SM0610120002', 'Iron Mine', 'Manager', 1),
('SM0610120003', 'Iron Mine', 'Employee', 3),
('SM0610120004', 'Oil Refinery', 'Manager', 1),
('SM0610120005', 'Oil Refinery', 'Employee', 4),
('SM0610120006', 'Oil Well', 'Manager', 1),
('SM0610120007', 'Oil Well', 'Employee', 4),
('SM0610120008', 'Petrol Power Plant', 'Manager', 1),
('SM0610120009', 'Petrol Power Plant', 'Employee', 5),
('SM0610120010', 'Silica Mine', 'Manager', 1),
('SM0610120011', 'Silica Mine', 'Employee', 3),
('SM0610120012', 'Water Well', 'Manager', 1),
('SM0610120013', 'Water Well', 'Employee', 4);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_equipment`
--

CREATE TABLE IF NOT EXISTS `info_sector_equipment` (
  `id` varchar(15) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `equipment_type` varchar(20) NOT NULL,
  `items` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_equipment`
--

INSERT INTO `info_sector_equipment` (`id`, `sector`, `equipment_type`, `items`) VALUES
('SQ0610120000', 'Chemical Plant', 'Generator', 2),
('SQ0610120001', 'Chemical Plant', 'Furnace', 13),
('SQ0610120002', 'Chemical Plant', 'Pump', 9),
('SQ0610120003', 'Chemical Plant', 'Machine', 7),
('SQ0610120004', 'Iron Mine', 'Drill', 150),
('SQ0610120005', 'Iron Mine', 'Machine', 20),
('SQ0610120006', 'Iron Mine', 'Generator', 10),
('SQ0610120007', 'Oil Refinery', 'Machine', 15),
('SQ0610120008', 'Oil Refinery', 'Pump', 30),
('SQ0610120009', 'Oil Refinery', 'Generator', 3),
('SQ0610120010', 'Oil Well', 'Drill', 11),
('SQ0610120011', 'Oil Well', 'Generator', 5),
('SQ0610120012', 'Oil Well', 'Pump', 50),
('SQ0610120013', 'Oil Well', 'Machine', 20),
('SQ0610120014', 'Petrol Power Plant', 'Pump', 10),
('SQ0610120015', 'Petrol Power Plant', 'Machine', 40),
('SQ0610120016', 'Petrol Power Plant', 'Generator', 9),
('SQ0610120017', 'Petrol Power Plant', 'Furnace', 20),
('SQ0610120018', 'Silica Mine', 'Drill', 120),
('SQ0610120019', 'Silica Mine', 'Machine', 60),
('SQ0610120020', 'Silica Mine', 'Generator', 25),
('SQ0610120021', 'Water Well', 'Generator', 5),
('SQ0610120022', 'Water Well', 'Drill', 10),
('SQ0610120023', 'Water Well', 'Pump', 75),
('SQ0610120024', 'Water Well', 'Machine', 25);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_input`
--

CREATE TABLE IF NOT EXISTS `info_sector_input` (
  `id` varchar(15) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `input_type` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_input`
--

INSERT INTO `info_sector_input` (`id`, `sector`, `input_type`, `size`) VALUES
('SI0610120000', 'Chemical Plant', 'Energy', 25.00),
('SI0610120001', 'Chemical Plant', 'Crude Oil', 15.00),
('SI0610120002', 'Chemical Plant', 'Silica', 40.00),
('SI0610120003', 'Chemical Plant', 'Iron', 50.00),
('SI0610120004', 'Iron Mine', 'Water', 175.00),
('SI0610120005', 'Iron Mine', 'Energy', 35.00),
('SI0610120006', 'Oil Refinery', 'Crude Oil', 30.00),
('SI0610120007', 'Oil Refinery', 'Energy', 30.00),
('SI0610120008', 'Oil Well', 'Energy', 60.00),
('SI0610120009', 'Petrol Power Plant', 'Chemical', 17.00),
('SI0610120010', 'Petrol Power Plant', 'Gasoline', 19.00),
('SI0610120011', 'Petrol Power Plant', 'Water', 70.00),
('SI0610120012', 'Silica Mine', 'Energy', 35.00),
('SI0610120013', 'Silica Mine', 'Water', 175.00),
('SI0610120014', 'Water Well', 'Energy', 50.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_output`
--

CREATE TABLE IF NOT EXISTS `info_sector_output` (
  `id` varchar(15) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `output_type` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_output`
--

INSERT INTO `info_sector_output` (`id`, `sector`, `output_type`, `size`) VALUES
('SO0610120000', 'Chemical Plant', 'Chemical', 40.00),
('SO0610120001', 'Iron Mine', 'Iron', 300.00),
('SO0610120002', 'Iron Mine', 'Trash', 90.00),
('SO0610120003', 'Iron Mine', 'Wastewater', 40.00),
('SO0610120004', 'Oil Refinery', 'Wastewater', 7.00),
('SO0610120005', 'Oil Refinery', 'Gasoline', 11.00),
('SO0610120006', 'Oil Well', 'Trash', 60.00),
('SO0610120007', 'Oil Well', 'Crude Oil', 100.00),
('SO0610120008', 'Petrol Power Plant', 'Energy', 300.00),
('SO0610120009', 'Petrol Power Plant', 'Wastewater', 20.00),
('SO0610120010', 'Silica Mine', 'Trash', 50.00),
('SO0610120011', 'Silica Mine', 'Wastewater', 13.00),
('SO0610120012', 'Silica Mine', 'Silica', 225.00),
('SO0610120013', 'Water Well', 'Trash', 50.00),
('SO0610120014', 'Water Well', 'Water', 300.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_values`
--

CREATE TABLE IF NOT EXISTS `info_values` (
  `name` varchar(25) NOT NULL,
  `value` varchar(15) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_values`
--

INSERT INTO `info_values` (`name`, `value`) VALUES
('cost_storage', '500'),
('inc_borrow_bank', '2'),
('inc_employee', '14'),
('inc_equipment', '141'),
('inc_installment', '3'),
('inc_market_employee', '0'),
('inc_market_equipment', '0'),
('inc_market_product', '3'),
('inc_market_share', '0'),
('inc_market_share_product', '0'),
('inc_req_borrow_bank', '2'),
('inc_storage', '6'),
('inc_storage_product', '72'),
('inc_user_market_license', '3'),
('inc_user_sector_blueprint', '9'),
('interest', '0.12'),
('last_inc_set_date', '081012'),
('max_turn_bank', '540'),
('sector', '1000'),
('storage', '1000'),
('storage_inc', '500'),
('turn', '1581');

-- --------------------------------------------------------

--
-- Table structure for table `info_zone`
--

CREATE TABLE IF NOT EXISTS `info_zone` (
  `id` varchar(5) NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  `transport_in` decimal(10,2) NOT NULL,
  `transport_out` decimal(10,2) NOT NULL,
  `resident_cost` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_zone`
--

INSERT INTO `info_zone` (`id`, `cost`, `transport_in`, `transport_out`, `resident_cost`) VALUES
('A', 100.00, 0.05, 0.20, 2000.00),
('B', 100.00, 0.05, 0.20, 2000.00);

-- --------------------------------------------------------

--
-- Table structure for table `installment`
--

CREATE TABLE IF NOT EXISTS `installment` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `type` varchar(25) NOT NULL,
  `supply` varchar(15) NOT NULL,
  `planned_supply` decimal(10,2) NOT NULL,
  `actual_supply` decimal(10,2) NOT NULL,
  `tariff` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment`
--

INSERT INTO `installment` (`id`, `user`, `zone`, `type`, `supply`, `planned_supply`, `actual_supply`, `tariff`) VALUES
('IN0810120000', 'demigodA', 'A', 'Petrol Power Plant', '', 0.00, 0.00, 3.00),
('IN0810120001', 'Pioneer', 'A', 'Chemical Plant', 'IN0810120000', 25.00, 0.00, 0.00),
('IN0810120002', 'bagus', 'A', 'Chemical Plant', 'IN0810120000', 25.00, 0.00, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `installment_employee`
--

CREATE TABLE IF NOT EXISTS `installment_employee` (
  `id` varchar(15) NOT NULL,
  `installment` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment_employee`
--

INSERT INTO `installment_employee` (`id`, `installment`) VALUES
('EM0810120000', 'IN0810120000'),
('EM0810120001', 'IN0810120000'),
('EM0810120002', 'IN0810120000'),
('EM0810120003', 'IN0810120000'),
('EM0810120004', 'IN0810120000'),
('EM0810120005', 'IN0810120000'),
('EM0810120006', 'IN0810120001'),
('EM0810120007', 'IN0810120001'),
('EM0810120008', 'IN0810120001'),
('EM0810120009', 'IN0810120001'),
('EM0810120010', 'IN0810120002'),
('EM0810120011', 'IN0810120002'),
('EM0810120012', 'IN0810120002'),
('EM0810120013', 'IN0810120002');

-- --------------------------------------------------------

--
-- Table structure for table `installment_equipment`
--

CREATE TABLE IF NOT EXISTS `installment_equipment` (
  `id` varchar(15) NOT NULL,
  `installment` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment_equipment`
--

INSERT INTO `installment_equipment` (`id`, `installment`) VALUES
('EQ0810120000', 'IN0810120000'),
('EQ0810120001', 'IN0810120000'),
('EQ0810120002', 'IN0810120000'),
('EQ0810120003', 'IN0810120000'),
('EQ0810120004', 'IN0810120000'),
('EQ0810120005', 'IN0810120000'),
('EQ0810120006', 'IN0810120000'),
('EQ0810120007', 'IN0810120000'),
('EQ0810120008', 'IN0810120000'),
('EQ0810120009', 'IN0810120000'),
('EQ0810120010', 'IN0810120000'),
('EQ0810120011', 'IN0810120000'),
('EQ0810120012', 'IN0810120000'),
('EQ0810120013', 'IN0810120000'),
('EQ0810120014', 'IN0810120000'),
('EQ0810120015', 'IN0810120000'),
('EQ0810120016', 'IN0810120000'),
('EQ0810120017', 'IN0810120000'),
('EQ0810120018', 'IN0810120000'),
('EQ0810120019', 'IN0810120000'),
('EQ0810120020', 'IN0810120000'),
('EQ0810120021', 'IN0810120000'),
('EQ0810120022', 'IN0810120000'),
('EQ0810120023', 'IN0810120000'),
('EQ0810120024', 'IN0810120000'),
('EQ0810120025', 'IN0810120000'),
('EQ0810120026', 'IN0810120000'),
('EQ0810120027', 'IN0810120000'),
('EQ0810120028', 'IN0810120000'),
('EQ0810120029', 'IN0810120000'),
('EQ0810120030', 'IN0810120000'),
('EQ0810120031', 'IN0810120000'),
('EQ0810120032', 'IN0810120000'),
('EQ0810120033', 'IN0810120000'),
('EQ0810120034', 'IN0810120000'),
('EQ0810120035', 'IN0810120000'),
('EQ0810120036', 'IN0810120000'),
('EQ0810120037', 'IN0810120000'),
('EQ0810120038', 'IN0810120000'),
('EQ0810120039', 'IN0810120000'),
('EQ0810120040', 'IN0810120000'),
('EQ0810120041', 'IN0810120000'),
('EQ0810120042', 'IN0810120000'),
('EQ0810120043', 'IN0810120000'),
('EQ0810120044', 'IN0810120000'),
('EQ0810120045', 'IN0810120000'),
('EQ0810120046', 'IN0810120000'),
('EQ0810120047', 'IN0810120000'),
('EQ0810120048', 'IN0810120000'),
('EQ0810120049', 'IN0810120000'),
('EQ0810120050', 'IN0810120000'),
('EQ0810120051', 'IN0810120000'),
('EQ0810120052', 'IN0810120000'),
('EQ0810120053', 'IN0810120000'),
('EQ0810120054', 'IN0810120000'),
('EQ0810120055', 'IN0810120000'),
('EQ0810120056', 'IN0810120000'),
('EQ0810120057', 'IN0810120000'),
('EQ0810120058', 'IN0810120000'),
('EQ0810120059', 'IN0810120000'),
('EQ0810120060', 'IN0810120000'),
('EQ0810120061', 'IN0810120000'),
('EQ0810120062', 'IN0810120000'),
('EQ0810120063', 'IN0810120000'),
('EQ0810120064', 'IN0810120000'),
('EQ0810120065', 'IN0810120000'),
('EQ0810120066', 'IN0810120000'),
('EQ0810120067', 'IN0810120000'),
('EQ0810120068', 'IN0810120000'),
('EQ0810120069', 'IN0810120000'),
('EQ0810120070', 'IN0810120000'),
('EQ0810120071', 'IN0810120000'),
('EQ0810120072', 'IN0810120000'),
('EQ0810120073', 'IN0810120000'),
('EQ0810120074', 'IN0810120000'),
('EQ0810120075', 'IN0810120000'),
('EQ0810120076', 'IN0810120000'),
('EQ0810120077', 'IN0810120000'),
('EQ0810120078', 'IN0810120000'),
('EQ0810120079', 'IN0810120001'),
('EQ0810120080', 'IN0810120001'),
('EQ0810120081', 'IN0810120001'),
('EQ0810120082', 'IN0810120001'),
('EQ0810120083', 'IN0810120001'),
('EQ0810120084', 'IN0810120001'),
('EQ0810120085', 'IN0810120001'),
('EQ0810120086', 'IN0810120001'),
('EQ0810120087', 'IN0810120001'),
('EQ0810120088', 'IN0810120001'),
('EQ0810120089', 'IN0810120001'),
('EQ0810120090', 'IN0810120001'),
('EQ0810120091', 'IN0810120001'),
('EQ0810120092', 'IN0810120001'),
('EQ0810120093', 'IN0810120001'),
('EQ0810120094', 'IN0810120001'),
('EQ0810120095', 'IN0810120001'),
('EQ0810120096', 'IN0810120001'),
('EQ0810120097', 'IN0810120001'),
('EQ0810120098', 'IN0810120001'),
('EQ0810120099', 'IN0810120001'),
('EQ0810120100', 'IN0810120001'),
('EQ0810120101', 'IN0810120001'),
('EQ0810120102', 'IN0810120001'),
('EQ0810120103', 'IN0810120001'),
('EQ0810120104', 'IN0810120001'),
('EQ0810120105', 'IN0810120001'),
('EQ0810120106', 'IN0810120001'),
('EQ0810120107', 'IN0810120001'),
('EQ0810120108', 'IN0810120001'),
('EQ0810120109', 'IN0810120001'),
('EQ0810120110', 'IN0810120002'),
('EQ0810120111', 'IN0810120002'),
('EQ0810120112', 'IN0810120002'),
('EQ0810120113', 'IN0810120002'),
('EQ0810120114', 'IN0810120002'),
('EQ0810120115', 'IN0810120002'),
('EQ0810120116', 'IN0810120002'),
('EQ0810120117', 'IN0810120002'),
('EQ0810120118', 'IN0810120002'),
('EQ0810120119', 'IN0810120002'),
('EQ0810120120', 'IN0810120002'),
('EQ0810120121', 'IN0810120002'),
('EQ0810120122', 'IN0810120002'),
('EQ0810120123', 'IN0810120002'),
('EQ0810120124', 'IN0810120002'),
('EQ0810120125', 'IN0810120002'),
('EQ0810120126', 'IN0810120002'),
('EQ0810120127', 'IN0810120002'),
('EQ0810120128', 'IN0810120002'),
('EQ0810120129', 'IN0810120002'),
('EQ0810120130', 'IN0810120002'),
('EQ0810120131', 'IN0810120002'),
('EQ0810120132', 'IN0810120002'),
('EQ0810120133', 'IN0810120002'),
('EQ0810120134', 'IN0810120002'),
('EQ0810120135', 'IN0810120002'),
('EQ0810120136', 'IN0810120002'),
('EQ0810120137', 'IN0810120002'),
('EQ0810120138', 'IN0810120002'),
('EQ0810120139', 'IN0810120002'),
('EQ0810120140', 'IN0810120002');

-- --------------------------------------------------------

--
-- Table structure for table `list_employee`
--

CREATE TABLE IF NOT EXISTS `list_employee` (
  `id` varchar(15) NOT NULL,
  `desc` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `list_employee`
--

INSERT INTO `list_employee` (`id`, `desc`) VALUES
('EM0810120000', 'EMMNGR02'),
('EM0810120001', 'EMEMPL02'),
('EM0810120002', 'EMEMPL02'),
('EM0810120003', 'EMEMPL02'),
('EM0810120004', 'EMEMPL02'),
('EM0810120005', 'EMEMPL02'),
('EM0810120006', 'EMMNGR02'),
('EM0810120007', 'EMEMPL02'),
('EM0810120008', 'EMEMPL02'),
('EM0810120009', 'EMEMPL02'),
('EM0810120010', 'EMMNGR02'),
('EM0810120011', 'EMEMPL02'),
('EM0810120012', 'EMEMPL02'),
('EM0810120013', 'EMEMPL02');

-- --------------------------------------------------------

--
-- Table structure for table `list_equipment`
--

CREATE TABLE IF NOT EXISTS `list_equipment` (
  `id` varchar(15) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `durability` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `list_equipment`
--

INSERT INTO `list_equipment` (`id`, `desc`, `durability`) VALUES
('EQ0810120000', 'EQPUMP02', 99.94),
('EQ0810120001', 'EQPUMP02', 99.94),
('EQ0810120002', 'EQPUMP02', 99.94),
('EQ0810120003', 'EQPUMP02', 99.94),
('EQ0810120004', 'EQPUMP02', 99.94),
('EQ0810120005', 'EQPUMP02', 99.94),
('EQ0810120006', 'EQPUMP02', 99.94),
('EQ0810120007', 'EQPUMP02', 99.94),
('EQ0810120008', 'EQPUMP02', 99.94),
('EQ0810120009', 'EQPUMP02', 99.94),
('EQ0810120010', 'EQMCHN02', 99.94),
('EQ0810120011', 'EQMCHN02', 99.94),
('EQ0810120012', 'EQMCHN02', 99.94),
('EQ0810120013', 'EQMCHN02', 99.94),
('EQ0810120014', 'EQMCHN02', 99.94),
('EQ0810120015', 'EQMCHN02', 99.94),
('EQ0810120016', 'EQMCHN02', 99.94),
('EQ0810120017', 'EQMCHN02', 99.94),
('EQ0810120018', 'EQMCHN02', 99.94),
('EQ0810120019', 'EQMCHN02', 99.94),
('EQ0810120020', 'EQMCHN02', 99.94),
('EQ0810120021', 'EQMCHN02', 99.94),
('EQ0810120022', 'EQMCHN02', 99.94),
('EQ0810120023', 'EQMCHN02', 99.94),
('EQ0810120024', 'EQMCHN02', 99.94),
('EQ0810120025', 'EQMCHN02', 99.94),
('EQ0810120026', 'EQMCHN02', 99.94),
('EQ0810120027', 'EQMCHN02', 99.94),
('EQ0810120028', 'EQMCHN02', 99.94),
('EQ0810120029', 'EQMCHN02', 99.94),
('EQ0810120030', 'EQMCHN02', 99.94),
('EQ0810120031', 'EQMCHN02', 99.94),
('EQ0810120032', 'EQMCHN02', 99.94),
('EQ0810120033', 'EQMCHN02', 99.94),
('EQ0810120034', 'EQMCHN02', 99.94),
('EQ0810120035', 'EQMCHN02', 99.94),
('EQ0810120036', 'EQMCHN02', 99.94),
('EQ0810120037', 'EQMCHN02', 99.94),
('EQ0810120038', 'EQMCHN02', 99.94),
('EQ0810120039', 'EQMCHN02', 99.94),
('EQ0810120040', 'EQMCHN02', 99.94),
('EQ0810120041', 'EQMCHN02', 99.94),
('EQ0810120042', 'EQMCHN02', 99.94),
('EQ0810120043', 'EQMCHN02', 99.94),
('EQ0810120044', 'EQMCHN02', 99.94),
('EQ0810120045', 'EQMCHN02', 99.94),
('EQ0810120046', 'EQMCHN02', 99.94),
('EQ0810120047', 'EQMCHN02', 99.94),
('EQ0810120048', 'EQMCHN02', 99.94),
('EQ0810120049', 'EQMCHN02', 99.94),
('EQ0810120050', 'EQGNTR02', 99.94),
('EQ0810120051', 'EQGNTR02', 99.94),
('EQ0810120052', 'EQGNTR02', 99.94),
('EQ0810120053', 'EQGNTR02', 99.94),
('EQ0810120054', 'EQGNTR02', 99.94),
('EQ0810120055', 'EQGNTR02', 99.94),
('EQ0810120056', 'EQGNTR02', 99.94),
('EQ0810120057', 'EQGNTR02', 99.94),
('EQ0810120058', 'EQGNTR02', 99.94),
('EQ0810120059', 'EQFRNC02', 99.94),
('EQ0810120060', 'EQFRNC02', 99.94),
('EQ0810120061', 'EQFRNC02', 99.94),
('EQ0810120062', 'EQFRNC02', 99.94),
('EQ0810120063', 'EQFRNC02', 99.94),
('EQ0810120064', 'EQFRNC02', 99.94),
('EQ0810120065', 'EQFRNC02', 99.94),
('EQ0810120066', 'EQFRNC02', 99.94),
('EQ0810120067', 'EQFRNC02', 99.94),
('EQ0810120068', 'EQFRNC02', 99.94),
('EQ0810120069', 'EQFRNC02', 99.94),
('EQ0810120070', 'EQFRNC02', 99.94),
('EQ0810120071', 'EQFRNC02', 99.94),
('EQ0810120072', 'EQFRNC02', 99.94),
('EQ0810120073', 'EQFRNC02', 99.94),
('EQ0810120074', 'EQFRNC02', 99.94),
('EQ0810120075', 'EQFRNC02', 99.94),
('EQ0810120076', 'EQFRNC02', 99.94),
('EQ0810120077', 'EQFRNC02', 99.94),
('EQ0810120078', 'EQFRNC02', 99.94),
('EQ0810120079', 'EQGNTR02', 100.00),
('EQ0810120080', 'EQGNTR02', 100.00),
('EQ0810120081', 'EQFRNC02', 100.00),
('EQ0810120082', 'EQFRNC02', 100.00),
('EQ0810120083', 'EQFRNC02', 100.00),
('EQ0810120084', 'EQFRNC02', 100.00),
('EQ0810120085', 'EQFRNC02', 100.00),
('EQ0810120086', 'EQFRNC02', 100.00),
('EQ0810120087', 'EQFRNC02', 100.00),
('EQ0810120088', 'EQFRNC02', 100.00),
('EQ0810120089', 'EQFRNC02', 100.00),
('EQ0810120090', 'EQFRNC02', 100.00),
('EQ0810120091', 'EQFRNC02', 100.00),
('EQ0810120092', 'EQFRNC02', 100.00),
('EQ0810120093', 'EQFRNC02', 100.00),
('EQ0810120094', 'EQPUMP02', 100.00),
('EQ0810120095', 'EQPUMP02', 100.00),
('EQ0810120096', 'EQPUMP02', 100.00),
('EQ0810120097', 'EQPUMP02', 100.00),
('EQ0810120098', 'EQPUMP02', 100.00),
('EQ0810120099', 'EQPUMP02', 100.00),
('EQ0810120100', 'EQPUMP02', 100.00),
('EQ0810120101', 'EQPUMP02', 100.00),
('EQ0810120102', 'EQPUMP02', 100.00),
('EQ0810120103', 'EQMCHN02', 100.00),
('EQ0810120104', 'EQMCHN02', 100.00),
('EQ0810120105', 'EQMCHN02', 100.00),
('EQ0810120106', 'EQMCHN02', 100.00),
('EQ0810120107', 'EQMCHN02', 100.00),
('EQ0810120108', 'EQMCHN02', 100.00),
('EQ0810120109', 'EQMCHN02', 100.00),
('EQ0810120110', 'EQGNTR02', 99.97),
('EQ0810120111', 'EQGNTR02', 99.97),
('EQ0810120112', 'EQFRNC02', 99.97),
('EQ0810120113', 'EQFRNC02', 99.97),
('EQ0810120114', 'EQFRNC02', 99.97),
('EQ0810120115', 'EQFRNC02', 99.97),
('EQ0810120116', 'EQFRNC02', 99.97),
('EQ0810120117', 'EQFRNC02', 99.97),
('EQ0810120118', 'EQFRNC02', 99.97),
('EQ0810120119', 'EQFRNC02', 99.97),
('EQ0810120120', 'EQFRNC02', 99.97),
('EQ0810120121', 'EQFRNC02', 99.97),
('EQ0810120122', 'EQFRNC02', 99.97),
('EQ0810120123', 'EQFRNC02', 99.97),
('EQ0810120124', 'EQFRNC02', 99.97),
('EQ0810120125', 'EQPUMP02', 99.97),
('EQ0810120126', 'EQPUMP02', 99.97),
('EQ0810120127', 'EQPUMP02', 99.97),
('EQ0810120128', 'EQPUMP02', 99.97),
('EQ0810120129', 'EQPUMP02', 99.97),
('EQ0810120130', 'EQPUMP02', 99.97),
('EQ0810120131', 'EQPUMP02', 99.97),
('EQ0810120132', 'EQPUMP02', 99.97),
('EQ0810120133', 'EQPUMP02', 99.97),
('EQ0810120134', 'EQMCHN02', 99.97),
('EQ0810120135', 'EQMCHN02', 99.97),
('EQ0810120136', 'EQMCHN02', 99.97),
('EQ0810120137', 'EQMCHN02', 99.97),
('EQ0810120138', 'EQMCHN02', 99.97),
('EQ0810120139', 'EQMCHN02', 99.97),
('EQ0810120140', 'EQMCHN02', 99.97);

-- --------------------------------------------------------

--
-- Table structure for table `market_employee`
--

CREATE TABLE IF NOT EXISTS `market_employee` (
  `id` varchar(15) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `market_equipment`
--

CREATE TABLE IF NOT EXISTS `market_equipment` (
  `id` varchar(15) NOT NULL,
  `storage_equipment_id` varchar(15) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `market_product`
--

CREATE TABLE IF NOT EXISTS `market_product` (
  `id` varchar(15) NOT NULL,
  `storage_product_id` varchar(15) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_product`
--

INSERT INTO `market_product` (`id`, `storage_product_id`, `desc`, `zone`, `price`, `size`) VALUES
('MP0810120000', 'PR0810120060', '', 'A', 14.88, 62.50),
('MP0810120001', 'PR0810120061', '', 'A', 13.44, 40.00),
('MP0810120002', 'PR0810120060', '', 'A', 14.00, 17.50),
('MP0908120000', '', 'PRCROL02', 'A', 3.00, 851.58),
('MP0908120001', '', 'PRIRON02', 'A', 1.90, 608.09),
('MP0908120002', '', 'PRSLCA02', 'A', 2.50, 686.50),
('MP0908120003', '', 'PRWATR02', 'A', 1.00, 1000.00);

-- --------------------------------------------------------

--
-- Table structure for table `market_share`
--

CREATE TABLE IF NOT EXISTS `market_share` (
  `id` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `product` varchar(25) NOT NULL,
  `value` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_share`
--

INSERT INTO `market_share` (`id`, `zone`, `product`, `value`) VALUES
('MS0410120000', 'A', 'Gasoline', 1000.00),
('MS0410120001', 'A', 'Chemical', 1000.00);

-- --------------------------------------------------------

--
-- Table structure for table `product_advertisement`
--

CREATE TABLE IF NOT EXISTS `product_advertisement` (
  `id` varchar(15) NOT NULL,
  `user` varchar(25) NOT NULL,
  `product` varchar(25) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `adv` varchar(15) NOT NULL,
  `count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `req_borrow_bank`
--

CREATE TABLE IF NOT EXISTS `req_borrow_bank` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `turn` bigint(20) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `raw_turn` int(11) NOT NULL,
  `storage` tinyint(1) NOT NULL,
  `zone` varchar(5) NOT NULL,
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
('ST0810120002', 'Pioneer', 'A', 1),
('ST0810120003', 'bagus', 'A', 1),
('ST0810120005', 'demigodA', 'A', 1);

-- --------------------------------------------------------

--
-- Table structure for table `storage_equipment`
--

CREATE TABLE IF NOT EXISTS `storage_equipment` (
  `id` varchar(15) NOT NULL,
  `storage` varchar(15) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `storage_product`
--

CREATE TABLE IF NOT EXISTS `storage_product` (
  `id` varchar(15) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `storage` varchar(15) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storage_product`
--

INSERT INTO `storage_product` (`id`, `desc`, `storage`, `size`) VALUES
('PR0810120045', 'PRWWTR02', 'ST0810120000', 20.00),
('PR0810120049', 'PRWWTR02', 'ST0810120001', 20.00),
('PR0810120050', 'PRCROL02', 'ST0810120002', 35.92),
('PR0810120051', 'PRIRON02', 'ST0810120002', 40.75),
('PR0810120052', 'PRSLCA02', 'ST0810120002', 30.28),
('PR0810120053', 'PRCROL02', 'ST0810120003', 22.50),
('PR0810120054', 'PRSLCA02', 'ST0810120003', 43.22),
('PR0810120055', 'PRIRON02', 'ST0810120003', 51.16),
('PR0810120059', 'PRWWTR02', 'ST0810120004', 20.00),
('PR0810120060', 'PRCMCL01', 'ST0810120002', 120.00),
('PR0810120061', 'PRCMCL01', 'ST0810120003', 120.00),
('PR0810120065', 'PRWWTR02', 'ST0810120005', 60.00);

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
  `level` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`name`, `pass`, `email`, `dob`, `about`, `avatar`, `money`, `rep`, `zone`, `level`) VALUES
('bagus', 'bagus', 'bagus@a.c', 'Apr 1 1990', 'This is me', '', 270.23, 0, 'A', 2),
('demigodA', 'Adogimed', 'demigod@bizgame.net', 'Apr 4 1990', 'This is me', '', 75666.70, 0, 'A', 4),
('Pioneer', '1234', 'yahoo@jp', 'Nov 9 1990', 'This is me', '', 282.10, 0, 'A', 2);

-- --------------------------------------------------------

--
-- Table structure for table `user_market_license`
--

CREATE TABLE IF NOT EXISTS `user_market_license` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `zone` varchar(5) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_market_license`
--

INSERT INTO `user_market_license` (`id`, `user`, `zone`) VALUES
('UM0810120000', 'demigodA', 'A'),
('UM0810120001', 'Pioneer', 'A'),
('UM0810120002', 'bagus', 'A');

-- --------------------------------------------------------

--
-- Table structure for table `user_sector_blueprint`
--

CREATE TABLE IF NOT EXISTS `user_sector_blueprint` (
  `id` varchar(15) NOT NULL,
  `user` varchar(15) NOT NULL,
  `sector` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_sector_blueprint`
--

INSERT INTO `user_sector_blueprint` (`id`, `user`, `sector`) VALUES
('US0810120000', 'demigodA', 'Chemical Plant'),
('US0810120001', 'demigodA', 'Iron Mine'),
('US0810120002', 'demigodA', 'Oil Refinery'),
('US0810120003', 'demigodA', 'Oil Well'),
('US0810120004', 'demigodA', 'Petrol Power Plant'),
('US0810120005', 'demigodA', 'Silica Mine'),
('US0810120006', 'demigodA', 'Water Well'),
('US0810120007', 'Pioneer', 'Chemical Plant'),
('US0810120008', 'bagus', 'Chemical Plant');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
