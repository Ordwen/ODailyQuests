package com.ordwen.odailyquests.quests.player.progression.storage.mysql;

import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
     * @param activeQuests player quests.
     */
    public void saveProgression(String playerName, HashMap<String, PlayerQuests> activeQuests) {

        /* init variables */
        PlayerQuests playerQuests = activeQuests.get(playerName);
        long timestamp = playerQuests.getTimestamp();
        HashMap<Quest, Progression> quests = playerQuests.getPlayerQuests();

        Connection connection = mySqlManager.getConnection();

        String test = "SELECT * FROM Player WHERE playerName = '" + playerName + "'";

        try {
            PreparedStatement testQuery = connection.prepareStatement(test);
            ResultSet result = testQuery.executeQuery();

            if (result.next()) {
                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " detected into database.");

                String query = "UPDATE PLAYER\n" +
                        "SET PLAYERTIMESTAMP = " + timestamp + "\n" +
                        "WHERE PLAYERNAME = '" + playerName + "'";
                connection.prepareStatement(query).executeQuery();

                int index = 0;
                for (Quest quest : quests.keySet()) {
                    String update = "UPDATE Progression\n" +
                            "SET questIndex = " + quest.getQuestIndex() + ", advancement = " + quests.get(quest).getProgression() + ", isAchieved = " + quests.get(quest).isAchieved() + "\n"
                            + "WHERE playerName = '" + playerName + "' AND playerQuestId = " + index;
                    connection.prepareStatement(update).executeQuery();
                    index++;
                }

            } else {
                String query = "INSERT INTO PLAYER\n" +
                        "VALUES\n" +
                        "('" + playerName + "', " + timestamp + ")";
                connection.prepareStatement(query).executeQuery();

                int index = 0;
                for (Quest quest : quests.keySet()) {
                    String update = "INSERT INTO PROGRESSION\n" +
                            "VALUES\n" +
                            "('" + playerName + "', " + index + ", " + quest.getQuestIndex() + ", " + quests.get(quest).getProgression() + ", " + quests.get(quest).isAchieved() + ")";
                    connection.prepareStatement(update).executeQuery();
                    index++;
                }

                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " added to database.");
            }

            testQuery.close();
            result.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
