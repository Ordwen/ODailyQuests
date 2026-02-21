package com.ordwen.odailyquests.tools.updater.database.updates;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.DatabaseUpdater;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Update 0 -> 1
 * This version only computes counters for migrability based on usercache.json presence.
 * No migration is performed, no Bukkit OfflinePlayer lookup.
 */
public class Update0to1 extends DatabaseUpdater {

    private static final String SELECT_ALL_FROM_PLAYER_TABLE = """
            SELECT PLAYERNAME, PLAYERTIMESTAMP
            FROM `PLAYER`;
            """;

    private static final String COUNT_PLAYER_OLD = """
            SELECT COUNT(*) FROM `PLAYER`;
            """;

    public Update0to1(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        boolean success = false;

        try {
            if (Database.getMode() == StorageMode.SQLITE || Database.getMode() == StorageMode.MYSQL) {
                applyMySQL();
            } else {
                PluginLogger.info("No database update required for storage mode: " + Database.getMode());
            }

            success = true;
        } catch (Exception ex) {
            PluginLogger.error("Database update 0->1 failed; version not updated.");
            PluginLogger.error(ex.getMessage());
        }

        Bukkit.getOfflinePlayers();

        if (success) {
            // updateVersion(version);
        }
    }

    @Override
    public void applyMySQL() {
        Debugger.write("[0->1][SQL] ====== Starting SQL data conversion ======");
        final StorageMode currentMode = Database.getMode();
        Debugger.write("[0->1][SQL] StorageMode=" + currentMode);

        // 1) Load usercache.json (server root)
        final File usercache = resolveUsercacheFile();
        if (!usercache.exists() || !usercache.isFile()) {
            PluginLogger.error("usercache.json not found at: " + usercache.getAbsolutePath());
            throw new IllegalStateException("usercache.json missing");
        }

        final Set<String> cachedNames = loadUsercacheNamesLowercase(usercache);

        // 2) Count old players (optional info)
        int oldPlayerCount = -1;
        try (Connection c = legacyConnection(currentMode);
             PreparedStatement st = c.prepareStatement(COUNT_PLAYER_OLD);
             ResultSet rs = st.executeQuery()
        ) {
            if (rs.next()) oldPlayerCount = rs.getInt(1);
        } catch (SQLException e) {
            PluginLogger.error("Unable to count old players: " + e.getMessage());
        }

        // 3) Read PLAYER table and compute counters
        int totalRows = 0;
        int migrable = 0;
        int notMigrable = 0;

        // Group non-migrable by date (day precision) -> count
        // (we keep it simple: yyyy-MM-dd)
        final Map<String, Integer> notMigrableByDay = new TreeMap<>();
        final ZoneId zone = ZoneId.systemDefault();
        final DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(zone);

        try (Connection read = legacyConnection(currentMode);
             PreparedStatement st = read.prepareStatement(SELECT_ALL_FROM_PLAYER_TABLE);
             ResultSet rs = st.executeQuery()
        ) {
            while (rs.next()) {
                totalRows++;

                final String rawName = rs.getString("PLAYERNAME");
                final String playerName = (rawName == null) ? "" : rawName.trim();
                final long tsMillis = rs.getLong("PLAYERTIMESTAMP"); // epoch millis

                if (playerName.isEmpty()) {
                    // treat as non-migrable (no name -> can't resolve)
                    notMigrable++;
                    final String dayKey = dayFmt.format(Instant.ofEpochMilli(safeTs(tsMillis)));
                    notMigrableByDay.merge(dayKey, 1, Integer::sum);
                    continue;
                }

                final boolean inUsercache = cachedNames.contains(playerName.toLowerCase(Locale.ROOT));
                if (inUsercache) {
                    migrable++;
                } else {
                    notMigrable++;
                    final String dayKey = dayFmt.format(Instant.ofEpochMilli(safeTs(tsMillis)));
                    notMigrableByDay.merge(dayKey, 1, Integer::sum);
                }
            }
        } catch (SQLException e) {
            PluginLogger.error("SQL check failed: " + e.getMessage());
            throw new IllegalStateException("SQL check failed", e);
        }

        // 4) Final report
        Debugger.write("[0->1][SQL] ====== Migration feasibility report ======");
        Debugger.write("[0->1][SQL] usercache entries (names): " + cachedNames.size());
        Debugger.write("[0->1][SQL] oldPlayerCount(COUNT*): " + oldPlayerCount);
        Debugger.write("[0->1][SQL] totalRows(read): " + totalRows);
        Debugger.write("[0->1][SQL] migrable (in usercache): " + migrable);
        Debugger.write("[0->1][SQL] NOT migrable (missing usercache): " + notMigrable);

        if (!notMigrableByDay.isEmpty()) {
            Debugger.write("[0->1][SQL] NOT migrable grouped by day (PLAYERTIMESTAMP):");
            for (Map.Entry<String, Integer> e : notMigrableByDay.entrySet()) {
                Debugger.write("[0->1][SQL]  - " + e.getKey() + " : " + e.getValue());
            }
        } else {
            Debugger.write("[0->1][SQL] No non-migrable players detected.");
        }

        Debugger.write("[0->1][SQL] ====== End SQL data conversion ======");
    }

    @Override
    public void applySQLite() {
        // no-op
    }

    @Override
    public void applyYAML() {
        // not relevant here
    }

    // --------------------------------------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------------------------------------

    private Connection legacyConnection(StorageMode currentMode) throws SQLException {
        // Keep same behavior as before:
        // - SQLITE legacy => H2 file
        // - MYSQL legacy => SQLManager connection
        if (currentMode == StorageMode.SQLITE) {
            return java.sql.DriverManager.getConnection("jdbc:h2:./plugins/ODailyQuests/database", "odq", "");
        }
        return databaseManager.getSqlManager().getConnection();
    }

    private File resolveUsercacheFile() {
        // plugins/ODailyQuests -> plugins -> server root
        File serverRoot = ODailyQuests.INSTANCE.getDataFolder().getParentFile().getParentFile();
        return new File(serverRoot, "usercache.json");
    }

    private static Set<String> loadUsercacheNamesLowercase(File usercache) {
        final Set<String> names = new HashSet<>();
        try (FileReader reader = new FileReader(usercache)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (!root.isJsonArray()) {
                throw new IllegalStateException("usercache.json root is not a JSON array");
            }

            JsonArray arr = root.getAsJsonArray();
            for (JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                JsonObject obj = el.getAsJsonObject();

                JsonElement nameEl = obj.get("name");
                if (nameEl == null || nameEl.isJsonNull()) continue;

                String name = nameEl.getAsString();
                if (name == null) continue;

                name = name.trim();
                if (!name.isEmpty()) {
                    names.add(name.toLowerCase(Locale.ROOT));
                }
            }
        } catch (Exception e) {
            PluginLogger.error("Failed to read/parse usercache.json: " + e.getMessage());
            throw new IllegalStateException("usercache.json parse failed", e);
        }
        return names;
    }

    private static long safeTs(long tsMillis) {
        return tsMillis > 0 ? tsMillis : 0L;
    }
}
