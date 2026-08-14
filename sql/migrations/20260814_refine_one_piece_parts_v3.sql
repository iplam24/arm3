START TRANSACTION;

-- One-pixel in-game refinements after the common v2 alignment.
-- Usopp and Jinbe move left; Sanji, Chopper, Franky and Brook move up.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`) VALUES
(287, 0, '[{"dx":-2,"dy":0,"id":2201},{"dx":-2,"dy":-1,"id":2202},{"dx":-2,"dy":-3,"id":2203},{"dx":-1,"dy":0,"id":2204}]'),
(288, 0, '[{"dx":-1,"dy":0,"id":2205},{"dx":-1,"dy":-1,"id":2206},{"dx":-1,"dy":-2,"id":2207},{"dx":0,"dy":0,"id":2208}]'),
(289, 0, '[{"dx":-1,"dy":0,"id":2209},{"dx":-1,"dy":-1,"id":2210},{"dx":-1,"dy":-2,"id":2211},{"dx":0,"dy":1,"id":2212}]'),
(291, 0, '[{"dx":0,"dy":-1,"id":2217},{"dx":0,"dy":-3,"id":2218},{"dx":0,"dy":-5,"id":2219},{"dx":1,"dy":-2,"id":2220}]'),
(292, 0, '[{"dx":0,"dy":0,"id":2221},{"dx":0,"dy":0,"id":2222},{"dx":0,"dy":0,"id":2223},{"dx":1,"dy":3,"id":2224}]'),
(293, 0, '[{"dx":-1,"dy":1,"id":2225},{"dx":-1,"dy":1,"id":2226},{"dx":-1,"dy":0,"id":2227},{"dx":0,"dy":2,"id":2228}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

ALTER TABLE `avatar_parts` AUTO_INCREMENT = 294;
COMMIT;
