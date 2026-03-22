/**
 * WGCinema Main Script
 * Handles Theme Toggle, Movie Rendering, and Auth interactions.
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Theme Configuration
    initTheme();

    // 2. Fetch and Render Movies
    fetchMoviesAndRender();

    // 3. Handle Auto-tabs
    if (window.location.hash) {
        const targetTab = document.querySelector(`button[data-bs-target="${window.location.hash}"]`);
        if (targetTab) {
            targetTab.click();
        }
    }

    // 4. Initialize Auth Page if on it
    initAuthPage();
});

// ========================
// Theme Toggle Logic
// ========================
function initTheme() {
    const htmlElement = document.documentElement;
    const switchElement = document.getElementById('flexSwitchCheckDefault');
    const switchLabel = document.querySelector('.form-check-label[for="flexSwitchCheckDefault"]');

    if (switchElement && switchLabel) {
        const currentTheme = localStorage.getItem('bsTheme') || 'dark';
        htmlElement.setAttribute('data-bs-theme', currentTheme);
        switchElement.checked = currentTheme === 'dark';
        switchLabel.textContent = currentTheme === 'dark' ? 'Chế Độ Tối' : 'Chế Độ Sáng';

        switchElement.addEventListener('change', function () {
            if (this.checked) {
                htmlElement.setAttribute('data-bs-theme', 'dark');
                localStorage.setItem('bsTheme', 'dark');
                switchLabel.textContent = 'Chế Độ Tối';
            } else {
                htmlElement.setAttribute('data-bs-theme', 'light');
                localStorage.setItem('bsTheme', 'light');
                switchLabel.textContent = 'Chế Độ Sáng';
            }
        });
    }
}

// ========================
// Movie Rendering Logic
// ========================
function fetchMoviesAndRender() {
    const showingContainer = document.getElementById('showing-movies-container');
    const upcomingContainer = document.getElementById('upcoming-movies-container');

    if (!showingContainer && !upcomingContainer) return;

    fetch('scripts/movies.json')
        .then(response => response.json())
        .then(movies => {
            if (showingContainer) {
                 const showingMovies = movies.filter(m => m.category === 'MoviesShowing');
                 showingContainer.innerHTML = showingMovies.map(movieCardTemplate).join('');
            }
            if (upcomingContainer) {
                 const upcomingMovies = movies.filter(m => m.category === 'MoviesUpcoming');
                 upcomingContainer.innerHTML = upcomingMovies.map(movieCardTemplate).join('');
            }
        })
        .catch(error => console.error('Error fetching movies:', error));
}

const movieCardTemplate = (movie) => `
  <div class="col-lg-3 col-md-4 col-sm-6 mb-4">
    <div class="card h-100 border-0 text-center">
      <a href="movieinfo.html" onClick="storeMovieInfo(${movie.id})">
        <img src="images/movies/${movie.imageFileName}" class="card-img-top w-100" alt="${movie.movieTitle}" />
      </a>
      <div class="btn-book">
         <a href="#qr-code-${movie.id}" class="btn btn-gradient" data-fancybox><i class="fas fa-ticket-alt me-2"></i> Đặt Vé</a>
      </div>
      <div class="card-body">
        <p class="card-text">${movie.movieTitle}</p>
      </div>
    </div>
    
    <!-- Fancybox QR Modal -->
    <div style="display: none;" id="qr-code-${movie.id}" class="fancybox-content">
      <h3 class="text-center mb-4 gradient-text">Số tiền cần thanh toán: 70.000 VNĐ</h3>
      <div class="text-center mb-4">
        <img width="300px" class="rounded shadow" src="images/QR-code.jpg" alt="QR Code" onerror="this.src='images/seat.jpg'"/>
      </div>
      <div class="d-flex justify-content-center gap-3">
            <button class="btn btn-gradient px-4" onclick="confirmBooking()">Xác nhận</button>
            <button class="btn btn-outline-danger px-4" style="border-radius: 50px;" onclick="$.fancybox.close()">Hủy</button>
      </div>
    </div>
  </div>
`;

window.storeMovieInfo = function(id) {
    localStorage.setItem('clickedMovieId', id);
    // Backward compatibility with legacy code
    localStorage.setItem('clickedMovieLineNumber', id);
};

window.confirmBooking = function() {
    if (typeof Swal !== 'undefined') {
      Swal.fire({
          icon: 'success',
          title: 'Đã đặt vé thành công',
          text: 'Vui lòng kiểm tra email để lấy vé.',
          showConfirmButton: false,
          timer: 2000,
          background: 'var(--glass-bg)',
          color: 'var(--text-main)'
      });
    } else {
      alert('Đã đặt vé thành công!');
    }
    if (typeof $ !== 'undefined' && $.fancybox) {
        $.fancybox.close();
    }
};

if (typeof $ !== 'undefined') {
    $(document).ready(function() {
        if ($.fn.fancybox) {
            $('[data-fancybox]').fancybox({
                buttons: ["close"],
                animationEffect: "zoom-in-out"
            });
        }
    });
}

// ========================
// Auth Page Interaction
// ========================
function initAuthPage() {
    if (!document.querySelector('.auth-page')) return;
    
    const params = new URLSearchParams(window.location.search);
    const mode = params.get('mode') || 'login';
    switchAuthTab(mode);
}

window.switchAuthTab = function(mode) {
    // Update Tabs
    document.getElementById('tab-login')?.classList.remove('active');
    document.getElementById('tab-register')?.classList.remove('active');
    document.getElementById(`tab-${mode}`)?.classList.add('active');

    // Update Forms
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');
    
    if (loginForm && registerForm) {
        if (mode === 'login') {
            registerForm.style.display = 'none';
            loginForm.style.display = 'block';
            loginForm.classList.add('active');
        } else {
            loginForm.style.display = 'none';
            registerForm.style.display = 'block';
            registerForm.classList.add('active');
        }
    }
}

window.handleLogin = function(event) {
    event.preventDefault();
    const user = document.getElementById('login-username').value;
    const pass = document.getElementById('login-password').value;
    if(user && pass) {
        Swal.fire({
            title: "Đăng nhập thành công",
            text: "Chào mừng bạn trở lại!",
            icon: "success",
            showConfirmButton: false,
            timer: 1500
        }).then(() => {
            window.location.href = 'index.html';
        });
    }
};

window.handleRegister = function(event) {
    event.preventDefault();
    const user = document.getElementById('reg-username').value;
    const pass = document.getElementById('reg-password').value;
    const conf = document.getElementById('reg-confirm-password').value;
    
    if (pass !== conf) {
        Swal.fire({
            title: "Lỗi",
            text: "Mật khẩu xác nhận không khớp!",
            icon: "error"
        });
        return;
    }
    
    Swal.fire({
        title: "Đăng ký thành công",
        text: "Tài khoản của bạn đã được tạo.",
        icon: "success",
        showConfirmButton: false,
        timer: 1500
    }).then(() => {
        window.location.href = 'index.html';
    });
};
