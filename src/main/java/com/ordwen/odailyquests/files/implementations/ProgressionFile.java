package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ProgressionFile extends APluginFile {

    public ProgressionFile(ODailyQuests plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        file = new File(plugin.getDataFolder(), "progression.yml");

        if (!file.exists()) {
            plugin.saveResource("progression.yml", false);
            PluginLogger.info("Progression file created.");
        }

        config = new YamlConfiguration();

        try {
            config.load(file);
            PluginLogger.fine("Progression file successfully loaded (YAML).");
        } catch (Exception e) {
            PluginLogger.error("An error occurred while loading the progression file.");
            PluginLogger.error(e.getMessage());
        }
    }
}