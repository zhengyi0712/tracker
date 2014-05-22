CREATE TABLE `task_log` (
  `id` varchar(36) NOT NULL,
  `create_time` datetime NOT NULL,
  `content` varchar(1000) NOT NULL,
  `task_id` varchar(36) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_task_task_log` (`task_id`),
  CONSTRAINT `fk_task_task_log` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;