-- Migration: Giftcode
CREATE TABLE IF NOT EXISTS `giftcode` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `code` VARCHAR(32) NOT NULL UNIQUE,
  `gold` INT NOT NULL DEFAULT 0,
  `gem` INT NOT NULL DEFAULT 0,
  `item_id` INT NOT NULL DEFAULT 0,
  `item_quantity` INT NOT NULL DEFAULT 1,
  `items_json` TEXT DEFAULT NULL,
  `max_use` INT NOT NULL DEFAULT 1,
  `used_count` INT NOT NULL DEFAULT 0,
  `expires_at` DATETIME DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `giftcode_usage` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `code_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `used_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `idx_code_user` (`code_id`, `user_id`),
  FOREIGN KEY (`code_id`) REFERENCES `giftcode`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `accounts`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `giftcode` (`code`, `gold`, `gem`, `items_json`, `max_use`) VALUES
  ('VIP2026', 100000, 500, NULL, 1000),
  ('TANTHU', 5000000, 5000, '[{"id":338,"quantity":20},{"id":232,"quantity":5},{"id":233,"quantity":5},{"id":255,"quantity":5},{"id":234,"quantity":5}]', 999999)
ON DUPLICATE KEY UPDATE 
  `gold` = VALUES(`gold`), 
  `gem` = VALUES(`gem`), 
  `items_json` = VALUES(`items_json`), 
  `max_use` = VALUES(`max_use`);
