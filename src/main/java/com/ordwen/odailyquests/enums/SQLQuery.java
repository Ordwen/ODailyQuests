package com.ordwen.odailyquests.enums;

public enum SQLQuery {

    // MySQL queries //

    MYSQL_CREATE_PLAYER_TABLE("""
                CREATE TABLE IF NOT EXISTS `odq_player` (
                    `player_uuid` CHAR(36) NOT NULL,
                    `player_timestamp` BIGINT NOT NULL,
                    `achieved_quests` TINYINT NOT NULL,
                    `total_achieved_quests` INT NOT NULL,
                    CONSTRAINT `odq_pk_player` PRIMARY KEY (`player_uuid`)
                );
            """),

    MYSQL_CREATE_PROGRESSION_TABLE("""
                CREATE TABLE IF NOT EXISTS `odq_progression` (
                    `primary_key` INT AUTO_INCREMENT,
                    `player_uuid` CHAR(36) NOT NULL,
                    `player_quest_id` SMALLINT NOT NULL,
                    `quest_index` INT NOT NULL,
                    `advancement` INT NOT NULL,
                    `is_achieved` BIT NOT NULL,
                    PRIMARY KEY (`primary_key`),
                    CONSTRAINT `odq_unique_player_quest` UNIQUE (`player_uuid`, `player_quest_id`)
                );
            """),

    MYSQL_PLAYER_QUERY("""
                INSERT INTO `odq_player` (`player_uuid`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    `player_timestamp` = VALUES(`player_timestamp`),
                    `achieved_quests` = VALUES(`achieved_quests`),
                    `total_achieved_quests` = VALUES(`total_achieved_quests`);
            """),

    MYSQL_PROGRESS_UPDATE("""
                INSERT INTO `odq_progression` (`player_uuid`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    `quest_index` = VALUES(`quest_index`),
                    `advancement` = VALUES(`advancement`),
                    `is_achieved` = VALUES(`is_achieved`);
            """),

    // SQLite queries //

    SQLITE_CREATE_PLAYER_TABLE("""
                CREATE TABLE IF NOT EXISTS `odq_player` (
                    `player_uuid` TEXT NOT NULL,
                    `player_timestamp` INTEGER NOT NULL,
                    `achieved_quests` INTEGER NOT NULL,
                    `total_achieved_quests` INTEGER NOT NULL,
                    PRIMARY KEY (`player_uuid`)
                );
            """),

    SQLITE_CREATE_PROGRESSION_TABLE("""
                CREATE TABLE IF NOT EXISTS odq_progression (
                    primary_key INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    player_quest_id INTEGER NOT NULL,
                    quest_index INTEGER NOT NULL,
                    advancement INTEGER NOT NULL,
                    is_achieved INTEGER NOT NULL,
                    UNIQUE (player_uuid, player_quest_id)
                );
            """),

    SQLITE_PLAYER_QUERY("""
                INSERT OR REPLACE INTO `odq_player` (`player_uuid`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`)
                VALUES (?, ?, ?, ?);
            """),

    SQLITE_PROGRESS_UPDATE("""
                INSERT OR REPLACE INTO `odq_progression` (`player_uuid`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`)
                VALUES (?, ?, ?, ?, ?);
            """),

    // Common queries //

    TIMESTAMP_QUERY("""
                SELECT player_timestamp, achieved_quests, total_achieved_quests FROM `odq_player`
                WHERE player_uuid = ?;
            """),

    QUEST_PROGRESSION_QUERY("""
                SELECT * FROM `odq_progression`
                WHERE player_uuid = ?;
            """),

    ;

    private final String query;

    SQLQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return this.query;
    }
}
