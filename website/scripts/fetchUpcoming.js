fetch('Movies.txt')
  .then(response => response.text())
  .then(data => {
    const movieData = data.split('\n').map((line, index) => {
      const [info] = line.trim().split(' - ');
      const [category, imageFileName] = info.split('\\');
      if (category === 'MoviesUpcoming') {
        const movieTitle = imageFileName.replace(/.jpg/g, '');
        return { category, movieTitle, imageFileName, lineNumber: index + 1 };
      }
    }).filter(Boolean);

    const movieCardTemplate = (movie) => `
      <div class="col-lg-3 col-sm-6">
        <div class="card border-0" style="width: 15; margin: auto;">
          <a href="MovieInfo.html" onClick="storeMovieInfo(${movie.lineNumber})">
            <img src="${movie.category}/${movie.imageFileName}" class="card-img-top" />
          </a>
          <div class="card-body">
            <p class="card-text text-center">${movie.movieTitle}</p>
          </div>
        </div>
        <div class="d-flex justify-content-center align-items-center">
          <a href="#qr-code-${movie.lineNumber}" class="btn btn-primary mb-3" data-fancybox>Đặt Vé</a>
        </div>
      </div>
      <div style="display: none;" id="qr-code-${movie.lineNumber}">
        <h3 style="text-align: center; color: #000;">Số tiền cần thanh toán: 70.000 VNĐ</h3>
        <img width="600px" style="max-width: 100%; height: auto;" src="img/QR-code.jpg" alt="QR Code" />
        <div class="d-flex justify-content-center mt-3">
              <button class="btn btn-success mx-2" onclick="confirmBooking()">Xác nhận</button>
              <button class="btn btn-danger mx-2" onclick="$.fancybox.close()">Hủy</button>
        </div>
      </div>
    `;

    const container = document.getElementById('upcoming-movies-container');
    container.innerHTML = movieData.map(movieCardTemplate).join('');
  })
  .catch(error => console.error('Error:', error));

function storeMovieInfo(lineNumber) {
  localStorage.setItem('clickedMovieLineNumber', lineNumber);
}
$(document).ready(function() {
  $('[data-fancybox]').fancybox({
    buttons: [
      "zoom",
      "close"
    ]
  });
});
function confirmBooking() {
  Swal.fire({
    icon: 'success',
    title: 'Đã đặt vé thành công',
    showConfirmButton: false,
    timer: 2000
  });
  $.fancybox.close();
}