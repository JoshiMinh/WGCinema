<?php
require_once 'config.php';

$type = $_GET['type'] ?? '';
$id = (int)($_GET['id'] ?? 0);

if (!$id) exit;

if ($type === 'movie') {
    $stmt = $pdo->prepare("SELECT poster_blob FROM movies WHERE id = ?");
    $stmt->execute([$id]);
    $row = $stmt->fetch();
    if ($row && $row['poster_blob']) {
        header("Content-Type: image/jpeg"); // Adjust if multiple types supported
        echo $row['poster_blob'];
    }
} elseif ($type === 'carousel') {
    $stmt = $pdo->prepare("SELECT image_blob FROM carousel WHERE id = ?");
    $stmt->execute([$id]);
    $row = $stmt->fetch();
    if ($row && $row['image_blob']) {
        header("Content-Type: image/jpeg");
        echo $row['image_blob'];
    }
}
