START TRANSACTION;

SET @can_rebalance_caption_exp = (
    SELECT CASE WHEN `exp` = 135039 THEN 1 ELSE 0 END
    FROM `caption_levels`
    WHERE `id` = 3
    LIMIT 1
);

CREATE TEMPORARY TABLE `tmp_caption_exp` AS
SELECT
    `id`,
    `exp` AS `old_exp`,
    LEAD(`exp`) OVER (ORDER BY `id`) AS `old_next_exp`,
    CASE WHEN `id` = 0 THEN 0 ELSE 1500 * `id` * `id` - 500 * `id` END AS `new_exp`,
    LEAD(CASE WHEN `id` = 0 THEN 0 ELSE 1500 * `id` * `id` - 500 * `id` END)
        OVER (ORDER BY `id`) AS `new_next_exp`
FROM `caption_levels`;

CREATE TEMPORARY TABLE `tmp_player_current_exp` AS
SELECT
    `id`,
    GREATEST(0, CAST(JSON_UNQUOTE(JSON_EXTRACT(`stats_json`, '$.exp')) AS SIGNED)) AS `current_exp`
FROM `players`
WHERE @can_rebalance_caption_exp = 1
  AND JSON_VALID(`stats_json`)
  AND JSON_CONTAINS_PATH(`stats_json`, 'one', '$.exp');

CREATE TEMPORARY TABLE `tmp_player_level` AS
SELECT player_exp.`id`, MAX(level_exp.`id`) AS `level_id`
FROM `tmp_player_current_exp` player_exp
JOIN `tmp_caption_exp` level_exp ON level_exp.`old_exp` <= player_exp.`current_exp`
GROUP BY player_exp.`id`;

CREATE TEMPORARY TABLE `tmp_player_mapped_exp` AS
SELECT
    player_exp.`id`,
    CASE
        WHEN level_exp.`old_next_exp` IS NULL THEN
            FLOOR(player_exp.`current_exp` * level_exp.`new_exp`
                / GREATEST(1, level_exp.`old_exp`))
        ELSE level_exp.`new_exp` + FLOOR(
            (player_exp.`current_exp` - level_exp.`old_exp`)
            * (level_exp.`new_next_exp` - level_exp.`new_exp`)
            / GREATEST(1, level_exp.`old_next_exp` - level_exp.`old_exp`)
        )
    END AS `mapped_exp`
FROM `tmp_player_current_exp` player_exp
JOIN `tmp_player_level` player_level ON player_level.`id` = player_exp.`id`
JOIN `tmp_caption_exp` level_exp ON level_exp.`id` = player_level.`level_id`;

UPDATE `players` player
JOIN `tmp_player_mapped_exp` mapped ON mapped.`id` = player.`id`
SET player.`stats_json` = JSON_SET(player.`stats_json`, '$.exp', mapped.`mapped_exp`)
WHERE @can_rebalance_caption_exp = 1;

UPDATE `caption_levels`
SET `exp` = CASE WHEN `id` = 0 THEN 0 ELSE 1500 * `id` * `id` - 500 * `id` END
WHERE @can_rebalance_caption_exp = 1;

DROP TEMPORARY TABLE `tmp_player_mapped_exp`;
DROP TEMPORARY TABLE `tmp_player_level`;
DROP TEMPORARY TABLE `tmp_player_current_exp`;
DROP TEMPORARY TABLE `tmp_caption_exp`;

COMMIT;
