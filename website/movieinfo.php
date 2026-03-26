<?php
require_once 'lib/config.php';
require_once 'lib/functions.php';

$movieId = isset($_GET['id']) ? (int)$_GET['id'] : 1;
$movie = getMovieById($pdo, $movieId);

if (!$movie) {
  header("Location: index.php");
  exit;
}

$pageTitle = "WG Cinema - " . $movie['title'];
include 'ui/header.php';

// Prepare trailer URL
$trailerUrl = $movie['trailer'];
if (strpos($trailerUrl, 'watch?v=') !== false) {
  $trailerUrl = str_replace('watch?v=', 'embed/', $trailerUrl);
}
if (strpos($trailerUrl, 'embed/') !== false && strpos($trailerUrl, 'autoplay=1') === false) {
  $trailerUrl .= (strpos($trailerUrl, '?') === false ? '?' : '&') . 'autoplay=1&mute=1';
}

$showtimes = getShowtimesByMovieId($pdo, $movieId);
$defaultPrice = !empty($showtimes) ? $showtimes[0]['regular_price'] : 80000;
?>

    <!-- Movie Content Sections -->
    <div class="container-fluid px-0" style="margin-top: 80px;">
      <!-- Hero Trailer -->
      <div class="position-relative w-100 bg-black overflow-hidden shadow-lg" style="height: 60vh;">
        <iframe id="youtube-iframe" class="w-100 h-100 border-0" style="pointer-events: auto; transform: scale(1.05);" src="<?php echo h($trailerUrl); ?>" allow="autoplay; encrypted-media" allowfullscreen></iframe>
        <div class="position-absolute bottom-0 w-100" style="height: 15vh; background: linear-gradient(to top, var(--bg-dark), transparent); pointer-events:none;"></div>
      </div>
      
      <!-- Content Panel -->
      <div class="container position-relative" style="margin-top: -80px; z-index: 10;">
        <div class="glass-panel p-4 p-lg-5">
          <div class="row g-5">
            <!-- Poster -->
            <div class="col-lg-3 col-md-4 text-center">
              <img id="movie-image" src="lib/image_display.php?type=movie&id=<?php echo $movie['id']; ?>" class="img-fluid rounded-4 shadow-lg mb-4 w-100 object-fit-cover" style="max-height: 450px;" alt="Movie Poster" />
              <button class="btn btn-gradient w-100 py-3 fw-bold fs-5 text-uppercase shadow-lg d-none d-md-block" onclick="openQRCodeModal()"><i class="fas fa-ticket-alt me-2"></i> Đặt Vé Ngay</button>
            </div>

            <!-- Details -->
            <div class="col-lg-9 col-md-8">
              <h1 class="movie-title fw-bold mb-3 display-4 text-white" style="letter-spacing: -1px;"><?php echo h($movie['title']); ?></h1>
              <div class="d-flex flex-wrap gap-3 mb-4 text-white align-items-center">
                 <span class="badge bg-primary text-white py-2 px-3 fw-bold shadow-sm" style="font-size: 0.9rem;"><i class="fas fa-calendar-alt me-2"></i> <span><?php echo h($movie['release_date']); ?></span></span>
                 <span class="badge bg-dark border border-secondary text-white py-2 px-3 fw-bold shadow-sm" style="font-size: 0.9rem;">Rating: <span><?php echo h($movie['age_rating'] ?? 'N/A'); ?></span></span>
                 <span class="badge bg-info text-dark py-2 px-3 fw-bold shadow-sm" style="font-size: 0.9rem;"><i class="fas fa-clock me-2"></i> <?php echo h($movie['duration']); ?> phút</span>
              </div>
              
              <h4 class="fw-bold gradient-text mt-5 mb-3">Nội Dung Phim</h4>
              <p id="description" class="text-muted-custom fs-5 lh-lg mb-5" style="text-align: justify;"><?php echo nl2br(h($movie['description'])); ?></p>
              
              <hr class="border-secondary opacity-50 mb-5">
              
              <div class="row mb-5 text-white">
                <div class="col-md-6">
                    <h5 class="fw-bold text-light mb-2">Đạo diễn</h5>
                    <p class="text-muted-custom"><?php echo h($movie['director']); ?></p>
                </div>
              </div>

              <div class="showtimes-container bg-dark bg-opacity-25 p-4 rounded-4 border border-secondary border-opacity-25">
                <?php if (!empty($showtimes)): ?>
                    <div class="row g-3">
                        <?php foreach ($showtimes as $st): ?>
                            <div class="col-6 col-md-4 col-lg-3">
                                <div class="showtime-card p-3 rounded-3 border border-secondary text-center cursor-pointer hover-effect" onclick="selectShowtime(<?php echo $st['showtime_id']; ?>, <?php echo $st['regular_price']; ?>)">
                                    <div class="fw-bold text-white"><?php echo date('H:i', strtotime($st['time'])); ?></div>
                                    <div class="small text-muted-custom"><?php echo date('d/m', strtotime($st['date'])); ?></div>
                                    <div class="extra-small text-info"><?php echo h($st['showroom_name']); ?></div>
                                </div>
                            </div>
                        <?php
  endforeach; ?>
                    </div>
                <?php
