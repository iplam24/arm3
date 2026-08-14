START TRANSACTION;

INSERT INTO `sprite_images` (`id`, `image_id`, `x`, `y`, `width`, `height`)
VALUES (2128, 4, 256, 0, 24, 16)
ON DUPLICATE KEY UPDATE
    `image_id` = VALUES(`image_id`),
    `x` = VALUES(`x`),
    `y` = VALUES(`y`),
    `width` = VALUES(`width`),
    `height` = VALUES(`height`);

UPDATE `clans`
SET `icon_id` = 1616 - `icon_id`
WHERE `icon_id` BETWEEN -24 AND -1;

ALTER TABLE `sprite_images` AUTO_INCREMENT = 2129;

COMMIT;