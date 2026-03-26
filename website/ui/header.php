<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
require_once __DIR__ . '/../lib/config.php';
require_once __DIR__ . '/../lib/functions.php';

if (!isset($pageTitle)) {
    $pageTitle = 'WG Cinema';
}
?>
<!DOCTYPE html>
<html lang="vn" data-bs-theme="dark">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title><?php echo h($pageTitle); ?></title>
    <link rel="icon" type="image/x-icon" href="icon.ico" />
    <link rel="stylesheet" href="assets/css/style.css" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css" />
    <link rel="stylesheet" href="assets/owlcarousel/assets/owl.carousel.min.css" />
    <link rel="stylesheet" href="assets/owlcarousel/assets/owl.theme.default.css" />
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/fancybox/3.5.7/jquery.fancybox.min.css" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/fancybox/3.5.7/jquery.fancybox.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script src="assets/owlcarousel/owl.carousel.min.js"></script>
    <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">
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
            <img src="icon.ico" alt="WG Logo" width="45px" />
          </a>
          <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasNavbar">
            <div class="offcanvas-header">
              <h5 class="offcanvas-title"><img src="icon.ico" alt="" width="45px"/></h5>
              <button type="button" class="btn-close" data-bs-dismiss="offcanvas"></button>
            </div>
            <div class="offcanvas-body">
              <ul class="navbar-nav flex-grow-1 pe-3">
                <li class="nav-item"><a class="nav-link mx-lg-2" href="index.php#pills-home">Phim Đang Chiếu</a></li>
                <li class="nav-item"><a class="nav-link mx-lg-2" href="index.php#pills-sub">Phim Sắp Chiếu</a></li>
                <li class="nav-item"><a class="nav-link mx-lg-2" href="about.php">Về Chúng Tôi</a></li>
                <li class="nav-item align-self-center">
                  <div class="form-check form-switch ms-3">
                    <input class="form-check-input" type="checkbox" role="switch" checked id="flexSwitchCheckDefault" />
                    <label class="form-check-label" for="flexSwitchCheckDefault">Chế Độ Tối</label>
                  </div>
                </li>
              </ul>
            </div>
          </div>
          <?php if (isset($_SESSION['user_name'])): ?>
            <span class="text-light me-3">Xin chào, <strong><?php echo h($_SESSION['user_name']); ?></strong></span>
            <a href="lib/logout.php" class="login-button left-bt-login">Đăng xuất</a>
          <?php else: ?>
            <a href="auth.php?mode=login" class="login-button left-bt-login">Đăng nhập</a>
            <a href="auth.php?mode=register" class="login-button right-bt-sign">Đăng ký</a>
          <?php endif; ?>
          <button class="navbar-toggler border-0 shadow-none" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasNavbar">
            <span class="navbar-toggler-icon"></span>
          </button>
        </div>
      </nav>
    </header>
