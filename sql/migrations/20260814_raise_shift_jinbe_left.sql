START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (293, 0, '[{"dx":-2,"dy":0,"id":2245},{"dx":-2,"dy":0,"id":2246},{"dx":-2,"dy":-1,"id":2247},{"dx":-1,"dy":1,"id":2248}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

UPDATE `items` SET `icon` = 2245 WHERE `id` = 423;

ALTER TABLE `sprite_images` AUTO_INCREMENT = 2249;

COMMIT;