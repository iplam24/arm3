-- Level 5 equipment and weapon shop completion (2026-08-14)
START TRANSACTION;

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
    WHEN 114 THEN '[{"param":300,"id":1},{"param":300,"id":14}]'
    WHEN 124 THEN '[{"param":290,"id":1},{"param":290,"id":14}]'
    WHEN 134 THEN '[{"param":390,"id":1},{"param":390,"id":14}]'
    WHEN 144 THEN '[{"param":400,"id":1},{"param":400,"id":14}]'
    WHEN 154 THEN '[{"param":360,"id":1},{"param":360,"id":14}]'
    WHEN 164 THEN '[{"param":390,"id":1},{"param":390,"id":14}]'
    WHEN 174 THEN '[{"param":760,"id":1},{"param":632,"id":14}]'
    WHEN 184 THEN '[{"param":512,"id":1},{"param":512,"id":14}]'
    WHEN 194 THEN '[{"param":360,"id":1},{"param":360,"id":14}]'
    WHEN 204 THEN '[{"param":660,"id":1},{"param":660,"id":14}]'
    ELSE `options`
END,
`buy_gold` = CASE WHEN `type` = 5 THEN 1000000 ELSE 500000 END,
`buy_gem` = 0
WHERE `level` = 5
  AND `id` IN (
      14,19,24,29,34,39,44,49,54,59,
      64,69,74,79,84,89,94,99,104,109,
      114,124,134,144,154,164,174,184,194,204
  );

COMMIT;
