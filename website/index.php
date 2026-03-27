<?php
require_once 'lib/config.php';
require_once 'lib/functions.php';

$pageTitle = "WG Cinema - Home";
include 'ui/header.php';

$showingMovies = getAllMovies($pdo, 'showing');
$upcomingMovies = getAllMovies($pdo, 'upcoming');
$carouselSlides = getCarouselSlides($pdo);
?>

<div class="content container">
  <!-- Carousel Section -->
  <div class="content1 glass-panel p-3 mb-5 mt-5">
    <div id="owl-carousel1" class="owl-carousel owl-theme">
      <?php if (!empty($carouselSlides)): ?>
        <?php foreach ($carouselSlides as $slide): ?>
          <div class="item">
            <img 
              src="lib/image_display.php?type=carousel&id=<?= $slide['id']; ?>" 
              class="d-block w-100 rounded-4" 
              alt="<?= h($slide['title'] ?? ''); ?>" 
            />
          </div>
        <?php endforeach; ?>
      <?php else: ?>
        <div class="item">
          <img src="assets/images/background.png" class="d-block w-100 rounded-4" alt="Cinema" />
        </div>
      <?php endif; ?>
    </div>
  </div>

  <!-- Movie List Section -->
  <div class="content2 my-5">
    <div class="text-center mb-5">
      <h2 class="gradient-text text-uppercase fw-bold" style="font-size: 2.5rem;">Movie List</h2>
      <div class="d-flex justify-content-center">
        <ul class="nav nav-pills custom-pills mb-3" id="pills-tab" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link active px-4 py-2" id="pills-home-tab" data-bs-toggle="pill" data-bs-target="#pills-home" type="button" role="tab">
              Now Showing
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link px-4 py-2" id="pills-sub-tab" data-bs-toggle="pill" data-bs-target="#pills-sub" type="button" role="tab">
              Coming Soon
            </button>
          </li>
        </ul>
      </div>
    </div>

    <div class="tab-content" id="pills-tabContent">
      <!-- NOW SHOWING -->
      <div class="tab-pane fade show active" id="pills-home" role="tabpanel">
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4" id="showing-movies-container">
          <?php foreach ($showingMovies as $movie): ?>
            <div class="col" data-aos="fade-up">
              <div class="movie-card py-3">
                <div class="position-relative overflow-hidden rounded-4 shadow-lg mb-3">
                  <img 
                    src="lib/image_display.php?type=movie&id=<?= $movie['id']; ?>" 
                    class="img-fluid w-100 object-fit-cover" 
                    style="height: 400px;" 
                    alt="<?= h($movie['title']); ?>"
                  >
                  <div class="movie-overlay d-flex align-items-center justify-content-center">
                    <a href="movieinfo.php?id=<?= $movie['id']; ?>" class="btn btn-gradient rounded-pill px-4 py-2">
                      View Details
                    </a>
                  </div>
                </div>
                <h5 class="text-white fw-bold mb-1 px-2"><?= h($movie['title']); ?></h5>
                <p class="text-muted-custom small px-2 mb-0">
                  <?= h($movie['duration']); ?> mins | <?= h($movie['age_rating'] ?? 'N/A'); ?>
                </p>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      </div>

      <!-- UPCOMING -->
      <div class="tab-pane fade" id="pills-sub" role="tabpanel">
        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4" id="upcoming-movies-container">
          <?php foreach ($upcomingMovies as $movie): ?>
            <div class="col" data-aos="fade-up">
              <div class="movie-card py-3">
                <div class="position-relative overflow-hidden rounded-4 shadow-lg mb-3">
                  <img 
                    src="lib/image_display.php?type=movie&id=<?= $movie['id']; ?>" 
                    class="img-fluid w-100 object-fit-cover" 
                    style="height: 400px;" 
                    alt="<?= h($movie['title']); ?>"
                  >
                  <div class="movie-overlay d-flex align-items-center justify-content-center">
                    <a href="movieinfo.php?id=<?= $movie['id']; ?>" class="btn btn-gradient rounded-pill px-4 py-2">
                      Xem Chi Tiết
                    </a>
                  </div>
                </div>
                <h5 class="text-white fw-bold mb-1 px-2"><?= h($movie['title']); ?></h5>
                <p class="text-muted-custom small px-2 mb-0">
                  <?= h($movie['duration']); ?> phút | <?= h($movie['age_rating'] ?? 'N/A'); ?>
                </p>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
  $(document).ready(function () {
    $("#owl-carousel1").owlCarousel({
      loop: true,
      margin: 20,
      stagePadding: 100,
      nav: false,
      dots: true,
      autoplay: true,
      autoplayTimeout: 5000,
      responsive: {
        0: { items: 1, stagePadding: 0 },
        1000: { items: 1, stagePadding: 150 }
      }
    });
  });
</script>

<?php include 'ui/footer.php'; ?>