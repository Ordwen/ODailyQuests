package com.ordwen.odailyquests.quests.player.progression.sql;

import org.bukkit.plugin.PluginLogger;

import java.sql.Connection;
import java.util.logging.Logger;

public class SaveProgressionSQL {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Constructor.
     * @param sqlManager SQLManager instance.
     */
    public SaveProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /* Logger */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /* requests */

    /**
     * Save player quests progression.
     * @param playerName name of the player.
     */
    public void saveProgression(String playerName) {

        final Connection connection = sqlManager.getConnection();

    }
}
