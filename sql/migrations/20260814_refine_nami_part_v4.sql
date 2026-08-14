START TRANSACTION;

-- Nami moves 1 px left and 1 px up at zoom x1.
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (286, 0, '[{"dx":-2,"dy":-2,"id":2197},{"dx":-2,"dy":-3,"id":2198},{"dx":-2,"dy":-5,"id":2199},{"dx":-1,"dy":-2,"id":2200}]')
ON DUPLICATE KEY UPDATE
    `type` = VALUES(`type`),
    `part_data` = VALUES(`part_data`);

ALTER TABLE `avatar_parts` AUTO_INCREMENT = 294;
COMMIT;
