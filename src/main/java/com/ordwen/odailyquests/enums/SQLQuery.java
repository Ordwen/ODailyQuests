package com.ordwen.odailyquests.enums;

import com.ordwen.odailyquests.configuration.essentials.Database;

public enum SQLQuery {

    CREATE_PLAYER_TABLE("""
        CREATE TABLE `%splayer` (
            `player_name` CHAR(32) NOT NULL,
            `player_timestamp` BIGINT NOT NULL,
            `achieved_quests` TINYINT NOT NULL,
            `total_achieved_quests` INT NOT NULL,
            CONSTRAINT `pk_player` PRIMARY KEY (`player_name`)
        );
    """),

    CREATE_PROGRESSION_TABLE("""
        CREATE TABLE `%sprogression` (
            `primary_key` INT AUTO_INCREMENT,
            `player_name` CHAR(32) NOT NULL,
            `player_quest_id` SMALLINT NOT NULL,
            `quest_index` INT NOT NULL,
            `advancement` INT NOT NULL,
            `is_achieved` BIT NOT NULL,
            PRIMARY KEY (`primary_key`),
            CONSTRAINT `unique_player_quest` UNIQUE (`player_name`, `player_quest_id`)
        );
    """),

    MYSQL_PLAYER_QUERY("""
        INSERT INTO `%splayer` (`player_name`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            `player_timestamp` = VALUES(`player_timestamp`),
            `achieved_quests` = VALUES(`achieved_quests`),
            `total_achieved_quests` = VALUES(`total_achieved_quests`);
    """),

    MYSQL_PROGRESS_UPDATE("""
        INSERT INTO `%sprogression` (`player_name`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`)
        VALUES (?, ?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            `quest_index` = VALUES(`quest_index`),
            `advancement` = VALUES(`advancement`),
            `is_achieved` = VALUES(`is_achieved`);
    """),

    H2_PLAYER_QUERY("""
        MERGE INTO `%splayer` (`player_name`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`)
        KEY (`player_name`) VALUES (?, ?, ?, ?);
    """),

    H2_PROGRESS_UPDATE("""
        MERGE INTO `%sprogression` (`player_name`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`)
        KEY (`player_name`, `player_quest_id`) VALUES (?, ?, ?, ?, ?);
    """),

    TIMESTAMP_QUERY("""
        SELECT player_timestamp, achieved_quests, total_achieved_quests FROM `%splayer`
        WHERE player_name = ?;
    """),

    QUEST_PROGRESSION_QUERY("""
        SELECT * FROM `%sprogression`
        WHERE player_name = ?;
    """),

    ;

    private final String query;

    SQLQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return String.format(this.query, Database.getPrefix());
    }
}
