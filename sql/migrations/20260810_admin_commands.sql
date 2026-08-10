-- Quyền quản trị và nhật ký lệnh chat admin.
SET @co_cot_is_admin = (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'accounts'
      AND COLUMN_NAME = 'is_admin'
);
SET @lenh_them_is_admin = IF(
    @co_cot_is_admin = 0,
    'ALTER TABLE accounts ADD COLUMN is_admin TINYINT(1) NOT NULL DEFAULT 0 AFTER is_online',
    'SELECT 1'
);
PREPARE them_is_admin FROM @lenh_them_is_admin;
EXECUTE them_is_admin;
DEALLOCATE PREPARE them_is_admin;

CREATE TABLE IF NOT EXISTS admin_audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT,
    admin_player_id INT NOT NULL,
    command_text VARCHAR(500) NOT NULL,
    success TINYINT(1) NOT NULL DEFAULT 1,
    result_text VARCHAR(1000) NOT NULL DEFAULT '',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_admin_audit_player_time (admin_player_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
