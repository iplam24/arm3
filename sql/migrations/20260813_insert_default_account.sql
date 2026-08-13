-- Đổi giá trị này thành tên tài khoản cần tạo trước khi chạy file.
SET @ten_tai_khoan = 'mobiarmy3';

-- Mật khẩu mặc định: mobiarmy3
-- BCrypt strength 10, tương thích VXLMaHoaMatKhau.
SET @mat_khau_bcrypt = '$2a$10$I0qiGYQcRumWSMwJpBIwvOpW7BBvhKJrYVMIz7S/4MKAmYj0N2j2K';

INSERT INTO `accounts` (`username`, `password`, `is_banned`, `is_online`, `is_admin`)
VALUES (@ten_tai_khoan, @mat_khau_bcrypt, 0, 0, 0)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `is_banned` = 0,
    `is_online` = 0;

SELECT `id`, `username`, `is_banned`, `is_online`, `is_admin`
FROM `accounts`
WHERE `username` = @ten_tai_khoan
LIMIT 1;
