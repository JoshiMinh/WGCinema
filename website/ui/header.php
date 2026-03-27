<?php
if (session_status() === PHP_SESSION_NONE) {
  session_start();
}
require_once __DIR__ . '/../lib/config.php';
require_once __DIR__ . '/../lib/functions.php';

$pageTitle = $pageTitle ?? 'WG Cinema';
?>
<!DOCTYPE html>
<html lang="en" data-bs-theme="dark">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title><?= h($pageTitle) ?></title>
  <link rel="icon" type="image/x-icon" href="icon.ico" />
  <link rel="stylesheet" href="assets/css/style.css" />
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
  <link rel="stylesheet" href="assets/owlcarousel/assets/owl.carousel.min.css" />
  <link rel="stylesheet" href="assets/owlcarousel/assets/owl.theme.default.css" />
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/fancybox/3.5.7/jquery.fancybox.min.css" />
  <link rel="stylesheet" href="https://unpkg.com/aos@2.3.1/dist/aos.css" />
  <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/fancybox/3.5.7/jquery.fancybox.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
  <script src="assets/owlcarousel/owl.carousel.min.js"></script>
  <script src="assets/js/main.js"></script>
</head>
<body class="modern-theme">
  <script src="https://unpkg.com/aos@2.3.1/dist/aos.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

  <!-- Animated Background -->
  <div class="bg-shape bg-shape-1"></div>
  <div class="bg-shape bg-shape-2"></div>
  <div class="bg-shape bg-shape-3"></div>

  <header class="header">
    <nav class="navbar navbar-expand-lg fixed-top">
      <div class="container-fluid">
        <a class="navbar-brand me-auto text-light" href="index.php">
          <img src="icon.ico" alt="WG Logo" width="45" />
        </a>
        <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar">
          <div class="offcanvas-header">
            <h5 class="offcanvas-title">
              <img src="icon.ico" alt="" width="45" />
            </h5>
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas"></button>
          </div>
          <div class="offcanvas-body">
            <ul class="navbar-nav flex-grow-1 pe-3">
              <li class="nav-item">
                <a class="nav-link mx-lg-2" href="index.php#pills-home">Now Showing</a>
              </li>
              <li class="nav-item">
                <a class="nav-link mx-lg-2" href="index.php#pills-sub">Coming Soon</a>
              </li>
              <li class="nav-item">
                <a class="nav-link mx-lg-2" href="about.php">About Us</a>
              </li>
              <li class="nav-item align-self-center">
                <div class="form-check form-switch ms-3">
                  <input class="form-check-input" type="checkbox" role="switch" checked id="flexSwitchCheckDefault" />
                  <label class="form-check-label" for="flexSwitchCheckDefault">Dark Mode</label>
                </div>
              </li>
            </ul>
          </div>
        </div>
        <?php if (!empty($_SESSION['user_name'])): ?>
          <span class="text-light me-3">Hello, <strong><?= h($_SESSION['user_name']) ?></strong></span>
          <a href="lib/logout.php" class="login-button left-bt-login">Logout</a>
        <?php else: ?>
          <a href="auth.php?mode=login" class="login-button left-bt-login">Login</a>
          <a href="auth.php?mode=register" class="login-button right-bt-sign">Register</a>
        <?php endif; ?>
        <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar">
          <span class="navbar-toggler-icon"></span>
        </button>
      </div>
    </nav>
  </header>