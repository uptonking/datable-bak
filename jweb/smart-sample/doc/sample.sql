/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50515
Source Host           : localhost:3306
Source Database       : sample

Target Server Type    : MYSQL
Target Server Version : 50515
File Encoding         : 65001

Date: 2013-09-23 11:44:01
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_type_id` bigint(20) NOT NULL,
  `product_name` varchar(100) NOT NULL,
  `product_code` char(5) NOT NULL,
  `price` int(10) NOT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '1', 'iPhone 3g', 'MP001', '3000', 'iPhone 3g 移动电话');
INSERT INTO `product` VALUES ('2', '1', 'iPhone 3gs', 'MP002', '3500', 'iPhone 3gs 移动电话');
INSERT INTO `product` VALUES ('3', '2', 'iPad 2', 'TC001', '3000', 'iPad 2 平板电脑');
INSERT INTO `product` VALUES ('4', '1', 'iPhone 4', 'MP003', '4000', 'iPhone 4 移动电话');
INSERT INTO `product` VALUES ('5', '1', 'iPhone 4s', 'MP004', '4500', 'iPhone 4s 移动电话');
INSERT INTO `product` VALUES ('6', '2', 'iPad 3', 'TC002', '3000', 'iPad 3 平板电脑');
INSERT INTO `product` VALUES ('7', '1', 'iPhone 5', 'MP005', '5000', 'iPhone 5 移动电话');
INSERT INTO `product` VALUES ('8', '2', 'iPad mini', 'TC003', '3000', 'iPad mini 平板电脑');
INSERT INTO `product` VALUES ('9', '1', '1', '1', '1', '');
INSERT INTO `product` VALUES ('10', '1', '2', '2', '2', '');
INSERT INTO `product` VALUES ('11', '1', '3', '3', '3', '');
INSERT INTO `product` VALUES ('12', '1', '4', '4', '4', '');

-- ----------------------------
-- Table structure for product_type
-- ----------------------------
DROP TABLE IF EXISTS `product_type`;
CREATE TABLE `product_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_type_name` varchar(50) DEFAULT NULL,
  `product_type_code` char(2) DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of product_type
-- ----------------------------
INSERT INTO `product_type` VALUES ('1', 'Mobile Phone', 'MP', '移动电话');
INSERT INTO `product_type` VALUES ('2', 'Tablet Computer', 'TC', '平板电脑');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'admin');
