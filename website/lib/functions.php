<?php
/**
 * Shared helper functions for WGCinema
 */

/**
 * Fetch all movies from the database
 * 
 * @param PDO $pdo The database connection object
 * @param string|null $category 'showing' or 'upcoming'
 * @return array List of movies
 */
function getAllMovies($pdo, $category = null) {
    if ($category === 'showing') {
        $stmt = $pdo->prepare("SELECT * FROM movies WHERE release_date <= CURDATE() ORDER BY release_date DESC");
    } elseif ($category === 'upcoming') {
        $stmt = $pdo->prepare("SELECT * FROM movies WHERE release_date > CURDATE() ORDER BY release_date ASC");
    } else {
        $stmt = $pdo->query("SELECT * FROM movies ORDER BY release_date DESC");
    }
    $stmt->execute();
    return $stmt->fetchAll();
}

/**
 * Fetch a single movie by ID
 * 
 * @param PDO $pdo The database connection object
 * @param int $id The movie ID
 * @return array|false Movie details or false if not found
 */
function getMovieById($pdo, $id) {
    $stmt = $pdo->prepare("SELECT * FROM movies WHERE id = ?");
    $stmt->execute([$id]);
    return $stmt->fetch();
}

/**
 * Fetch all carousel slides from the database
 * 
 * @param PDO $pdo
 * @return array
 */
function getCarouselSlides($pdo) {
    $stmt = $pdo->query("SELECT * FROM carousel ORDER BY id ASC");
    return $stmt->fetchAll();
}

/**
 * Sanitize output to prevent XSS
 * 
 * @param string $data
 * @return string
 */
function h($data) {
    return htmlspecialchars($data, ENT_QUOTES, 'UTF-8');
}
?>
