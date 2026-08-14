START TRANSACTION;

UPDATE `items`
SET `options` = '[{"param":2,"id":18}]'
WHERE `id` BETWEEN 414 AND 423 AND `type` = 0;

COMMIT;