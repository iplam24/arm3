START TRANSACTION;

UPDATE `items`
SET `options` = '[{"param":400,"id":1},{"param":300,"id":14},{"param":100,"id":6},{"param":100,"id":26}]'
WHERE `id` IN (400, 401);

COMMIT;
