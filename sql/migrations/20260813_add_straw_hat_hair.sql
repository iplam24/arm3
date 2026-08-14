START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (
    284,
    0,
    '[{"dx":-4,"dy":-3,"id":2143},{"dx":-4,"dy":-4,"id":2144},{"dx":-4,"dy":-7,"id":2145},{"dx":-3,"dy":-6,"id":2146}]'
)
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

INSERT INTO `items` (
    `id`, `name`, `type`, `gender`, `description`, `level`, `icon`, `part_id`,
    `strength_required`, `buy_gold`, `buy_gem`, `options`
)
VALUES (
    414, CONVERT(0x54C3B363204DC5A92052C6A16D USING utf8mb4), 0, -1, CONVERT(0x4B69E1BB83752074C3B363206DC5A92072C6A16D207468E1BB9D69207472616E67 USING utf8mb4), 1, 2143, 284,
    0, 100000, 0, '[]'
)
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

COMMIT;
