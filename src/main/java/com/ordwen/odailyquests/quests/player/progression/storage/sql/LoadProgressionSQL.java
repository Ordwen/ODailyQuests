package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.SQLQuery;
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
    public void loadProgression(String playerName, Map<String, PlayerQuests> activeQuests) {
        Debugger.write("Entering loadProgression (SQL) method for player " + playerName + ".");
        final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        ODailyQuests.morePaperLib.scheduling().asyncScheduler().runDelayed(() -> {
            Debugger.write("Running async task to load progression of " + playerName + " from SQL database.");

            boolean hasStoredData = false;
            long timestamp = 0;
            int achievedQuests = 0;
            int totalAchievedQuests = 0;

            try {
                final Connection connection = sqlManager.getConnection();

                final PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.TIMESTAMP_QUERY.getQuery());
                preparedStatement.setString(1, playerName);

                final ResultSet resultSet = preparedStatement.executeQuery();

                Debugger.write("Executing query for player " + playerName + ": " + SQLQuery.TIMESTAMP_QUERY.getQuery());

                if (resultSet.next()) {
                    hasStoredData = true;
                    timestamp = resultSet.getLong("player_timestamp");
                    achievedQuests = resultSet.getInt("achieved_quests");
                    totalAchievedQuests = resultSet.getInt("total_achieved_quests");

                    Debugger.write("Player " + playerName + " has stored data.");

                } else {
                    Debugger.write("Player " + playerName + " has no stored data.");
                }

                resultSet.close();
                preparedStatement.close();
                connection.close();

                Debugger.write("Database connection closed.");


            } catch (SQLException e) {
                PluginLogger.error(ChatColor.RED + "An error occurred while loading player " + playerName + "'s quests progression.");

                Debugger.write("An error occurred while loading player " + playerName + "'s quests progression.");
                Debugger.write(e.getMessage());

                PluginLogger.error(e.getMessage());
            }

            if (hasStoredData) {
                Debugger.write("Player " + playerName + " has data in the database.");

                if (QuestLoaderUtils.checkTimestamp(timestamp)) {
                    QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuests);
                } else {
                    loadPlayerQuests(playerName, quests);

                    PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    final Player target = Bukkit.getPlayer(playerName);
                    if (target == null) {
                        Debugger.write("Player " + playerName + " is null. Impossible to load quests.");
                        PluginLogger.warn("It looks like " + playerName + " has disconnected before his quests were loaded.");
                        return;
                    }

                    activeQuests.put(playerName, playerQuests);
                    PluginLogger.info(playerName + "'s quests have been loaded.");

                    final String msg;
                    if (achievedQuests == playerQuests.getQuests().size()) {
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

        Debugger.write("Entering loadPlayerQuests method for player " + playerName + ".");


        try {
            final Connection connection = sqlManager.getConnection();

            final PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.QUEST_PROGRESSION_QUERY.getQuery());
            preparedStatement.setString(1, playerName);

            final ResultSet resultSet = preparedStatement.executeQuery();

            int id = 1;

            resultSet.next();
            System.out.println("QuestsAmount.getQuestsAmount() = " + QuestsAmount.getQuestsAmount());
            System.out.println("resultSet length = " + resultSet.getFetchSize());

            do {
                int questIndex = resultSet.getInt("quest_index");
                int advancement = resultSet.getInt("advancement");
                boolean isAchieved = resultSet.getBoolean("is_achieved");

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

            Debugger.write("An error occurred while loading player " + playerName + "'s quests.");
            Debugger.write(e.getMessage());


            PluginLogger.error(e.getMessage());
        }

        Debugger.write("Quests of player " + playerName + " have been loaded.");
    }
}