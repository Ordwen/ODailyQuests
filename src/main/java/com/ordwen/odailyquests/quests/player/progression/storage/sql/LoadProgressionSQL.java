package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public void loadProgression(QuestSystem questSystem, String playerName, HashMap<String, PlayerQuests> activeQuests) {

        Debugger.addDebug("Entering loadProgression method for player " + playerName + ".");

        LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        Bukkit.getScheduler().runTaskLaterAsynchronously(ODailyQuests.INSTANCE, () -> {

            Debugger.addDebug("Running async task to load progression of " + playerName + " from SQL database.");

            boolean hasStoredData = false;
            long timestamp = 0;
            int achievedQuests = 0;
            int totalAchievedQuests = 0;

            try {
                final Connection connection = sqlManager.getConnection();
                final String timestampQuery = "SELECT PLAYERTIMESTAMP,ACHIEVEDQUESTS,TOTALACHIEVEDQUESTS FROM " + questSystem.getPlayerTableName() + " WHERE PLAYERNAME = ?";

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
                if (QuestLoaderUtils.checkTimestamp(questSystem, timestamp)) {
                    QuestLoaderUtils.loadNewPlayerQuests(questSystem, playerName, activeQuests, totalAchievedQuests);
                } else {
                    loadPlayerQuests(questSystem, playerName, quests);
                    PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);
                    final Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        PluginLogger.warn("It looks like " + playerName + " has disconnected before his quests were loaded.");
                        return;
                    }
                    activeQuests.put(playerName, playerQuests);
                    PluginLogger.info(playerName + "'s quests have been loaded.");
                    final String msg;
                    if (achievedQuests == playerQuests.getPlayerQuests().size()) {
                        msg = questSystem.getALL_QUESTS_ACHIEVED().getMessage(playerName);
                    } else {
                        msg = questSystem.getQUESTS_IN_PROGRESS().getMessage(playerName);
                    }
                    if (msg != null) target.sendMessage(msg);
                }
            } else {
                QuestLoaderUtils.loadNewPlayerQuests(questSystem, playerName, activeQuests, 0);
            }
        }, 10);
    }

    /**
     * Load player quests.
     *
     * @param playerName       player.
     * @param quests           list of player quests.
     */
    private void loadPlayerQuests(QuestSystem questSystem, String playerName, LinkedHashMap<AbstractQuest, Progression> quests) {

        Debugger.addDebug("Entering loadPlayerQuests method for player " + playerName + ".");


        try {
            final Connection connection = sqlManager.getConnection();
            final String getQuestProgressionQuery = "SELECT * FROM " + questSystem.getProgressionTableName() + " WHERE PLAYERNAME = ?";

            final PreparedStatement preparedStatement = connection.prepareStatement(getQuestProgressionQuery);
            preparedStatement.setString(1, playerName);

            final ResultSet resultSet = preparedStatement.executeQuery();

            int id = 1;

            resultSet.next();
            do {
                int questId = resultSet.getInt("QUESTID");
                int advancement = resultSet.getInt("ADVANCEMENT");
                boolean isAchieved = resultSet.getBoolean("ISACHIEVED");

                Progression progression = new Progression(advancement, isAchieved);
                AbstractQuest quest = QuestLoaderUtils.findQuest(questSystem, playerName, questId, id);

                quests.put(quest, progression);

                id++;
            } while (resultSet.next() && id <= questSystem.getQuestsAmount());

            if (resultSet.next()) {
                PluginLogger.warn("Player " + playerName + " has more " + questSystem.getSystemName() + " quests than the configuration.");
                PluginLogger.warn("Only the first " + questSystem.getQuestsAmount() + " quests will be loaded.");
                PluginLogger.warn("After changing the number of quests, we recommend that you reset the progressions to avoid any problems.");
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            PluginLogger.error(ChatColor.RED + "An error occurred while loading player " + playerName + "'s " + questSystem.getSystemName() + " quests.");

            Debugger.addDebug("An error occurred while loading player " + playerName + "'s " + questSystem.getSystemName() + " quests.");
            Debugger.addDebug(e.getMessage());


            PluginLogger.error(e.getMessage());
        }

        Debugger.addDebug(questSystem.getSystemName() + " quests of player " + playerName + " have been loaded.");
    }
}