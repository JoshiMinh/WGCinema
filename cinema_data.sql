-- User accounts table
CREATE TABLE `accounts` (
  `account_email` VARCHAR(255) NOT NULL, -- Unique email
  `name` VARCHAR(50) NOT NULL, -- Account holder name
  `gender` ENUM('Male', 'Female', 'Other') NOT NULL, -- Gender
  `date_of_birth` DATE NOT NULL, -- Birth date
  `membership` TINYINT(1) NOT NULL DEFAULT 0, -- Membership (0=no, 1=yes)
  `transactions` INT(10) UNSIGNED NOT NULL DEFAULT 0, -- Transaction count
  `password_hash` CHAR(64) NOT NULL, -- Password hash
  `admin` TINYINT(1) NOT NULL DEFAULT 0, -- Admin (0=no, 1=yes)
  PRIMARY KEY (`account_email`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Movies table
CREATE TABLE `movies` (
  `id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT, -- Movie ID
  `title` VARCHAR(100) NOT NULL, -- Movie title
  `director` VARCHAR(100) NOT NULL, -- Director
  `release_date` DATE NOT NULL, -- Release date
  `duration` INT(10) UNSIGNED NOT NULL, -- Duration (minutes)
  `language` VARCHAR(50) NOT NULL, -- Language
  `age_rating` ENUM('PG', 'PG-13', 'PG-16', 'R') DEFAULT NULL, -- Age rating
  `trailer` VARCHAR(255) DEFAULT NULL, -- Trailer URL
  `poster` VARCHAR(255) DEFAULT NULL, -- Poster URL
  `description` TEXT DEFAULT NULL, -- Synopsis
  PRIMARY KEY (`id`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Showrooms table
CREATE TABLE `showrooms` (
  `showroom_id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT, -- Showroom ID
  `showroom_name` VARCHAR(50) NOT NULL, -- Showroom name
  `max_chairs` SMALLINT(5) UNSIGNED NOT NULL, -- Seating capacity
  `rowCount` INT(11) DEFAULT NULL, -- Row count
  `collumnCount` INT(11) DEFAULT NULL, -- Column count
  PRIMARY KEY (`showroom_id`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Predefined showroom data
INSERT INTO `showrooms` (`showroom_id`, `showroom_name`, `max_chairs`, `rowCount`, `collumnCount`) VALUES
(1, 'Mini', 100, 10, 10),
(2, 'Standard', 200, 10, 20),
(3, 'Standard', 200, 10, 20),
(4, 'IMAX', 300, 12, 25);

-- Showtimes table
CREATE TABLE `showtimes` (
  `showtime_id` SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT, -- Showtime ID
  `time` TIME NOT NULL, -- Show time
  `movie_id` TINYINT(3) UNSIGNED NOT NULL, -- Movie ID
  `date` DATE NOT NULL, -- Show date
  `showroom_id` TINYINT(3) UNSIGNED NOT NULL, -- Showroom ID
  `reserved_chairs` SMALLINT(5) UNSIGNED DEFAULT 0, -- Reserved chairs
  `chairs_booked` VARCHAR(255) NOT NULL DEFAULT '', -- Booked chairs
  PRIMARY KEY (`showtime_id`),
  FOREIGN KEY (`movie_id`) REFERENCES `movies`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`showroom_id`) REFERENCES `showrooms`(`showroom_id`) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Transactions table
CREATE TABLE `transactions` (
  `transaction_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, -- Transaction ID
  `transaction_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- Date/time
  `movie_id` TINYINT(3) UNSIGNED NOT NULL, -- Movie ID
  `amount` DECIMAL(10,2) NOT NULL, -- Amount
  `seats_preserved` VARCHAR(255) NOT NULL, -- Reserved seats
  `showroom_id` TINYINT(3) UNSIGNED NOT NULL, -- Showroom ID
  `account_email` VARCHAR(255) NOT NULL, -- Account email
  `showtime_id` SMALLINT(5) UNSIGNED NOT NULL, -- Showtime ID
  PRIMARY KEY (`transaction_id`),
  FOREIGN KEY (`movie_id`) REFERENCES `movies`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`showroom_id`) REFERENCES `showrooms`(`showroom_id`) ON DELETE CASCADE,
  FOREIGN KEY (`account_email`) REFERENCES `accounts`(`account_email`) ON DELETE CASCADE,
  FOREIGN KEY (`showtime_id`) REFERENCES `showtimes`(`showtime_id`) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;