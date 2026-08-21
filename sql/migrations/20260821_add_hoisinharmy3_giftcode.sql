-- Migration: Add giftcode hoisinharmy3 (50m gold, 50k gem, 50 of each level 10 gem)
INSERT INTO `giftcode` (`code`, `gold`, `gem`, `items_json`, `max_use`)
VALUES (
  'HOISINHARMY3',
  50000000,
  50000,
  '[{"id":344,"quantity":50},{"id":345,"quantity":50},{"id":346,"quantity":50},{"id":347,"quantity":50},{"id":348,"quantity":50}]',
  999999
)
ON DUPLICATE KEY UPDATE
  `gold` = VALUES(`gold`),
  `gem` = VALUES(`gem`),
  `items_json` = VALUES(`items_json`),
  `max_use` = VALUES(`max_use`);
