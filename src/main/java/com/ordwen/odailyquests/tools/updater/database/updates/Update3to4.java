package com.ordwen.odailyquests.tools.updater.database.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.files.implementations.ProgressionFile;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.DatabaseUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Update3to4 extends DatabaseUpdater {

    private static final String MYSQL_ADD_REWARD_AMOUNT_COLUMN = """
            ALTER TABLE `odq_progression`
            ADD COLUMN `reward_amount` DOUBLE NULL AFTER `required_amount`;
            """;

    private static final String SQLITE_ADD_REWARD_AMOUNT_COLUMN = """
            ALTER TABLE `odq_progression`
            ADD COLUMN `reward_amount` REAL;
            """;

    public Update3to4(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        final StorageMode mode = Database.getMode();

        switch (mode) {
            case MYSQL -> applyMySQL();
            case SQLITE -> applySQLite();
            case YAML -> applyYAML();
        }

        updateVersion(version);
    }

    @Override
    public void applyMySQL() {
        PluginLogger.info("Applying MySQL database update 3 -> 4 (add reward_amount).");
        alterProgressionTable(MYSQL_ADD_REWARD_AMOUNT_COLUMN);
    }

    @Override
    public void applySQLite() {
        PluginLogger.info("Applying SQLite database update 3 -> 4 (add reward_amount).");
        alterProgressionTable(SQLITE_ADD_REWARD_AMOUNT_COLUMN);
    }

    @Override
    public void applyYAML() {
        PluginLogger.info("Applying YAML database update 3 -> 4 (rewardAmount default value).");

        final ProgressionFile progression = this.progressionFile;
        final FileConfiguration progressionConfig = progression.getConfig();

        for (String playerKey : progressionConfig.getKeys(false)) {
            final ConfigurationSection questsSection = progressionConfig.getConfigurationSection(playerKey + ".quests");
            if (questsSection == null) {
                continue;
            }

            for (String questKey : questsSection.getKeys(false)) {
                final String rewardPath = questKey + ".rewardAmount";
                if (questsSection.isSet(rewardPath)) {
                    continue;
                }

                final int questIndex = questsSection.getInt(questKey + ".index");
                final String categoryName = questsSection.getString(questKey + ".category");
                final AbstractQuest quest = QuestLoaderUtils.findQuest(playerKey, categoryName, questIndex, Integer.parseInt(questKey));
                if (quest == null) {
                    Debugger.write("Quest " + questKey + " not found while updating rewardAmount for " + playerKey + ".");
                    continue;
                }

                final double rewardAmount = quest.getReward().resolveRewardAmount();
                questsSection.set(rewardPath, rewardAmount);
            }
        }

        try {
            progressionConfig.save(progression.getFile());
            PluginLogger.info("YAML database update 3 -> 4 completed!");
        } catch (IOException e) {
            PluginLogger.error("An error occurred while saving YAML data during database update 3 -> 4:");
            PluginLogger.error(e.getMessage());
        }
    }

    private void alterProgressionTable(String query) {
        if (databaseManager.getSqlManager() == null) {
            PluginLogger.warn("SQL manager not initialized. Skipping database migration 3 -> 4.");
            return;
        }

        try (Connection connection = databaseManager.getSqlManager().getConnection()) {
            if (connection == null) {
                PluginLogger.error("Unable to obtain a database connection for migration 3 -> 4.");
                return;
            }

            try (Statement statement = connection.createStatement()) {
                statement.execute(query);
                PluginLogger.info("Database migration 3 -> 4 (odq_progression) applied successfully.");
            }
        } catch (SQLException exception) {
            final String message = exception.getMessage();
            if (message != null && message.toLowerCase().contains("duplicate column")) {
                PluginLogger.info("Column 'reward_amount' already exists in odq_progression. Skipping alteration.");
                return;
            }

            PluginLogger.error("Failed to apply database migration 3 -> 4 on odq_progression: " + exception.getMessage());
        }
    }
}
