SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

CREATE DATABASE IF NOT EXISTS `cinema_data`;
USE `cinema_data`;

CREATE TABLE `accounts` (
  `account_email` VARCHAR(255) NOT NULL PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL,
  `gender` ENUM('Male', 'Female', 'Other') NOT NULL,
  `date_of_birth` DATE NOT NULL,
  `membership` BOOLEAN NOT NULL DEFAULT 0,
  `transactions` INT UNSIGNED NOT NULL DEFAULT 0,
  `password_hash` CHAR(64) NOT NULL,
  `admin` BOOLEAN NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `movies` (
  `id` TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `title` VARCHAR(100) NOT NULL,
  `director` VARCHAR(100) NOT NULL,
  `release_date` DATE NOT NULL,
  `duration` SMALLINT UNSIGNED NOT NULL,
  `language` VARCHAR(50) NOT NULL,
  `rating` VARCHAR(5) NOT NULL,
  `link` VARCHAR(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `showrooms` (
  `showroom_id` TINYINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `showroom_name` VARCHAR(50) NOT NULL,
  `max_chairs` SMALLINT UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `showrooms` VALUES
(1, 'Mini', 100),
(2, 'Standard', 200),
(3, 'Standard', 200),
(4, 'IMAX', 380);

CREATE TABLE `showtimes` (
  `showtime_id` SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `time` TIME NOT NULL,
  `movie_id` TINYINT UNSIGNED NOT NULL,
  `date` DATE NOT NULL,
  `showroom_id` TINYINT UNSIGNED NOT NULL,
  `reserved_chairs` SMALLINT UNSIGNED DEFAULT 0,
  `chairs_booked` VARCHAR(255) DEFAULT NULL,
  FOREIGN KEY (`movie_id`) REFERENCES `movies`(`id`),
  FOREIGN KEY (`showroom_id`) REFERENCES `showrooms`(`showroom_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `transactions` (
  `transaction_id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  `transaction_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `movie_id` TINYINT UNSIGNED NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `seats_preserved` VARCHAR(255) NOT NULL,
  `showroom_id` TINYINT UNSIGNED NOT NULL,
  `account_email` VARCHAR(255) NOT NULL,
  `showtime_id` SMALLINT UNSIGNED NOT NULL,
  FOREIGN KEY (`movie_id`) REFERENCES `movies`(`id`),
  FOREIGN KEY (`showroom_id`) REFERENCES `showrooms`(`showroom_id`),
  FOREIGN KEY (`account_email`) REFERENCES `accounts`(`account_email`),
  FOREIGN KEY (`showtime_id`) REFERENCES `showtimes`(`showtime_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

COMMIT;