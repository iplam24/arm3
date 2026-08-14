START TRANSACTION;

UPDATE `caption_levels`
SET `icon` = CASE `icon`
    WHEN 1300 THEN 1248
    WHEN 1301 THEN 1249
    WHEN 1302 THEN 1250
    WHEN 1303 THEN 1251
    WHEN 1304 THEN 1252
    ELSE `icon`
END
WHERE `id` BETWEEN 0 AND 24;

INSERT INTO `sprite_images` (`id`, `image_id`, `x`, `y`, `width`, `height`)
VALUES (2129, 4, 256, 0, 24, 16)
ON DUPLICATE KEY UPDATE
    `image_id` = VALUES(`image_id`),
    `x` = VALUES(`x`),
    `y` = VALUES(`y`),
    `width` = VALUES(`width`),
    `height` = VALUES(`height`);

UPDATE `clans`
SET `icon_id` = 2129
WHERE `icon_id` = 2128;

ALTER TABLE `sprite_images` AUTO_INCREMENT = 2130;

COMMIT;
