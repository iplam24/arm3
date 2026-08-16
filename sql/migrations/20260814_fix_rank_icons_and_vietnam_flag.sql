START TRANSACTION;

UPDATE `caption_levels`
SET `icon` = CASE
    WHEN `id` BETWEEN 0 AND 4 THEN 1300
    WHEN `id` BETWEEN 5 AND 9 THEN 1301
    WHEN `id` BETWEEN 10 AND 14 THEN 1248
    WHEN `id` BETWEEN 15 AND 19 THEN 1249
    WHEN `id` BETWEEN 20 AND 24 THEN 1250
    ELSE `icon`
END
WHERE `id` BETWEEN 0 AND 24;

INSERT INTO `sprite_images` (`id`, `image_id`, `x`, `y`, `width`, `height`)
VALUES (1694, 4, 256, 0, 24, 16)
ON DUPLICATE KEY UPDATE
    `image_id` = VALUES(`image_id`),
    `x` = VALUES(`x`),
    `y` = VALUES(`y`),
    `width` = VALUES(`width`),
    `height` = VALUES(`height`);

UPDATE `clans`
SET `icon_id` = 1694
WHERE `icon_id` = 2128;

ALTER TABLE `sprite_images` AUTO_INCREMENT = 2130;

COMMIT;
