<?php
// WGCinema Database Configuration

define('DB_HOST', 'localhost');
define('DB_NAME', 'wgcinema');
define('DB_USER', 'root');
define('DB_PASS', '');

try {
    $dsn = sprintf(
        'mysql:host=%s;dbname=%s;charset=utf8mb4',
        DB_HOST,
        DB_NAME
    );
    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
    ];
    $pdo = new PDO($dsn, DB_USER, DB_PASS, $options);
} catch (PDOException $e) {
    // In production, log the error and show a generic message.
    die('Database connection failed: ' . $e->getMessage());
}
?>