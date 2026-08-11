START TRANSACTION;

UPDATE `items`
SET `options` = JSON_ARRAY_APPEND(
    CASE
        WHEN JSON_CONTAINS(`options`, JSON_OBJECT('id', 1), '$') THEN `options`
        ELSE JSON_ARRAY_APPEND(
            `options`, '$', JSON_OBJECT(
                'param', CASE `gender`
                    WHEN 0 THEN 280 + 5 * (`level` - 1)
                    WHEN 1 THEN 250 + 10 * (`level` - 1)
                    WHEN 2 THEN 300 + 15 * (`level` - 1)
                    WHEN 3 THEN 320 + 20 * (`level` - 1)
                    WHEN 4 THEN 330 + 15 * (`level` - 1)
                    WHEN 5 THEN 350 + 10 * (`level` - 1)
                    WHEN 6 THEN 600 + 40 * (`level` - 1)
                    WHEN 7 THEN 300 + 15 * (`level` - 1)
                    WHEN 8 THEN 480 + 8 * (`level` - 1)
                    WHEN 9 THEN 500 + 40 * (`level` - 1)
                END,
                'id', 1
            )
        )
    END,
    '$', JSON_OBJECT(
        'param', CASE `gender`
            WHEN 0 THEN 280 + 5 * (`level` - 1)
            WHEN 1 THEN 250 + 10 * (`level` - 1)
            WHEN 2 THEN 300 + 15 * (`level` - 1)
            WHEN 3 THEN 320 + 20 * (`level` - 1)
            WHEN 4 THEN 330 + 15 * (`level` - 1)
            WHEN 5 THEN 350 + 10 * (`level` - 1)
            WHEN 6 THEN 500 + 33 * (`level` - 1)
            WHEN 7 THEN 300 + 15 * (`level` - 1)
            WHEN 8 THEN 480 + 8 * (`level` - 1)
            WHEN 9 THEN 500 + 40 * (`level` - 1)
        END,
        'id', 14
    )
)
WHERE `type` = 5
  AND `id` BETWEEN 114 AND 209
  AND `level` BETWEEN 5 AND 10
  AND `gender` BETWEEN 0 AND 9
  AND JSON_VALID(`options`)
  AND NOT JSON_CONTAINS(`options`, JSON_OBJECT('id', 14), '$');

UPDATE `items`
SET `options` = JSON_ARRAY_APPEND(
    CASE
        WHEN JSON_CONTAINS(`options`, JSON_OBJECT('id', 1), '$') THEN `options`
        ELSE JSON_ARRAY_APPEND(
            `options`, '$', JSON_OBJECT(
                'param', CASE `id` WHEN 295 THEN 300 ELSE 400 END,
                'id', 1
            )
        )
    END,
    '$', JSON_OBJECT(
        'param', CASE `id` WHEN 295 THEN 500 ELSE 300 END,
        'id', 14
    )
)
WHERE `id` IN (295, 400, 401)
  AND `type` = 5
  AND JSON_VALID(`options`)
  AND NOT JSON_CONTAINS(`options`, JSON_OBJECT('id', 14), '$');

COMMIT;
