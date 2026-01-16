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

/**
 * Loads a player's quest progression from an SQL database.
 * <p>
 * This loader queries the stored player metadata (timestamp, achieved quests, rerolls),
 * then either restores the saved quests and their progression or regenerates a new set
 * when no valid data is found or when the stored data is outdated.
 * <p>
 * The SQL work is executed asynchronously after a configurable delay to avoid
 * blocking the server thread during login bursts or heavy IO.
 */
public class LoadProgressionSQL extends ProgressionLoader {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Creates a new SQL-based progression loader.
     *
     * @param sqlManager the SQL manager used to get database connections and execute queries
     */
    public LoadProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Loads a player's progression from the SQL database.
     * <p>
     * The loading is scheduled asynchronously after the configured delay.
     * If the player is no longer online when the task runs, the loading is aborted.
     * <p>
     * If no valid stored data is found, new quests are generated for the player.
     *
     * @param playerName        the player's name
     * @param activeQuests      the current active quests map to populate/update
     * @param sendStatusMessage whether a status message should be sent to the player (when supported by the loader flow)
     */
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

    /**
     * Restores progression using the stored player data.
     * <p>
     * If the stored timestamp indicates the quests must be renewed, a fresh set of quests is generated.
     * If stored quest rows are missing or inconsistent with the current configuration, a fresh set is generated.
     *
     * @param player            the online player instance
     * @param activeQuests      the current active quests map to populate/update
     * @param data              stored player metadata loaded from the database
     * @param quests            the target map that will be filled with loaded quests and their progression
     * @param sendStatusMessage whether a status message should be sent to the player (when supported by the loader flow)
     */
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
     * Loads the player's stored quests and their progression rows from the database.
     * <p>
     * The method validates each row against the current quest definitions and configuration
     * (required amount, selected required index, schema compatibility checks).
     * <p>
     * When the stored rows are missing or invalid, the caller should regenerate a new quest set.
     *
     * @param player the player whose quests are being loaded
     * @param quests the target map that will be populated with loaded quests and progressions
     * @return true if quests were successfully loaded and validated, false if the data should be treated as invalid
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
     * Handles the case where the progression query returns no quest rows for the player.
     *
     * @param playerName the player's name
     * @return always false to indicate that quests must be regenerated
     */
    private boolean handleNoQuestRows(String playerName) {
        PluginLogger.warn("Player " + playerName + " has no stored quests. New quests will be drawn.");
        return false; // always false: no quests = regenerate a full set
    }

    /**
     * Loads and validates a single quest progression row.
     * <p>
     * This method:
     * - reads quest identifiers and progression data from the current ResultSet row
     * - runs schema compatibility checks
     * - resolves the quest definition from loaded categories
     * - validates stored values against the quest definition
     * - inserts a Progression entry into the provided map when valid
     *
     * @param resultSet  the result set positioned on the row to read
     * @param playerName the player's name (used for logs and error reporting)
     * @param quests     the target map to populate
     * @param questId    the sequential quest id used to resolve the quest definition within the player's drawn quests
     * @return true if the row is valid and was loaded, false if the stored data is inconsistent and must be regenerated
     * @throws SQLException if a JDBC access error occurs while reading the row
     */
    private boolean loadSingleQuestRow(ResultSet resultSet, String playerName, LinkedHashMap<AbstractQuest, Progression> quests, int questId) throws SQLException {
        final int questIndex = resultSet.getInt("quest_index");
        final String categoryName = resultSet.getString("category");
        final int advancement = resultSet.getInt("advancement");
        final int requiredAmount = resultSet.getInt("required_amount");

        Double rewardAmount = resultSet.getDouble("reward_amount");
        if (resultSet.wasNull()) {
            rewardAmount = null;
        }

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

        addQuestProgression(quests, quest, requiredAmount, rewardAmount, advancement, isAchieved, selectedRequired);
        return true;
    }

    /**
     * Validates that the stored required amount matches the quest definition when the quest does not use a random required amount.
     *
     * @param quest          the quest definition resolved from the configuration
     * @param requiredAmount the stored required amount
     * @param playerName     the player's name (used for logs and error reporting)
     * @return true if the stored value is compatible with the quest definition, false otherwise
     */
    private boolean isRequiredAmountValid(AbstractQuest quest, int requiredAmount, String playerName) {
        if (!quest.isRandomRequiredAmount() && requiredAmount != Integer.parseInt(quest.getRequiredAmountRaw())) {
            requiredAmountNotEqual(playerName);
            return false;
        }
        return true;
    }

    /**
     * Adds a progression entry to the loaded quests map.
     * <p>
     * If the stored selected required index is present (non -1), it is applied to the progression.
     *
     * @param quests           the target map to populate
     * @param quest            the quest definition
     * @param requiredAmount   the required amount for completion
     * @param rewardAmount     the reward amount, or null if not defined
     * @param advancement      the current advancement value
     * @param isAchieved       whether the quest is marked as achieved
     * @param selectedRequired the selected required index, or -1 when not applicable
     */
    private void addQuestProgression(LinkedHashMap<AbstractQuest, Progression> quests, AbstractQuest quest, int requiredAmount, Double rewardAmount, int advancement, boolean isAchieved, int selectedRequired) {
        final double resolvedRewardAmount = resolveRewardAmount(quest, rewardAmount);
        final Progression progression = new Progression(requiredAmount, resolvedRewardAmount, advancement, isAchieved);
        if (selectedRequired != -1) {
            progression.setSelectedRequiredIndex(selectedRequired);
        }

        quests.put(quest, progression);
    }

    /**
     * Resolves the reward amount for a quest, using the stored value if present.
     *
     * @param quest              the quest definition
     * @param storedRewardAmount the stored reward amount, or null if not defined
     * @return the resolved reward amount
     */
    private double resolveRewardAmount(AbstractQuest quest, Double storedRewardAmount) {
        if (storedRewardAmount != null) {
            return storedRewardAmount;
        }

        return quest.getReward().resolveRewardAmount();
    }


    /**
     * Loads per-category achieved quest statistics for a player.
     * <p>
     * The returned map associates a category identifier/name with the total number of achieved quests for that category.
     * Missing or failed queries return an empty map.
     *
     * @param playerUuid the player's UUID as a string
     * @return a map of category name to achieved quests count
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
