CREATE TABLE IF NOT EXISTS `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `topic` (
  `topic_id` bigint NOT NULL  AUTO_INCREMENT,
  `topic_name` int NOT NULL,
  `subscribers` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`topic_id`),
  UNIQUE KEY `topic_name_UNIQUE` (`topic_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
