START TRANSACTION;

-- Shop icons reuse the first aiming frame; each head references only four sprite IDs.
UPDATE `items` SET `icon` = 2143 WHERE `id` = 414;

-- Zoro: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (285, 0, '[{"dx":-4,"dy":-3,"id":2193},{"dx":-4,"dy":-3,"id":2194},{"dx":-4,"dy":-5,"id":2195},{"dx":-3,"dy":-2,"id":2196}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2193 WHERE `id` = 415;

-- Nami: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (286, 0, '[{"dx":-4,"dy":-3,"id":2197},{"dx":-4,"dy":-4,"id":2198},{"dx":-4,"dy":-6,"id":2199},{"dx":-3,"dy":-3,"id":2200}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2197 WHERE `id` = 416;

-- Usopp: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (287, 0, '[{"dx":-4,"dy":-2,"id":2201},{"dx":-4,"dy":-3,"id":2202},{"dx":-4,"dy":-5,"id":2203},{"dx":-3,"dy":-2,"id":2204}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2201 WHERE `id` = 417;

-- Sanji: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (288, 0, '[{"dx":-4,"dy":-1,"id":2205},{"dx":-4,"dy":-2,"id":2206},{"dx":-4,"dy":-3,"id":2207},{"dx":-3,"dy":-1,"id":2208}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2205 WHERE `id` = 418;

-- Chopper: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (289, 0, '[{"dx":-4,"dy":-1,"id":2209},{"dx":-4,"dy":-2,"id":2210},{"dx":-4,"dy":-3,"id":2211},{"dx":-3,"dy":0,"id":2212}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2209 WHERE `id` = 419;

-- Robin: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (290, 0, '[{"dx":-3,"dy":-1,"id":2213},{"dx":-3,"dy":-2,"id":2214},{"dx":-3,"dy":-4,"id":2215},{"dx":-2,"dy":-2,"id":2216}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2213 WHERE `id` = 420;

-- Franky: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (291, 0, '[{"dx":-3,"dy":-2,"id":2217},{"dx":-3,"dy":-4,"id":2218},{"dx":-3,"dy":-6,"id":2219},{"dx":-2,"dy":-3,"id":2220}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2217 WHERE `id` = 421;

-- Brook: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (292, 0, '[{"dx":-3,"dy":-1,"id":2221},{"dx":-3,"dy":-1,"id":2222},{"dx":-3,"dy":-1,"id":2223},{"dx":-2,"dy":2,"id":2224}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2221 WHERE `id` = 422;

-- Jinbe: cache-safe four-angle alignment.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (293, 0, '[{"dx":-3,"dy":-1,"id":2225},{"dx":-3,"dy":-1,"id":2226},{"dx":-3,"dy":-2,"id":2227},{"dx":-2,"dy":0,"id":2228}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);
UPDATE `items` SET `icon` = 2225 WHERE `id` = 423;

ALTER TABLE `avatar_parts` AUTO_INCREMENT = 294;
ALTER TABLE `items` AUTO_INCREMENT = 424;
ALTER TABLE `sprite_images` AUTO_INCREMENT = 2229;
COMMIT;
