START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (286, 0, '[{"dx":-4,"dy":-2,"id":2153},{"dx":-4,"dy":-3,"id":2154},{"dx":-4,"dy":-6,"id":2155},{"dx":-3,"dy":-5,"id":2156}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (287, 0, '[{"dx":-4,"dy":-2,"id":2158},{"dx":-4,"dy":-3,"id":2159},{"dx":-4,"dy":-6,"id":2160},{"dx":-3,"dy":-5,"id":2161}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

COMMIT;
