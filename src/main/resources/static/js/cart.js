// Hàm thêm vào giỏ hàng bằng AJAX
function themVaoGioHang(sanPhamId, soLuong = 1) {
    // Tạo FormData
    const formData = new FormData();
    formData.append('sanPhamId', sanPhamId);
    formData.append('soLuong', soLuong);

    // Gửi AJAX request
    fetch('/client/gio-hang/them', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            // Hiển thị thông báo thành công
            hienThiThongBao(data.message, 'success');

            // Cập nhật số lượng giỏ hàng trên header
            capNhatSoLuongGioHang(data.soLuongGioHang);
        } else {
            // Hiển thị thông báo lỗi
            hienThiThongBao(data.message, 'error');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        hienThiThongBao('Có lỗi xảy ra, vui lòng thử lại!', 'error');
    });
}

// Hàm hiển thị thông báo
function hienThiThongBao(message, type) {
    // Xóa thông báo cũ nếu có
    const oldNotification = document.querySelector('.notification-toast');
    if (oldNotification) {
        oldNotification.remove();
    }

    // Tạo thông báo mới
    const notification = document.createElement('div');
    notification.className = `notification-toast ${type}`;
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;

    // Thêm vào body
    document.body.appendChild(notification);

    // Hiện animation
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);

    // Tự động ẩn sau 3 giây
    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => {
            notification.remove();
        }, 300);
    }, 3000);
}

// Hàm cập nhật số lượng giỏ hàng
function capNhatSoLuongGioHang(soLuong) {
    const cartCounts = document.querySelectorAll('.cart-count');
    cartCounts.forEach(count => {
        count.textContent = soLuong;
    });
}

// Xử lý form thêm vào giỏ hàng
document.addEventListener('DOMContentLoaded', function() {
    // Lắng nghe sự kiện submit của tất cả form thêm giỏ hàng
    document.addEventListener('submit', function(e) {
        const form = e.target;

        // Kiểm tra xem có phải form thêm giỏ hàng không
        if (form.action.includes('/client/gio-hang/them')) {
            e.preventDefault(); // Ngăn form submit bình thường

            // Lấy dữ liệu từ form
            const sanPhamId = form.querySelector('input[name="sanPhamId"]').value;
            const soLuong = form.querySelector('input[name="soLuong"]').value;

            // Gọi hàm thêm vào giỏ hàng
            themVaoGioHang(sanPhamId, soLuong);
        }
    });
});