<?php
require_once 'config.php';

function outputImage($imgPath, $defaultImg) {
    if (file_exists($imgPath)) {
        $ext = strtolower(pathinfo($imgPath, PATHINFO_EXTENSION));
        header("Content-Type: image/" . ($ext === 'jpg' ? 'jpeg' : $ext));
        readfile($imgPath);
    } else {
        header("Content-Type: image/png");
        readfile($defaultImg);
    }
    exit;
}

$type = $_GET['type'] ?? '';
$id = (int)($_GET['id'] ?? 0);

if (!$id) exit;

$defaultImg = __DIR__ . '/../assets/images/background.png';

switch ($type) {
    case 'movie':
        $stmt = $pdo->prepare("SELECT poster FROM movies WHERE id = ?");
        $stmt->execute([$id]);
        $row = $stmt->fetch();
        if ($row && $row['poster']) {
            $imgPath = __DIR__ . '/../assets/images/' . $row['poster'];
            outputImage($imgPath, $defaultImg);
        }
        break;
    case 'carousel':
        $stmt = $pdo->prepare("SELECT image FROM carousel WHERE id = ?");
        $stmt->execute([$id]);
        $row = $stmt->fetch();
        if ($row && $row['image']) {
            $imgPath = __DIR__ . '/../assets/images/' . $row['image'];
            outputImage($imgPath, $defaultImg);
        }
        break;
}