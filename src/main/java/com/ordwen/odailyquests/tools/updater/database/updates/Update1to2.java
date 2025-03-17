package com.ordwen.odailyquests.tools.updater.database.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.DatabaseUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Update1to2 extends DatabaseUpdater {

    public Update1to2(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        switch (Database.getMode()) {
            case MYSQL -> applyMySQL();
            case SQLITE -> applySQLite();
            case YAML -> applyYAML();
            default ->
                    PluginLogger.error("Impossible to apply database update: the selected storage mode is incorrect!");
        }

        updateVersion(version);
    }

    @Override
    public void applyMySQL() {
        try (final Connection connection = databaseManager.getSqlManager().getConnection();
             final Statement statement = connection.createStatement()) {

            final String alterTableQuery = "ALTER TABLE `odq_progression` ADD COLUMN `required_amount` INT NOT NULL DEFAULT 0;";
            statement.executeUpdate(alterTableQuery);
            PluginLogger.info("Database update 1 to 2 applied successfully for MySQL.");

        } catch (SQLException e) {
            PluginLogger.error("Failed to apply database update 1 to 2 for MySQL: " + e.getMessage());
        }
    }

    @Override
    public void applySQLite() {
        try (final Connection connection = databaseManager.getSqlManager().getConnection();
             final Statement statement = connection.createStatement()) {

            final String alterTableQuery = "ALTER TABLE odq_progression ADD COLUMN required_amount INTEGER NOT NULL DEFAULT 0;";
            statement.executeUpdate(alterTableQuery);
            PluginLogger.info("Database update 1 to 2 applied successfully for SQLite.");

        } catch (SQLException e) {
            PluginLogger.error("Failed to apply database update 1 to 2 for SQLite: " + e.getMessage());
        }
    }

    @Override
    public void applyYAML() {
        final FileConfiguration yamlConfig = ProgressionFile.getProgressionFileConfiguration();
        for (String playerUuid : yamlConfig.getKeys(false)) {
            final ConfigurationSection playerSection = yamlConfig.getConfigurationSection(playerUuid);
            if (playerSection == null) continue;

            final ConfigurationSection questsSection = playerSection.getConfigurationSection("quests");
            if (questsSection == null) continue;

            for (String questId : questsSection.getKeys(false)) {
                ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
                if (questSection == null) continue;

                if (!questSection.contains("requiredAmount")) {
                    questSection.set("requiredAmount", 0);
                }
            }
        }

        try {
            yamlConfig.save(ProgressionFile.getProgressionFile());
        } catch (Exception e) {
            PluginLogger.error("Failed to apply database update 1 to 2 for YAML: " + e.getMessage());
            return;
        }

        PluginLogger.info("YAML database update 1 to 2 applied successfully.");
    }
}
