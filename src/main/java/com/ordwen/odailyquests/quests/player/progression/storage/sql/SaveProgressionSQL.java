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
     * Save player quests progression using an existing connection.
     * Mainly used during data migration.
     *
     * @param conn         existing SQL connection.
     * @param playerName   name of the player.
     * @param playerUuid   player uuid.
     * @param playerQuests player quests.
     * @throws SQLException if a database access error occurs.
     */
    public void saveProgression(Connection conn, String playerName, String playerUuid, PlayerQuests playerQuests) throws SQLException {
        Debugger.write("[SaveProgressionSQL#saveProgression(conn)] START playerName=" + playerName + " uuid=" + playerUuid
                + " | conn=" + safeConnId(conn)
                + " | playerQuestsNull=" + (playerQuests == null));

        if (playerQuests == null) {
            Debugger.write("[SaveProgressionSQL#saveProgression(conn)] playerQuests=null -> RETURN");
            return;
        }

        Debugger.write("[SaveProgressionSQL#saveProgression(conn)] Building PlayerSaveData...");
        final PlayerSaveData data = new PlayerSaveData(
                playerName,
                playerUuid,
                playerQuests.getTimestamp(),
                playerQuests.getAchievedQuests(),
                playerQuests.getTotalAchievedQuests(),
                playerQuests.getRecentlyRolled(),
                playerQuests.getQuests(),
                playerQuests.getTotalAchievedQuestsByCategory()
        );

        Debugger.write("[SaveProgressionSQL#saveProgression(conn)] Data built: ts=" + data.timestamp()
                + " achieved=" + data.achievedQuests()
                + " totalAchieved=" + data.totalAchievedQuests()
                + " recentRerolls=" + data.recentRerolls()
                + " quests.size=" + safeSize(data.quests())
                + " categoryStats.size=" + safeSize(data.totalAchievedByCategory()));

        Debugger.write("[SaveProgressionSQL#saveProgression(conn)] Calling saveDatasTransactional(purgeOld=false, manageTransaction=false)...");
        long t = System.nanoTime();
        saveDatasTransactional(conn, data, false, false);
        Debugger.write("[SaveProgressionSQL#saveProgression(conn)] END OK in " + ((System.nanoTime() - t) / 1_000_000L) + "ms");
    }

    private static int safeSize(Map<?, ?> map) {
        return map == null ? -1 : map.size();
    }

    private static String safeConnId(Connection c) {
        if (c == null) return "null";
        return c.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(c));
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
            saveDatasTransactional(conn, data, true, true);
        } catch (SQLException e) {
            Debugger.write("An error occurred while saving player " + playerName + " data (connection).");
            Debugger.write(e.getMessage());
            PluginLogger.error("An error occurred while saving player " + playerName + " data (connection).");
            PluginLogger.error(e.getMessage());
        }
    }

    /**
     * Save player quests progression within a transaction.
     *
     * @param conn              existing SQL connection.
     * @param data              grouped player save data.
     * @param purgeOld          whether to purge old data before saving new data.
     * @param manageTransaction whether to manage the transaction (commit/rollback) within this method.
     * @throws SQLException if a database access error occurs.
     */

    private void saveDatasTransactional(Connection conn, PlayerSaveData data, boolean purgeOld, boolean manageTransaction) throws SQLException {
        final String playerName = data.playerName();
        final String playerUuid = data.playerUuid();

        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] START player=" + playerName + " uuid=" + playerUuid
                + " | purgeOld=" + purgeOld
                + " | manageTransaction=" + manageTransaction
                + " | conn=" + safeConnId(conn));

        final long tGlobal = System.nanoTime();

        final long timestamp = data.timestamp();
        final int achievedQuests = data.achievedQuests();
        final int totalAchievedQuests = data.totalAchievedQuests();
        final int recentRerolls = data.recentRerolls();

        final Map<AbstractQuest, Progression> quests = data.quests();
        final Map<String, Integer> totalAchievedByCategory = data.totalAchievedByCategory();

        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] Data snapshot: ts=" + timestamp
                + " achieved=" + achievedQuests
                + " totalAchieved=" + totalAchievedQuests
                + " recentRerolls=" + recentRerolls
                + " quests.size=" + safeSize(quests)
                + " categoryStats.size=" + safeSize(totalAchievedByCategory));

        final boolean oldAutoCommit = safeGetAutoCommit(conn);
        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] conn.getAutoCommit()=" + oldAutoCommit);

        if (manageTransaction) {
            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] Setting autoCommit=false...");
            long t = System.nanoTime();
            conn.setAutoCommit(false);
            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] autoCommit=false OK in " + ms(t) + "ms");
        }

        try {
            if (purgeOld) {
                // 0) Purge old progression data
                final String deleteProgressQuery = (Database.getMode() == StorageMode.MYSQL)
                        ? SQLQuery.MYSQL_DELETE_PROGRESS.getQuery()
                        : SQLQuery.SQLITE_DELETE_PROGRESS.getQuery();

                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE progress START query=" + shortQuery(deleteProgressQuery));
                long tDel1 = System.nanoTime();
                try (PreparedStatement deleteProgress = conn.prepareStatement(deleteProgressQuery)) {
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE progress prepared. setString(1, uuid)...");
                    deleteProgress.setString(1, playerUuid);
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE progress executeUpdate()...");
                    int rows = deleteProgress.executeUpdate();
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE progress END rows=" + rows + " in " + ms(tDel1) + "ms");
                }

                // 0bis) Purge old category stats
                final String deleteCategoryQuery = (Database.getMode() == StorageMode.MYSQL)
                        ? SQLQuery.MYSQL_DELETE_PLAYER_CATEGORY_STATS.getQuery()
                        : SQLQuery.SQLITE_DELETE_PLAYER_CATEGORY_STATS.getQuery();

                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE category START query=" + shortQuery(deleteCategoryQuery));
                long tDel2 = System.nanoTime();
                try (PreparedStatement deleteCategory = conn.prepareStatement(deleteCategoryQuery)) {
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE category prepared. setString(1, uuid)...");
                    deleteCategory.setString(1, playerUuid);
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE category executeUpdate()...");
                    int rows = deleteCategory.executeUpdate();
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] PURGE category END rows=" + rows + " in " + ms(tDel2) + "ms");
                }
            } else {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] purgeOld=false -> skipping purge queries");
            }

            // 1) Save player main data
            final String playerQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_SAVE_PLAYER.getQuery()
                    : SQLQuery.SQLITE_SAVE_PLAYER.getQuery();

            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE player START query=" + shortQuery(playerQuery));
            long tPlayer = System.nanoTime();
            try (PreparedStatement playerStatement = conn.prepareStatement(playerQuery)) {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE player prepared. Binding params...");
                playerStatement.setString(1, playerUuid);
                playerStatement.setLong(2, timestamp);
                playerStatement.setInt(3, achievedQuests);
                playerStatement.setInt(4, totalAchievedQuests);
                playerStatement.setInt(5, recentRerolls);

                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE player executeUpdate()...");
                int rows = playerStatement.executeUpdate();
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE player END rows=" + rows + " in " + ms(tPlayer) + "ms");
            }

            // 2) Save quests progression
            final String progressQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_SAVE_PROGRESS.getQuery()
                    : SQLQuery.SQLITE_SAVE_PROGRESS.getQuery();

            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE progress START query=" + shortQuery(progressQuery)
                    + " | quests.size=" + safeSize(quests));
            long tProg = System.nanoTime();

            try (PreparedStatement progressionStatement = conn.prepareStatement(progressQuery)) {
                progressionStatement.setString(1, playerUuid);

                int index = 0;

                if (quests == null) {
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] WARNING quests map is null -> no progression rows will be inserted");
                } else {
                    for (Map.Entry<AbstractQuest, Progression> entry : quests.entrySet()) {
                        final AbstractQuest quest = entry.getKey();
                        final Progression progression = entry.getValue();

                        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] progress.addBatch START idx=" + index
                                + " questIndex=" + (quest == null ? "null" : quest.getQuestIndex())
                                + " category=" + (quest == null ? "null" : quest.getCategoryName()));

                        progressionStatement.setInt(2, index);
                        progressionStatement.setInt(3, quest.getQuestIndex());
                        progressionStatement.setString(4, quest.getCategoryName());
                        progressionStatement.setInt(5, progression.getAdvancement());
                        progressionStatement.setInt(6, progression.getRequiredAmount());

                        // reward resolve can be slow if it does something non-trivial; log around it
                        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] resolveRewardAmount START idx=" + index);
                        long tReward = System.nanoTime();
                        double reward = resolveRewardAmount(quest, progression);
                        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] resolveRewardAmount END idx=" + index + " reward=" + reward + " in " + ms(tReward) + "ms");

                        progressionStatement.setDouble(7, reward);
                        progressionStatement.setBoolean(8, progression.isAchieved());
                        progressionStatement.setInt(9, progression.getSelectedRequiredIndex());

                        progressionStatement.addBatch();
                        Debugger.write("[SaveProgressionSQL#saveDatasTransactional] progress.addBatch END idx=" + index);

                        index++;
                    }
                }

                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] progress.executeBatch() START (batches=" + (quests == null ? 0 : quests.size()) + ")");
                long tExecBatch = System.nanoTime();
                int[] res = progressionStatement.executeBatch();
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] progress.executeBatch() END (len=" + (res == null ? "null" : res.length) + ") in " + ms(tExecBatch) + "ms");
            }

            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE progress END total in " + ms(tProg) + "ms");

            // 3) Save stats by category
            final String categoryQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_SAVE_PLAYER_CATEGORY_STATS.getQuery()
                    : SQLQuery.SQLITE_SAVE_PLAYER_CATEGORY_STATS.getQuery();

            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE categoryStats START query=" + shortQuery(categoryQuery)
                    + " | categoryStats.size=" + safeSize(totalAchievedByCategory));
            long tCat = System.nanoTime();

            try (PreparedStatement categoryStatement = conn.prepareStatement(categoryQuery)) {
                categoryStatement.setString(1, playerUuid);

                if (totalAchievedByCategory == null) {
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] WARNING totalAchievedByCategory is null -> no rows inserted");
                } else {
                    for (Map.Entry<String, Integer> entry : totalAchievedByCategory.entrySet()) {
                        categoryStatement.setString(2, entry.getKey());
                        categoryStatement.setInt(3, entry.getValue());
                        categoryStatement.addBatch();
                    }
                }

                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] categoryStats.executeBatch() START (batches=" + (totalAchievedByCategory == null ? 0 : totalAchievedByCategory.size()) + ")");
                long tExec = System.nanoTime();
                int[] res = categoryStatement.executeBatch();
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] categoryStats.executeBatch() END (len=" + (res == null ? "null" : res.length) + ") in " + ms(tExec) + "ms");
            }

            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] SAVE categoryStats END total in " + ms(tCat) + "ms");

            if (manageTransaction) {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] conn.commit() START");
                long tCommit = System.nanoTime();
                conn.commit();
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] conn.commit() END in " + ms(tCommit) + "ms");
            } else {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] manageTransaction=false -> NOT committing here");
            }

            if (Logs.isEnabled()) {
                PluginLogger.info(playerName + "'s data saved.");
            }

            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] END OK total=" + ms(tGlobal) + "ms");

        } catch (SQLException e) {
            Debugger.write("[SaveProgressionSQL#saveDatasTransactional] CATCH SQLException: " + e.getMessage());

            if (manageTransaction) {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] conn.rollback() START");
                try {
                    long tRb = System.nanoTime();
                    conn.rollback();
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] conn.rollback() END in " + ms(tRb) + "ms");
                } catch (SQLException rbEx) {
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] conn.rollback() FAILED: " + rbEx.getMessage());
                    throw rbEx;
                }
            } else {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] manageTransaction=false -> NOT rolling back here");
            }

            Debugger.write("An error occurred while saving player " + playerName + " data.");
            Debugger.write(e.getMessage());
            PluginLogger.error("An error occurred while saving player " + playerName + " data.");
            PluginLogger.error(e.getMessage());
            throw e;

        } finally {
            if (manageTransaction) {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] Restoring autoCommit to old value=" + oldAutoCommit + " START");
                long tRestore = System.nanoTime();
                try {
                    conn.setAutoCommit(oldAutoCommit);
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] Restoring autoCommit END in " + ms(tRestore) + "ms");
                } catch (SQLException ex) {
                    Debugger.write("[SaveProgressionSQL#saveDatasTransactional] Restoring autoCommit FAILED: " + ex.getMessage());
                    throw ex;
                }
            } else {
                Debugger.write("[SaveProgressionSQL#saveDatasTransactional] manageTransaction=false -> leaving autocommit as-is");
            }
        }
    }

    private static String shortQuery(String q) {
        if (q == null) return "null";
        String oneLine = q.replace('\n', ' ').replace('\r', ' ').trim();
        return oneLine.length() > 140 ? oneLine.substring(0, 140) + "..." : oneLine;
    }

    private static long ms(long nanoStart) {
        return (System.nanoTime() - nanoStart) / 1_000_000L;
    }

    private static boolean safeGetAutoCommit(Connection conn) {
        try {
            return conn.getAutoCommit();
        } catch (SQLException e) {
            Debugger.write("[SaveProgressionSQL] getAutoCommit() FAILED: " + e.getMessage());
            return true; // fallback
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
