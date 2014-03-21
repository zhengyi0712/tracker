/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : bugsfly

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2014-03-21 19:48:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for bug
-- ----------------------------
DROP TABLE IF EXISTS `bug`;
CREATE TABLE `bug` (
  `id` varchar(36) NOT NULL,
  `project_id` varchar(36) DEFAULT NULL COMMENT '项目ID',
  `bug_status` varchar(50) NOT NULL COMMENT 'bug的状态',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `detail` longtext COMMENT '详情',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `assign_user_id` varchar(36) DEFAULT NULL COMMENT '分派用户ID',
  PRIMARY KEY (`id`),
  KEY `fk_bug_project` (`project_id`),
  KEY `fk_bug_user` (`assign_user_id`),
  CONSTRAINT `fk_bug_user` FOREIGN KEY (`assign_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_bug_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for company
-- ----------------------------
DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '公司名称',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `uk_company_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公司表';

-- ----------------------------
-- Table structure for company_user
-- ----------------------------
DROP TABLE IF EXISTS `company_user`;
CREATE TABLE `company_user` (
  `company_id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `role` varchar(50) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`company_id`,`user_id`),
  KEY `fk_company_user_user` (`user_id`),
  CONSTRAINT `fk_company_user_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_company_user_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='公司与用户多对多映射表';

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `company_id` varchar(36) NOT NULL COMMENT '公司ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `fk_project_company` (`company_id`),
  CONSTRAINT `fk_project_company` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目表';

-- ----------------------------
-- Table structure for project_user
-- ----------------------------
DROP TABLE IF EXISTS `project_user`;
CREATE TABLE `project_user` (
  `project_id` varchar(36) NOT NULL COMMENT '项目ID',
  `user_id` varchar(36) NOT NULL COMMENT '用户ID',
  `role` varchar(50) NOT NULL COMMENT '角色',
  PRIMARY KEY (`project_id`,`user_id`),
  KEY `fk_project_user_user` (`user_id`),
  CONSTRAINT `fk_project_user_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_project_user_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin` (
  `admin_id` varchar(36) NOT NULL,
  PRIMARY KEY (`admin_id`),
  CONSTRAINT `fk_SYS_ADMIN_USER` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统管理员表';

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL,
  `ch_name` varchar(15) NOT NULL COMMENT '中文名',
  `en_name` varchar(50) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(15) DEFAULT NULL COMMENT '手机',
  `md5` varchar(50) NOT NULL COMMENT '密码的md5加密结果',
  `create_time` datetime NOT NULL COMMENT '用户创建时间',
  `login_time` datetime DEFAULT NULL COMMENT '用户登录时间',
  `salt` varchar(50) DEFAULT NULL COMMENT 'md5加密盐值',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_email` (`email`),
  UNIQUE KEY `uk_user_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';