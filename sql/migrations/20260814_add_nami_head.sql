START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (286, 0, '[{"dx":-4,"dy":-2,"id":2153},{"dx":-4,"dy":-3,"id":2154},{"dx":-4,"dy":-6,"id":2155},{"dx":-3,"dy":-5,"id":2156}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

INSERT INTO `items` (`id`, `name`, `type`, `gender`, `description`, `level`, `icon`, `part_id`, `strength_required`, `buy_gold`, `buy_gem`, `options`)
VALUES (416, CONVERT(0x54C3B363204E616D69 USING utf8mb4), 0, -1, CONVERT(0x4B69E1BB83752074C3B363204E616D69 USING utf8mb4), 1, 2153, 286, 0, 100000, 0, '[]')
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


ALTER TABLE `avatar_parts` AUTO_INCREMENT = 287;
ALTER TABLE `items` AUTO_INCREMENT = 417;
ALTER TABLE `sprite_images` AUTO_INCREMENT = 2158;
COMMIT;
