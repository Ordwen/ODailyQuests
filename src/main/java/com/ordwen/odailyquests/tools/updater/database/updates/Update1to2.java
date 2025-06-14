package com.ordwen.odailyquests.tools.updater.database.updates;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.DatabaseUpdater;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class
Update1to2 extends DatabaseUpdater {

    public Update1to2(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void apply(ODailyQuests plugin, String version) {
        if (Database.getMode() == StorageMode.YAML) {
            applyYAML();
        } else {
            PluginLogger.warn("No database update required for storage mode: " + Database.getMode());
        }

        updateVersion(version);
    }

    @Override
    public void applyMySQL() {
        // no database update required
    }

    @Override
    public void applySQLite() {
        // no database update required
    }

    @Override
    public void applyYAML() {
        final FileConfiguration config = progressionFile.getConfig();
        for (String playerUuid : config.getKeys(false)) {
            final ConfigurationSection playerSection = config.getConfigurationSection(playerUuid);
            if (playerSection == null) continue;

            final ConfigurationSection questsSection = playerSection.getConfigurationSection("quests");
            if (questsSection == null) continue;

            for (String questId : questsSection.getKeys(false)) {
                ConfigurationSection questSection = questsSection.getConfigurationSection(questId);
                if (questSection == null) continue;

                if (!questSection.contains("requiredAmount")) {
                    questSection.set("requiredAmount", 0);
                }

                if (!questSection.contains("selectedRequired")) {
                    questSection.set("selectedRequired", -1);
                }
            }
        }

        try {
            config.save(progressionFile.getFile());
        } catch (Exception e) {
            PluginLogger.error("Failed to apply database update 1 to 2 for YAML: " + e.getMessage());
            return;
        }

        PluginLogger.info("YAML database update 1 to 2 applied successfully.");
    }
}
