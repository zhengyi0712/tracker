/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50525
Source Host           : localhost:3306
Source Database       : bugsfly

Target Server Type    : MYSQL
Target Server Version : 50525
File Encoding         : 65001

Date: 2014-04-17 23:45:54
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL COMMENT '项目名称',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `intro` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_name` (`name`)
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
  CONSTRAINT `fk_project_user_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `fk_project_user_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目与用户映射表';

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
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` varchar(36) NOT NULL,
  `name` varchar(36) NOT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` varchar(36) NOT NULL,
  `project_id` varchar(36) NOT NULL COMMENT '项目ID',
  `status` varchar(50) NOT NULL COMMENT 'bug的状态',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `detail` longtext COMMENT '详情',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `create_user_id` varchar(36) NOT NULL,
  `assign_user_id` varchar(36) DEFAULT NULL COMMENT '分派用户ID',
  PRIMARY KEY (`id`),
  KEY `fk_bug_project` (`project_id`) USING BTREE,
  KEY `fk_bug_user` (`assign_user_id`) USING BTREE,
  KEY `fk_create_user_task` (`create_user_id`),
  CONSTRAINT `fk_create_user_task` FOREIGN KEY (`create_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_project_task` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `fk_user_task` FOREIGN KEY (`assign_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for task_tag
-- ----------------------------
DROP TABLE IF EXISTS `task_tag`;
CREATE TABLE `task_tag` (
  `tag_id` varchar(36) NOT NULL,
  `task_id` varchar(36) NOT NULL,
  PRIMARY KEY (`tag_id`,`task_id`),
  KEY `pk_task_tasktag` (`task_id`),
  CONSTRAINT `pk_task_tasktag` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`),
  CONSTRAINT `pk_tag_tasktag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL,
  `zh_name` varchar(15) NOT NULL COMMENT '中文名',
  `en_name` varchar(50) DEFAULT NULL,
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `mobile` varchar(15) NOT NULL COMMENT '手机',
  `md5` varchar(50) NOT NULL COMMENT '密码的md5加密结果',
  `create_time` datetime NOT NULL COMMENT '用户创建时间',
  `login_time` datetime DEFAULT NULL COMMENT '用户登录时间',
  `salt` varchar(50) NOT NULL COMMENT 'md5加密盐值',
  `disabled` tinyint(1) NOT NULL DEFAULT '0' COMMENT '禁用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_email` (`email`),
  UNIQUE KEY `uk_user_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- data of tag table
INSERT INTO `tag` VALUES ('BUG', '错误');
INSERT INTO `tag` VALUES ('FEATURE', '新功能');
INSERT INTO `tag` VALUES ('IMPROVE', '改善');
INSERT INTO `tag` VALUES ('OPTIMIZATION', '优化');