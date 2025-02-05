package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
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
    public void loadProgression(String playerName, HashMap<String, PlayerQuests> activeQuests) {
        Debugger.addDebug("Entering loadProgression (SQL) method for player " + playerName + ".");
        final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        ODailyQuests.morePaperLib.scheduling().asyncScheduler().runDelayed(() -> {
            Debugger.addDebug("Running async task to load progression of " + playerName + " from SQL database.");

            boolean hasStoredData = false;
            long timestamp = 0;
            int achievedQuests = 0;
            int totalAchievedQuests = 0;

            try {
                final Connection connection = sqlManager.getConnection();
                final String timestampQuery = "SELECT PLAYERTIMESTAMP,ACHIEVEDQUESTS,TOTALACHIEVEDQUESTS FROM PLAYER WHERE PLAYERNAME = ?";

                final PreparedStatement preparedStatement = connection.prepareStatement(timestampQuery);
                preparedStatement.setString(1, playerName);

                final ResultSet resultSet = preparedStatement.executeQuery();

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

                PluginLogger.error(e.getMessage());
            }

            if (hasStoredData) {
                Debugger.addDebug("Player " + playerName + " has data in the database.");

                if (QuestLoaderUtils.checkTimestamp(timestamp)) {
                    QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuests);
                } else {
                    loadPlayerQuests(playerName, quests);

                    PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    final Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        Debugger.addDebug("Player " + playerName + " is null. Impossible to load quests.");
                        PluginLogger.warn("It looks like " + playerName + " has disconnected before his quests were loaded.");
                        return;
                    }

                    activeQuests.put(playerName, playerQuests);
                    PluginLogger.info(playerName + "'s quests have been loaded.");

                    final String msg;
                    if (achievedQuests == playerQuests.getPlayerQuests().size()) {
                        msg = QuestsMessages.ALL_QUESTS_ACHIEVED_CONNECT.getMessage(playerName);
                    } else {
                        msg = QuestsMessages.QUESTS_IN_PROGRESS.getMessage(playerName);
                    }
                    if (msg != null) target.sendMessage(msg);
                }
            } else {
                QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, 0);
            }
        }, Duration.ofMillis(500));
    }

    /**
     * Load player quests.
     *
     * @param playerName player.
     * @param quests     list of player quests.
     */
    private void loadPlayerQuests(String playerName, LinkedHashMap<AbstractQuest, Progression> quests) {

        Debugger.addDebug("Entering loadPlayerQuests method for player " + playerName + ".");


        try {
            final Connection connection = sqlManager.getConnection();
            final String getQuestProgressionQuery = "SELECT * FROM PROGRESSION WHERE PLAYERNAME = ?";

            final PreparedStatement preparedStatement = connection.prepareStatement(getQuestProgressionQuery);
            preparedStatement.setString(1, playerName);

            final ResultSet resultSet = preparedStatement.executeQuery();

            int id = 1;

            resultSet.next();
            do {
                int questIndex = resultSet.getInt("QUESTINDEX");
                int advancement = resultSet.getInt("ADVANCEMENT");
                boolean isAchieved = resultSet.getBoolean("ISACHIEVED");

                Progression progression = new Progression(advancement, isAchieved);
                AbstractQuest quest = QuestLoaderUtils.findQuest(playerName, questIndex, id);

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


            PluginLogger.error(e.getMessage());
        }

        Debugger.addDebug("Quests of player " + playerName + " have been loaded.");
    }
}