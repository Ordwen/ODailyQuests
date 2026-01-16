package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Logs;
import com.ordwen.odailyquests.enums.SQLQuery;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class SaveProgressionSQL {

    /* instance of SQLManager */
    private final SQLManager sqlManager;

    /**
     * Grouped player save data.
     */
    private record PlayerSaveData(
            String playerName,
            String playerUuid,
            long timestamp,
            int achievedQuests,
            int totalAchievedQuests,
            int recentRerolls,
            Map<AbstractQuest, Progression> quests,
            Map<String, Integer> totalAchievedByCategory
    ) {
    }

    /**
     * Constructor.
     *
     * @param sqlManager instance of MySQLManager.
     */
    public SaveProgressionSQL(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Save player quests progression.
     *
     * @param playerName       name of the player.
     * @param playerUuid       player uuid.
     * @param playerQuests     player quests.
     * @param isServerStopping whether the server is stopping or a migration is in progress.
     */
    public void saveProgression(String playerName, String playerUuid, PlayerQuests playerQuests, boolean isServerStopping) {
        if (playerQuests == null) {
            PluginLogger.warn("Impossible to save progression for player " + playerName + " because playerQuests is null.");
            PluginLogger.warn("It can happen if the server is starting/reloading and the player's quests are not loaded yet.");
            return;
        }

        Debugger.write("Entering saveProgression method for player " + playerName);

        final long timestamp = playerQuests.getTimestamp();
        final int achievedQuests = playerQuests.getAchievedQuests();
        final int totalAchievedQuests = playerQuests.getTotalAchievedQuests();
        final int recentRerolls = playerQuests.getRecentlyRolled();

        final Map<AbstractQuest, Progression> quests = playerQuests.getQuests();
        final Map<String, Integer> totalAchievedByCategory = playerQuests.getTotalAchievedQuestsByCategory();

        final PlayerSaveData data = new PlayerSaveData(
                playerName,
                playerUuid,
                timestamp,
                achievedQuests,
                totalAchievedQuests,
                recentRerolls,
                quests,
                totalAchievedByCategory
        );

        if (isServerStopping) {
            Debugger.write("Saving player " + playerName + " progression (server is stopping or migration is in progress).");
            saveDatas(data);
        } else {
            ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
                Debugger.write("Saving player " + playerName + " progression asynchronously");
                saveDatas(data);
            });
        }
    }

    /**
     * Save player quests progression.
     *
     * @param data grouped player save data.
     */
    private void saveDatas(PlayerSaveData data) {
        final String playerName = data.playerName();

        try (final Connection conn = sqlManager.getConnection()) {
            if (conn == null) {
                PluginLogger.error("Database connection unavailable");
                return;
            }
            saveDatasTransactional(conn, data);
        } catch (SQLException e) {
            Debugger.write("An error occurred while saving player " + playerName + " data (connection).");
            Debugger.write(e.getMessage());
            PluginLogger.error("An error occurred while saving player " + playerName + " data (connection).");
            PluginLogger.error(e.getMessage());
        }
    }

    private void saveDatasTransactional(Connection conn, PlayerSaveData data) throws SQLException {
        final String playerName = data.playerName();
        final String playerUuid = data.playerUuid();
        final long timestamp = data.timestamp();
        final int achievedQuests = data.achievedQuests();
        final int totalAchievedQuests = data.totalAchievedQuests();
        final int recentRerolls = data.recentRerolls();

        Map<AbstractQuest, Progression> quests = data.quests();
        Map<String, Integer> totalAchievedByCategory = data.totalAchievedByCategory();

        final boolean oldAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 0) Purge old progression data
            final String deleteProgressQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_DELETE_PROGRESS.getQuery()
                    : SQLQuery.SQLITE_DELETE_PROGRESS.getQuery();

            try (PreparedStatement deleteProgress = conn.prepareStatement(deleteProgressQuery)) {
                deleteProgress.setString(1, playerUuid);
                deleteProgress.executeUpdate();
                Debugger.write("Old progression rows cleared for player " + playerName);
            }

            // 0bis) Purge old category stats
            final String deleteCategoryQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_DELETE_PLAYER_CATEGORY_STATS.getQuery()
                    : SQLQuery.SQLITE_DELETE_PLAYER_CATEGORY_STATS.getQuery();

            try (PreparedStatement deleteCategory = conn.prepareStatement(deleteCategoryQuery)) {
                deleteCategory.setString(1, playerUuid);
                deleteCategory.executeUpdate();
                Debugger.write("Old category stats cleared for player " + playerName);
            }

            // 1) Save player main data
            final String playerQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_SAVE_PLAYER.getQuery()
                    : SQLQuery.SQLITE_SAVE_PLAYER.getQuery();

            try (PreparedStatement playerStatement = conn.prepareStatement(playerQuery)) {
                playerStatement.setString(1, playerUuid);
                playerStatement.setLong(2, timestamp);
                playerStatement.setInt(3, achievedQuests);
                playerStatement.setInt(4, totalAchievedQuests);
                playerStatement.setInt(5, recentRerolls);
                playerStatement.executeUpdate();

                Debugger.write("Player " + playerName + " data saved");
            }

            // 2) Save quests progression
            final String progressQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_SAVE_PROGRESS.getQuery()
                    : SQLQuery.SQLITE_SAVE_PROGRESS.getQuery();

            try (PreparedStatement progressionStatement = conn.prepareStatement(progressQuery)) {
                progressionStatement.setString(1, playerUuid);

                int index = 0;
                for (Map.Entry<AbstractQuest, Progression> entry : quests.entrySet()) {
                    final AbstractQuest quest = entry.getKey();
                    final Progression progression = entry.getValue();

                    progressionStatement.setInt(2, index);
                    progressionStatement.setInt(3, quest.getQuestIndex());
                    progressionStatement.setString(4, quest.getCategoryName());
                    progressionStatement.setInt(5, progression.getAdvancement());
                    progressionStatement.setInt(6, progression.getRequiredAmount());
                    progressionStatement.setDouble(7, resolveRewardAmount(quest, progression));
                    progressionStatement.setBoolean(8, progression.isAchieved());
                    progressionStatement.setInt(9, progression.getSelectedRequiredIndex());

                    progressionStatement.addBatch();

                    Debugger.write("Quest number " + index + " saved for player " + playerName);
                    index++;
                }

                progressionStatement.executeBatch();
                Debugger.write(playerName + " quests progression saved");
            }

            // 3) Save stats by category
            final String categoryQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_SAVE_PLAYER_CATEGORY_STATS.getQuery()
                    : SQLQuery.SQLITE_SAVE_PLAYER_CATEGORY_STATS.getQuery();

            try (PreparedStatement categoryStatement = conn.prepareStatement(categoryQuery)) {
                categoryStatement.setString(1, playerUuid);

                for (Map.Entry<String, Integer> entry : totalAchievedByCategory.entrySet()) {
                    categoryStatement.setString(2, entry.getKey());
                    categoryStatement.setInt(3, entry.getValue());
                    categoryStatement.addBatch();
                }

                categoryStatement.executeBatch();
                Debugger.write(playerName + "'s category stats saved.");
            }

            conn.commit();
            if (Logs.isEnabled()) {
                PluginLogger.info(playerName + "'s data saved.");
            }

        } catch (SQLException e) {
            conn.rollback();
            Debugger.write("An error occurred while saving player " + playerName + " data (rolled back).");
            Debugger.write(e.getMessage());
            PluginLogger.error("An error occurred while saving player " + playerName + " data.");
            PluginLogger.error(e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(oldAutoCommit);
        }
    }

    /**
     * Resolve and cache the reward amount for a quest progression.
     *
     * @param quest       the quest.
     * @param progression the progression.
     * @return the resolved reward amount.
     */
    private double resolveRewardAmount(AbstractQuest quest, Progression progression) {
        if (progression.hasRewardAmount()) {
            return progression.getRewardAmount();
        }

        final double resolved = quest.getReward().resolveRewardAmount();
        progression.setRewardAmount(resolved);
        return resolved;
    }
}
