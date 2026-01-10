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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoadProgressionSQL extends ProgressionLoader {

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

    public void loadProgression(String playerName, Map<String, PlayerQuests> activeQuests, boolean sendStatusMessage) {
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
            StoredPlayerProgression data = null;

            try (final Connection connection = sqlManager.getConnection();
                 final PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.LOAD_PLAYER.getQuery())) {

                preparedStatement.setString(1, playerUuid);

                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    Debugger.write("Executing query for player " + playerName + ": " + SQLQuery.LOAD_PLAYER.getQuery());

                    if (resultSet.next()) {
                        hasStoredData = true;

                        final long timestamp = resultSet.getLong("player_timestamp");
                        final int achievedQuests = resultSet.getInt("achieved_quests");
                        final int totalAchievedQuests = resultSet.getInt("total_achieved_quests");
                        final int recentRerolls = resultSet.getInt("recent_rerolls");

                        data = new StoredPlayerProgression(
                                timestamp,
                                achievedQuests,
                                totalAchievedQuests,
                                recentRerolls
                        );

                        Debugger.write(playerName + " has stored data.");
                    } else {
                        Debugger.write(playerName + " has no stored data.");
                    }
                }

                Debugger.write("Database connection closed.");
            } catch (SQLException e) {
                error(playerName, e.getMessage());
            }

            if (hasStoredData && data != null) {
                loadStoredData(player, activeQuests, data, quests, sendStatusMessage);
            } else {
                QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, new HashMap<>(), 0);
            }
        }, Duration.ofMillis(PlayerDataLoadDelay.getDelay()));
    }

    private void loadStoredData(
            Player player,
            Map<String, PlayerQuests> activeQuests,
            StoredPlayerProgression data,
            LinkedHashMap<AbstractQuest, Progression> quests,
            boolean sendStatusMessage
    ) {
        final String playerName = player.getName();

        Debugger.write(playerName + " has data in the database.");

        final Map<String, Integer> categoryStats = loadCategoryStats(player.getUniqueId().toString());

        if (QuestLoaderUtils.checkTimestamp(data.timestamp())) {
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, categoryStats, data.totalAchievedQuests());
            return;
        }

        if (!loadPlayerQuests(player, quests)) {
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, categoryStats, data.totalAchievedQuests());
            return;
        }

        registerLoadedPlayerQuests(player, activeQuests, categoryStats, quests, data, sendStatusMessage);
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
             final PreparedStatement preparedStatement = connection.prepareStatement(SQLQuery.LOAD_PROGRESS.getQuery())) {

            preparedStatement.setString(1, player.getUniqueId().toString());

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                int id = 1;

                if (!resultSet.next()) {
                    return handleNoQuestRows(playerName);
                }

                final int maxQuests = QuestsPerCategory.getTotalQuestsAmount(player);
                do {
                    if (!loadSingleQuestRow(resultSet, playerName, quests, id)) {
                        return false;
                    }
                    id++;
                } while (resultSet.next() && id <= maxQuests);

                final int loadedQuests = id - 1;
                Debugger.write("Loaded " + loadedQuests + " quests for " + playerName + " (config max: " + maxQuests + ").");
            }
        } catch (final SQLException e) {
            error(playerName, e.getMessage());
            return false;
        }

        Debugger.write("Quests of player " + playerName + " have been loaded.");
        return true;
    }

    /**
     * Handle case when no quest rows are returned.
     */
    private boolean handleNoQuestRows(String playerName) {
        PluginLogger.warn("Player " + playerName + " has no stored quests. New quests will be drawn.");
        return false; // always false: no quests = regenerate a full set
    }

    /**
     * Load and validate a single quest row from the ResultSet.
     */
    private boolean loadSingleQuestRow(ResultSet resultSet, String playerName, LinkedHashMap<AbstractQuest, Progression> quests, int questId) throws SQLException {
        final int questIndex = resultSet.getInt("quest_index");
        final String categoryName = resultSet.getString("category");
        final int advancement = resultSet.getInt("advancement");
        final int requiredAmount = resultSet.getInt("required_amount");
        int selectedRequired = resultSet.getInt("selected_required");
        if (resultSet.wasNull()) {
            selectedRequired = -1;
        }

        // schema update check (1 to 2)
        if (requiredAmount == 0) {
            requiredAmountIsZero(playerName);
            return false;
        }

        final boolean isAchieved = resultSet.getBoolean("is_achieved");

        final AbstractQuest quest = QuestLoaderUtils.findQuest(playerName, categoryName, questIndex, questId);
        if (quest == null) {
            Debugger.write("Quest " + questId + " does not exist. New quests will be drawn.");
            return false;
        }

        if (!isRequiredAmountValid(quest, requiredAmount, playerName)) {
            return false;
        }

        if (isSelectedRequiredInvalid(quest, selectedRequired, playerName)) {
            return false;
        }

        addQuestProgression(quests, quest, requiredAmount, advancement, isAchieved, selectedRequired);
        return true;
    }

    /**
     * Validate that the required amount matches the quest definition when not random.
     */
    private boolean isRequiredAmountValid(AbstractQuest quest, int requiredAmount, String playerName) {
        if (!quest.isRandomRequiredAmount() && requiredAmount != Integer.parseInt(quest.getRequiredAmountRaw())) {
            requiredAmountNotEqual(playerName);
            return false;
        }
        return true;
    }

    /**
     * Add the quest progression entry to the map.
     */
    private void addQuestProgression(LinkedHashMap<AbstractQuest, Progression> quests, AbstractQuest quest, int requiredAmount, int advancement, boolean isAchieved, int selectedRequired) {
        final Progression progression = new Progression(requiredAmount, advancement, isAchieved);
        if (selectedRequired != -1) {
            progression.setSelectedRequiredIndex(selectedRequired);
        }

        quests.put(quest, progression);
    }

    /**
     * Load player category stats.
     *
     * @param playerUuid UUID of the player.
     * @return Map of category stats.
     */
    private Map<String, Integer> loadCategoryStats(String playerUuid) {
        final Map<String, Integer> categoryStats = new HashMap<>();

        try (final Connection connection = sqlManager.getConnection();
             final PreparedStatement statement = connection.prepareStatement(SQLQuery.LOAD_PLAYER_CATEGORY_STATS.getQuery())) {

            statement.setString(1, playerUuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final String category = resultSet.getString("category");
                    final int count = resultSet.getInt("total_achieved_quests");
                    categoryStats.put(category, count);
                }
            }
        } catch (SQLException e) {
            PluginLogger.error("Failed to load category stats for player " + playerUuid + ": " + e.getMessage());
        }

        return categoryStats;
    }
}
