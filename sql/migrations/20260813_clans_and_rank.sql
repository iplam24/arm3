SET @schema_name = DATABASE();

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='clans' AND column_name='icon_id'),
  'SELECT 1',
  'ALTER TABLE `clans` ADD COLUMN `icon_id` smallint NOT NULL DEFAULT -1 AFTER `name`'
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='clans' AND column_name='max_members'),
  'SELECT 1',
  'ALTER TABLE `clans` ADD COLUMN `max_members` tinyint unsigned NOT NULL DEFAULT 50 AFTER `clan_gold`'
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='clan_members' AND column_name='donated'),
  'SELECT 1',
  'ALTER TABLE `clan_members` ADD COLUMN `donated` int NOT NULL DEFAULT 0 AFTER `member_role`'
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='clan_members' AND column_name='received_donate'),
  'SELECT 1',
  'ALTER TABLE `clan_members` ADD COLUMN `received_donate` int NOT NULL DEFAULT 0 AFTER `donated`'
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='clan_members' AND column_name='clan_point'),
  'SELECT 1',
  'ALTER TABLE `clan_members` ADD COLUMN `clan_point` int NOT NULL DEFAULT 0 AFTER `received_donate`'
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=@schema_name AND table_name='clan_members' AND column_name='joined_at'),
  'SELECT 1',
  'ALTER TABLE `clan_members` ADD COLUMN `joined_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `clan_point`'
);
PREPARE migration_statement FROM @sql;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

CREATE TABLE IF NOT EXISTS `clan_messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clan_id` int NOT NULL,
  `message_type` tinyint NOT NULL DEFAULT 0,
  `player_id` int NOT NULL,
  `message_text` varchar(200) NOT NULL DEFAULT '',
  `message_color` tinyint NOT NULL DEFAULT 0,
  `resolved` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_clan_messages_feed` (`clan_id`,`resolved`,`id`),
  KEY `idx_clan_messages_player` (`player_id`,`message_type`,`resolved`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `clan_invites` (
  `id` int NOT NULL AUTO_INCREMENT,
  `clan_id` int NOT NULL,
  `player_id` int NOT NULL,
  `invite_code` int NOT NULL,
  `inviter_player_id` int NOT NULL,
  `resolved` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_clan_invite_code` (`invite_code`),
  KEY `idx_clan_invites_player` (`player_id`,`resolved`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;