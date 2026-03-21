document.addEventListener('DOMContentLoaded', (event) => {
    // 1. Theme configuration
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

    // 2. Fetch logic for dynamic movie loading
    fetchMoviesAndRender();

    if (window.location.hash) {
        const targetTab = document.querySelector(`button[data-bs-target="${window.location.hash}"]`);
        if (targetTab) {
            targetTab.click();
        }
    }
});

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
  <div class="col-lg-3 col-sm-6">
    <div class="card border-0" style="width: 15; margin: auto;">
      <a href="movieinfo.html" onClick="storeMovieInfo(${movie.id})">
        <img src="images/movies/${movie.imageFileName}" class="card-img-top" />
      </a>
      <div class="card-body">
        <p class="card-text text-center">${movie.movieTitle}</p>
      </div>
    </div>
    <div class="d-flex justify-content-center align-items-center">
      <a href="#qr-code-${movie.id}" class="btn btn-primary mb-3" data-fancybox>Đặt Vé</a>
    </div>
  </div>
  <div style="display: none;" id="qr-code-${movie.id}">
    <h3 style="text-align: center; color: #000;">Số tiền cần thanh toán: 70.000 VNĐ</h3>
    <img width="600px" style="max-width: 100%; height: auto;" src="images/QR-code.jpg" alt="QR Code" onerror="this.src='images/seat.jpg'"/>
    <div class="d-flex justify-content-center mt-3">
          <button class="btn btn-success mx-2" onclick="confirmBooking()">Xác nhận</button>
          <button class="btn btn-danger mx-2" onclick="$.fancybox.close()">Hủy</button>
    </div>
  </div>
`;

function storeMovieInfo(id) {
    localStorage.setItem('clickedMovieId', id);
}

$(document).ready(function() {
    if ($.fn.fancybox) {
        $('[data-fancybox]').fancybox({
            buttons: ["zoom", "close"]
        });
    }
});

function confirmBooking() {
    if (typeof Swal !== 'undefined') {
      Swal.fire({
          icon: 'success',
          title: 'Đã đặt vé thành công',
          showConfirmButton: false,
          timer: 2000
      });
    } else {
      alert('Đã đặt vé thành công!');
    }
    if ($.fancybox) {
        $.fancybox.close();
    }
}

// 3. Validation Logic
window.InvalidEmail = function(textbox) {
    if (textbox.value === '') { 
        textbox.setCustomValidity('Bạn phải nhập địa chỉ email!'); 
    } else if (textbox.validity.typeMismatch) { 
        textbox.setCustomValidity('Địa chỉ email không đúng định dạng!'); 
    } else { 
        textbox.setCustomValidity(''); 
    } 
    return true; 
}

window.InvalidUser = function(textbox) {
    if (textbox.value === '') { 
        textbox.setCustomValidity('Bạn phải nhập tên đăng nhập');
    } else {
        textbox.setCustomValidity(''); 
    } 
    return true; 
}

window.InvalidPass = function(textbox) {
    if (textbox.value === '') { 
        textbox.setCustomValidity('Bạn phải nhập mật khẩu');
    } else {
        textbox.setCustomValidity(''); 
    } 
    return true;
}

window.InvalidConfirmPass = function() {
    var password = document.getElementById("password").value;
    var confirmPassword = document.getElementById("confirm_password").value;
    var confirmPasswordField = document.getElementById("confirm_password");

    if (!confirmPasswordField) return true;

    if (confirmPassword === '') {
        confirmPasswordField.setCustomValidity('Bạn phải xác nhận mật khẩu');
    } else if (confirmPassword !== password) {
        confirmPasswordField.setCustomValidity('Mật khẩu xác nhận không khớp');
    } else {
        confirmPasswordField.setCustomValidity('');
    }
    return true;
}
