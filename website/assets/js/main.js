/**
 * WGCinema Main Script
 * Handles Theme Toggle and Auth TAB switching.
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Theme Configuration
    initTheme();

    // 2. Handle Auto-tabs
    if (window.location.hash) {
        const targetTab = document.querySelector(`button[data-bs-target="${window.location.hash}"]`);
        if (targetTab) {
            targetTab.click();
        }
    }

    // 3. Initialize Auth Page if on it
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
