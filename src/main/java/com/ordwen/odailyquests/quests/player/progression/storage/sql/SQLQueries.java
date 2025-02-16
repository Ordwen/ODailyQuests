package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.configuration.ConfigurationHolder;

public class SQLQueries {

    private SQLQueries() {
    }

    public static final String CREATE_PLAYER_TABLE = "CREATE TABLE `" + ConfigurationHolder.DatabaseConfig.PLAYER_TABLE_NAME + "` (\n" +
                "    `player_name` char(32) NOT NULL,\n" +
                "    `player_timestamp` bigint NOT NULL,\n" +
                "    `achieved_quests` tinyint NOT NULL,\n" +
                "    `total_achieved_quests` int NOT NULL,\n" +
                "    constraint `pk_player` primary key (`player_name`)\n" +
                ");";


    public static final String CREATE_PROGRESSION_TABLE = "CREATE TABLE `" + ConfigurationHolder.DatabaseConfig.PROGRESSION_TABLE_NAME + "` (\n" +
                "    `primary_key` int auto_increment,\n" +
                "    `player_name` char(32) NOT NULL,\n" +
                "    `player_quest_id` smallint NOT NULL,\n" +
                "    `quest_index` int NOT NULL,\n" +
                "    `advancement` int NOT NULL,\n" +
                "    `is_achieved` bit NOT NULL,\n" +
                "    primary key (`primary_key`),\n" +
                "    constraint `unique_player_quest` unique (`player_name`, `player_quest_id`)\n" +
                ");";

    public static final String TIMESTAMP_QUERY = "SELECT player_timestamp, achieved_quests, total_achieved_quests FROM " + ConfigurationHolder.DatabaseConfig.PLAYER_TABLE_NAME + " WHERE player_name = ?";
    
    public static final String MYSQL_PLAYER_QUERY =
            "INSERT INTO `" + ConfigurationHolder.DatabaseConfig.PLAYER_TABLE_NAME + "` (`player_name`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`) " +
                    "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "`player_timestamp` = VALUES(`player_timestamp`), " +
                    "`achieved_quests` = VALUES(`achieved_quests`), " +
                    "`total_achieved_quests` = VALUES(`total_achieved_quests`)";

    public static final String H2_PLAYER_QUERY = "MERGE INTO `" + ConfigurationHolder.DatabaseConfig.PLAYER_TABLE_NAME + "` (`player_name`, `player_timestamp`, `achieved_quests`, `total_achieved_quests`) " + "KEY (`player_name`) VALUES (?, ?, ?, ?)";

    public static final String MYSQL_PROGRESS_UPDATE =
    "INSERT INTO `" + ConfigurationHolder.DatabaseConfig.PROGRESSION_TABLE_NAME + "` (`player_name`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`) " + "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " + "`quest_index` = VALUES(`quest_index`), " + "`advancement` = VALUES(`advancement`), " + "`is_achieved` = VALUES(`is_achieved`)";
    public static final String H2_PROGRESS_UPDATE = "MERGE INTO `" + ConfigurationHolder.DatabaseConfig.PROGRESSION_TABLE_NAME + "` (`player_name`, `player_quest_id`, `quest_index`, `advancement`, `is_achieved`) " + "KEY (`player_name`, `player_quest_id`) VALUES (?, ?, ?, ?, ?)";

    public static final String QUEST_PROGRESSION_QUERY = "SELECT * FROM " + ConfigurationHolder.DatabaseConfig.PROGRESSION_TABLE_NAME + "WHERE player_name = ?";

}