package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.PlayerDataLoadDelay;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.enums.SQLQuery;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.ProgressionLoader;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoadProgressionSQL extends ProgressionLoader {

    private static final String NEW_QUESTS = "New quests will be drawn.";
    private static final String CONFIG_CHANGE = "This can happen if the quest has been modified in the config file.";

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

            final Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                handlePlayerDisconnected(playerName);
                return;
            }

            final String playerUuid = player.getUniqueId().toString();

            boolean hasStoredData = false;
            long timestamp = 0;
            int achievedQuests = 0;
            int totalAchievedQuests = 0;

            try (final Connection connection = sqlManager.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.TIMESTAMP_QUERY.getQuery())) {

                preparedStatement.setString(1, playerUuid);

                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    Debugger.write("Executing query for player " + playerName + ": " + SQLQuery.TIMESTAMP_QUERY.getQuery());

                    if (resultSet.next()) {
                        hasStoredData = true;
                        timestamp = resultSet.getLong("player_timestamp");
                        achievedQuests = resultSet.getInt("achieved_quests");
                        totalAchievedQuests = resultSet.getInt("total_achieved_quests");

                        Debugger.write(playerName + " has stored data.");
                    } else {
                        Debugger.write(playerName + " has no stored data.");
                    }
                }

                Debugger.write("Database connection closed.");
            } catch (SQLException e) {
                error(playerName, e.getMessage());
            }

            if (hasStoredData) {
                loadStoredData(player, activeQuests, timestamp, totalAchievedQuests, quests, achievedQuests);
            } else {
                QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, 0);
            }
        }, Duration.ofMillis(PlayerDataLoadDelay.getDelay()));
    }

    private void loadStoredData(Player player, Map<String, PlayerQuests> activeQuests, long timestamp, int totalAchievedQuests, LinkedHashMap<AbstractQuest, Progression> quests, int achievedQuests) {
        final String playerName = player.getName();

        Debugger.write(playerName + " has data in the database.");

        if (QuestLoaderUtils.checkTimestamp(timestamp)) {
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuests);
        } else {
            if (!loadPlayerQuests(player, quests)) {
                QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuests);
                return;
            }

            final PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
            playerQuests.setAchievedQuests(achievedQuests);
            playerQuests.setTotalAchievedQuests(totalAchievedQuests);

            activeQuests.put(playerName, playerQuests);
            PluginLogger.info(playerName + "'s quests have been loaded.");

            sendQuestStatusMessage(player, achievedQuests, playerQuests);
        }
    }

    /**
     * Load player quests.
     *
     * @param player player.
     * @param quests list of player quests.
     */
    private boolean loadPlayerQuests(Player player, LinkedHashMap<AbstractQuest, Progression> quests) {
        final String playerName = player.getName();

        Debugger.write("Entering loadPlayerQuests method for player " + playerName + ".");

        try (final Connection connection = sqlManager.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.QUEST_PROGRESSION_QUERY.getQuery())) {

            preparedStatement.setString(1, player.getUniqueId().toString());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                int id = 1;
                if (resultSet.next()) {
                    do {
                        final int questIndex = resultSet.getInt("quest_index");
                        final int advancement = resultSet.getInt("advancement");
                        final int requiredAmount = resultSet.getInt("required_amount");
                        int selectedRequired = resultSet.getInt("selected_required");
                        if (resultSet.wasNull()) {
                            selectedRequired = -1;
                        }

                        final AbstractQuest quest = QuestLoaderUtils.findQuest(playerName, questIndex, id);
                        if (quest == null) {
                            Debugger.write("Quest " + id + " does not exist. New quests will be drawn.");
                            return false;
                        }

                        // check if random quest have data
                        if (isSelectedRequiredInvalid(quest, selectedRequired, playerName)) return false;

                        // schema update check (1 to 2)
                        if (requiredAmount == 0) {
                            PluginLogger.warn("Required amount is 0 for player " + playerName + ". " + NEW_QUESTS);
                            PluginLogger.warn(CONFIG_CHANGE);
                            return false;
                        }

                        final boolean isAchieved = resultSet.getBoolean("is_achieved");

                        final Progression progression = new Progression(requiredAmount, advancement, isAchieved);
                        if (selectedRequired != -1) {
                            progression.setSelectedRequiredIndex(selectedRequired);
                        }

                        quests.put(quest, progression);
                        id++;
                    } while (resultSet.next() && id <= QuestsPerCategory.getTotalQuestsAmount());

                    if (resultSet.next()) {
                        logExcessQuests(playerName);
                    }
                }
            }
        } catch (final SQLException e) {
            error(playerName, e.getMessage());
        }

        Debugger.write("Quests of player " + playerName + " have been loaded.");
        return true;
    }
}
