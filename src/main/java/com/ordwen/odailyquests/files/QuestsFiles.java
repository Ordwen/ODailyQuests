package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QuestsFiles {

    private static final Map<String, FileConfiguration> configurations = new HashMap<>();

    private final ODailyQuests oDailyQuests;

    public QuestsFiles(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    public static FileConfiguration getQuestsConfigurationByCategory(String category) {
        final FileConfiguration configuration = configurations.get(category);
        if (configuration == null) {
            PluginLogger.error("Impossible to find the configuration file for category " + category + ".");
            PluginLogger.error("Please check that the file exists and is correctly referenced in the configuration file (quests_per_category section).");
            PluginLogger.error("If the problem persists, please inform the developer.");
            return null;
        }

        return configuration;
    }

    /**
     * Init quests files.
     */
    public void loadQuestsFiles() {
        configurations.clear();

        final File questsFolder = new File(oDailyQuests.getDataFolder(), "quests");

        if (!questsFolder.exists() || questsFolder.listFiles() == null || questsFolder.listFiles().length == 0) {
            questsFolder.mkdirs();
            createDefaultQuestFiles();
        }

        final File[] questFiles = questsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (questFiles == null) {
            PluginLogger.error("An error occurred while loading quests files.");
            PluginLogger.error("Please inform the developer.");
            return;
        }

        for (File file : questFiles) {
            final String category = file.getName().replace(".yml", "");

            final FileConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
                configurations.put(category, config);
                PluginLogger.fine(category + " quests file successfully loaded.");
            } catch (InvalidConfigurationException | IOException e) {
                PluginLogger.error("An error occurred while loading the " + category + " quests file.");
                PluginLogger.error("Please inform the developer.");
                PluginLogger.error(e.getMessage());
            }
        }
    }

    private void createDefaultQuestFiles() {
        final String[] defaultFiles = {"global.yml", "easy.yml", "medium.yml", "hard.yml"};

        for (String fileName : defaultFiles) {
            oDailyQuests.saveResource("quests/" + fileName, false);
            PluginLogger.info(fileName + " created as default.");
        }
    }
}
