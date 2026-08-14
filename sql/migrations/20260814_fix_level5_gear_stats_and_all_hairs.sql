START TRANSACTION;

UPDATE `items` AS `i`
JOIN JSON_TABLE(
    CASE WHEN JSON_VALID(`i`.`options`) THEN `i`.`options` ELSE JSON_ARRAY() END,
    '$[*]' COLUMNS (`option_index` FOR ORDINALITY, `option_id` INT PATH '$.id')
) AS `option_rows`
SET `i`.`options` = JSON_SET(
    CASE WHEN JSON_VALID(`i`.`options`) THEN `i`.`options` ELSE JSON_ARRAY() END,
    CONCAT('$[', `option_rows`.`option_index` - 1, '].param'),
    2
)
WHERE `i`.`type` = 0
  AND `option_rows`.`option_id` = 18;

UPDATE `items` AS `i`
SET `i`.`options` = JSON_ARRAY_APPEND(
    CASE WHEN JSON_VALID(`i`.`options`) THEN `i`.`options` ELSE JSON_ARRAY() END,
    '$', JSON_OBJECT('param', 2, 'id', 18)
)
WHERE `i`.`type` = 0
  AND NOT JSON_CONTAINS(
      CASE WHEN JSON_VALID(`i`.`options`) THEN `i`.`options` ELSE JSON_ARRAY() END,
      JSON_OBJECT('id', 18), '$'
  );

UPDATE `items`
SET `options` = CASE `id`
    WHEN 14 THEN '[{"param":1200,"id":0},{"param":30,"id":1},{"param":15,"id":2},{"param":15,"id":3},{"param":10,"id":4}]'
    WHEN 19 THEN '[{"param":60,"id":1},{"param":30,"id":2},{"param":30,"id":3},{"param":15,"id":4},{"param":100,"id":0}]'
    WHEN 24 THEN '[{"param":60,"id":2},{"param":60,"id":3},{"param":300,"id":0},{"param":8,"id":1},{"param":10,"id":4}]'
    WHEN 29 THEN '[{"param":120,"id":3},{"param":60,"id":4},{"param":300,"id":0},{"param":8,"id":1},{"param":5,"id":2}]'
    WHEN 34 THEN '[{"param":120,"id":4},{"param":600,"id":0},{"param":15,"id":1},{"param":8,"id":2},{"param":10,"id":3}]'
    WHEN 39 THEN '[{"param":1200,"id":0},{"param":30,"id":2},{"param":30,"id":3},{"param":15,"id":4},{"param":5,"id":1}]'
    WHEN 44 THEN '[{"param":60,"id":1},{"param":60,"id":3},{"param":30,"id":4},{"param":150,"id":0},{"param":5,"id":2}]'
    WHEN 49 THEN '[{"param":60,"id":2},{"param":60,"id":4},{"param":300,"id":0},{"param":8,"id":1},{"param":10,"id":3}]'
    WHEN 54 THEN '[{"param":120,"id":3},{"param":600,"id":0},{"param":15,"id":1},{"param":8,"id":2},{"param":10,"id":4}]'
    WHEN 59 THEN '[{"param":120,"id":4},{"param":30,"id":1},{"param":15,"id":2},{"param":15,"id":3},{"param":100,"id":0}]'
    WHEN 64 THEN '[{"param":1200,"id":0},{"param":60,"id":3},{"param":30,"id":4},{"param":8,"id":1},{"param":5,"id":2}]'
    WHEN 69 THEN '[{"param":60,"id":1},{"param":60,"id":4},{"param":300,"id":0},{"param":8,"id":2},{"param":10,"id":3}]'
    WHEN 74 THEN '[{"param":60,"id":2},{"param":600,"id":0},{"param":15,"id":1},{"param":15,"id":3},{"param":10,"id":4}]'
    WHEN 79 THEN '[{"param":120,"id":3},{"param":30,"id":1},{"param":15,"id":2},{"param":15,"id":4},{"param":100,"id":0}]'
    WHEN 84 THEN '[{"param":120,"id":4},{"param":30,"id":2},{"param":30,"id":3},{"param":150,"id":0},{"param":5,"id":1}]'
    WHEN 89 THEN '[{"param":1200,"id":0},{"param":60,"id":4},{"param":15,"id":1},{"param":8,"id":2},{"param":10,"id":3},{"param":6,"id":13}]'
    WHEN 94 THEN '[{"param":60,"id":1},{"param":600,"id":0},{"param":15,"id":2},{"param":15,"id":3},{"param":10,"id":4},{"param":6,"id":13}]'
    WHEN 99 THEN '[{"param":60,"id":2},{"param":30,"id":1},{"param":30,"id":3},{"param":15,"id":4},{"param":100,"id":0},{"param":6,"id":13}]'
    WHEN 104 THEN '[{"param":120,"id":3},{"param":30,"id":2},{"param":30,"id":4},{"param":150,"id":0},{"param":5,"id":1},{"param":6,"id":13}]'
    WHEN 109 THEN '[{"param":120,"id":4},{"param":60,"id":3},{"param":300,"id":0},{"param":8,"id":1},{"param":5,"id":2},{"param":6,"id":13}]'
    ELSE `options`
END
WHERE `id` IN (14,19,24,29,34,39,44,49,54,59,64,69,74,79,84,89,94,99,104,109);

COMMIT;
