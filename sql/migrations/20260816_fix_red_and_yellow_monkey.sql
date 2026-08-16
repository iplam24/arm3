START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (246, 0, '[{"dx":4,"dy":-2,"id":1909},{"dx":6,"dy":-2,"id":1922},{"dx":5,"dy":-5,"id":1923},{"dx":2,"dy":-3,"id":1924}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (249, 3, '[{"dx":4,"dy":0,"id":1932},{"dx":4,"dy":0,"id":1933},{"dx":4,"dy":0,"id":1934},{"dx":-2,"dy":2,"id":1935},{"dx":-4,"dy":2,"id":1936},{"dx":4,"dy":4,"id":1937},{"dx":4,"dy":2,"id":1938}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (250, 0, '[{"dx":5,"dy":-4,"id":1941},{"dx":6,"dy":-2,"id":1942},{"dx":5,"dy":-5,"id":1943},{"dx":2,"dy":-3,"id":1944}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

COMMIT;
