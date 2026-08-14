START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`) VALUES
(285, 0, '[{"dx":-1,"dy":0,"id":2229},{"dx":-1,"dy":0,"id":2230},{"dx":-1,"dy":-2,"id":2231},{"dx":0,"dy":1,"id":2232}]'),
(288, 0, '[{"dx":-1,"dy":-1,"id":2233},{"dx":-1,"dy":-2,"id":2234},{"dx":-1,"dy":-3,"id":2235},{"dx":0,"dy":-1,"id":2236}]'),
(291, 0, '[{"dx":0,"dy":-2,"id":2237},{"dx":0,"dy":-4,"id":2238},{"dx":0,"dy":-6,"id":2239},{"dx":1,"dy":-3,"id":2240}]'),
(292, 0, '[{"dx":0,"dy":-1,"id":2241},{"dx":0,"dy":-1,"id":2242},{"dx":0,"dy":-1,"id":2243},{"dx":1,"dy":2,"id":2244}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

UPDATE `items` SET `icon` = 2229 WHERE `id` = 415;
UPDATE `items` SET `icon` = 2233 WHERE `id` = 418;
UPDATE `items` SET `icon` = 2237 WHERE `id` = 421;
UPDATE `items` SET `icon` = 2241 WHERE `id` = 422;

ALTER TABLE `avatar_parts` AUTO_INCREMENT = 294;
ALTER TABLE `items` AUTO_INCREMENT = 424;
ALTER TABLE `sprite_images` AUTO_INCREMENT = 2245;

COMMIT;