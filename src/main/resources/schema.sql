DROP TABLE IF EXISTS `transaction_record`;
DROP TABLE IF EXISTS `account`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE IF NOT EXISTS `users` (
  `user_serial` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(45) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(200) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`user_serial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE `users` ADD CONSTRAINT `user_id` UNIQUE KEY (`user_id`);
CREATE TABLE IF NOT EXISTS `account` (
  `iban_code` VARCHAR(26) NOT NULL,
  `currency` VARCHAR(45) NOT NULL,
  `balance` DECIMAL NOT NULL DEFAULT 0,
  `type` VARCHAR(45) NOT NULL,
  `user_id` VARCHAR(45) NOT NULL UNIQUE,
  PRIMARY KEY (`iban_code`),
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


CREATE TABLE IF NOT EXISTS `transaction_record` (
  `transaction_id` VARCHAR(200) NOT NULL,
  `amount` DECIMAL NOT NULL,
  `account_iban_code` VARCHAR(26) NOT NULL,
  `currency` VARCHAR(45) NOT NULL,
  `value_date` DATETIME NOT NULL,
  `description` VARCHAR(100) NULL,
  PRIMARY KEY (`transaction_id`),
  INDEX `account_iban_code_idx` (`account_iban_code` ASC) VISIBLE,
  CONSTRAINT `account_iban_code`
    FOREIGN KEY (`account_iban_code`)
    REFERENCES `ebanking`.`account` (`iban_code`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
