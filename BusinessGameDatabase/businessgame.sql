-- phpMyAdmin SQL Dump
-- version 3.4.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 17, 2012 at 02:36 PM
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
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `turn` bigint(20) NOT NULL,
  `borrow` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `borrow_bank`
--

INSERT INTO `borrow_bank` (`id`, `user`, `turn`, `borrow`) VALUES
('BB1310120000', 'bagus', 7, 1325.05);

-- --------------------------------------------------------

--
-- Table structure for table `desc_advertisement`
--

CREATE TABLE IF NOT EXISTS `desc_advertisement` (
  `id` varchar(15) NOT NULL,
  `advertise` varchar(25) NOT NULL,
  `multiplier` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `desc_advertisement`
--

INSERT INTO `desc_advertisement` (`id`, `advertise`, `multiplier`, `price`) VALUES
('ADINET', 'Internet', 3, 120.00),
('ADNEWS', 'Newspaper', 1, 50.00),
('ADTLVS', 'Television', 6, 250.00);

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
  `draw` varchar(15) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_employee`
--

INSERT INTO `info_employee` (`name`, `base_price`, `base_operational`, `draw`) VALUES
('Employee', 5.00, 0.30, '0x7f020001'),
('Manager', 15.00, 0.70, '0x7f020002');

-- --------------------------------------------------------

--
-- Table structure for table `info_equipment`
--

CREATE TABLE IF NOT EXISTS `info_equipment` (
  `name` varchar(25) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `base_operational` decimal(10,2) NOT NULL,
  `base_size` decimal(10,2) NOT NULL,
  `draw` varchar(15) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_equipment`
--

INSERT INTO `info_equipment` (`name`, `base_price`, `base_operational`, `base_size`, `draw`) VALUES
('Drill', 12.00, 0.06, 8.00, '0x7f020003'),
('Furnace', 20.00, 0.06, 4.00, '0x7f020004'),
('Generator', 8.00, 0.05, 6.00, '0x7f020005'),
('Machine', 7.50, 0.02, 6.00, '0x7f020006'),
('Pump', 9.00, 0.04, 6.00, '0x7f020007');

-- --------------------------------------------------------

--
-- Table structure for table `info_finance`
--

CREATE TABLE IF NOT EXISTS `info_finance` (
  `name` varchar(25) NOT NULL,
  `factor` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_finance`
--

INSERT INTO `info_finance` (`name`, `factor`) VALUES
('Advertisement', -1),
('Depreciation', -1),
('Electricity', -1),
('Fixed', -1),
('Interest', -1),
('Operation', -1),
('Raw Material', -1),
('Retribution', -1),
('Sales', 1),
('Transport', -1),
('Wage', -1);

-- --------------------------------------------------------

--
-- Table structure for table `info_product`
--

CREATE TABLE IF NOT EXISTS `info_product` (
  `name` varchar(25) NOT NULL,
  `base_price` decimal(10,2) NOT NULL,
  `transport` decimal(10,2) NOT NULL,
  `draw` varchar(15) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_product`
--

INSERT INTO `info_product` (`name`, `base_price`, `transport`, `draw`) VALUES
('Chemical', 18.05, 0.02, '0x7f02000f'),
('Crude Oil', 3.00, 0.01, '0x7f020010'),
('Energy', 3.00, 0.00, '0x7f020011'),
('Gasoline', 21.00, 0.02, '0x7f020012'),
('Iron', 1.90, 0.01, '0x7f020013'),
('Silica', 2.50, 0.01, '0x7f020014'),
('Trash', -0.30, 0.01, '0x7f020015'),
('Wastewater', -0.50, 0.01, '0x7f020016'),
('Water', 1.00, 0.01, '0x7f020017');

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
  `draw` varchar(15) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector`
--

INSERT INTO `info_sector` (`name`, `cost`, `prob`, `level`, `draw`) VALUES
('Chemical Plant', 100.00, 0.70, 1, '0x7f020018'),
('Iron Mine', 200.00, 0.45, 2, '0x7f020019'),
('Oil Refinery', 100.00, 0.70, 1, '0x7f02001a'),
('Oil Well', 200.00, 0.45, 2, '0x7f02001b'),
('Petrol Power Plant', 300.00, 0.20, 3, '0x7f02001c'),
('Silica Mine', 200.00, 0.45, 2, '0x7f02001d'),
('Water Well', 200.00, 0.45, 2, '0x7f02001e');

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_employee`
--

CREATE TABLE IF NOT EXISTS `info_sector_employee` (
  `id` varchar(20) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `employee_type` varchar(20) NOT NULL,
  `items` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_employee`
--

INSERT INTO `info_sector_employee` (`id`, `sector`, `employee_type`, `items`) VALUES
('SM1350306791646000', 'Chemical Plant', 'Manager', 1),
('SM1350306791646001', 'Chemical Plant', 'Employee', 3),
('SM1350306791646002', 'Iron Mine', 'Manager', 1),
('SM1350306791646003', 'Iron Mine', 'Employee', 3),
('SM1350306791646004', 'Oil Refinery', 'Manager', 1),
('SM1350306791646005', 'Oil Refinery', 'Employee', 4),
('SM1350306791646006', 'Oil Well', 'Manager', 1),
('SM1350306791646007', 'Oil Well', 'Employee', 4),
('SM1350306791646008', 'Petrol Power Plant', 'Manager', 1),
('SM1350306791646009', 'Petrol Power Plant', 'Employee', 5),
('SM1350306791646010', 'Silica Mine', 'Manager', 1),
('SM1350306791646011', 'Silica Mine', 'Employee', 3),
('SM1350306791646012', 'Water Well', 'Manager', 1),
('SM1350306791646013', 'Water Well', 'Employee', 4);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_equipment`
--

CREATE TABLE IF NOT EXISTS `info_sector_equipment` (
  `id` varchar(20) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `equipment_type` varchar(20) NOT NULL,
  `items` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_equipment`
--

INSERT INTO `info_sector_equipment` (`id`, `sector`, `equipment_type`, `items`) VALUES
('SQ1350306791646000', 'Chemical Plant', 'Generator', 2),
('SQ1350306791646001', 'Chemical Plant', 'Furnace', 13),
('SQ1350306791646002', 'Chemical Plant', 'Pump', 9),
('SQ1350306791646003', 'Chemical Plant', 'Machine', 7),
('SQ1350306791646004', 'Iron Mine', 'Drill', 150),
('SQ1350306791646005', 'Iron Mine', 'Machine', 20),
('SQ1350306791646006', 'Iron Mine', 'Generator', 10),
('SQ1350306791646007', 'Oil Refinery', 'Machine', 15),
('SQ1350306791646008', 'Oil Refinery', 'Pump', 30),
('SQ1350306791646009', 'Oil Refinery', 'Generator', 3),
('SQ1350306791646010', 'Oil Well', 'Drill', 11),
('SQ1350306791646011', 'Oil Well', 'Generator', 5),
('SQ1350306791646012', 'Oil Well', 'Pump', 50),
('SQ1350306791646013', 'Oil Well', 'Machine', 20),
('SQ1350306791646014', 'Petrol Power Plant', 'Pump', 10),
('SQ1350306791646015', 'Petrol Power Plant', 'Machine', 40),
('SQ1350306791646016', 'Petrol Power Plant', 'Generator', 9),
('SQ1350306791646017', 'Petrol Power Plant', 'Furnace', 20),
('SQ1350306791646018', 'Silica Mine', 'Drill', 120),
('SQ1350306791646019', 'Silica Mine', 'Machine', 60),
('SQ1350306791646020', 'Silica Mine', 'Generator', 25),
('SQ1350306791646021', 'Water Well', 'Generator', 5),
('SQ1350306791646022', 'Water Well', 'Drill', 10),
('SQ1350306791646023', 'Water Well', 'Pump', 75),
('SQ1350306791646024', 'Water Well', 'Machine', 25);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_input`
--

CREATE TABLE IF NOT EXISTS `info_sector_input` (
  `id` varchar(20) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `input_type` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_input`
--

INSERT INTO `info_sector_input` (`id`, `sector`, `input_type`, `size`) VALUES
('SI1350306791646000', 'Chemical Plant', 'Energy', 25.00),
('SI1350306791646001', 'Chemical Plant', 'Crude Oil', 15.00),
('SI1350306791646002', 'Chemical Plant', 'Silica', 40.00),
('SI1350306791646003', 'Chemical Plant', 'Iron', 50.00),
('SI1350306791646004', 'Iron Mine', 'Water', 175.00),
('SI1350306791646005', 'Iron Mine', 'Energy', 35.00),
('SI1350306791646006', 'Oil Refinery', 'Crude Oil', 30.00),
('SI1350306791646007', 'Oil Refinery', 'Energy', 30.00),
('SI1350306791646008', 'Oil Well', 'Energy', 60.00),
('SI1350306791646009', 'Petrol Power Plant', 'Chemical', 17.00),
('SI1350306791646010', 'Petrol Power Plant', 'Gasoline', 19.00),
('SI1350306791646011', 'Petrol Power Plant', 'Water', 70.00),
('SI1350306791646012', 'Silica Mine', 'Energy', 35.00),
('SI1350306791646013', 'Silica Mine', 'Water', 175.00),
('SI1350306791646014', 'Water Well', 'Energy', 50.00);

-- --------------------------------------------------------

--
-- Table structure for table `info_sector_output`
--

CREATE TABLE IF NOT EXISTS `info_sector_output` (
  `id` varchar(20) NOT NULL,
  `sector` varchar(25) NOT NULL,
  `output_type` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_sector_output`
--

INSERT INTO `info_sector_output` (`id`, `sector`, `output_type`, `size`) VALUES
('SO1350306791646000', 'Chemical Plant', 'Chemical', 40.00),
('SO1350306791646001', 'Iron Mine', 'Iron', 300.00),
('SO1350306791646002', 'Iron Mine', 'Trash', 90.00),
('SO1350306791646003', 'Iron Mine', 'Wastewater', 40.00),
('SO1350306791646004', 'Oil Refinery', 'Wastewater', 7.00),
('SO1350306791646005', 'Oil Refinery', 'Gasoline', 11.00),
('SO1350306791646006', 'Oil Well', 'Trash', 60.00),
('SO1350306791646007', 'Oil Well', 'Crude Oil', 100.00),
('SO1350306791646008', 'Petrol Power Plant', 'Energy', 300.00),
('SO1350306791646009', 'Petrol Power Plant', 'Wastewater', 20.00),
('SO1350306791646010', 'Silica Mine', 'Trash', 50.00),
('SO1350306791646011', 'Silica Mine', 'Wastewater', 13.00),
('SO1350306791646012', 'Silica Mine', 'Silica', 225.00),
('SO1350306791646013', 'Water Well', 'Trash', 50.00),
('SO1350306791646014', 'Water Well', 'Water', 300.00);

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
('cost_storage_upgrade', '250'),
('inc_borrow_bank', '0'),
('inc_employee', '0'),
('inc_equipment', '0'),
('inc_installment', '0'),
('inc_market_employee', '0'),
('inc_market_equipment', '0'),
('inc_market_product', '0'),
('inc_market_share', '0'),
('inc_market_share_product', '0'),
('inc_product_advertisement', '0'),
('inc_req_borrow_bank', '0'),
('inc_storage', '0'),
('inc_storage_product', '0'),
('inc_user_contract', '1'),
('inc_user_finance', '0'),
('inc_user_market_license', '0'),
('inc_user_message', '0'),
('inc_user_sector_blueprint', '0'),
('interest', '0.15'),
('last_inc_set_date', '151012'),
('last_inc_set_millis', '1350466138686'),
('max_turn_bank', '540'),
('sector', '1000'),
('storage', '1000'),
('storage_inc', '500'),
('tax', '0.20'),
('turn', '1594');

-- --------------------------------------------------------

--
-- Table structure for table `info_zone`
--

CREATE TABLE IF NOT EXISTS `info_zone` (
  `id` varchar(5) NOT NULL,
  `cost` decimal(10,2) NOT NULL,
  `transport_in` decimal(10,2) NOT NULL,
  `transport_out` decimal(10,2) NOT NULL,
  `retribution` decimal(10,2) NOT NULL,
  `resident_cost` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `info_zone`
--

INSERT INTO `info_zone` (`id`, `cost`, `transport_in`, `transport_out`, `retribution`, `resident_cost`) VALUES
('A', 100.00, 0.05, 0.20, 0.03, 2000.00),
('B', 100.00, 0.05, 0.20, 0.03, 2000.00);

-- --------------------------------------------------------

--
-- Table structure for table `installment`
--

CREATE TABLE IF NOT EXISTS `installment` (
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `type` varchar(25) NOT NULL,
  `supply` varchar(20) NOT NULL,
  `planned_supply` decimal(10,2) NOT NULL,
  `actual_supply` decimal(10,2) NOT NULL,
  `tariff` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment`
--

INSERT INTO `installment` (`id`, `user`, `zone`, `type`, `supply`, `planned_supply`, `actual_supply`, `tariff`) VALUES
('IN1350306791646000', 'demigodA', 'A', 'Petrol Power Plant', '', 0.00, 0.00, 3.00),
('IN1350306791646001', 'bagus', 'A', 'Oil Refinery', '', 30.00, 30.00, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `installment_employee`
--

CREATE TABLE IF NOT EXISTS `installment_employee` (
  `id` varchar(20) NOT NULL,
  `installment` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment_employee`
--

INSERT INTO `installment_employee` (`id`, `installment`) VALUES
('EM0810120000', 'IN1350306791646000'),
('EM0810120001', 'IN1350306791646000'),
('EM0810120002', 'IN1350306791646000'),
('EM0810120003', 'IN1350306791646000'),
('EM0810120004', 'IN1350306791646000'),
('EM0810120005', 'IN1350306791646000'),
('EM1310120000', 'IN1350306791646001'),
('EM1310120001', 'IN1350306791646001'),
('EM1310120002', 'IN1350306791646001'),
('EM1310120003', 'IN1350306791646001'),
('EM1310120004', 'IN1350306791646001');

-- --------------------------------------------------------

--
-- Table structure for table `installment_equipment`
--

CREATE TABLE IF NOT EXISTS `installment_equipment` (
  `id` varchar(20) NOT NULL,
  `installment` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `installment_equipment`
--

INSERT INTO `installment_equipment` (`id`, `installment`) VALUES
('EQ0810120000', 'IN1350306791646000'),
('EQ0810120001', 'IN1350306791646000'),
('EQ0810120002', 'IN1350306791646000'),
('EQ0810120003', 'IN1350306791646000'),
('EQ0810120004', 'IN1350306791646000'),
('EQ0810120005', 'IN1350306791646000'),
('EQ0810120006', 'IN1350306791646000'),
('EQ0810120007', 'IN1350306791646000'),
('EQ0810120008', 'IN1350306791646000'),
('EQ0810120009', 'IN1350306791646000'),
('EQ0810120010', 'IN1350306791646000'),
('EQ0810120011', 'IN1350306791646000'),
('EQ0810120012', 'IN1350306791646000'),
('EQ0810120013', 'IN1350306791646000'),
('EQ0810120014', 'IN1350306791646000'),
('EQ0810120015', 'IN1350306791646000'),
('EQ0810120016', 'IN1350306791646000'),
('EQ0810120017', 'IN1350306791646000'),
('EQ0810120018', 'IN1350306791646000'),
('EQ0810120019', 'IN1350306791646000'),
('EQ0810120020', 'IN1350306791646000'),
('EQ0810120021', 'IN1350306791646000'),
('EQ0810120022', 'IN1350306791646000'),
('EQ0810120023', 'IN1350306791646000'),
('EQ0810120024', 'IN1350306791646000'),
('EQ0810120025', 'IN1350306791646000'),
('EQ0810120026', 'IN1350306791646000'),
('EQ0810120027', 'IN1350306791646000'),
('EQ0810120028', 'IN1350306791646000'),
('EQ0810120029', 'IN1350306791646000'),
('EQ0810120030', 'IN1350306791646000'),
('EQ0810120031', 'IN1350306791646000'),
('EQ0810120032', 'IN1350306791646000'),
('EQ0810120033', 'IN1350306791646000'),
('EQ0810120034', 'IN1350306791646000'),
('EQ0810120035', 'IN1350306791646000'),
('EQ0810120036', 'IN1350306791646000'),
('EQ0810120037', 'IN1350306791646000'),
('EQ0810120038', 'IN1350306791646000'),
('EQ0810120039', 'IN1350306791646000'),
('EQ0810120040', 'IN1350306791646000'),
('EQ0810120041', 'IN1350306791646000'),
('EQ0810120042', 'IN1350306791646000'),
('EQ0810120043', 'IN1350306791646000'),
('EQ0810120044', 'IN1350306791646000'),
('EQ0810120045', 'IN1350306791646000'),
('EQ0810120046', 'IN1350306791646000'),
('EQ0810120047', 'IN1350306791646000'),
('EQ0810120048', 'IN1350306791646000'),
('EQ0810120049', 'IN1350306791646000'),
('EQ0810120050', 'IN1350306791646000'),
('EQ0810120051', 'IN1350306791646000'),
('EQ0810120052', 'IN1350306791646000'),
('EQ0810120053', 'IN1350306791646000'),
('EQ0810120054', 'IN1350306791646000'),
('EQ0810120055', 'IN1350306791646000'),
('EQ0810120056', 'IN1350306791646000'),
('EQ0810120057', 'IN1350306791646000'),
('EQ0810120058', 'IN1350306791646000'),
('EQ0810120059', 'IN1350306791646000'),
('EQ0810120060', 'IN1350306791646000'),
('EQ0810120061', 'IN1350306791646000'),
('EQ0810120062', 'IN1350306791646000'),
('EQ0810120063', 'IN1350306791646000'),
('EQ0810120064', 'IN1350306791646000'),
('EQ0810120065', 'IN1350306791646000'),
('EQ0810120066', 'IN1350306791646000'),
('EQ0810120067', 'IN1350306791646000'),
('EQ0810120068', 'IN1350306791646000'),
('EQ0810120069', 'IN1350306791646000'),
('EQ0810120070', 'IN1350306791646000'),
('EQ0810120071', 'IN1350306791646000'),
('EQ0810120072', 'IN1350306791646000'),
('EQ0810120073', 'IN1350306791646000'),
('EQ0810120074', 'IN1350306791646000'),
('EQ0810120075', 'IN1350306791646000'),
('EQ0810120076', 'IN1350306791646000'),
('EQ0810120077', 'IN1350306791646000'),
('EQ0810120078', 'IN1350306791646000'),
('EQ1310120000', 'IN1350306791646001'),
('EQ1310120001', 'IN1350306791646001'),
('EQ1310120002', 'IN1350306791646001'),
('EQ1310120003', 'IN1350306791646001'),
('EQ1310120004', 'IN1350306791646001'),
('EQ1310120005', 'IN1350306791646001'),
('EQ1310120006', 'IN1350306791646001'),
('EQ1310120007', 'IN1350306791646001'),
('EQ1310120008', 'IN1350306791646001'),
('EQ1310120009', 'IN1350306791646001'),
('EQ1310120010', 'IN1350306791646001'),
('EQ1310120011', 'IN1350306791646001'),
('EQ1310120012', 'IN1350306791646001'),
('EQ1310120013', 'IN1350306791646001'),
('EQ1310120014', 'IN1350306791646001'),
('EQ1310120015', 'IN1350306791646001'),
('EQ1310120016', 'IN1350306791646001'),
('EQ1310120017', 'IN1350306791646001'),
('EQ1310120018', 'IN1350306791646001'),
('EQ1310120019', 'IN1350306791646001'),
('EQ1310120020', 'IN1350306791646001'),
('EQ1310120021', 'IN1350306791646001'),
('EQ1310120022', 'IN1350306791646001'),
('EQ1310120023', 'IN1350306791646001'),
('EQ1310120024', 'IN1350306791646001'),
('EQ1310120025', 'IN1350306791646001'),
('EQ1310120026', 'IN1350306791646001'),
('EQ1310120027', 'IN1350306791646001'),
('EQ1310120028', 'IN1350306791646001'),
('EQ1310120029', 'IN1350306791646001'),
('EQ1310120030', 'IN1350306791646001'),
('EQ1310120031', 'IN1350306791646001'),
('EQ1310120032', 'IN1350306791646001'),
('EQ1310120033', 'IN1350306791646001'),
('EQ1310120034', 'IN1350306791646001'),
('EQ1310120035', 'IN1350306791646001'),
('EQ1310120036', 'IN1350306791646001'),
('EQ1310120037', 'IN1350306791646001'),
('EQ1310120038', 'IN1350306791646001'),
('EQ1310120039', 'IN1350306791646001'),
('EQ1310120040', 'IN1350306791646001'),
('EQ1310120041', 'IN1350306791646001'),
('EQ1310120042', 'IN1350306791646001'),
('EQ1310120043', 'IN1350306791646001'),
('EQ1310120044', 'IN1350306791646001'),
('EQ1310120045', 'IN1350306791646001'),
('EQ1310120046', 'IN1350306791646001'),
('EQ1310120047', 'IN1350306791646001');

-- --------------------------------------------------------

--
-- Table structure for table `list_employee`
--

CREATE TABLE IF NOT EXISTS `list_employee` (
  `id` varchar(20) NOT NULL,
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
('EM1310120000', 'EMMNGR02'),
('EM1310120001', 'EMEMPL02'),
('EM1310120002', 'EMEMPL02'),
('EM1310120003', 'EMEMPL02'),
('EM1310120004', 'EMEMPL02');

-- --------------------------------------------------------

--
-- Table structure for table `list_equipment`
--

CREATE TABLE IF NOT EXISTS `list_equipment` (
  `id` varchar(20) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `durability` decimal(10,2) NOT NULL,
  `buy_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `list_equipment`
--

INSERT INTO `list_equipment` (`id`, `desc`, `durability`, `buy_price`) VALUES
('EQ0810120000', 'EQPUMP02', 100.00, 0.00),
('EQ0810120001', 'EQPUMP02', 100.00, 0.00),
('EQ0810120002', 'EQPUMP02', 100.00, 0.00),
('EQ0810120003', 'EQPUMP02', 100.00, 0.00),
('EQ0810120004', 'EQPUMP02', 100.00, 0.00),
('EQ0810120005', 'EQPUMP02', 100.00, 0.00),
('EQ0810120006', 'EQPUMP02', 100.00, 0.00),
('EQ0810120007', 'EQPUMP02', 100.00, 0.00),
('EQ0810120008', 'EQPUMP02', 100.00, 0.00),
('EQ0810120009', 'EQPUMP02', 100.00, 0.00),
('EQ0810120010', 'EQMCHN02', 100.00, 0.00),
('EQ0810120011', 'EQMCHN02', 100.00, 0.00),
('EQ0810120012', 'EQMCHN02', 100.00, 0.00),
('EQ0810120013', 'EQMCHN02', 100.00, 0.00),
('EQ0810120014', 'EQMCHN02', 100.00, 0.00),
('EQ0810120015', 'EQMCHN02', 100.00, 0.00),
('EQ0810120016', 'EQMCHN02', 100.00, 0.00),
('EQ0810120017', 'EQMCHN02', 100.00, 0.00),
('EQ0810120018', 'EQMCHN02', 100.00, 0.00),
('EQ0810120019', 'EQMCHN02', 100.00, 0.00),
('EQ0810120020', 'EQMCHN02', 100.00, 0.00),
('EQ0810120021', 'EQMCHN02', 100.00, 0.00),
('EQ0810120022', 'EQMCHN02', 100.00, 0.00),
('EQ0810120023', 'EQMCHN02', 100.00, 0.00),
('EQ0810120024', 'EQMCHN02', 100.00, 0.00),
('EQ0810120025', 'EQMCHN02', 100.00, 0.00),
('EQ0810120026', 'EQMCHN02', 100.00, 0.00),
('EQ0810120027', 'EQMCHN02', 100.00, 0.00),
('EQ0810120028', 'EQMCHN02', 100.00, 0.00),
('EQ0810120029', 'EQMCHN02', 100.00, 0.00),
('EQ0810120030', 'EQMCHN02', 100.00, 0.00),
('EQ0810120031', 'EQMCHN02', 100.00, 0.00),
('EQ0810120032', 'EQMCHN02', 100.00, 0.00),
('EQ0810120033', 'EQMCHN02', 100.00, 0.00),
('EQ0810120034', 'EQMCHN02', 100.00, 0.00),
('EQ0810120035', 'EQMCHN02', 100.00, 0.00),
('EQ0810120036', 'EQMCHN02', 100.00, 0.00),
('EQ0810120037', 'EQMCHN02', 100.00, 0.00),
('EQ0810120038', 'EQMCHN02', 100.00, 0.00),
('EQ0810120039', 'EQMCHN02', 100.00, 0.00),
('EQ0810120040', 'EQMCHN02', 100.00, 0.00),
('EQ0810120041', 'EQMCHN02', 100.00, 0.00),
('EQ0810120042', 'EQMCHN02', 100.00, 0.00),
('EQ0810120043', 'EQMCHN02', 100.00, 0.00),
('EQ0810120044', 'EQMCHN02', 100.00, 0.00),
('EQ0810120045', 'EQMCHN02', 100.00, 0.00),
('EQ0810120046', 'EQMCHN02', 100.00, 0.00),
('EQ0810120047', 'EQMCHN02', 100.00, 0.00),
('EQ0810120048', 'EQMCHN02', 100.00, 0.00),
('EQ0810120049', 'EQMCHN02', 100.00, 0.00),
('EQ0810120050', 'EQGNTR02', 100.00, 0.00),
('EQ0810120051', 'EQGNTR02', 100.00, 0.00),
('EQ0810120052', 'EQGNTR02', 100.00, 0.00),
('EQ0810120053', 'EQGNTR02', 100.00, 0.00),
('EQ0810120054', 'EQGNTR02', 100.00, 0.00),
('EQ0810120055', 'EQGNTR02', 100.00, 0.00),
('EQ0810120056', 'EQGNTR02', 100.00, 0.00),
('EQ0810120057', 'EQGNTR02', 100.00, 0.00),
('EQ0810120058', 'EQGNTR02', 100.00, 0.00),
('EQ0810120059', 'EQFRNC02', 100.00, 0.00),
('EQ0810120060', 'EQFRNC02', 100.00, 0.00),
('EQ0810120061', 'EQFRNC02', 100.00, 0.00),
('EQ0810120062', 'EQFRNC02', 100.00, 0.00),
('EQ0810120063', 'EQFRNC02', 100.00, 0.00),
('EQ0810120064', 'EQFRNC02', 100.00, 0.00),
('EQ0810120065', 'EQFRNC02', 100.00, 0.00),
('EQ0810120066', 'EQFRNC02', 100.00, 0.00),
('EQ0810120067', 'EQFRNC02', 100.00, 0.00),
('EQ0810120068', 'EQFRNC02', 100.00, 0.00),
('EQ0810120069', 'EQFRNC02', 100.00, 0.00),
('EQ0810120070', 'EQFRNC02', 100.00, 0.00),
('EQ0810120071', 'EQFRNC02', 100.00, 0.00),
('EQ0810120072', 'EQFRNC02', 100.00, 0.00),
('EQ0810120073', 'EQFRNC02', 100.00, 0.00),
('EQ0810120074', 'EQFRNC02', 100.00, 0.00),
('EQ0810120075', 'EQFRNC02', 100.00, 0.00),
('EQ0810120076', 'EQFRNC02', 100.00, 0.00),
('EQ0810120077', 'EQFRNC02', 100.00, 0.00),
('EQ0810120078', 'EQFRNC02', 100.00, 0.00),
('EQ1310120000', 'EQMCHN02', 99.97, 7.50),
('EQ1310120001', 'EQMCHN02', 99.97, 7.50),
('EQ1310120002', 'EQMCHN02', 99.97, 7.50),
('EQ1310120003', 'EQMCHN02', 99.97, 7.50),
('EQ1310120004', 'EQMCHN02', 99.97, 7.50),
('EQ1310120005', 'EQMCHN02', 99.97, 7.50),
('EQ1310120006', 'EQMCHN02', 99.97, 7.50),
('EQ1310120007', 'EQMCHN02', 99.97, 7.50),
('EQ1310120008', 'EQMCHN02', 99.97, 7.50),
('EQ1310120009', 'EQMCHN02', 99.97, 7.50),
('EQ1310120010', 'EQMCHN02', 99.97, 7.50),
('EQ1310120011', 'EQMCHN02', 99.97, 7.50),
('EQ1310120012', 'EQMCHN02', 99.97, 7.50),
('EQ1310120013', 'EQMCHN02', 99.97, 7.50),
('EQ1310120014', 'EQMCHN02', 99.97, 7.50),
('EQ1310120015', 'EQPUMP02', 99.97, 9.00),
('EQ1310120016', 'EQPUMP02', 99.97, 9.00),
('EQ1310120017', 'EQPUMP02', 99.97, 9.00),
('EQ1310120018', 'EQPUMP02', 99.97, 9.00),
('EQ1310120019', 'EQPUMP02', 99.97, 9.00),
('EQ1310120020', 'EQPUMP02', 99.97, 9.00),
('EQ1310120021', 'EQPUMP02', 99.97, 9.00),
('EQ1310120022', 'EQPUMP02', 99.97, 9.00),
('EQ1310120023', 'EQPUMP02', 99.97, 9.00),
('EQ1310120024', 'EQPUMP02', 99.97, 9.00),
('EQ1310120025', 'EQPUMP02', 99.97, 9.00),
('EQ1310120026', 'EQPUMP02', 99.97, 9.00),
('EQ1310120027', 'EQPUMP02', 99.97, 9.00),
('EQ1310120028', 'EQPUMP02', 99.97, 9.00),
('EQ1310120029', 'EQPUMP02', 99.97, 9.00),
('EQ1310120030', 'EQPUMP02', 99.97, 9.00),
('EQ1310120031', 'EQPUMP02', 99.97, 9.00),
('EQ1310120032', 'EQPUMP02', 99.97, 9.00),
('EQ1310120033', 'EQPUMP02', 99.97, 9.00),
('EQ1310120034', 'EQPUMP02', 99.97, 9.00),
('EQ1310120035', 'EQPUMP02', 99.97, 9.00),
('EQ1310120036', 'EQPUMP02', 99.97, 9.00),
('EQ1310120037', 'EQPUMP02', 99.97, 9.00),
('EQ1310120038', 'EQPUMP02', 99.97, 9.00),
('EQ1310120039', 'EQPUMP02', 99.97, 9.00),
('EQ1310120040', 'EQPUMP02', 99.97, 9.00),
('EQ1310120041', 'EQPUMP02', 99.97, 9.00),
('EQ1310120042', 'EQPUMP02', 99.97, 9.00),
('EQ1310120043', 'EQPUMP02', 99.97, 9.00),
('EQ1310120044', 'EQPUMP02', 99.97, 9.00),
('EQ1310120045', 'EQGNTR02', 99.97, 8.00),
('EQ1310120046', 'EQGNTR02', 99.97, 8.00),
('EQ1310120047', 'EQGNTR02', 99.97, 8.00);

-- --------------------------------------------------------

--
-- Table structure for table `market_employee`
--

CREATE TABLE IF NOT EXISTS `market_employee` (
  `id` varchar(20) NOT NULL,
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
  `id` varchar(20) NOT NULL,
  `storage_equipment_id` varchar(20) NOT NULL,
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
  `id` varchar(20) NOT NULL,
  `storage_product_id` varchar(20) NOT NULL,
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
('MP1350306791646000', '', 'PRCROL02', 'A', 3.00, 793.85),
('MP1350306791646001', '', 'PRIRON02', 'A', 1.90, 608.09),
('MP1350306791646002', '', 'PRSLCA02', 'A', 2.50, 686.50),
('MP1350306791646003', '', 'PRWATR02', 'A', 1.00, 1000.00);

-- --------------------------------------------------------

--
-- Table structure for table `market_share`
--

CREATE TABLE IF NOT EXISTS `market_share` (
  `id` varchar(20) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `product` varchar(25) NOT NULL,
  `value` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `market_share`
--

INSERT INTO `market_share` (`id`, `zone`, `product`, `value`) VALUES
('MS1350306791646000', 'A', 'Gasoline', 1000.00),
('MS1350306791646001', 'A', 'Chemical', 1000.00);

-- --------------------------------------------------------

--
-- Table structure for table `product_advertisement`
--

CREATE TABLE IF NOT EXISTS `product_advertisement` (
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `product` varchar(25) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `ads` varchar(15) NOT NULL,
  `turn` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `product_advertisement`
--

INSERT INTO `product_advertisement` (`id`, `user`, `product`, `zone`, `ads`, `turn`) VALUES
('PA1350396586151000', 'bagus', 'Chemical', 'A', 'ADNEWS', 1);

-- --------------------------------------------------------

--
-- Table structure for table `req_borrow_bank`
--

CREATE TABLE IF NOT EXISTS `req_borrow_bank` (
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
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
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `zone` varchar(5) NOT NULL,
  `level` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storage`
--

INSERT INTO `storage` (`id`, `user`, `zone`, `level`) VALUES
('ST1350306791646000', 'demigodA', 'A', 1),
('ST1350306791646001', 'bagus', 'A', 1);

-- --------------------------------------------------------

--
-- Table structure for table `storage_equipment`
--

CREATE TABLE IF NOT EXISTS `storage_equipment` (
  `id` varchar(20) NOT NULL,
  `storage` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `storage_product`
--

CREATE TABLE IF NOT EXISTS `storage_product` (
  `id` varchar(20) NOT NULL,
  `desc` varchar(15) NOT NULL,
  `storage` varchar(20) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  `avg_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storage_product`
--

INSERT INTO `storage_product` (`id`, `desc`, `storage`, `size`, `avg_price`) VALUES
('PR1350306791646048', 'PRCROL02', '', 10.00, 3.00),
('PR1350306791646074', 'PRWWTR02', '', 15.80, -0.50);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `name` varchar(25) NOT NULL,
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
('bagus', 'bagus', 'bagus@g.a', 'Apr 1 1990', 'Life is chance. Death is certain.', '', 3305.94, 0, 'A', 2),
('demigodA', 'Adogimed', 'demigod@bizgame.net', 'Apr 4 1990', 'This is me', '', 79550.42, 0, 'A', 4);

-- --------------------------------------------------------

--
-- Table structure for table `user_contract`
--

CREATE TABLE IF NOT EXISTS `user_contract` (
  `id` varchar(20) NOT NULL,
  `request_storage` varchar(20) NOT NULL,
  `supplier_storage` varchar(20) NOT NULL,
  `product_desc` varchar(15) NOT NULL,
  `size` decimal(10,2) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `turn` bigint(20) NOT NULL,
  `accept` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_finance`
--

CREATE TABLE IF NOT EXISTS `user_finance` (
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `type` varchar(25) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_finance`
--

INSERT INTO `user_finance` (`id`, `user`, `type`, `total`) VALUES
('UF1350306791646006', 'bagus', 'Sales', 1425.69),
('UF1350306791646007', 'bagus', 'Transport', -3.39),
('UF1350306791646008', 'bagus', 'Retribution', -2.04),
('UF1350306791646022', 'bagus', 'Interest', -0.09),
('UF1350306791646023', 'bagus', 'Electricity', -540.00),
('UF1350306791646024', 'bagus', 'Raw Material', -203.19),
('UF1350306791646025', 'bagus', 'Wage', -5.70),
('UF1350306791646026', 'bagus', 'Operation', -4.95),
('UF1350306791646027', 'bagus', 'Depreciation', -12.18),
('UF1350396585835000', 'bagus', 'Advertisement', -50.00);

-- --------------------------------------------------------

--
-- Table structure for table `user_market_license`
--

CREATE TABLE IF NOT EXISTS `user_market_license` (
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `zone` varchar(5) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_market_license`
--

INSERT INTO `user_market_license` (`id`, `user`, `zone`) VALUES
('UM1350306791646000', 'demigodA', 'A'),
('UM1350306791646001', 'bagus', 'A');

-- --------------------------------------------------------

--
-- Table structure for table `user_message`
--

CREATE TABLE IF NOT EXISTS `user_message` (
  `id` varchar(20) NOT NULL,
  `sender` varchar(25) NOT NULL,
  `recipient` varchar(25) NOT NULL,
  `message` text NOT NULL,
  `unread` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_message`
--

INSERT INTO `user_message` (`id`, `sender`, `recipient`, `message`, `unread`) VALUES
('UP1350452715164000', 'bagus', 'demigodA', 'tes', 0),
('UP1350453123940000', 'demigodA', 'bagus', 'ape?', 0);

-- --------------------------------------------------------

--
-- Table structure for table `user_sector_blueprint`
--

CREATE TABLE IF NOT EXISTS `user_sector_blueprint` (
  `id` varchar(20) NOT NULL,
  `user` varchar(25) NOT NULL,
  `sector` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_sector_blueprint`
--

INSERT INTO `user_sector_blueprint` (`id`, `user`, `sector`) VALUES
('US1350306791646000', 'demigodA', 'Chemical Plant'),
('US1350306791646001', 'demigodA', 'Iron Mine'),
('US1350306791646002', 'demigodA', 'Oil Refinery'),
('US1350306791646003', 'demigodA', 'Oil Well'),
('US1350306791646004', 'demigodA', 'Petrol Power Plant'),
('US1350306791646005', 'demigodA', 'Silica Mine'),
('US1350306791646006', 'demigodA', 'Water Well'),
('US1450306791646000', 'bagus', 'Oil Refinery');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