else: ?>
                    <p class="text-muted-custom">Hiện chưa có suất chiếu cho phim này.</p>
                <?php
endif; ?>
              </div>
              
              <button class="btn btn-gradient w-100 py-3 mt-4 fw-bold fs-5 text-uppercase shadow-lg d-block d-md-none" onclick="openQRCodeModal()"><i class="fas fa-ticket-alt me-2"></i> Đặt Vé Ngay</button>
            </div>
          </div>
        </div>
      </div>

      <div style="display: none;" id="qr-code-modal" class="fancybox-content text-center">
        <h3 class="fw-bold gradient-text mb-4">Thanh Toán Trực Tuyến</h3>
        <p class="text-white mb-4">Số tiền: <strong class="fs-4" id="modal-price"><?php echo number_format($defaultPrice, 0, ',', '.'); ?> VNĐ</strong></p>
        <div class="bg-white p-3 d-inline-block rounded-4 shadow mb-4">
          <img width="300px" src="assets/images/QR-code.jpg" onerror="this.src='assets/images/background.png'" alt="QR Code" class="img-fluid" />
        </div>
        <div class="d-flex justify-content-center gap-3">
          <button class="btn btn-gradient px-5 py-2 fw-bold" onclick="confirmBooking()">Xác Nhận Đã Chuyển Khoản</button>
        </div>
      </div>
    </div>

    <script>
      $(document).ready(function () {
        if ($.fn.fancybox) {
          $('[data-fancybox]').fancybox({ buttons: ["close"], animationEffect: "zoom-in-out" });
        }
      });
      window.openQRCodeModal = function() {
        $.fancybox.open({ src: '#qr-code-modal', type: 'inline', animationEffect: "zoom-in-out" });
      };
      var selectedShowtimeId = null;
      window.selectShowtime = function(id, price) {
          selectedShowtimeId = id;
          $('.showtime-card').removeClass('border-primary shadow-glow').addClass('border-secondary');
          $(event.currentTarget).removeClass('border-secondary').addClass('border-primary shadow-glow');
          $('#modal-price').text(new Intl.NumberFormat('vi-VN').format(price) + ' VNĐ');
          openQRCodeModal();
      };
      window.confirmBooking = function() {
          if (!selectedShowtimeId) {
              Swal.fire('Thông báo', 'Vui lòng chọn suất chiếu trước!', 'warning');
              return;
          }
          Swal.fire({
              title: 'Thành công!',
              text: 'Yêu cầu đặt vé của bạn đã được gửi. Vui lòng kiểm tra email sau khi chúng tôi xác nhận thanh toán.',
              icon: 'success',
              confirmButtonText: 'Đóng'
          });
      };
    </script>

<?php include 'ui/footer.php'; ?>
