CREATE TABLE IF NOT EXISTS `clan_items` (
  `clan_id` int NOT NULL,
  `item_id` int NOT NULL,
  `purchased_by` int NOT NULL,
  `active` tinyint NOT NULL DEFAULT '0',
  `acquired_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `activated_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`clan_id`,`item_id`),
  KEY `idx_clan_items_active` (`clan_id`,`active`),
  KEY `idx_clan_items_purchased_by` (`purchased_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
