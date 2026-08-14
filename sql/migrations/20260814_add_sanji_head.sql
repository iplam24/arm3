START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (288, 0, '[{"dx":-4,"dy":-3,"id":2163},{"dx":-4,"dy":-4,"id":2164},{"dx":-4,"dy":-7,"id":2165},{"dx":-3,"dy":-6,"id":2166}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

INSERT INTO `items` (`id`, `name`, `type`, `gender`, `description`, `level`, `icon`, `part_id`, `strength_required`, `buy_gold`, `buy_gem`, `options`)
VALUES (418, CONVERT(0x54C3B3632053616E6A69 USING utf8mb4), 0, -1, CONVERT(0x4B69E1BB83752074C3B3632053616E6A69 USING utf8mb4), 1, 2163, 288, 0, 100000, 0, '[]')
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `type` = VALUES(`type`),
    `gender` = VALUES(`gender`),
    `description` = VALUES(`description`),
    `level` = VALUES(`level`),
    `icon` = VALUES(`icon`),
    `part_id` = VALUES(`part_id`),
    `strength_required` = VALUES(`strength_required`),
    `buy_gold` = VALUES(`buy_gold`),
    `buy_gem` = VALUES(`buy_gem`),
    `options` = VALUES(`options`);


ALTER TABLE `avatar_parts` AUTO_INCREMENT = 289;
ALTER TABLE `items` AUTO_INCREMENT = 419;
ALTER TABLE `sprite_images` AUTO_INCREMENT = 2168;
COMMIT;
