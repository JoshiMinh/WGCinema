-- ===========================
-- Drop and Recreate Accounts Table
-- ===========================
DROP TABLE IF EXISTS `accounts`;
CREATE TABLE `accounts` (
  `account_email`   VARCHAR(255) COLLATE utf8mb4_general_ci NOT NULL,
  `name`            VARCHAR(50)  COLLATE utf8mb4_general_ci NOT NULL,
  `gender`          ENUM('Male', 'Female', 'Other') COLLATE utf8mb4_general_ci NOT NULL,
  `date_of_birth`   DATE         NOT NULL,
  `membership`      TINYINT(1)   NOT NULL DEFAULT 0,
  `password_hash`   CHAR(64)     COLLATE utf8mb4_general_ci NOT NULL,
  `admin`           TINYINT(1)   NOT NULL DEFAULT 0,
  PRIMARY KEY (`account_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ===========================
-- Drop and Recreate Movies Table
-- ===========================
DROP TABLE IF EXISTS `movies`;
CREATE TABLE `movies` (
  `id`           TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `title`        VARCHAR(100)     COLLATE utf8mb4_general_ci NOT NULL,
  `director`     VARCHAR(100)     COLLATE utf8mb4_general_ci NOT NULL,
  `release_date` DATE             NOT NULL,
  `duration`     INT UNSIGNED     NOT NULL,
  `language`     VARCHAR(50)      COLLATE utf8mb4_general_ci NOT NULL,
  `age_rating`   ENUM('PG', 'PG-13', 'PG-16', 'R') COLLATE utf8mb4_general_ci DEFAULT NULL,
  `trailer`      VARCHAR(255)     COLLATE utf8mb4_general_ci DEFAULT NULL,
  `poster`       VARCHAR(255)     COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description`  TEXT             COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ===========================
-- Drop and Recreate Carousel Table
-- ===========================
DROP TABLE IF EXISTS `carousel`;
CREATE TABLE `carousel` (
  `id`          TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `image`       VARCHAR(255)     COLLATE utf8mb4_general_ci NOT NULL,
  `title`       VARCHAR(100)     COLLATE utf8mb4_general_ci DEFAULT NULL,
  `description` VARCHAR(255)     COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ===========================
-- Drop and Recreate Showrooms Table
-- ===========================
DROP TABLE IF EXISTS `showrooms`;
CREATE TABLE `showrooms` (
  `showroom_id`   TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `showroom_name` VARCHAR(50)      COLLATE utf8mb4_general_ci NOT NULL,
  `max_chairs`    SMALLINT UNSIGNED NOT NULL,
  `row_count`     INT DEFAULT NULL,
  `column_count`  INT DEFAULT NULL,
  PRIMARY KEY (`showroom_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ===========================
-- Drop and Recreate Showtimes Table
-- ===========================
DROP TABLE IF EXISTS `showtimes`;
CREATE TABLE `showtimes` (
  `showtime_id`   SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `time`          TIME             NOT NULL,
  `movie_id`      TINYINT UNSIGNED NOT NULL,
  `date`          DATE             NOT NULL,
  `showroom_id`   TINYINT UNSIGNED NOT NULL,
  `regular_price` INT              NOT NULL DEFAULT 80000,
  `vip_price`     INT              NOT NULL DEFAULT 85000,
  PRIMARY KEY (`showtime_id`),
  KEY `idx_movie_id` (`movie_id`),
  KEY `idx_showroom_id` (`showroom_id`),
  CONSTRAINT `fk_showtimes_movie`    FOREIGN KEY (`movie_id`)    REFERENCES `movies`    (`id`)           ON DELETE CASCADE,
  CONSTRAINT `fk_showtimes_showroom` FOREIGN KEY (`showroom_id`) REFERENCES `showrooms` (`showroom_id`)  ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ===========================
-- Drop and Recreate Transactions Table
-- ===========================
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `transaction_id`   INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  `transaction_date` DATETIME          NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `account_email`    VARCHAR(255)      COLLATE utf8mb4_general_ci NOT NULL,
  `showtime_id`      SMALLINT UNSIGNED NOT NULL,
  `total_amount`     DECIMAL(10,2)     NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `idx_account_email` (`account_email`),
  KEY `idx_showtime_id`  (`showtime_id`),
  CONSTRAINT `fk_transactions_account`  FOREIGN KEY (`account_email`) REFERENCES `accounts`  (`account_email`) ON DELETE CASCADE,
  CONSTRAINT `fk_transactions_showtime` FOREIGN KEY (`showtime_id`)   REFERENCES `showtimes` (`showtime_id`)   ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ===========================
-- Drop and Recreate Tickets Table
-- ===========================
DROP TABLE IF EXISTS `tickets`;
CREATE TABLE `tickets` (
  `ticket_id`      INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  `transaction_id` INT UNSIGNED      NOT NULL,
  `seat_identifier` VARCHAR(10)      COLLATE utf8mb4_general_ci NOT NULL,
  `seat_type`      ENUM('regular', 'vip') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'regular',
  `price`          DECIMAL(10,2)     NOT NULL,
  PRIMARY KEY (`ticket_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  CONSTRAINT `fk_tickets_transaction` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`transaction_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;