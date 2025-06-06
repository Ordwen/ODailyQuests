package com.ordwen.odailyquests.tools.updater.database.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.SQLQuery;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.files.ProgressionFile;
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
                SELECT * FROM `PLAYER`;
            """;

    private static final String COUNT_PLAYER_OLD = """
                SELECT COUNT(*) FROM `PLAYER`
            """;

    private static final String COUNT_PLAYER_NEW = """
                SELECT COUNT(*) FROM `odq_player`
            """;

    public Update0to1(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        if (Database.getMode() == StorageMode.SQLITE || Database.getMode() == StorageMode.MYSQL) {
            applyMySQL();
        } else if (Database.getMode() == StorageMode.YAML) {
            applyYAML();
        } else {
            PluginLogger.info("No database update required for storage mode: " + Database.getMode());
        }

        updateVersion(version);
    }

    @Override
    public void applyMySQL() {
        Debugger.write("Starting SQL data conversion...");
        final StorageMode currentMode = Database.getMode();

        try {
            Class.forName("org.h2.Driver");
            Debugger.write("H2 driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            PluginLogger.error("Failed to load H2 driver.");
            PluginLogger.error(e.getMessage());
        }

        // get the count of players in the old table
        int oldPlayerCount = 0;
        try (final Connection connection = (currentMode == StorageMode.SQLITE) ? DriverManager.getConnection("jdbc:h2:./plugins/ODailyQuests/database", "odq", "") : databaseManager.getSqlManager().getConnection(); final PreparedStatement statement = connection.prepareStatement(COUNT_PLAYER_OLD)) {
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    oldPlayerCount = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            PluginLogger.error("An error has occurred while trying to count players in the old SQL table.");
            PluginLogger.error(e.getMessage());
            Debugger.write("An error has occurred while trying to count players in the old SQL table.");
            Debugger.write(e.getMessage());
        }

        try (final Connection connection = (currentMode == StorageMode.SQLITE) ? DriverManager.getConnection("jdbc:h2:./plugins/ODailyQuests/database", "odq", "") : databaseManager.getSqlManager().getConnection(); final PreparedStatement statement = connection.prepareStatement(SELECT_ALL_FROM_PLAYER_TABLE)) {

            try (final ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    final String playerName = resultSet.getString("PLAYERNAME").trim();
                    Debugger.write("Trying to convert data for player " + playerName + ".");

                    final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                    if (!offlinePlayer.hasPlayedBefore()) {
                        Debugger.write("Impossible to find UUID for player " + playerName + ".");
                        continue;
                    }

                    final String playerUuid = offlinePlayer.getUniqueId().toString();
                    Debugger.write("Found UUID for player " + playerName + " : " + playerUuid + ".");

                    final long timestamp = resultSet.getLong("PLAYERTIMESTAMP");
                    final int achievedQuests = resultSet.getInt("ACHIEVEDQUESTS");
                    final int totalAchievedQuests = resultSet.getInt("TOTALACHIEVEDQUESTS");

                    updateSQLPlayerData(playerName, playerUuid, timestamp, achievedQuests, totalAchievedQuests);
                }
            }

            Debugger.write("SQL data conversion completed successfully.");
        } catch (SQLException e) {
            PluginLogger.error("An error has occurred while trying to convert SQL data.");
            PluginLogger.error(e.getMessage());
            Debugger.write("An error has occurred while trying to convert SQL data.");
            Debugger.write(e.getMessage());
        }

        // get the count of players in the new table
        int newPlayerCount = 0;
        try (final Connection connection = databaseManager.getSqlManager().getConnection(); final PreparedStatement statement = connection.prepareStatement(COUNT_PLAYER_NEW)) {
            try (final ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    newPlayerCount = resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            PluginLogger.error("An error has occurred while trying to count players in the new SQL table.");
            PluginLogger.error(e.getMessage());
            Debugger.write("An error has occurred while trying to count players in the new SQL table.");
            Debugger.write(e.getMessage());
        }

        if (oldPlayerCount == newPlayerCount) {
            Debugger.write("SQL data conversion completed successfully. " + oldPlayerCount + " players have been converted.");
            PluginLogger.warn("SQL data conversion completed successfully. " + oldPlayerCount + " players have been converted.");
        } else {
            Debugger.write("SQL data conversion completed with discrepancies: " + oldPlayerCount + " old players, but only " + newPlayerCount + " new players found.");
            PluginLogger.error("SQL data conversion completed with discrepancies: " + oldPlayerCount + " old players, but only " + newPlayerCount + " new players found.");
        }
    }

    @Override
    public void applySQLite() {
        // no database update required
    }

    @Override
    public void applyYAML() {
        final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
        Debugger.write("Starting YAML data conversion...");

        for (String playerName : progressionFile.getKeys(false)) {
            Debugger.write("Trying to convert data for player " + playerName + ".");

            final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            if (!offlinePlayer.hasPlayedBefore()) {
                Debugger.write("Impossible to find UUID for player " + playerName + ". Skipping.");
                continue;
            }

            final String playerUuid = offlinePlayer.getUniqueId().toString();
            Debugger.write("Found UUID for player " + playerName + " : " + playerUuid + ".");

            if (progressionFile.contains(playerUuid)) {
                Debugger.write("Player " + playerName + " data already exists in the new format. Skipping.");
                continue;
            }

            // copy data in new format
            progressionFile.set(playerUuid + ".timestamp", progressionFile.getLong(playerName + ".timestamp"));
            progressionFile.set(playerUuid + ".achievedQuests", progressionFile.getInt(playerName + ".achievedQuests"));
            progressionFile.set(playerUuid + ".totalAchievedQuests", progressionFile.getInt(playerName + ".totalAchievedQuests"));

            final ConfigurationSection oldQuestsSection = progressionFile.getConfigurationSection(playerName + ".quests");
            if (oldQuestsSection != null) {
                final ConfigurationSection newQuestsSection = progressionFile.createSection(playerUuid + ".quests");

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
            progressionFile.set(playerName, null);
            Debugger.write("Conversion completed for player " + playerName + " -> " + playerUuid);
        }

        try {
            progressionFile.save(ProgressionFile.getProgressionFile());
            Debugger.write("YAML data conversion completed successfully.");
        } catch (IOException e) {
            PluginLogger.error("An error occurred while saving the converted YAML data.");
            PluginLogger.error(e.getMessage());
        }

        Debugger.write("YAML data conversion completed successfully.");
        PluginLogger.warn("YAML data conversion completed successfully.");
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
