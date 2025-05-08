-- Table to store user account information
CREATE TABLE `accounts` (
  `account_email` VARCHAR(255) NOT NULL, -- Unique email for each account
  `name` VARCHAR(50) NOT NULL, -- Name of the account holder
  `gender` ENUM('Male', 'Female', 'Other') NOT NULL, -- Gender of the user
  `date_of_birth` DATE NOT NULL, -- Date of birth of the user
  `membership` TINYINT(1) NOT NULL DEFAULT 0, -- Membership status (0 = no, 1 = yes)
  `transactions` INT(10) UNSIGNED NOT NULL DEFAULT 0, -- Number of transactions made
  `password_hash` CHAR(64) NOT NULL, -- Hashed password for security
  `admin` TINYINT(1) NOT NULL DEFAULT 0, -- Admin status (0 = no, 1 = yes)
  PRIMARY KEY (`account_email`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Table to store movie details
CREATE TABLE `movies` (
  `id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT, -- Unique ID for each movie
  `title` VARCHAR(100) NOT NULL, -- Title of the movie
  `director` VARCHAR(100) NOT NULL, -- Director of the movie
  `release_date` DATE NOT NULL, -- Release date of the movie
  `duration` INT(10) UNSIGNED NOT NULL, -- Duration of the movie in minutes
  `language` VARCHAR(50) NOT NULL, -- Language of the movie
  `age_rating` ENUM('PG', 'PG-13', 'PG-16', 'R') DEFAULT NULL, -- Age rating of the movie
  `trailer` VARCHAR(255) DEFAULT NULL, -- URL of the movie trailer
  `poster` VARCHAR(255) DEFAULT NULL, -- URL of the movie poster
  `description` TEXT DEFAULT NULL, -- Description or synopsis of the movie
  PRIMARY KEY (`id`)
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Table to store showroom details
CREATE TABLE `showrooms` (
  `showroom_id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT, -- Unique ID for each showroom
  `showroom_name` VARCHAR(50) NOT NULL, -- Name of the showroom
  `max_chairs` SMALLINT(5) UNSIGNED NOT NULL, -- Maximum seating capacity
  `rowCount` INT(11) DEFAULT NULL, -- Number of rows in the showroom
  `collumnCount` INT(11) DEFAULT NULL, -- Number of columns in the showroom
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

-- Table to store showtimes for movies
CREATE TABLE `showtimes` (
  `showtime_id` SMALLINT(5) UNSIGNED NOT NULL AUTO_INCREMENT, -- Unique ID for each showtime
  `time` TIME NOT NULL, -- Time of the show
  `movie_id` TINYINT(3) UNSIGNED NOT NULL, -- Associated movie ID
  `date` DATE NOT NULL, -- Date of the show
  `showroom_id` TINYINT(3) UNSIGNED NOT NULL, -- Associated showroom ID
  `reserved_chairs` SMALLINT(5) UNSIGNED DEFAULT 0, -- Number of reserved chairs
  `chairs_booked` VARCHAR(255) NOT NULL DEFAULT '', -- Details of booked chairs
  PRIMARY KEY (`showtime_id`),
  FOREIGN KEY (`movie_id`) REFERENCES `movies`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`showroom_id`) REFERENCES `showrooms`(`showroom_id`) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;

-- Table to store transaction details
CREATE TABLE `transactions` (
  `transaction_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, -- Unique ID for each transaction
  `transaction_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(), -- Date and time of the transaction
  `movie_id` TINYINT(3) UNSIGNED NOT NULL, -- Associated movie ID
  `amount` DECIMAL(10,2) NOT NULL, -- Transaction amount
  `seats_preserved` VARCHAR(255) NOT NULL, -- Details of reserved seats
  `showroom_id` TINYINT(3) UNSIGNED NOT NULL, -- Associated showroom ID
  `account_email` VARCHAR(255) NOT NULL, -- Associated account email
  `showtime_id` SMALLINT(5) UNSIGNED NOT NULL, -- Associated showtime ID
  PRIMARY KEY (`transaction_id`),
  FOREIGN KEY (`movie_id`) REFERENCES `movies`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`showroom_id`) REFERENCES `showrooms`(`showroom_id`) ON DELETE CASCADE,
  FOREIGN KEY (`account_email`) REFERENCES `accounts`(`account_email`) ON DELETE CASCADE,
  FOREIGN KEY (`showtime_id`) REFERENCES `showtimes`(`showtime_id`) ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_general_ci;