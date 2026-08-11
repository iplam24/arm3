START TRANSACTION;

SET @can_reduce_caption_exp_50_percent = (
    SELECT CASE WHEN `exp` = 1000 THEN 1 ELSE 0 END
    FROM `caption_levels`
    WHERE `id` = 1
    LIMIT 1
);

UPDATE `caption_levels`
SET `exp` = FLOOR(`exp` / 2)
WHERE @can_reduce_caption_exp_50_percent = 1
  AND `id` > 0;

COMMIT;