package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class SaveProgressionSQL {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Constructor.
     *
     * @param sqlManager instance of MySQLManager.
     */
    public SaveProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /* requests */
    private final String MYSQL_PLAYER_QUERY =
            "INSERT INTO PLAYER (PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS) " +
                    "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "PLAYERTIMESTAMP = " + "VALUES(PLAYERTIMESTAMP), " +
                    "ACHIEVEDQUESTS = VALUES(ACHIEVEDQUESTS), " +
                    "TOTALACHIEVEDQUESTS = VALUES(TOTALACHIEVEDQUESTS)";

    private final String H2_PLAYER_QUERY =
            "MERGE INTO PLAYER (PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS) " +
                    "KEY (PLAYERNAME) VALUES (?, ?, ?, ?)";
    private final String MYSQL_PROGRESS_UPDATE =
            "INSERT INTO PROGRESSION (PLAYERNAME, PLAYERQUESTID, QUESTINDEX, ADVANCEMENT, ISACHIEVED) " +
                    "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE " +
                    "QUESTINDEX = VALUES(QUESTINDEX), " +
                    "ADVANCEMENT = VALUES(ADVANCEMENT), " +
                    "ISACHIEVED = VALUES(ISACHIEVED)";

    private final String H2_PROGRESS_UPDATE =
            "MERGE INTO PROGRESSION (PLAYERNAME, PLAYERQUESTID, QUESTINDEX, ADVANCEMENT, ISACHIEVED) " +
                    "KEY (PLAYERNAME, PLAYERQUESTID) VALUES (?, ?, ?, ?, ?)";

    /**
     * Save player quests progression.
     *
     * @param playerName   name of the player.
     * @param playerQuests player quests.
     */
    public void saveProgression(String playerName, PlayerQuests playerQuests, boolean isAsync) {

        /* init variables */
        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

        final LinkedHashMap<AbstractQuest, Progression> quests = playerQuests.getPlayerQuests();

        if (isAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> saveDatas(playerName, timestamp, achievedQuests, totalAchievedQuests, quests));
        } else saveDatas(playerName, timestamp, achievedQuests, totalAchievedQuests, quests);
    }

    /**
     * Save player quests progression.
     *
     * @param playerName          name of the player.
     * @param timestamp           timestamp.
     * @param achievedQuests      achieved quests.
     * @param totalAchievedQuests total achieved quests.
     * @param quests              quests.
     */
    private void saveDatas(String playerName, long timestamp, int achievedQuests, int totalAchievedQuests, LinkedHashMap<AbstractQuest, Progression> quests) {
        final Connection connection = sqlManager.getConnection();

        try {
            PreparedStatement playerStatement;
            if (Modes.getStorageMode().equalsIgnoreCase("mysql")) playerStatement = connection.prepareStatement(MYSQL_PLAYER_QUERY);
            else playerStatement = connection.prepareStatement(H2_PLAYER_QUERY);

            playerStatement.setString(1, playerName);
            playerStatement.setLong(2, timestamp);
            playerStatement.setInt(3, achievedQuests);
            playerStatement.setInt(4, totalAchievedQuests);

            playerStatement.executeUpdate();

            int index = 0;
            for (AbstractQuest quest : quests.keySet()) {
                PreparedStatement progressionStatement;
                if (Modes.getStorageMode().equalsIgnoreCase("mysql")) progressionStatement = connection.prepareStatement(MYSQL_PROGRESS_UPDATE);
                else progressionStatement = connection.prepareStatement(H2_PROGRESS_UPDATE);

                progressionStatement.setString(1, playerName);
                progressionStatement.setInt(2, index);
                progressionStatement.setInt(3, quest.getQuestIndex());
                progressionStatement.setInt(4, quests.get(quest).getProgression());
                progressionStatement.setBoolean(5, quests.get(quest).isAchieved());

                progressionStatement.executeUpdate();
                index++;
            }

            PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s data saved.");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
