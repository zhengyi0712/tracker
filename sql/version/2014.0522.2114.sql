SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` varchar(36) NOT NULL,
  `name` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  `intro` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project_user
-- ----------------------------
DROP TABLE IF EXISTS `project_user`;
CREATE TABLE `project_user` (
  `project_id` varchar(36) NOT NULL,
  `user_id` varchar(36) NOT NULL,
  `role` varchar(50) NOT NULL,
  PRIMARY KEY (`project_id`,`user_id`),
  KEY `fk_project_user_user` (`user_id`),
  CONSTRAINT `fk_project_user_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `fk_project_user_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin` (
  `admin_id` varchar(36) NOT NULL,
  PRIMARY KEY (`admin_id`),
  CONSTRAINT `fk_SYS_ADMIN_USER` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `project_id` varchar(36) NOT NULL,
  `status` varchar(50) NOT NULL COMMENT '',
  `title` varchar(255) NOT NULL,
  `detail` longtext,
  `finish_time` datetime DEFAULT NULL,
  `create_user_id` varchar(36) NOT NULL,
  `assign_user_id` varchar(36) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_user_id` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pk_project_task` (`project_id`),
  KEY `pk_create_user_task` (`create_user_id`),
  KEY `pk_assign_user_task` (`assign_user_id`),
  KEY `update_user_id` (`update_user_id`),
  CONSTRAINT `pk_assign_user_task` FOREIGN KEY (`assign_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `pk_create_user_task` FOREIGN KEY (`create_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `pk_project_task` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`),
  CONSTRAINT `task_ibfk_1` FOREIGN KEY (`update_user_id`) REFERENCES `user` (`id`)
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
  CONSTRAINT `pk_tag_tasktag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`),
  CONSTRAINT `pk_task_tasktag` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL,
  `zh_name` varchar(15) NOT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `mobile` varchar(15) NOT NULL,
  `md5` varchar(50) NOT NULL,
  `create_time` datetime NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `salt` varchar(50) NOT NULL,
  `disabled` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_user_email` (`email`),
  UNIQUE KEY `uk_user_mobile` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `task_log` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime NOT NULL,
  `content` varchar(1000) NOT NULL,
  `task_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_task_task_log` (`task_id`),
  CONSTRAINT `fk_task_task_log` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES ('BUG', '错误');
INSERT INTO `tag` VALUES ('FEATURE', '新功能');
INSERT INTO `tag` VALUES ('IMPROVE', '改善');
INSERT INTO `tag` VALUES ('OPTIMIZATION', '优化');

-- system admin data
-- initial password is 123456
INSERT INTO USER (
	id,
	zh_name,
	en_name,
	email,
	mobile,
	md5,
	create_time,
	login_time,
	salt,
	disabled
)
VALUES
	(
		'startagain',
		'管理员',
		'admin',
		'admin@bugsfly.com',
		'18888888888',
		'ea42974e2cf40a9f26ad69f643ddbaf7',
		now(),
		null,
		'd759b0204c7e469f9574849dc0648f67',
		0
	);

INSERT INTO sys_admin (admin_id)
VALUES
	('startagain');
