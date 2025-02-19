package com.ordwen.odailyquests.enums;

public enum SQLQuery {

    CREATE_PLAYER_TABLE("""
        CREATE TABLE IF NOT EXISTS `odq_player` (
            `player_name` CHAR(32) NOT NULL,
            `player_timestamp` BIGINT NOT NULL,
            `achieved_quests` TINYINT NOT NULL,
            `total_achieved_quests` INT NOT NULL,
            CONSTRAINT `odq_pk_player` PRIMARY KEY (`player_name`)
        );
    """),

    CREATE_PROGRESSION_TABLE("""
        CREATE TABLE IF NOT EXISTS `odq_progression` (
            `primary_key` INT AUTO_INCREMENT,
            `player_name` CHAR(32) NOT NULL,
            `player_quest_id` SMALLINT NOT NULL,
            `quest_index` INT NOT NULL,
            `advancement` INT NOT NULL,
            `is_achieved` BIT NOT NULL,
            PRIMARY KEY (`primary_key`),
            CONSTRAINT `odq_unique_player_quest` UNIQUE (`player_name`, `player_quest_id`)
        );
    """),

    MYSQL_PLAYER_QUERY("""
        INSERT INTO `odq_player` (`player_name`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            `player_timestamp` = VALUES(`player_timestamp`),
            `achieved_quests` = VALUES(`achieved_quests`),
            `total_achieved_quests` = VALUES(`total_achieved_quests`);
    """),

    MYSQL_PROGRESS_UPDATE("""
        INSERT INTO `odq_progression` (`player_name`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`)
        VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            `quest_index` = VALUES(`quest_index`),
            `advancement` = VALUES(`advancement`),
            `is_achieved` = VALUES(`is_achieved`);
    """),

    H2_PLAYER_QUERY("""
        MERGE INTO `odq_player` (`player_name`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`)
        KEY (`player_name`) VALUES (?, ?, ?, ?);
    """),

    H2_PROGRESS_UPDATE("""
        MERGE INTO `odq_progression` (`player_name`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`)
        KEY (`player_name`, `player_quest_id`) VALUES (?, ?, ?, ?, ?);
    """),

    TIMESTAMP_QUERY("""
        SELECT player_timestamp, achieved_quests, total_achieved_quests FROM `odq_player`
        WHERE player_name = ?;
    """),

    QUEST_PROGRESSION_QUERY("""
        SELECT * FROM `odq_progression`
        WHERE player_name = ?;
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
