package com.ordwen.odailyquests.tools.updater.database.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.DatabaseUpdater;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Update0to1 extends DatabaseUpdater {

    private static final String SELECT_ALL_FROM_PLAYER_TABLE = """
            SELECT PLAYERNAME, PLAYERTIMESTAMP, ACHIEVEDQUESTS, TOTALACHIEVEDQUESTS
            FROM `PLAYER`;
            """;

    private static final String COUNT_PLAYER_OLD = """
            SELECT COUNT(*) FROM `PLAYER`;
            """;

    private static final String COUNT_PLAYER_NEW = """
            SELECT COUNT(*) FROM `odq_player`;
            """;

    /**
     * Internal exception to signal a migration failure.
     */
    private static final class MigrationFailedException extends RuntimeException {
        MigrationFailedException(String message, Throwable cause) {
            super(message, cause);
        }

        MigrationFailedException(String message) {
            super(message);
        }
    }

    public Update0to1(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        boolean success = false;

        try {
            if (Database.getMode() == StorageMode.SQLITE || Database.getMode() == StorageMode.MYSQL) {
                applyMySQL();
            } else if (Database.getMode() == StorageMode.YAML) {
                applyYAML();
            } else {
                PluginLogger.info("No database update required for storage mode: " + Database.getMode());
            }

            success = true;
        } catch (MigrationFailedException ex) {
            PluginLogger.error("Database update 0->1 failed; version not updated.");
            PluginLogger.error(ex.getMessage());
            if (ex.getCause() != null) {
                PluginLogger.error(ex.getCause().getMessage());
            }
        } catch (Exception ex) {
            PluginLogger.error("Database update 0->1 failed unexpectedly; version not updated.");
            PluginLogger.error(ex.getMessage());
        }

        if (success) {
            updateVersion(version);
        }
    }

    // --------------------------------------------------------------------------------------------
    // SQL MIGRATION WITH HEAVY DEBUG TRACING
    // --------------------------------------------------------------------------------------------

    @Override
    public void applyMySQL() {
        Debugger.write("[0->1][SQL] ====== Starting SQL data conversion ======");
        final StorageMode currentMode = Database.getMode();
        Debugger.write("[0->1][SQL] StorageMode=" + currentMode + " (legacy read source=" + ((currentMode == StorageMode.SQLITE) ? "H2" : "SQLManager") + ")");

        // necessary to load the H2 driver for some unknown reason
        Debugger.write("[0->1][SQL] Loading H2 driver (best effort)...");
        try {
            Class.forName("org.h2.Driver");
            Debugger.write("[0->1][SQL] H2 driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            PluginLogger.error("Failed to load H2 driver.");
            PluginLogger.error(e.getMessage());
            Debugger.write("[0->1][SQL] H2 driver NOT found (not fatal for MySQL).");
        }

        // 1) Count old players (best effort; do NOT fail the migration if this count fails)
        int oldPlayerCount = -1;
        Debugger.write("[0->1][SQL] Counting old players (COUNT_PLAYER_OLD)...");
        long tCountOldStart = System.nanoTime();
        try {
            Debugger.write("[0->1][SQL] Opening legacy connection for COUNT...");
            try (Connection c = legacyConnection(currentMode)) {
                Debugger.write("[0->1][SQL] Legacy connection acquired for COUNT. Preparing statement...");
                try (PreparedStatement st = c.prepareStatement(COUNT_PLAYER_OLD)) {
                    Debugger.write("[0->1][SQL] Executing COUNT_PLAYER_OLD query...");
                    try (ResultSet rs = st.executeQuery()) {
                        Debugger.write("[0->1][SQL] COUNT_PLAYER_OLD executed. Reading result...");
                        if (rs.next()) oldPlayerCount = rs.getInt(1);
                    }
                }
            }
            Debugger.write("[0->1][SQL] Old players counted: " + oldPlayerCount + " (took " + millisSince(tCountOldStart) + "ms)");
        } catch (SQLException e) {
            PluginLogger.error("An error has occurred while trying to count players in the old SQL table.");
            PluginLogger.error(e.getMessage());
            Debugger.write("[0->1][SQL] Old player count failed after " + millisSince(tCountOldStart) + "ms: " + e.getMessage());
        }

        // 2) Migrate with one write connection + batch commits
        Debugger.write("[0->1][SQL] Preparing migration resources (read connection + select + write connection)...");
        long tSetupStart = System.nanoTime();

        try (
                // READ side
                Connection read = legacyConnection(currentMode);
                PreparedStatement st = read.prepareStatement(SELECT_ALL_FROM_PLAYER_TABLE);
                ResultSet rs = st.executeQuery();

                // WRITE side
                Connection write = databaseManager.getSqlManager().getConnection()
        ) {
            Debugger.write("[0->1][SQL] Resources acquired (took " + millisSince(tSetupStart) + "ms).");
            Debugger.write("[0->1][SQL] readConn=" + safeConnId(read) + " | writeConn=" + safeConnId(write));

            if (write == null) {
                Debugger.write("[0->1][SQL] write connection is NULL -> abort");
                throw new MigrationFailedException("Database connection unavailable for new schema (write connection).");
            }

            Debugger.write("[0->1][SQL] Disabling autocommit on write connection...");
            long tAutoCommitStart = System.nanoTime();
            write.setAutoCommit(false);
            Debugger.write("[0->1][SQL] write.setAutoCommit(false) OK (took " + millisSince(tAutoCommitStart) + "ms)");

            int converted = 0;
            int skipped = 0;
            int rowIndex = 0;

            // Timings bucket (cheap aggregate, avoids spamming per-row with durations unless needed)
            long tOfflineLookupMs = 0L;
            long tHasPlayedMs = 0L;
            long tUuidMs = 0L;
            long tSaveMs = 0L;
            long tCommitMs = 0L;

            long tLoopStart = System.nanoTime();
            Debugger.write("[0->1][SQL] Starting row iteration over legacy PLAYER table...");

            try {
                while (true) {
                    Debugger.write("[0->1][SQL] rs.next() ... (rowIndex=" + rowIndex + ")");
                    long tNextStart = System.nanoTime();
                    boolean hasNext = rs.next();
                    long tNextMs = millisSince(tNextStart);
                    Debugger.write("[0->1][SQL] rs.next() => " + hasNext + " (took " + tNextMs + "ms)");

                    if (!hasNext) break;

                    rowIndex++;

                    // Read columns
                    Debugger.write("[0->1][SQL] Reading columns from ResultSet (PLAYERNAME/PLAYERTIMESTAMP/ACHIEVEDQUESTS/TOTALACHIEVEDQUESTS)...");
                    long tColsStart = System.nanoTime();
                    final String rawName = rs.getString("PLAYERNAME");
                    final String playerName = (rawName == null) ? "" : rawName.trim();
                    final long timestamp = rs.getLong("PLAYERTIMESTAMP");
                    final int achievedQuests = rs.getInt("ACHIEVEDQUESTS");
                    final int totalAchievedQuests = rs.getInt("TOTALACHIEVEDQUESTS");
                    Debugger.write("[0->1][SQL] Columns read for '" + playerName + "' (took " + millisSince(tColsStart) + "ms)");

                    if (playerName.isEmpty()) {
                        skipped++;
                        Debugger.write("[0->1][SQL] Empty playerName -> skip (skipped=" + skipped + ")");
                        continue;
                    }

                    // Offline player resolution (likely slow point)
                    Debugger.write("[0->1][SQL] Bukkit.getOfflinePlayer('" + playerName + "') START");
                    long tOfflineStart = System.nanoTime();
                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                    long offlineMs = millisSince(tOfflineStart);
                    tOfflineLookupMs += offlineMs;
                    Debugger.write("[0->1][SQL] Bukkit.getOfflinePlayer('" + playerName + "') END (took " + offlineMs + "ms)");

                    // hasPlayedBefore (can also touch disk)
                    Debugger.write("[0->1][SQL] offlinePlayer.hasPlayedBefore() START for '" + playerName + "'");
                    long tHasPlayedStart = System.nanoTime();
                    final boolean hasPlayedBefore = offlinePlayer.hasPlayedBefore();
                    long hasPlayedMs = millisSince(tHasPlayedStart);
                    tHasPlayedMs += hasPlayedMs;
                    Debugger.write("[0->1][SQL] offlinePlayer.hasPlayedBefore() END => " + hasPlayedBefore + " (took " + hasPlayedMs + "ms)");

                    if (!hasPlayedBefore) {
                        skipped++;
                        Debugger.write("[0->1][SQL] UUID not resolvable via hasPlayedBefore=false for '" + playerName + "' -> skip (skipped=" + skipped + ")");
                        continue;
                    }

                    // UUID extraction
                    Debugger.write("[0->1][SQL] offlinePlayer.getUniqueId() START for '" + playerName + "'");
                    long tUuidStart = System.nanoTime();
                    final String playerUuid = offlinePlayer.getUniqueId().toString();
                    long uuidMs = millisSince(tUuidStart);
                    tUuidMs += uuidMs;
                    Debugger.write("[0->1][SQL] offlinePlayer.getUniqueId() END => " + playerUuid + " (took " + uuidMs + "ms)");

                    // Build minimal PlayerQuests (empty quests map)
                    Debugger.write("[0->1][SQL] Building PlayerQuests object for '" + playerName + "'...");
                    final Map<AbstractQuest, Progression> quests = new LinkedHashMap<>();
                    final PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    // Save to new schema
                    Debugger.write("[0->1][SQL] saveProgression(write, '" + playerName + "', uuid=" + playerUuid + ") START");
                    long tSaveStart = System.nanoTime();
                    databaseManager.getSqlManager()
                            .getSaveProgressionSQL()
                            .saveProgression(write, playerName, playerUuid, playerQuests);
                    long saveMs = millisSince(tSaveStart);
                    tSaveMs += saveMs;
                    Debugger.write("[0->1][SQL] saveProgression END for '" + playerName + "' (took " + saveMs + "ms)");

                    converted++;
                    Debugger.write("[0->1][SQL] Converted++ => " + converted + " (skipped=" + skipped + ")");

                    // periodic commit
                    if (converted % 250 == 0) {
                        Debugger.write("[0->1][SQL] COMMIT checkpoint reached (converted=" + converted + ") START");
                        long tCommitStart = System.nanoTime();
                        write.commit();
                        long commitMs = millisSince(tCommitStart);
                        tCommitMs += commitMs;
                        Debugger.write("[0->1][SQL] COMMIT checkpoint END (took " + commitMs + "ms). Totals so far: " +
                                "offline=" + tOfflineLookupMs + "ms, hasPlayed=" + tHasPlayedMs + "ms, uuid=" + tUuidMs + "ms, save=" + tSaveMs + "ms, commit=" + tCommitMs + "ms");
                    }

                    // Extra periodic summary every 50 rows (helps locate stalls without flooding too much)
                    if (converted > 0 && converted % 50 == 0) {
                        Debugger.write("[0->1][SQL] Progress summary: rowIndex=" + rowIndex +
                                " converted=" + converted + " skipped=" + skipped +
                                " | aggregates(ms): offline=" + tOfflineLookupMs +
                                " hasPlayed=" + tHasPlayedMs + " uuid=" + tUuidMs +
                                " save=" + tSaveMs + " commit=" + tCommitMs);
                    }
                }

                Debugger.write("[0->1][SQL] Final COMMIT START (converted=" + converted + ", skipped=" + skipped + ")");
                long tFinalCommitStart = System.nanoTime();
                write.commit();
                long finalCommitMs = millisSince(tFinalCommitStart);
                tCommitMs += finalCommitMs;
                Debugger.write("[0->1][SQL] Final COMMIT END (took " + finalCommitMs + "ms)");

                Debugger.write("[0->1][SQL] SQL data conversion completed successfully. " +
                        "Converted=" + converted + ", skipped=" + skipped +
                        ", loopTime=" + millisSince(tLoopStart) + "ms" +
                        " | aggregates(ms): offline=" + tOfflineLookupMs +
                        " hasPlayed=" + tHasPlayedMs + " uuid=" + tUuidMs +
                        " save=" + tSaveMs + " commit=" + tCommitMs);

            } catch (SQLException ex1) {
                PluginLogger.error("An error has occurred while trying to convert SQL data.");
                PluginLogger.error(ex1.getMessage());
                Debugger.write("[0->1][SQL] Conversion failed after " + millisSince(tLoopStart) + "ms: " + ex1.getMessage());

                Debugger.write("[0->1][SQL] Attempting ROLLBACK on write connection...");
                try {
                    long tRollbackStart = System.nanoTime();
                    write.rollback();
                    Debugger.write("[0->1][SQL] ROLLBACK OK (took " + millisSince(tRollbackStart) + "ms)");
                } catch (SQLException ex2) {
                    PluginLogger.error("An error has occurred while trying to rollback the SQL transaction.");
                    PluginLogger.error(ex2.getMessage());
                    Debugger.write("[0->1][SQL] ROLLBACK FAILED: " + ex2.getMessage());
                }

                throw new MigrationFailedException("SQL data conversion failed.", ex1);
            } finally {
                Debugger.write("[0->1][SQL] Restoring write autocommit=true...");
                try {
                    long tRestoreStart = System.nanoTime();
                    write.setAutoCommit(true);
                    Debugger.write("[0->1][SQL] write.setAutoCommit(true) OK (took " + millisSince(tRestoreStart) + "ms)");
                } catch (SQLException ex3) {
                    PluginLogger.error("Failed to restore autoCommit=true: " + ex3.getMessage());
                    Debugger.write("[0->1][SQL] Failed to restore autoCommit=true: " + ex3.getMessage());
                }
            }

        } catch (SQLException e) {
            PluginLogger.error("An error has occurred while preparing or executing the SQL migration.");
            PluginLogger.error(e.getMessage());
            Debugger.write("[0->1][SQL] Migration setup failed after " + millisSince(tSetupStart) + "ms: " + e.getMessage());
            throw new MigrationFailedException("SQL migration setup failed.", e);
        }

        // 3) Compare counts only if old count is known (best effort; do NOT fail the migration if this count fails)
        if (oldPlayerCount >= 0) {
            Debugger.write("[0->1][SQL] Counting new players (COUNT_PLAYER_NEW) for comparison...");
            long tCountNewStart = System.nanoTime();

            int newPlayerCount = -1;
            try {
                Debugger.write("[0->1][SQL] Opening new-schema connection for COUNT...");
                try (Connection c = databaseManager.getSqlManager().getConnection()) {
                    Debugger.write("[0->1][SQL] New-schema connection acquired for COUNT. Preparing statement...");
                    try (PreparedStatement st = c.prepareStatement(COUNT_PLAYER_NEW)) {
                        Debugger.write("[0->1][SQL] Executing COUNT_PLAYER_NEW query...");
                        try (ResultSet rs = st.executeQuery()) {
                            Debugger.write("[0->1][SQL] COUNT_PLAYER_NEW executed. Reading result...");
                            if (rs.next()) newPlayerCount = rs.getInt(1);
                        }
                    }
                }
                Debugger.write("[0->1][SQL] New players counted: " + newPlayerCount + " (took " + millisSince(tCountNewStart) + "ms)");
            } catch (SQLException e) {
                PluginLogger.error("An error has occurred while trying to count players in the new SQL table.");
                PluginLogger.error(e.getMessage());
                Debugger.write("[0->1][SQL] New player count failed after " + millisSince(tCountNewStart) + "ms: " + e.getMessage());
            }

            if (newPlayerCount >= 0) {
                if (oldPlayerCount == newPlayerCount) {
                    Debugger.write("[0->1][SQL] SQL conversion OK. old=" + oldPlayerCount + " new=" + newPlayerCount);
                    PluginLogger.warn("SQL data conversion completed successfully. " + oldPlayerCount + " players have been converted.");
                } else {
                    Debugger.write("[0->1][SQL] SQL conversion DISCREPANCY. old=" + oldPlayerCount + " new=" + newPlayerCount);
                    PluginLogger.error("SQL data conversion completed with discrepancies: " + oldPlayerCount + " old players, but only " + newPlayerCount + " new players found.");
                }
            }
        } else {
            Debugger.write("[0->1][SQL] Skipping count comparison because oldPlayerCount is unknown (=-1).");
        }

        Debugger.write("[0->1][SQL] ====== End SQL data conversion ======");
    }

    // --------------------------------------------------------------------------------------------
    // YAML part unchanged (you asked to ignore it for now)
    // --------------------------------------------------------------------------------------------

    @Override
    public void applySQLite() {
        // no database update required
    }

    @Override
    public void applyYAML() {
        final FileConfiguration config = progressionFile.getConfig();
        Debugger.write("Starting YAML data conversion...");

        boolean convertedAny = false;

        for (String playerName : config.getKeys(false)) {
            Debugger.write("Trying to convert data for player " + playerName + ".");

            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            if (!offlinePlayer.hasPlayedBefore()) {
                Debugger.write("Impossible to find UUID for player " + playerName + ". Skipping.");
                continue;
            }

            final String playerUuid = offlinePlayer.getUniqueId().toString();
            Debugger.write("Found UUID for player " + playerName + " : " + playerUuid + ".");

            if (config.contains(playerUuid)) {
                Debugger.write("Player " + playerName + " data already exists in the new format. Skipping.");
                continue;
            }

            // copy data in new format
            config.set(playerUuid + ".timestamp", config.getLong(playerName + ".timestamp"));
            config.set(playerUuid + ".achievedQuests", config.getInt(playerName + ".achievedQuests"));
            config.set(playerUuid + ".totalAchievedQuests", config.getInt(playerName + ".totalAchievedQuests"));

            final ConfigurationSection oldQuestsSection = config.getConfigurationSection(playerName + ".quests");
            if (oldQuestsSection != null) {
                final ConfigurationSection newQuestsSection = config.createSection(playerUuid + ".quests");

                for (String key : oldQuestsSection.getKeys(false)) {
                    final ConfigurationSection questSection = oldQuestsSection.getConfigurationSection(key);
                    if (questSection != null) {
                        final ConfigurationSection newQuestSection = newQuestsSection.createSection(key);
                        newQuestSection.set("index", questSection.getInt("index"));
                        newQuestSection.set("progression", questSection.getInt("progression"));
                        newQuestSection.set("requiredAmount", questSection.getInt("requiredAmount"));
                        newQuestSection.set("isAchieved", questSection.getBoolean("isAchieved"));
                    }
                }
            }

            // delete old entry
            config.set(playerName, null);
            Debugger.write("Conversion completed for player " + playerName + " -> " + playerUuid);
            convertedAny = true;
        }

        try {
            if (convertedAny) {
                config.save(progressionFile.getFile());
            }
            Debugger.write("YAML data conversion completed successfully.");
            PluginLogger.warn("YAML data conversion completed successfully.");
        } catch (IOException e) {
            PluginLogger.error("An error occurred while saving the converted YAML data.");
            PluginLogger.error(e.getMessage());
            throw new MigrationFailedException("YAML migration failed while saving.", e);
        }
    }

    // --------------------------------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------------------------------

    private Connection legacyConnection(StorageMode currentMode) throws SQLException {
        Debugger.write("[0->1][SQL] legacyConnection(" + currentMode + ") START");
        long t = System.nanoTime();

        Connection c;
        if (currentMode == StorageMode.SQLITE) {
            Debugger.write("[0->1][SQL] Opening H2 legacy connection: jdbc:h2:./plugins/ODailyQuests/database");
            c = DriverManager.getConnection("jdbc:h2:./plugins/ODailyQuests/database", "odq", "");
        } else {
            Debugger.write("[0->1][SQL] Requesting SQLManager connection for legacyConnection...");
            c = databaseManager.getSqlManager().getConnection();
        }

        Debugger.write("[0->1][SQL] legacyConnection(" + currentMode + ") END (took " + millisSince(t) + "ms) conn=" + safeConnId(c));
        return c;
    }

    private static long millisSince(long nanoStart) {
        return (System.nanoTime() - nanoStart) / 1_000_000L;
    }

    private static String safeConnId(Connection c) {
        if (c == null) return "null";
        try {
            return c.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(c));
        } catch (Exception ignored) {
            return "conn@" + Integer.toHexString(System.identityHashCode(c));
        }
    }
}
