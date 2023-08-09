package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LoadProgressionSQL {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Constructor.
     *
     * @param sqlManager instance of MySQLManager.
     */
    public LoadProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Load player quests progression.
     *
     * @param playerName name of the player.
     */
    public void loadProgression(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode, int temporalityMode) {

        Debugger.addDebug("Entering loadProgression method for player " + playerName + ".");

        LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        Bukkit.getScheduler().runTaskLaterAsynchronously(ODailyQuests.INSTANCE, () -> {

            Debugger.addDebug("Running async task to load progression of " + playerName + " from SQL database.");

            boolean hasStoredData = false;
            long timestamp = 0;
            int achievedQuests = 0;
            int totalAchievedQuests = 0;

            try {
                Connection connection = sqlManager.getConnection();
                String timestampQuery = "SELECT PLAYERTIMESTAMP,ACHIEVEDQUESTS,TOTALACHIEVEDQUESTS FROM PLAYER WHERE PLAYERNAME = '" + playerName + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(timestampQuery);

                ResultSet resultSet = preparedStatement.executeQuery();

                Debugger.addDebug("Executing query for player " + playerName + ": " + timestampQuery);


                if (resultSet.next()) {
                    hasStoredData = true;
                    timestamp = resultSet.getLong("PLAYERTIMESTAMP");
                    achievedQuests = resultSet.getInt("ACHIEVEDQUESTS");
                    totalAchievedQuests = resultSet.getInt("TOTALACHIEVEDQUESTS");

                    Debugger.addDebug("Player " + playerName + " has stored data.");

                } else {
                    Debugger.addDebug("Player " + playerName + " has no stored data.");

                }

                resultSet.close();
                preparedStatement.close();
                connection.close();

                Debugger.addDebug("Database connection closed.");


            } catch (SQLException e) {
                PluginLogger.error(ChatColor.RED + "An error occurred while loading player " + playerName + "'s quests progression.");

                Debugger.addDebug("An error occurred while loading player " + playerName + "'s quests progression.");
                Debugger.addDebug(e.getMessage());

                e.printStackTrace();
            }

            if (hasStoredData) {
                if (Utils.checkTimestamp(timestampConfigMode, temporalityMode, timestamp)) {
                    Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, totalAchievedQuests);
                } else {
                    loadPlayerQuests(playerName, questsConfigMode, quests);

                    PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    if (Bukkit.getPlayer(playerName) != null) {
                        activeQuests.put(playerName, playerQuests);
                        PluginLogger.info(playerName + "'s quests have been loaded.");
                    } else {
                        PluginLogger.warn("It looks like " + playerName + " has disconnected before his quests were loaded.");
                        return;
                    }

                    final String msg;
                    if (achievedQuests == playerQuests.getPlayerQuests().size()) {
                        msg = QuestsMessages.ALL_QUESTS_ACHIEVED_CONNECT.getMessage(playerName);
                    } else {
                        msg = QuestsMessages.QUESTS_IN_PROGRESS.getMessage(playerName);
                    }
                    if (msg != null) Bukkit.getPlayer(playerName).sendMessage(msg);
                }
            } else {
                Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, 0);
            }
        }, 10);
    }

    /**
     * Load player quests.
     *
     * @param playerName       player.
     * @param questsConfigMode configuration mode.
     * @param quests           list of player quests.
     */
    private void loadPlayerQuests(String playerName, int questsConfigMode, LinkedHashMap<AbstractQuest, Progression> quests) {

        Debugger.addDebug("Entering loadPlayerQuests method for player " + playerName + ".");


        try {
            Connection connection = sqlManager.getConnection();
            String getQuestProgressionQuery = "SELECT * FROM PROGRESSION WHERE PLAYERNAME = '" + playerName + "'";
            PreparedStatement preparedStatement = connection.prepareStatement(getQuestProgressionQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            int id = 1;

            resultSet.next();
            do {
                int questIndex = resultSet.getInt("QUESTINDEX");
                int advancement = resultSet.getInt("ADVANCEMENT");
                boolean isAchieved = resultSet.getBoolean("ISACHIEVED");

                Progression progression = new Progression(advancement, isAchieved);
                AbstractQuest quest = Utils.findQuest(playerName, questsConfigMode, questIndex, id);

                quests.put(quest, progression);

                id++;
            } while (resultSet.next() && id <= QuestsAmount.getQuestsAmount());

            if (resultSet.next()) {
                PluginLogger.warn("Player " + playerName + " has more quests than the configuration.");
                PluginLogger.warn("Only the first " + QuestsAmount.getQuestsAmount() + " quests will be loaded.");
                PluginLogger.warn("After changing the number of quests, we recommend that you reset the progressions to avoid any problems.");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            PluginLogger.error(ChatColor.RED + "An error occurred while loading player " + playerName + "'s quests.");

            Debugger.addDebug("An error occurred while loading player " + playerName + "'s quests.");
            Debugger.addDebug(e.getMessage());


            e.printStackTrace();
        }

        Debugger.addDebug("Quests of player " + playerName + " have been loaded.");
    }
}