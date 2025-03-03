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
     * @param playerName   name of the player.
     * @param playerUuid   player uuid.
     * @param playerQuests player quests.
     */
    public void saveProgression(String playerName, String playerUuid, PlayerQuests playerQuests, boolean isServerStopping) {
        if (playerQuests == null) {
            PluginLogger.warn("Impossible to save progression for player " + playerName + " because playerQuests is null.");
            PluginLogger.warn("It can happen if the server is starting/reloading and the player's quests are not loaded yet.");
            return;
        }

        Debugger.write("Entering saveProgression method for player " + playerName);

        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

        final Map<AbstractQuest, Progression> quests = playerQuests.getQuests();

        if (isServerStopping) {
            Debugger.write("Saving player " + playerName + " progression (server is stopping).");
            saveDatas(playerName, playerUuid, timestamp, achievedQuests, totalAchievedQuests, quests);
        } else {
            ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
                Debugger.write("Saving player " + playerName + " progression asynchronously");

                saveDatas(playerName, playerUuid, timestamp, achievedQuests, totalAchievedQuests, quests);
            });
        }
    }

    /**
     * Save player quests progression.
     *
     * @param playerName          name of the player.
     * @param playerUuid          player uuid.
     * @param timestamp           timestamp.
     * @param achievedQuests      achieved quests.
     * @param totalAchievedQuests total achieved quests.
     * @param quests              quests.
     */
    private void saveDatas(String playerName, String playerUuid, long timestamp, int achievedQuests, int totalAchievedQuests, Map<AbstractQuest, Progression> quests) {
        final Connection connection = sqlManager.getConnection();
        if (connection == null) {
            PluginLogger.error("Database connection unavailable");
            return;
        }

        try (Connection conn = connection) {
            final String playerQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_PLAYER_QUERY.getQuery()
                    : SQLQuery.H2_PLAYER_QUERY.getQuery();

            try (PreparedStatement playerStatement = conn.prepareStatement(playerQuery)) {
                playerStatement.setString(1, playerUuid);
                playerStatement.setLong(2, timestamp);
                playerStatement.setInt(3, achievedQuests);
                playerStatement.setInt(4, totalAchievedQuests);
                playerStatement.executeUpdate();
                Debugger.write("Player " + playerName + " data saved");
            }

            final String progressQuery = (Database.getMode() == StorageMode.MYSQL)
                    ? SQLQuery.MYSQL_PROGRESS_UPDATE.getQuery()
                    : SQLQuery.H2_PROGRESS_UPDATE.getQuery();

            try (PreparedStatement progressionStatement = conn.prepareStatement(progressQuery)) {
                progressionStatement.setString(1, playerUuid);

                int index = 0;
                for (Map.Entry<AbstractQuest, Progression> entry : quests.entrySet()) {
                    final AbstractQuest quest = entry.getKey();
                    final Progression progression = entry.getValue();

                    progressionStatement.setInt(2, index);
                    progressionStatement.setInt(3, quest.getQuestIndex());
                    progressionStatement.setInt(4, progression.getProgression());
                    progressionStatement.setBoolean(5, progression.isAchieved());
                    progressionStatement.addBatch();

                    Debugger.write("Quest number " + index + " saved for player " + playerName);
                    index++;
                }
                progressionStatement.executeBatch();
                Debugger.write(playerName + " quests progression saved");
            }

            if (Logs.isEnabled()) {
                PluginLogger.info(playerName + "'s data saved.");
            }
        } catch (SQLException e) {
            Debugger.write("An error occurred while saving player " + playerName + " data.");
            Debugger.write(e.getMessage());
            PluginLogger.error("An error occurred while saving player " + playerName + " data.");
            PluginLogger.error(e.getMessage());
        }
    }
}
