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
        int achievedQuests = playerQuests.getAchievedQuests();
        HashMap<Quest, Progression> quests = playerQuests.getPlayerQuests();

        Connection connection = mySqlManager.getConnection();

        String test = "SELECT * FROM PLAYER WHERE PLAYERNAME = '" + playerName + "'";

        try {
            PreparedStatement testQuery = connection.prepareStatement(test);
            ResultSet result = testQuery.executeQuery();

            if (result.next()) {
                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " detected into database.");

                String query = "UPDATE PLAYER\n" +
                        "SET PLAYERTIMESTAMP = " + timestamp + ", ACHIEVEDQUESTS = " + achievedQuests + "\n" +
                        "WHERE PLAYERNAME = '" + playerName + "'"
                        ;
                connection.prepareStatement(query).execute();

                int index = 0;
                for (Quest quest : quests.keySet()) {
                    String update = "UPDATE PROGRESSION\n" +
                            "SET QUESTINDEX = " + quest.getQuestIndex() + ", ADVANCEMENT = " + quests.get(quest).getProgression() + ", ISACHIEVED = " + quests.get(quest).isAchieved() + "\n"
                            + "WHERE PLAYERNAME = '" + playerName + "' AND PLAYERQUESTID = " + index;
                    connection.prepareStatement(update).execute();
                    index++;
                }

            } else {
                logger.info(ChatColor.GOLD + playerName + "" + timestamp);

                String query = "INSERT INTO PLAYER\n" +
                        "VALUES\n" +
                        "('" + playerName + "', " + timestamp + ", " + achievedQuests + ")";

                connection.prepareStatement(query).execute();

                int index = 0;
                for (Quest quest : quests.keySet()) {
                    String update = "INSERT INTO PROGRESSION(PLAYERNAME, PLAYERQUESTID, QUESTINDEX, ADVANCEMENT, ISACHIEVED)\n" +
                            "VALUES\n" +
                            "('" + playerName + "', " + index + ", " + quest.getQuestIndex() + ", " + quests.get(quest).getProgression() + ", " + quests.get(quest).isAchieved() + ")";
                    connection.prepareStatement(update).execute();
                    index++;
                }

                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " added to database.");
            }

            testQuery.close();
            result.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
