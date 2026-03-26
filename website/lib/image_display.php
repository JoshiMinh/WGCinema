<?php
require_once 'config.php';

$type = $_GET['type'] ?? '';
$id = (int)($_GET['id'] ?? 0);

if (!$id)
    exit;

if ($type === 'movie') {
    $stmt = $pdo->prepare("SELECT poster FROM movies WHERE id = ?");
    $stmt->execute([$id]);
    $row = $stmt->fetch();
    if ($row && $row['poster']) {
        $imgPath = __DIR__ . '/../assets/images/' . $row['poster'];
        if (file_exists($imgPath)) {
            $ext = pathinfo($imgPath, PATHINFO_EXTENSION);
            header("Content-Type: image/" . ($ext === 'jpg' ? 'jpeg' : $ext));
            readfile($imgPath);
        }
        else {
            header("Content-Type: image/png");
            readfile(__DIR__ . '/../assets/images/background.png');
        }
    }
}
elseif ($type === 'carousel') {
    $stmt = $pdo->prepare("SELECT image FROM carousel WHERE id = ?");
    $stmt->execute([$id]);
    $row = $stmt->fetch();
    if ($row && $row['image']) {
        $imgPath = __DIR__ . '/../assets/images/' . $row['image'];
        if (file_exists($imgPath)) {
            $ext = pathinfo($imgPath, PATHINFO_EXTENSION);
            header("Content-Type: image/" . ($ext === 'jpg' ? 'jpeg' : $ext));
            readfile($imgPath);
        }
        else {
            header("Content-Type: image/png");
            readfile(__DIR__ . '/../assets/images/background.png');
        }
    }
}
