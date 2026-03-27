<?php
/**
 * Shared helper functions for WGCinema
 */

/**
 * Fetch all movies from the database.
 *
 * @param PDO $pdo The database connection object.
 * @param string|null $category 'showing', 'upcoming', or null for all.
 * @return array List of movies.
 */
function getAllMovies(PDO $pdo, ?string $category = null): array
{
    $cols = "id, title, director, release_date, duration, language, age_rating, trailer, poster, description";
    switch ($category) {
        case 'showing':
            $stmt = $pdo->prepare("SELECT $cols FROM movies WHERE release_date <= CURDATE() ORDER BY release_date DESC");
            $stmt->execute();
            break;
        case 'upcoming':
            $stmt = $pdo->prepare("SELECT $cols FROM movies WHERE release_date > CURDATE() ORDER BY release_date ASC");
            $stmt->execute();
            break;
        default:
            $stmt = $pdo->query("SELECT $cols FROM movies ORDER BY release_date DESC");
    }
    return $stmt->fetchAll();
}

/**
 * Fetch a single movie by ID.
 *
 * @param PDO $pdo The database connection object.
 * @param int $id The movie ID.
 * @return array|false Movie details or false if not found.
 */
function getMovieById(PDO $pdo, int $id)
{
    $cols = "id, title, director, release_date, duration, language, age_rating, trailer, poster, description";
    $stmt = $pdo->prepare("SELECT $cols FROM movies WHERE id = ?");
    $stmt->execute([$id]);
    return $stmt->fetch();
}

/**
 * Fetch all carousel slides from the database.
 *
 * @param PDO $pdo
 * @return array
 */
function getCarouselSlides(PDO $pdo): array
{
    $stmt = $pdo->query("SELECT * FROM carousel ORDER BY id ASC");
    return $stmt->fetchAll();
}

/**
 * Fetch showtimes for a specific movie.
 *
 * @param PDO $pdo
 * @param int $movieId
 * @return array
 */
function getShowtimesByMovieId(PDO $pdo, int $movieId): array
{
    $sql = "
        SELECT s.showtime_id, s.time, s.movie_id, s.date, s.showroom_id, s.regular_price, s.vip_price, r.showroom_name
        FROM showtimes s
        JOIN showrooms r ON s.showroom_id = r.showroom_id
        WHERE s.movie_id = ? AND s.date >= CURDATE()
        ORDER BY s.date ASC, s.time ASC
    ";
    $stmt = $pdo->prepare($sql);
    $stmt->execute([$movieId]);
    return $stmt->fetchAll();
}

/**
 * Sanitize output to prevent XSS.
 *
 * @param string $data
 * @return string
 */
function h(string $data): string
{
    return htmlspecialchars($data, ENT_QUOTES, 'UTF-8');
}