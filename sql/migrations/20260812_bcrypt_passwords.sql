-- Mở rộng cột để lưu BCrypt ($2a$/$2b$/$2y$ dài 60 ký tự).
ALTER TABLE `accounts`
    MODIFY COLUMN `password` VARCHAR(255) NOT NULL;

-- Chặn tạo trùng tài khoản khi nhiều yêu cầu đăng ký đến đồng thời.
SET @co_unique_username = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'accounts'
      AND INDEX_NAME = 'uk_accounts_username'
);
SET @lenh_them_unique_username = IF(
    @co_unique_username = 0,
    'ALTER TABLE `accounts` ADD UNIQUE KEY `uk_accounts_username` (`username`)',
    'SELECT 1'
);
PREPARE them_unique_username FROM @lenh_them_unique_username;
EXECUTE them_unique_username;
DEALLOCATE PREPARE them_unique_username;