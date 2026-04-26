package com.ordwen.odailyquests.tools.updater.database.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.SQLQuery;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.DatabaseUpdater;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.*;

public class Update0to1 extends DatabaseUpdater {

    public Update0to1(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        if (Database.getMode() == StorageMode.SQLITE || Database.getMode() == StorageMode.MYSQL) {
            applyMySQL();
        }
    }

    @Override
    public void applyMySQL() {
        Debugger.write("Starting SQL data conversion (LuckPerms + Offline fallback)...");
        final StorageMode currentMode = Database.getMode();

        Map<String, String> luckPermsMap = new HashMap<>();

        try (Connection connection = (currentMode == StorageMode.SQLITE)
                ? DriverManager.getConnection("jdbc:h2:./plugins/ODailyQuests/database", "odq", "")
                : databaseManager.getSqlManager().getConnection()) {

            try (PreparedStatement st = connection.prepareStatement(
                    "SELECT username, uuid FROM luckperms_players");
                 ResultSet rs = st.executeQuery()) {

                while (rs.next()) {
                    String username = rs.getString("username");
                    String uuid = rs.getString("uuid");

                    if (username != null && uuid != null) {
                        luckPermsMap.put(username.toLowerCase(Locale.ROOT), uuid);
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM PLAYER");
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String playerName = resultSet.getString("PLAYERNAME");
                    if (playerName == null || playerName.isBlank()) {
                        continue;
                    }

                    playerName = playerName.trim();
                    Debugger.write("Trying to convert data for player " + playerName);

                    String playerUuid = null;

                    if (luckPermsMap.containsKey(playerName.toLowerCase(Locale.ROOT))) {
                        playerUuid = luckPermsMap.get(playerName.toLowerCase(Locale.ROOT));
                        Debugger.write("UUID found via LuckPerms: " + playerUuid);
                    }
                    else {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                        if (offlinePlayer.hasPlayedBefore()) {
                            offlinePlayer.getUniqueId();
                            playerUuid = offlinePlayer.getUniqueId().toString();
                            Debugger.write("UUID found via OfflinePlayer: " + playerUuid);
                        }
                    }

                    if (playerUuid == null) {
                        Debugger.write("Impossible to find UUID for player " + playerName + ". Skipping.");
                        continue;
                    }

                    long timestamp = resultSet.getLong("PLAYERTIMESTAMP");
                    int achievedQuests = resultSet.getInt("ACHIEVEDQUESTS");
                    int totalAchievedQuests = resultSet.getInt("TOTALACHIEVEDQUESTS");

                    updateSQLPlayerData(playerName, playerUuid, timestamp, achievedQuests, totalAchievedQuests);
                }
            }

            Debugger.write("SQL data conversion completed successfully.");

        } catch (SQLException e) {
            PluginLogger.error("An error occurred during SQL data conversion.");
            PluginLogger.error(e.getMessage());
            Debugger.write(e.getMessage());
        }
    }

    @Override
    public void applySQLite() {
        // no-op
    }

    @Override
    public void applyYAML() {
        // no-op
    }

    private void updateSQLPlayerData(String playerName, String playerUuid, long timestamp, int achievedQuests, int totalAchievedQuests) {
        try (final Connection connection = databaseManager.getSqlManager().getConnection()) {
            final String checkQuery = SQLQuery.LOAD_PLAYER.getQuery();
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, playerUuid);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Debugger.write("Player " + playerUuid + " data already exists in the new schema. Skipping.");
                        return;
                    }
                }
            }

            final Map<AbstractQuest, Progression> quests = new LinkedHashMap<>();
            final PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
            playerQuests.setAchievedQuests(achievedQuests);
            playerQuests.setTotalAchievedQuests(totalAchievedQuests);

            databaseManager.getSqlManager().getSaveProgressionSQL().saveProgression(playerName, playerUuid, playerQuests, true);
            Debugger.write("Player " + playerName + " data saved in the new schema.");
        } catch (SQLException e) {
            Debugger.write("An error occurred while saving player " + playerUuid + " data.");
            Debugger.write(e.getMessage());
        }
    }
}