START TRANSACTION;

INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (249, 3, '[{"dx":0,"dy":0,"id":0},{"dx":0,"dy":0,"id":0},{"dx":0,"dy":0,"id":0},{"dx":0,"dy":0,"id":0},{"dx":0,"dy":0,"id":0},{"dx":0,"dy":0,"id":0},{"dx":0,"dy":0,"id":0}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

COMMIT;
