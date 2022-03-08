package com.ordwen.odailyquests.quests.player.progression.sql;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadProgressionSQL {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Constructor.
     * @param sqlManager SQLManager instance.
     */
    public LoadProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /* init variables */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    ResultSet resultSet;

    long timestamp;
    PlayerQuests playerQuests;
    HashMap<Quest, Progression> quests = new HashMap<>();

    /* requests */
    private final String getTimestampQuery = "SELECT timestamp FROM progressions WHERE playerName = ";

    /**
     * Load player quests progression.
     * @param playerName name of the player.
     */
    public void loadProgression(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode) {

        try {
            Connection connection = sqlManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(getTimestampQuery + playerName);
            resultSet = preparedStatement.executeQuery();
            timestamp = resultSet.getLong("timestamp");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean timeToRedraw = false;

        /* check if last quests renewed day before */
        if (timestampConfigMode == 1) {
            Calendar oldCal = Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);
            currentCal.setTimeInMillis(System.currentTimeMillis());
            if (oldCal.get(Calendar.DATE) < currentCal.get(Calendar.DATE)) {
                timeToRedraw = true;
            }
        }

        /* check if last quests renewed is older than 24 hours */
        else if (timestampConfigMode == 2) {
            if (System.currentTimeMillis() - timestamp >= 86400000) {
                timeToRedraw = true;
            }
        }
        else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");

        /* renew quests */
        if (timeToRedraw) {
            activeQuests.remove(playerName);
            QuestsManager.selectRandomQuests(quests);
            if (timestampConfigMode == 1) {
                playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
            } else {
                playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
            }
            activeQuests.put(playerName, playerQuests);
            Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());

            logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been renewed.");
        }

        /* load non-achieved quests */
        else {

            // SCHEMA BD

        }

    }
}