/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : acis2

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2018-01-24 14:05:14
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `character_shortcut_configs`
-- ----------------------------
DROP TABLE IF EXISTS `character_shortcut_configs`;
CREATE TABLE `character_shortcut_configs` (
  `char_obj_id` decimal(11,0) NOT NULL DEFAULT '0',
  `slot` decimal(3,0) NOT NULL DEFAULT '0',
  `page` decimal(3,0) NOT NULL DEFAULT '0',
  `item_id` decimal(16,0) DEFAULT NULL,
  PRIMARY KEY (`char_obj_id`,`slot`,`page`),
  KEY `item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
