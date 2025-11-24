package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QuestsFiles {

    private final ODailyQuests plugin;
    private final Map<String, FileConfiguration> configurations = new HashMap<>();

    public QuestsFiles(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    /**
     * Returns the quests configuration for a given category.
     *
     * @param category the category name (file name without .yml)
     * @return the configuration, or null if not found
     */
    public FileConfiguration getQuestsConfigurationByCategory(String category) {
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
     * Load all quests files from the quests folder.
     */
    public void load() {
        configurations.clear();

        final File questsFolder = new File(plugin.getDataFolder(), "quests");

        // Ensure the folder exists and has default files if empty
        if (!questsFolder.exists() || questsFolder.listFiles() == null || questsFolder.listFiles().length == 0) {
            if (!questsFolder.exists() && !questsFolder.mkdirs()) {
                PluginLogger.error("Unable to create quests folder: " + questsFolder.getPath());
                return;
            }
            createDefaultQuestFiles();
        }

        final File[] questFiles = questsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (questFiles == null) {
            PluginLogger.error("An error occurred while listing quests files in folder " + questsFolder.getPath() + ".");
            PluginLogger.error("Please inform the developer.");
            return;
        }

        for (File file : questFiles) {
            final String category = file.getName().replace(".yml", "");

            final FileConfiguration config = new YamlConfiguration();
            try {
                config.load(file);
                configurations.put(category, config);
                PluginLogger.fine("Quests file for category '" + category + "' successfully loaded.");
            } catch (InvalidConfigurationException | IOException e) {
                PluginLogger.error("An error occurred while loading the quests file for category '" + category + "'.");
                PluginLogger.error("Please inform the developer.");
                PluginLogger.error(e.getMessage());
            }
        }
    }

    private void createDefaultQuestFiles() {
        final String[] defaultFiles = {"examples.yml", "easy.yml", "medium.yml", "hard.yml"};

        for (String fileName : defaultFiles) {
            plugin.saveResource("quests/" + fileName, false);
            PluginLogger.info(fileName + " created as default.");
        }
    }
}