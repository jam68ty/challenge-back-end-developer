--DROP TABLE IF EXISTS `transaction_record`;
--DROP TABLE IF EXISTS `multi_currency_account`;
--DROP TABLE IF EXISTS `account`;
--DROP TABLE IF EXISTS `users`;

CREATE TABLE IF NOT EXISTS `users` (
  `user_serial` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) NOT NULL UNIQUE,
  `username` varchar(45) NOT NULL,
  `password` varchar(200) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`user_serial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `account` (
  `iban_code` varchar(34) NOT NULL,
  `user_id` varchar(45) NOT NULL,
  PRIMARY KEY (`iban_code`),
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `multi_currency_account` (
  `multi_currency_account_serial` int NOT NULL AUTO_INCREMENT,
  `multi_currency_account_id` varchar(45) NOT NULL,
  `currency` varchar(45) NOT NULL,
  `iban_code` varchar(34) NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0',
  `type` varchar(45) NOT NULL DEFAULT 'saving',
  PRIMARY KEY (`multi_currency_account_serial`),
  KEY `iban_code_idx` (`iban_code`),
  KEY `multi_currency_account_id_idx` (`multi_currency_account_id`),
  CONSTRAINT `iban_code_1` FOREIGN KEY (`iban_code`) REFERENCES `account` (`iban_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `transaction_record` (
  `transaction_id` binary(16) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `iban_code` varchar(34) NOT NULL,
  `currency` varchar(45) NOT NULL,
  `value_date` datetime NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  `type` varchar(45) NOT NULL,
  `multi_currency_account_id` varchar(45) NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `iban_code_idx` (`iban_code`),
  KEY `multi_currency_account_id_idx` (`multi_currency_account_id`),
  CONSTRAINT `iban_code` FOREIGN KEY (`iban_code`) REFERENCES `account` (`iban_code`),
  CONSTRAINT `multi_currency_account_id` FOREIGN KEY (`multi_currency_account_id`) REFERENCES `multi_currency_account` (`multi_currency_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
