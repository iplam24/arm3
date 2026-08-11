-- Đảm bảo tài khoản admin có đầy đủ quyền quản trị.
UPDATE accounts
SET is_admin = 1
WHERE username = 'admin';
