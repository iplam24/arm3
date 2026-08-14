START TRANSACTION;

SET @column_exists = (
    SELECT COUNT(*)
    FROM `information_schema`.`COLUMNS`
    WHERE `TABLE_SCHEMA` = DATABASE()
      AND `TABLE_NAME` = 'items'
      AND `COLUMN_NAME` = 'notrade'
);

SET @alter_items = IF(
    @column_exists = 0,
    'ALTER TABLE `items` ADD COLUMN `notrade` TINYINT(1) NOT NULL DEFAULT 0 AFTER `options`',
    'SELECT 1'
);

PREPARE add_notrade_statement FROM @alter_items;
EXECUTE add_notrade_statement;
DEALLOCATE PREPARE add_notrade_statement;

COMMIT;

-- Set notrade = 1 for items that must not be traded.
UPDATE `items` SET `notrade` = 1 WHERE `id` IN (391,392,393,394,395,396,397,398,400,401) AND `type` = 5;
