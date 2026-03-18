document.addEventListener('DOMContentLoaded', (event) => {
    const htmlElement = document.documentElement;
    const switchElement = document.getElementById('flexSwitchCheckDefault');
    const switchLabel = document.querySelector('.form-check-label[for="flexSwitchCheckDefault"]');

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
});