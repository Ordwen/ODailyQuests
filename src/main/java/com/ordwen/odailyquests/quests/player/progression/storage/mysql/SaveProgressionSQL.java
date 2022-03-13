package com.ordwen.odailyquests.quests.player.progression.storage.mysql;

import org.bukkit.plugin.PluginLogger;

import java.sql.Connection;
import java.util.logging.Logger;

public class SaveProgressionSQL {

    /* instance of SQLManager */
    private final MySQLManager mySqlManager;

    /**
     * Constructor.
     * @param mySqlManager SQLManager instance.
     */
    public SaveProgressionSQL(MySQLManager mySqlManager) {
        this.mySqlManager = mySqlManager;
    }

    /* Logger */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* requests */

    /**
     * Save player quests progression.
     * @param playerName name of the player.
     */
    public void saveProgression(String playerName) {

        final Connection connection = mySqlManager.getConnection();

    }
}
